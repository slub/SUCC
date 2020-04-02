package MAB;

public class MABConstants {

    // MAB all
    public static String partFieldDelimiter   = "‡";          // E2 80 A1
    public static String mabStichwortStart    = "\u007B";
    public static String mabStichwortEnde     = "\u007D";
    public static String mabNichtSortStart    = "\u0088";    // C2 98
    public static String mabNichtSortEnde     = "\u0089";    // C2 9C

    // MAB tape
    public static String recordDelimiter      = "\u001D";
    public static String fieldDelimiter       = "\u001E";
    public static String subFieldDelimiterT   = "\u001F";

    // MAB disc
    public static String subFieldDelimiterD   = "$";

    // MAB xml

        // <tf>
        // <ns> </ns>
        // <stw> </stw>

    // FieldTypes

    public static final int VALUE     = 0;
    public static final int PART      = 1;
    public static final int SUBFIELD  = 2;

    public static final int useAlways = 99999999;
}
