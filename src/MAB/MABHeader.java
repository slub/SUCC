package MAB;

import java.io.FileOutputStream;

public class MABHeader {

    private String mabHeader        = "00000nM2.01200024      h";

    private String mabLength        = "00000";  // 0-4
    private String mabStatus        = "n";      // 5
    private String mabVersion       = "M2.0";   // 6-9
    private String mabIndLength     = "1";      // 10
    private String mabPartLength    = "2";      // 11
    private String mabDataStart     = "00024";  // 12-16
    private String mabNotUsed       = "      "; // 17-22
    private String mabTyp           = "h";      // 23

    public MABHeader() {}

    public MABHeader(String header) {
        mabHeader = header;
        decryptHeader();
    }

    private void decryptHeader() {
        mabLength       = mabHeader.substring(0,5);
        mabStatus       = mabHeader.substring(5,6);
        mabVersion      = mabHeader.substring(6,10);
        mabIndLength    = mabHeader.substring(10,11);
        mabPartLength   = mabHeader.substring(11,12);
        mabDataStart    = mabHeader.substring(12,17);
        mabNotUsed      = mabHeader.substring(17,23);
        mabTyp          = mabHeader.substring(23);
    }

    private void encryptHeader() {
        mabHeader = mabLength +
                    mabStatus +
                    mabVersion +
                    mabIndLength +
                    mabPartLength +
                    mabDataStart +
                    mabNotUsed +
                    mabTyp;
    }

    public void dump() {
        System.out.println(mabHeader);
    }

    public Integer length() { return 24; }

    public void writeMABDisk(FileOutputStream fos) {
        this.encryptHeader();
        try {
            fos.write(("###   " + mabHeader + "\n").getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        this.encryptHeader();
        try {
            fos.write(("" + mabHeader).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLength(String length) {
        mabLength = length;
        this.encryptHeader();
    }
}
