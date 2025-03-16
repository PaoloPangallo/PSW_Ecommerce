package demo.demo_ecommerce;

import demo.demo_ecommerce.Controllers.FirebaseController;
import demo.demo_ecommerce.services.FirebaseStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FirebaseController.class)
class FirebaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Fake Image Content".getBytes()
        );

        when(firebaseStorageService.uploadFile(Mockito.any())).thenReturn("https://storage.googleapis.com/unilire.firebasestorage.app/test-image.jpg");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/storage/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("https://storage.googleapis.com/unilire.firebasestorage.app/test-image.jpg"));
    }
}
