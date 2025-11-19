package src.convenience.dto.product;

import java.time.LocalDate;

public record ProductPromotionDate(
        LocalDate start,
        LocalDate end
) {
}
