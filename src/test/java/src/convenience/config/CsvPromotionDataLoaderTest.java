package src.convenience.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import src.convenience.domain.entity.promotion.Promotion;
import src.convenience.domain.entity.promotion.PromotionRepository;

@SpringBootTest
public class CsvPromotionDataLoaderTest {

    @Autowired
    PromotionRepository promotionRepository;

    private void assertPromotion(String name, String startDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse("2025-12-31");

        Promotion promotion = promotionRepository.findByName(name)
                .orElseThrow();

        assertThat(promotion.getPromotionStartDate()).isEqualTo(start);
        assertThat(promotion.getPromotionEndDate()).isEqualTo(end);
    }

    @Test
    void 기본_프로모션_데이터_테스트() {
        assertPromotion("2+1", "2025-01-01");
    }

    @Test
    void 한정_프로모션_데이터_테스트() {
        assertPromotion("한정 1+1", "2025-11-01");
    }
}
