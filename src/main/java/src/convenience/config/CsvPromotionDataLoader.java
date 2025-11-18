package src.convenience.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import src.convenience.domain.entity.promotion.Promotion;
import src.convenience.domain.entity.promotion.PromotionRepository;

@Component
public class CsvPromotionDataLoader implements ApplicationRunner {

    private final PromotionRepository promotionRepository;

    public CsvPromotionDataLoader(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (promotionRepository.count() > 0) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream("/promotions.csv")))) {
            List<Promotion> promotions = br.lines()
                    .skip(1)
                    .map(line -> line.split(","))
                    .map(tokens -> new Promotion(
                            tokens[0],
                            Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            LocalDate.parse(tokens[3]),
                            LocalDate.parse(tokens[4])
                    ))
                    .toList();

            promotionRepository.saveAll(promotions);
        }
    }
}


