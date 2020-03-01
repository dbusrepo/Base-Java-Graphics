package com.busatod.graphics.application;

public class Settings {
	static final String TITLE = "Java Graphics";
	static final int WIDTH = 1024;
	static final int HEIGHT = 768;
	static final int NUM_BUFFERS = 3;
	static final int BIT_DEPTH = 32;
	static final int TARGET_FPS = 1000;

	public String title = TITLE;
	public int width = WIDTH;
	public int height = HEIGHT;
	public int bitDepth = BIT_DEPTH;
	public boolean fullScreen = false;
	public boolean debugInfo = true;
	public int numBuffers = NUM_BUFFERS;
	public int targetFps = TARGET_FPS;
	public boolean showCapabilities = true;

	public void toggleFullscreen() {
		fullScreen = !fullScreen;
	}
}
