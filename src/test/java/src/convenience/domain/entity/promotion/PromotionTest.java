package src.convenience.domain.entity.promotion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;


public class PromotionTest {
    LocalDate start;
    LocalDate end;

    @Test
    void 프로모션_없음_테스트() {
        Promotion none = Promotion.none();
        assertThat(none.applyPromotion(10).paidQuantity()).isEqualTo(10);
        assertThat(none.applyPromotion(10).giftQuantity()).isEqualTo(0);
    }

    @Test
    void 프로모션_놓침_테스트() {
        start = LocalDate.now().plusDays(1);
        end = LocalDate.now().plusDays(1);

        Promotion onePlusOne = new Promotion("1+1", 1, 1, start, end);
        assertThat(onePlusOne.isMissingPromotion(1)).isTrue();  // 1개만 담은 경우(1+1)
        assertThat(onePlusOne.isMissingPromotion(2)).isFalse(); // 2개면 완성
        assertThat(onePlusOne.isMissingPromotion(3)).isTrue();  // 3개 → (1개 부족)

        Promotion twoPlusOne = new Promotion("2+1", 2, 1, start, end);
        assertThat(twoPlusOne.isMissingPromotion(2)).isTrue();  // 2개만 담은 경우(2+1)
        assertThat(twoPlusOne.isMissingPromotion(3)).isFalse(); // 3개면 완성
        assertThat(twoPlusOne.isMissingPromotion(5)).isTrue();  // 5개 → (1개 부족)
    }

    @Test
    void 프로모션_적용_테스트() {
        start = LocalDate.now().minusDays(1);
        end = LocalDate.now().plusDays(1);

        Promotion onePlusOne = new Promotion("1+1", 1, 1, start, end);
        assertThat(onePlusOne.applyPromotion(10).paidQuantity()).isEqualTo(5);
        assertThat(onePlusOne.applyPromotion(10).giftQuantity()).isEqualTo(5);

        Promotion twoPlusOne = new Promotion("2+1", 2, 1, start, end);
        assertThat(twoPlusOne.applyPromotion(10).paidQuantity()).isEqualTo(7);
        assertThat(twoPlusOne.applyPromotion(10).giftQuantity()).isEqualTo(3);
    }

    @Test
    void 프로모션_날짜_안맞으면_미적용_테스트() {
        start = LocalDate.now().minusDays(10);
        end = LocalDate.now().minusDays(5);

        Promotion onePlusOne = new Promotion("1+1", 1, 1, start, end);

        assertThat(onePlusOne.applyPromotion(10).paidQuantity()).isEqualTo(10);
        assertThat(onePlusOne.applyPromotion(10).giftQuantity()).isEqualTo(0);
    }


}
