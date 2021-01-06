package base.graphics.app.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;

public class FileLogHandler extends FileHandler {

	private static final String LOG_FILE_PATH = "log/";

	public FileLogHandler(String file, int limit, int count) throws IOException, SecurityException {
		super(initFilePath(file), limit, count);
	}

	private static String initFilePath(String file) throws IOException {
		Files.createDirectories(Paths.get(LOG_FILE_PATH));
		return LOG_FILE_PATH + file;
	}

}
