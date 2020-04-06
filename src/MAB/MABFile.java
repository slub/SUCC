package MAB;

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
 */

import TOOLS.Convert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

public class MABFile {

    private ArrayList<MABRecord> mabFile;
    private Integer recordCount;
    private String mabTyp;
    private Properties statProp;

    public MABFile() {
        this("t");
    }

    public MABFile(String typ) {
        mabFile = new ArrayList<>();
        recordCount = 0;
        mabTyp = typ;
    }

    public Integer loadMABTape(String fileName) {
        String mabT = "";
        try {
            InputStream inputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String str;
            while ( (str = bufferedReader.readLine()) != null ) {
                str = Convert.mabConvert(str);
                mabT += str;
            }
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] mabTRecords = mabT.split(MABConstants.recordDelimiter);
        for (String mabTRecord : mabTRecords) {
            mabFile.add(new MABRecord("t", mabTRecord));
            recordCount++;
        }

        return recordCount;
    }

    public Integer loadMABDisk(String fileName) {
        String mabD = "";
        try {
            InputStream inputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String str;
            while ( (str = bufferedReader.readLine()) != null ) {
                if ((str.startsWith("###")) && (str.length() > 2)) {
                    str = Convert.mabConvert(str);
                    if (mabD.length() > 2) {
                        mabFile.add(new MABRecord("d", mabD));
                        recordCount++;
                        mabD = "";
                    }
                }
                mabD += str + MABConstants.fieldDelimiter;
            }
            inputStream.close();
            if (mabD.length() > 2) {
                mabFile.add(new MABRecord("d", mabD));
                recordCount++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordCount;
    }

    public Integer writeMABDisk(String fileName) {
        Integer recordCount = 0;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            for (MABRecord mabRecord : mabFile) {
                if (mabRecord.getExportListLength() > 0) {
                    mabRecord.doExport();
                }
                mabRecord.writeMABDisk(fileOutputStream);
                recordCount++;
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File <" + fileName + "> not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordCount;
    }

    public Integer writeMABTape(String fileName) {
        Integer recordCount = 0;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            for (MABRecord mabRecord : mabFile) {
                mabRecord.writeMABTape(fileOutputStream);
                recordCount++;
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File <" + fileName + "> not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordCount;
    }

    public void dump() {
        System.out.println("Records: " + recordCount);
        for (MABRecord mabRecord : mabFile) {
            System.out.println("--------------------------------------------------------------------------------");
            mabRecord.dump();
        }
    }

    public MABRecord[] getRecords() {
        MABRecord[] mabRecords = mabFile.toArray(new MABRecord[mabFile.size()]);
        return mabRecords;
    }

    public String getMabTyp() { return mabTyp; }

}
