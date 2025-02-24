package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Operation(summary = "Recupera tutti gli utenti", description = "Restituisce la lista di tutti gli utenti registrati.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di utenti recuperata con successo.")
    })


    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = usersService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Recupera un utente specifico", description = "Restituisce i dettagli di un utente specifico tramite ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente recuperato con successo."),
            @ApiResponse(responseCode = "404", description = "Utente non trovato.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = usersService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Recupera utenti in base al ruolo", description = "Restituisce gli utenti che hanno uno specifico ruolo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di utenti recuperata con successo."),
            @ApiResponse(responseCode = "400", description = "Ruolo non valido.")
    })
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = usersService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Crea un nuovo utente", description = "Aggiunge un nuovo utente al sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente creato con successo."),
            @ApiResponse(responseCode = "400", description = "Dati dell'utente non validi.")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        User createdUser = usersService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    @Operation(summary = "Aggiorna un utente esistente", description = "Modifica i dettagli di un utente specifico tramite ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente aggiornato con successo."),
            @ApiResponse(responseCode = "404", description = "Utente non trovato.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User updatedUser = usersService.updateUser(id, user);
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
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            Pageable pageable) {
        Page<User> users = usersService.searchUsers(username, email, pageable);
        return ResponseEntity.ok(users);
    }


}
