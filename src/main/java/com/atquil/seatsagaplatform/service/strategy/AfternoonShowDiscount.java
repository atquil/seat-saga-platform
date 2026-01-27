package com.atquil.seatsagaplatform.service.strategy;

import com.atquil.seatsagaplatform.configure.strategy.discount.DiscountStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author atquil
 */
@Component
public class AfternoonShowDiscount implements DiscountStrategy {

    @Value("${discount.afternoon-show.enabled:true}")
    private boolean enabled;

    @Value("${discount.afternoon-show.percentage:20}")
    private int percentage;

    @Value("${discount.afternoon-show.start-hour:12}")
    private int startHour;

    @Value("${discount.afternoon-show.end-hour:17}")
    private int endHour;

    @Override
    public BigDecimal calculateDiscount(LocalDateTime showTime, List<BigDecimal> seatPrices) {
        if (!enabled) return BigDecimal.ZERO;
        if (seatPrices.isEmpty()) return BigDecimal.ZERO;

        int hour = showTime.getHour();
        boolean isAfternoon = hour >= startHour && hour < endHour;

        if (!isAfternoon) return BigDecimal.ZERO;

        // Calculate total
        BigDecimal total = seatPrices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate percentage discount
        BigDecimal discountMultiplier = BigDecimal.valueOf(percentage)
                .divide(BigDecimal.valueOf(100));

        return total.multiply(discountMultiplier);
    }

    @Override
    public String getName() {
        return String.format("%d%% Afternoon Show (%d-%d PM)",
                percentage, startHour, endHour);
    }
}