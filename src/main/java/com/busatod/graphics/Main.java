package com.busatod.graphics;

import java.util.Map;
import com.busatod.graphics.application.*;

public class Main {

	public static void main(String[] args) {
//		String os = System.getProperty("os.name");
//		System.setProperty("sun.java2d.trace", "count");
//		if(os.contains("Windows")){
//			System.setProperty("sun.java2d.d3d", "True");
//		}else{
//			System.setProperty("sun.java2d.opengl", "True");
//		}
		Settings settings = new Settings();
		new GraphicsApplication(settings);
	}

}
