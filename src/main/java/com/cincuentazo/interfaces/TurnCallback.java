package com.cincuentazo.interfaces;

/**
 * Interface for handling turn completion callbacks.
 * Used primarily for machine player turn notifications.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public interface TurnCallback {

    /**
     * Called when a turn has been completed successfully.
     */
    void onTurnCompleted();

    /**
     * Called when a turn encounters an error.
     *
     * @param error the error message
     */
    void onTurnError(String error);
}