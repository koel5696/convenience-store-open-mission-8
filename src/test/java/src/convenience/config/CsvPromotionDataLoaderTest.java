package src.convenience.config;

import static org.assertj.core.api.Assertions.assertThat;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.LIMITED_ONE_PLUS_ONE;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.TWO_PLUS_ONE;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import src.convenience.domain.entity.promotion.Promotion;
import src.convenience.domain.entity.promotion.PromotionRepository;

@SpringBootTest
public class CsvPromotionDataLoaderTest {
    private static final LocalDate START_DATE = LocalDate.of(2025, 1, 1);
    private static final LocalDate LIMITED_START_DATE = LocalDate.of(2025, 11, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 12, 31);

    @Autowired
    PromotionRepository promotionRepository;

    private void assertPromotion(String name, String startDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(END_DATE.toString());

        Promotion promotion = promotionRepository.findByName(name)
                .orElseThrow();

        assertThat(promotion.getPromotionStartDate()).isEqualTo(start);
        assertThat(promotion.getPromotionEndDate()).isEqualTo(end);
    }

    @Test
    void 기본_프로모션_데이터_테스트() {
        assertPromotion(TWO_PLUS_ONE.getPromotionType(), START_DATE.toString());
    }

    @Test
    void 한정_프로모션_데이터_테스트() {
        assertPromotion(LIMITED_ONE_PLUS_ONE.getPromotionType(), LIMITED_START_DATE.toString());
    }
}
