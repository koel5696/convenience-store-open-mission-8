package src.convenience.domain.entity.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import src.convenience.exception.BusinessException;
import src.convenience.exception.constants.ErrorCode;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    default Product getByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NON_EXIST_PRODUCT));
    }

    default Product getByNameOrThrow(String name) {
        return findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.NON_EXIST_PRODUCT));
    }
}
