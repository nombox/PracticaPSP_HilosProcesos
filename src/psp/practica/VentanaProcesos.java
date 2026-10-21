package psp.practica;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VentanaProcesos extends JFrame {

    private static final String COMANDOS_EJEMPLO = String.join("\n",
            "ping -n 4 localhost",
            "ping -n 2 127.0.0.1",
            "ipconfig",
            "cmd /c dir",
            "cmd /c dir carpetaQueNoExiste",
            "ping -n 3 localhost",
            "programaInventado",
            "ping -n 5 localhost");

    private final JTextField txtComando = new JTextField("ping", 30);
    private final JTextField txtArgumentos = new JTextField("-n 5 localhost", 30);
    private final JTextField txtFichero = new JTextField(30);
    private final JLabel lblFin = new JLabel("Código de salida: -");
    private final JLabel lblEstadoHilo = new JLabel("Estado del hilo: -");
    private final JLabel lblResultado = new JLabel();
    private final StringBuilder textoSalida = new StringBuilder();
    private Thread hiloActual;

    private final JTextArea txtComandos = new JTextArea(COMANDOS_EJEMPLO, 8, 40);
    private final JSpinner spnPool = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
    private final JSpinner spnMaximo = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
    private final JLabel lblContadores = new JLabel();
    private final JLabel lblTodos = new JLabel(" ");
    private final JTextArea txtSalidaVarios = new JTextArea();

    private record Peticion(String comando, String[] argumentos, File fichero) {
    }

    public VentanaProcesos() {
        super("PSP - Procesos e Hilos");

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Procesos", crearPestanaProcesos());
        pestanas.addTab("Varios procesos", crearPestanaVarios());
        add(pestanas);

        new Timer(200, e -> lblEstadoHilo.setText("Estado del hilo: " + GestorHilos.estado(hiloActual))).start();
        pintarContadores(0, 0, 0, 0);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
    }

    private JPanel crearPestanaProcesos() {
        JButton btnExaminar = new JButton("Examinar...");
        JButton btnParte1 = new JButton("Ejecutar (Parte 1)");
        JButton btnEnHilo = new JButton("Ejecutar en hilo (Parte 2)");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnLog = new JButton("Abrir errores.log");

        btnExaminar.addActionListener(e -> elegirFichero());
        btnParte1.addActionListener(e -> ejecutarParte1());
        btnEnHilo.addActionListener(e -> ejecutarEnHilo());
        btnCancelar.addActionListener(e -> GestorHilos.cancelar(hiloActual));
        btnLog.addActionListener(e -> abrirLog());

        JPanel formulario = new JPanel(new GridLayout(5, 1));
        formulario.add(fila("Proceso:", txtComando));
        formulario.add(fila("Argumentos:", txtArgumentos));
        formulario.add(fila("Fichero entrada:", txtFichero, btnExaminar));
        formulario.add(fila("", btnParte1, btnEnHilo, btnCancelar, btnLog));
        formulario.add(fila("", lblFin, new JLabel("     "), lblEstadoHilo));

        lblResultado.setVerticalAlignment(SwingConstants.TOP);
        lblResultado.setOpaque(true);
        lblResultado.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(lblResultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Salida estándar del proceso"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(formulario, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaVarios() {
        JButton btnLanzar = new JButton("Lanzar todos");
        JButton btnLog = new JButton("Abrir errores.log");
        btnLanzar.addActionListener(e -> lanzarVarios());
        btnLog.addActionListener(e -> abrirLog());

        JScrollPane scrollComandos = new JScrollPane(txtComandos);
        scrollComandos.setBorder(BorderFactory.createTitledBorder("Comandos (uno por línea)"));

        JPanel controles = new JPanel(new GridLayout(3, 1));
        controles.add(fila("Hilos del pool:", spnPool, new JLabel("   Máx. simultáneos:"), spnMaximo, btnLanzar, btnLog));
        controles.add(fila("", lblContadores));
        controles.add(fila("", lblTodos));

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(scrollComandos, BorderLayout.CENTER);
        norte.add(controles, BorderLayout.SOUTH);

        txtSalidaVarios.setEditable(false);
        txtSalidaVarios.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollSalida = new JScrollPane(txtSalidaVarios);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Actividad"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(norte, BorderLayout.NORTH);
        panel.add(scrollSalida, BorderLayout.CENTER);
        return panel;
    }

    private JPanel fila(String texto, JComponent... componentes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setPreferredSize(new Dimension(125, 20));
        p.add(etiqueta);
        for (JComponent c : componentes) p.add(c);
        return p;
    }

    private Peticion leerFormulario() {
        String comando = txtComando.getText().trim();
        if (comando.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indica el nombre del proceso");
            return null;
        }
        String textoArgs = txtArgumentos.getText().trim();
        String[] argumentos = textoArgs.isEmpty() ? new String[0] : textoArgs.split("\\s+");
        String ruta = txtFichero.getText().trim();
        File fichero = ruta.isEmpty() ? null : new File(ruta);
        return new Peticion(comando, argumentos, fichero);
    }

    private void ejecutarParte1() {
        Peticion p = leerFormulario();
        if (p == null) return;
        textoSalida.setLength(0);
        try {
            ResultadoProceso r = GestorProcesos.ejecutar(p.comando(), p.argumentos(), p.fichero());
            if (r == null) {
                lblFin.setText("GestorProcesos.ejecutar() devuelve null");
            } else {
                lblFin.setText("Código de salida: " + r.codigoSalida());
                textoSalida.append(r.salida());
            }
        } catch (IOException | InterruptedException ex) {
            lblFin.setText("Código de salida: ERROR");
            textoSalida.append("No se pudo ejecutar el proceso:\n").append(ex.getMessage());
        }
        pintarSalida();
    }

    private void ejecutarEnHilo() {
        if (hiloActual != null && hiloActual.isAlive()) {
            JOptionPane.showMessageDialog(this, "Ya hay un proceso en marcha");
            return;
        }
        Peticion p = leerFormulario();
        if (p == null) return;
        textoSalida.setLength(0);
        pintarSalida();
        lblFin.setText("Ejecutando...");
        hiloActual = GestorHilos.lanzarEnHilo(p.comando(), p.argumentos(), p.fichero(), this);
    }

    private void lanzarVarios() {
        List<String> comandos = new ArrayList<>();
        for (String linea : txtComandos.getText().split("\\R")) {
            if (!linea.isBlank()) comandos.add(linea.trim());
        }
        if (comandos.isEmpty()) return;
        txtSalidaVarios.setText("");
        lblTodos.setText("En curso...");
        pintarContadores(0, 0, 0, 0);
        new LanzadorMultiple(this).lanzarTodos(comandos, (Integer) spnPool.getValue(), (Integer) spnMaximo.getValue());
    }

    public void anadirSalida(String linea) {
        SwingUtilities.invokeLater(() -> {
            textoSalida.append(linea).append('\n');
            pintarSalida();
        });
    }

    public void mostrarFin(String texto) {
        SwingUtilities.invokeLater(() -> lblFin.setText(texto));
    }

    public void anadirActividad(String texto) {
        SwingUtilities.invokeLater(() -> txtSalidaVarios.append(texto + "\n"));
    }

    public void actualizarContadores(int lanzados, int enEjecucion, int terminados, int conError) {
        SwingUtilities.invokeLater(() -> pintarContadores(lanzados, enEjecucion, terminados, conError));
    }

    public void todosTerminados() {
        SwingUtilities.invokeLater(() -> lblTodos.setText("TODOS LOS PROCESOS HAN TERMINADO"));
    }

    private void pintarContadores(int lanzados, int enEjecucion, int terminados, int conError) {
        lblContadores.setText("Lanzados: " + lanzados + "   |   En ejecución: " + enEjecucion
                + "   |   Terminados: " + terminados + "   |   Con error: " + conError);
    }

    private void pintarSalida() {
        String escapado = textoSalida.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        lblResultado.setText("<html><pre>" + escapado + "</pre></html>");
    }

    private void elegirFichero() {
        JFileChooser selector = new JFileChooser(new File("."));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtFichero.setText(selector.getSelectedFile().getAbsolutePath());
        }
    }

    private void abrirLog() {
        try {
            Desktop.getDesktop().open(Log.FICHERO);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se puede abrir el log: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaProcesos().setVisible(true));
    }
}
