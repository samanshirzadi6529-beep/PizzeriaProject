import javax.swing.SwingUtilities;

// Startklasse: erzeugt das Fenster und zeigt es an
public class Main {
    public static void main(String[] args) {
        // Swing-Oberflächen müssen im Event-Dispatch-Thread erstellt werden
        SwingUtilities.invokeLater(() -> {
            PizzeriaGUI gui = new PizzeriaGUI(); // Objekt der GUI-Klasse erstellen
            gui.setVisible(true);                // Fenster sichtbar machen
        });
    }
}