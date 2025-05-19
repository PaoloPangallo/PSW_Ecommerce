package demo.demo_ecommerce.config;

import demo.demo_ecommerce.dtos.LoginResponseDTO;
import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.services.PasswordResetService;
import demo.demo_ecommerce.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequest loginRequest) {
        var user = usersService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(new LoginResponseDTO(token, user.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        if (usersService.findByUsername(userDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (usersService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        usersService.registerUser(userDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        passwordResetService.createPasswordResetToken(email);
        return ResponseEntity.ok(Map.of("message", "Se l'email è corretta, riceverai un link per reimpostare la password.").toString());
    }


    // 🔒 Reset password con token
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Token o nuova password mancanti.");
        }

        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password aggiornata con successo.");
    }

}
