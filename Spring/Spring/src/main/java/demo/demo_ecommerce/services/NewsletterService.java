package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.NewsletterSubscriber;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.NewsletterSubscriberRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final RecommendationService recommendationService;


    private final NewsletterSubscriberRepository newsletterRepository;
    private final JavaMailSender mailSender;

    public List<NewsletterSubscriber> getAllSubscribers() {
        return newsletterRepository.findAll();
    }

    @Transactional
    public NewsletterSubscriber subscribe(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Indirizzo email non valido.");
        }

        Optional<NewsletterSubscriber> existingOpt = newsletterRepository.findByEmail(email.trim().toLowerCase());

        if (existingOpt.isPresent()) {
            NewsletterSubscriber existing = existingOpt.get();

            if (existing.isConfirmed()) {
                throw new IllegalStateException("L'indirizzo email è già iscritto e confermato.");
            }

            existing.setConfirmationToken(UUID.randomUUID().toString());
            existing.setSubscriptionDate(LocalDateTime.now());

            NewsletterSubscriber saved = newsletterRepository.save(existing);
            sendConfirmationEmail(saved);
            return saved;
        }

        NewsletterSubscriber newSubscriber = NewsletterSubscriber.builder()
                .email(email.trim().toLowerCase())
                .confirmed(false)
                .confirmationToken(UUID.randomUUID().toString())
                .subscriptionDate(LocalDateTime.now())
                .build();

        NewsletterSubscriber saved = newsletterRepository.save(newSubscriber);
        sendConfirmationEmail(saved);
        return saved;
    }

    @Transactional
    public boolean confirmSubscription(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Optional<NewsletterSubscriber> subscriberOpt = newsletterRepository.findByConfirmationToken(token.trim());

        if (subscriberOpt.isEmpty()) {
            return false;
        }

        NewsletterSubscriber subscriber = subscriberOpt.get();
        subscriber.setConfirmed(true);
        subscriber.setConfirmationToken(null);
        newsletterRepository.save(subscriber);

        return true;
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public void sendConfirmationEmail(NewsletterSubscriber subscriber) {
        String confirmationUrl = "http://localhost:4200/newsletter/confirm?token=" + subscriber.getConfirmationToken();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(subscriber.getEmail());
        message.setSubject("Conferma la tua iscrizione alla newsletter");
        message.setText("Clicca sul seguente link per confermare l'iscrizione:\n" + confirmationUrl);

        try {
            mailSender.send(message);
            System.out.println("✅ Email inviata a " + subscriber.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Errore durante l'invio email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 10 * * MON") // Ogni lunedì alle 10:00
    public void sendWeeklyNewsletter() {
        List<NewsletterSubscriber> subscribers = newsletterRepository.findAll()
                .stream()
                .filter(NewsletterSubscriber::isConfirmed)
                .toList();

        for (NewsletterSubscriber subscriber : subscribers) {
            try {
                sendNewsletterEmail(subscriber);
            } catch (Exception e) {
                System.err.println("❌ Errore invio a " + subscriber.getEmail() + ": " + e.getMessage());
            }
        }

        System.out.println("✅ Newsletter inviata a " + subscribers.size() + " utenti confermati");
    }

    public void sendNewsletterEmail(NewsletterSubscriber subscriber) {
        try {
            List<Product> products = recommendationService.getRecommendationsForEmail(subscriber.getEmail());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(subscriber.getEmail());
            helper.setSubject("🛍️ Novità e consigli settimanali da Unilire");

            // Costruisci HTML dei prodotti
            StringBuilder productsHtml = new StringBuilder();
            for (Product product : products) {
                productsHtml.append(
                        "<div style='border:1px solid #ddd;padding:10px;margin:10px 0;border-radius:8px;'>"
                                + "<img src='" + product.getImageUrl() + "' alt='" + product.getName() + "' width='100' style='float:left;margin-right:10px;'>"
                                + "<div style='overflow:hidden;'>"
                                + "<h3 style='margin:0;color:#2c3e50;'>" + product.getName() + "</h3>"
                                + "<p style='margin:5px 0;font-weight:bold;'>💰 " + product.getPrice() + " L</p>"
                                + "<a href='http://localhost:4200/product/" + product.getId() + "'"
                                + " style='color:#4CAF50;text-decoration:underline;'>"
                                + "Vai al prodotto</a></div><div style='clear:both;'></div></div>"
                );
            }

            String htmlContent = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#2c3e50;'>Ciao!</h2>"
                    + "<p>Ecco alcuni prodotti che ti consigliamo questa settimana:</p>"
                    + productsHtml
                    + "<br/><p><a href='http://localhost:4200' style='padding:10px 20px;"
                    + "background-color:#4CAF50;color:white;text-decoration:none;border-radius:5px;'>"
                    + "Visita lo shop</a></p>"
                    + "<p style='font-size:0.8em;color:#888;'>Hai ricevuto questa email perché sei iscritto alla newsletter Unilire.</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Email HTML inviata a " + subscriber.getEmail());

        } catch (Exception e) {
            System.err.println("❌ Errore invio HTML a " + subscriber.getEmail() + ": " + e.getMessage());
        }
    }

}
