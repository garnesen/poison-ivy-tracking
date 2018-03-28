package com.hci_capstone.poison_ivy_tracker.PoisonIvyReporter;

import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that takes toggle buttons and mimics the behavior of a RadioButtonGroup.
 */
public class ToggleButtonGroup {

    private List<ToggleButton> buttons;
    private OnCheckedChangeListener listener;
    private boolean isEnabled;
    private ToggleButton curSelected;

    /**
     * Creates a new ToggleButtonGroup.
     */
    public ToggleButtonGroup() {
        buttons = new ArrayList<>();
        isEnabled = true;
    }

    /**
     * Adds a toggle button to the group.
     * @param button
     */
    public void addToggleButton(final ToggleButton button) {
        buttons.add(button);
        button.setChecked(false);

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // Toggle the rest of the buttons off.
                if (isChecked) {
                    button.setEnabled(false);
                    curSelected = button;
                    for (ToggleButton curButton : buttons) {
                        if (curButton != button) {
                            curButton.setChecked(false);
                            curButton.setEnabled(true);
                        }
                    }
                }

                // Pass the event on to listeners.
                if (listener != null && isChecked) {
                    listener.onCheckedChanged(button, isChecked);
                }
            }
        });
    }

    /**
     * Set the enabled state of all the buttons in the group.
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        for (ToggleButton button : buttons) {
            button.setEnabled(enabled);
        }
    }

    /**
     * Returns true if the ToggleButtonGroup is enabled.
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets each ToggleButton to be unchecked.
     */
    public void clearCheck() {
        for (ToggleButton button : buttons) {
            button.setChecked(false);
        }
        curSelected = null;
    }

    /**
     * Get the currently selected ToggleButton.
     * @return the ToggleButton
     */
    public ToggleButton getCurSelected() {
        return curSelected;
    }

    /**
     * Get the tag of the current selected item.
     * @return the tag
     */
    public String getCurSelectedTag() {
        return curSelected != null ? (String) curSelected.getTag() : null;
    }

    /**
     * Set the listener for when a ToggleButton in the group has been changed.
     * @param listener
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    /**
     * A basic listener interface.
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(ToggleButton button, boolean isChecked);
    }
}
