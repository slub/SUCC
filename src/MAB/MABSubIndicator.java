package MAB;

import java.io.FileOutputStream;

public class MABSubIndicator {

    private String mabSubIndicator;

    public MABSubIndicator(String ind) {
        mabSubIndicator = ind;
    }

    public void dump() { System.out.print(mabSubIndicator); }

    public Integer length() { return mabSubIndicator.length() + 1; }

    public void writeMABDisk(FileOutputStream fos) {
        try {
            fos.write((MABConstants.subFieldDelimiterD + mabSubIndicator).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        try {
            fos.write((MABConstants.subFieldDelimiterD + mabSubIndicator).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSubIndicator() { return mabSubIndicator; }
}
