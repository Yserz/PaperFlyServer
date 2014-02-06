package de.fhb.paperfly.server.logging.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;

/**
 * Local Admin Interface for LoggingServices.
 *
 * @author Michael Koppen
 */
@Local
public interface LoggingServiceLocalAdmin {

	void setAllLoggerLoggingLevel(Level level);

	void setConsoleHandlerLoggingLevel(Level level);

	void setLoggerLoggingLevel(String className, Level level);

	void showLoggers();

	void log(String className, Level level, String message);

	void log(String className, Level level, String message, Object param);

	void log(String className, Level level, String message, Object[] param);
}
