package src.convenience.domain.entity.promotion;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByName(String name);

    default Promotion promotionCreate(String name) {
        if (Promotion.checkNonePromotion(name)) {
            return Promotion.none(); // NONE 전용 Promotion 객체 반환 (직접 만들기)
        }

        return findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 없는 상품입니다"));
    }
}
