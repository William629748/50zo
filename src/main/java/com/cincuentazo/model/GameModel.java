package com.cincuentazo.model;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList; // <-- Importante añadir esto

public class GameModel {
    public void initializeGame(int numMachines) {
    }

    public Player getCurrentPlayer() {
        return null; // <-- ARREGLO
    }

    public void playCard(Card card) {
    }

    public boolean isGameEnded() {
        return false; // <-- ARREGLO
    }

    public void nextTurn() {
    }

    public int getTableSum() {
        return 0; // <-- ARREGLO
    }

    public Card getTableCard() {
        return null; // <-- ARREGLO
    }

    public Collection<Object> getDeck() {
        return new ArrayList<>(); // <-- ARREGLO
    }

    public List<Object> getPlayers() {
        return new ArrayList<>(); // <-- ARREGLO
    }

    public void drawCard() {
    }

    public Player getWinner() {
        return null; // <-- ARREGLO
    }
}