package src.convenience.service.paymentService;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static src.convenience.productEnum.TestProductId.COKE;
import static src.convenience.productEnum.TestProductId.JUICE;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.cart.Cart;
import src.convenience.dto.payment.PayRequest;
import src.convenience.exception.MembershipException;
import src.convenience.exception.PromotionProductException;

@SpringBootTest
@Transactional
class PaymentFlowCheckerTest {

    @Autowired
    PaymentFlowChecker checker;

    @Autowired
    Cart cart;

    @BeforeEach
    void init() {
        cart.clear();
    }

    private void cartSetup() {
        cart.addItem(COKE.id(), 5);
        cart.addItem(JUICE.id(), 9);
    }

    @Test
    void 결제중_프로모션_누락_예외_테스트() {
        PayRequest request = new PayRequest(null, true, true);

        assertThatThrownBy(() -> checker.checkFlow(request, Map.of(COKE.id(), 5)))
                .isInstanceOf(PromotionProductException.class);
    }


    @Test
    void 결제중_프로모션_누락이지만_재고까지_부족_예외_테스트() {
        PayRequest request = new PayRequest(true, null, true);

        assertThatThrownBy(() -> checker.checkFlow(request, Map.of(JUICE.id(), 9)))
                .isInstanceOf(PromotionProductException.class);
    }

    @Test
    void 결제중_프로모션_누락과_재고_부족_동시_예외_테스트() {
        cartSetup();
        Map<Long, Integer> items = cart.getItems();
        PayRequest request = new PayRequest(null, null, true);

        assertThatThrownBy(() -> checker.checkFlow(request, items))
                .isInstanceOf(PromotionProductException.class);
    }

    @Test
    void 결제중_멤버십_미선택_예외_테스트() {
        cartSetup();
        Map<Long, Integer> items = cart.getItems();
        PayRequest request = new PayRequest(true, true, null);

        assertThatThrownBy(() -> checker.checkFlow(request, items))
                .isInstanceOf(MembershipException.class);
    }

    @Test
    void 결제중_사용자_선택_모두_완료_테스트() {
        cartSetup();
        Map<Long, Integer> items = cart.getItems();
        PayRequest request = new PayRequest(true, true, true);

        assertThatCode(() -> checker.checkFlow(request, items))
                .doesNotThrowAnyException();
    }
}
