package Views;

import Controllers.Statistics;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Histogram extends JPanel {

    private Map<Integer, List<Double>> data;
    private List<Integer> f;
    private double media, mediana, moda, desviacion;

    public Histogram(Map<Integer, List<Double>> data, List<Integer> f) {
        updateData(data, f);
        this.setBackground(new Color(30, 30, 30));
    }

    public void updateData(Map<Integer, List<Double>> newData, List<Integer> newFrequencies) {
        this.data = newData;
        this.f = newFrequencies;
        this.media = Statistics.media(data, f);
        this.mediana = Statistics.mediana(data, f);
        this.moda = Statistics.moda(data, f);
        this.desviacion = Statistics.desviacion(data, f, media);
        repaint();
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.WHITE);
        g2.drawLine(40, h - 40, w - 20, h - 40);
        g2.drawLine(40, 20, 40, h - 40);
        g2.drawString("0", 25, h - 25);
        g2.drawString("Frecuencia", 5, 15);
        g2.drawString("Marca de clase", w / 2 - 40, h - 10);

        double maxFreq = f.stream().mapToInt(Integer::intValue).max().orElse(1);
        double xScale = (w - 60) / (double) data.size();
        double yScale = (h - 60) / (maxFreq * 1.2);

        Color[] barColors = {Color.CYAN, Color.MAGENTA, Color.GREEN};

        int[] polyX = new int[data.size() + 2];
        int[] polyY = new int[data.size() + 2];
        polyX[0] = 40;
        polyY[0] = h - 40;

        for (int i = 0; i < data.size(); i++) {
            List<Double> clase = data.get(i);
            int x = (int) (40 + i * xScale);
            int barWidth = (int) xScale - 4;
            int barHeight = (int) (f.get(i) * yScale);

            g2.setColor(barColors[i % barColors.length]);
            g2.fillRect(x, h - 40 - barHeight, barWidth, barHeight);

            g2.setColor(Color.WHITE);
            String label = String.format("%.1f", clase.get(4));
            g2.drawString(label, x + barWidth / 4, h - 20);

            polyX[i + 1] = x + barWidth / 2;
            polyY[i + 1] = h - 40 - barHeight;
        }

        polyX[polyX.length - 1] = 40 + (int) ((data.size() - 1) * xScale) + (int) xScale;
        polyY[polyY.length - 1] = h - 40;

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(3));
        g2.drawPolyline(polyX, polyY, polyX.length);

        f.stream().distinct().sorted().forEach(freq -> {
            int y = h - 40 - (int) (freq * yScale);
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(freq), 10, y + 5);
        });


        g2.setStroke(new BasicStroke(1));
        drawLabeledLine(g2, media, h, xScale, data, "Media");
        drawLabeledLine(g2, mediana, h, xScale, data, "Mediana");
        drawLabeledLine(g2, moda, h, xScale, data, "Moda");

        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
        drawLabeledLine(g2, media - desviacion, h, xScale, data, "-S");
        drawLabeledLine(g2, media + desviacion, h, xScale, data, "+S");
        g2.setStroke(originalStroke);
    }

    private void drawLabeledLine(Graphics2D g2, double value, int h, double xScale, Map<Integer, List<Double>> data, String label) {
        double x0 = data.get(0).get(1);
        double interval = data.get(1).get(1) - x0;
        int x = (int) (40 + (value - x0) / interval * xScale);
        g2.setColor(Color.RED);
        g2.drawLine(x, 20, x, h - 40);
        g2.setColor(Color.WHITE);
        g2.drawString(label, x + 2, 32);
    }

    //public static void main(String[] args) {
    //   Map<Integer, List<Double>> data = Statistics.generateData(1, 2, 4, 6);
    //   List<Integer> f = List.of(2, 5, 8, 6, 3, 1);
    //
    //   JFrame frame = new JFrame("Sexo anal");
    //   Histogram panel = new Histogram(data, f);
    //   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //   frame.add(panel);
    //   frame.setSize(800, 600);
    //   frame.setLocationRelativeTo(null);
    //   frame.setVisible(true);
    //}
}
