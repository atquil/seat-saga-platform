package com.atquil.seatsagaplatform.configure.strategy.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author atquil
 */
public interface DiscountStrategy {
    BigDecimal calculateDiscount(LocalDateTime showTime, List<BigDecimal> seatPrices);
    String getName();
}