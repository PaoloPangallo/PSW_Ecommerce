package demo.demo_ecommerce.Controllers;

import com.itextpdf.text.DocumentException;
import demo.demo_ecommerce.dtos.OrderDTO;
import demo.demo_ecommerce.dtos.OrderRequestDTO;
import demo.demo_ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/users/{userId}/orders")
public class OrdersController {

    private final OrderService orderService;

    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Crea un nuovo ordine per l'utente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordine creato con successo", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Errore nella richiesta")
    })
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @PostMapping
    public ResponseEntity<?> createOrder(@PathVariable Long userId,
                                         @RequestBody OrderRequestDTO request) {
        try {
            OrderDTO orderDto = orderService.createOrder(
                    userId,
                    request.getShippingMethod(),
                    request.getConfirmedItemIds() // ✅ Passaggio corretto
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Elenca tutti gli ordini di un utente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di ordini recuperata con successo", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping
    public ResponseEntity<?> getUserOrders(@PathVariable Long userId,
                                           @PageableDefault(size = 10) Pageable pageable) {
        Page<OrderDTO> ordersPage = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @Operation(summary = "Ottieni i dettagli di un singolo ordine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dettagli dell'ordine recuperati con successo", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        try {
            OrderDTO orderDto = orderService.getOrderById(userId, orderId);
            return ResponseEntity.ok(orderDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ordine non trovato: " + e.getMessage());
        }
    }

    @GetMapping(
            value = "/{orderId}/invoice",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long userId, @PathVariable Long orderId) throws IOException, DocumentException {
        byte[] pdf = orderService.generateInvoicePdf(userId, orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("ordine_" + orderId + ".pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
