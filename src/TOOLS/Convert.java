package TOOLS;

public class Convert {

    public void StrConvert() {}

    public static String mabConvert(String str) {
        return str.replaceAll("\u0098|\u009c", "\u00ac");
    }

}


