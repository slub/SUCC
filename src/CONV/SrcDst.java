package CONV;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class SrcDst {

    private Boolean isMabField = Boolean.FALSE;
    private Boolean isVariable = Boolean.FALSE;
    private Boolean isValue = Boolean.FALSE;
    private Boolean isSystem = Boolean.FALSE;

    public static final int IS_UNKNOWN  = 0;
    public static final int IS_MABFIELD = 1;
    public static final int IS_VARIABLE = 2;
    public static final int IS_VALUE    = 3;
    public static final int IS_SYSTEM   = 4;

    private Integer isTyp = 0;
    // 0 ... n/a
    // 1 ... MabFeld
    // 2 ... Variable
    // 3 ... Wert
    // 4 ... Systemvariable

    private MABfield mabFeld;
    private String myVariable;
    private String myValue;
    private String mySystem;

    public SrcDst(String s, Converter conv) {
        switch(s.substring(0,1)) {
            case "W": {
                isValue = Boolean.TRUE;
                isTyp = IS_VALUE;
                myValue = s.substring(1);
                break;
            }
            case "V": {
                isVariable = Boolean.TRUE;
                isTyp = IS_VARIABLE;
                conv.setSpeicher(s.substring(1), "");
                myVariable = s.substring(1);
                break;
            }
            case "C": {
                isSystem = Boolean.TRUE;
                isTyp = IS_SYSTEM;
                Date date = new Date(System.currentTimeMillis());
                switch (s.substring(1)) {
                    case "today" : {
                        SimpleDateFormat formToday = new SimpleDateFormat("yyyyMMddHHmmss");
                        mySystem = formToday.format(date);
                        break;
                    }
                    case "date" : {
                        SimpleDateFormat formDate = new SimpleDateFormat("yyyyMMdd");
                        mySystem = formDate.format(date);
                        break;
                    }
                    case "time" : {
                        SimpleDateFormat formTime = new SimpleDateFormat("HHmmss");
                        mySystem = formTime.format(date);
                        break;
                    }
                }
                break;
            }
            case "0": {}
            case "1": {}
            case "2": {}
            case "3": {}
            case "4": {}
            case "5": {}
            case "6": {}
            case "7": {}
            case "8": {}
            case "9": {
                isMabField = Boolean.TRUE;
                isTyp = IS_MABFIELD;
                mabFeld = new MABfield(s);
                break;
            }
            default: {
                isTyp = IS_UNKNOWN;
            }
        }
    }

    public void dump() {
        switch(isTyp) {
            case 0: {
                break;
            }
            case 1: {
                mabFeld.dump();
                break;
            }
            case 2: {
                System.out.print("V" + myVariable);
                break;
            }
            case 3: {
                System.out.print("W" + myValue);
                break;
            }
        }
    }

    public Integer getTyp() { return isTyp; }

    public MABfield getMabField() { return mabFeld; }

    public String getVariable() { return myVariable; }

    public String getValue() { return myValue; }

    public String getSystem() { return mySystem; }
}
