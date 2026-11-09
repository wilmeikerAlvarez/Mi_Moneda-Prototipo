package com.mimoneda.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class ImporteUtils {

    private ImporteUtils() {
    }

    public static long eurosACentimos(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El importe es obligatorio");
        }

        String normalizado = texto
                .trim()
                .replace("€", "")
                .replace(" ", "");

        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("El importe es obligatorio");
        }

        int ultimaComa = normalizado.lastIndexOf(',');
        int ultimoPunto = normalizado.lastIndexOf('.');

        if (ultimaComa > ultimoPunto) {
            normalizado = normalizado.replace(".", "").replace(',', '.');
        } else if (ultimoPunto > ultimaComa && ultimaComa >= 0) {
            normalizado = normalizado.replace(",", "");
        } else {
            normalizado = normalizado.replace(',', '.');
        }

        try {
            BigDecimal euros = new BigDecimal(normalizado)
                    .setScale(2, RoundingMode.HALF_UP);

            if (euros.signum() <= 0) {
                throw new IllegalArgumentException("El importe debe ser mayor que cero");
            }

            return euros.movePointRight(2).longValueExact();
        } catch (NumberFormatException | ArithmeticException excepcion) {
            throw new IllegalArgumentException("Introduce un importe válido", excepcion);
        }
    }

    public static String formatearCentimos(long centimos) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("es-ES")
        );
        return formato.format(BigDecimal.valueOf(centimos, 2));
    }
}
