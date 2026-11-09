package com.mimoneda.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mimoneda.app.data.MiMonedaDbHelper;
import com.mimoneda.app.model.Movimiento;
import com.mimoneda.app.util.ImporteUtils;
import com.mimoneda.app.util.SesionManager;

import java.util.List;

public class ResumenActivity extends AppCompatActivity {

    private TextView txtSaldo;
    private TextView[] textosMovimientos;
    private SesionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        sesion = new SesionManager(this);
        txtSaldo = findViewById(R.id.txtSaldo);
        textosMovimientos = new TextView[]{
                findViewById(R.id.txtMov1),
                findViewById(R.id.txtMov2),
                findViewById(R.id.txtMov3),
                findViewById(R.id.txtMov4)
        };

        Button btnIngreso = findViewById(R.id.btnIngreso);
        Button btnGasto = findViewById(R.id.btnGasto);
        Button btnMas = findViewById(R.id.btnMas);
        Button btnMenos = findViewById(R.id.btnMenos);
        Button btnMovimientos = findViewById(R.id.btnMovimientos);
        Button btnConfig = findViewById(R.id.btnConfig);

        View.OnClickListener abrirIngreso = vista ->
                startActivity(new Intent(this, AniadirIngreso.class));
        View.OnClickListener abrirGasto = vista ->
                startActivity(new Intent(this, AniadirGasto1.class));

        btnIngreso.setOnClickListener(abrirIngreso);
        btnMas.setOnClickListener(abrirIngreso);
        btnGasto.setOnClickListener(abrirGasto);
        btnMenos.setOnClickListener(abrirGasto);
        btnMovimientos.setOnClickListener(
                vista -> startActivity(new Intent(this, EstadisticasActivity.class))
        );
        btnConfig.setOnClickListener(vista -> mostrarDialogoCerrarSesion());
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Quieres cerrar la sesión actual?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", (dialogo, opcion) -> cerrarSesion())
                .show();
    }

    private void cerrarSesion() {
        sesion.cerrarSesion();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!sesion.haySesion()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        actualizarResumen();
    }

    private void actualizarResumen() {
        MiMonedaDbHelper db = MiMonedaDbHelper.obtenerInstancia(this);
        long idCuenta = sesion.obtenerIdCuenta();

        txtSaldo.setText(
                ImporteUtils.formatearCentimos(db.obtenerSaldoCentimos(idCuenta))
        );

        List<Movimiento> movimientos = db.obtenerUltimosMovimientos(
                idCuenta,
                textosMovimientos.length
        );

        if (movimientos.isEmpty()) {
            textosMovimientos[0].setText("Aún no hay movimientos");
            textosMovimientos[0].setVisibility(View.VISIBLE);
            for (int i = 1; i < textosMovimientos.length; i++) {
                textosMovimientos[i].setVisibility(View.GONE);
            }
            return;
        }

        for (int i = 0; i < textosMovimientos.length; i++) {
            if (i < movimientos.size()) {
                Movimiento movimiento = movimientos.get(i);
                boolean esIngreso = MiMonedaDbHelper.TIPO_INGRESO.equals(
                        movimiento.getTipo()
                );
                String icono = esIngreso ? "🟢" : "🔴";
                String signo = esIngreso ? "+" : "-";
                textosMovimientos[i].setText(
                        icono + " " + movimiento.getConcepto() + " " + signo +
                                ImporteUtils.formatearCentimos(
                                        movimiento.getImporteCentimos()
                                )
                );
                textosMovimientos[i].setVisibility(View.VISIBLE);
            } else {
                textosMovimientos[i].setVisibility(View.GONE);
            }
        }
    }
}
