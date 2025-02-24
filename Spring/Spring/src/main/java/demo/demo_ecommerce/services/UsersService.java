package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.UserNotFoundException;
import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
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

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
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

    // Create a new user with default role and encoded password
    public User createUser(@Valid UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.USER);
        return usersRepository.save(user);
    }


    @Transactional
    public User updateUser(Long id, @Valid User userDetails) {
        logger.info("Updating user with ID: {}", id);
        User existingUser = findUserById(id);

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());

        // Aggiorna la password solo se fornita
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        // Aggiorna il ruolo solo se fornito
        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }

        return usersRepository.save(existingUser);
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

    // Helper method to find user by ID
    private User findUserById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }


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
    public void registerUser(UserDTO userDTO) {
        if (usersRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (usersRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.USER); // Ruolo predefinito

        usersRepository.save(user);
    }

    // Metodo per creare utenti come amministratore
    public User createUserAsAdmin(@org.jetbrains.annotations.NotNull UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.USER);
        return usersRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }



}
