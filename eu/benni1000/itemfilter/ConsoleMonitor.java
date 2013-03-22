package eu.benni1000.itemfilter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ConsoleMonitor implements Filter {

    public ConsoleMonitor() {
    }

    @Override
    public boolean isLoggable(LogRecord arg0) {
        //This fixes a bug in the sql library which would normally spam the logs.
        //The Error gets triggered by performing a INSERT INTO or UPDATE statement.
        if(arg0.getMessage().contains("Error at SQL Query: query does not return ResultSet")) {
            return false;
        }
        return true;
    }

}