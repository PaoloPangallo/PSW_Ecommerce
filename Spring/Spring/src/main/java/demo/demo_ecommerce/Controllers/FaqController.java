package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.Faq;
import demo.demo_ecommerce.services.FaqService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin(origins = "*")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    // Pubblico
    @GetMapping
    public List<Faq> getAllFaqs() {
        return faqService.getAllFaqs();
    }

    // Solo ADMIN può aggiungere nuove FAQ
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Faq createFaq(@RequestBody Faq faq) {
        return faqService.saveFaq(faq);
    }

    // Solo ADMIN può cancellare una FAQ
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFaq(@PathVariable Long id) {
        faqService.deleteFaqById(id);
    }
}
