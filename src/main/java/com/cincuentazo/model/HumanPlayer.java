package com.cincuentazo.model;

/**
 * Represents a human player in the Cincuentazo game.
 * This class extends the {@link Player} class and specifically marks
 * an instance as a human player.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class HumanPlayer extends Player {

    /**
     * Constructs a new HumanPlayer with the specified name.
     * Automatically sets the 'isHuman' flag to true.
     *
     * @param name The name of the human player.
     */
    public HumanPlayer(String name) {
        // Calls the constructor of the superclass (Player)
        // passing the provided name and 'true' to indicate it's a human player.
        super(name, true);
    }

    /**
     * Overrides the {@code isHuman()} method from the {@link Player} class
     * to always return {@code true} for a HumanPlayer.
     *
     * @return {@code true} as this is always a human player.
     */
    @Override
    public boolean isHuman() {
        return true;
    }
}