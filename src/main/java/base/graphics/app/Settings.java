package base.graphics.app;

import java.util.logging.Level;

public class Settings {

	static final String TITLE = "Java Graphics";
	static final int WIDTH = 1024;
	static final int HEIGHT = 768;
	static final int NUM_BUFFERS = 3;
	static final int BIT_DEPTH = 32;
	static final int TARGET_FPS = 120;

	public String title = TITLE;
	public int winWidth = WIDTH;
	public int winHeight = HEIGHT;
	public int bitDepth = BIT_DEPTH;
	public boolean fullScreen = false;
	public int numBuffers = NUM_BUFFERS;
	public int targetFps = TARGET_FPS;
	public boolean showFps = true;
	public boolean showCapabilities = true;
	public boolean showMenu = true;

	public boolean useLogWindow = true;
	public int logWinWidth = 500;
	public int logWinHeight = 500;
	public boolean useLogFile = true;
	public Level logLevel = Level.ALL;

	public void toggleFullscreen() {
		fullScreen = !fullScreen;
	}
}
