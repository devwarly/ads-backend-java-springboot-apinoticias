package com.application.ads.controller;

import com.application.ads.model.Noticia;
import com.application.ads.model.Periodo;
import com.application.ads.repository.NoticiaRepository;
import com.application.ads.repository.PeriodoRepository;
import com.application.ads.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class NoticiaController {

    private final NoticiaRepository noticiaRepository;
    private final PeriodoRepository periodoRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    public NoticiaController(NoticiaRepository noticiaRepository, PeriodoRepository periodoRepository) {
        this.noticiaRepository = noticiaRepository;
        this.periodoRepository = periodoRepository;
    }

    @GetMapping("/noticias")
    public List<Noticia> getAllNoticias() {
        return noticiaRepository.findAllByOrderByDataDesc();
    }


    @GetMapping("/noticias/{id}")
    public ResponseEntity<Noticia> getNoticiaById(@PathVariable Long id) {
        return noticiaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/noticias")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN', 'ADMIN')")
    public Noticia createNoticia(@RequestBody Noticia noticia) {
        Noticia savedNoticia = noticiaRepository.save(noticia);

        // Envia a notificação para todos os inscritos
        String subject = "Nova Notícia no Site ADS: " + noticia.getTitulo();
        String content = "Olá!\n\nUma nova notícia foi publicada no site ADS:\n\n" +
                noticia.getTitulo() + "\n\n" +
                "Leia mais em: http://127.0.0.1:5500/templates/noticia.html?id=" + savedNoticia.getId();

        notificationService.sendNotificationToSubscribers(subject, content);

        return savedNoticia;
    }

    @PutMapping("/noticias/{id}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN', 'ADMIN')")
    public ResponseEntity<Noticia> updateNoticia(@PathVariable Long id, @RequestBody Noticia noticiaDetails) {
        Optional<Noticia> noticiaOptional = noticiaRepository.findById(id);

        if (noticiaOptional.isPresent()) {
            Noticia noticia = noticiaOptional.get();
            // A anotação @PreAuthorize já verifica a permissão, então essa verificação interna pode ser mais simples
            noticia.setTitulo(noticiaDetails.getTitulo());
            noticia.setTexto(noticiaDetails.getTexto());
            noticia.setImagem(noticiaDetails.getImagem());
            Noticia updatedNoticia = noticiaRepository.save(noticia);
            return ResponseEntity.ok(updatedNoticia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/noticias/{id}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> deleteNoticia(@PathVariable Long id) {
        if (noticiaRepository.existsById(id)) {
            noticiaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImagem(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "uploads";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            String imageUrl = "http://localhost:8080/" + uploadDir + "/" + uniqueFileName;
            return ResponseEntity.ok(Collections.singletonMap("url", imageUrl));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Erro no upload da imagem."));
        }
    }

    @GetMapping("/curriculo")
    public List<Periodo> getAllPeriodos() {
        return periodoRepository.findAll();
    }

    @PostMapping("/curriculo")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public Periodo createPeriodo(@RequestBody Periodo periodo) {
        Periodo savedPeriodo = periodoRepository.save(periodo);

        // Envia notificação para todos os inscritos
        String subject = "Nova Atualização na Grade Curricular do ADS";
        String content = "Olá!\n\nUm novo período foi adicionado à grade curricular do ADS:\n\n" +
                periodo.getNome() + "\n\n" +
                "Verifique a nova grade em: http://127.0.0.1:5500/templates/grade_curricular.html";

        notificationService.sendNotificationToSubscribers(subject, content);

        return savedPeriodo;
    }

    @PutMapping("/curriculo/{id}")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<Periodo> updatePeriodo(@PathVariable Long id, @RequestBody Periodo periodoDetails) {
        return periodoRepository.findById(id)
                .map(periodo -> {
                    periodo.setNome(periodoDetails.getNome());
                    periodo.setDisciplinas(periodoDetails.getDisciplinas());
                    Periodo updatedPeriodo = periodoRepository.save(periodo);
                    return ResponseEntity.ok(updatedPeriodo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/curriculo/{id}")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<?> deletePeriodo(@PathVariable Long id) {
        if (periodoRepository.existsById(id)) {
            periodoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}