package src.convenience.dto.cart;

public record CartRequest(
        Long productId,
        int quantity
) {
}
