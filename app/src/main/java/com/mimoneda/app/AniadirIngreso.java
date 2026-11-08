package com.mimoneda.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AniadirIngreso extends AppCompatActivity {

    private EditText etImporte;
    private EditText etConcepto;
    private EditText etFecha;

    private Button btnGuardar;
    private Button btnCancelar;

    private ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_ingreso);

        // Referencias XML
        etImporte = findViewById(R.id.etImporte);
        etConcepto = findViewById(R.id.etConcepto);
        etFecha = findViewById(R.id.etFecha);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnVolver = findViewById(R.id.btnVolver);

        // Selector de fecha
        etFecha.setOnClickListener(v -> mostrarCalendario());

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());

        // Botón cancelar
        btnCancelar.setOnClickListener(v -> finish());

        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarIngreso());
    }

    private void mostrarCalendario() {

        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    String fecha =
                            dayOfMonth + "/" +
                                    (month + 1) + "/" +
                                    year;

                    etFecha.setText(fecha);
                },
                anio,
                mes,
                dia
        );

        datePicker.show();
    }

    private void guardarIngreso() {

        String importe = etImporte.getText().toString().trim();
        String concepto = etConcepto.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (importe.isEmpty()) {
            etImporte.setError("Introduce un importe");
            return;
        }

        if (concepto.isEmpty()) {
            etConcepto.setError("Introduce un concepto");
            return;
        }

        if (fecha.isEmpty()) {
            etFecha.setError("Selecciona una fecha");
            return;
        }

        Toast.makeText(
                this,
                "Ingreso guardado correctamente",
                Toast.LENGTH_SHORT
        ).show();

        // Aquí podrás guardar en SQLite más adelante

        finish();
    }
}