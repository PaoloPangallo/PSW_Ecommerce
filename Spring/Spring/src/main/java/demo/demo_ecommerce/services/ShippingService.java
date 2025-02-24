package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.ShippingNotFoundException;
import demo.demo_ecommerce.entities.Shipping;
import demo.demo_ecommerce.entities.Shipping.ShippingStatus;
import demo.demo_ecommerce.repositories.ShippingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShippingService {

    private final ShippingRepository shippingRepository;

    @Autowired
    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    // Crea una nuova spedizione
    @Transactional
    public Shipping createShipping(Shipping shipping) {
        return shippingRepository.save(shipping);
    }

    // Recupera una spedizione per ID
    public Shipping getShippingById(Long id) {
        return shippingRepository.findById(id)
                .orElseThrow(() -> new ShippingNotFoundException("Spedizione non trovata con ID: " + id));
    }

    // Recupera tutte le spedizioni con paginazione
    public Page<Shipping> getAllShippings(Pageable pageable) {
        return shippingRepository.findAll(pageable);
    }

    // Recupera spedizioni per stato
    public List<Shipping> getShippingsByStatus(ShippingStatus status) {
        return shippingRepository.findByStatus(status);
    }

    // Recupera spedizioni prima di una certa data
    public List<Shipping> getShippingsBeforeDate(LocalDateTime date) {
        return shippingRepository.findByShippingDateBefore(date);
    }

    // Recupera spedizioni dopo una certa data
    public List<Shipping> getShippingsAfterDate(LocalDateTime date) {
        return shippingRepository.findByShippingDateAfter(date);
    }

    // Aggiorna lo stato della spedizione
    @Transactional
    public Shipping updateShippingStatus(Long id, ShippingStatus status) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new ShippingNotFoundException("Spedizione non trovata con ID: " + id));
        shipping.setStatus(status);
        return shippingRepository.save(shipping);
    }

    // Elimina una spedizione
    @Transactional
    public void deleteShipping(Long id) {
        if (!shippingRepository.existsById(id)) {
            throw new ShippingNotFoundException("Spedizione non trovata con ID: " + id);
        }
        shippingRepository.deleteById(id);
    }
}
