package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.Faq;
import demo.demo_ecommerce.services.FaqService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin(origins = "*")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public List<Faq> getAllFaqs() {
        return faqService.getAllFaqs();
    }
}

