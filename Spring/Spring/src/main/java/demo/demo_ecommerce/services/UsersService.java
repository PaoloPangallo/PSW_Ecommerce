package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.UserNotFoundException;
import demo.demo_ecommerce.dtos.UpdateUserDTO;
import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.dtos.UserResponseDTO;
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

    public User getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return findUserById(id);
    }

    public User createUser(@Valid demo.demo_ecommerce.dtos.UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.USER);
        // Imposta i nuovi campi se presenti
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setCap(userDTO.getCap());
        user.setCity(userDTO.getCity());
        user.setRegion(userDTO.getRegion());
        user.setCountry(userDTO.getCountry());
        return usersRepository.save(user);
    }

    // Metodo update aggiornato per utilizzare il DTO dedicato UpdateUserDTO
    @Transactional
    public UserResponseDTO updateUser(Long id, @Valid UpdateUserDTO updateUserDTO) {
        logger.info("Updating user with ID: {}", id);
        User existingUser = findUserById(id);

        // Aggiorniamo solo i campi relativi a recapiti ed indirizzi se forniti
        if (updateUserDTO.getEmail() != null) {
            existingUser.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getPhone() != null) {
            existingUser.setPhone(updateUserDTO.getPhone());
        }
        if (updateUserDTO.getAddress() != null) {
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if (updateUserDTO.getCap() != null) {
            existingUser.setCap(updateUserDTO.getCap());
        }
        if (updateUserDTO.getCity() != null) {
            existingUser.setCity(updateUserDTO.getCity());
        }
        if (updateUserDTO.getRegion() != null) {
            existingUser.setRegion(updateUserDTO.getRegion());
        }
        if (updateUserDTO.getCountry() != null) {
            existingUser.setCountry(updateUserDTO.getCountry());
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

    public List<User> getUsersByRole(Role role) {
        logger.info("Fetching users with role: {}", role);
        return usersRepository.findByRole(role);
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

    @Transactional
    public User registerUser(demo.demo_ecommerce.dtos.UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.USER);
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setCap(userDTO.getCap());
        user.setCity(userDTO.getCity());
        user.setRegion(userDTO.getRegion());
        user.setCountry(userDTO.getCountry());

        User savedUser = usersRepository.save(user);
        // Crea il carrello associato al nuovo utente
        Cart newCart = new Cart(savedUser);
        cartRepository.save(newCart);

        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public User getUserByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    // Helper per convertire un'entità User in UserResponseDTO
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

    // Helper per trovare un utente per ID
    private User findUserById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
