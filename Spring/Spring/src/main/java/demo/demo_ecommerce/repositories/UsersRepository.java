package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    // Controlla se esiste un utente con una determinata email
    boolean existsByEmail(String email);

    // Controlla se esiste un utente con un determinato username
    boolean existsByUsername(String username);

    // Trova un utente tramite email
    Optional<User> findByEmail(String email);

    // Cerca utenti il cui username contiene una determinata stringa
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContains(String username);

    // Recupera tutti gli utenti con supporto per paginazione
    Page<User> findAll(Pageable pageable);

    // Aggiunto: Trova utenti in base al ruolo
    List<User> findByRole(Role role);

    // Aggiunto: Verifica se esistono utenti con un ruolo specifico
    boolean existsByRole(Role role);

    // Aggiunto: Recupera utenti con ruolo specifico con paginazione
    Page<User> findByRole(Role role, Pageable pageable);



    // Metodo per trovare un utente in base al nome utente
    Optional<User> findByUsername(String username);


    Page<User> findByUsernameContaining(String username, Pageable pageable);

    Page<User> findByEmailContaining(String email, Pageable pageable);

    Page<User> findByUsernameContainingAndEmailContaining(String username, String email, Pageable pageable);


}





