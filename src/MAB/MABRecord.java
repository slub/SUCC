package MAB;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

public class MABRecord {

    private Integer maxEntry = 0;

    private MABHeader mabHeader;
    private ArrayList<MABField> mabFields;
    private ArrayList<String> exportNames;

    public MABRecord() {
        mabHeader = new MABHeader();
        mabFields = new ArrayList<>();
        exportNames = new ArrayList<>();
    }

    public MABRecord(String typ, String mab) {
        switch (typ) {
            case "t": {
                MABtRecord(mab);
                break;
            }
            case "d": {
                MABdRecord(mab);
            }
            default: {
                break;
            }
        }
    }

    private void MABtRecord(String mab) {
        mabHeader = new MABHeader(mab.substring(0,24));
        mab = mab.substring(24);
        mabFields = new ArrayList<>();
        exportNames = new ArrayList<>();
        String[] mabFieldsStrings = mab.split(MABConstants.fieldDelimiter);
        for (String mabFieldsString : mabFieldsStrings) {
            if (mabFieldsString.length()>0) {
                if (mabFieldsString.startsWith("331") || mabFieldsString.startsWith("335")) {
                    mabFieldsString = mabFieldsString.replace('|', ' ');
                }
                mabFields.add(new MABField(mabFieldsString, ++maxEntry));
            }
        }
    }

    private void MABdRecord(String mab) {
        String[] mabFieldsStrings = mab.split(MABConstants.fieldDelimiter);
        ArrayList<String> mabFieldsArray = new ArrayList<>(Arrays.asList(mabFieldsStrings));
        mabHeader = new MABHeader(mabFieldsArray.get(0).substring(6));
        mabFieldsArray.remove(0);
        mabFields = new ArrayList<>();
        exportNames = new ArrayList<>();
        for (String mabFieldsString : mabFieldsArray) {
            if (mabFieldsString.length()>0) {
                mabFieldsString = mabFieldsString.substring(0,4) + mabFieldsString.substring(6);
                mabFieldsString.replaceAll(MABConstants.subFieldDelimiterD, MABConstants.subFieldDelimiterT);
                System.out.println("mFS: " + mabFieldsString);
                mabFields.add(new MABField(mabFieldsString, ++maxEntry));
            }
        }


    }

    public void writeMABDisk(FileOutputStream fos) {
        mabHeader.setLength(this.length());
        try {
            mabHeader.writeMABDisk(fos);
            Collections.sort(mabFields, new Comparator<MABField>() {
                @Override
                public int compare(MABField f1, MABField f2) {
                    return f1.getMabTI().compareTo(f2.getMabTI());
                }
            });
            for (MABField mabField : mabFields) {
                mabField.writeMABDisk(fos);
            }

            fos.write(("\n").getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMABTape(FileOutputStream fos) {
        mabHeader.setLength(this.length());
        try {
            mabHeader.writeMABTape(fos);
            Collections.sort(mabFields, new Comparator<MABField>() {
                @Override
                public int compare(MABField f1, MABField f2) {
                    return f1.getMabTI().compareTo(f2.getMabTI());
                }
            });
            for (MABField mabField : mabFields) {
                mabField.writeMABTape(fos);
            }

            fos.write(MABConstants.recordDelimiter.getBytes("UTF-8"));
            fos.write("\n".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dump() {
        mabHeader.setLength(this.length());
        mabHeader.dump();
        for (MABField mabField : mabFields) {
            mabField.dump();
        }
    }

    public String length() {
        Integer mabLength = mabHeader.length();
        for (MABField mabField : mabFields) {
            mabLength += mabField.length();
        }

        return String.format("%05d",mabLength);
    }

    public Boolean exists(String mabTag, String mabInd) {
        MABField testField = new MABField(mabTag, mabInd);
        for (MABField mabField : mabFields) {
            if (mabField.equals(testField)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public MABField[] getFields(String mabTag, String mabInd) {
        MABField testField = new MABField(mabTag, mabInd);
        ArrayList<MABField> retArray = new ArrayList<>();
        for (MABField mabField : mabFields) {
            if (mabField.equals(testField)) {
                retArray.add(mabField);
            }
        }
        return retArray.toArray(MABField[]::new);
    }

    public void addMABField(String mabTag, String mabInd, String mabValue) {
        mabFields.add(new MABField(mabTag, mabInd, mabValue, ++maxEntry));
    }

    public Integer addSUBField(String mabTag, String mabIndicator, String subTag, String value) {
        mabFields.add(new MABField(mabTag + mabIndicator + MABConstants.subFieldDelimiterT + subTag + value, ++maxEntry));
        return maxEntry;
    }

    public void setSUBField(String mabTag, String mabIndicator, String subTag, String value) {
        if (exists(mabTag, mabIndicator)) {
            // Feld existiert
            replaceSUBField(mabTag, mabIndicator, subTag, value);
        } else {
            // Feld existiert nicht
            mabFields.add(new MABField(mabTag + mabIndicator + MABConstants.subFieldDelimiterT + subTag + value, ++maxEntry));
        }
    }

    public void setSUBField(Integer toUse, String subTag, String newValue) {
        Iterator it = mabFields.iterator();
        while (it.hasNext()) {
            MABField aktField = (MABField) it.next();
            if (aktField.getEntryNumber().equals(toUse)) {
                aktField.addSubField(subTag, newValue);
            }
        }
    }

    public void replaceMABField(String mabTag, String mabInd, String mabValueNew) {
        MABField testField = new MABField(mabTag, mabInd);
        Iterator it = mabFields.iterator();
        while (it.hasNext()) {
            MABField aktField = (MABField) it.next();
            if (aktField.equals(testField)) {
                aktField.setValue(mabValueNew);
                break;
            }
        }
    }

    public void replaceMABField(Integer toUse, String newValue) {
        Iterator it = mabFields.iterator();
        while (it.hasNext()) {
            MABField aktField = (MABField) it.next();
            if (aktField.getEntryNumber().equals(toUse)) {
                aktField.setValue(newValue);
                break;
            }
        }
    }

    public void deleteMABField(String mabTag, String mabInd) {
        MABField testField = new MABField(mabTag, mabInd);
        Iterator it = mabFields.iterator();
        while (it.hasNext()) {
            MABField aktField = (MABField) it.next();
            if (aktField.equals(testField)) {
                it.remove();
            }
        }
    }

    public void deleteMABField(Integer toUse) {
        Iterator it = mabFields.iterator();
        while (it.hasNext()) {
            MABField aktField = (MABField) it.next();
            if (aktField.getEntryNumber().equals(toUse)) {
                it.remove();
            }
        }
    }

    public void copyMABField(String mabTag, String mabInd, String mabTag1, String mabInd1) {
        MABField testField = new MABField(mabTag, mabInd);
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).equals(testField)) {
                addMABField(mabTag1, mabInd1, mabFields.get(i).getValue());
            }
        }
    }

    public void copyMABField(Integer toUse, String mabTag, String mabIndicator) {
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).getEntryNumber().equals(toUse)) {
                addMABField(mabTag, mabIndicator, mabFields.get(i).getValue());
            }
        }
    }

    public void copyMABFieldWithSubFields(String mabTag, String mabInd, String mabTag1, String mabInd1) {
        MABField testField = new MABField(mabTag, mabInd);
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).equals(testField)) {
                Integer newUse = 0;
                Boolean firstField = Boolean.TRUE;
                for (MABSubField mabSubField : mabFields.get(i).getSubFields()) {
                    if (firstField) {
                        newUse = addSUBField(mabTag1, mabInd1, mabSubField.getSubFieldIndicator(), mabSubField.getSubFieldValue());
                        firstField = Boolean.FALSE;
                    } else {
                        setSUBField(newUse, mabSubField.getSubFieldIndicator(), mabSubField.getSubFieldValue());
                    }
                }
            }
        }
    }

    public void copyMABFieldWithSubFields(Integer toUse, String mabTag, String mabIndicator) {
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).getEntryNumber().equals(toUse)) {
                Integer newUse = 0;
                Boolean firstField = Boolean.TRUE;
                for (MABSubField mabSubField : mabFields.get(i).getSubFields()) {
                    if (firstField) {
                        newUse = addSUBField(mabTag, mabIndicator, mabSubField.getSubFieldIndicator(), mabSubField.getSubFieldValue());
                        firstField = Boolean.FALSE;
                    } else {
                        setSUBField(newUse, mabSubField.getSubFieldIndicator(), mabSubField.getSubFieldValue());
                    }
                }
            }
        }
    }

    public void moveMABField(String mabTag, String mabInd, String mabTag1, String mabInd1) {
        MABField testField = new MABField(mabTag, mabInd);
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).equals(testField)) {
                mabFields.get(i).modify(mabTag1, mabInd1);
            }
        }
    }

    public String getMabFieldValue(String mabTag, String mabInd) {
        MABField testField = new MABField(mabTag, mabInd);
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).equals(testField)) {
                return mabFields.get(i).getValue();
            }
        }
        return "";
    }

    public String getSubFieldValue(String mabTag, String mabInd, String subTag) {
        MABField testField = new MABField(mabTag, mabInd);
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).equals(testField)) {
                MABField mabField = mabFields.get(i);
                for (int j=0; j< mabField.mabSubFieldList.size(); j++) {
                    if (mabField.mabSubFieldList.get(j).getSubFieldIndicator().equals(subTag)) {
                        return mabField.mabSubFieldList.get(j).getSubFieldValue();
                    }
                }
            }
        }
        return "";
    }

    public String getMabFieldValue(Integer mabEntry) {
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).getEntryNumber().equals(mabEntry)) {
                return mabFields.get(i).getValue();
            }
        }
        return "";
    }

    public String getSubFieldValue(String subTag, Integer mabEntry) {
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).getEntryNumber().equals(mabEntry)) {
                MABField mabField = mabFields.get(i);
                for (int j=0; j< mabField.mabSubFieldList.size(); j++) {
                    if (mabField.mabSubFieldList.get(j).getSubFieldIndicator().equals(subTag)) {
                        return mabField.mabSubFieldList.get(j).getSubFieldValue();
                    }
                }
            }
        }
        return "";
    }

    public boolean hasSubField(String mabTag, String mabIndicator, String subTag) {
        MABField testField = new MABField(mabTag, mabIndicator);
        for (MABField mabField : mabFields) {
            if (mabField.equals(testField)) {
                for (MABSubField subField : mabField.getSubFields()) {
                    if (subField.getSubFieldValue().equals(subTag)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    public boolean hasSubFields(String mabTag, String mabIndicator) {
        MABField testField = new MABField(mabTag, mabIndicator);
        for (MABField mabField : mabFields) {
            if (mabField.equals(testField)) {
                if (mabField.hasSubFields()) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public void replaceSUBField(String mabTag, String mabIndicator, String subTag, String value) {
        MABField testField = new MABField(mabTag, mabIndicator);
        Iterator it = mabFields.iterator();
        while (it.hasNext()) {
            MABField aktField = (MABField) it.next();
            if (aktField.equals(testField)) {
                MABSubField[] mabSubField = aktField.getSubFields();
                Boolean notFoundSub = Boolean.TRUE;
                for (Integer i = 0; i < mabSubField.length; i++) {
                    if (mabSubField[i].getSubFieldIndicator().equals(subTag)) {
                        mabSubField[i].setSubFieldValue(value);
                        notFoundSub = Boolean.FALSE;
                    }
                }
                if (notFoundSub) {
                    aktField.addSubField(subTag, value);
                }
                break;
            }
        }
    }

    public void replaceSUBField(Integer toUse, String subTag, String newValue) {
        Iterator it = mabFields.iterator();
        while (it.hasNext()) {
            MABField aktField = (MABField) it.next();
            if (aktField.getEntryNumber().equals(toUse)) {
                aktField.replaceSubField(subTag, newValue);
            }
        }
    }

    public void setExport(String fileName) {
        exportNames.add(fileName);
    }

    public void doExport() {
        for (String mabFileName : exportNames) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(mabFileName,true);
                writeMABDisk(fileOutputStream);
                fileOutputStream.close();
            } catch(FileNotFoundException e){
                System.out.println("File <" + mabFileName + "> not found!");
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public int getExportListLength() {
        if (exportNames.isEmpty()) {
            return 0;
        }
        return exportNames.size();
    }

    public void copySubFieldOnly(Integer toUse, String mabTag, String mabIndicator, String subField) {
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).getEntryNumber().equals(toUse)) {
                if (mabFields.get(i).getSubValue(subField).length() > 0) {
                    addMABField(mabTag, mabIndicator, mabFields.get(i).getSubValue(subField));
                }
            }
        }
    }

    public void copySubFieldOnly(String mabTag, String mabIndicator, String subField, String mabTag1, String mabIndicator1) {
        MABField testField = new MABField(mabTag, mabIndicator);
        for (int i=0; i<mabFields.size(); i++) {
            if (mabFields.get(i).equals(testField)) {
                if (mabFields.get(i).hasSubFields()) {
                    if (mabFields.get(i).getSubValue(subField).length() > 0) {
                        addMABField(mabTag1, mabIndicator1, mabFields.get(i).getSubValue(subField));
                    }
                }
            }
        }
    }
}
