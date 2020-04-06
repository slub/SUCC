package MAB;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.io.FileOutputStream;

public class MABPartField {

    private String value;

    public MABPartField(String part) {
        value = part;
    }

    public void dump() {
        System.out.print("   ");
        System.out.println(value);
    }

    public Integer length() {
        return value.length() + 1;
    }

    public void writeMABDisk(FileOutputStream fos) {
        try {
            fos.write((value + MABConstants.partFieldDelimiter).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        try {
            fos.write((value + MABConstants.partFieldDelimiter).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
