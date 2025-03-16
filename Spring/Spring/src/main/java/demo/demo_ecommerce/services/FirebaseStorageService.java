package demo.demo_ecommerce.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final Storage storage;

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Genera un nome unico per il file
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        // Carica il file su Firebase Storage
        storage.create(blobInfo, file.getBytes());

        // Restituisce l'URL pubblico dell'immagine
        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
    }
}
