package de.fhb.paperfly.server.logging.service;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

/**
 * This class is for the global logging.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Singleton
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class LoggingService implements LoggingServiceLocal, LoggingServiceLocalAdmin {

	private static Logger LOG;
	private Set<String> loggerNames;
	private Level globalLoggingLevel = Level.ALL;

	public LoggingService() {
		loggerNames = new HashSet<String>();
		LOG = Logger.getLogger(this.getClass().getName());
	}

	@PostConstruct
	private void init() {
		LOG = this.getLogger(this.getClass().getName());
		setAllLoggerLoggingLevel(globalLoggingLevel);
	}

	@Override
	public void showLoggers() {
		LOG.log(Level.INFO, "-------------------------------------------------------");
		LOG.log(Level.INFO, " +	Showing registered Loggers:");
		for (String className : loggerNames) {
			LOG.log(Level.INFO, " -		-> {0}	:	{1}", new Object[]{this.getLogger(className).getLevel(), className});
		}
	}

	@Override
	public void setConsoleHandlerLoggingLevel(Level level) {
		Logger givenLogger = Logger.getLogger("");
		LOG.log(Level.INFO, " -	Setting all Loggers to Level \"{0}\"", new Object[]{level});
		// Handler for console (reuse it if it already exists)
		Handler consoleHandler = null;
		//see if there is already a console handler
		for (Handler handler : givenLogger.getHandlers()) {
			if (handler instanceof ConsoleHandler) {
				//found the console handler
				consoleHandler = handler;
				break;
			}
		}


		if (consoleHandler == null) {
			//there was no console handler found, create a new one
			consoleHandler = new ConsoleHandler();
			givenLogger.addHandler(consoleHandler);
		}
		//set the console handler to fine:
		consoleHandler.setLevel(level);
	}

	@Override
	public void setLoggerLoggingLevel(String className, Level level) {
		Logger tempLogger = this.getLogger(className);
		LOG.log(Level.INFO, " -	Setting Logger \"{0}\" from Level \"{1}\" to Level \"{2}\"", new Object[]{tempLogger.getName(), tempLogger.getLevel(), level});
		tempLogger.setLevel(level);
	}

	@Override
	public void setAllLoggerLoggingLevel(Level level) {
		LOG.log(Level.INFO, " -	Setting All Loggers to Level \"{0}\"", new Object[]{level});
		for (String className : loggerNames) {
			Logger.getLogger(className).setLevel(level);
		}
		globalLoggingLevel = level;
	}

	@Override
	public void log(String className, Level level, String message) {
		getLogger(className).log(level, message);
	}

	@Override
	public void log(String className, Level level, String message, Object param) {
		getLogger(className).log(level, message, param);
	}

	@Override
	public void log(String className, Level level, String message, Object[] param) {
		getLogger(className).log(level, message, param);
	}

	private Logger getLogger(String className) {
		loggerNames.add(className);
		Logger tempLogger = Logger.getLogger(className);
		tempLogger.setLevel(globalLoggingLevel);
		return tempLogger;
	}

	@Schedule(second = "0", minute = "*/5", hour = "*")
	private void cleanUp() {
		showLoggers();
	}
}
