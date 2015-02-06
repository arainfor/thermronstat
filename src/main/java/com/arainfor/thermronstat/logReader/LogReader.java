package com.arainfor.thermronstat.logReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by arainfor on 2/5/15.
 */
public abstract class LogReader {

    private static final Logger logger = LoggerFactory.getLogger(LogReader.class);
    private BufferedReader br;
    private String dateString;

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
        dateString = formatter.format(currentDate.getTime());
        if (logFileName.contains(File.separator) && logFileName.endsWith(".zip")) {
            logFileName = logFileName.substring(logFileName.lastIndexOf(File.separator) + 1);
        }
        if (logFileName.contains(".") && logFileName.endsWith(".zip")) {
            String[] tokens = logFileName.split("\\.");
            dateString = tokens[1];
        }

        try {

            InputStream stream;
            if (entry != null)
                stream = zipFile.getInputStream(entry);
            else
                stream = new FileInputStream(logFileName);

            // open input stream for reading purpose.
            br = new BufferedReader(new InputStreamReader(stream));

        } catch (Exception e) {
            logger.error("Fatal Error:", e);
        }

    }

    protected String getDate() {
        return dateString;
    }

    protected void read() throws IOException {
        String thisLine;

        while ((thisLine = br.readLine()) != null) {
            try {
                parse(getDate(), thisLine);
            } catch (Exception e) {
                logger.warn("Cannot decode record:{}", thisLine);
            }
        }

    }

    protected abstract void parse(String dateString, String data) throws ParseException;

    protected String fmtHhMmSs(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    protected String fmtHhMmSsMmm(long millis) {
        return String.format("%02d:%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));
    }


    protected Calendar getCal(long timems) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timems);
        return cal;
    }

    protected long timeDiff(long fanStart, long fanStop) {
        long diff = fanStop - fanStart;//as given

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        return diff;
    }

}
