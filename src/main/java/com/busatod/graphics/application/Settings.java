package com.busatod.graphics.application;

public class Settings {
	static final int WIDHT = 1024;
	static final int HEIGHT = 768;
	static final int NUM_BUFFERS = 3;
	static final int TARGET_FPS = 80;
	static final String TITLE = "Java Graphics";
	static final int BIT_DEPTH = 32;

	public String title = TITLE;
	public int width = WIDHT;
	public int height = HEIGHT;
	public int bitDepth = BIT_DEPTH;
	public boolean fullScreen;
	public boolean debugInfo = true;
	public int numBuffers = NUM_BUFFERS;
	public int targetFps = TARGET_FPS;
}
