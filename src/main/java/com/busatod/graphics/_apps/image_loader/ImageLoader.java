package com.busatod.graphics._apps.image_loader;

import com.busatod.graphics.app.GraphicsApplication;
import com.busatod.graphics.app.Settings;
import com.busatod.graphics.app.input.InputAction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class ImageLoader extends GraphicsApplication
{
	
	private final static String IMAGE_DIR = "/image_loader/pics"; // relativo a /resources
	private final static String FILE_NAME = "img0.jpg";
	
	private InputAction   exitAction;
	private BufferedImage im;
	
	public ImageLoader()
	{
		Settings settings = new Settings();
		start(settings);
	}
	
	public static void main(String[] args)
	{
		new ImageLoader();
	}
	
	@Override
	protected void appInit()
	{
		// load the image
		im = loadImage(IMAGE_DIR + "/" + FILE_NAME);
		// input init
		exitAction = new InputAction("Exit", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		inputManager.mapToKey(KeyEvent.VK_Q, exitAction);
	}
	
	@Override
	protected void appUpdate(long elapsedTime)
	{
	}
	
	@Override
	protected void checkSystemInput()
	{
		super.checkSystemInput();
		if (exitAction.isPressed()) {
			stopApp();
		}
	}
	
	@Override
	protected void appDraw()
	{
//		super.appDraw();
//		Graphics2D gBuffer = (Graphics2D) bufferImage.getGraphics();
//		gBuffer.drawImage(im, 0, 0, null);
//		int[] imageBuffer = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
//		int image_offset = 0;
//		int buffer_offset = 0;
//		for (int y = 0; y != im.getHeight(); ++y) {
//			System.arraycopy(imageBuffer, image_offset, buffer, buffer_offset, im.getWidth());
//			image_offset += im.getWidth();
//			buffer_offset += bufferImage.getWidth();
//		}
		int[] imageBuffer = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
		int image_offset = 0;
		int buffer_offset = 0;
		int image_width = im.getWidth();
		for (int y = 0; y != im.getHeight(); ++y) {
			int p_b = buffer_offset;
			int p_i = image_offset;
			for (int x = image_width; x != 0; --x) {
				buffer[p_b++] = imageBuffer[p_i++];
			}
			image_offset += im.getWidth();
			buffer_offset += bufferImage.getWidth();
		}
	}
	
	@Override
	protected void showStats(Graphics2D g)
	{
		super.showStats(g);
	}
	
	@Override
	protected void appFinishOff()
	{
		writeImage(im, "out", "png");
	}
	
	@Override
	protected void appPrintFinalStats()
	{
	}
}
