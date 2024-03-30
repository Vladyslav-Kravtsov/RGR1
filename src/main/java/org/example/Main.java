package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Main extends JFrame {
    private ChartPanel chartPanel;
    private TreeMap<Double, Point> pointsList = new TreeMap<>();
    private JFreeChart chart;
    private JTextField textFieldStep;
    private JTextField textFieldStop;
    private JTextField textFieldStart;
    private JTextField textFieldY;
    private JPanel contentPane;
    private JTextField textFieldA;
    private XYSeriesCollection xyDataset;
    private double start;
    private double stop;
    private double step;
    private double a;
    private String fExpression;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Main() {
        setResizable(false);
        setTitle("Chart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        setBounds(dimension.width/2-500,dimension.height/2-400,1000,800);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panelButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panelButtons.getLayout();
        flowLayout.setHgap(15);
        contentPane.add(panelButtons, BorderLayout.SOUTH);

        JButton btnNewButtonPlot = new JButton("Plot");
        btnNewButtonPlot.addActionListener(arg0 -> createChart());
        panelButtons.add(btnNewButtonPlot);

        JButton btnNewButtonExit = new JButton("Exit");
        btnNewButtonExit.addActionListener(e -> System.exit(0));
        panelButtons.add(btnNewButtonExit);

        JButton btnNewButtonOpen = new JButton("Open");
        btnNewButtonOpen.addActionListener(arg0 -> openFile());
        panelButtons.add(btnNewButtonOpen);


        JButton btnNewButtonSave = new JButton("Save");
        btnNewButtonSave.addActionListener(arg0 -> saveFile());
        panelButtons.add(btnNewButtonSave);

        JPanel panelData = new JPanel();
        contentPane.add(panelData, BorderLayout.NORTH);

        JLabel lblNewLabel = new JLabel("a:");
        panelData.add(lblNewLabel);
        textFieldA = new JTextField();
        textFieldA.setText("1.0");
        panelData.add(textFieldA);
        textFieldA.setColumns(6);

        JLabel lblNewLabelY = new JLabel("function:");
        panelData.add(lblNewLabelY);
        textFieldY = new JTextField();
        panelData.add(textFieldY);
        textFieldY.setColumns(15);

        JLabel lblNewLabelStart = new JLabel("Start:");
        panelData.add(lblNewLabelStart);
        textFieldStart = new JTextField();
        panelData.add(textFieldStart);
        textFieldStart.setColumns(6);

        JLabel lblNewLabelStop = new JLabel("Stop:");
        panelData.add(lblNewLabelStop);
        textFieldStop = new JTextField();
        panelData.add(textFieldStop);
        textFieldStop.setColumns(6);

        JLabel lblNewLabelStep = new JLabel("Step:");
        panelData.add(lblNewLabelStep);
        textFieldStep = new JTextField();
        panelData.add(textFieldStep);
        textFieldStep.setColumns(6);


        XYSeries series = new XYSeries("Function");
        XYSeriesCollection xyDataset = new XYSeriesCollection(series);
        chart = ChartFactory.createXYLineChart("", "x", "y",
                xyDataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        chartPanel = new ChartPanel(chart);
        contentPane.add(chartPanel, BorderLayout.CENTER);
    }

    private void createChart() {
        start = Double.parseDouble(textFieldStart.getText());
        stop = Double.parseDouble(textFieldStop.getText());
        step = Double.parseDouble(textFieldStep.getText());
        a = Double.parseDouble(textFieldA.getText());

        XYSeries series = new XYSeries("Function");
        XYSeries seriesDef = new XYSeries("Derivative");
        fExpression = textFieldY.getText();

        double x;
        double resultExp;
        double resultExpDer;

        Expression expression = new ExpressionBuilder(fExpression).variables("x", "a").build();
        expression.setVariable("a", a);
        pointsList.clear();
        for (x = start; x <= stop; x += step) {
            expression.setVariable("x", x);
            resultExp = expression.evaluate();
            series.add(x, resultExp);

            resultExpDer = (expression.setVariable("x", x + 0.0001).evaluate()
                    - expression.setVariable("x", x - 0.0001).evaluate()) / 0.0002;

            seriesDef.add(x, resultExpDer);
            pointsList.put(x, new Point(resultExp, resultExpDer));
        }
        for (Map.Entry<Double, Point> item : pointsList.entrySet()) {
            System.out.printf("X: %f  Y: x: %f  DerivativeY: %f\n", item.getKey(), item.getValue().getY(), item.getValue().getDirY());
        }
        xyDataset = new XYSeriesCollection(series);
        xyDataset.addSeries(seriesDef);
        drawing();
    }

    private void drawing() {
        chart = ChartFactory.createXYLineChart("Function: " + fExpression, "x", "y",
                xyDataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        chartPanel.setChart(chart);
    }

    private void graphFromTreeMap() {
        XYSeries series = new XYSeries("Function");
        XYSeries seriesDef = new XYSeries("Derivative");
        for (Map.Entry<Double, Point> item : pointsList.entrySet()) {
            series.add((double) item.getKey(), item.getValue().getY());
            seriesDef.add((double) item.getKey(), item.getValue().getDirY());
        }
        xyDataset = new XYSeriesCollection(series);
        xyDataset.addSeries(seriesDef);
    }

    public void openFile(){
        File file = new File("testFile.csv");
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] nextL;
            csvReader.readNext();
            nextL = csvReader.readNext();
            a = Double.parseDouble(nextL[0]);
            textFieldA.setText(nextL[0]);
            step = Double.parseDouble(nextL[1]);
            textFieldStep.setText(nextL[1]);
            start = Double.parseDouble(nextL[2]);
            textFieldStart.setText(nextL[2]);
            stop = Double.parseDouble(nextL[3]);
            textFieldStop.setText(nextL[3]);
            textFieldY.setText(nextL[4]);
            fExpression = String.valueOf(nextL[4]);
            csvReader.readNext();
            pointsList.clear();
            while ((nextL = csvReader.readNext()) != null) {
                double x = Double.parseDouble(nextL[0]);
                double y = Double.parseDouble(nextL[1]);
                double dirY = Double.parseDouble(nextL[2]);
                pointsList.put(x, new Point(y, dirY));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        graphFromTreeMap();
        drawing();
        JOptionPane.showMessageDialog(null,
                "File with function was successfully opened.\n" +
                        "File name: "+ file.getName()+
                        "\nFunction = "+ fExpression+
                        "\nParameter = " + a+
                        "\nStep = " + step+
                        "\nStart = " + start+
                        "\nStop = " + stop,
                "PopUp Dialog",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void saveFile(){
        File selectedFile = new File("testFile.csv");
        try (FileWriter fileWriter = new FileWriter(selectedFile);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            csvWriter.writeNext(new String[]{"Parameter ", "Step ",
                    "Start", "Stop", "Function"});
            csvWriter.writeNext(new String[]{String.valueOf(a),String.valueOf(step),
                    String.valueOf(start),String.valueOf(stop),String.valueOf(fExpression)});
            csvWriter.writeNext(new String[]{"X","Y","DerivativeY"});

            for(Map.Entry<Double, Point> item : pointsList.entrySet()){
                String[] row = new String[]{
                        Double.toString(item.getKey()),
                        Double.toString(item.getValue().getY()),
                        Double.toString(item.getValue().getDirY())
                };
                csvWriter.writeNext(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null,
                "Points successfully saved to .csv file",
                "PopUp Dialog",
                JOptionPane.INFORMATION_MESSAGE);
    }
}