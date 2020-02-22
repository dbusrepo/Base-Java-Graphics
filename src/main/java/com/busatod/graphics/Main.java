package com.busatod.graphics;

import java.util.Map;
import com.busatod.graphics.application.*;

public class Main {

	public static void main(String[] args) {
		Settings settings = new Settings();
		settings.fullScreen = false;
		new GraphicsApplication(settings);
	}

}
