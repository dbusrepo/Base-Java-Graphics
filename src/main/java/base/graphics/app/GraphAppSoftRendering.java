package base.graphics.app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public abstract class GraphAppSoftRendering extends GraphApp {

	protected BufferedImage frameImage;
	protected int[] pixels; // buffer as int[]

	protected GraphAppSoftRendering() {
	}

	protected void initFrameImage() {
		// TYPE_INT_ARGB, 4 bytes per pixel with alpha channel
		// TYPE_INT_RGB, 4 bytes per pixel without alpha channel
		// https://stackoverflow.com/questions/32414617/how-to-decide-which-bufferedimage-image-type-to-use
		int width = getCanvas().getWidth();
		int height = getCanvas().getHeight();
		this.frameImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.pixels = ((DataBufferInt) this.frameImage.getRaster().getDataBuffer()).getData();
	}

	@Override
	public void initApp() {
		initFrameImage();
	}

	@Override
	public void drawFrameApp(Graphics2D g) {
		updateImage();
		g.drawImage(frameImage, 0, 0, getCanvas().getWidth(), getCanvas().getHeight(), null);
	}

	private void updateImage() {
		Graphics2D g = null;
		try {
			g = frameImage.createGraphics();
			appDrawImage(g);
		} finally {
			if (g != null) {
				g.dispose();
			}
		}
	}

	protected void appDrawImage(Graphics2D g) {
		g.setBackground(Color.BLACK);
		g.clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
		// subclasses will use pixels[] ...
	}

}

//protected void draw() {
////	int redRgb = Color.RED.getRGB();
////	int height = bufferedImage.getHeight();
////	int width = bufferedImage.getWidth();
////	for (int y = 0; y != height; ++y) {
////		for (int x = 0; x != width; ++x) {
////			bufferedImage.setRGB(x, y, redRgb);
////		}
////	}
//
////	int numPixels = bufferedImage.getWidth() * bufferedImage.getHeight();
////	for (int c = numPixels, i = 0; c != 0; --c) {
////		buffer[i++] = redRgb;
////	}
//	// or this...
////	for (int i = 0; i != numPixels; ++i)
////		buffer[i] = redRgb;
//}
//	// fill back buffer
//	Graphics2D gBuffer = (Graphics2D) bufferedImage.getGraphics();
//	gBuffer.setColor(Color.BLACK);
//	gBuffer.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
//
//	Random rand = new Random();
//	gBuffer.setColor(Color.red);
//	int w = bufferedImage.getWidth();
//	int h = bufferedImage.getHeight();
//	for (int i = 0; i <= 1000; i++) {
//		int x0 = Math.abs(rand.nextInt()) % w;
//		int y0 = Math.abs(rand.nextInt()) % h;
//		int x1 = Math.abs(rand.nextInt()) % w;
//		int y1 = Math.abs(rand.nextInt()) % h;
//		gBuffer.drawLine(x0, y0, x1, y1);
//	}
//	// Note: render only if (!isPaused && !appOver) ? // see section Inefficient Pausing https://fivedots.coe.psu.ac.th/~ad/jg/ch1/readers.html
//}
