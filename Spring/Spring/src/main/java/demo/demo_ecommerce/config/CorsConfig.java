package demo.demo_ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // ✅ Permette tutte le chiamate API
                .allowedOrigins("http://localhost:4200") // ✅ Permette richieste dal frontend Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // ✅ Consenti tutti i metodi
                .allowedHeaders("*") // ✅ Consenti tutti gli header
                .allowCredentials(true); // ✅ Se usi autenticazione con JWT o sessioni
    }
}
