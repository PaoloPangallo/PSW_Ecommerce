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
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Il file è vuoto");
        }
        // Controlla che il file sia un'immagine
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Il file deve essere un'immagine");
        }
        // Controlla la dimensione (ad esempio, max 5 MB)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Il file supera la dimensione massima consentita (5MB)");
        }

        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        Blob blob = storage.create(blobInfo, file.getBytes());
        blob.createAcl(com.google.cloud.storage.Acl.of(com.google.cloud.storage.Acl.User.ofAllUsers(), com.google.cloud.storage.Acl.Role.READER));

        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
    }

}
