package ma.barid.avis_sort_DGI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;


    @Async
    public void envoyerIdentifiants(String toEmail, String username, String password, String role) {
        System.out.println("Début envoi email");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Vos identifiants - Avis de Sort DGI");
        message.setText(
                "Bonjour,\n\n" +
                        "Un compte a ete cree pour vous sur la plateforme Avis de Sort DGI.\n\n" +
                        "Nom d'utilisateur : " + username + "\n" +
                        "Mot de passe temporaire : " + password + "\n" +
                        "Role : " + role + "\n\n" +
                        "Merci de vous connecter et de changer votre mot de passe des que possible.\n\n" +
                        "Cordialement,\n" +
                        "Barid Al Maghrib - Direction Generale des Impots"
        );
        mailSender.send(message);
    }
}