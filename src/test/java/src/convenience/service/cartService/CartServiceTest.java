package src.convenience.service.cartService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import src.convenience.domain.cart.Cart;


@SpringBootTest
public class CartServiceTest {

    @Autowired
    CartService cartService;

    @Test
    void 장바구니_정상_담기_테스트() {
        cartService.addItem(1L, 3);
    }

    @Test
    void 장바구니_담기_재고초과_예외_테스트() {
        assertThatThrownBy(() -> cartService.addItem(1L, 999999))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 장바구니_정상_제거_테스트() {
        cartService.addItem(1L, 2);
        cartService.removeItem(1L);
    }

    @Test
    void 장바구니_제거_상품_없음_예외_테스트() {
        cartService.addItem(2L, 2);
        assertThatThrownBy(() -> cartService.removeItem(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
