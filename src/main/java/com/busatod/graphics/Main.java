package com.busatod.graphics;

import java.util.Map;
import com.busatod.graphics.display.*;

public class Main {

	private static final boolean USE_FULLSCREEN = false;
	private static final int DEFAULT_WIDTH = 1024;
	private static final int DEFAULT_HEIGHT = 768;
	private static final boolean PRINT_DEBUG_INFO = true;

	static class ArgSettings {

		// TARGET_FPS, VSYNC, ...
		//"true".equalsIgnoreCase(args.get("vsync"));

		private Map<String, String> settings;
		// javaGraphics -f 1024 768

		ArgSettings(String[] args) {
		}

		boolean useFullScreen() {
			return USE_FULLSCREEN;
		}

		int getWidth() {
			return DEFAULT_WIDTH;
		}

		int getHeight() {
			return DEFAULT_HEIGHT;
		}

	}

	public static void main(String[] args) {
		ArgSettings settings = new ArgSettings(args);
		settings.useFullScreen();
		new GraphicsApplication(settings.getWidth(), settings.getHeight(), settings.useFullScreen(), PRINT_DEBUG_INFO);
	}

}
