package base.graphics.app.log;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import base.graphics.app.Settings;

public class Log {

	private static Logger logger; // = Logger.getLogger(Log.class.getName());
	private static WinLogHandler winLogHandler;
	private static FileHandler fileHandler;

	static class PrintOptions {
		private final String category;
		private final Color color;

		protected PrintOptions(String category, Color color) {
			this.category = category;
			this.color = color;
		}

		String getCategory() {
			return category;
		}

		Color getColor() {
			return color;
		}

	}

	private static Map<Level, PrintOptions> printOptionsMap = new HashMap<>();

	static {
		printOptionsMap.put(Level.SEVERE, new PrintOptions(" ERROR: ", Color.RED));
		printOptionsMap.put(Level.WARNING, new PrintOptions("  WARN: ", Color.ORANGE));
		printOptionsMap.put(Level.INFO, new PrintOptions("  INFO: ", Color.GREEN));
		printOptionsMap.put(Level.CONFIG, new PrintOptions(" CONFIG: ", Color.GRAY));
		printOptionsMap.put(Level.FINE, new PrintOptions(" DEBUG: ", Color.GRAY));
		printOptionsMap.put(Level.FINER, new PrintOptions(" TRACE: ", Color.BLACK));
	}

	public static void init(Settings settings, Logger logger) throws Exception {
		Log.logger = logger;
		logger.setLevel(settings.logLevel);
		logger.setUseParentHandlers(false);
		LogDataFormatter formatter = new LogDataFormatter(Instant.now());
		if (settings.useLogWindow) {
			winLogHandler = new WinLogHandler(settings, formatter);
			logger.addHandler(winLogHandler);
		}
		if (settings.useLogFile) {
			try {
				fileHandler = new FileLogHandler("log%u.txt", 1000000, 1);
			} catch (Exception ex) {
				closeWinHandler();
				System.err.println("Log file creation failed.");
				throw ex;
			}
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void finishOff() {
		closeWinHandler();
		if (fileHandler != null) {
			logger.removeHandler(fileHandler);
			fileHandler.close();
		}
	}

	static synchronized void closeWinHandler() {
		if (winLogHandler != null) {
			logger.removeHandler(winLogHandler);
			winLogHandler.close();
			winLogHandler = null;
		}
	}

	public static PrintOptions getPrintOptions(Level level) {
		return printOptionsMap.get(level);
	}

	public static void setLevel(Level level) {
		logger.setLevel(level);
	}

	public static void error(String message) {
		logger.log(buildError(null, message, null));
	}

	public static void error(String message, Throwable ex) {
		logger.log(buildError(null, message, ex));
	}

	public static void error(String category, String message, Throwable ex) {
		logger.log(buildError(category, message, ex));
	}

	public static void error(String category, String message) {
		logger.log(buildError(category, message, null));
	}

	public static void warn(String message) {
		logger.log(buildWarning(null, message, null));
	}

	public static void warn(String message, Throwable ex) {
		logger.log(buildWarning(null, message, ex));
	}

	public static void warn(String category, String message, Throwable ex) {
		logger.log(buildWarning(category, message, ex));
	}

	public static void warn(String category, String message) {
		logger.log(buildWarning(category, message, null));
	}

	public static void info(String message) {
		logger.log(buildInfo(null, message, null));
	}

	public static void info(String category, String message) {
		logger.log(buildInfo(category, message, null));
	}

	public static void config(String message) {
		logger.log(buildConfig(null, message, null));
	}

	public static void config(String category, String message) {
		logger.log(buildConfig(category, message, null));
	}

	public static void debug(String message) {
		logger.log(buildDebug(null, message, null));
	}

	public static void debug(String category, String message) {
		logger.log(buildDebug(category, message, null));
	}

	public static void trace(String message) {
		logger.log(buildTrace(null, message, null));
	}

	public static void trace(String category, String message) {
		logger.log(buildTrace(category, message, null));
	}

	private static LogData buildError(String category, String message, Throwable ex) {
		return buildLogData(Level.SEVERE, category, message, ex);
	}

	private static LogData buildWarning(String category, String message, Throwable ex) {
		return buildLogData(Level.WARNING, category, message, ex);
	}

	private static LogData buildInfo(String category, String message, Throwable ex) {
		return buildLogData(Level.INFO, category, message, ex);
	}

	private static LogData buildConfig(String category, String message, Throwable ex) {
		return buildLogData(Level.CONFIG, category, message, ex);
	}

	private static LogData buildDebug(String category, String message, Throwable ex) {
		return buildLogData(Level.FINE, category, message, ex);
	}

	private static LogData buildTrace(String category, String message, Throwable ex) {
		return buildLogData(Level.FINER, category, message, ex);
	}

	private static LogData buildLogData(Level level, String category, String message, Throwable ex) {
		return new LogData(level, category, message, ex);
	}

	private Log() {
	}
}
