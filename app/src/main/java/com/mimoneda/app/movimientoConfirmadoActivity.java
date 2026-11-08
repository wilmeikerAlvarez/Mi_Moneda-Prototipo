package com.mimoneda.app;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class movimientoConfirmadoActivity extends AppCompatActivity {

    // Elementos de la pantalla
    private TextView txtMovimientoGuardado;
    private TextView txtDescripcionConfirmacion;
    private TextView btnVolverInicio;
    private ImageView imgConfirmacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargar la pantalla de confirmación
        setContentView(R.layout.activity_movimiento_confirmado);

        // Conectar los elementos del XML con Java
        txtMovimientoGuardado = findViewById(
                R.id.txtMovimientoGuardado
        );

        txtDescripcionConfirmacion = findViewById(
                R.id.txtDescripcionConfirmacion
        );

        imgConfirmacion = findViewById(
                R.id.imgConfirmacion
        );

        btnVolverInicio = findViewById(
                R.id.btnVolverInicio
        );

        // Configurar los textos
        txtMovimientoGuardado.setText(
                "¡Movimiento\nguardado!"
        );

        txtDescripcionConfirmacion.setText(
                "«Tu movimiento se ha registrado correctamente»"
        );

        // Preparar el botón VOLVER AL INICIO
        btnVolverInicio.setOnClickListener(v -> {

            // Aquí añadiremos la navegación
            // cuando conectemos todas las pantallas.

        });
    }
}
