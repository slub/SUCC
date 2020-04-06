package MAB;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.io.FileOutputStream;

public class MABSubField {

    private MABSubIndicator mabSubIndicator;
    private String mabSubValue;

    public MABSubField(String ind, String value) {
        mabSubIndicator = new MABSubIndicator(ind);
        mabSubValue = value;
    }

    public MABSubField(String subField) {
        mabSubIndicator= new MABSubIndicator(subField.substring(0, 1));
        mabSubValue = subField.substring(1);
    }

    public void dump() {
        System.out.print("   ");
        mabSubIndicator.dump();
        System.out.print(": ");
        System.out.println(mabSubValue);
    }

    public Integer length() {
        return mabSubIndicator.length() + mabSubValue.length();
    }

    public void writeMABDisk(FileOutputStream fos) {
        mabSubIndicator.writeMABDisk(fos);
        try {
            fos.write((mabSubValue).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        mabSubIndicator.writeMABTape(fos);
        try {
            fos.write((mabSubValue).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSubFieldValue() { return mabSubValue; }

    public String getSubFieldIndicator() { return mabSubIndicator.getSubIndicator(); }

    public void setSubFieldValue(String value) {
        mabSubValue = value;
    }
}
