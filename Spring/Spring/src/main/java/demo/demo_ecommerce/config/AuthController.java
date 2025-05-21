package demo.demo_ecommerce.config;

import demo.demo_ecommerce.dtos.LoginResponseDTO;
import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.services.PasswordResetService;
import demo.demo_ecommerce.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);


    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetService passwordResetService;

    public AuthController(UsersService usersService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          PasswordResetService passwordResetService) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username e password sono obbligatori."));
        }

        var userOpt = usersService.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            logger.warn("Tentativo di login fallito: utente non trovato [{}]", loginRequest.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Username o password non validi."));
        }

        var user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Tentativo di login fallito: password errata per [{}]", user.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Username o password non validi."));
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        logger.info("Login riuscito per utente [{}]", user.getUsername());

        return ResponseEntity.ok(new LoginResponseDTO(token, user.getId()));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Compila tutti i campi obbligatori."));
        }

        if (usersService.findByUsername(userDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username già esistente."));
        }

        if (usersService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email già registrata."));
        }

        usersService.registerUser(userDTO);
        return ResponseEntity.ok(Map.of("message", "Registrazione completata con successo."));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        passwordResetService.createPasswordResetToken(email);
        return ResponseEntity.ok(Map.of("message", "Se l'email è corretta, riceverai un link per reimpostare la password.").toString());
    }


    // 🔒 Reset password con token
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (token == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token non valido o password troppo corta."));
        }

        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password aggiornata con successo."));
        } catch (Exception e) {
            logger.error("Errore durante reset password: {}", e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", "Reset password fallito: " + e.getMessage()));
        }
    }


}
