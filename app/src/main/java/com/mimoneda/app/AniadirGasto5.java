package com.mimoneda.app;

public class AniadirGasto5 extends BaseGastoActivity {

    @Override
    protected int obtenerLayout() {
        return R.layout.activity_aniadir_gasto5;
    }

    @Override
    protected String obtenerCategoriaInicial() {
        return "Ocio";
    }
}
