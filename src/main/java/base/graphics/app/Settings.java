package base.graphics.app;

import java.awt.Color;
import java.util.logging.Level;

public class Settings {

	static final String TITLE = "Java Graphics";
	static final int WIN_WIDTH = 1024;
	static final int WIN_HEIGHT = 768;
	static final int NUM_BUFFERS = 3;
	static final int BIT_DEPTH = 32;
	static final int TARGET_FPS = 120;

	public String title = TITLE;
	public int winWidth = WIN_WIDTH;
	public int winHeight = WIN_HEIGHT;
	public int bitDepth = BIT_DEPTH;
	public boolean fullScreen = false;
	public int numBuffers = NUM_BUFFERS;
	public int targetFps = TARGET_FPS;
	public boolean showGraphCapabilities = true;
	public boolean showAccelMemory = false;
	public boolean showFps = true;
	public boolean showTitleBar = true;
	public boolean showMenu = true;

	public static int statsFontSize = 10;
	public static String statsFontName = "SansSerif";
	public static Color statsTextCol = Color.YELLOW;
	public static Color statsBgCol = Color.RED;

	public boolean useLog = false;
	public boolean showLogWindow = true;
	public int logWinWidth = 500;
	public int logWinHeight = 500;
	public boolean useLogFile = true;
	public Level logLevel = Level.ALL;

	public void toggleFullscreen() {
		fullScreen = !fullScreen;
	}
}
