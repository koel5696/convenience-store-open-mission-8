package src.convenience.domain.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    //추후에 게터대신 여기서 직접 select해서 db로 부터 게터 없이 가져오는 것도 고려. gpt와의 대화 아카이브

    default Product getByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + id));
    }
}
