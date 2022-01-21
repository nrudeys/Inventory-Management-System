
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author shahn
 */
public class TestMainFrame {

    private void testItemNoName() {
        
    }

    public static void main(String[] args) throws BackingStoreException {
        try {
            MainFrame mainFrame = new MainFrame();
        } catch (SQLException | FileNotFoundException | ParseException ex) {
            Logger.getLogger(TestMainFrame.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }
}
