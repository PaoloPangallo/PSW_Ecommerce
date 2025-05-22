package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.UserNotFoundException;
import demo.demo_ecommerce.dtos.*;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final ReviewRepository reviewRepository;;


    public UsersService(UsersRepository usersRepository,
                        PasswordEncoder passwordEncoder,
                        CartRepository cartRepository,
                        OrderRepository orderRepository,
                        WishlistRepository wishlistRepository1, ReviewRepository reviewRepository1) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.wishlistRepository = wishlistRepository1;
        this.reviewRepository = reviewRepository1;
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
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setCap(userDTO.getCap());
        user.setCity(userDTO.getCity());
        user.setRegion(userDTO.getRegion());
        user.setCountry(userDTO.getCountry());
        return usersRepository.save(user);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, @Valid UpdateUserDTO updateUserDTO) {
        logger.info("Updating user with ID: {}", id);
        User existingUser = findUserById(id);

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

        // Recupera l'utente (lancia eccezione se non trovato)
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Gestione degli ordini: dissocia l'utente dagli ordini associati
        List<Order> orders = orderRepository.findByUserId(user.getId());
        for (Order order : orders) {
            order.setUser(null);
        }
        orderRepository.flush();

        // Ora, cancellando l'utente, grazie al cascade e orphanRemoval nelle entità collegate
        // (cart, wishlist, reviews) JPA si occuperà di eliminare in cascata tutte le entità associate.
        usersRepository.delete(user);
        usersRepository.flush();

        logger.info("User with ID {} deleted successfully.", id);
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
    public void registerUser(UserDTO userDTO) {
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

    }

    public Optional<User> findByUsername(String username) {
        return usersRepository.findByUsername(username);
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
        dto.setProfileImageUrl(user.getProfileImageUrl()); // ✅ deve esserci questa riga!

        return dto;
    }

    // Helper per trovare un utente per ID
    private User findUserById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public UserProfileSummaryDTO getUserProfileSummary(Long userId) {
        User user = findUserById(userId);

        int totalOrders = orderRepository.countByUserId(userId);
        BigDecimal totalSpent = orderRepository.sumTotalByUserId(userId);
        int wishlistCount = wishlistRepository.countByUserId(userId);
        int reviewsCount = reviewRepository.countByUserId(userId);


        UserProfileSummaryDTO dto = new UserProfileSummaryDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCity(user.getCity());
        dto.setCountry(user.getCountry());
        dto.setTotalOrders(totalOrders);
        dto.setTotalSpent(totalSpent != null ? totalSpent : BigDecimal.ZERO);
        dto.setWishlistCount(wishlistCount);
        dto.setReviewsCount(reviewsCount);
        dto.setProfileImageUrl(user.getProfileImageUrl());


        return dto;
    }

    public String uploadProfileImage(Long userId, MultipartFile file) {
        User user = findUserById(userId);
        String fileName = "user_" + userId + "_" + file.getOriginalFilename();
        Path imagePath = Paths.get("uploads/profile_images", fileName);

        try {
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'immagine", e);
        }
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/public/profile_images/")
                .path(fileName)
                .toUriString();


        user.setProfileImageUrl(imageUrl);
        usersRepository.save(user);
        return imageUrl;
    }

    @Transactional
    public void removeProfileImage(Long userId) {
        User user = findUserById(userId);

        // Debug log
        System.out.println(">> Prima: " + user.getProfileImageUrl());

        user.setProfileImageUrl(null);
        usersRepository.save(user);

        System.out.println(">> Dopo: " + user.getProfileImageUrl());
    }


    @Transactional
    public Page<ReviewDTO> getUserReviews(Long userId, Pageable pageable) {
        // Verifica che l’utente esista
        findUserById(userId);

        return reviewRepository.findByUserId(userId, pageable)
                .map(ReviewDTO::fromEntity);
    }








}
