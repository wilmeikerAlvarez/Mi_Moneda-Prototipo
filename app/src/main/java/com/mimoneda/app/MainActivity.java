package com.mimoneda.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mimoneda.app.data.MiMonedaDbHelper;
import com.mimoneda.app.util.SesionManager;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCorreo;
    private EditText editTextContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SesionManager sesion = new SesionManager(this);
        if (sesion.haySesion()) {
            abrirResumen();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (vista, insets) -> {
                    Insets barrasSistema = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );
                    vista.setPadding(
                            barrasSistema.left,
                            barrasSistema.top,
                            barrasSistema.right,
                            barrasSistema.bottom
                    );
                    return insets;
                }
        );

        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextContrasena = findViewById(R.id.editTextContrasena);
        Button buttonIniciarSesion = findViewById(R.id.botonCrearCuenta);
        TextView textViewRegistrarse = findViewById(R.id.textViewRegistrarse);

        buttonIniciarSesion.setOnClickListener(vista -> iniciarSesion());
        textViewRegistrarse.setOnClickListener(
                vista -> startActivity(new Intent(this, CrearCuentaActivity.class))
        );
    }

    private void iniciarSesion() {
        String correo = editTextCorreo.getText().toString().trim();
        String password = editTextContrasena.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            editTextCorreo.setError("Introduce un correo válido");
            editTextCorreo.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextContrasena.setError("Introduce tu contraseña");
            editTextContrasena.requestFocus();
            return;
        }

        MiMonedaDbHelper db = MiMonedaDbHelper.obtenerInstancia(this);
        long idUsuario = db.autenticarUsuario(correo, password);
        if (idUsuario == -1L) {
            Toast.makeText(
                    this,
                    "Correo o contraseña incorrectos",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        long idCuenta = db.obtenerCuentaPrincipal(idUsuario);
        if (idCuenta == -1L) {
            Toast.makeText(this, "No se encontró la cuenta", Toast.LENGTH_SHORT).show();
            return;
        }

        new SesionManager(this).guardarSesion(idUsuario, idCuenta);
        abrirResumen();
    }

    private void abrirResumen() {
        Intent intent = new Intent(this, ResumenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
