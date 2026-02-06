package src.convenience.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import src.convenience.dto.product.ProductResponse;
import src.convenience.service.productService.ProductService;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/products")
    public ApiResponse<List<ProductResponse>> getProductList() {
        return ApiResponse.success(productService.getAllProducts());
    }
}
