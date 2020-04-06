package TOOLS;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

public class Convert {

    public void StrConvert() {}

    public static String mabConvert(String str) {
        return str.replaceAll("\u0098|\u009c", "\u00ac");
    }

}


