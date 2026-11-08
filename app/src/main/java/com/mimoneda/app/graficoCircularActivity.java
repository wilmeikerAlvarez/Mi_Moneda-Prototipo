package com.mimoneda.app;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;

import java.util.Locale;

public class graficoCircularActivity extends View {

    // Porcentajes de las seis categorías
    private final float[] porcentajes = {
            10f, 40f, 15f, 25f, 5f, 5f
    };

    // Colores del diseño original
    private final int[] colores = {
            Color.rgb(131, 206, 255), // Azul
            Color.rgb(124, 222, 160), // Verde
            Color.rgb(255, 220, 105), // Amarillo
            Color.rgb(255, 172, 164), // Salmón
            Color.rgb(255, 203, 133), // Naranja
            Color.rgb(201, 169, 246)  // Morado
    };

    private final Paint pintura = new Paint(
            Paint.ANTI_ALIAS_FLAG
    );

    private final RectF rectangulo = new RectF();

    private double total = 425.00;

    public graficoCircularActivity(Context context) {
        super(context);
        setTotal(total);
    }

    // Actualizar importe total
    public void setTotal(double nuevoTotal) {
        total = nuevoTotal;

        setContentDescription(
                String.format(
                        Locale.US,
                        "Gráfico de gastos. Total %.2f euros",
                        total
                )
        );

        invalidate();
    }

    // Convertir sp a píxeles
    private float sp(float valor) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                valor,
                getResources().getDisplayMetrics()
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float ancho = getWidth();
        float alto = getHeight();

        // Centro del gráfico
        float centroX = ancho / 2f;
        float centroY = alto / 2f;

        // Radio adaptable a la pantalla
        float radio = Math.min(ancho, alto) / 2f - sp(6);

        if (radio <= 0) {
            return;
        }

        // Rectángulo que contiene el círculo
        rectangulo.set(
                centroX - radio,
                centroY - radio,
                centroX + radio,
                centroY + radio
        );

        // Dibujar los sectores de colores
        float anguloInicial = -90f;

        pintura.setStyle(Paint.Style.FILL);

        for (int i = 0; i < porcentajes.length; i++) {

            float angulo = porcentajes[i] * 360f / 100f;

            pintura.setColor(colores[i]);

            canvas.drawArc(
                    rectangulo,
                    anguloInicial,
                    angulo,
                    true,
                    pintura
            );

            anguloInicial += angulo;
        }

        // Dibujar el círculo blanco interior
        pintura.setColor(Color.WHITE);

        float radioInterior = radio * 0.50f;

        canvas.drawCircle(
                centroX,
                centroY,
                radioInterior,
                pintura
        );

        // Dibujar los porcentajes
        anguloInicial = -90f;

        float radioTexto = radio * 0.78f;

        pintura.setColor(Color.BLACK);
        pintura.setTextAlign(Paint.Align.CENTER);
        pintura.setTypeface(
                android.graphics.Typeface.DEFAULT
        );

        pintura.setTextSize(
                Math.min(sp(12), radio * 0.12f)
        );

        for (int i = 0; i < porcentajes.length; i++) {

            float angulo = porcentajes[i] * 3.6f;

            float anguloCentral = anguloInicial + angulo / 2f;

            double radianes = Math.toRadians(
                    anguloCentral
            );

            float x = centroX + radioTexto *
                    (float) Math.cos(radianes);

            float y = centroY + radioTexto *
                    (float) Math.sin(radianes);

            String texto = String.format(
                    Locale.US,
                    "%.0f%%",
                    porcentajes[i]
            );

            // Centrar verticalmente el porcentaje
            Paint.FontMetrics fm = pintura.getFontMetrics();

            float ajuste = -(fm.ascent + fm.descent) / 2f;

            canvas.drawText(
                    texto,
                    x,
                    y + ajuste,
                    pintura
            );

            anguloInicial += angulo;
        }

        // Escribir el importe total
        pintura.setColor(Color.RED);

        pintura.setTypeface(
                android.graphics.Typeface.create(
                        "sans-serif",
                        android.graphics.Typeface.BOLD
                )
        );

        pintura.setTextSize(
                Math.min(sp(22), radio * 0.22f)
        );

        pintura.setTextAlign(Paint.Align.CENTER);

        String textoTotal = String.format(
                Locale.US,
                "%.2f€",
                total
        );

        canvas.drawText(
                textoTotal,
                centroX,
                centroY,
                pintura
        );

        // Escribir "Total" debajo
        pintura.setColor(Color.DKGRAY);

        pintura.setTypeface(
                android.graphics.Typeface.DEFAULT
        );

        pintura.setTextSize(
                Math.min(sp(13), radio * 0.13f)
        );

        canvas.drawText(
                "Total",
                centroX,
                centroY + radio * 0.18f,
                pintura
        );
    }
}

