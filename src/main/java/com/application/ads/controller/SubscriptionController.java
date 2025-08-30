package com.application.ads.controller;

import com.application.ads.model.Subscriber;
import com.application.ads.repository.SubscriberRepository;
import com.application.ads.service.SubscriptionService; // Importe o serviço
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class SubscriptionController {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SubscriptionService subscriptionService; // Injete o serviço

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (subscriberRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Collections.singletonMap("error", "Este e-mail já está inscrito."));
        }
        Subscriber newSubscriber = new Subscriber();
        newSubscriber.setEmail(email);
        subscriptionService.subscribe(newSubscriber); // Use o serviço
        return ResponseEntity.ok(Collections.singletonMap("message", "Inscrição realizada com sucesso!"));
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (subscriberRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "E-mail não encontrado na lista de inscritos."));
        }
        subscriptionService.unsubscribe(email); // Use o serviço
        return ResponseEntity.ok(Collections.singletonMap("message", "Inscrição cancelada com sucesso."));
    }
}