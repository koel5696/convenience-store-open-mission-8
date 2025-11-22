package src.convenience.service.cartService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static src.convenience.productEnum.TestProductId.CIDER;
import static src.convenience.productEnum.TestProductId.COKE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class CartServiceTest {

    @Autowired
    CartService cartService;

    @Test
    void 장바구니_정상_담기_테스트() {
        cartService.addItem(COKE.id(), 3);
    }

    @Test
    void 장바구니_담기_재고초과_예외_테스트() {
        assertThatThrownBy(() -> cartService.addItem(COKE.id(), 999999))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 장바구니_정상_제거_테스트() {
        cartService.addItem(COKE.id(), 2);
        cartService.removeItem(COKE.id());
    }

    @Test
    void 장바구니_제거_상품_없음_예외_테스트() {
        cartService.addItem(CIDER.id(), 2);
        assertThatThrownBy(() -> cartService.removeItem(COKE.id()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
