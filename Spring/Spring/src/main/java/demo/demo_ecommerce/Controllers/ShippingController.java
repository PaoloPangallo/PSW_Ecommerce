package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.ShippingDTO;
import demo.demo_ecommerce.entities.Shipping;
import demo.demo_ecommerce.entities.Shipping.ShippingStatus;
import demo.demo_ecommerce.services.ShippingService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shippings")
public class ShippingController {

    private final ShippingService shippingService;

    @Autowired
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    // Crea una nuova spedizione
    @PostMapping
    public ResponseEntity<ShippingDTO> createShipping(@Valid @RequestBody Shipping shipping) {
        Shipping createdShipping = shippingService.createShipping(shipping);
        return ResponseEntity.status(HttpStatus.CREATED).body(ShippingDTO.fromEntity(createdShipping));
    }

    // Recupera una spedizione tramite ID
    @GetMapping("/{id}")
    public ResponseEntity<ShippingDTO> getShippingById(@PathVariable Long id) {
        Shipping shipping = shippingService.getShippingById(id);
        return ResponseEntity.ok(ShippingDTO.fromEntity(shipping));
    }

    // Recupera tutte le spedizioni con paginazione
    @GetMapping
    public ResponseEntity<Page<ShippingDTO>> getAllShippings(Pageable pageable) {
        Page<Shipping> shippings = shippingService.getAllShippings(pageable);
        Page<ShippingDTO> dtoPage = shippings.map(ShippingDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    // Aggiorna lo stato della spedizione
    @PatchMapping("/{id}/status")
    public ResponseEntity<ShippingDTO> updateShippingStatus(
            @PathVariable Long id,
            @RequestParam ShippingStatus status) {
        Shipping updatedShipping = shippingService.updateShippingStatus(id, status);
        return ResponseEntity.ok(ShippingDTO.fromEntity(updatedShipping));
    }

    // Elimina una spedizione
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipping(@PathVariable Long id) {
        shippingService.deleteShipping(id);
        return ResponseEntity.noContent().build();
    }
}
