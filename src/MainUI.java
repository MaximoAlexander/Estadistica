import Controllers.Statistics;
import Views.Histogram;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class MainUI extends JFrame {
    private final JPanel leftPanel;
    private final CardLayout cardLayout;
    private final JPanel graphPanelContainer;
    private final JTextField txtClases, txtDatos;
    private final JTable freqTable;
    private final DefaultTableModel freqModel;
    private final Font font = new Font("Lucida Sans", Font.PLAIN, 14);
    private Map<Integer, List<Double>> data;
    private List<Integer> frecuencias;
    private final JPanel dataPanel;
    private boolean showingData = false;

    public MainUI() {
        setTitle("Estadistica");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        this.setLocale(null);

        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension screenSize = new Dimension(1000,700);
        setSize(screenSize);

        JPanel header = new ImagePanel("header.png");
        header.setPreferredSize(new Dimension(screenSize.width, 80));
        add(header, BorderLayout.NORTH);

        JPanel footer = new ImagePanel("footer.png");
        footer.setPreferredSize(new Dimension(screenSize.width, 80));
        add(footer, BorderLayout.SOUTH);

        leftPanel = new JPanel(null);
        leftPanel.setPreferredSize(new Dimension(200, screenSize.height - 160));
        leftPanel.setBackground(new Color(10, 10, 10));
        add(leftPanel, BorderLayout.WEST);

        JButton btnCambiar = new JButton("Cambiar");
        btnCambiar.setFont(font);
        btnCambiar.setBackground(new Color(10, 10, 10));
        btnCambiar.setForeground(Color.WHITE);
        btnCambiar.setBounds(10, 10, 120, 30);
        leftPanel.add(btnCambiar);

        JLabel lblClases = new JLabel("Num. clases:");
        lblClases.setForeground(Color.WHITE);
        lblClases.setFont(font);
        lblClases.setBounds(10, 60, 100, 20);
        leftPanel.add(lblClases);

        txtClases = new JTextField("1");
        txtClases.setFont(font);
        txtClases.setBackground(new Color(10, 10, 10));
        txtClases.setForeground(Color.WHITE);
        txtClases.setBounds(110, 60, 60, 25);
        leftPanel.add(txtClases);

        txtClases.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTableSize(); }
            public void removeUpdate(DocumentEvent e) { updateTableSize(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        JLabel lblDatos = new JLabel("Datos:");
        lblDatos.setForeground(Color.WHITE);
        lblDatos.setFont(font);
        lblDatos.setBounds(10, 95, 100, 20);
        leftPanel.add(lblDatos);

        txtDatos = new JTextField("1, 2, 4");
        txtDatos.setFont(font);
        txtDatos.setBackground(new Color(10, 10, 10));
        txtDatos.setForeground(Color.WHITE);
        txtDatos.setBounds(60, 95, 110, 25);
        leftPanel.add(txtDatos);

        freqModel = new DefaultTableModel(new Object[]{"frecuencias"}, 1);
        freqTable = new JTable(freqModel);
        freqTable.setFont(font);
        freqTable.setRowHeight(25);
        freqTable.setBackground(new Color(10, 10, 10));
        freqTable.setForeground(Color.WHITE);
        freqTable.setGridColor(new Color(60, 60, 60));

        JScrollPane freqScroll = new JScrollPane(freqTable);
        freqScroll.setBounds(10, 160, 180, 200);
        freqScroll.getViewport().setBackground(new Color(10, 10, 10));
        freqScroll.setBackground(new Color(10, 10, 10));
        leftPanel.add(freqScroll);

        JButton btnGenerar = new JButton("Generar");
        btnGenerar.setFont(font);
        btnGenerar.setBackground(new Color(10, 10, 10));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setBounds(30, 380, 140, 30);
        leftPanel.add(btnGenerar);

        graphPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        graphPanelContainer.setLayout(cardLayout);
        add(graphPanelContainer, BorderLayout.CENTER);

        JPanel defaultPanel = new JPanel();
        defaultPanel.setBackground(new Color(10, 10, 10));
        graphPanelContainer.add(defaultPanel, "default");

        dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBackground(new Color(10, 10, 10));
        graphPanelContainer.add(dataPanel, "data");

        btnGenerar.addActionListener(e -> updateGraph());

        btnCambiar.addActionListener(e -> {
            if (data != null) {
                if (!showingData) {
                    JTable table = new JTable(new DataModel(data));
                    table.setFont(font);
                    table.setRowHeight(25);
                    table.setBackground(new Color(10, 10, 10));
                    table.setForeground(Color.WHITE);
                    table.setGridColor(new Color(60, 60, 60));

                    JScrollPane scroll = new JScrollPane(table);
                    scroll.getViewport().setBackground(new Color(10, 10, 10));
                    scroll.setBackground(new Color(10, 10, 10));

                    dataPanel.removeAll();
                    dataPanel.add(scroll, BorderLayout.CENTER);

                    JPanel stats = new JPanel(new GridLayout(4, 1));
                    stats.setBackground(new Color(10, 10, 10));
                    stats.setForeground(Color.WHITE);

                    double media = Statistics.media(data, frecuencias);
                    double mediana = Statistics.mediana(data, frecuencias);
                    double moda = Statistics.moda(data, frecuencias);
                    double desv = Statistics.desviacion(data, frecuencias, media);

                    for (String label : List.of("Media: " + media, "Mediana: " + mediana, "Moda: " + moda, "Desviación: " + desv)) {
                        JLabel l = new JLabel(label);
                        l.setFont(font);
                        l.setForeground(Color.WHITE);
                        stats.add(l);
                    }
                    dataPanel.add(stats, BorderLayout.SOUTH);
                    cardLayout.show(graphPanelContainer, "data");
                    showingData = true;
                } else {
                    cardLayout.show(graphPanelContainer, "graph");
                    showingData = false;
                }
            }
        });

        cardLayout.show(graphPanelContainer, "default");
        setVisible(true);
    }

    private void updateTableSize() {
        try {
            int n = Integer.parseInt(txtClases.getText().trim());
            freqModel.setRowCount(n);
        } catch (NumberFormatException ignored) {}
    }

    private void updateGraph() {
        try {
            int n = Integer.parseInt(txtClases.getText().trim());
            String[] nums = txtDatos.getText().trim().split(",");
            if (nums.length < 3) return;

            double li1 = Double.parseDouble(nums[0].trim());
            double ls1 = Double.parseDouble(nums[1].trim());
            double li2 = Double.parseDouble(nums[2].trim());

            data = Statistics.generateData(li1, ls1, li2, n);

            frecuencias = IntStream.range(0, n)
                    .mapToObj(i -> {
                        Object v = freqModel.getValueAt(i, 0);
                        return v != null && !v.toString().isEmpty() ? Integer.parseInt(v.toString()) : 0;
                    }).toList();

            Histogram histo = new Histogram(data, frecuencias);
            histo.setPreferredSize(new Dimension(getWidth() - leftPanel.getWidth(), getHeight()));
            graphPanelContainer.add(histo, "graph");
            cardLayout.show(graphPanelContainer, "graph");
            showingData = false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos.");
        }
    }

    static class DataModel extends AbstractTableModel {
        private final String[] cols = {"Clase", "LRI", "Li", "Ls", "LRS", "Marca"};
        private final List<Object[]> rows;

        public DataModel(Map<Integer, List<Double>> data) {
            rows = new ArrayList<>();
            for (var entry : data.entrySet()) {
                var v = entry.getValue();
                rows.add(new Object[]{entry.getKey() + 1, v.get(0), v.get(1), v.get(2), v.get(3), v.get(4)});
            }
        }

        public int getRowCount() { return rows.size(); }
        public int getColumnCount() { return cols.length; }
        public Object getValueAt(int row, int col) { return rows.get(row)[col]; }
        public String getColumnName(int col) { return cols[col]; }
    }

    class ImagePanel extends JPanel {
        private BufferedImage img;

        public ImagePanel(String path) {
            try {
                img = ImageIO.read(getClass().getResourceAsStream("/resources/" + path));
            } catch (IOException | NullPointerException e) {
                System.err.println("No se pudo cargar la imagen: " + path);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
