
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * <h1>MainFrame Helper</h1>
 * This class contains the helper methods that are component based (e.g., table formatting, writing
 * to color file, retrieving input values, etc). This excludes SQL and JFreeChart based functions.
 *
 * @author shahn
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainFrameHelpers {

    MainFrame mainFrame;
    MainFrameSQLHelpers mainSQLHelpers;
    JTable invtTable, salesTable, clone;

    public MainFrameHelpers(MainFrame mainFrame, MainFrameSQLHelpers mainSQLHelpers,
            JTable invtTable, JTable salesTable, JTable clone) {
        this.mainFrame = mainFrame;
        this.mainSQLHelpers = mainSQLHelpers;
        this.invtTable = invtTable;
        this.salesTable = salesTable;
        this.clone = clone;
    }

    /**
     * Set table cell and header formatting for all JTables in form.
     */
    public void setAllTableFormats() {
        // Creating formatting for cells
        TableCellRenderer tcr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = formatNumberCell(table, column, value);
                }

                setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
            }
        };

        // Centering headers of tables
        ((DefaultTableCellRenderer) invtTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        ((DefaultTableCellRenderer) salesTable.getTableHeader().getDefaultRenderer()).
                setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        // Centering and formatting cells of table
        setTableCRToTable(tcr);
    }

    /**
     * Set the MainFrame background to the color stored in the ColorPreference node. If no color if
     * previously stored, the color displayed will be light gray.
     *
     * @param settingsDialog Dialog with setting buttons
     * @throws java.util.prefs.BackingStoreException Backing store failure
     */
    public void setMainFrameColor(JDialog settingsDialog) throws BackingStoreException {
        ColorPreferences cp = new ColorPreferences();

        if (!java.util.prefs.Preferences.userRoot().nodeExists("ColorPreferences")) {
            cp.setColorPreference(Color.LIGHT_GRAY);
            mainFrame.getContentPane().setBackground(Color.LIGHT_GRAY);
            settingsDialog.getContentPane().setBackground(Color.LIGHT_GRAY);
        } else {
            Color color = new Color(cp.getColorPreference());
            mainFrame.getContentPane().setBackground(color);
            settingsDialog.getContentPane().setBackground(color);
        }
    }

    /**
     * Retrieve all values from inventory input components
     *
     * @param invtCompts Array of inventory input components
     * @param rowCount number of row that is being inserted or updated
     * @return Object array of all values of inventory inputs
     */
    public Object[] getInvtInputComptsValues(Component[] invtCompts, int rowCount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Object[] fields = new Object[11];
        int idx = 1;

        fields[0] = rowCount;

        for (Component c : invtCompts) {
            if (c instanceof JTextField) {
                // Name, category, supplier, total qty, original price
                fields[idx] = getTFInputValue(c);
            } else if (c instanceof JComboBox) {
                // Condition
                fields[idx] = ((JComboBox) c).getSelectedItem();
            } else if (c instanceof JDateChooser && ((JDateChooser) c).getDate() != null) {
                // Purchase and selling date
                fields[idx] = sdf.format(((JDateChooser) c).getDate());
            }

            // Skip over profit and roi columns
            idx = (idx == 6) ? idx + 3 : idx + 1;
        }

        return fields;
    }

    /**
     * Retrieve all values from sales input components
     *
     * @param saleCompts Array of sales input components
     * @param rowCount number of row that is being inserted or updated
     * @return Object array of all values of sales inputs
     */
    public Object[] getSalesInputComptsValues(Component[] saleCompts, int rowCount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Object[] fields = new Object[10];
        int idx = 1;

        fields[0] = rowCount;

        for (Component c : saleCompts) {
            if (c instanceof JTextField) {
                if (c == mainFrame.getQtySoldTF()
                        && mainFrame.getQtySoldTF().getText().trim().equals("")) {
                    fields[idx] = 1;
                } else {
                    fields[idx] = getTFInputValue(c);
                }
            } else if (c instanceof JDateChooser && ((JDateChooser) c).getDate() != null) {
                fields[idx] = sdf.format(((JDateChooser) c).getDate());
            }

            idx++;
        }

        return fields;
    }

    /**
     * Set up inventory components for opening edit inputs.May throw ParseException if error occur
     * while parsing dates.
     *
     * @param c component to set value to
     * @param col column index corresponding to component
     * @param val value to be set
     * @throws java.text.ParseException Invalid value while parsing
     */
    public void setValuesInvtInputs(Component c, int col, Object val) throws ParseException {
        DecimalFormat fmt = new DecimalFormat("0.00");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if (c instanceof JTextField) {
            if (col == 6 || col == 7) {
                ((JTextField) c).setText(fmt.format((Double) val));
            } else {
                ((JTextField) c).setText(val.toString());
            }
        } else if (c instanceof JComboBox) {
            ((JComboBox) c).setSelectedItem((String) val);
        } else if (c instanceof JDateChooser) {
            ((JDateChooser) c).setDate(df.parse(val.toString()));
        }
    }

    /**
     * Set up sales components for opening edit inputs.
     *
     * @param c component to set value to
     * @param col column index corresponding to component
     * @param val value to be set
     * @throws java.text.ParseException Invalid value while parsing
     */
    public void setValuesSaleInputs(Component c, int col, Object val)
            throws ParseException {
        DecimalFormat fmt = new DecimalFormat("0.00");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if (c instanceof JTextField) {
            if (col >= 5 && col <= 8) {
                ((JTextField) c).setText(fmt.format((Double) val));
            } else {
                ((JTextField) c).setText(val.toString());
            }
        } else if (c instanceof JDateChooser) {
            ((JDateChooser) c).setDate(df.parse(val.toString()));
        }
    }

    /**
     * Verify if inventory input values are valid.
     *
     * @param name Item name
     * @param pd Purchase date
     * @param sd Selling date
     * @return true if inputs are valid, false otherwise
     */
    public boolean verifyInvtInputVals(String name, String pd, String sd) {
        String errMessage = "";

        if (name == null) {
            errMessage = "Provide a name";
        } else if (pd != null && sd != null && pd.compareTo(sd) > 0) {
            mainFrame.getPurchaseDC().setCalendar(null);
            mainFrame.getSellingDC().setCalendar(null);

            errMessage = "Purchase date cannot be more selling date";
        }

        if (!errMessage.isEmpty()) {
            JOptionPane.showMessageDialog(null, errMessage, "Error Message",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Verify if sales input values are valid.
     *
     * @param itemID item ID of selected row
     * @param sd Selling date
     * @return true if inputs are valid, false otherwise
     */
    public boolean verifySalesInputVals(String itemID, String sd) {
        String errMessage = "";

        if (itemID.trim().equals("")) {
            errMessage = "Item ID required";
        } else if (invtTable.getRowCount() == 0) {
            errMessage = "Item is not in inventory table";
        } else {
            String pd = (String) invtTable.getValueAt(Integer.valueOf(itemID) - 1, 9);

            if (sd != null && pd != null && sd.compareTo(pd) < 0) {
                errMessage = "Purchase date cannot be more than selling date. Refer to inventory "
                        + "table to see purchase date";
            }
        }

        if (!errMessage.isEmpty()) {
            JOptionPane.showMessageDialog(null, errMessage, "Error Message",
                    JOptionPane.ERROR_MESSAGE);

            return false;
        }

        return true;
    }

    /**
     * Set all input values empty
     *
     * @param inputs Array of input components
     */
    public void clearInputValues(Component[] inputs) {
        for (Component c : inputs) {
            if (c instanceof JTextField) {
                ((JTextField) c).setText("");
            } else if (c instanceof JComboBox) {
                ((JComboBox) c).setSelectedIndex(0);
            } else if (c instanceof JDateChooser) {
                ((JDateChooser) c).setDate(null);
            }
        }
    }

    /**
     * Clears value from formatted TextField if not valid.
     *
     * @param jtf Formatted TextField
     */
    public void clearTFValue(JFormattedTextField jtf) {
        if (!jtf.isEditValid()) {
            // Clear invalid value
            jtf.setValue(null);
        }
    }

    /**
     * Add new row to invt JTable and apply changes that come with new row (new id/selling year).
     *
     * @param values Input values to insert into row
     * @param sd Selling date
     */
    public void insertItemIntoJTable(Object[] values, String sd) {
        // Add new row to inventory table
        ((DefaultTableModel) invtTable.getModel()).addRow(values);

        JComboBox selIDCB = mainFrame.getSelIDCB(), analysisYrCB = mainFrame.getAnalysisYrCB();

        if (selIDCB.getItemCount() == 0) {
            // Add filler choice before ids
            selIDCB.addItem(null);
        }

        // Add row id to selectIDCombo
        selIDCB.addItem(String.valueOf(invtTable.getRowCount()));

        // Add year into analysisYearCB if it does not exist already
        if (sd != null) {
            String date = sd.substring(0, 4);

            if (((DefaultComboBoxModel) analysisYrCB.getModel()).getIndexOf(date) == -1) {
                insertYear(date);
            }
        }

        // Clearing user input values
        clearInputValues(mainFrame.getInvtInputCompts());
    }

    /**
     * Add new row to sales JTable and apply changes that come with new row (new id/selling
     * year).May throw SQLException if JDBC encounters an error.
     *
     * @param values Input values to insert into row
     * @param sd Selling date
     * @throws java.sql.SQLException JDBC error occurred
     */
    public void insertSalesIntoJTable(Object[] values, String sd) throws SQLException {
        // Getting name from id element in values
        values[2] = invtTable.getValueAt((int) values[2] - 1, 1);

        // Add new row to sales table
        ((DefaultTableModel) salesTable.getModel()).addRow(values);

        int itemIDIdx = Integer.valueOf(mainFrame.getItemIDTF().getText()) - 1,
                qtySold = values[4] == null ? 1 : (int) values[4];
        Object quantity, invtSD;
        Double op = (Double) invtTable.getValueAt(itemIDIdx, 6), sp = (Double) values[5],
                profit = (Double) invtTable.getValueAt(itemIDIdx, 7);

        if ((quantity = invtTable.getValueAt(itemIDIdx, 5)) != null) {
            // Decrement inventory total quantity
            mainSQLHelpers.updtInvtQtyField(itemIDIdx + 1, (int) quantity - qtySold);
            invtTable.setValueAt((int) quantity - qtySold, itemIDIdx, 5);
        }

        if (op != null && sp != null) {
            double amount = (sp - op) * qtySold, roi;

            profit = profit == null ? amount : profit + amount;
            roi = profit / (op * mainSQLHelpers.selectSalesQtySQL(itemIDIdx + 1));

            // Updating profit
            mainSQLHelpers.updtProfitSQL(itemIDIdx + 1, profit);
            invtTable.setValueAt(profit, itemIDIdx, 7);

            // Updating ROI
            mainSQLHelpers.updtROISQL(itemIDIdx + 1, roi);
            invtTable.setValueAt(roi, itemIDIdx, 8);
        }

        if (sd != null) {
            if ((invtSD = invtTable.getValueAt(itemIDIdx, 10)) != null
                    && (sd.compareTo(invtSD.toString()) > 0)) {
                // Update latest selling date
                mainSQLHelpers.updtSDSQL(invtTable, itemIDIdx, sd);
                invtTable.setValueAt(sd, itemIDIdx, 10);
            }

            //Adding new year to analysisYearComboBox options
            String year = sd.substring(0, 4);
            JComboBox analysisYrCB = mainFrame.getAnalysisYrCB();

            if (((DefaultComboBoxModel) analysisYrCB.getModel()).getIndexOf(year) == -1) {
                // New unique year to add
                insertYear(year);
            }
        }

        mainFrame.getSelSalesIDCB().addItem(String.valueOf(salesTable.getRowCount()));
        clearInputValues(mainFrame.getSaleInputCompts());
    }

    /**
     * Retrieve the ID column value of the given table and row index.
     *
     * @param table Table to search
     * @param rowIndx Index of row
     * @return ID number
     */
    public int locateTableIDRow(DefaultTableModel table, int rowIndx) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Integer.parseInt(table.getValueAt(i, 0).toString()) == rowIndx) {
                return i;
            }
        }

        return -1;
    }

    /**
     * After updating selling date of a row. Insert year into analysis year combo box if year is
     * unique. Otherwise, if year does not appear anymore, remove the entry.
     *
     * @param yrPreUpdt Year of selling date before update
     * @param sd Selling date
     */
    public void updtRowSellingYr(String yrPreUpdt, String sd) {
        JComboBox analysisYrCB = mainFrame.getAnalysisYrCB();
        DefaultComboBoxModel yrModel = (DefaultComboBoxModel) analysisYrCB.getModel();

        if (sd != null && yrModel.getIndexOf(sd.substring(0, 4)) == -1) {
            insertYear(sd.substring(0, 4));
        }

        try {
            if (yrPreUpdt != null && !mainSQLHelpers.selectYearSQL(yrPreUpdt)) {
                analysisYrCB.setEnabled(false);
                analysisYrCB.removeItem(yrPreUpdt);
                analysisYrCB.setEnabled(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error while updating selling year.",
                    "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Search inventory table for search value and filter table based on results. The columns
     * checked are name, category, and supplier.
     *
     * <br> NOTE: To reset table, search for an empty string.
     *
     * @param searchVal User input
     */
    public void searchInventory(String searchVal) {
        RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                String name = (String) entry.getValue(1),
                        category = (String) entry.getValue(2),
                        supplier = (String) entry.getValue(3);

                if (category != null && supplier != null) {
                    return name.toLowerCase().contains(searchVal)
                            || category.toLowerCase().contains(searchVal)
                            || supplier.toLowerCase().contains(searchVal);
                } else if (category == null && supplier != null) {
                    return name.toLowerCase().contains(searchVal)
                            || supplier.toLowerCase().contains(searchVal);
                } else if (category != null && supplier == null) {
                    return name.toLowerCase().contains(searchVal)
                            || category.toLowerCase().contains(searchVal);
                } else {
                    return name.toLowerCase().contains(searchVal);
                }
            }
        };

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(
                invtTable.getModel());
        sorter.setRowFilter(filter);
        invtTable.setRowSorter(sorter);

    }

    /**
     * Search sales table for search value and filter table based on results. The columns checked
     * are customer name, item name, and platform.
     *
     * <br> NOTE: To reset table, search for an empty string.
     *
     * @param searchVal User input
     */
    public void searchSales(String searchVal) {
        RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                String customerName = (String) entry.getValue(1),
                        itemName = (String) entry.getValue(2),
                        platform = (String) entry.getValue(3);

                if (customerName != null && platform != null) {
                    return customerName.toLowerCase().contains(searchVal)
                            || itemName.toLowerCase().contains(searchVal)
                            || platform.toLowerCase().contains(searchVal);
                } else if (customerName == null && platform != null) {
                    return itemName.toLowerCase().contains(searchVal)
                            || platform.toLowerCase().contains(searchVal);
                } else if (customerName != null && platform == null) {
                    return customerName.toLowerCase().contains(searchVal)
                            || itemName.toLowerCase().contains(searchVal);
                } else {
                    return itemName.toLowerCase().contains(searchVal);
                }
            }
        };

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(salesTable.getModel());
        sorter.setRowFilter(filter);
        salesTable.setRowSorter(sorter);
    }

    /**
     * Set up for printer.
     *
     * @param printerJob Printer job
     */
    public void setUpPrintJob(PrinterJob printerJob) {
        printerJob.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            JPanel analysisPnl = mainFrame.getAnalysisPnl();

            Dimension dim = analysisPnl.getSize();
            double cHeight = dim.getHeight();
            double cWidth = dim.getWidth();

            // get the bounds of the printable area
            double pHeight = pageFormat.getImageableHeight();
            double pWidth = pageFormat.getImageableWidth();

            double pXStart = pageFormat.getImageableX();
            double pYStart = pageFormat.getImageableY();

            double xRatio = pWidth / cWidth;
            double yRatio = pHeight / cHeight;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pXStart, pYStart);
            g2.scale(xRatio, yRatio);

            analysisPnl.paint(g2);
            return Printable.PAGE_EXISTS;
        });
    }

    /**
     * Set formatting to number cells based on column value type.
     *
     * @param table JTable to format
     * @param column Index of column in table
     * @param value Value of cell in column
     *
     * @return value with formatting if applicable
     */
    private Object formatNumberCell(JTable table, int column, Object value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(),
                percentFormat = NumberFormat.getPercentInstance();

        percentFormat.setMinimumFractionDigits(2);

        if ((table == invtTable || table == clone) && (column == 6 || column == 7)
                || (table == salesTable && (column >= 5 && column <= 8))) {
            /* Original price, profit (invtTable/clone) OR
               Selling price, selling, processing, or shipping fees (salesTable) */
            value = currencyFormat.format(value);
        } else if ((table == invtTable || table == clone) && column == 8) {
            // ROI in invtTable
            value = percentFormat.format(value);
        }

        return value;
    }

    /**
     * Set alignments and formatting to cells of JTables in the form.
     *
     * @param tcr TableCellRenderer to apply to JTables
     */
    private void setTableCRToTable(TableCellRenderer tcr) {
        for (int i = 0; i < invtTable.getColumnCount(); i++) {
            invtTable.getColumnModel().getColumn(i).setCellRenderer(tcr);
        }

        for (int i = 0; i < salesTable.getColumnCount(); i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(tcr);
        }

        // Set clone to match invtTable model
        clone.setModel(invtTable.getModel());
        clone.setColumnModel(invtTable.getColumnModel());
    }

    /**
     * Check if text field component should be return as double, int, or string depending on what
     * the component value represents
     *
     * @param c Text field component
     * @return Object array of all values of inputs
     */
    private Object getTFInputValue(Component c) {
        if (((JTextField) c).getText().trim().equals("")) {
            return null;

        } else if (c == mainFrame.getOrigPriceTF() || c == mainFrame.getSellingPriceTF()
                || c == mainFrame.getSellFeesTF() || c == mainFrame.getProcessFeesTF()
                || c == mainFrame.getShipFeesTF()) {
            // Currency
            return Double.parseDouble(((JTextField) c).getText());
        } else if (c == mainFrame.getItemIDTF() || c == mainFrame.getTotalQtyTF()
                || c == mainFrame.getQtySoldTF()) {
            // ID or qtys 
            return Integer.parseInt(((JTextField) c).getText());
        } else {
            return (String) ((JTextField) c).getText();
        }
    }

    /**
     * This method is used to insert an item into the analysisYearComboBox according to descending
     * order.
     *
     * @param year The value to be inserted into the combo box
     */
    private void insertYear(String year) {
        int i = 0;
        JComboBox analysisYrCB = mainFrame.getAnalysisYrCB();

        if (analysisYrCB.getItemCount() == 0
                || year.compareTo((String) analysisYrCB.getItemAt(0)) > 0) {
            // Value is greater than current max year
            analysisYrCB.insertItemAt(year, 0);
            analysisYrCB.setEnabled(false);
            analysisYrCB.setSelectedIndex(0);
            analysisYrCB.setEnabled(true);
        } else if (year.compareTo((String) analysisYrCB.getItemAt(analysisYrCB.getItemCount() - 1)) < 0) {
            // Value is less than current min year
            analysisYrCB.insertItemAt(year, analysisYrCB.getItemCount());
        } else {
            // Must search for pos where year is greater than pos + 1 and
            // year is less than pos - 1
            while (i < analysisYrCB.getItemCount() - 1) {
                if (year.compareTo((String) analysisYrCB.getItemAt(i)) < 0
                        && year.compareTo((String) analysisYrCB.getItemAt(i + 1)) > 0) {
                    // Found 
                    analysisYrCB.insertItemAt(year, i + 1);
                    return;
                }
                i++;
            }
        }
    }
}
