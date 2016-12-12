package bgu.spl.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyFormat extends Formatter {

    @Override
    public String format(LogRecord record) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		Date date = new Date();
        return record.getLevel() + ", " + dateFormat.format(date) +  ": " + record.getMessage() + "\n";
 
    }
}