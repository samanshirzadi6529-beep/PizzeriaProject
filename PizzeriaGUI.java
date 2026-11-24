import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PizzeriaGUI extends JFrame {

    private static final String CSV_DATEI = "bestellungen.csv";
    private static final String CSV_KOPF =
            "Kundenname;Pizza;Groesse;ExtraKaese;Gesamtpreis";

    private static final String KARTE_BESTELLUNG = "bestellung";
    private static final String KARTE_UEBERSICHT = "uebersicht";

    private static final Color WEINROT = new Color(128, 32, 48);
    private static final Color DUNKELROT = new Color(94, 23, 36);
    private static final Color HELL = new Color(248, 246, 242);
    private static final Color TEXT = new Color(48, 43, 40);
    private static final Color GOLD = new Color(201, 157, 75);

    private final JTextField nameFeld = new JTextField(18);
    private final JComboBox<String> pizzaBox =
            new JComboBox<>(new String[]{
                    "Margherita", "Salami", "Funghi",
                    "Napoli", "Tonno", "Hawaii", "Chef"});
    private final JLabel pizzaLabel = new JLabel("", SwingConstants.CENTER);
    private final JRadioButton kleinButton = new JRadioButton("Klein", true);
    private final JRadioButton mittelButton = new JRadioButton("Mittel");
    private final JRadioButton grossButton = new JRadioButton("Groß");
    private final JCheckBox kaeseBox =
            new JCheckBox("Extra Käse (+1,00 €)");

    private final JLabel preisLabel = new JLabel("0,00 €");
    private final JLabel zusammenfassungLabel =
            new JLabel("<html>Noch keine Bestellung berechnet.</html>");
    private final JTextArea statusArea = new JTextArea(5, 30);

    private final JLabel logoLabel = new JLabel("", SwingConstants.CENTER);


    private final CardLayout cardLayout = new CardLayout();
    private final JPanel kartenPanel = new JPanel(cardLayout);

    public PizzeriaGUI() {
        super("Luigi's Pizzeria");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 570));

        JPanel hauptPanel = new JPanel(new BorderLayout());
        hauptPanel.setBackground(HELL);
        setContentPane(hauptPanel);

        hauptPanel.add(baueSeitenleiste(), BorderLayout.WEST);

        kartenPanel.setBackground(HELL);
        kartenPanel.add(baueBestellseite(), KARTE_BESTELLUNG);
        kartenPanel.add(baueUebersichtsseite(), KARTE_UEBERSICHT);
        hauptPanel.add(kartenPanel, BorderLayout.CENTER);

        ladeBild(logoLabel, "/Image/logo.png", 250, 140, "LUIGI'S PIZZERIA");
        ladeBild(pizzaLabel, "/Image/pizza.png", 290, 220, "Pizzabild fehlt");

        pizzaBox.addActionListener(e -> aktualisierePizzaBild());
        aktualisierePizzaBild();

        pack();
        setSize(Math.max(getWidth(), 900), Math.max(getHeight(), 600));
        setLocationRelativeTo(null);

        zeigeKarte(KARTE_BESTELLUNG);
    }
    private void aktualisierePizzaBild() {
        String pizza = (String) pizzaBox.getSelectedItem();

        if (pizza == null) {
            return;
        }

        String dateiname = switch (pizza) {
            case "Margherita" -> "/Image/margherita.png";
            case "Salami"     -> "/Image/salami.png";
            case "Funghi"     -> "/Image/funghi.png";
            case "Napoli"     -> "/Image/napoli.png";
            case "Tonno"      -> "/Image/tonno.png";
            case "Hawaii"     -> "/Image/hawaii.png";
            case "Chef"       -> "/Image/chef.png";
            default           -> "/Image/pizza.png";
        };

        ladeBild(pizzaLabel, dateiname, 290, 220, "Kein Bild verfügbar");
    }

    private JPanel baueSeitenleiste() {
        JPanel seitenleiste = new JPanel();
        seitenleiste.setBackground(WEINROT);
        seitenleiste.setPreferredSize(new Dimension(205, 0));
        seitenleiste.setBorder(new EmptyBorder(24, 16, 24, 16));
        seitenleiste.setLayout(new BoxLayout(seitenleiste, BoxLayout.Y_AXIS));

        JLabel marke = new JLabel("LUIGI'S");
        marke.setForeground(Color.WHITE);
        marke.setFont(new Font("SansSerif", Font.BOLD, 25));
        marke.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel untertitel = new JLabel("PIZZERIA");
        untertitel.setForeground(new Color(240, 220, 185));
        untertitel.setFont(new Font("SansSerif", Font.BOLD, 13));
        untertitel.setAlignmentX(Component.CENTER_ALIGNMENT);

        seitenleiste.add(marke);
        seitenleiste.add(Box.createVerticalStrut(4));
        seitenleiste.add(untertitel);
        seitenleiste.add(Box.createVerticalStrut(42));

        JButton bestellungButton = baueSeitenButton("Neue Bestellung");
        JButton uebersichtButton = baueSeitenButton("Bestellübersicht");
        JButton beendenButton = baueSeitenButton("Beenden");

        bestellungButton.addActionListener(e -> zeigeKarte(KARTE_BESTELLUNG));
        uebersichtButton.addActionListener(e -> aktualisiereUebersicht());
        beendenButton.addActionListener(e -> dispose());

        seitenleiste.add(bestellungButton);
        seitenleiste.add(Box.createVerticalStrut(12));
        seitenleiste.add(uebersichtButton);
        seitenleiste.add(Box.createVerticalGlue());
        seitenleiste.add(beendenButton);

        return seitenleiste;
    }

    private JButton baueSeitenButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setBackground(DUNKELROT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(157, 71, 82)),
                new EmptyBorder(10, 12, 10, 12)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel baueBestellseite() {
        JPanel seite = new JPanel(new BorderLayout(18, 18));
        seite.setBackground(HELL);
        seite.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel kopf = new JPanel(new BorderLayout(16, 0));
        kopf.setOpaque(false);
        kopf.add(logoLabel, BorderLayout.WEST);

        JPanel titelPanel = new JPanel();
        titelPanel.setOpaque(false);
        titelPanel.setLayout(new BoxLayout(titelPanel, BoxLayout.Y_AXIS));

        JLabel titel = new JLabel("Willkommen bei Luigi's");
        titel.setFont(new Font("SansSerif", Font.BOLD, 25));
        titel.setForeground(TEXT);

        JLabel erklaerung = new JLabel("Stelle deine Bestellung zusammen.");
        erklaerung.setFont(new Font("SansSerif", Font.PLAIN, 14));
        erklaerung.setForeground(new Color(115, 105, 98));

        titelPanel.add(titel);
        titelPanel.add(Box.createVerticalStrut(5));
        titelPanel.add(erklaerung);
        kopf.add(titelPanel, BorderLayout.CENTER);
        seite.add(kopf, BorderLayout.NORTH);

        JPanel inhalt = new JPanel(new GridLayout(1, 2, 20, 0));
        inhalt.setOpaque(false);
        inhalt.add(baueFormularPanel());
        inhalt.add(baueProduktPanel());
        seite.add(inhalt, BorderLayout.CENTER);

        JPanel unten = new JPanel(new BorderLayout(12, 8));
        unten.setOpaque(false);
        unten.add(baueAktionsPanel(), BorderLayout.NORTH);

        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusArea.setBackground(Color.WHITE);
        statusArea.setForeground(TEXT);
        statusArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setPreferredSize(new Dimension(400, 72));
        statusScroll.setBorder(BorderFactory.createTitledBorder("Meldungen"));
        unten.add(statusScroll, BorderLayout.CENTER);

        seite.add(unten, BorderLayout.SOUTH);
        return seite;
    }

    private JPanel baueFormularPanel() {
        JPanel karte = baueKarte("Deine Bestellung");
        JPanel formular = new JPanel(new GridBagLayout());
        formular.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 5, 9, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        nameFeld.setFont(new Font("SansSerif", Font.PLAIN, 15));
        pizzaBox.setFont(new Font("SansSerif", Font.PLAIN, 15));

        addFormularZeile(formular, gbc, 0, "Kundenname:", nameFeld);
        addFormularZeile(formular, gbc, 1, "Pizza:", pizzaBox);

        ButtonGroup groessenGruppe = new ButtonGroup();
        groessenGruppe.add(kleinButton);
        groessenGruppe.add(mittelButton);
        groessenGruppe.add(grossButton);

        JPanel groessenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        groessenPanel.setOpaque(false);
        for (JRadioButton button :
                new JRadioButton[]{kleinButton, mittelButton, grossButton}) {
            button.setOpaque(false);
            groessenPanel.add(button);
        }
        addFormularZeile(formular, gbc, 2, "Größe:", groessenPanel);

        kaeseBox.setOpaque(false);
        addFormularZeile(formular, gbc, 3, "Extra:", kaeseBox);

        karte.add(formular, BorderLayout.CENTER);
        return karte;
    }

    private void addFormularZeile(
            JPanel panel, GridBagConstraints gbc, int zeile, String titel, Component feld) {

        gbc.gridy = zeile;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel label = new JLabel(titel);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(feld, gbc);
    }

    private JPanel baueProduktPanel() {
        JPanel karte = baueKarte("Unsere Pizza");
        JPanel innen = new JPanel(new BorderLayout(5, 12));
        innen.setOpaque(false);

        JLabel beschreibung = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "<b>Frisch aus dem Ofen</b><br>"
                        + "Wähle deine Lieblingspizza und Größe."
                        + "</div></html>",
                SwingConstants.CENTER);
        beschreibung.setForeground(TEXT);
        beschreibung.setFont(new Font("SansSerif", Font.PLAIN, 14));

        innen.add(beschreibung, BorderLayout.NORTH);
        innen.add(pizzaLabel, BorderLayout.CENTER);
        karte.add(innen, BorderLayout.CENTER);
        return karte;
    }

    private JPanel baueAktionsPanel() {
        JPanel aktionen = new JPanel(new BorderLayout(14, 0));
        aktionen.setOpaque(false);

        JPanel preisPanel = new JPanel();
        preisPanel.setOpaque(false);
        preisPanel.setLayout(new BoxLayout(preisPanel, BoxLayout.Y_AXIS));

        JLabel preisTitel = new JLabel("GESAMTPREIS");
        preisTitel.setForeground(new Color(115, 105, 98));
        preisTitel.setFont(new Font("SansSerif", Font.BOLD, 11));

        preisLabel.setForeground(WEINROT);
        preisLabel.setFont(new Font("SansSerif", Font.BOLD, 25));

        preisPanel.add(preisTitel);
        preisPanel.add(Box.createVerticalStrut(3));
        preisPanel.add(preisLabel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        buttons.setOpaque(false);

        JButton berechnenButton = baueAktionsButton("Preis berechnen", false);
        JButton speichernButton = baueAktionsButton("Bestellung speichern", true);
        JButton leerenButton = baueAktionsButton("Leeren", false);

        berechnenButton.addActionListener(e -> berechnen());
        speichernButton.addActionListener(e -> speichern());
        leerenButton.addActionListener(e -> leeren());

        buttons.add(leerenButton);
        buttons.add(berechnenButton);
        buttons.add(speichernButton);

        aktionen.add(preisPanel, BorderLayout.WEST);
        aktionen.add(buttons, BorderLayout.CENTER);
        return aktionen;
    }

    private JButton baueAktionsButton(String text, boolean primaer) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 14, 11, 14));

        if (primaer) {
            button.setBackground(WEINROT);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(WEINROT);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 210, 198)),
                    new EmptyBorder(10, 13, 10, 13)));
        }
        return button;
    }

    private JPanel baueUebersichtsseite() {
        JPanel seite = new JPanel(new BorderLayout(18, 18));
        seite.setBackground(HELL);
        seite.setBorder(new EmptyBorder(30, 32, 30, 32));

        JLabel titel = new JLabel("Bestellübersicht");
        titel.setFont(new Font("SansSerif", Font.BOLD, 27));
        titel.setForeground(TEXT);
        seite.add(titel, BorderLayout.NORTH);

        JPanel karte = baueKarte("Aktuelle Auswahl");
        zusammenfassungLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        zusammenfassungLabel.setForeground(TEXT);
        karte.add(zusammenfassungLabel, BorderLayout.CENTER);
        seite.add(karte, BorderLayout.CENTER);

        JButton zurueckButton = baueAktionsButton("Zur Bestellung", true);
        zurueckButton.addActionListener(e -> zeigeKarte(KARTE_BESTELLUNG));

        JPanel fuss = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        fuss.setOpaque(false);
        fuss.add(zurueckButton);
        seite.add(fuss, BorderLayout.SOUTH);

        return seite;
    }

    private JPanel baueKarte(String titel) {
        JPanel karte = new JPanel(new BorderLayout(10, 12));
        karte.setBackground(Color.WHITE);
        karte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 226, 218)),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel ueberschrift = new JLabel(titel);
        ueberschrift.setFont(new Font("SansSerif", Font.BOLD, 17));
        ueberschrift.setForeground(WEINROT);
        karte.add(ueberschrift, BorderLayout.NORTH);

        return karte;
    }

    private Bestellung aktuelleBestellung() {
        String groesse = mittelButton.isSelected() ? "Mittel"
                : grossButton.isSelected() ? "Groß" : "Klein";

        return new Bestellung(
                nameFeld.getText(),
                (String) pizzaBox.getSelectedItem(),
                groesse,
                kaeseBox.isSelected());
    }

    private void berechnen() {
        Bestellung bestellung = aktuelleBestellung();
        preisLabel.setText(bestellung.getPreisText() + " €");
        aktualisiereZusammenfassung(bestellung);
        meldung("Preis berechnet: " + bestellung.getPreisText() + " €");
    }

    private void speichern() {
        Bestellung bestellung = aktuelleBestellung();
        String fehler = bestellung.pruefe();

        if (fehler != null) {
            meldung("FEHLER: " + fehler);
            JOptionPane.showMessageDialog(
                    this, fehler, "Eingabe prüfen", JOptionPane.WARNING_MESSAGE);
            return;
        }

        preisLabel.setText(bestellung.getPreisText() + " €");
        aktualisiereZusammenfassung(bestellung);

        Path datei = Paths.get(CSV_DATEI);
        try {
            boolean neu = !Files.exists(datei) || Files.size(datei) == 0;
            StringBuilder text = new StringBuilder();

            if (neu) {
                text.append(CSV_KOPF).append(System.lineSeparator());
            }
            text.append(bestellung.toCsvZeile()).append(System.lineSeparator());

            Files.write(datei, text.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            meldung("Bestellung gespeichert. Datei: " + datei.toAbsolutePath());
            JOptionPane.showMessageDialog(
                    this, "Die Bestellung wurde gespeichert.",
                    "Gespeichert", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            meldung("FEHLER beim Speichern: " + ex.getMessage());
            JOptionPane.showMessageDialog(
                    this, "Die Bestellung konnte nicht gespeichert werden:\n"
                            + ex.getMessage(),
                    "Speicherfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void leeren() {
        nameFeld.setText("");
        pizzaBox.setSelectedIndex(0);
        kleinButton.setSelected(true);
        kaeseBox.setSelected(false);
        preisLabel.setText("0,00 €");
        zusammenfassungLabel.setText("<html>Noch keine Bestellung berechnet.</html>");
        meldung("Formular geleert.");
    }

    private void aktualisiereUebersicht() {
        aktualisiereZusammenfassung(aktuelleBestellung());
        zeigeKarte(KARTE_UEBERSICHT);
    }

    private void aktualisiereZusammenfassung(Bestellung bestellung) {
        String kaese = kaeseBox.isSelected() ? "Ja" : "Nein";
        zusammenfassungLabel.setText(
                "<html><div style='line-height:1.8;'>"
                        + "<b>Kundenname:</b> " + html(bestellung.getKundenname()) + "<​br>"
                        + "<b>Pizza:</b> " + html(bestellung.getPizza()) + "<​br>"
                        + "<b>Größe:</b> " + html(bestellung.getGroesse()) + "<​br>"
                        + "<b>Extra Käse:</b> " + kaese + "<​br>"
                        + "<b>Gesamtpreis:</b> "
                        + bestellung.getPreisText() + " €"
                        + "</div></html>");
    }

    private String html(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<​", "&lt;")
                .replace(">", "&gt;");
    }

    private void zeigeKarte(String karte) {
        cardLayout.show(kartenPanel, karte);
    }

    private void meldung(String text) {
        statusArea.append(text + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }

    private void ladeBild(
            JLabel label, String dateiname, int maxBreite, int maxHoehe, String ersatzText) {

        URL url = getClass().getResource(dateiname);
        if (url == null) {
            label.setText(ersatzText);
            return;
        }

        try {
            BufferedImage original = ImageIO.read(url);
            if (original == null) {
                label.setText(ersatzText);
                return;
            }

            double faktor = Math.min(
                    (double) maxBreite / original.getWidth(),
                    (double) maxHoehe / original.getHeight());
            faktor = Math.min(faktor, 1.0);

            int breite = Math.max(1, (int) (original.getWidth() * faktor));
            int hoehe = Math.max(1, (int) (original.getHeight() * faktor));

            BufferedImage skaliert =
                    new BufferedImage(breite, hoehe, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = skaliert.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(original, 0, 0, breite, hoehe, null);
            g.dispose();

            label.setText("");
            label.setIcon(new ImageIcon(skaliert));
        } catch (IOException ex) {
            label.setText(ersatzText);
        }
    }
}