package com.busatod.graphics._apps.image_loader;

import com.busatod.graphics.app.GraphicsApplication;
import com.busatod.graphics.app.Settings;
import com.busatod.graphics.input.InputAction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class ImageLoader extends GraphicsApplication {

	private final static String IMAGE_DIR = "/image_loader/pics"; // relativo a /resources
	private final static String FILE_NAME = "img0.jpg";

	private InputAction exitAction;
	private BufferedImage im;

	public static void main(String[] args) {
		new ImageLoader();
	}

	public ImageLoader() {
		Settings settings = new Settings();
		start(settings);
	}

	@Override
	protected void appInit() {
		// load the image
		im = loadImage(IMAGE_DIR + "/" + FILE_NAME);
		// input init
		exitAction = new InputAction("Exit", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		inputManager.mapToKey(KeyEvent.VK_Q, exitAction);
	}

	@Override
	protected void appUpdate(long elapsedTime) {

	}

	@Override
	protected void checkSystemInput() {
		super.checkSystemInput();
		if (exitAction.isPressed()) {
			stopApp();
		}
	}

	@Override
	protected void appDraw() {

	}

	@Override
	protected void showStats(Graphics2D g) {
		super.showStats(g);
	}

	@Override
	protected void appFinishOff() {

	}

	@Override
	protected void appPrintFinalStats() {

	}
}
