import java.util.Locale;

// Datenklasse: speichert eine Bestellung und berechnet den Preis
public class Bestellung {

    // Eigenschaften der Bestellung (nach dem Erstellen unveränderlich)
    private final String kundenname;
    private final String pizza;
    private final String groesse;
    private final boolean extraKaese;

    // Konstruktor: übernimmt die Eingaben aus der GUI
    public Bestellung(String kundenname, String pizza, String groesse, boolean extraKaese) {
        // trim() entfernt Leerzeichen am Anfang/Ende; null wird zu leerem Text
        this.kundenname = kundenname == null ? "" : kundenname.trim();
        this.pizza = pizza;
        this.groesse = groesse;
        this.extraKaese = extraKaese;
    }

    // Preisberechnung in Cent (int), damit es keine Rundungsfehler wie bei double gibt
    public int berechnePreisCent() {
        int preis;

        // Grundpreis je nach Pizza
        switch (pizza) {
            case "Margherita": preis = 800;  break;
            case "Salami":     preis = 1000; break;
            case "Funghi":     preis = 900;  break;
            case "Napoli":     preis = 950;  break;
            case "Tonno":      preis = 1050; break;
            case "Hawaii":     preis = 1000; break;
            case "Chef":       preis = 1200; break;
            default:           preis = 0;
        }
        // Zuschlag je nach Größe (Klein = Grundpreis)
        if ("Mittel".equals(groesse)) {
            preis += 200;  // +2,00 €
        } else if ("Groß".equals(groesse)) {
            preis += 400;  // +4,00 €
        }

        // Zuschlag für Extra Käse
        if (extraKaese) {
            preis += 100;  // +1,00 €
        }
        return preis;
    }

    // Wandelt Cent in Text um, z. B. 1100 -> "11,00"
    public String getPreisText() {
        int cent = berechnePreisCent();
        return String.format(Locale.GERMANY, "%d,%02d", cent / 100, cent % 100);
    }

    // Prüft die Eingaben. null = alles in Ordnung, sonst die Fehlermeldung
    public String pruefe() {
        if (kundenname.isEmpty()) {
            return "Bitte einen Kundennamen eingeben.";
        }
        // Semikolon und Zeilenumbruch würden die CSV-Datei zerstören
        if (kundenname.contains(";") || kundenname.contains("\n") || kundenname.contains("\r")) {
            return "Der Name darf kein Semikolon und keinen Zeilenumbruch enthalten.";
        }
        return null;
    }

    // Erzeugt eine CSV-Zeile: Kundenname;Pizza;Groesse;ExtraKaese;Gesamtpreis
    public String toCsvZeile() {
        return kundenname + ";" + pizza + ";" + groesse + ";"
                + (extraKaese ? "Ja" : "Nein") + ";" + getPreisText();
    }

    // Getter
    public String getKundenname() { return kundenname; }
    public String getPizza()      { return pizza; }
    public String getGroesse()    { return groesse; }
    public boolean isExtraKaese() { return extraKaese; }
}