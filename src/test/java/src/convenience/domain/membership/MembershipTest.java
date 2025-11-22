package src.convenience.domain.membership;

import static org.assertj.core.api.Assertions.assertThat;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.NONE_PROMOTION;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.TWO_PLUS_ONE;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;

public class MembershipTest {
    Membership membership = new Membership();

    private ReceiptItem item(String name, String promotion,
                             int price, int paidQuantity, int giftQuantity, int nonePromotionQuantity) {
        return new ReceiptItem(name, promotion, price, paidQuantity, giftQuantity, nonePromotionQuantity);
    }

    private PayRequest request(Boolean membership) {
        return new PayRequest(false, false, membership);
    }


    @Test
    void 멤버십_적용_10퍼센트_할인_테스트() {
        List<ReceiptItem> items = List.of(
                item("물", NONE_PROMOTION.getPromotionType(), 700, 5, 0, 5));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(350);
    }

    @Test
    void 멤버십_미적용_할인_없음_테스트() {
        List<ReceiptItem> items = List.of(
                item("물", NONE_PROMOTION.getPromotionType(), 700, 5, 0, 5));

        int discount = membership.discount(request(false), items);

        assertThat(discount).isEqualTo(0);
    }

    @Test
    void 멤버십을_적용해도_프로모션_상품이면_할인_미적용_테스트() {
        List<ReceiptItem> items = List.of(
                item("콜라", TWO_PLUS_ONE.getPromotionType(), 1700, 4, 2, 0));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(0);
    }

    @Test
    void 프로모션_상품이어도_증정이_없으면_멤버십_적용_테스트() {
        List<ReceiptItem> items = List.of(
                item("콜라", TWO_PLUS_ONE.getPromotionType(), 1700, 2, 0, 2));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(340);
    }

    @Test
    @DisplayName("만약 콜라 7개 구매시 2+1 정책으로 6개만 적용되며 나머지 1개는 적용대상이 아니다.")
    void 프로모션_상품이어도_증정_단위와_맞지_않게_구매하면_멤버십_적용_테스트() {
        List<ReceiptItem> items = List.of(
                item("콜라", TWO_PLUS_ONE.getPromotionType(), 1700, 5, 2, 1));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(170);
    }


    @Test
    void 할인금액_한도_최대_10000원_테스트() {
        List<ReceiptItem> items = List.of(
                item("정식도시락", NONE_PROMOTION.getPromotionType(), 5500, 8, 0, 8),
                item("삼각김밥", NONE_PROMOTION.getPromotionType(), 1500, 10, 0, 10),
                item("에너지바", NONE_PROMOTION.getPromotionType(), 1500, 30, 0, 30));

        int discount = membership.discount(request(true), items);

        assertThat(discount).isEqualTo(10000);
    }

}
