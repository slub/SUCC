package TOOLS;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

public class Debug {
    private String aktMethod;

    public Debug() {
        this.aktMethod = "";
    }

    private void setMethod() {
        this.aktMethod = Thread.currentThread().getStackTrace()[3].getClassName() + "." + Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    public String getMethod() {
        return this.aktMethod = Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public void println(String o) {
        this.setMethod();
        o = o.replace("\\<","XXX");
        o = o.replace("\\>","YYY");
        System.out.println(this.aktMethod + ": \"" + o + "\"");
    }
}
