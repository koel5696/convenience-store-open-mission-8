package src.convenience.dto.payment;

public record PayRequest(
        Boolean missingPromotion,
        Boolean insufficientStock,
        Boolean membership
) {}
