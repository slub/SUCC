package CONV;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.util.HashMap;

public class Converter {
    private HashMap<Integer, Eintrag> convEntry = new HashMap<>();
    private HashMap<String, String> speicher = new HashMap<>();

    public Converter() {}

    public Integer setEntry(Integer pos, String line) {
        Boolean isLine = Boolean.TRUE;
        String[] lineArray = line.split("\\_");
        if (line.startsWith("#")) {
            // Zeile ist Kommentar -> ignorieren
            isLine = Boolean.FALSE;
        } else {
            if (lineArray.length < 5) {
                // Zeile zu kurz => ignorieren
                isLine = Boolean.FALSE;
            }
        }
        if (isLine) {
            convEntry.put(pos, new Eintrag(lineArray, this));
            return 1;
        }
        return 0;
    }

    public void dumpEntries() {
        for (Integer key : convEntry.keySet()) {
            System.out.print(key + " : ");
            convEntry.get(key).dump();
            System.out.println();
        }
    }

    public void dumpVariables() {
        for (String key : speicher.keySet()) {
            System.out.print(key + " : " + speicher.get(key));
            System.out.println();
        }
    }

    public String getSpeicher(String vN) {
        return speicher.get(vN);
    }

    public String getSpeicher(String vN, String defaultValue) {
        if (speicher.get(vN) == null) {
            return defaultValue;
        } else {
            return speicher.get(vN);
        }
    }

    public Boolean setSpeicher(String vN, String vV) {
        if (speicher.containsKey(vN)) {
            speicher.replace(vN, vV);
            return Boolean.TRUE;
        } else {
            speicher.put(vN, vV);
            return Boolean.TRUE;
        }
    }

    public Eintrag[] getEntries() {
        Eintrag[] eintrag = new Eintrag[convEntry.size()];
        Integer count = 0;
        for (Integer key : convEntry.keySet()) {
            eintrag[count++] = convEntry.get(key);
        }
        return eintrag;
    }
}
