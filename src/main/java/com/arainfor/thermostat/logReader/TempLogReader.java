package com.arainfor.thermostat.logReader;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARAINFOR on 1/25/2015.
 */
public class TempLogReader extends LogReader {

    private final List<TemperatureLogRecord> logRecords = new ArrayList<TemperatureLogRecord>();

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

        System.out.println("Parsing Log:" + cmd.getOptionValue("log"));
        TempLogReader lr = new TempLogReader(cmd.getOptionValue("log"));
        lr.read();
        System.out.println("Parsed " + lr.logRecords.size() + " records");

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