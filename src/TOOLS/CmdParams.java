package TOOLS;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class CmdParams {
    private HashMap<String, String> cmdParams = new HashMap<>();

    public CmdParams() {}

    public void fill(String[] cmdArgs) {
        String cmdLine = String.join(" ", cmdArgs);
        String[] newArray = cmdLine.split("-");
        for (String pair : newArray) {
            String[] elem = pair.split("=");
            if (elem[0].equals("")) {
                // do nothing
            } else {
                if (elem.length == 2) {
                    cmdParams.put(elem[0], elem[1].trim());
                } else {
                    cmdParams.put(elem[0], "null");
                }
            }
        }

    }

    public Boolean containsKey(String key) {
        return cmdParams.containsKey(key);
    }

    public String get(String key) {
        return cmdParams.get(key);
    }

    public String getOrDefault(String key, String defaultString) {
        return cmdParams.getOrDefault(key, defaultString);
    }

    public void dump() {
        cmdParams.forEach((key, value) -> System.out.println("|" + key + ": " + value + "|"));
    }

    /*    public void forEach(BiConsumer<? super String, ? super String> T) {
        cmdParams.forEach(T);
    }
*/

}
