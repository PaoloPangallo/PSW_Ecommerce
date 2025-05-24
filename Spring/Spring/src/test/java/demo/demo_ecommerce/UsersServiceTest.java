package demo.demo_ecommerce;

import demo.demo_ecommerce.dtos.UpdateUserDTO;
import demo.demo_ecommerce.dtos.UserDTO;
import demo.demo_ecommerce.dtos.UserResponseDTO;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.*;
import demo.demo_ecommerce.services.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @InjectMocks
    private UsersService usersService;

    @Mock private UsersRepository usersRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CartRepository cartRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private WishlistRepository wishlistRepository;
    @Mock private ReviewRepository reviewRepository;

    @Test
    void testCreateUser_Success() {
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        dto.setEmail("user@mail.com");
        dto.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("user");

        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usersRepository.save(any())).thenReturn(savedUser);

        User created = usersService.createUser(dto);

        assertNotNull(created);
        assertEquals("user", created.getUsername());
        verify(usersRepository).save(any());
    }

    @Test
    void testUpdateUser_OnlyEmailAndCity() {
        Long userId = 1L;
        User existing = new User(); existing.setId(userId); existing.setEmail("old@mail.com");
        when(usersRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(usersRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setEmail("new@mail.com");
        dto.setCity("Milano");

        UserResponseDTO updated = usersService.updateUser(userId, dto);

        assertEquals("new@mail.com", updated.getEmail());
        assertEquals("Milano", updated.getCity());
    }

    @Test
    void testRegisterUser_CreatesCart() {
        UserDTO dto = new UserDTO();
        dto.setUsername("register");
        dto.setEmail("register@mail.com");
        dto.setPassword("secure");

        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usersRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(99L);
            return u;
        });

        usersService.registerUser(dto);

        verify(usersRepository).save(any());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testDeleteUser_RemovesUserAndUnlinksOrders() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);

        Order order1 = new Order(); order1.setUser(user);
        Order order2 = new Order(); order2.setUser(user);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order1, order2));

        usersService.deleteUser(userId);

        assertNull(order1.getUser());
        assertNull(order2.getUser());
        verify(usersRepository).delete(user);
    }

    @Test
    void testGetUserById_UserExists() {
        Long id = 1L;
        User user = new User(); user.setId(id); user.setUsername("check");
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));

        User result = usersService.getUserById(id);
        assertEquals("check", result.getUsername());
    }


}
