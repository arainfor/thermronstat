package com.arainfor.thermronstat.logReader;

import com.arainfor.thermronstat.RelayDef;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by ARAINFOR on 1/25/2015.
 */
public class StatusLogReader extends LogReader {

    protected static final String APPLICATION_NAME = "StatusLogReader";
    protected static final int APPLICATION_VERSION_MAJOR = 1;
    protected static final int APPLICATION_VERSION_MINOR = 1;
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

        System.err.println(APPLICATION_NAME + " v" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
        Options options = new Options();
        options.addOption("log", true, "The log file");
        options.addOption("config", true, "The configuration file");
        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(StatusLogReader.class.getSimpleName(), options);
                return;
            }
            if (!cmd.hasOption("log")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(StatusLogReader.class.getSimpleName(), options);
                return;
            }
        } catch (org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
            return;
        }

        String propFileName = "thermostat.properties";
        if (cmd.getOptionValue("config") != null)
            propFileName = cmd.getOptionValue("config");

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

        System.out.println("Parsing Log:" + cmd.getOptionValue("log"));
        StatusLogReader lr = new StatusLogReader(cmd.getOptionValue("log"));
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
        long totalRunTime = 0;
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
                totalRunTime += diff;
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

        long average = 0;
        if (fanCycles > 0) {
            average = totalRunTime / fanCycles;
        }
        long totalPeriod = periodEnd.getTime() - periodStart.getTime();
        double dutyCycle = 0;
        if (totalPeriod > 0) {
            dutyCycle = (double) totalRunTime / (double) totalPeriod;
        }
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        String msg = StringUtils.center(" Summary for Log:" + cmd.getOptionValue("log") + " ", 80, '*');
        System.out.println(msg);

        System.out.println("Log period: " + lr.fmtHhMmSs(totalPeriod) + " Runtime:" + lr.fmtHhMmSs(totalRunTime) + " Long:" + lr.fmtHhMmSs(longestRun) + " Short:" + lr.fmtHhMmSs(shortestRun));
        System.out.println("Fan Cycles: " + fanCycles);
        System.out.println("Heat: " + y1Cycles + " Average:" + lr.fmtHhMmSs(average) + " DutyCycle:" + defaultFormat.format(dutyCycle) + " Runtime:" + lr.fmtHhMmSs(y1Runtime));
        System.out.println("Aux: " + y2Cycles + " Runtime:" + lr.fmtHhMmSs(y2Runtime));

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