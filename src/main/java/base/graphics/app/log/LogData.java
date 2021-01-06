package base.graphics.app.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogData extends LogRecord {

	private static final long serialVersionUID = 1L;

	private final String category;

	public LogData(Level level, String category, String msg, Throwable ex) {
		super(level, msg);
		setThrown(ex);
		this.category = category;
	}

	String getCategory() {
		return category;
	}

}
