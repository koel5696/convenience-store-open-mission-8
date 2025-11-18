package src.convenience.dto.payment;

public record PayRequest(
        Boolean missingPromotion,    // 담기 여부
        Boolean insufficientStock, // 재고 부족 시 진행 여부
        Boolean membership
) {}
