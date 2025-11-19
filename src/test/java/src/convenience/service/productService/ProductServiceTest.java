package src.convenience.service.productService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.dto.product.ProductResponse;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Test
    void 모든_상품_DTO_정상_매핑_테스트() {
        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).isNotEmpty();

        assertThat(result)
                .allSatisfy(dto -> {
                    assertThat(dto.id()).isNotNull();
                    assertThat(dto.name()).isNotNull();
                    assertThat(dto.price()).isGreaterThan(0);
                });
    }

    @Test
    void 초기데이터에_존재하는_상품이_조회_테스트() {
        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result)
                .extracting(ProductResponse::name)
                .contains("콜라", "사이다", "물");   // 초기 데이터 기준
    }
}
