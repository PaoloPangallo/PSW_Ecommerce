package demo.demo_ecommerce.config;


import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.services.UsersService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


class AuthControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("securepassword");

        when(usersService.findByUsername("testuser")).thenReturn(Optional.empty());
        when(usersService.existsByEmail("testuser@example.com")).thenReturn(false);

        // Act
        ResponseEntity<String> response = authController.register(userDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(usersService, times(1)).registerUser(userDTO);
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("securepassword");

        when(usersService.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        // Act
        ResponseEntity<String> response = authController.register(userDTO);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("securepassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("$2a$10$abcde..."); // Simulazione di una password codificata
        user.setRole(Role.USER); // Assegna un ruolo per evitare il NullPointerException

        // Simula il comportamento del service e delle dipendenze
        when(usersService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("securepassword", user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken("testuser", "USER"))
                .thenReturn("jwt-token");

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("jwt-token", response.getBody().getToken());

        // Verifica che i metodi mockati siano stati chiamati correttamente
        verify(usersService, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("securepassword", user.getPassword());
        verify(jwtTokenProvider, times(1)).generateToken("testuser", "USER");
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("$2a$10$abcde...");

        when(usersService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequest);
        });
        assertEquals("Invalid username or password", exception.getMessage());
    }
}

