package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CheckoutRequest;
import demo.demo_ecommerce.dtos.CheckoutResponse;
import demo.demo_ecommerce.services.CheckoutService;
import demo.demo_ecommerce.entities.Order;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    // Endpoint: POST /checkout/{userId}
    @PostMapping("/{userId}")
    public ResponseEntity<CheckoutResponse> processCheckout(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest) {

        Order order = checkoutService.processCheckout(
                userId,
                checkoutRequest.getTransaction(),
                checkoutRequest.getShipping()
        );

        CheckoutResponse response = new CheckoutResponse(
                order.getId(),
                order.getStatus(),
                "Checkout completato con successo"
        );
        return ResponseEntity.ok(response);
    }
}
