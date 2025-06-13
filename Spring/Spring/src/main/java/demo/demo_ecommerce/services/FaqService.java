package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Faq;
import demo.demo_ecommerce.repositories.FaqRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqService {

    private static final Logger logger = LoggerFactory.getLogger(FaqService.class);

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public Faq saveFaq(Faq faq) {
        if (faq.getQuestion() == null || faq.getQuestion().trim().isEmpty() ||
                faq.getAnswer() == null || faq.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("Domanda e risposta non possono essere vuote.");
        }
        faq.setQuestion(faq.getQuestion().trim());
        faq.setAnswer(faq.getAnswer().trim());
        return faqRepository.save(faq);
    }


    public void deleteFaqById(Long id) {
        faqRepository.deleteById(id);
    }
    public Faq updateFaq(Long id, Faq updatedFaq) {
        return faqRepository.findById(id)
                .map(existing -> {
                    existing.setQuestion(updatedFaq.getQuestion());
                    existing.setAnswer(updatedFaq.getAnswer());
                    return faqRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("FAQ non trovata con ID: " + id));
    }


    public Faq getFaqById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ non trovata con ID: " + id));
    }




}

