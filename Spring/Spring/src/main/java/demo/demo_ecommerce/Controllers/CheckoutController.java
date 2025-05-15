package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CheckoutRequest;
import demo.demo_ecommerce.dtos.CheckoutResponse;
import demo.demo_ecommerce.services.CheckoutService;
import demo.demo_ecommerce.services.DeliveryEstimationService;
import demo.demo_ecommerce.entities.Order;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final DeliveryEstimationService deliveryEstimationService;

    public CheckoutController(CheckoutService checkoutService, DeliveryEstimationService deliveryEstimationService) {
        this.checkoutService = checkoutService;
        this.deliveryEstimationService = deliveryEstimationService;
    }

    // POST /checkout/{userId}
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

    // GET /checkout/estimate?cap=80100
    @GetMapping("/estimate")
    public ResponseEntity<LocalDate> getEstimate(@RequestParam String cap) {
        LocalDate estimate = deliveryEstimationService.estimateDeliveryDate(cap);
        return ResponseEntity.ok(estimate);
    }
}
