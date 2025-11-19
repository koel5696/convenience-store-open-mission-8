package src.convenience.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;

@SpringBootTest
class CsvProductDataLoaderTest {
    private static final int INITIAL_PRODUCT_QUANTITY = 13;

    @Autowired
    ProductRepository productRepository;

    @Test
    void 초기_상품_데이터_DB_정상주입_테스트() {
        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(INITIAL_PRODUCT_QUANTITY);

        // 대표 상품 몇 개만 샘플 검증
        assertThat(products)
                .extracting(Product::getName)
                .contains("콜라", "사이다", "탄산수", "정식도시락", "물");

        assertThat(products)
                .filteredOn(p -> p.getName().equals("콜라"))
                .extracting(Product::getPromotion)
                .contains("2+1");
    }
}