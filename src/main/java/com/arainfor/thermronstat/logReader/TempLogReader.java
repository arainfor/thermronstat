package com.arainfor.thermronstat.logReader;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARAINFOR on 1/25/2015.
 */
public class TempLogReader extends LogReader {

    Logger logger = LoggerFactory.getLogger(TempLogReader.class);
    List<TemperatureLogRecord> logRecords = new ArrayList<TemperatureLogRecord>();

    public TempLogReader(String logFileName) {
        super(logFileName);
    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Starting...");
        Options options = new Options();
        options.addOption("log", true, "The log file");
        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(TempLogReader.class.getSimpleName(), options);
                return;
            }
            if (!cmd.hasOption("log")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(TempLogReader.class.getSimpleName(), options);
                return;
            }
        } catch (org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
            return;
        }

        // This is just our local output format.
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        System.out.println("Parsing Log:" + cmd.getOptionValue("log"));
        TempLogReader lr = new TempLogReader(cmd.getOptionValue("log"));
        lr.read();
        System.out.println("Parsed " + lr.logRecords.size() + " records");

        long totalRunTime = 0;
        long shortestRun = Long.MAX_VALUE;
        long longestRun = 0;

        for (TemperatureLogRecord tlr : lr.logRecords) {

            System.out.println(tlr.toString());
        }

        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        String msg = StringUtils.center(" Summary for Log:" + cmd.getOptionValue("log") + " ", 80, '*');
        System.out.println(msg);


    }

    protected void parse(String yyyymmdd, String thisLine) throws ParseException {
        logRecords.add(new TemperatureLogRecord(yyyymmdd, thisLine));
    }


}