package demo.demo_ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImageUploadResponse {
    private String fileName;
    private String url;
}
