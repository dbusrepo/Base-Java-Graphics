package base.graphics.app.log;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.SwingUtilities;

import base.graphics.app.Settings;
import base.graphics.app.log.Log.PrintOptions;

public class WinLogHandler extends Handler {

	private LogWindow window;

	public WinLogHandler(Settings settings, Formatter formatter) {
		setFormatter(formatter);
		initLogWindow(settings);
	}

	private void initLogWindow(Settings settings) {
		LogWindow[] res = new LogWindow[1];
		try {
			SwingUtilities.invokeAndWait(() -> {
				var logWin = new LogWindow(settings.logWinWidth, settings.logWinHeight);
				logWin.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						super.windowClosing(e);
						Log.closeWinHandler();
//						JOptionPane.showConfirmDialog(null, "Are sure to close!");
					}
				});
				res[0] = logWin;
			});
			window = res[0];
		} catch (Exception e) {
			// TODO
		}
	}

	@Override
	public void publish(LogRecord record) {
		if (record instanceof LogData) {
			String message = getFormatter().format(record);
			LogData logData = (LogData) record;
			showOnWindow(logData, message);
		}
	}

	private void showOnWindow(LogData logData, String message) {
		Color color = detColor(logData.getLevel());
		window.append(message, color);
	}

	private Color detColor(Level level) {
		PrintOptions printOption = Log.getPrintOptions(level);
		return printOption != null ? printOption.getColor() : Color.BLACK;
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
		// https://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
		window.setVisible(false);
		window.dispose();
	}
}
