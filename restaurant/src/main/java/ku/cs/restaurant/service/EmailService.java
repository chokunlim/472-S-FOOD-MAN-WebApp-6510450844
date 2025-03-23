package ku.cs.restaurant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void newPromotion(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("New Promotion Available!");
        message.setText("We have an exciting new promotion for you! Check it out in our restaurant app. 🎉");

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }

}