package com.busatod.graphics.application;

public class Settings {
	static final int WIDHT = 1024;
	static final int HEIGHT = 768;
	static final int NUM_BUFFERS = 3;
	static final int TARGET_FPS = 80;
	static final String TITLE = "Java Graphics";

	String title = TITLE;
	int width = WIDHT;
	int height = HEIGHT;
	boolean fullScreen;
	boolean debugInfo = true;
	int numBuffers = NUM_BUFFERS;
	int targetFps = TARGET_FPS;
}
