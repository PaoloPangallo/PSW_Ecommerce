package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.UserNotFoundException;
import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.dtos.UserResponseDTO;  // Assicurati di creare questa classe DTO
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;

    public UsersService(UsersRepository usersRepository,
                        PasswordEncoder passwordEncoder,
                        CartRepository cartRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartRepository = cartRepository;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination");
        return usersRepository.findAll(pageable);
    }

    // Fetch user by ID
    public User getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return findUserById(id);
    }

    // Crea un nuovo utente con i nuovi campi phone e address
    public User createUser(@Valid UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.USER);
        // Imposta i nuovi campi
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        return usersRepository.save(user);
    }

    // Aggiorna un utente esistente (inclusi i nuovi campi) e restituisce un DTO di risposta
    @Transactional
    public UserResponseDTO updateUser(Long id, @Valid User userDetails) {
        logger.info("Updating user with ID: {}", id);
        User existingUser = findUserById(id);

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        // Aggiorna i nuovi campi se forniti
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setAddress(userDetails.getAddress());

        // Aggiorna la password solo se fornita
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        // Aggiorna il ruolo solo se fornito
        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }

        User savedUser = usersRepository.save(existingUser);
        return toResponseDTO(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        if (!usersRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        usersRepository.deleteById(id);
    }

    // Get users by role
    public List<User> getUsersByRole(Role role) {
        logger.info("Fetching users with role: {}", role);
        return usersRepository.findByRole(role);
    }

    // Metodo per la ricerca degli utenti
    public Page<User> searchUsers(String username, String email, Pageable pageable) {
        if (username != null && email != null) {
            return usersRepository.findByUsernameContainingAndEmailContaining(username, email, pageable);
        } else if (username != null) {
            return usersRepository.findByUsernameContaining(username, pageable);
        } else if (email != null) {
            return usersRepository.findByEmailContaining(email, pageable);
        } else {
            return usersRepository.findAll(pageable);
        }
    }

    // Metodo per registrare un utente (usato dall'endpoint di registrazione)
    @Transactional
    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.USER);
        // Imposta i nuovi campi
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());

        User savedUser = usersRepository.save(user);
        // Crea il carrello associato al nuovo utente
        Cart newCart = new Cart(savedUser);
        cartRepository.save(newCart);

        return savedUser;
    }

    // Metodo che restituisce un Optional
    public Optional<User> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    // Metodo aggiunto: restituisce direttamente l'utente o lancia un'eccezione se non trovato
    public User getUserByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    // Helper method per convertire un'entità User in un DTO di risposta
    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole()));
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setCap(user.getCap());
        dto.setCity(user.getCity());
        dto.setRegion(user.getRegion());
        dto.setCountry(user.getCountry());
        return dto;
    }

    // Helper method per trovare un utente per ID
    private User findUserById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
