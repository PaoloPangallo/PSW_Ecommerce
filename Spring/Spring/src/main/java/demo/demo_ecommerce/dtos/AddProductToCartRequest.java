package demo.demo_ecommerce.dtos;

public class AddProductToCartRequest {
    private Long productId;

    // Getter e Setter
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}