//*************************************************************************************************
//   SUCC - Saxony / Slub UniCode Converter
//
//   doing the download from SWB to LIBERO without the UCC (and it's licensing disadvantage)
//
//   by rt3 @ IT @ SLUB Dresden
//*************************************************************************************************

/*
 * @project SUCC
 * @author  Ralf Talkenberger
 * @version 0.9
 * @created 2020-04-07
*/

import CONV.Converter;
import CONV.Eintrag;
import CONV.Operator;
import MAB.MABConstants;
import MAB.MABField;
import MAB.MABFile;
import MAB.MABRecord;
import TOOLS.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class succ {

    // structure for config.properties - entries
    private static Properties config = new Properties();

    // class instance converter.ini - entries
    // class: CONV.Converter
    private static Converter converterINI = new Converter();

    // structure for time statistics (runtime) (in milli seconds)
    private static TimeMetrics timeMetrics = new TimeMetrics();

    // class instance for command line parameters
    // class: TOOLS.CmdParams
    private static CmdParams cmdParams = new CmdParams();

    // structure (with class instance) for action time statistics (in nano seconds)
    // class: TOOLS.ActionStats
    private static HashMap<String, ActionStats> actionStats = new HashMap<>();

    // class intstance for debugging output
    // class: TOOLS.Debug
    private static Debug debug = new Debug();

    // default config file
    private static String configFile = "conf/config.properties";

    // counter for program steps
    private static Integer step = 0;

    // flag for start debugging (end after reading and initializing)
    private static Boolean doStartOnly = Boolean.FALSE;


    //***************************************
    // main routine / entry point
    //***************************************
    public static void main (String[] args) {

        System.out.println();

        // starting time for whole program
        timeMetrics.startTimer("999_Programm");

        // reading command line parameters
        try {
            String strStep = String.format("%03d", ++step);
            timeMetrics.startTimer(strStep + "_readCommandLine");
            cmdParams.fill(args);
            timeMetrics.stopTimer(strStep + "_readCommandLine");
            System.out.println(strStep + ": command line read ...");
        } catch (Exception e) {
            System.out.println("Error while reading command line parameters!");
            e.printStackTrace();
        }

        // loading config from conf/config.properties
        try {
            String strStep = String.format("%03d", ++step);
            timeMetrics.startTimer(strStep + "_loadConfig");
            if (cmdParams.containsKey("propFile")) {
                configFile = cmdParams.get("propFile");
            }
            config = loadConfig(configFile);
            timeMetrics.stopTimer(strStep + "_loadConfig");
            System.out.println(strStep + ": config loaded from <" + configFile + ">: version " + config.getProperty("version","0.0") + " ...");
        } catch (Exception e) {
            System.out.println("Error while loading config!");
            e.printStackTrace();
        }

        // overwriting config with command line parameters
        try {
            String strStep = String.format("%03d", ++step);
            timeMetrics.startTimer(strStep + "_overwriteConfig");
            overWriteConfig();
            timeMetrics.stopTimer(strStep + "_overwriteConfig");
            System.out.println(strStep + ": config overwritten (where needed) ...");
        } catch (Exception e) {
            System.out.println("Error while overwriting config!");
            e.printStackTrace();
        }

        // loading converter file into structure
        try {
            String strStep = String.format("%03d", ++step);
            timeMetrics.startTimer(strStep + "_loadConverter");
            readConverter(config.getProperty("converter", "converter.ini"));
            timeMetrics.stopTimer(strStep + "_loadConverter");
            System.out.println(strStep + ": converter file <" + config.getProperty("converter", "converter.ini") +"> read ... ");
        } catch (Exception e) {
            System.out.println("Error while reading converter entries!");
            e.printStackTrace();
        }

        // initializing variables from config
        try {
            String strStep = String.format("%03d", ++step);
            timeMetrics.startTimer(strStep + "_initVariables");
            initVariables();
            timeMetrics.stopTimer(strStep + "_initVariables");
            System.out.println(strStep + ": variables from properties initialized ...");
        } catch (Exception e) {
            System.out.println("Error while initializing variables!");
            e.printStackTrace();
        }

        System.out.println();

        // debug and stop if just start
        if (doStartOnly) {
            cmdParams.dump();
            converterINI.dumpEntries();
            converterINI.dumpVariables();
            dumpProperties(config);
            System.exit(1);
        }


        switch (cmdParams.getOrDefault("function", "help")) {
            case "mabd2d": {
                String mabInFile = config.getProperty("mabInFile", "mabIn.txt");
                String mabOutFile = config.getProperty("mabOutFile", "mabOut.txt");
                MABFile mabFile = new MABFile();
                Boolean seq = Boolean.FALSE;
                processMABdFile(mabFile, mabInFile, mabOutFile, 1, "d", "d", seq);
                System.out.println();
                break;
            }
            case "mabd2t": {
                String mabInFile = config.getProperty("mabInFile", "mabIn.txt");
                String mabOutFile = config.getProperty("mabOutFile", "mabOut.txt");
                MABFile mabFile = new MABFile();
                Boolean seq = Boolean.FALSE;
                processMABdFile(mabFile, mabInFile, mabOutFile, 1, "d", "t", seq);
                System.out.println();
                break;
            }
            case "mabt2d": {
                String mabInFile = config.getProperty("mabInFile", "mabIn.txt");
                String mabOutFile = config.getProperty("mabOutFile", "mabOut.txt");
                MABFile mabFile = new MABFile();
                Boolean seq = Boolean.FALSE;
                processMABtFile(mabFile, mabInFile, mabOutFile, 1, "t", "d", seq);
                System.out.println();
                break;
            }
            case "mabt2t": {
                String mabInFile = config.getProperty("mabInFile", "mabIn.txt");
                String mabOutFile = config.getProperty("mabOutFile", "mabOut.txt");
                MABFile mabFile = new MABFile();
                Boolean seq = Boolean.FALSE;
                processMABtFile(mabFile, mabInFile, mabOutFile, 1, "t", "t", seq);
                System.out.println();
                break;
            }
            case "mabt2dlist": {
                String mabInList = config.getProperty("mabInList", "mabTlist.txt");
                BufferedReader bufferedReader;
                try {
                    bufferedReader = new BufferedReader(new FileReader(mabInList));
                    String listLine = bufferedReader.readLine();
                    Integer fileCount = 0;
                    while (listLine != null) {
                        fileCount++;
                        String[] mabFileNames = listLine.split("\\|");
                        String mabInFileName = "";
                        if (mabFileNames.length > 0) {
                            mabInFileName = mabFileNames[0];
                        }
                        String mabOutFileName;
                        String tempInFileName = mabInFileName;
                        if ((mabFileNames.length > 1) && (!(mabFileNames[1].equals("")))) {
                            mabOutFileName = mabFileNames[1];
                        } else {
                            if (mabInFileName.contains(".")) {

                            } else {
                                tempInFileName = mabInFileName + ".";
                            }
                            String[] mabInFileNameParts = tempInFileName.split("\\.",2);
                            String delim = ".";
                            if (mabInFileNameParts[1].length() < 1) {
                                delim = "";
                            }
                            mabOutFileName = mabInFileNameParts[0] + config.getProperty("listSuffix", "_out") + delim + mabInFileNameParts[1];
                        }
                        String mabTyp = config.getProperty("defaultTyp", "t");
                        if (mabFileNames.length > 2) {
                            mabTyp = mabFileNames[2];
                        }
                        MABFile mabFile = new MABFile(mabTyp);

                        Boolean seq = Boolean.FALSE;
                        processMABtFile(mabFile, mabInFileName, mabOutFileName, fileCount, mabTyp, "d", seq);

                        System.out.println();
                        listLine = bufferedReader.readLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "help": {}
            default: {
                System.out.println("SLUB-MAB-Tool");
                System.out.println("-------------");
                System.out.println("");
                System.out.println("command line help:");
                System.out.println("      -help ........ show this help screen");
                System.out.println("      -mabt2d ...... convert one mab tape file to mab disk file");
                System.out.println("      -mabd2d ...... convert one mab disk file to mab disk file");
                System.out.println("      -mabt2t ...... convert one mab tape file to mab tape file");
                System.out.println("      -mabt2dlist .. convert mab tape files from list to mab disk files");
                System.out.println("");
                System.exit(99);
            }
        }

        dumpActionStats();

        timeMetrics.stopTimer("999_Programm");
        System.out.println("Laufzeit: " + (0.0 + timeMetrics.getTimer("999_Programm"))/1000 + "s");
    }
    //***************************************
    // end main routine
    //***************************************


    //---------------------------------------------------------------------------------------------
    // routines for startup
    //---------------------------------------------------------------------------------------------

    // loading config
    private static Properties loadConfig(String conffile) {
        Properties config = new Properties();
        try {
            try (InputStream inputStream = new FileInputStream(conffile)) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                    config.load(reader);
                }
            }
        }
        catch (IOException e) {
            System.out.println("FATAL ERROR: Die Datei '" + conffile + "' konnte nicht geöffnet werden!");
            System.exit(98);
        }
        return config;
    }

    // overwrite config
    private static void overWriteConfig() {
        if (cmdParams.containsKey("convFile")) {
            config.setProperty("converter", cmdParams.get("convFile"));
        }
        if (cmdParams.containsKey("inFile")) {
            config.setProperty("mabInFile", cmdParams.get("inFile"));
        }
        if (cmdParams.containsKey("outFile")) {
            config.setProperty("mabOutFile", cmdParams.get("outFile"));
        }
        if (cmdParams.containsKey("inFile") && (cmdParams.get("function").equals("mabt2dlist"))) {
            config.setProperty("mabInList", cmdParams.get("inFile"));
        }
        if (cmdParams.containsKey("mabTyp") && (cmdParams.get("function").equals("mabt2dlist"))) {
            config.setProperty("defaultTyp", cmdParams.get("mabTyp"));
        }
        if (cmdParams.containsKey("outSuffix") && (cmdParams.get("function").equals("mabt2dlist"))) {
            config.setProperty("listSuffix", cmdParams.get("outSuffix"));
        }
        if (cmdParams.containsKey("work")) {
            config.setProperty("work", cmdParams.get("work"));
        }
    }

    // reading converter entries
    private static void readConverter(String converterFileName) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(converterFileName));
            String listLine = bufferedReader.readLine();
            Integer convLine = 1;
            while (listLine != null) {
                convLine += converterINI.setEntry(convLine, listLine);
                listLine = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            debug.println("Sorry! File <" + converterFileName + "> not found!");
            System.exit(91);
        } catch (Exception e) {
            debug.println("Sorry! Unknown Error while reading file <" + converterFileName + ">!");
            e.printStackTrace();
            System.exit(91);
        }
    }

    // initializing variables
    private static void initVariables() {
        config.forEach((key, value) -> {
            if (key.toString().startsWith("V")) {
                converterINI.setSpeicher(key.toString().substring(1), config.getProperty(key.toString()));
            }
            if (key.toString().startsWith("L")) {
                converterINI.setSpeicher(key.toString().substring(1), config.getProperty(key.toString()));
            }
        });

    }



    //---------------------------------------------------------------------------------------------
    // routines for processing
    //---------------------------------------------------------------------------------------------
    private static void processMABtFile(MABFile mabFile, String mabTinFile, String mabOutFile, Integer fileCount, String mabTypIn, String mabTypOut, Boolean seq) {
        if (config.getProperty("work","bulk").equals("bulk")) {
            // file on bulk
            timeMetrics.startTimer("" + fileCount + "_003_loadRecords " + mabTinFile);
            Integer loadedRecords = mabFile.loadMABTape(mabTinFile);
            timeMetrics.stopTimer("" + fileCount + "_003_loadRecords " + mabTinFile);
            System.out.println("" + loadedRecords + " loaded from " + mabTinFile + " (" + mabTypIn + ")" + " in " + timeMetrics.getTimer("" + fileCount + "_003_loadRecords " + mabTinFile) + "ms");

            //mabFile.dump();

            timeMetrics.startTimer("" + fileCount + "_004_transformMAB " + mabTinFile);
            transformMABFile(mabFile);
            timeMetrics.stopTimer("" + fileCount + "_004_transformMAB " + mabTinFile);
            System.out.println("" + loadedRecords + " transformed in " + timeMetrics.getTimer("" + fileCount + "_004_transformMAB " + mabTinFile) + "ms");

            timeMetrics.startTimer("" + fileCount + "_005_writeRecords " + mabTinFile);
            Integer writtenRecords = 0;
            switch (mabTypOut) {
                case "d": {
                    writtenRecords = mabFile.writeMABDisk(mabOutFile);
                    break;
                }
                case "t": {
                    writtenRecords = mabFile.writeMABTape(mabOutFile);
                    break;
                }
            }
            timeMetrics.stopTimer("" + fileCount + "_005_writeRecords " + mabTinFile);
            System.out.println("" + writtenRecords + " written to " + mabOutFile + " in " + timeMetrics.getTimer("" + fileCount + "_005_writeRecords " + mabTinFile) + "ms");

            Long fileZeit = timeMetrics.getTimer("" + fileCount + "_003_loadRecords " + mabTinFile) + timeMetrics.getTimer("" + fileCount + "_004_transformMAB " + mabTinFile) + timeMetrics.getTimer("" + fileCount + "_005_writeRecords " + mabTinFile);
            if (fileZeit > 3000) {
                System.out.println("Gesamtzeit für <" + mabTinFile + ">: " + (0.0 + fileZeit) / 1000 + "s");
            } else {
                System.out.println("Gesamtzeit für <" + mabTinFile + ">: " + fileZeit + "ms");
            }
        } else {
            // file on sequentiel
            String loadTimeName = "" + fileCount + "_003_loadRecords_" + mabTinFile;
            String transTimeName = "" + fileCount + "_004_transformMAB_" + mabTinFile;
            String writeTimeName = "" + fileCount + "_005_writeRecords_" + mabTinFile;
            String fileTimeName = "" + fileCount + "_006_Gesamtzeit_" + mabTinFile;
            timeMetrics.startTimer(fileTimeName);
            Integer countMAB = 0;
            try {
                InputStream inputStream = new FileInputStream(mabTinFile);
                FileOutputStream fileOutputStream = new FileOutputStream(mabOutFile);
                writeBOM(fileOutputStream);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String str;
                timeMetrics.startTimer(loadTimeName);
                while ( (str = bufferedReader.readLine()) != null ) {
                    str = Convert.mabConvert(str);
                    MABRecord mabRecord = new MABRecord("t", str.split(MABConstants.recordDelimiter)[0]);
                    timeMetrics.stopTimer(loadTimeName);
                    timeMetrics.startTimer(transTimeName);
                    transformMABRecord(mabRecord, mabFile);
                    timeMetrics.stopTimer(transTimeName);
                    timeMetrics.startTimer(writeTimeName);
                    switch (mabTypOut) {
                        case "d": {
                            mabRecord.writeMABDisk(fileOutputStream);
                            break;
                        }
                        case "t": {
                            mabRecord.writeMABTape(fileOutputStream);
                            break;
                        }
                    }
                    mabRecord = null;
                    timeMetrics.stopTimer(writeTimeName);
                    countMAB++;
                    timeMetrics.startTimer(loadTimeName);
                }
                timeMetrics.stopTimer(loadTimeName);
                inputStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            timeMetrics.stopTimer(fileTimeName);
            System.out.println("Statistics for <" + mabTinFile + ">");
            System.out.println("records  : " + countMAB);
            System.out.println("loading  : " + timeMetrics.getTimer(loadTimeName) + "ms");
            System.out.println("transform: " + timeMetrics.getTimer(transTimeName) + "ms");
            System.out.println("writing  : " + timeMetrics.getTimer(writeTimeName) + "ms");
            System.out.println("over all : " + timeMetrics.getTimer(fileTimeName) + "ms");
            System.out.println();
        }
    }

    private static void processMABdFile(MABFile mabFile, String mabDinFile, String mabDoutFile, Integer fileCount, String mabTypIn, String mabTypOut, Boolean seq) {
        if (config.getProperty("work","bulk").equals("bulk")) {
            // file on bulk
            timeMetrics.startTimer("" + fileCount + "_003_loadRecords " + mabDinFile);
            Integer loadedRecords = mabFile.loadMABDisk(mabDinFile);
            timeMetrics.stopTimer("" + fileCount + "_003_loadRecords " + mabDinFile);
            System.out.println("" + loadedRecords + " loaded from " + mabDinFile + " (" + mabTypIn + ")" + " in " + timeMetrics.getTimer("" + fileCount + "_003_loadRecords " + mabDinFile) + "ms");

            timeMetrics.startTimer("" + fileCount + "_004_transformMAB " + mabDinFile);
            transformMABFile(mabFile);
            timeMetrics.stopTimer("" + fileCount + "_004_transformMAB " + mabDinFile);
            System.out.println("" + loadedRecords + " transformed in " + timeMetrics.getTimer("" + fileCount + "_004_transformMAB " + mabDinFile) + "ms");

            timeMetrics.startTimer("" + fileCount + "_005_writeRecords " + mabDinFile);
            Integer writtenRecords = mabFile.writeMABDisk(mabDoutFile);
            timeMetrics.stopTimer("" + fileCount + "_005_writeRecords " + mabDinFile);
            System.out.println("" + writtenRecords + " written to " + mabDoutFile + " in " + timeMetrics.getTimer("" + fileCount + "_005_writeRecords " + mabDinFile) + "ms");

            Long fileZeit = timeMetrics.getTimer("" + fileCount + "_003_loadRecords " + mabDinFile) + timeMetrics.getTimer("" + fileCount + "_004_transformMAB " + mabDinFile) + timeMetrics.getTimer("" + fileCount + "_005_writeRecords " + mabDinFile);
            if (fileZeit > 3000) {
                System.out.println("Gesamtzeit für <" + mabDinFile + ">: " + (0.0 + fileZeit) / 1000 + "s");
            } else {
                System.out.println("Gesamtzeit für <" + mabDinFile + ">: " + fileZeit + "ms");
            }
        } else {
            // file on sequentiel
            String loadTimeName = "" + fileCount + "_003_loadRecords_" + mabDinFile;
            String transTimeName = "" + fileCount + "_004_transformMAB_" + mabDinFile;
            String writeTimeName = "" + fileCount + "_005_writeRecords_" + mabDinFile;
            String fileTimeName = "" + fileCount + "_006_Gesamtzeit_" + mabDinFile;
            timeMetrics.startTimer(fileTimeName);
            Integer countMAB = 0;
            try {
                InputStream inputStream = new FileInputStream(mabDinFile);
                FileOutputStream fileOutputStream = new FileOutputStream(mabDoutFile);
                writeBOM(fileOutputStream);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String str = "";
                String mabD = "";
                MABRecord mabRecord;
                timeMetrics.startTimer(loadTimeName);
                while ( (str = bufferedReader.readLine()) != null ) {
                    if ((str.startsWith("###")) && (str.length() > 2)) {
                        str = Convert.mabConvert(str);
                        if (mabD.length() > 2) {
                            mabRecord = new MABRecord("d", mabD);
                            mabD = "";
                            timeMetrics.stopTimer(loadTimeName);
                            timeMetrics.startTimer(transTimeName);
                            transformMABRecord(mabRecord, mabFile);
                            timeMetrics.stopTimer(transTimeName);
                            timeMetrics.startTimer(writeTimeName);
                            mabRecord.writeMABDisk(fileOutputStream);
                            mabRecord = null;
                            timeMetrics.stopTimer(writeTimeName);
                            countMAB++;
                            timeMetrics.startTimer(loadTimeName);
                        }
                    }
                    mabD += str + MABConstants.fieldDelimiter;
                }
                timeMetrics.stopTimer(loadTimeName);
                inputStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            timeMetrics.stopTimer(fileTimeName);
            System.out.println("Statistics for <" + mabDinFile + ">");
            System.out.println("records  : " + countMAB);
            System.out.println("loading  : " + timeMetrics.getTimer(loadTimeName) + "ms");
            System.out.println("transform: " + timeMetrics.getTimer(transTimeName) + "ms");
            System.out.println("writing  : " + timeMetrics.getTimer(writeTimeName) + "ms");
            System.out.println("over all : " + timeMetrics.getTimer(fileTimeName) + "ms");
            System.out.println();
        }
    }

    private static void transformMABFile(MABFile mabFile) {
        for (MABRecord aktMAB : mabFile.getRecords()) {
            transformMABRecord(aktMAB, mabFile);
        }
    }

    private static void transformMABRecord(MABRecord aktMAB, MABFile mabFile) {
        for (Eintrag eintrag : converterINI.getEntries()) {
            Integer toUse = 0;
            if (eintrag.useAlways()) {
                toUse = MABConstants.useAlways;
            } else {
                toUse = checkCondition(eintrag, aktMAB, mabFile);
            }
            if (toUse > 0) {
                doAction(eintrag, aktMAB, actionStats, toUse);
            }
        }
    }


    // starting action based on converter entry
    private static void doAction(Eintrag eintrag, MABRecord aktMAB, HashMap<String, ActionStats> actionStats, Integer toUse) {
        // System.out.println("##############################################################");
        // System.out.print(eintrag.getAction().getAction() + "|");
        // eintrag.getSRC().dump();
        // System.out.print("|");
        // eintrag.getDST().dump();
        // System.out.print("|");
        // System.out.println();
        // aktMAB.dump();
        // System.out.println(converterINI);
        // System.out.println("Line: " + toUse);
        eintrag.getAction().doAction(eintrag.getSRC(), eintrag.getDST(), aktMAB, converterINI, actionStats, toUse);
        // System.out.println("---");
    }

    // checking if converter entry applies to MAB record
    private static Integer checkCondition(Eintrag eintrag, MABRecord aktMAB, MABFile mabFile) {
        String useOn = eintrag.getUseOn();
        String condVar = eintrag.getBedingung().getVariable();
        String mabTag = "";
        String mabInd = "";
        String subTag = "";
        if (condVar.equals("xyz")) {
            mabTag = eintrag.getBedingung().getMabFeld().getMabTag();
            mabInd = eintrag.getBedingung().getMabFeld().getMabIndicator();
            subTag = eintrag.getBedingung().getMabFeld().getSubTag();
        }
        Operator op = eintrag.getBedingung().getOperator();
        String wert = eintrag.getBedingung().getWert();
        if (!(wert.equals(""))) {
            switch (wert.substring(0, 1)) {
                case "V": {
                    wert = converterINI.getSpeicher(wert.substring(1), "");
                    break;
                }
                case "W": {
                    wert = wert.substring(1);
                    break;
                }
            }
        }

        Boolean isUseSuccess = Boolean.FALSE;
        if (mabFile.getMabTyp().equals(useOn) || mabFile.getMabTyp().equals("*")) {
            isUseSuccess = Boolean.TRUE;
        }

        Integer entryNumber = 0;
        if (condVar.equals("xyz")) {
            if (aktMAB.exists(mabTag, mabInd)) {
                for (MABField mabField : aktMAB.getFields(mabTag, mabInd)) {
                    if (subTag.equals(" ")) {
                        if (op.checkOP(wert, mabField.getValue())) {
                            entryNumber = mabField.getEntryNumber();
                        }
                    } else {
                        if (op.checkOP(wert, mabField.getSubValue(subTag))) {
                            entryNumber = mabField.getEntryNumber();
                        }
                    }
                }
            }
        } else {
            if (op.checkOP(wert, converterINI.getSpeicher(condVar))) {
                entryNumber = MABConstants.useAlways;
            }
        }
        //System.out.println("----------------------------------------------------------------------------");
        //mabFile.dump();
        //System.out.println(entryNumber);
        //System.out.println("----------------------------------------------------------------------------");
        return entryNumber;
    }


    //---------------------------------------------------------------------------------------------
    // helper routines (dumping etc.)
    //---------------------------------------------------------------------------------------------

    // dumping action statistics
    private static void dumpActionStats() {
        System.out.println("Action statistics:");
        Integer cntAll = 0;
        Long sumNanos = 0L;
        for (String action : actionStats.keySet()) {
            Integer count = actionStats.get(action).getCount();
            Long cntNanos = actionStats.get(action).getNanos();
            Double avgNanos = actionStats.get(action).getAvg();
            cntAll += count;
            sumNanos += cntNanos;
            String strCnt = String.format("%,3d",count);
            String strNns = String.format("%,10.1f",Double.valueOf(cntNanos)/1000.0);
            String strAvg = String.format("%,10.1f",avgNanos/1000.0);
            System.out.println("Action: " + String.format("%8s", action) + " - count: " + strCnt + " - mys: " + strNns + "µs  - avg: " + strAvg + "µs");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------");
        String strCnt = String.format("%,3d",cntAll);
        String strNns = String.format("%,10.1f",Double.valueOf(sumNanos)/1000.0);
        System.out.println("Sum:               count: " + strCnt + " - mys: " + strNns + "µs");
        System.out.println();
    }

    // dumping loaded properties
    private static void dumpProperties(Properties config) {
        config.forEach((key, value) -> System.out.println("#" + key + ": " + value + "#"));
    }

    private static void writeBOM(FileOutputStream fileOutputStream) {
        try {
            fileOutputStream.write(0xEF);
            fileOutputStream.write(0xBB);
            fileOutputStream.write(0xBF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
