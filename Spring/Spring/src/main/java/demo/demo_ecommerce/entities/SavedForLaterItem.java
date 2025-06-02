package demo.demo_ecommerce.entities;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_for_later")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SavedForLaterItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Product product;

    private int quantity;
}
