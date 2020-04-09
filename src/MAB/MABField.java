package MAB;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.io.FileOutputStream;
import java.util.ArrayList;

public class MABField {

    private Integer maxEntry = 0;

    private Integer mabEntry;
    private MABTag mabTag;
    private MABIndicator mabIndicator;

    private Integer fieldType;

    private String mabValue;
    ArrayList<MABSubField> mabSubFieldList;
    ArrayList<MABPartField> mabPartFieldList;

    public MABField(String tag, String ind, String value, Integer mabAkt) {
        mabEntry = mabAkt;
        mabTag = new MABTag(tag);
        mabIndicator = new MABIndicator(ind);
        mabValue = value;
        fieldType = MABConstants.VALUE;
    }

    public MABField(String tag, String ind, String[] parts, Integer mabAkt) {
        mabEntry = mabAkt;
        mabTag = new MABTag(tag);
        mabIndicator = new MABIndicator(ind);
        mabPartFieldList = new ArrayList<>();
        for (String part : parts) {
            mabPartFieldList.add(new MABPartField(part));
        }
        fieldType = MABConstants.PART;
    }

    public MABField(String tag, String ind, Integer mabAkt) {
        mabEntry = mabAkt;
        mabTag = new MABTag(tag);
        mabIndicator = new MABIndicator(ind);
        mabSubFieldList = new ArrayList<>();
        fieldType = MABConstants.SUBFIELD;
    }

    public MABField(String tag, String ind) {
        mabEntry = 0;
        mabTag = new MABTag(tag);
        mabIndicator = new MABIndicator(ind);
        mabSubFieldList = new ArrayList<>();
        fieldType = MABConstants.SUBFIELD;
    }

    // Constructor for tape-field
    public MABField(String mabField, Integer mabAkt) {
        mabEntry = mabAkt;
        mabTag = new MABTag(mabField.substring(0,3));
        mabIndicator = new MABIndicator(mabField.substring(3,4));
        mabField = mabField.substring(4);
        if (mabField.contains(MABConstants.subFieldDelimiterT)) {
            fieldType = MABConstants.SUBFIELD;
            mabSubFieldList = new ArrayList<>();
            String[] parts = mabField.split(MABConstants.subFieldDelimiterT);
            for (String part : parts) {
                if (part.length()>1) {
                    mabSubFieldList.add(new MABSubField(part.substring(0, 1), part.substring(1)));
                }
            }
        } else {
            if (mabField.contains(MABConstants.partFieldDelimiter)) {
                fieldType = MABConstants.PART;
                mabPartFieldList = new ArrayList<>();
                String[] parts = mabField.split(MABConstants.partFieldDelimiter);
                for (String part : parts) {
                    mabPartFieldList.add(new MABPartField(part));
                }
                mabValue = mabField;
            } else {
                mabValue = mabField;
                fieldType = MABConstants.VALUE;
            }
        }
    }

    public void dump() {
        System.out.print(mabEntry + " : ");
        mabTag.dump();
        mabIndicator.dump();
        switch (fieldType) {
            case MABConstants.VALUE: {
                System.out.println("  " + mabValue);
                break;
            }
            case MABConstants.PART: {
                System.out.println();
                for (MABPartField mabPartField : mabPartFieldList) {
                    mabPartField.dump();
                }
                break;
            }
            case MABConstants.SUBFIELD: {
                System.out.println();
                for (MABSubField mabSubField : mabSubFieldList) {
                    mabSubField.dump();
                }
                break;
            }
            default: {
                System.out.println(" ### n/a ###");
            }
        }

    }

    public Integer length() {
        Integer fieldLength = 1;
        fieldLength += mabTag.length();
        fieldLength += mabIndicator.length();
        switch (fieldType) {
            case MABConstants.VALUE: {
                fieldLength += mabValue.length();
                break;
            }
            case MABConstants.PART: {
                for (MABPartField mabPartField : mabPartFieldList) {
                    fieldLength += mabPartField.length();
                }
                break;
            }
            case MABConstants.SUBFIELD: {
                for (MABSubField mabSubField : mabSubFieldList) {
                    fieldLength += mabSubField.length();
                }
                break;
            }
            default: {
            }
        }
        return fieldLength;
    }

    public String getMabTI() { return "" + mabTag.getMabTag() + "" + mabIndicator.getMabIndicator(); }

    public void writeMABDisk(FileOutputStream fos) {
        mabTag.writeMABDisk(fos);
        mabIndicator.writeMABDisk(fos);
        try {
            fos.write("  ".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (fieldType) {
            case MABConstants.VALUE: {
                try {
                    fos.write(mabValue.getBytes("UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case MABConstants.PART: {
                for (MABPartField mabPartField : mabPartFieldList) {
                    mabPartField.writeMABDisk(fos);
                }
                break;
            }
            case MABConstants.SUBFIELD: {
                for (MABSubField mabSubField : mabSubFieldList) {
                    mabSubField.writeMABDisk(fos);
                }
                break;
            }
            default: {
                break;
            }
        }
        try {
            fos.write("\n".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        mabTag.writeMABTape(fos);
        mabIndicator.writeMABTape(fos);
        switch (fieldType) {
            case MABConstants.VALUE: {
                try {
                    fos.write(mabValue.getBytes("UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case MABConstants.PART: {
                for (MABPartField mabPartField : mabPartFieldList) {
                    mabPartField.writeMABTape(fos);
                }
                break;
            }
            case MABConstants.SUBFIELD: {
                for (MABSubField mabSubField : mabSubFieldList) {
                    mabSubField.writeMABTape(fos);
                }
                break;
            }
            default: {
                break;
            }
        }
        try {
            fos.write(MABConstants.fieldDelimiter.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean equals(Object o) {
        MABField testMab = (MABField) o;
        boolean isEqual = Boolean.FALSE;
        if ((testMab.mabTag.getMabTag().equals(mabTag.getMabTag())) && (testMab.mabIndicator.getMabIndicator().equals(mabIndicator.getMabIndicator()))) {
            isEqual = Boolean.TRUE;
        }
        return isEqual;
    }

    public String getValue() {
        return mabValue;
    }

    public String getSubValue(String subField) {
        for( Integer i = 0; i < mabSubFieldList.size(); i++) {
            if (mabSubFieldList.get(i).getSubFieldIndicator().equals(subField)) {
                return mabSubFieldList.get(i).getSubFieldValue();
            }
        }
        return "";
    }

    public void setValue(String mabValueNew) { mabValue = mabValueNew; }

    public void modify(String mabTagNew, String mabIndNew) {
        mabTag.setMabTag(mabTagNew);
        mabIndicator.setMabIndicator(mabIndNew);
    }

    public MABSubField[] getSubFields() {
        MABSubField[] subFields = new MABSubField[mabSubFieldList.size()];
        for (Integer i = 0; i < mabSubFieldList.size(); i++) {
            subFields[i] = mabSubFieldList.get(i);
        }
        return subFields;
    }

    public Boolean hasSubFields() {
        if (mabSubFieldList == null) {
            return Boolean.FALSE;
        } else {
            if (mabSubFieldList.size() > 0) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public void addSubField(String subTag, String value) {
        mabSubFieldList.add(new MABSubField(subTag, value));
    }

    public Integer getEntryNumber() {
        return mabEntry;
    }

    public void replaceSubField(String subTag, String newValue) {
        Boolean i = Boolean.TRUE;
        for (MABSubField mabSubField : mabSubFieldList) {
            if (mabSubField.getSubFieldIndicator().equals(subTag)) {
                mabSubField.setSubFieldValue(newValue);
                i = Boolean.FALSE;
            }
        }
        if (i) {
            addSubField(subTag, newValue);
        }
    }

}
