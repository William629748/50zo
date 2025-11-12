package com.cincuentazo.model;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        // Pasamos el nombre Y 'true' porque es humano
        super(name, true);
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}