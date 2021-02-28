package base.graphics.app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public abstract class GraphAppSoftRendering extends GraphApp {

	protected BufferedImage image;
	protected int[] pixels; // img as int[] pixel array
	protected Graphics2D g2Dimg = null; // g2d cached?

	protected GraphAppSoftRendering() {
	}

	@Override
	public void init() {
		initFrameImage();
	}

	protected void initFrameImage() {
		// TYPE_INT_ARGB, 4 bytes per pixel with alpha channel
		// TYPE_INT_RGB, 4 bytes per pixel without alpha channel
		// https://stackoverflow.com/questions/32414617/how-to-decide-which-bufferedimage-image-type-to-use
		this.image = new BufferedImage(getCanvas().getWidth(),
				getCanvas().getHeight(), BufferedImage.TYPE_INT_ARGB);
		this.pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer())
				.getData();
		this.g2Dimg = image.createGraphics(); // cache the graph2d obj
	}

	@Override
	public void drawFrame(Graphics2D g) {
		updateImage();
		g.drawImage(image, 0, 0, getCanvas().getWidth(),
				getCanvas().getHeight(), null);
	}

	protected void updateImage() {
		g2Dimg.setBackground(Color.BLACK);
		g2Dimg.clearRect(0, 0, image.getWidth(), image.getHeight());
		// subclasses can use pixels[] ...
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
