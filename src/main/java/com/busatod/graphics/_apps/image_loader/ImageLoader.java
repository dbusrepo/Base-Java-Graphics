package com.busatod.graphics._apps.image_loader;

import com.busatod.graphics.app.GraphicsApplication;
import com.busatod.graphics.app.IAppLogic;
import com.busatod.graphics.app.Settings;

public class ImageLoader implements IAppLogic {

	public static void main(String[] args) {
		new ImageLoader();
	}

	public ImageLoader() {
		Settings settings = new Settings();
		new GraphicsApplication(settings, this);
	}

	@Override
	public void init() {

	}

	@Override
	public void draw() {

	}

	@Override
	public void finish() {

	}

	@Override
	public void printFinalStats() {

	}

	@Override
	public void update(long elapsedTime) {

	}
}
