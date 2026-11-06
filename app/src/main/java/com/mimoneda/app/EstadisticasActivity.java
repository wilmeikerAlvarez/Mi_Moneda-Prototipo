package com.mimoneda.app;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import android.widget.FrameLayout;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class EstadisticasActivity extends AppCompatActivity {

    // Botones de filtros
    private TextView btnSemana, btnMes, btnAnio;

    // Categorías de la tabla
    private TextView txtVivienda, txtCoche;
    private TextView txtSupermercado, txtCompras;
    private TextView txtRegalos, txtRestaurantes;

    private graficoCircularActivity graficoCircular;
    private FrameLayout contenedorGrafico;

    // Datos de ejemplo
    private final double[] porcentajes = {
            25, 15, 40, 5, 10, 5
    };

    private int periodoSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_estadisticas);

// Buscar el contenedor del gráfico
        contenedorGrafico = findViewById(
                R.id.contenedorGrafico
        );

// Crear el gráfico circular
      graficoCircular = new graficoCircularActivity(this);

// Añadirlo al contenedor
        contenedorGrafico.addView(
                graficoCircular,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );


        // Conectar los botones
        btnSemana = findViewById(R.id.btnSemana);
        btnMes = findViewById(R.id.btnMes);
        btnAnio = findViewById(R.id.btnAnio);

        // Conectar las categorías
        txtVivienda = findViewById(R.id.txtVivienda);
        txtCoche = findViewById(R.id.txtCoche);
        txtSupermercado = findViewById(R.id.txtSupermercado);
        txtCompras = findViewById(R.id.txtCompras);
        txtRegalos = findViewById(R.id.txtRegalos);
        txtRestaurantes = findViewById(R.id.txtRestaurantes);

        // Estado inicial: SEMANA
        seleccionarPeriodo(0);

        // Eventos de los botones
        btnSemana.setOnClickListener(v -> seleccionarPeriodo(0));

        btnMes.setOnClickListener(v -> seleccionarPeriodo(1));

        btnAnio.setOnClickListener(v -> seleccionarPeriodo(2));
    }

    // Cambiar el periodo seleccionado
    private void seleccionarPeriodo(int periodo) {

        periodoSeleccionado = periodo;

        TextView[] botones = {
                btnSemana, btnMes, btnAnio
        };

        for (int i = 0; i < botones.length; i++) {

            if (i == periodo) {

                botones[i].setBackgroundResource(
                        R.drawable.btn_pulsado
                );

                botones[i].setTextColor(Color.WHITE);

            } else {

                botones[i].setBackgroundResource(
                        R.drawable.btn_noPulsado
                );

                botones[i].setTextColor(Color.BLACK);
            }
        }

        // Actualizar los datos de la tabla
        actualizarEstadisticas(periodo);
    }

    // Actualizar las cantidades según el periodo
    private void actualizarEstadisticas(int periodo) {

        double total;

        switch (periodo) {

            case 0:
                // SEMANA
                total = 425.00;
                break;

            case 1:
                // MES
                total = 1350.00;
                break;

            case 2:
                // AÑO
                total = 1600.00;
                break;

            default:
                total = 425.00;
                break;
        }

// Actualizar el centro del gráfico
        graficoCircular.setTotal(total);


        // Calcular los importes por categoría
        double vivienda = total * porcentajes[0] / 100;
        double coche = total * porcentajes[1] / 100;
        double supermercado = total * porcentajes[2] / 100;
        double compras = total * porcentajes[3] / 100;
        double regalos = total * porcentajes[4] / 100;
        double restaurantes = total * porcentajes[5] / 100;

        // Mostrar los resultados
        txtVivienda.setText(
                "⌂ " + formatear(vivienda) + "   25%"
        );

        txtCoche.setText(
                "🚗 " + formatear(coche) + "   15%"
        );

        txtSupermercado.setText(
                "▣ " + formatear(supermercado) + "   40%"
        );

        txtCompras.setText(
                "🛍 " + formatear(compras) + "   5%"
        );

        txtRegalos.setText(
                "♙ " + formatear(regalos) + "   10%"
        );

        txtRestaurantes.setText(
                "♨ " + formatear(restaurantes) + "   5%"
        );
    }

    // Formatear cantidades con dos decimales
    private String formatear(double cantidad) {

        return String.format(
                Locale.US,
                "%.2f€",
                cantidad
        );
    }
}