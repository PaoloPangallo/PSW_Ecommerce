// src/main/java/demo/demo_ecommerce/config/LoginResponseDTO.java
package demo.demo_ecommerce.dtos;

public class LoginResponseDTO {
    private String token;
    private Long userId;

    public LoginResponseDTO(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    // Getters (e setters se necessario)
    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }
}
