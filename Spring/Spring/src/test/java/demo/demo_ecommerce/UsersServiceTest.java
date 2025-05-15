package demo.demo_ecommerce;

import demo.demo_ecommerce.dtos.UpdateUserDTO;
import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.dtos.UserResponseDTO;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.*;
import demo.demo_ecommerce.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersServiceTest {

    @Mock private UsersRepository usersRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CartRepository cartRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks private UsersService usersService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_returnsPageOfUsers() {
        User user = new User(); user.setUsername("pippo");
        Page<User> page = new PageImpl<>(List.of(user));
        when(usersRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<User> result = usersService.getAllUsers(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(usersRepository).findAll(any(PageRequest.class));
    }

    @Test
    void createUser_shouldEncodePasswordAndSave() {
        UserDTO dto = new UserDTO();
        dto.setUsername("mario");
        dto.setPassword("pass");
        dto.setEmail("mario@mail.com");

        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(usersRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = usersService.createUser(dto);

        assertEquals("encoded", saved.getPassword());
        assertEquals("mario", saved.getUsername());
        verify(usersRepository).save(any(User.class));
    }

    @Test
    void getUserById_shouldThrowIfNotFound() {
        when(usersRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usersService.getUserById(123L));
    }

    @Test
    void updateUser_shouldUpdateFields() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@mail.com");

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setEmail("new@mail.com");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = usersService.updateUser(1L, dto);

        assertEquals("new@mail.com", result.getEmail());
        verify(usersRepository).save(user);
    }
}
