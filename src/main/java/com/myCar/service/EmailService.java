package com.myCar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.myCar.dto.ContactRequest;
import com.myCar.dto.ExpertReportDTO;
import com.myCar.domain.ExpertReport;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender emailSender;

    public void sendContactEmail(ContactRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("amenallah1991@gmail.com"); // L'email de l'application (doit correspondre à spring.mail.username)
        message.setTo(request.getEmail()); // <-- L'email saisi dans le formulaire (l'utilisateur reçoit le mail)
        message.setSubject("Merci pour votre message !");
        
        String emailContent = String.format(
            "Bonjour %s,\n\nMerci de nous avoir contactés. Nous avons bien reçu votre message :\n\n%s\n\nNous vous répondrons dans les plus brefs délais.\n\nCordialement,\nL'équipe MyCar",
            request.getName(),
            request.getMessage()
        );
        
        message.setText(emailContent);
        emailSender.send(message);
    }

    public void sendExpertiseReportNotification(String toEmail, String name, ExpertReport report) {
        System.out.println("Tentative d'envoi d'email à : " + toEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("amenallah1991@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Votre rapport d'expertise est disponible");

        String content = String.format(
            "Bonjour %s,\n\nVotre rapport d'expertise pour le véhicule %s %s (%d) est maintenant disponible.\n\nTitre du rapport : %s\nDate d'expertise : %s\n\nMerci de votre confiance.\n\nL'équipe MyCar",
            name,
            report.getExpertiseRequest().getCar().getMake(),
            report.getExpertiseRequest().getCar().getModel(),
            report.getExpertiseRequest().getCar().getYear(),
            report.getTitle(),
            report.getExpertiseDate().toString()
        );

        message.setText(content);
        try {
            emailSender.send(message);
            System.out.println("Email envoyé à : " + toEmail);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }
}