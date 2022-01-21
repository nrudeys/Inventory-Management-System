/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * <h1>Color Preferences</h1>
 * This class contains the methods that are used to store the color users choose for future
 * references.
 *
 * @author shahn
 * @version 1.0.0
 * @since 1.0.0
 */
import java.awt.Color;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ColorPreferences {

    // This will define a node in which the preferences can be stored
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    /**
     * Set color within the preference node.
     *
     * @param color Color to set
     */
    public void setColorPreference(Color color) {
        prefs.putInt("Color", color.getRGB());
    }

    /**
     * Retrieve the color within the preference node.
     *
     */
    public int getColorPreference() throws BackingStoreException {
        return prefs.getInt("Color", Color.LIGHT_GRAY.getRGB());
    }
}
