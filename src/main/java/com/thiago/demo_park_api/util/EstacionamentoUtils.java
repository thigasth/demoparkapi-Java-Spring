package com.thiago.demo_park_api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoUtils {

    // 2025-04-23 19T45:23:48.616463500
    // 20250423-192121

    private static final double PRIMEIROS_15_MINUTES = 5.00;
    private static final double PRIMEIROS_60_MINUTES = 9.25;
    private static final double ADICIONAL_15_MINUTES = 1.75;
    private static final double DESCONTO_PERCENTUAL = 0.30;

    public static String gerarRecibo() {
        LocalDateTime date = LocalDateTime.now();
        String recibo = date.toString().substring( 0,19);
        return recibo.replace("-", "")
                .replace(":", "")
                .replace("T", "-");
    }

    public static BigDecimal calcularCusto(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {

            total = PRIMEIROS_15_MINUTES;

        } else if (minutes <= 60) {

            total = PRIMEIROS_60_MINUTES;

        } else {

            long minutosExtras = minutes - 60;
            long faixaDe15 = (long) Math.ceil(minutosExtras / 15.0);
            total = PRIMEIROS_60_MINUTES + (faixaDe15 * ADICIONAL_15_MINUTES);

        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calcularDesconto(BigDecimal custo, long numeroDeVezes) {

        BigDecimal desconto = BigDecimal.ZERO;

        if (numeroDeVezes > 0 && numeroDeVezes % 10 == 0) {
            desconto = custo.multiply(BigDecimal.valueOf(DESCONTO_PERCENTUAL));
        }

        if (desconto == null) {
            desconto = BigDecimal.ZERO;
        }

        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }
}

