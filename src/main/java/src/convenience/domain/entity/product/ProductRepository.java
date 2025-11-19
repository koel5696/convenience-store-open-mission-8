package src.convenience.domain.entity.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    default Product getByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + id));
    }

    default Product getByNameOrThrow(String name) {
        return findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + name));
    }
}
