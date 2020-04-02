package CONV;

import MAB.MABConstants;
import MAB.MABRecord;
import TOOLS.ActionStats;

import java.lang.reflect.Method;
import java.util.*;

public class Action {
    private String action;
    private ArrayList<String> args = new ArrayList<>();

    public Action(String aktion) {
        String[] purAktion = aktion.split("\\(");
        if (purAktion.length > 1) {
            String[] params = purAktion[1].split("\\)")[0].split(",");
            Collections.addAll(args, params);
        }
        aktion = purAktion[0];
        switch (aktion) {
            case "set": {}
            case "copy": {}
            case "move": {}
            case "delete": {}
            case "prefix": {}
            case "suffix": {}
            case "init": {}
            case "format": {}
            case "export": {}
            case "piece": {
                // 2 Parameter Pflicht
            }
            case "assign": {
                // 1 Parameter Pflicht
            }
            case "add": {}
            case "sub": {
                action = aktion;
                break;
            }
            default: {
                action = "n/a";
            }
        }
    }

    public void dump() {
        System.out.print(action);
        if (args.size() > 1) {
            System.out.print("(");
            Iterator<String> it = args.iterator();
            int i = 0;
            while (it.hasNext()) {
                if (i > 0) {
                    System.out.print(",");
                }
                i++;
                System.out.print(it.next());
            }
            System.out.print(")");
        }
        if (1 == 0) {
            // just for avoiding "method never used"-warning
            Converter conv = new Converter();
            SrcDst src = new SrcDst("1", conv);
            SrcDst dst = new SrcDst("1", conv);
            MABRecord mabRecord = new MABRecord();
            Integer toUse = MABConstants.useAlways;
            add(src, dst, mabRecord, conv, toUse);
            set(src, dst, mabRecord, conv, toUse);
            delete(src, dst, mabRecord, conv, toUse);
            copy(src, dst, mabRecord, conv, toUse);
            move(src, dst, mabRecord, conv, toUse);
            piece(src, dst, mabRecord, conv, toUse);
            suffix(src, dst, mabRecord, conv, toUse);
            prefix(src, dst, mabRecord, conv, toUse);
            assign(src, dst, mabRecord, conv, toUse);
            format(src, dst, mabRecord, conv, toUse);
            export(src, dst, mabRecord, conv, toUse);
        }
    }

    public void doAction(SrcDst src, SrcDst dst, MABRecord mab, Converter conv, HashMap<String, ActionStats> actionStats, Integer toUse) {
        try {
            Object[] params = {src, dst, mab, conv, toUse};
            Class<?>[] paramsDef = new Class[5];
            paramsDef[0] = SrcDst.class;
            paramsDef[1] = SrcDst.class;
            paramsDef[2] = MABRecord.class;
            paramsDef[3] = Converter.class;
            paramsDef[4] = Integer.class;
            Method method = this.getClass().getMethod(action, paramsDef);
            Object Nanos = method.invoke(this, params);
            if (actionStats.containsKey(action)) {
                actionStats.get(action).increaseNanos(0L);
            } else {
                actionStats.put(action, new ActionStats((Long) Nanos));
            }
        } catch (NoSuchMethodException e) {
            System.out.println("Action <" + action + "> not implemented yet!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long add(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        String newMabValue = "";
        if (dst.getTyp().equals(SrcDst.IS_VARIABLE)) {
            newMabValue = conv.getSpeicher(dst.getVariable());
        }
        if (dst.getTyp().equals(SrcDst.IS_VALUE)) {
            newMabValue = dst.getValue();
        }
        if (dst.getTyp().equals(SrcDst.IS_SYSTEM)) {
            newMabValue = dst.getSystem();
        }
        if (src.getMabField().getSubTag().equals(" ")) {
            mabRecord.addMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), newMabValue);
        } else {
            mabRecord.addSUBField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), src.getMabField().getSubTag(), newMabValue);
        }
        return System.nanoTime() - startNano;
    }

    public Long set(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        int possibility = 0;
        String newValue = "";
        switch (src.getTyp()) {
            case SrcDst.IS_MABFIELD: {
                possibility = 1;
                if (dst.getTyp().equals(SrcDst.IS_VALUE)) {
                    newValue = dst.getValue();
                }
                if (dst.getTyp().equals(SrcDst.IS_VARIABLE)) {
                    newValue = conv.getSpeicher(dst.getVariable());
                }
                if (dst.getTyp().equals(SrcDst.IS_SYSTEM)) {
                    newValue = dst.getSystem();
                }
                if (dst.getTyp().equals(SrcDst.IS_MABFIELD)) {
                    if (dst.getMabField().getSubTag().equals(" ")) {
                        if (toUse.equals(MABConstants.useAlways)) {
                            newValue = mabRecord.getMabFieldValue(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
                        } else {
                            //System.out.println("dst.toUse(mab): " + toUse);
                            newValue = mabRecord.getMabFieldValue(toUse);
                        }
                    } else {
                        if (toUse.equals(MABConstants.useAlways)) {
                            newValue = mabRecord.getSubFieldValue(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator(), dst.getMabField().getSubTag());
                        } else {
                            //System.out.println("dst(sub).toUse: " + toUse);
                            newValue = mabRecord.getSubFieldValue(dst.getMabField().getSubTag(), toUse);
                        }
                    }
                }
                break;
            }
            case SrcDst.IS_VARIABLE: {
                possibility = 2;
                if (dst.getTyp().equals(SrcDst.IS_VALUE)) {
                    newValue = dst.getValue();
                }
                if (dst.getTyp().equals(SrcDst.IS_VARIABLE)) {
                    newValue = conv.getSpeicher(dst.getVariable());
                }
                if (dst.getTyp().equals(SrcDst.IS_SYSTEM)) {
                    newValue = conv.getSpeicher(dst.getSystem());
                }
                if (dst.getTyp().equals(SrcDst.IS_MABFIELD)) {
                    if (dst.getMabField().getSubTag().equals(" ")) {
                        if (toUse.equals(MABConstants.useAlways)) {
                            newValue = mabRecord.getMabFieldValue(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
                        } else {
                            //System.out.println("dst(subV).toUse: " + toUse);
                            newValue = mabRecord.getSubFieldValue(dst.getMabField().getSubTag(), toUse);
                        }
                    } else {
                        if (toUse.equals(MABConstants.useAlways)) {
                            newValue = mabRecord.getSubFieldValue(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator(), dst.getMabField().getSubTag());
                        } else {
                            //System.out.println("dst(subV).toUse: " + toUse);
                            newValue = mabRecord.getSubFieldValue(dst.getMabField().getSubTag(), toUse);
                        }
                    }
                }
                break;
            }
        }

        switch (possibility) {
            case 1: {
                if (src.getMabField().getSubTag().equals(" ")) {
                    if (mabRecord.exists(src.getMabField().getMabTag(), src.getMabField().getMabIndicator())) {
                        if (toUse.equals(MABConstants.useAlways)) {
                            mabRecord.replaceMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), newValue);
                        } else {
                            mabRecord.replaceMABField(toUse, newValue);
                        }
                    } else {
                        mabRecord.addMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), newValue);
                    }
                } else {
                    // SubTag
                    if (mabRecord.hasSubField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), src.getMabField().getSubTag())) {
                        if (toUse.equals(MABConstants.useAlways)) {
                            //System.out.println("src.toUse(rep_mab): " + toUse);
                            mabRecord.replaceSUBField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), src.getMabField().getSubTag(), newValue);
                        } else {
                            //System.out.println("src.toUse(rep_mab): " + toUse);
                            mabRecord.replaceSUBField(toUse, src.getMabField().getSubTag(), newValue);
                        }
                    } else {
                        if (toUse.equals(MABConstants.useAlways)) {
                            //System.out.println("src.toUse(sub_mab): " + toUse);
                            mabRecord.setSUBField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), src.getMabField().getSubTag(), newValue);
                        } else {
                            //System.out.println("src.toUse(sub_set): " + toUse);
                            mabRecord.setSUBField(toUse, src.getMabField().getSubTag(), newValue);
                        }
                    }
                }
                break;
            }
            case 2: {
                conv.setSpeicher(src.getVariable(), newValue);
                break;
            }
        }
        return System.nanoTime() - startNano;
    }

    public Long delete(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        if (src.getTyp().equals(SrcDst.IS_MABFIELD)) {
            if (toUse.equals(MABConstants.useAlways)) {
                mabRecord.deleteMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator());
            } else {
                mabRecord.deleteMABField(toUse);
            }
        }
        return System.nanoTime() - startNano;
    }

    public Long copy(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        if (src.getTyp().equals(SrcDst.IS_MABFIELD) && dst.getTyp().equals((SrcDst.IS_MABFIELD))) {
            if (mabRecord.hasSubFields(src.getMabField().getMabTag(), src.getMabField().getMabIndicator())) {
                if (src.getMabField().getSubTag().equals(" ")) {
                    if (toUse.equals(MABConstants.useAlways)) {
                        mabRecord.copyMABFieldWithSubFields(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
                    } else {
                        mabRecord.copyMABFieldWithSubFields(toUse, dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
                    }
                } else {
                    // copy subfield only
                    if (toUse.equals(MABConstants.useAlways)) {
                        mabRecord.copySubFieldOnly(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), src.getMabField().getSubTag(), dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
                    } else {
                        mabRecord.copySubFieldOnly(toUse, dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator(), dst.getMabField().getSubTag());
                    }
                }
            } else {
                if (toUse.equals(MABConstants.useAlways)) {
                    mabRecord.copyMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
                } else {
                    mabRecord.copyMABField(toUse, dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
                }
            }
        }
        if (src.getTyp().equals(SrcDst.IS_MABFIELD) && dst.getTyp().equals((SrcDst.IS_VARIABLE))) {
            if (src.getMabField().getSubTag().equals(" ")) {
                conv.setSpeicher(dst.getVariable(), mabRecord.getMabFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator()));
            } else {
                conv.setSpeicher(dst.getVariable(), mabRecord.getSubFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), src.getMabField().getSubTag()));
            }
        }
        return System.nanoTime() - startNano;
    }

    public Long move(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        if (src.getTyp().equals(SrcDst.IS_MABFIELD) && dst.getTyp().equals((SrcDst.IS_MABFIELD))) {
            mabRecord.moveMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
        }
        return System.nanoTime() - startNano;
    }

    public Long piece(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        String splitValue;
        if (src.getTyp().equals(SrcDst.IS_MABFIELD)) {
            splitValue = mabRecord.getMabFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator());
        } else {
            splitValue = conv.getSpeicher(src.getVariable());
        }
        String splittedValue = "";
        if (!(splitValue.equals(""))) {
            if (args.get(1).equals("|") || args.get(1).equals("&") || args.get(1).equals("/") || args.get(1).equals(".")) {
                //noinspection Annotator
                splitValue = splitValue.replaceAll("\\" + args.get(1), "|");
                splittedValue = splitValue.split("\\|",-2)[Integer.valueOf(args.get(0)) - 1];
            } else {
                splittedValue = splitValue.split(args.get(1),-2)[Integer.valueOf(args.get(0)) - 1];
            }
        }
        if (dst.getTyp().equals(SrcDst.IS_MABFIELD)) {
            if (mabRecord.exists(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator())) {
                mabRecord.replaceMABField(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator(), splittedValue);
            } else {
                mabRecord.addMABField(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator(), splittedValue);
            }
        } else {
            conv.setSpeicher(dst.getVariable(), splittedValue);
        }
        return System.nanoTime() - startNano;
    }

    public Long suffix(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        String suffixString;
        if (dst.getTyp().equals(SrcDst.IS_VALUE)) {
            suffixString = dst.getValue();
        } else {
            suffixString = conv.getSpeicher(dst.getVariable());
        }
        if (src.getTyp().equals(SrcDst.IS_VARIABLE)) {
            conv.setSpeicher(src.getVariable(), conv.getSpeicher(src.getVariable()) + suffixString);
        } else {
            if (mabRecord.exists(src.getMabField().getMabTag(), src.getMabField().getMabIndicator())) {
                mabRecord.replaceMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), mabRecord.getMabFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator()) + suffixString);
            } else {
                mabRecord.addMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), mabRecord.getMabFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator()) + suffixString);
            }
        }
        return System.nanoTime() - startNano;
    }

    public Long prefix(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        String suffixString;
        if (dst.getTyp().equals(SrcDst.IS_VALUE)) {
            suffixString = dst.getValue();
        } else {
            suffixString = conv.getSpeicher(dst.getVariable());
        }
        if (src.getTyp().equals(SrcDst.IS_VARIABLE)) {
            conv.setSpeicher(src.getVariable(), suffixString + conv.getSpeicher(src.getVariable()));
        } else {
            if (mabRecord.exists(src.getMabField().getMabTag(), src.getMabField().getMabIndicator())) {
                mabRecord.replaceMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), suffixString + mabRecord.getMabFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator()));
            } else {
                mabRecord.addMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), suffixString + mabRecord.getMabFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator()));
            }
        }
        return System.nanoTime() - startNano;
    }

    public Long assign(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        String entryString = "_";
        if (dst.getTyp().equals(SrcDst.IS_MABFIELD)) {
            if (dst.getMabField().getSubTag().equals(" ")) {
                entryString = mabRecord.getMabFieldValue(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator());
            } else {
                entryString = mabRecord.getSubFieldValue(dst.getMabField().getMabTag(), dst.getMabField().getMabIndicator(), dst.getMabField().getSubTag());
            }
        }
        if (dst.getTyp().equals(SrcDst.IS_VARIABLE)) {
            entryString = conv.getSpeicher(dst.getVariable());
        }
        if (dst.getTyp().equals(SrcDst.IS_VALUE)) {
            entryString = dst.getValue();
        }
        if (dst.getTyp().equals(SrcDst.IS_SYSTEM)) {
            entryString = dst.getSystem();
        }

        String varString = args.get(0) + "_" + entryString;
        entryString = conv.getSpeicher(varString, "n/a");

        if (entryString.equals("n/a")) {

        } else {
            if (src.getTyp().equals(SrcDst.IS_VARIABLE)) {
                conv.setSpeicher(src.getVariable(), entryString);
            } else {
                if (mabRecord.exists(src.getMabField().getMabTag(), src.getMabField().getMabIndicator())) {
                    mabRecord.replaceMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), entryString);
                } else {
                    mabRecord.addMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), entryString);
                }
            }
        }
        return System.nanoTime() - startNano;
    }

    public Long format(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        String entryString = "";
        if (dst.getTyp().equals(SrcDst.IS_VALUE)) {
            entryString = dst.getValue();
        }
        String varString = "";
        if (src.getTyp().equals(SrcDst.IS_VARIABLE)) {
            varString = conv.getSpeicher(src.getVariable());
        }
        if (src.getTyp().equals(SrcDst.IS_MABFIELD)) {
            if (src.getMabField().getSubTag().equals(" ")) {
                varString = mabRecord.getMabFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator());
            } else {
                varString = mabRecord.getSubFieldValue(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), src.getMabField().getSubTag());
            }
        }
        String varType = entryString.substring(entryString.length()-1);

        String ergString = "";
        switch (varType) {
            case "d": {
                ergString = String.format("%" + entryString, Integer.valueOf(varString));
                break;
            }
            case "f": {
                ergString = String.format("%" + entryString, Float.valueOf(varString));
                break;
            }
            case "s": {
                ergString = String.format("%" + entryString, varString);
                break;
            }
            default: {
                ergString = String.format("%" + entryString, varString);
            }
        }
        if (src.getTyp().equals(SrcDst.IS_MABFIELD)) {
            if (mabRecord.exists(src.getMabField().getMabTag(), src.getMabField().getMabIndicator())) {
                mabRecord.replaceMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), ergString);
            } else {
                mabRecord.addMABField(src.getMabField().getMabTag(), src.getMabField().getMabIndicator(), ergString);
            }
        }
        if (src.getTyp().equals(SrcDst.IS_VARIABLE)) {
            conv.setSpeicher(src.getVariable(), ergString);
        }
        return System.nanoTime() - startNano;
    }

    public Long export(SrcDst src, SrcDst dst, MABRecord mabRecord, Converter conv, Integer toUse) {
        Long startNano = System.nanoTime();
        switch (src.getTyp()) {
            case SrcDst.IS_VARIABLE: {
                mabRecord.setExport(src.getVariable());
                break;
            }
            case SrcDst.IS_VALUE: {
                mabRecord.setExport(src.getValue());
                break;
            }
        }
        return System.nanoTime() - startNano;
    }

    public String getAction() {return action;}
}
