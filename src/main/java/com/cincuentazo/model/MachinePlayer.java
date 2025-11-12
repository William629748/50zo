package com.cincuentazo.model;

public class MachinePlayer extends Player {

    public MachinePlayer(String name) {
        // Pasamos el nombre Y 'false' porque NO es humano
        super(name, false);
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}