package com.mimoneda.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class CrearCuentaActivity extends AppCompatActivity {

        private EditText editTextNombre;
        private EditText editTextCorreo;
        private EditText editTextPassword;
        private Button btnCrearCuenta;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_crear_cuenta);

            editTextNombre = findViewById(R.id.editTextNombre);
            editTextCorreo = findViewById(R.id.editTextCorreo);
            editTextPassword = findViewById(R.id.editTextContrasena);
            btnCrearCuenta = findViewById(R.id.botonCrearCuenta);

            btnCrearCuenta.setOnClickListener(v -> {

                String nombre = editTextNombre.getText().toString().trim();
                String correo = editTextCorreo.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(nombre.isEmpty() ||
                        correo.isEmpty() ||
                        password.isEmpty()) {

                    Toast.makeText(
                            CrearCuentaActivity.this,
                            "Debes completar todos los campos",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                Toast.makeText(
                        CrearCuentaActivity.this,
                        "Cuenta creada correctamente",
                        Toast.LENGTH_SHORT
                ).show();
            });
        }
    }