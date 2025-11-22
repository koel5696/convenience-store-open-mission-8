package src.convenience.domain.entity.promotion;

import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.LIMITED_ONE_PLUS_ONE;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.NONE_PROMOTION;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.ONE_PLUS_ONE;
import static src.convenience.domain.entity.promotion.promotionPolicy.PromotionType.TWO_PLUS_ONE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import src.convenience.domain.entity.promotion.promotionPolicy.OnePlusOnePolicy;
import src.convenience.domain.entity.promotion.promotionPolicy.PromotionPolicy;
import src.convenience.domain.entity.promotion.promotionPolicy.TwoPlusOnePolicy;
import src.convenience.dto.payment.PayRequest;

@Entity
@Table(name = "promotion")
public class Promotion {

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
        return new Promotion(NONE_PROMOTION.getPromotionType(), 0, 0,
                LocalDate.MIN, LocalDate.MAX);
    }

    public static boolean checkNonePromotion(String promotion) {
        return promotion.equals(NONE_PROMOTION.getPromotionType());
    }

    public boolean isMissingPromotion(int quantity) {
        if (checkNonePromotion()) {
            return false;
        }
        int total = buy + gift;
        if (quantity == buy) {
            return true;
        }
        return quantity % total == buy;
    }

    public PromotionResult applyPromotion(PayRequest request, int quantity) {
        if (!checkNonePromotion() && checkPromotionDate()) {
            return calculatePromotion(request, quantity);
        }
        return new PromotionResult(quantity, 0, quantity);
    }

    public LocalDate getPromotionStartDate() {
        return promotionStartDate;
    }

    public LocalDate getPromotionEndDate() {
        return promotionEndDate;
    }

    public boolean checkNonePromotion() {
        return name.equals(NONE_PROMOTION.getPromotionType());
    }

    private boolean checkPromotionDate() {
        LocalDate nowDate = LocalDate.now();

        return !nowDate.isBefore(promotionStartDate) && !nowDate.isAfter(promotionEndDate);
    }

    private PromotionResult calculatePromotion(PayRequest request, int quantity) {
        int total = buy + gift;
        int giftQuantity = quantity / total;
        int paidQuantity = quantity - giftQuantity;

        PromotionPolicy promotionPolicy = selectPolicy();
        int nonePromotionQuantity = promotionPolicy.noPromotionQuantity(request, quantity);

        return new PromotionResult(paidQuantity, giftQuantity, nonePromotionQuantity);
    }

    private PromotionPolicy selectPolicy() {
        if (name.equals(ONE_PLUS_ONE.getPromotionType()) || name.equals(LIMITED_ONE_PLUS_ONE.getPromotionType())) {
            return new OnePlusOnePolicy();
        }
        if (name.equals(TWO_PLUS_ONE.getPromotionType())) {
            return new TwoPlusOnePolicy();
        }
        throw new IllegalStateException("알수 없는 프로모션: " + name);
    }
}
