package com.mimoneda.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mimoneda.app.data.MiMonedaDbHelper;
import com.mimoneda.app.util.ImporteUtils;
import com.mimoneda.app.util.SesionManager;

import java.util.Calendar;
import java.util.Locale;

public class AniadirIngreso extends AppCompatActivity {

    private EditText etImporte;
    private EditText etConcepto;
    private EditText etFecha;
    private String fechaIso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_ingreso);

        etImporte = findViewById(R.id.etImporte);
        etConcepto = findViewById(R.id.etConcepto);
        etFecha = findViewById(R.id.etFecha);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);
        ImageButton btnVolver = findViewById(R.id.btnVolver);

        establecerFecha(Calendar.getInstance());
        etFecha.setOnClickListener(vista -> mostrarCalendario());
        btnVolver.setOnClickListener(vista -> finish());
        btnCancelar.setOnClickListener(vista -> finish());
        btnGuardar.setOnClickListener(vista -> guardarIngreso());
    }

    private void mostrarCalendario() {
        Calendar calendario = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (vista, anio, mes, dia) -> {
                    Calendar seleccion = Calendar.getInstance();
                    seleccion.set(anio, mes, dia);
                    establecerFecha(seleccion);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void establecerFecha(Calendar calendario) {
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        fechaIso = String.format(Locale.ROOT, "%04d-%02d-%02d", anio, mes, dia);
        etFecha.setText(String.format(Locale.ROOT, "%d/%d/%d", dia, mes, anio));
    }

    private void guardarIngreso() {
        String concepto = etConcepto.getText().toString().trim();
        long importeCentimos;

        try {
            importeCentimos = ImporteUtils.eurosACentimos(
                    etImporte.getText().toString()
            );
        } catch (IllegalArgumentException excepcion) {
            etImporte.setError(excepcion.getMessage());
            etImporte.requestFocus();
            return;
        }

        if (concepto.isEmpty()) {
            etConcepto.setError("Introduce un concepto");
            etConcepto.requestFocus();
            return;
        }

        SesionManager sesion = new SesionManager(this);
        if (!sesion.haySesion()) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        long idMovimiento = MiMonedaDbHelper.obtenerInstancia(this).insertarMovimiento(
                sesion.obtenerIdCuenta(),
                MiMonedaDbHelper.TIPO_INGRESO,
                importeCentimos,
                concepto,
                fechaIso,
                "Ingresos"
        );

        Class<?> destino = idMovimiento == -1L
                ? errorActivity.class
                : movimientoConfirmadoActivity.class;
        startActivity(new Intent(this, destino));
        finish();
    }
}
