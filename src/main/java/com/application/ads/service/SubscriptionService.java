package com.application.ads.service;

import com.application.ads.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe a anotação
import com.application.ads.model.Subscriber;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    // O método deleteByEmail deve ser transacional para funcionar
    @Transactional
    public void unsubscribe(String email) {
        subscriberRepository.deleteByEmail(email);
    }

    // Crie também um método para a inscrição
    @Transactional
    public void subscribe(Subscriber subscriber) {
        subscriberRepository.save(subscriber);
    }
}