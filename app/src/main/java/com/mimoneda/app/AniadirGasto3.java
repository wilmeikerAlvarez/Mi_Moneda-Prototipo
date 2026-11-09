package com.mimoneda.app;

public class AniadirGasto3 extends BaseGastoActivity {

    @Override
    protected int obtenerLayout() {
        return R.layout.activity_aniadir_gasto3;
    }

    @Override
    protected String obtenerCategoriaInicial() {
        return "Familia";
    }
}
