package demo.demo_ecommerce.services;

import demo.demo_ecommerce.repositories.ShippingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {

    @Autowired
    public ShippingService(ShippingRepository shippingRepository) {
    }

}
