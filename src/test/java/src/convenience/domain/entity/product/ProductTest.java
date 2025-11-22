package src.convenience.domain.entity.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.ONE_PLUS_ONE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductTest {

    Product product;

    @BeforeEach
    void init() {
        product = new Product("콜라", 1000, 5, ONE_PLUS_ONE.getPromotionType());
    }

    @Test
    void 재고_검증_성공_테스트() {
        product.checkQuantity(5); // 예외 없어야 함
    }

    @Test
    void 재고_검증_실패_테스트() {
        assertThatThrownBy(() -> product.checkQuantity(6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    void 재고_차감_성공_테스트() {
        product.decreaseQuantity(3);

        assertThat(product.getQuantity()).isEqualTo(2);
    }

    @Test
    void 재고_차감_실패_테스트() {
        assertThatThrownBy(() -> product.decreaseQuantity(6))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 재고_비교_테스트() {
        boolean result1 = product.compareQuantity(3);
        boolean result2 = product.compareQuantity(6);

        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }
}
