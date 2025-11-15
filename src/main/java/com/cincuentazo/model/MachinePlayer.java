package com.cincuentazo.model;

/**
 * Represents an AI-controlled (machine) player in the Cincuentazo game.
 * This class extends the {@link Player} class and specifically marks
 * an instance as a machine player.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class MachinePlayer extends Player {

    /**
     * Constructs a new MachinePlayer with the specified name.
     * Automatically sets the 'isHuman' flag to false.
     *
     * @param name The name of the machine player.
     */
    public MachinePlayer(String name) {
        // Calls the constructor of the superclass (Player)
        // passing the provided name and 'false' to indicate it's a machine player.
        super(name, false);
    }

    /**
     * Overrides the {@code isHuman()} method from the {@link Player} class
     * to always return {@code false} for a MachinePlayer.
     *
     * @return {@code false} as this is always a machine player.
     */
    @Override
    public boolean isHuman() {
        return false;
    }
}