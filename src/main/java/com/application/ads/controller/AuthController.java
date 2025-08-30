package com.application.ads.controller;

import com.application.ads.model.Admin;
import com.application.ads.model.AccessKey;
import com.application.ads.model.AdminRequest;
import com.application.ads.repository.AdminRepository;
import com.application.ads.repository.AccessKeyRepository;
import com.application.ads.repository.AdminRequestRepository;
import com.application.ads.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AccessKeyRepository accessKeyRepository;

    @Autowired
    private AdminRequestRepository adminRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JavaMailSender mailSender;

    private static final String ADMIN_PANEL_LINK = "http://127.0.0.1:5500/templates/admin-requests.html";

    /**
     * Endpoint de login. Autentica o usuário e retorna um JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("username");
        String password = credentials.get("password");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                String token = jwtUtil.generateToken(userDetails, admin.getRole());

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("success", true);
                responseBody.put("token", token);
                responseBody.put("adminName", admin.getNome());
                responseBody.put("adminEmail", admin.getEmail());
                responseBody.put("adminRole", admin.getRole());

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(401).body(Collections.singletonMap("error", "Credenciais inválidas."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Credenciais inválidas."));
        }
    }

    /**
     * Endpoint para solicitar acesso de admin.
     */
    @PostMapping("/request-access")
    public ResponseEntity<Map<String, String>> requestAccess(@RequestBody Map<String, String> request) {
        String solicitanteEmail = request.get("email");

        if (adminRepository.findByEmail(solicitanteEmail).isPresent()) {
            return ResponseEntity.status(409).body(Collections.singletonMap("error", "Este e-mail já está cadastrado."));
        }
        if (adminRequestRepository.findByEmail(solicitanteEmail).isPresent()) {
            return ResponseEntity.status(409).body(Collections.singletonMap("error", "Uma solicitação já está pendente para este e-mail."));
        }

        AdminRequest newRequest = new AdminRequest();
        newRequest.setEmail(solicitanteEmail);
        adminRequestRepository.save(newRequest);

        sendNotificationToMasterAdmin(solicitanteEmail);

        return ResponseEntity.ok(Collections.singletonMap("message", "Sua solicitação foi enviada para aprovação do administrador."));
    }

    /**
     * Endpoint para listar todas as solicitações de admin pendentes.
     * Apenas o admin mestre pode acessar.
     */
    @GetMapping("/requests")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllRequests() {
        List<AdminRequest> requests = adminRequestRepository.findAll();
        List<Map<String, Object>> response = requests.stream()
                .map(req -> {
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("id", req.getId());
                    requestMap.put("email", req.getEmail());
                    requestMap.put("requestDate", req.getRequestDate().toString());
                    return requestMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * NOVO Endpoint para listar todos os admins cadastrados.
     * Apenas o admin mestre pode acessar.
     */
    @GetMapping("/admins")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<List<Map<String, String>>> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        List<Map<String, String>> response = admins.stream()
                .map(admin -> {
                    Map<String, String> adminMap = new HashMap<>();
                    adminMap.put("id", String.valueOf(admin.getId()));
                    adminMap.put("nome", admin.getNome());
                    adminMap.put("email", admin.getEmail());
                    adminMap.put("role", admin.getRole());
                    return adminMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para aprovar uma solicitação de admin.
     * Apenas o admin mestre pode acessar.
     */
    @PostMapping("/requests/approve/{id}")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<Map<String, String>> approveRequest(@PathVariable Long id) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest adminRequest = requestOpt.get();
            String uniqueKey = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

            AccessKey newKey = new AccessKey();
            newKey.setKey(uniqueKey);
            newKey.setEmail(adminRequest.getEmail());
            newKey.setExpiryDate(LocalDateTime.now().plusHours(1));
            accessKeyRepository.save(newKey);

            sendKeyToUser(adminRequest.getEmail(), uniqueKey);
            adminRequestRepository.delete(adminRequest);

            return ResponseEntity.ok(Collections.singletonMap("message", "Solicitação aprovada. Chave de acesso enviada por e-mail."));
        }
        return ResponseEntity.status(404).body(Collections.singletonMap("error", "Solicitação não encontrada."));
    }

    /**
     * Endpoint para rejeitar uma solicitação de admin.
     * Apenas o admin mestre pode acessar.
     */
    @DeleteMapping("/requests/reject/{id}")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<Map<String, String>> rejectRequest(@PathVariable Long id) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            adminRequestRepository.delete(requestOpt.get());
            return ResponseEntity.ok(Collections.singletonMap("message", "Solicitação rejeitada com sucesso."));
        }
        return ResponseEntity.status(404).body(Collections.singletonMap("error", "Solicitação não encontrada."));
    }

    /**
     * Endpoint para o cadastro final do admin, usando a chave de acesso.
     */
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> registrationData) {
        String email = registrationData.get("email");
        String key = registrationData.get("key");
        String nome = registrationData.get("nome");
        String sobrenome = registrationData.get("sobrenome");
        String password = registrationData.get("password");

        Optional<AccessKey> accessKeyOpt = accessKeyRepository.findByEmailAndKey(email, key);
        if (accessKeyOpt.isPresent() && accessKeyOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            Admin newAdmin = new Admin();
            newAdmin.setEmail(email);
            newAdmin.setNome(nome);
            newAdmin.setSobrenome(sobrenome);
            newAdmin.setPasswordHash(passwordEncoder.encode(password));
            newAdmin.setRole("ADMIN");
            adminRepository.save(newAdmin);
            accessKeyRepository.delete(accessKeyOpt.get());

            return ResponseEntity.ok(Collections.singletonMap("success", true));
        }

        return ResponseEntity.status(400).body(Collections.singletonMap("error", "Chave de acesso inválida ou expirada."));
    }

    /**
     * Método auxiliar para enviar notificação ao admin mestre.
     */
    private void sendNotificationToMasterAdmin(String solicitanteEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        String masterAdminEmail = "thesuport.ads@gmail.com";
        message.setTo(masterAdminEmail);
        message.setSubject("Nova Solicitação de Acesso Admin - ADS");
        message.setText("Uma nova solicitação de acesso de admin foi recebida de: " + solicitanteEmail + "\n\nPor favor, acesse o painel de gerenciamento para aprovar ou rejeitar a solicitação:\n" + ADMIN_PANEL_LINK);
        mailSender.send(message);
    }

    /**
     * Método auxiliar para enviar a chave de acesso ao usuário solicitante.
     */
    private void sendKeyToUser(String userEmail, String key) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Sua Chave de Acesso Admin - ADS");
        message.setText("Olá!\n\nSua solicitação de acesso de admin foi aprovada. Use a chave abaixo para completar seu cadastro:\n\n" + key + "\n\nEsta chave é válida por 1 hora e só pode ser usada uma vez.");
        mailSender.send(message);
    }

    // Métodos para mudança de senha e exclusão de conta
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String currentPassword = credentials.get("currentPassword");
        String newPassword = credentials.get("newPassword");

        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            if (passwordEncoder.matches(currentPassword, admin.getPasswordHash())) {
                admin.setPasswordHash(passwordEncoder.encode(newPassword));
                adminRepository.save(admin);
                return ResponseEntity.ok(Collections.singletonMap("message", "Senha alterada com sucesso."));
            } else {
                return ResponseEntity.status(401).body(Collections.singletonMap("error", "Senha atual incorreta."));
            }
        }
        return ResponseEntity.status(404).body(Collections.singletonMap("error", "Admin não encontrado."));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            if (passwordEncoder.matches(password, admin.getPasswordHash())) {
                adminRepository.delete(admin);
                return ResponseEntity.ok(Collections.singletonMap("message", "Conta excluída com sucesso."));
            } else {
                return ResponseEntity.status(401).body(Collections.singletonMap("error", "Senha incorreta."));
            }
        }
        return ResponseEntity.status(404).body(Collections.singletonMap("error", "Admin não encontrado."));
    }
}