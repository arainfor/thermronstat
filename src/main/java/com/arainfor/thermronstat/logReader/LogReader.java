package com.arainfor.thermronstat.logReader;

import com.arainfor.thermronstat.RelayDef;
import org.apache.commons.cli.*;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by ARAINFOR on 1/25/2015.
 */
public class LogReader {

    List<StatusLogRecord> statusLogRecord = new ArrayList<StatusLogRecord>();

    public LogReader(String logFileName) {

        ZipFile zipFile = null;
        ZipEntry entry = null;
        if (logFileName.endsWith(".zip")) {

            try {
                zipFile = new ZipFile(logFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while(entries.hasMoreElements()){
                entry = entries.nextElement();
            }
        }

        Calendar currentDate = Calendar.getInstance(); //Get the current date
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd"); //format it as per your requirement
        String dateString = formatter.format(currentDate.getTime());
        if (logFileName.contains(File.separator) && logFileName.endsWith(".zip")) {
            logFileName = logFileName.substring(logFileName.lastIndexOf(File.separator) + 1);
        }
        if (logFileName.contains(".") && logFileName.endsWith(".zip")) {
            String[] tokens = logFileName.split("\\.");
            dateString = tokens[1];
        }
        String thisLine = null;
        try {

            InputStream stream;
            if (entry != null)
                 stream = zipFile.getInputStream(entry);
            else
                stream = new FileInputStream(logFileName);

            // open input stream for reading purpose.
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            while ((thisLine = br.readLine()) != null) {
                parse(dateString, thisLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(String yyyymmdd, String thisLine) throws ParseException {
        statusLogRecord.add(new StatusLogRecord(yyyymmdd, thisLine));
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
                hf.printHelp(LogReader.class.getSimpleName(), options);
                return;
            }
            if (!cmd.hasOption("log")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(LogReader.class.getSimpleName(), options);
                return;
            }
        } catch (org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        LogReader lr = new LogReader(cmd.getOptionValue("log"));

        //LogReader lr = new LogReader("data\\status.2015-01-24.log.zip");
        //LogReader lr = new LogReader("data\\status.log");

        Date fanStart = null;
        Date fanStop = null;
        Date firstStart = null;
        Date lastStart = null;
        Date periodStart = null;
        Date periodEnd = null;
        long totalRunTime = 0;
        long shortestRun = Long.MAX_VALUE;
        long longestRun = 0;
        int numCycles = 0;

        for (StatusLogRecord slr : lr.statusLogRecord) {
            periodEnd = slr.date;
            if (periodStart == null)
                periodStart = slr.date;

            if (slr.relays.get(RelayDef.G).booleanValue() && fanStart == null) {
                fanStart = slr.date;
                fanStop = null;
                if (firstStart == null)
                    firstStart = slr.date;
                lastStart = slr.date;
            }
            else if (!slr.relays.get(RelayDef.G).booleanValue() && fanStart != null) {
                fanStop = slr.date;
            }

            if (fanStart != null && fanStop != null) {
                long diff = fanStop.getTime() - fanStart.getTime();//as given

                System.out.println("Start: " +  formatter.format(fanStart) + " Stop: " + formatter.format(fanStop) + " Runtime: " + lr.fmtHhMmSs(diff));
                fanStart = null;
                fanStop = null;
                numCycles++;
                totalRunTime += diff;
                if (diff < shortestRun)
                    shortestRun = diff;
                if (diff > longestRun)
                    longestRun = diff;
            }

            //System.out.println(slr.toString());
        }

        long average = totalRunTime / numCycles;
        long totalPeriod = periodEnd.getTime() - periodStart.getTime();
        double dutyCylce = (double)totalRunTime / (double)totalPeriod;
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        System.out.println("Num Cycles: " + numCycles + " Total Runtime:" + lr.fmtHhMmSs(totalRunTime) + " Long:" + lr.fmtHhMmSs(longestRun) + " Short:" + lr.fmtHhMmSs(shortestRun) + " Average:"  + lr.fmtHhMmSs(average) + " DutyCylce:" + defaultFormat.format(dutyCylce));
    }

    private String fmtHhMmSs(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private String fmtHhMmSsMmm(long millis) {
        return String.format("%02d:%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));
    }


    private Calendar getCal(long timems) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timems);
        return cal;
    }
    private long timeDiff(long fanStart, long fanStop) {
        long diff = fanStop - fanStart;//as given

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        return diff;
    }
}