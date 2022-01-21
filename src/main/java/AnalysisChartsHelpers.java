
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * <h1> MainFrame Analysis Charts Helper</h1>
 * This class contains the helper methods that are used to create and modify JFreeCharts to create
 * charts based off of data presented in the SQL database tables.
 *
 * @author shahn
 * @version 1.0.0
 * @since 1.0.0
 */
public class AnalysisChartsHelpers {

    JTable invtTable, salesTable;

    public AnalysisChartsHelpers(JTable invtTable, JTable salesTable) {
        this.invtTable = invtTable;
        this.salesTable = salesTable;
    }

    /**
     * Creates a bar graph of the number of items of a specific category given the specific year.
     * This graph is the set into the given JPanel parameter.
     *
     * @param catBarGraphPnl Panel to add graph to
     * @param year Year to check
     */
    public void createCategoryBarGraph(JPanel catBarGraphPnl, String year) {
        Map<String, Integer> values = new LinkedHashMap<>();
        int cnt = 0;
        String yr, cat;

        // Gather data
        for (int i = 0; i < invtTable.getRowCount(); i++) {
            if ((yr = (String) invtTable.getValueAt(i, 10)) != null
                    && yr.substring(0, 4).equals(year)) {
                if ((cat = (String) invtTable.getValueAt(i, 2)) == null) {
                    // No specified category
                    cnt++;
                } else {
                    if (!values.containsKey(cat.toLowerCase())) {
                        // Category not previously included
                        values.put(cat.toLowerCase(), 1);
                    } else {
                        // Category previously included
                        values.put(cat.toLowerCase(), values.get(cat.toLowerCase()) + 1);
                    }
                }
            }
        }

        values.put("Unspecified", cnt);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Populate dataset
        values.entrySet().forEach(entry -> {
            dataset.addValue(entry.getValue(), entry.getKey(), entry.getKey());
        });

        // Create graph
        JFreeChart chart = ChartFactory.createBarChart(
                "Number of Items by Category", "Category", "# of Items",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        setUpChartAxis(chart, catBarGraphPnl);
    }

    /**
     * Creates a bar graph of sales made each month across the specified year. This graph is the set
     * into the given JPanel parameter.
     *
     * @param salesBarPnl Panel to add graph to
     * @param year Year to check
     * @return Data set for profits across a year
     */
    public DefaultCategoryDataset createSalesBarGraph(JPanel salesBarPnl, String year) {
        Map<String, Double> salesMap = getMonMap();
        String val, mon;
        Double profit;

        // Gather data
        for (int i = 0; i < invtTable.getRowCount(); i++) {
            if ((val = (String) invtTable.getValueAt(i, 10)) != null) {
                mon = getMonth(val);

                if ((profit = (Double) invtTable.getValueAt(i, 8)) != null
                        && year.equals(val.substring(0, 4))) {
                    salesMap.put(mon, salesMap.get(mon) + profit);
                }
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Populate dataset
        salesMap.entrySet().forEach(entry -> {
            dataset.addValue(entry.getValue(), entry.getKey(), entry.getKey());
        });

        // Create graph
        JFreeChart chart = ChartFactory.createBarChart(
                "Sales by Month", //Chart Title  
                "Month", // Category axis  
                "Sales ($)", // Value axis  
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        setUpChartAxis(chart, salesBarPnl);

        return dataset;
    }

    /**
     * Creates a line and bar graph of profit and ROI made each month across the specified year.
     * This graph is the set into the given JPanel parameter.
     *
     * <br> NOTE: This graph is create by combining the line graph for ROI and bar graph for
     * profits.
     *
     * @param profitROIPnl Panel to add graph to
     * @param profitsDataset Dataset for profits
     * @param year Year to check
     */
    public void createProfitROIGraph(JPanel profitROIPnl, DefaultCategoryDataset profitsDataset,
            String year) {
        Map<String, Double> roiMap = getMonMap();
        String date, mon;
        Double roi;

        // Gather data
        for (int i = 0; i < invtTable.getRowCount(); i++) {
            if ((date = (String) invtTable.getValueAt(i, 10)) != null
                    && (roi = (Double) invtTable.getValueAt(i, 8)) != null
                    && year.equals(date.substring(0, 4))) {
                mon = getMonth(date);
                roiMap.put(mon, roiMap.get(mon) + roi);
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Populate dataset
        roiMap.entrySet().forEach(entry -> {
            if ((countTotalOccurences(entry.getKey(), year)) != 0) {
                dataset.addValue(entry.getValue() / countTotalOccurences(entry.getKey(), year),
                        "Average ROI", entry.getKey());
            } else {
                dataset.addValue(entry.getValue(), "Average ROI", entry.getKey());
            }
        });

        JFreeChart chart = new JFreeChart("Profit/ROI by Month",
                setUpLineBarGraph(dataset, profitsDataset));

        setUpChart(new ChartPanel(chart), profitROIPnl);
    }

    /**
     * Creates pie graph of platforms used in sales. This graph is the set into the given JPanel
     * parameter.
     *
     * @param platformPnl Panel to add graph to
     * @param year Year to check
     */
    public void createPlatformPieChart(JPanel platformPnl, String year) {
        Map<String, Integer> platformMap = new LinkedHashMap<>();
        String sd, platform;

        // Gather data
        for (int i = 0; i < salesTable.getRowCount(); i++) {
            if ((sd = (String) salesTable.getValueAt(i, 9)) != null
                    && (sd.substring(0, 4)).equals(year)
                    && (platform = (String) salesTable.getValueAt(i, 3)) != null) {
                if (!platformMap.containsKey(platform)) {
                    platformMap.put(platform, 1);
                } else {
                    platformMap.put(platform, platformMap.get(platform) + 1);
                }
            }
        }

        DefaultPieDataset dataset = new DefaultPieDataset();

        // Populate dataset
        platformMap.entrySet().forEach(entry -> {
            dataset.setValue(entry.getKey(), entry.getValue());
        });

        // Create graph
        JFreeChart chart = ChartFactory.createPieChart(
                "Platforms",
                dataset,
                true, true, false
        );

        setUpChart(new ChartPanel(chart), platformPnl);
    }

    /**
     * Creates triple bar graph of selling, processing, and shipping fees across the given year
     * parameter. This graph is the set into the given JPanel parameter.
     *
     * @param feesPnl Panel to add graph to
     * @param year Year to check
     */
    public void createFeesChart(JPanel feesPnl, String year) {
        Map<String, Double> sfMap, pfMap, shfMap;
        sfMap = pfMap = shfMap = new LinkedHashMap<>(getMonMap());
        String sd, mon;
        Double sf, pf, shf;

        // Gather data
        for (int i = 0; i < salesTable.getRowCount(); i++) {
            if ((sd = (String) salesTable.getValueAt(i, 9)) != null
                    && sd.substring(0, 4).equals(year)) {
                mon = getMonth(sd);

                if ((sf = (Double) salesTable.getValueAt(i, 6)) != null) {
                    sfMap.put(mon, sfMap.get(mon) + sf);
                }

                if ((pf = (Double) salesTable.getValueAt(i, 7)) != null) {
                    pfMap.put(mon, pfMap.get(mon) + pf);
                }

                if ((shf = (Double) salesTable.getValueAt(i, 8)) != null) {
                    shfMap.put(mon, shfMap.get(mon) + shf);
                }
            }
        }

        // Create graph
        JFreeChart chart = ChartFactory.createBarChart(
                "Fees by Month", "Month", "Fees ($)",
                createFeesDS(sfMap, pfMap, shfMap),
                PlotOrientation.VERTICAL,
                true, true, false
        );

        setUpChart(new ChartPanel(chart), feesPnl);
    }

    /**
     * Create and populate dataset using selling, processing, and shipping fee values.
     *
     * @param sellFeesMap Map containing selling fees across months values
     * @param processFeesMap Map containing processing fees across months values
     * @param shipFeesMap Map containing shipping fees across months values
     */
    private DefaultCategoryDataset createFeesDS(Map<String, Double> sellFeesMap,
            Map<String, Double> processFeesMap, Map<String, Double> shipFeesMap) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        sellFeesMap.entrySet().forEach(entry -> {
            dataset.addValue(entry.getValue(), "Selling Fees", entry.getKey());
        });

        processFeesMap.entrySet().forEach(entry -> {
            dataset.addValue(entry.getValue(), "Processing Fees", entry.getKey());
        });

        shipFeesMap.entrySet().forEach(entry -> {
            dataset.addValue(entry.getValue(), "Shipping Fees", entry.getKey());
        });

        return dataset;
    }

    /**
     * Get the count of ROI values in the given parameters.
     *
     * @param mon Month name
     * @param year Year value
     * @return Count of values
     */
    private int countTotalOccurences(String mon, String year) {
        int cnt = 0;
        String sd;

        for (int i = 0; i < invtTable.getRowCount(); i++) {
            if ((sd = (String) invtTable.getValueAt(i, 10)) != null
                    && invtTable.getValueAt(i, 8) != null && getMonth(sd).equals(mon)
                    && sd.substring(0, 4).equals(year)) {
                cnt++;
            }
        }

        return cnt;
    }

    /**
     * Insert chartPanel parameter to panel parameter. If there is a previous component inserted, it
     * is removed.
     *
     * @param chartPanel Panel that contains graph
     * @param panel Panel to display chart
     */
    private void setUpChart(JPanel chartPanel, JPanel panel) {
        if (panel.getComponentCount() != 0) {
            panel.remove(0);
        }

        chartPanel.setSize(panel.getPreferredSize());
        panel.add(chartPanel);
    }

    /**
     * Set chart axis to have tick marks and to have angled labels. This is to ensure that too many
     * labels will not clutter the graph.
     *
     * @param chart Graph
     * @param panel Panel to display chart
     */
    private void setUpChartAxis(JFreeChart chart, JPanel panel) {
        ChartPanel chartPanel = new ChartPanel(chart);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        setUpChart(chartPanel, panel);
    }

    /**
     * Merge the line and bar graph for the Profit and ROI line-bar graph.
     *
     * @param chartPanel Panel that contains graph
     * @param panel Panel to display chart
     * @return Line-Bar graph
     */
    private CategoryPlot setUpLineBarGraph(DefaultCategoryDataset dataset,
            DefaultCategoryDataset salesDataset) {
        CategoryPlot plot = new CategoryPlot();

        CategoryItemRenderer lineRenderer = new LineAndShapeRenderer();
        plot.setDataset(0, dataset);
        plot.setRenderer(0, lineRenderer);
        plot.setDomainAxis(new CategoryAxis("Month"));
        plot.setRangeAxis(new NumberAxis("ROI (%)"));

        // Add the second dataset and render as lines
        CategoryItemRenderer baRenderer = new BarRenderer();

        plot.setDataset(1, salesDataset);
        plot.setRenderer(1, baRenderer);
        plot.setRangeAxis(1, new NumberAxis("Profit ($)"));

        plot.mapDatasetToRangeAxis(0, 0); //1st dataset to 1st y-axis
        plot.mapDatasetToRangeAxis(1, 1); //2nd dataset to 2nd y-axis

        return plot;
    }

    /**
     * Retrieve empty map that maps months (MMM) to a double.
     *
     * @return Empty month to double map
     */
    private Map<String, Double> getMonMap() {
        Map<String, Double> map = new LinkedHashMap<>();

        map.put("Jan", 0.0);
        map.put("Feb", 0.0);
        map.put("Mar", 0.0);
        map.put("Apr", 0.0);
        map.put("May", 0.0);
        map.put("Jun", 0.0);
        map.put("Jul", 0.0);
        map.put("Aug", 0.0);
        map.put("Sep", 0.0);
        map.put("Oct", 0.0);
        map.put("Nov", 0.0);
        map.put("Dec", 0.0);

        return map;
    }

    /**
     * Retrieve the month in "MMM" format from the given parameter.
     *
     * @param date Date in "yyyy-MM-dd" format
     * @return Month in "MMM" format
     */
    private String getMonth(String date) {
        switch (date.substring(5, 7)) {
            case "01":
                return "Jan";
            case "02":
                return "Feb";
            case "03":
                return "Mar";
            case "04":
                return "Apr";
            case "05":
                return "May";
            case "06":
                return "Jun";
            case "07":
                return "Jul";
            case "08":
                return "Aug";
            case "09":
                return "Sep";
            case "10":
                return "Oct";
            case "11":
                return "Nov";
            case "12":
                return "Dec";
        }

        return null;
    }
}
