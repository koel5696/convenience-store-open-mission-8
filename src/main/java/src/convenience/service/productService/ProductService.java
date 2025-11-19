package src.convenience.service.productService;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;
import src.convenience.domain.entity.promotion.Promotion;
import src.convenience.domain.entity.promotion.PromotionRepository;
import src.convenience.dto.product.ProductPromotionDate;
import src.convenience.dto.product.ProductResponse;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public ProductService(ProductRepository productRepository,
                          PromotionRepository promotionRepository) {
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product product) {
        ProductPromotionDate date = null;
        Promotion promotion = promotionRepository.promotionCreate(product.getPromotion());
            if (!promotion.checkNonePromotion()) {
                date = new ProductPromotionDate(
                        promotion.getPromotionStartDate(),
                        promotion.getPromotionEndDate()
                );
            }
        return new ProductResponse(product, date);
    }
}
