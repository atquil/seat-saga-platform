package com.atquil.seatsagaplatform.service.strategy;

import com.atquil.seatsagaplatform.configure.strategy.discount.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {

    private final List<DiscountStrategy> discountStrategies;

    public BigDecimal calculateTotalDiscount(LocalDateTime showTime, List<BigDecimal> seatPrices) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (DiscountStrategy strategy : discountStrategies) {
            BigDecimal discount = strategy.calculateDiscount(showTime, seatPrices);
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(discount);
                log.info("Applied discount: {} = ${}", strategy.getName(), discount);
            }
        }

        return totalDiscount;
    }
}