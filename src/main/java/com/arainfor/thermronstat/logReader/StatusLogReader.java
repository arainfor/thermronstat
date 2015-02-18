package com.arainfor.thermronstat.logReader;

import com.arainfor.thermronstat.RelayDef;
import com.arainfor.thermronstat.StringConstants;
import com.arainfor.util.file.PropertiesLoader;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ARAINFOR on 1/25/2015.
 *
 * 16-feb-15 akr - Bump build version.  Modify text output.
 * 17-feb-15 akr - Bump minor version.  Add offset and defaults command line parameters.  Improved Stage 2 output.
 *
 */
public class StatusLogReader extends LogReader {

    protected static final String APPLICATION_NAME = "StatusLogReader";
    protected static final int APPLICATION_VERSION_MAJOR = 1;
    protected static final int APPLICATION_VERSION_MINOR = 2;
    protected static final int APPLICATION_VERSION_BUILD = 0;

    static Logger logger = LoggerFactory.getLogger(StatusLogReader.class);
    private final List<StatusLogRecord> statusLogRecord = new ArrayList<StatusLogRecord>();

    public StatusLogReader(String logFileName) {
        super(logFileName);
    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        String logFile = null;
        String logOffset = null;
        System.err.println(APPLICATION_NAME + " v" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
        Options options = new Options();
        options.addOption("log", true, "The log file.");
        options.addOption("config", true, "The configuration file.");
        options.addOption("offset", true, "The offset from log date.");
        options.addOption("defaults", false, "Use defaults for options not provided.");
        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(StatusLogReader.class.getSimpleName(), options);
                return;
            }
            if (cmd.hasOption("log")) {
                logFile = cmd.getOptionValue("log");
            }
            if (cmd.hasOption("offset")) {
                logOffset = cmd.getOptionValue("offset");
            }

        } catch (org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
            return;
        }

        String propFileName = "thermostat.properties";
        if (cmd.getOptionValue("config") != null) {
            propFileName = cmd.getOptionValue("config");
        }

        logger.info("loading...{}", propFileName);

        try {
            Properties props = new PropertiesLoader(propFileName).getProps();

            // Append the system properties with our application properties
            props.putAll(System.getProperties());
            System.setProperties(props);
        } catch (FileNotFoundException fnfe) {
            logger.warn("Cannot load file:", fnfe);
        }

        // This is just our local output format.
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        if (logOffset != null) {
            int idx = Integer.parseInt(logOffset);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -idx);
            SimpleDateFormat sdf = new SimpleDateFormat(StringConstants.FmtDate);
            logFile = System.getenv("LOGPATH") + "/status." + sdf.format(cal.getTime()) + ".log.zip";
        }
        if (logFile == null) {
            logFile = System.getenv("LOGPATH") + "/status.log";
        }


        System.out.println("Parsing Log:" + logFile);
        StatusLogReader lr = new StatusLogReader(logFile);
        lr.read();
        System.out.println("Parsed " + lr.statusLogRecord.size() + " records");

        if (lr.statusLogRecord.size() == 0)
            return;

        Date fanStart = null;
        Date fanStop = null;
        Date y2Start = null;
        Date y2Stop = null;
        Date y1Start = null;
        Date y1Stop = null;
        Date firstStart = null;
        Date periodStart = null;
        Date periodEnd = null;
        long y1TotalRunTime = 0;
        long y2TotalRunTime = 0;
        long shortestRun = Long.MAX_VALUE;
        long longestRun = 0;
        long y1Runtime = 0;
        long y2Runtime = 0;
        int fanCycles = 0;
        int y1Cycles = 0;
        int y2Cycles = 0;

        Double lowReturn = Double.POSITIVE_INFINITY;
        Double highReturn = Double.POSITIVE_INFINITY;
        Double lowPlenum = Double.POSITIVE_INFINITY;
        Double highPlenum = Double.POSITIVE_INFINITY;
        Double lowIndoor = Double.POSITIVE_INFINITY;
        Double highIndoor = Double.POSITIVE_INFINITY;

        for (StatusLogRecord slr : lr.statusLogRecord) {
            periodEnd = slr.date;
            if (periodStart == null)
                periodStart = slr.date;

            // fan cycles
            if (slr.relays.get(RelayDef.G) && fanStart == null) {
                fanStart = slr.date;
                fanStop = null;
            } else if (!slr.relays.get(RelayDef.G) && fanStart != null) {
                fanStop = slr.date;
            }

            // count stage1 cycles
            if (slr.relays.get(RelayDef.Y1) && y1Start == null) {
                y1Start = slr.date;
                y1Stop = null;
                if (firstStart == null)
                    firstStart = slr.date;
            } else if (!slr.relays.get(RelayDef.Y1) && y1Start != null) {
                y1Stop = slr.getDate();
            }


            // count stage2 cycles
            if (slr.relays.get(RelayDef.Y2) && y2Start == null) {
                y2Start = slr.date;
                y2Stop = null;
            } else if (!slr.relays.get(RelayDef.Y2) && y2Start != null) {
                y2Stop = slr.getDate();
            }

            if (y1Start != null && slr.temperatures.size() >= 3) {
                if (highIndoor == Double.POSITIVE_INFINITY || slr.temperatures.get(0).getValue() > highIndoor)
                    highIndoor = slr.temperatures.get(0).getValue();

                if (lowIndoor == Double.POSITIVE_INFINITY || slr.temperatures.get(0).getValue() < lowIndoor)
                    lowIndoor = slr.temperatures.get(0).getValue();

                if (highPlenum == Double.POSITIVE_INFINITY || slr.temperatures.get(1).getValue() > highPlenum)
                    highPlenum = slr.temperatures.get(1).getValue();

                if (lowPlenum == Double.POSITIVE_INFINITY || slr.temperatures.get(1).getValue() < lowPlenum)
                    lowPlenum = slr.temperatures.get(1).getValue();

                if (highReturn == Double.POSITIVE_INFINITY || slr.temperatures.get(2).getValue() > highReturn)
                    highReturn = slr.temperatures.get(2).getValue();

                if (lowReturn == Double.POSITIVE_INFINITY || slr.temperatures.get(2).getValue() < lowReturn)
                    lowReturn = slr.temperatures.get(2).getValue();

            }

            // fan cylcle complete
            if (fanStart != null && fanStop != null) {
                fanStart = null;
                fanStop = null;
                fanCycles++;
            }

            // stage2 clycle complete
            if (y2Start != null && y2Stop != null) {
                y2Stop = slr.getDate();
                y2Runtime += y2Stop.getTime() - y2Start.getTime();
                long diff = y2Stop.getTime() - y2Start.getTime();//as given
                y2TotalRunTime += diff;
                y2Start = null;
                y2Stop = null;
                y2Cycles++;
            }

            // stage1 cycle complete
            if (y1Start != null && y1Stop != null) {
                y1Stop = slr.getDate();
                y1Runtime += y1Stop.getTime() - y1Start.getTime();

                // show the completed cycle
                long diff = y1Stop.getTime() - y1Start.getTime();//as given
                y1TotalRunTime += diff;
                if (diff < shortestRun)
                    shortestRun = diff;
                if (diff > longestRun)
                    longestRun = diff;

                System.out.println("Start: " + formatter.format(y1Start) + " Stop: " + formatter.format(y1Stop) + " Runtime: " + lr.fmtHhMmSs(diff));
                System.out.println("Temp Hi/Low:" + highIndoor + "/" + lowIndoor + " Return: " + highReturn + "/" + lowReturn + " Plenum: " + highPlenum + "/" + lowPlenum);
                System.out.println();
                highIndoor = lowIndoor = highPlenum = lowPlenum = highReturn = lowReturn = Double.POSITIVE_INFINITY;

                y1Start = null;
                y1Stop = null;
                y1Cycles++;

            }

            //System.out.println(slr.toString());
        }

        long y1Average = 0;
        if (y1Cycles > 0) {
            y1Average = y1TotalRunTime / y1Cycles;
        }

        long y2Average = 0;
        if (y2Cycles > 0) {
            y2Average = y2TotalRunTime / y2Cycles;
        }
        long totalPeriod = periodEnd.getTime() - periodStart.getTime();
        double y1DutyCycle = 0;
        double y2DutyCycle = 0;
        if (totalPeriod > 0) {
            y1DutyCycle = (double) y1TotalRunTime / (double) totalPeriod;
            y2DutyCycle = (double) y2TotalRunTime / (double) totalPeriod;
        }
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);

        String runDate = new SimpleDateFormat(StringConstants.FmtDate).format(periodEnd);
        String msg = StringUtils.center(" Summary for Log:" + cmd.getOptionValue("log") + " ending: " + runDate + " ", 80, '*');
        System.out.println(msg);

        System.out.println("Log period: " + lr.fmtHhMmSs(totalPeriod) + " Runtime: " + lr.fmtHhMmSs(y1TotalRunTime) + " Long: " + lr.fmtHhMmSs(longestRun) + " Short: " + lr.fmtHhMmSs(shortestRun));
        System.out.println("Stage 1: " + y1Cycles + " Average: " + lr.fmtHhMmSs(y1Average) + " DutyCycle: " + defaultFormat.format(y1DutyCycle) + " Runtime: " + lr.fmtHhMmSs(y1Runtime));
        System.out.println("Stage 2: " + y2Cycles + " Average: " + lr.fmtHhMmSs(y2Average) + " DutyCycle: " + defaultFormat.format(y2DutyCycle) + " Runtime: " + lr.fmtHhMmSs(y2Runtime));
        System.out.println("Recirculation Fan Cycles: " + fanCycles);

    }

    /**
     * This implementation reads 2 lines for one message.
     *
     * @throws IOException
     */
    @Override
    void read() throws IOException {
        String thisLine;

        boolean bDateFound = false;
        while ((thisLine = br.readLine()) != null) {
            // read until we find a record that starts with the date!
            if (!bDateFound) {
                bDateFound = thisLine.startsWith("20");
            }

            if (!bDateFound) {
                continue;
            }

            try {
                parse(getDate(), thisLine);
            } catch (Exception e) {
                logger.warn("Error {} Cannot decode record:{}", e.getMessage(), thisLine);
            }
            bDateFound = false;
        }

    }

    protected void parse(String yyyymmdd, String thisLine) throws ParseException {
        statusLogRecord.add(new StatusLogRecord(yyyymmdd, thisLine));
    }


}