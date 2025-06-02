package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.Utility.ResourceNotFoundException;
import demo.demo_ecommerce.dtos.*;
import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Operation(summary = "Recupera tutti gli utenti", description = "Restituisce la lista di tutti gli utenti registrati.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di utenti recuperata con successo.")
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<User> usersPage = usersService.getAllUsers(pageable);
        Page<UserResponseDTO> dtoPage = usersPage.map(UserResponseDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(summary = "Recupera un utente specifico", description = "Restituisce i dettagli di un utente specifico tramite ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente recuperato con successo."),
            @ApiResponse(responseCode = "404", description = "Utente non trovato.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = usersService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        UserResponseDTO dto = UserResponseDTO.fromEntity(user);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Recupera utenti in base al ruolo", description = "Restituisce gli utenti che hanno uno specifico ruolo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di utenti recuperata con successo."),
            @ApiResponse(responseCode = "400", description = "Ruolo non valido.")
    })
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable Role role) {
        List<User> users = usersService.getUsersByRole(role);
        List<UserResponseDTO> dtoList = users.stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @Operation(summary = "Crea un nuovo utente", description = "Aggiunge un nuovo utente al sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente creato con successo."),
            @ApiResponse(responseCode = "400", description = "Dati dell'utente non validi.")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User createdUser = usersService.createUser(userDTO);
        UserResponseDTO dto = UserResponseDTO.fromEntity(createdUser);
        logger.info("User created with ID: {}", dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Aggiorna un utente esistente", description = "Modifica i dettagli di un utente specifico tramite ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente aggiornato con successo."),
            @ApiResponse(responseCode = "404", description = "Utente non trovato.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {

        UserResponseDTO updatedUser = usersService.updateUser(id, updateUserDTO);
        if (updatedUser == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        logger.info("User updated with ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }


    @Operation(summary = "Elimina un utente", description = "Rimuove un utente specifico dal sistema tramite ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utente eliminato con successo."),
            @ApiResponse(responseCode = "404", description = "Utente non trovato.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        logger.info("User deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            Pageable pageable) {
        Page<User> usersPage = usersService.searchUsers(username, email, pageable);
        Page<UserResponseDTO> dtoPage = usersPage.map(UserResponseDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{userId}/profile-summary")
    public ResponseEntity<UserProfileSummaryDTO> getUserProfile(@PathVariable Long userId) {
        UserProfileSummaryDTO profile = usersService.getUserProfileSummary(userId);
        if (profile == null) {
            throw new ResourceNotFoundException("User profile not found for ID: " + userId);
        }
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String imageUrl = usersService.uploadProfileImage(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    @PutMapping("/{id}/remove-image")
    public ResponseEntity<Void> removeProfileImage(@PathVariable Long id) {
        usersService.removeProfileImage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<Page<ReviewDTO>> getUserReviews(
            @PathVariable Long id,
            @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewDTO> reviews = usersService.getUserReviews(id, pageable);
        return ResponseEntity.ok(reviews);
    }
}
