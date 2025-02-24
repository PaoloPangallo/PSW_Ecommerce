package demo.demo_ecommerce.config;



import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UsersService usersService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // Verifica username
        var user = usersService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Verifica password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Genera il token
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        // Ritorna il token
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        // Verifica se username o email esistono già
        if (usersService.findByUsername(userDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (usersService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Crea un nuovo utente
        usersService.registerUser(userDTO);

        return ResponseEntity.ok("User registered successfully");
    }

}
