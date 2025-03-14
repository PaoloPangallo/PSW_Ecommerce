package demo.demo_ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;
@JsonIgnoreProperties({"products"}) // Evita problemi di serializzazione
@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString
@RequiredArgsConstructor  // Lombok per getter, setter, toString, equals, hashCode
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private List<Product> products;  // Relazione con i prodotti


    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        // S-proxiamo entrambi gli oggetti, così ci liberiamo del check su HibernateProxy
        Category that = (Category) Hibernate.unproxy(o);
        Category me = (Category) Hibernate.unproxy(this);

        // Se una delle due è null dopo unproxy (o non è nemmeno un Category), false
        if (!(that instanceof Category)) return false;

        // Se entrambi hanno un ID, confrontiamo quelli
        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }

        // Se arrivi qui, almeno uno dei due ID è null => fallback (o considerali diversi)
        return false;
    }


    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
