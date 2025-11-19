package src.convenience.domain.Cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.convenience.domain.cart.Cart;

public class CartTest {
    Cart cart;

    @BeforeEach
    void init() {
        cart = new Cart(); // 매 테스트마다 새로운 Cart 인스턴스 생성
    }

    @Test
    void 카트_담기_테스트() {
        cart.addItem(1L, 3);

        assertThat(cart.currentQuantity(1L)).isEqualTo(3);
    }

    @Test
    void 카트_제거_테스트() {
        cart.addItem(1L, 3);
        cart.removeItem(1L);

        assertThat(cart.currentQuantity(1L)).isEqualTo(0);
        assertThat(cart.getItems().containsKey(1L)).isFalse();
    }

    @Test
    void 카트_클리어_테스트() {
        cart.addItem(2L, 4);
        cart.addItem(3L, 5);
        cart.clear();

        assertThat(cart.getItems().isEmpty()).isTrue();
    }

    @Test
    void 빈_카트_테스트() {
        cart.clear();
        assertThatThrownBy(() -> cart.checkCartEmpty())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("장바구니가 비어있습니다.");
    }
}
