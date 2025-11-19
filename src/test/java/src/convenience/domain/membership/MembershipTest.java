package src.convenience.domain.membership;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;

public class MembershipTest {
    Membership membership = new Membership();

    private ReceiptItem item(String name, String promotion,
                             int price, int paidQuantity, int giftQuantity) {
        return new ReceiptItem(name, promotion, price, paidQuantity, giftQuantity);
    }

    private PayRequest request(Boolean membership) {
        return new PayRequest(false, false, membership);
    }


    @Test
    void 멤버십_적용_10퍼센트_할인_테스트() {
        List<ReceiptItem> items = List.of(
                item("물", "null",700, 5,  0));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(350);
    }

    @Test
    void 멤버십_미적용_할인_없음_테스트() {
        List<ReceiptItem> items = List.of(
                item("물", "null",700, 5,  0));

        int discount = membership.discount(request(false), items);

        assertThat(discount).isEqualTo(0);
    }

    @Test
    void 멤버십을_적용해도_프로모션_상품이면_할인_미적용_테스트() {
        List<ReceiptItem> items = List.of(
                item("콜라", "2+1",1700, 5,  2));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(0);
    }


    @Test
    void 할인금액_한도_최대_8000원_테스트() {
        List<ReceiptItem> items = List.of(
                item("정식도시락", "null",5500, 8,  0),
                item("삼각김밥", "null",1500, 10,  0),
                item("에너지바", "null",1500, 20,  0));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(8000);
    }

}
