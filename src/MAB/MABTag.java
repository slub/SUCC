package MAB;

import java.io.FileOutputStream;

public class MABTag {

    private String mabTag;

    public MABTag(String tag) {
        mabTag = tag;
    }

    public void dump() { System.out.print(mabTag);}

    public Integer length() { return mabTag.length();}

    public String getMabTag() { return mabTag; }

    public void writeMABDisk(FileOutputStream fos) {
        try {
            fos.write(mabTag.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        try {
            fos.write(mabTag.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMabTag(String mabTagNew) {
        mabTag = mabTagNew;
    }
}
