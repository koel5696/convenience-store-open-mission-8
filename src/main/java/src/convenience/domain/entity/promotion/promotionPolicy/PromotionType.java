package src.convenience.domain.entity.promotion.promotionPolicy;

public enum PromotionType {
    NONE_PROMOTION("null"),
    ONE_PLUS_ONE("1+1"),
    TWO_PLUS_ONE("2+1"),
    LIMITED_ONE_PLUS_ONE("한정 1+1");

    private final String promotionType;

    PromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public String getPromotionType() {
        return promotionType;
    }

}
