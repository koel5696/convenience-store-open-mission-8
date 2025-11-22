package src.convenience.domain.entity.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.ONE_PLUS_ONE;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.TWO_PLUS_ONE;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import src.convenience.dto.payment.PayRequest;


public class PromotionTest {
    PayRequest request = new PayRequest(true, true, null);
    LocalDate start;
    LocalDate end;

    @Test
    void 프로모션_없음_테스트() {
        Promotion none = Promotion.none();
        assertThat(none.applyPromotion(request, 10).paidQuantity()).isEqualTo(10);
        assertThat(none.applyPromotion(request, 10).giftQuantity()).isEqualTo(0);
    }

    @Test
    void 프로모션_놓침_테스트() {
        start = LocalDate.now().plusDays(1);
        end = LocalDate.now().plusDays(1);

        Promotion onePlusOne = new Promotion(ONE_PLUS_ONE.getPromotionType(), 1, 1, start, end);
        assertThat(onePlusOne.isMissingPromotion(1)).isTrue();  // 1개만 담은 경우(1+1)
        assertThat(onePlusOne.isMissingPromotion(2)).isFalse(); // 2개면 완성
        assertThat(onePlusOne.isMissingPromotion(3)).isTrue();  // 3개 → (1개 부족)

        Promotion twoPlusOne = new Promotion(TWO_PLUS_ONE.getPromotionType(), 2, 1, start, end);
        assertThat(twoPlusOne.isMissingPromotion(2)).isTrue();  // 2개만 담은 경우(2+1)
        assertThat(twoPlusOne.isMissingPromotion(3)).isFalse(); // 3개면 완성
        assertThat(twoPlusOne.isMissingPromotion(5)).isTrue();  // 5개 → (1개 부족)
    }

    @Test
    void 프로모션_적용_테스트() {
        start = LocalDate.now().minusDays(1);
        end = LocalDate.now().plusDays(1);

        Promotion onePlusOne = new Promotion(ONE_PLUS_ONE.getPromotionType(), 1, 1, start, end);
        assertThat(onePlusOne.applyPromotion(request, 10).paidQuantity()).isEqualTo(5);
        assertThat(onePlusOne.applyPromotion(request, 10).giftQuantity()).isEqualTo(5);

        Promotion twoPlusOne = new Promotion(TWO_PLUS_ONE.getPromotionType(), 2, 1, start, end);
        assertThat(twoPlusOne.applyPromotion(request, 10).paidQuantity()).isEqualTo(7);
        assertThat(twoPlusOne.applyPromotion(request, 10).giftQuantity()).isEqualTo(3);
    }

    @Test
    void 프로모션_날짜_안맞으면_미적용_테스트() {
        start = LocalDate.now().minusDays(10);
        end = LocalDate.now().minusDays(5);

        Promotion onePlusOne = new Promotion(ONE_PLUS_ONE.getPromotionType(), 1, 1, start, end);

        assertThat(onePlusOne.applyPromotion(request, 10).paidQuantity()).isEqualTo(10);
        assertThat(onePlusOne.applyPromotion(request, 10).giftQuantity()).isEqualTo(0);
    }


}
