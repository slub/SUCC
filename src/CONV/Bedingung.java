package CONV;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

public class Bedingung {
    private MABfield mabFeld;
    private String condVariable = "xyz";
    private Operator operator;
    private String wert;

    public Bedingung(String cond) {
        String[] bedgg = cond.split("\\.");
        if (bedgg[0].startsWith("V")) {
            condVariable = bedgg[0].substring(1);
        } else {
            mabFeld = new MABfield(bedgg[0]);
        }
        operator = new Operator(bedgg[1]);
        if (bedgg.length>2) {
            wert = bedgg[2];
        } else {
            wert = "";
        }
    }

    public void dump() {
        if (condVariable.equals("xyz")) {
            mabFeld.dump();
        } else{
            System.out.print("V" + condVariable);
        }
        System.out.print(".");
        operator.dump();
        System.out.print("." + wert);
    }

    public MABfield getMabFeld() { return mabFeld; }

    public Operator getOperator() { return operator; }

    public String getWert() { return wert; }

    public String getVariable() { return condVariable; }
}
