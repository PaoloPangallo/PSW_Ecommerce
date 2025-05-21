package demo.demo_ecommerce.Controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/public")
public class PublicFileController {

    private final Path uploadDir = Paths.get("uploads/profile_images");
    @GetMapping("/profile_images/{filename:.+}")
    public ResponseEntity<Resource> serveProfileImage(@PathVariable String filename) {
        try {
            Path file = uploadDir.resolve(filename).normalize();
            System.out.println("Cerco il file: " + file.toAbsolutePath());

            Resource resource = new UrlResource(file.toUri());
            System.out.println("Resource.exists(): " + resource.exists());
            System.out.println("Resource.isReadable(): " + resource.isReadable());

            if (!resource.exists()) {
                return ResponseEntity.status(404).body(null);
            }
            if (!resource.isReadable()) {
                return ResponseEntity.status(403).body(null);
            }

            return ResponseEntity.ok().body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
