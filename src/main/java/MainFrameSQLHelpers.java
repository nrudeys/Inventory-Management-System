
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * <h1>MainFrame SQL Helpers</h1>
 * This class contains the SQL helpers methods.
 *
 *
 * @author shahn
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainFrameSQLHelpers {

    Connection con;
    MainFrame mainFrame;
    JTable invtTable;

    public MainFrameSQLHelpers(Connection con, MainFrame mainFrame, JTable invtTable) {
        this.con = con;
        this.mainFrame = mainFrame;
        this.invtTable = invtTable;
    }

    /**
     * Populates the given table retrieving data from the table corresponding SQL table.May throw
     * SQLException if JDBC encounters an error.
     *
     * @param table JTable to populate (respective to its SQLite table)
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void selectAllSQL(JTable table) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM " + table.getName());
        ResultSet resSet = stmt.executeQuery();

        // Process results
        if (table == invtTable) {
            mainFrame.addAllItems(resSet, (DefaultTableModel) table.getModel());
        } else {
            mainFrame.addAllSales(resSet, (DefaultTableModel) table.getModel());
        }

        resSet.close();
        stmt.close();
    }

    /**
     * Retrieve a sales entry item ID using its unique ID.May throw SQLException if JDBC encounters
     * an error.
     *
     * @param id Sale entry unique ID number
     * @return Item ID number
     * @throws java.sql.SQLException JDBC error occurred
     */
    public String selectItemIDSaleSQL(int id) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT item_id FROM sales WHERE id = ?");

        stmt.setInt(1, id);

        ResultSet resSet = stmt.executeQuery();
        String itemID = String.valueOf(resSet.getInt(1));

        resSet.close();
        stmt.close();

        return itemID;
    }

    /**
     * Retrieve the max selling date in the sale given the unique item ID.May throw SQLException if
     * JDBC encounters an error.
     *
     * @param itemIDTF unique item ID number
     * @return Max selling date for given unique item ID in sales table
     * @throws java.sql.SQLException JDBC error occurred
     */
    public String selectMaxSaleSDSQL(JFormattedTextField itemIDTF) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT MAX(selling_date) FROM sales WHERE item_id = ?");

        stmt.setInt(1, Integer.parseInt(itemIDTF.getText()));

        ResultSet resSet = stmt.executeQuery();
        String max = resSet.getString(1);

        resSet.close();
        stmt.close();

        return max;

    }

    /**
     * Checks if given year parameter in either inventory or sales table.May throw SQLException if
     * JDBC encounters an error.
     *
     * @param year Selling date year
     * @return True if year is in either table, false otherwise
     * @throws java.sql.SQLException JDBC error occurred
     */
    public boolean selectYearSQL(String year) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM (SELECT inventory.selling_date, sales.selling_date FROM "
                + "inventory, sales WHERE SUBSTR(inventory.selling_date, 1, 4) = ? OR "
                + "SUBSTR(sales.selling_date, 1, 4) = ?)");

        stmt.setString(1, year);
        stmt.setString(2, year);

        ResultSet resSet = stmt.executeQuery();
        boolean found = resSet.getInt(1) != 0;

        resSet.close();
        stmt.close();

        return found;
    }

    /**
     * Retrieve the total quantity column sum from the inventory table.May throw SQLException if
     * JDBC encounters an error.
     *
     * @return Sum of quantity column in inventory table
     * @throws java.sql.SQLException JDBC error occurred
     */
    public int selectTotalQtySumSQL() throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT SUM(qty) FROM inventory");
        ResultSet rs = stmt.executeQuery();
        int sum = rs.getInt(1);

        rs.close();
        stmt.close();

        return sum;
    }

    /**
     * Retrieve the profit column sum from the inventory table.May throw SQLException if JDBC
     * encounters an error.
     *
     * @return Sum of profit column in inventory table
     * @throws java.sql.SQLException JDBC error occurred
     */
    public int selectProfitSumSQL() throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT SUM(profit) FROM inventory");
        ResultSet rs = stmt.executeQuery();
        int sum = rs.getInt(1);

        rs.close();
        stmt.close();

        return sum;
    }

    /**
     * Retrieve the sum of sold items of the given item ID.May throw SQLException if JDBC encounters
     * an error.
     *
     * @param id Item ID number
     * @return Sum of item quantity sold
     * @throws java.sql.SQLException JDBC error occurred
     */
    public int selectSalesQtySQL(int id) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT SUM(qty_sold) FROM sales WHERE item_id = ?");

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        int num = rs.getInt(1);

        rs.close();
        stmt.close();

        return num;
    }

    /**
     * Update all IDs starting from given ID number.May throw SQLException if JDBC encounters an
     * error.
     *
     * @param table Table to update
     * @param ID Initial ID to update
     * @param rowCount Number of rows
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void updtIDCountSQL(JTable table, int ID, int rowCount) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "UPDATE " + table.getName() + " SET id = ? WHERE id = ?");

        for (int i = ID; i <= rowCount; i++) {
            stmt.setInt(1, i);
            stmt.setInt(2, i + 1);
            stmt.executeUpdate();
            stmt.clearParameters();

            // Updating corresponding JTable
            table.setValueAt(String.valueOf(i), i - 1, 0);
        }

        stmt.close();

    }

    /**
     * Updates inventory qty SQL field using given parameters.May throw SQLException if JDBC
     * encounters an error.
     *
     * @param id ID of row to update
     * @param qty Update value
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void updtInvtQtyField(int id, int qty) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("UPDATE inventory SET qty = ? WHERE id = ?");

        stmt.setInt(1, qty);
        stmt.setInt(2, id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Updates inventory profit SQL field using given parameters.TMay throw SQLException if JDBC
     * encounters an error.
     *
     * @param id ID of row to update
     * @param profit Update value
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void updtProfitSQL(int id, double profit) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "UPDATE inventory SET profit = ? WHERE id = ?");

        stmt.setInt(1, (int) (profit * 100));
        stmt.setInt(2, id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Updates inventory ROI SQL field using given parameters.May throw SQLException if JDBC
     * encounters an error.
     *
     * @param id ID of row to update
     * @param roi Update value
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void updtROISQL(int id, double roi) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("UPDATE inventory SET roi = ? WHERE id = ?");

        stmt.setDouble(1, roi);
        stmt.setInt(2, id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Updates table selling date SQL field using given parameters.May throw SQLException if JDBC
     * encounters an error.
     *
     * @param table Table to update
     * @param id ID of row to update
     * @param sd Update value
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void updtSDSQL(JTable table, int id, String sd) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "UPDATE " + table.getName() + " SET selling_date = ? WHERE id = ?");

        stmt.setString(1, sd);
        stmt.setInt(2, id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Updates table selling date SQL field using given parameters.May throw SQLException if JDBC
     * encounters an error.
     *
     * @param table Table to delete data from
     * @param selID Select ID JComboBox to clear all items from
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void clearTableSQL(JTable table, JComboBox selID) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("DELETE FROM " + table.getName());

        stmt.executeUpdate();
        stmt.close();

        ((DefaultTableModel) table.getModel()).setNumRows(0);
        selID.removeAllItems();
        selID.addItem(null);

        mainFrame.resetAllFields();

        mainFrame.getSelItemIDBtn().setEnabled(true);

        mainFrame.getAnalysisYrCB().removeAllItems();
    }

    /**
     * Set inventory SQL fields using given input values.Throws SQLException if error occurs.
     *
     * @param stmt Statement to set fields to
     * @param values Inputs used to set field values
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void setInvtSQLFields(PreparedStatement stmt, Object[] values) throws SQLException {
        for (int i = 0; i < 11; i++) {
            if (i == 0 || i == 5 || i == 6 || i == 7) {
                // Setting ID, qty, original price, or profit
                setIntField(stmt, values[i], i + 1, ((i == 6 || i == 7)));
            } else if ((i >= 1 && i <= 4) || (i == 9 || i == 10)) {
                /* Setting name, category, supplier, condition, purchase or selling date */
                setStringField(stmt, values[i], i + 1);
            } else if (i == 8) {
                // Setting ROI
                setDecimalField(stmt, values[i], i + 1);
            }
        }
    }

    /**
     * Set Sales SQL fields using given input values.May throw SQLException if JDBC encounters an
     * error.
     *
     * @param stmt Statement to set fields to
     * @param values Inputs used to set field values
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void setFieldsSalesSQL(PreparedStatement stmt, Object[] values) throws SQLException {
        for (int i = 0; i < 10; i++) {
            if (i == 1 || i == 3 || i == 9) {
                // Name, platform, or selling date
                setStringField(stmt, values[i], i + 1);
            } else if (i == 0 || i == 2 || i == 4) {
                // ID, item id, or qty sold
                setIntField(stmt, values[i], i + 1, false);
            } else {
                // Price or fees
                setIntField(stmt, values[i], i + 1, true);
            }
        }
    }

    /**
     * Set INT SQL field. If value is null, the field gets set to null with its type specified. May
     * throw SQLException if JDBC encounters an error.
     *
     * @param stmt Statement to set then field to
     * @param values Value to set field
     * @param paramIndx Statement parameter index
     * @param isCurrency Indicator to if value should be considered as currency
     */
    private void setIntField(PreparedStatement stmt, Object value, int paramIndx,
            boolean isCurrency) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndx, Types.INTEGER);
        } else if (isCurrency) {
            // Converts currency amount to cents and sets INT SQL field
            stmt.setInt(paramIndx, (int) ((double) value) * 100);
        } else {
            stmt.setInt(paramIndx, (int) value);
        }
    }

    /**
     * Set String SQL field. If value is null, the field gets set to null with its type specified.
     * May throw SQLException if JDBC encounters an error.
     *
     * @param stmt Statement to set then field to
     * @param values Value to set field
     * @param paramIndx Statement parameter index
     */
    private void setStringField(PreparedStatement stmt, Object value, int paramIndx)
            throws SQLException {
        if (value == null || ((String) value).trim().equals("")) {
            stmt.setNull(paramIndx, Types.VARCHAR);
        } else {
            stmt.setString(paramIndx, (String) value);
        }
    }

    /**
     * Set Double SQL field. If value is null, the field gets set to null with its type specified.
     * May throw SQLException if JDBC encounters an error.
     *
     * @param stmt Statement to set then field to
     * @param values Value to set field
     * @param paramIndx Statement parameter index
     */
    private void setDecimalField(PreparedStatement stmt, Object value, int paramIndx)
            throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndx, Types.DECIMAL);
        } else {
            stmt.setDouble(paramIndx, ((Double) value) * 100);
        }
    }
}
