package CONV;

public class MABfield {
    private String mabTag;
    private String mabInd;
    private String subTag;

    public MABfield(String mabfeld) {
        while (mabfeld.length()<5) {
            mabfeld += " ";
        }
        mabTag = mabfeld.substring(0,3);
        mabInd = mabfeld.substring(3,4);
        subTag = mabfeld.substring(4,5);
    }

    public void dump() {
        System.out.print(mabTag + mabInd + subTag);
    }

    public String getMabTag() { return mabTag; }

    public String getMabIndicator() { return mabInd; }

    public String getSubTag() { return subTag; }
}
