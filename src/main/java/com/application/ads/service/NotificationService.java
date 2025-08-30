package com.application.ads.service;

import com.application.ads.model.Subscriber;
import com.application.ads.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SubscriberRepository subscriberRepository;

    public void sendNotificationToSubscribers(String subject, String content) {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        for (Subscriber subscriber : subscribers) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(subscriber.getEmail());
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        }
    }
}