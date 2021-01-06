package base.graphics.app.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import base.graphics.app.log.Log.PrintOptions;

public class LogDataFormatter extends Formatter {

	private final Instant startTime;

	protected LogDataFormatter(Instant instant) {
		this.startTime = instant;
	}

	@Override
	public String format(LogRecord record) {

		StringBuilder builder = new StringBuilder(256);

		long seconds = Duration.between(startTime, record.getInstant()).getSeconds();
		String delta = String.format("%02d:%02d", (seconds % 3600) / 60, seconds % 60);
		builder.append(delta);

		LogData logData = (LogData) record;
		String category = getCategory(logData.getLevel());

		if (category != null) {
			builder.append('[');
			builder.append(category);
			builder.append("] ");
		}

		builder.append(record.getMessage());

		if (record.getThrown() != null) {
			StringWriter writer = new StringWriter(256);
			record.getThrown().printStackTrace(new PrintWriter(writer));
			builder.append("\n");
			builder.append(writer.toString().trim());
		}

		builder.append("\n");

		return builder.toString();
	}

	private String getCategory(Level level) {
		PrintOptions printOption = Log.getPrintOptions(level);
		return printOption != null ? printOption.getCategory() : null;
	}

}
