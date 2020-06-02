package com.busatod.graphics._apps.image_loader;

import com.busatod.graphics.app.GraphicsApplication;
import com.busatod.graphics.app.Settings;
import com.busatod.graphics.input.InputAction;

import java.awt.event.KeyEvent;

public class ImageLoader extends GraphicsApplication {

	private InputAction exitAction;

	public static void main(String[] args) {
		new ImageLoader();
	}

	public ImageLoader() {
		Settings settings = new Settings();
		start(settings);
	}

	@Override
	protected void appInit() {
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
	protected void appFinishOff() {

	}

	@Override
	protected void appPrintFinalStats() {

	}
}
