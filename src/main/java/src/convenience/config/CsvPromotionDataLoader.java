package src.convenience.config;

import java.io.BufferedReader;
import java.io.InputStream;
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

    private static final String CSV_PATH = "/promotions.csv";
    private static final String CSV_DELIMITER = ",";

    private final PromotionRepository promotionRepository;

    public CsvPromotionDataLoader(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (promotionRepository.count() > 0) {
            return;
        }

        try (BufferedReader reader = createReader()) {
            List<Promotion> promotions = reader.lines()
                    .skip(1) // 헤더 스킵
                    .map(this::toPromotion)
                    .toList();

            promotionRepository.saveAll(promotions);
        }
    }

    private BufferedReader createReader() {
        InputStream inputStream = getClass().getResourceAsStream(CSV_PATH);
        if (inputStream == null) {
            throw new IllegalStateException("CSV 파일을 찾을 수 없습니다. path=" + CSV_PATH);
        }

        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private Promotion toPromotion(String line) {
        String[] tokens = line.split(CSV_DELIMITER);

        String name = tokens[0];
        int buy = Integer.parseInt(tokens[1]);
        int gift = Integer.parseInt(tokens[2]);
        LocalDate startDate = LocalDate.parse(tokens[3]);
        LocalDate endDate = LocalDate.parse(tokens[4]);

        return new Promotion(name, buy, gift, startDate, endDate);
    }
}
