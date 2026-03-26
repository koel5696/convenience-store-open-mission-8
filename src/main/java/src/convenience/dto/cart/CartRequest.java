package src.convenience.dto.cart;

import org.antlr.v4.runtime.misc.NotNull;

public record CartRequest(
        @NotNull Long productId,
        int quantity
) {
    /*public CartRequest {
        if (quantity == null || quantity.scale() > 0
                || quantity.compareTo(BigDecimal.ONE) < 0 ||
                quantity.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new BusinessException(ErrorCode.INTEGER);
        }
    }*/
}

