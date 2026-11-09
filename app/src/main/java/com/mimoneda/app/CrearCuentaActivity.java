package com.mimoneda.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mimoneda.app.data.MiMonedaDbHelper;
import com.mimoneda.app.util.SesionManager;

public class CrearCuentaActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextCorreo;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextPassword = findViewById(R.id.editTextContrasena);
        Button btnCrearCuenta = findViewById(R.id.botonCrearCuenta);
        TextView textViewIniciarSesion = findViewById(R.id.textViewRegistrarse);

        btnCrearCuenta.setOnClickListener(vista -> crearCuenta());
        textViewIniciarSesion.setOnClickListener(vista -> finish());
    }

    private void crearCuenta() {
        String nombre = editTextNombre.getText().toString().trim();
        String correo = editTextCorreo.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        if (nombre.length() < 2) {
            editTextNombre.setError("Introduce tu nombre");
            editTextNombre.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            editTextCorreo.setError("Introduce un correo válido");
            editTextCorreo.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("La contraseña debe tener al menos 6 caracteres");
            editTextPassword.requestFocus();
            return;
        }

        MiMonedaDbHelper db = MiMonedaDbHelper.obtenerInstancia(this);
        long idUsuario = db.registrarUsuario(nombre, correo, password);
        if (idUsuario == -1L) {
            editTextCorreo.setError("Ya existe una cuenta con este correo");
            editTextCorreo.requestFocus();
            return;
        }

        long idCuenta = db.obtenerCuentaPrincipal(idUsuario);
        if (idCuenta == -1L) {
            Toast.makeText(this, "No se pudo crear la cuenta", Toast.LENGTH_SHORT).show();
            return;
        }

        new SesionManager(this).guardarSesion(idUsuario, idCuenta);
        Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ResumenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
