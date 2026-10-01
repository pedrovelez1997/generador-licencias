package generadorlicencias;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorLicencias {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new VentanaGenerador().setVisible(true));
    }
    
    static class VentanaGenerador extends JFrame {
        
        private JTextField txtHardwareId;
        private JComboBox<String> cbDuracion;
        private JTextField txtClave;
        
        private final String[][] DURACIONES = {
            {"1 MINUTO (PRUEBA)", "1", "PRUEBA"},
            {"5 MINUTOS (PRUEBA)", "5", "PRUEBA"},
            {"30 días", "43200", "NORMAL"},
            {"60 días", "86400", "NORMAL"},
            {"90 días", "129600", "NORMAL"},
            {"180 días", "259200", "NORMAL"},
            {"1 año", "525600", "NORMAL"},
            {"2 años", "1051200", "NORMAL"},
            {"PERMANENTE", "0", "PERMANENTE"}
        };
        
        public VentanaGenerador() {
            setTitle("Generador de Licencias - COMERCIAL VELEZ");
            setSize(580, 480);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);
            
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Título
            JLabel titulo = new JLabel("GENERADOR DE LICENCIAS");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            panel.add(titulo, gbc);
            
            // Hardware ID
            gbc.gridy = 1; gbc.gridx = 0;
            gbc.gridwidth = 1;
            panel.add(new JLabel("Hardware ID del cliente:"), gbc);
            txtHardwareId = new JTextField(30);
            txtHardwareId.setFont(new Font("Monospaced", Font.PLAIN, 12));
            gbc.gridx = 1;
            panel.add(txtHardwareId, gbc);
            
            // Duración
            gbc.gridy = 2; gbc.gridx = 0;
            panel.add(new JLabel("Duración:"), gbc);
            String[] opciones = new String[DURACIONES.length];
            for (int i = 0; i < DURACIONES.length; i++) {
                opciones[i] = DURACIONES[i][0];
            }
            cbDuracion = new JComboBox<>(opciones);
            cbDuracion.setPreferredSize(new Dimension(250, 30));
            gbc.gridx = 1;
            panel.add(cbDuracion, gbc);
            
            // Botón Generar
            JButton btnGenerar = new JButton("GENERAR LICENCIA");
            btnGenerar.setBackground(new Color(76, 175, 80));
            btnGenerar.setForeground(Color.WHITE);
            btnGenerar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            gbc.gridy = 3; gbc.gridx = 0;
            gbc.gridwidth = 2;
            panel.add(btnGenerar, gbc);
            
            // Clave generada
            gbc.gridy = 4; gbc.gridx = 0;
            gbc.gridwidth = 1;
            panel.add(new JLabel("Clave generada:"), gbc);
            txtClave = new JTextField(50);
            txtClave.setEditable(false);
            txtClave.setFont(new Font("Monospaced", Font.BOLD, 12));
            txtClave.setForeground(new Color(76, 175, 80));
            gbc.gridx = 1;
            panel.add(txtClave, gbc);
            
            // Botón Copiar
            JButton btnCopiar = new JButton("Copiar clave");
            btnCopiar.setBackground(new Color(33, 150, 243));
            btnCopiar.setForeground(Color.WHITE);
            gbc.gridy = 5; gbc.gridx = 0;
            gbc.gridwidth = 2;
            panel.add(btnCopiar, gbc);
            
            // Información
            JLabel info = new JLabel("Formato: LIC-AÑO-MMDD-HHMM-MINUTOS-CHECKSUM-TIPO");
            info.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            info.setForeground(Color.GRAY);
            gbc.gridy = 6;
            panel.add(info, gbc);
            
            // Acciones
            btnGenerar.addActionListener(e -> generarClave());
            btnCopiar.addActionListener(e -> copiarClave());
            
            add(panel);
        }
        
        private void generarClave() {
            String hardwareId = txtHardwareId.getText().trim().toUpperCase();
            if (hardwareId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Ingrese el Hardware ID del cliente");
                return;
            }
            
            int idx = cbDuracion.getSelectedIndex();
            int minutos = Integer.parseInt(DURACIONES[idx][1]);
            String tipo = DURACIONES[idx][2];
            String duracionTexto = DURACIONES[idx][0];
            
            LocalDateTime ahora = LocalDateTime.now();
            int año = ahora.getYear();
            int mes = ahora.getMonthValue();
            int dia = ahora.getDayOfMonth();
            int hora = ahora.getHour();
            int minuto = ahora.getMinute();
            
            String fechaCodigo = String.format("%02d%02d", mes, dia);
            String horaCodigo = String.format("%02d%02d", hora, minuto);
            String minutosCodigo = String.format("%05d", minutos);
            
            // Calcular checksum
            String data = año + fechaCodigo + horaCodigo + minutosCodigo + tipo + hardwareId + "VELEZ2026";
            String checksum = Integer.toHexString(Math.abs(data.hashCode()) % 10000).toUpperCase();
            
            String clave = String.format("LIC-%d-%s-%s-%s-%s-%s", año, fechaCodigo, horaCodigo, minutosCodigo, checksum, tipo);
            txtClave.setText(clave);
            
            copiarClave();
            
            // Calcular fecha de expiración
            String expiracion;
            if (minutos == 0) {
                expiracion = "NUNCA (permanente)";
            } else {
                expiracion = LocalDateTime.now().plusMinutes(minutos).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            }
            
            JOptionPane.showMessageDialog(this,
                "✅ LICENCIA GENERADA\n\n" +
                "Clave: " + clave + "\n" +
                "Duración: " + duracionTexto + "\n" +
                "Expira: " + expiracion + "\n\n" +
                "📋 La clave se copió al portapapeles",
                "Licencia Generada",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void copiarClave() {
            String clave = txtClave.getText();
            if (!clave.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(clave), null);
                JOptionPane.showMessageDialog(this, "📋 Clave copiada!");
            }
        }
    }
}