package demo.demo_ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;

@JsonIgnoreProperties({"products"})
@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
    private List<Product> products;

    @Version
    private Long version = 0L;

    @PrePersist
    public void prePersist() {
        if (version == null) version = 0L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Category that = (Category) Hibernate.unproxy(o);
        Category me = (Category) Hibernate.unproxy(this);
        if (!(that instanceof Category)) return false;
        if (this.id != null && that.id != null) return this.id.equals(that.id);
        return false;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
