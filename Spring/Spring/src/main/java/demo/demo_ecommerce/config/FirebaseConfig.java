package demo.demo_ecommerce.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Storage firebaseStorage() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");

        if (serviceAccount == null) {
            throw new IOException("File firebase-key.json non trovato.");
        }

        return StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
                .getService();
    }
}
