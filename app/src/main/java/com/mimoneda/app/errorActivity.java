package com.mimoneda.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class errorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        TextView btnVolverInicio = findViewById(R.id.btnVolverInicioError);
        btnVolverInicio.setOnClickListener(vista -> volverAlResumen());
    }

    private void volverAlResumen() {
        Intent intent = new Intent(this, ResumenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
