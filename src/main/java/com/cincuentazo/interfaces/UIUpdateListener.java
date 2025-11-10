package com.cincuentazo.interfaces;

/**
 * Interface for UI update notifications.
 * Allows decoupling between model updates and view updates.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public interface UIUpdateListener {

    /**
     * Called when the UI needs to be refreshed.
     */
    void onUIUpdateRequired();

    /**
     * Called when the table sum changes.
     *
     * @param newSum the new table sum value
     */
    void onTableSumChanged(int newSum);

    /**
     * Called when a status message should be displayed.
     *
     * @param message the message to display
     */
    void onStatusMessage(String message);
}
