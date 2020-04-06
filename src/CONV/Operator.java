package CONV;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.util.Arrays;
import java.util.List;

public class Operator {
    private String operator;

    public Operator(String op) {
        operator = op;
    }

    public void dump() {
        System.out.print(operator);
    }

    public String getOperator() { return operator; }

    public Boolean checkOP(String left, String right) {
        Boolean isOK = Boolean.FALSE;
        switch (operator) {
            case "==" : {
                if (left.equals(right)) {
                    isOK = Boolean.TRUE;
                }
                break;
            }
            case "!=" : {
                if (!(left.equals(right))) {
                    isOK = Boolean.TRUE;
                }
                break;
            }
            case "in" : {
                String[] check = left.split(("\\,"));
                List<String> list = Arrays.asList(check);
                if (list.contains(right)) {
                    isOK = Boolean.TRUE;
                }
                break;
            }
            case "!in" : {
                String[] check = left.split(("\\,"));
                List<String> list = Arrays.asList(check);
                if (!(list.contains(right))) {
                    isOK = Boolean.TRUE;
                }
                break;
            }
            case "contains" : {
                try {
                    if (right.contains(left)) {
                        isOK = Boolean.TRUE;
                    }
                } catch (NullPointerException e) {
                    // Null
                }
                break;
            }
            case "!contains" : {
                try {
                    if (!(right.contains(left))) {
                        isOK = Boolean.TRUE;
                    }
                } catch (NullPointerException e) {
                    // Null
                }
                break;
            }
            case "startswith" : {
                if (right.startsWith(left)) {
                    isOK = Boolean.TRUE;
                }
                break;
            }
            case "!startswith" : {
                if (!(right.startsWith(left))) {
                    isOK = Boolean.TRUE;
                }
                break;
            }
        }
        return isOK;
    }
}
