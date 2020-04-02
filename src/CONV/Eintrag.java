package CONV;

import java.util.Arrays;
import java.util.List;

public class Eintrag {
        private String useOn;
        private Bedingung bedingung;
        private Boolean useAlways = Boolean.FALSE;
        private Action aktion;
        private SrcDst quelle;
        private SrcDst ziel;

        public Eintrag(String use, String cond, String act, String src, String dst, Converter conv) {
            String[] fileTyp = new String[]{"l","n","m","p","s","t","c","d","e","k"};
            List<String> useList = Arrays.asList(fileTyp);
            if (!(useList.contains(use))) {
                useOn = "*";
            } else {
                useOn = use;
            }
            if (cond.length() > 1) {
                bedingung = new Bedingung(cond);
            } else {
                useAlways = Boolean.TRUE;
            }
            aktion = new Action(act);
            quelle = new SrcDst(src, conv);
            ziel = new SrcDst(dst, conv);
        }

        public Eintrag(String[] line, Converter conv) {
            this(line[0], line[1], line[2], line[3], line[4], conv);
        }

        public void dump() {
            System.out.print(useOn);
            System.out.print("_");
            if (useAlways) {
                System.out.print("*");
            } else {
                bedingung.dump();
            }
            System.out.print("_");
            aktion.dump();
            System.out.print("_");
            quelle.dump();
            System.out.print("_");
            ziel.dump();
            System.out.print("_");
        }

        public Boolean useAlways() { return useAlways; }

        public String getUseOn() { return useOn; }

        public Bedingung getBedingung() { return bedingung; }

        public Action getAction() { return aktion; }

        public SrcDst getSRC() { return quelle; }

        public SrcDst getDST() { return ziel; }

}
