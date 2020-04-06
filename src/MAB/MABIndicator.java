package MAB;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.io.FileOutputStream;

public class MABIndicator {

    private String mabIndicator;

    public MABIndicator(String ind) {
        mabIndicator = ind;
    }

    public void dump() { System.out.print(mabIndicator); }

    public Integer length() { return mabIndicator.length(); }

    public String getMabIndicator() { return mabIndicator; }

    public void writeMABDisk(FileOutputStream fos) {
        try {
            fos.write(mabIndicator.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        try {
            fos.write(mabIndicator.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMabIndicator(String mabIndNew) {
        mabIndicator = mabIndNew;
    }
}
