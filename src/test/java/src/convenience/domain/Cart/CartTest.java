package src.convenience.domain.Cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static src.convenience.productEnum.TestProductId.CIDER;
import static src.convenience.productEnum.TestProductId.COKE;
import static src.convenience.productEnum.TestProductId.JUICE;

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
        cart.addItem(COKE.id(), 3);

        assertThat(cart.currentQuantity(COKE.id())).isEqualTo(3);
    }

    @Test
    void 카트_제거_테스트() {
        cart.addItem(COKE.id(), 3);
        cart.removeItem(COKE.id());

        assertThat(cart.currentQuantity(COKE.id())).isEqualTo(0);
        assertThat(cart.getItems().containsKey(COKE.id())).isFalse();
    }

    @Test
    void 카트_클리어_테스트() {
        cart.addItem(CIDER.id(), 4);
        cart.addItem(JUICE.id(), 5);
        cart.clear();

        assertThat(cart.getItems().isEmpty()).isTrue();
    }

    @Test
    void 빈_카트_테스트() {
        cart.clear();
        assertThatThrownBy(() -> cart.checkCartEmpty())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("장바구니가 비어있습니다");
    }
}
