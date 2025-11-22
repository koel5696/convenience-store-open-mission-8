package src.convenience.service.paymentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static src.convenience.productEnum.TestProductId.CIDER;
import static src.convenience.productEnum.TestProductId.COKE;
import static src.convenience.productEnum.TestProductId.ENERGY_BAR;
import static src.convenience.productEnum.TestProductId.JUICE;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.cart.Cart;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;
import src.convenience.exception.PromotionProductException;

@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    Cart cart;

    PayRequest request = new PayRequest(false, false, true);

    @BeforeEach
    void init() {
        cart.clear();
    }

    private void cartSetup() {
        cart.addItem(COKE.id(), 6);
        cart.addItem(CIDER.id(), 2);
        cart.addItem(ENERGY_BAR.id(), 16);
    }

    @Test
    void 정상_결제_테스트() {
        cartSetup();
        ReceiptResponse receipt = paymentService.pay(request);

        assertThat(receipt).isNotNull();

        List<ReceiptItem> items = receipt.items();
        ReceiptItem item = items.get(0);

        assertThat(item.productName()).isEqualTo("콜라");
        assertThat(item.paidQuantity()).isEqualTo(4);
        assertThat(item.giftQuantity()).isEqualTo(2);
    }

    @Test
    void 정상_결제_후_장바구니_비움_테스트() {
        cartSetup();

        paymentService.pay(request);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
        //재고 감소는 pay()의 핵심 비즈니스 로직의 결과이며, decreaseStock() 메서드를 테스트하는 것이 아님.
    void 정상_결제_후_재고_차감_테스트() {
        cartSetup();
        ReceiptResponse receipt = paymentService.pay(request);
        List<ReceiptItem> items = receipt.items();

        items.forEach(item -> {
            Product updated = productRepository.getByNameOrThrow(item.productName());
            assertThat(updated.getQuantity()).isEqualTo(14); // 셋업된 카트의 3가지 상품 모두 구매하면 각 14개씩 남는다.
        });
    }

    @Test
    void 결제중_프로모션_누락이지만_사용자_받지_않음_선택_테스트() {
        cart.addItem(COKE.id(), 5);
        PayRequest request = new PayRequest(false, true, true);

        ReceiptResponse receipt = paymentService.pay(request);

        List<ReceiptItem> items = receipt.items();
        ReceiptItem receiptItem = items.get(0);

        assertThat(receiptItem.paidQuantity()).isEqualTo(4);
        assertThat(receiptItem.giftQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("프로모션 누락 시 프론트가 수행하는 증정품 보정 흐름을 테스트에서 직접 시뮬레이션한다.")
    void 결제중_프로모션_누락_증정품_사용자_받음_선택_테스트() {
        // 기본 장바구니 상태
        cart.addItem(COKE.id(), 5);

        // 사용자가 '증정품 받음'을 선택하는 상황 가정
        PayRequest request = new PayRequest(true, true, true);

        // 프론트에서 증정품 1개 추가하는 흐름을 테스트에서 직접 시뮬레이션
        cart.addItem(COKE.id(), 1);

        ReceiptResponse receipt = paymentService.pay(request);

        ReceiptItem item = receipt.items().get(0);

        assertThat(item.paidQuantity()).isEqualTo(4);
        assertThat(item.giftQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("재고 부족으로 증정 불가한 상품을 사용자가 받지 않고 그대로 결제한다.")
    void 결제중_재고_부족_받지_않음_테스트() {
        cart.addItem(JUICE.id(), 9);
        PayRequest request = new PayRequest(null, true, true);

        ReceiptResponse receipt = paymentService.pay(request);

        List<ReceiptItem> items = receipt.items();
        ReceiptItem receiptItem = items.get(0);

        assertThat(receiptItem.paidQuantity()).isEqualTo(5);
        assertThat(receiptItem.giftQuantity()).isEqualTo(4);
    }

    @Test
    @DisplayName("재고 부족 프로모션 발생 시 결제가 중단되고 전체 트랜잭션이 롤백된다.")
    void 결제중_재고_부족_결제_취소_테스트() {

        //재고 부족을 유발하는 장바구니(1+1 제품 9개 담기)
        cart.addItem(JUICE.id(), 9);

        // 제품 원래 재고 확인
        Product before = productRepository.getByIdOrThrow(JUICE.id());
        int beforeStock = before.getQuantity();

        // 사용자에게 재고 부족으로 증정품 증정 불가 메시지 예외 전달
        PayRequest request1 = new PayRequest(null, null, true);
        assertThatThrownBy(() -> paymentService.pay(request1))
                .isInstanceOf(PromotionProductException.class);

        // 사용자는 새롭게 결제 취소(false)를 날림. 프론트에서 pay()가 재실행되지 않음!!
        new PayRequest(null, false, true);

        // 1) 장바구니는 결제 실패로 인해 clear() 되지 않아야 한다
        assertThat(cart.getItems()).isNotEmpty();

        // 2) 재고는 절대 줄어들지 않아야 한다
        Product after = productRepository.getByIdOrThrow(JUICE.id());
        assertThat(after.getQuantity()).isEqualTo(beforeStock);
    }
}
