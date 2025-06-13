package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CheckoutRequest;
import demo.demo_ecommerce.dtos.CheckoutResponse;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.UsersRepository;
import demo.demo_ecommerce.services.CheckoutService;
import demo.demo_ecommerce.services.DeliveryEstimationService;
import demo.demo_ecommerce.entities.Order;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final DeliveryEstimationService deliveryEstimationService;
    private final UsersRepository usersRepository;

    public CheckoutController(CheckoutService checkoutService, DeliveryEstimationService deliveryEstimationService,
                              UsersRepository usersRepository) {
        this.checkoutService = checkoutService;
        this.deliveryEstimationService = deliveryEstimationService;
        this.usersRepository = usersRepository;
    }

    // 🔐 Solo ADMIN o l'utente stesso possono fare checkout
    @PostMapping("/{userId}")
    public ResponseEntity<CheckoutResponse> processCheckout(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentUsername = authentication.getName();

        User currentUser = usersRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Verifica se è admin o il proprietario
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Order order = checkoutService.processCheckout(
                userId,
                checkoutRequest.getTransaction(),
                checkoutRequest.getShipping(),
                checkoutRequest.getConfirmedItemIds()
        );

        CheckoutResponse response = new CheckoutResponse(
                order.getId(),
                order.getStatus(),
                "Checkout completato con successo"
        );
        return ResponseEntity.ok(response);
    }

    // Pubblico: chiunque può stimare una consegna
    @GetMapping("/estimate")
    public ResponseEntity<LocalDate> getEstimate(@RequestParam String cap) {
        LocalDate estimate = deliveryEstimationService.estimateDeliveryDate(cap);
        return ResponseEntity.ok(estimate);
    }
}
