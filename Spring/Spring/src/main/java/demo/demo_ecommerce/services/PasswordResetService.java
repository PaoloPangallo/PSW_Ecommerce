package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.ResourceNotFoundException;
import demo.demo_ecommerce.entities.PasswordResetToken;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.PasswordResetTokenRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UsersRepository usersRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UsersRepository usersRepository,
                                JavaMailSender mailSender) {
        this.tokenRepository = tokenRepository;
        this.usersRepository = usersRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void createPasswordResetToken(String email) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + email));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = tokenRepository.findByUserId(user.getId())
                .map(existingToken -> {
                    existingToken.setToken(token);
                    existingToken.setExpiryDate(expiry);
                    existingToken.setUsed(false);
                    return existingToken;
                })
                .orElseGet(() -> PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(expiry)
                        .used(false)
                        .build());

        tokenRepository.save(resetToken);
        sendPasswordResetEmail(user.getEmail(), user.getUsername(), token);
    }






    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token non valido"));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Il token è scaduto");
        }

        User user = resetToken.getUser();
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        usersRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    private void sendPasswordResetEmail(String toEmail, String username, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🔐 Reimposta la tua password - Unilire");

            String resetLink = "http://localhost:4200/reset-password?token=" + token;

            String htmlContent = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#2c3e50;'>Ciao " + username + ",</h2>"
                    + "<p>Hai richiesto di reimpostare la tua password per il tuo account su <strong>Unilire</strong>.</p>"
                    + "<p>Clicca sul pulsante qui sotto per creare una nuova password:</p>"
                    + "<p><a href='" + resetLink + "' style='padding:12px 24px;display:inline-block;"
                    + "background-color:#007BFF;color:white;text-decoration:none;border-radius:6px;'>"
                    + "Reimposta Password</a></p>"
                    + "<p>Il link scadrà tra 30 minuti. Se non sei stato tu a fare questa richiesta, puoi ignorare questo messaggio.</p>"
                    + "<br/><p style='font-size:0.8em;color:#888;'>Grazie per aver scelto Unilire.</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("📨 Email reset inviata a " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Errore invio mail reset password a " + toEmail + ": " + e.getMessage());
        }
    }
}
