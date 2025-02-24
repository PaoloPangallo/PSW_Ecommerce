package demo.demo_ecommerce.Controllers;

import java.util.List;

import demo.demo_ecommerce.dtos.OrderDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/users/{userId}/orders")
public class OrdersController {

    private final OrderService orderService;

    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Crea un nuovo ordine per l'utente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordine creato con successo", content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Errore nella richiesta")
    })
    @PostMapping
    public ResponseEntity<?> createOrder(@PathVariable Long userId) {
        try {
            Order order = orderService.createOrder(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore: " + e.getMessage());
        }
    }

    @Operation(summary = "Elenca tutti gli ordini di un utente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di ordini recuperata con successo", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })


    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId)
                .stream()
                .map(OrderDTO::fromEntity) // Conversione
                .toList();
        return ResponseEntity.ok(orders);
    }


    @Operation(summary = "Ottieni i dettagli di un singolo ordine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dettagli dell'ordine recuperati con successo", content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(userId, orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ordine non trovato: " + e.getMessage());
        }
    }


}
