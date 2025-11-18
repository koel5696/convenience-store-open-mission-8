package src.convenience.domain.entity.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "promotion")
public class Promotion {
    private static final String NONE_PROMOTION = "null";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "buy")
    private int buy;

    @Column(name = "gift")
    private int gift;

    @Column(name = "promotionStartDate")
    private LocalDate promotionStartDate;

    @Column(name = "promotionEndDate")
    private LocalDate promotionEndDate;

    protected Promotion() {
    }

    public Promotion(String name, int buy, int gift,
                     LocalDate promotionStartDate, LocalDate promotionEndDate) {
        this.name = name;
        this.buy = buy;
        this.gift = gift;
        this.promotionStartDate = promotionStartDate;
        this.promotionEndDate = promotionEndDate;
    }

    public static Promotion none() {
        return new Promotion(NONE_PROMOTION, 0, 0,
                LocalDate.MIN, LocalDate.MAX);
    }

    public static boolean checkNonePromotion(String promotion) {
        return promotion.equals(NONE_PROMOTION);
    }

    public boolean isMissingPromotion(int quantity) {
        if(checkNonePromotion()) {
            return false;
        }
        int total = buy + gift;
        if(quantity == buy)
            return true;
        return quantity % total == buy;
    }

    public PromotionResult applyPromotion(int quantity) {
        if(!checkNonePromotion() && checkPromotionDate()) {
            return calculatePromotion(quantity); // 일단 존재하고 프로모션 날짜일 때
        }
        return new PromotionResult(quantity, 0);
    }

    public LocalDate getPromotionStartDate() {
        return promotionStartDate;
    }

    public LocalDate getPromotionEndDate() {
        return promotionEndDate;
    }

    private boolean checkPromotionDate() {
        LocalDate nowDate = LocalDate.now();
        return !nowDate.isBefore(promotionStartDate) && !nowDate.isAfter(promotionEndDate);
    }

    private boolean checkNonePromotion() {
        return name.equals(NONE_PROMOTION);
    }

    private PromotionResult calculatePromotion(int quantity) {
        int total = buy + gift;
        int giftQuantity = quantity / total;
        int paidQuantity = quantity - giftQuantity;
        return new PromotionResult(paidQuantity, giftQuantity);
    }
}
