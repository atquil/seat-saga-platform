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
public class ThirdTicketDiscount implements DiscountStrategy {

    @Value("${discount.third-ticket.enabled:true}")
    private boolean enabled;

    @Value("${discount.third-ticket.percentage:50}")
    private int percentage;

    @Value("${discount.third-ticket.min-tickets:3}")
    private int minTickets;

    @Override
    public BigDecimal calculateDiscount(LocalDateTime showTime, List<BigDecimal> seatPrices) {
        if (!enabled) return BigDecimal.ZERO;
        if (seatPrices.size() < minTickets) return BigDecimal.ZERO;

        // Sort prices: highest first
        List<BigDecimal> sortedPrices = seatPrices.stream()
                .sorted((a, b) -> b.compareTo(a))
                .toList();

        // Get the nth ticket for discount (minTickets-th ticket)
        BigDecimal discountableTicketPrice = sortedPrices.get(minTickets - 1);

        // Calculate percentage discount
        BigDecimal discountMultiplier = BigDecimal.valueOf(percentage)
                .divide(BigDecimal.valueOf(100));

        return discountableTicketPrice.multiply(discountMultiplier);
    }

    @Override
    public String getName() {
        return String.format("%d%% on %drd Ticket", percentage, minTickets);
    }
}

