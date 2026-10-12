package com.mimoneda.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        EditText editTextCorreo = findViewById(R.id.editTextCorreo);
        Button buttonIniciarSesion = findViewById(R.id.buttonIniciarSesion);

        buttonIniciarSesion.setOnClickListener(vista -> {
            String correo = editTextCorreo.getText().toString().trim();

            if (correo.isEmpty()) {
                editTextCorreo.setError(
                        "El correo electrónico es obligatorio"
                );
                editTextCorreo.requestFocus();
            } else {
                editTextCorreo.setError(null);
            }
        });
    }
}