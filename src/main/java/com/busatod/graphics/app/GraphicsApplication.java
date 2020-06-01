package com.busatod.graphics.app;

import com.busatod.graphics.input.InputAction;
import com.busatod.graphics.input.InputManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.text.DecimalFormat;

// vedere:
// https://stackoverflow.com/questions/35516191/what-is-the-correct-way-to-use-createbufferstrategy

// TODO
// Threading
// refactoring main..
// logic app class
// see reader comments about the book chapters on the book website
// vedi setBufferStrategy in wormChase.java full screen e la gestione del fullscreen

public class GraphicsApplication implements Runnable {

	private static final long NANO_IN_MILLI = 1000000L;
	private static final long NANO_IN_SEC = 1000L * NANO_IN_MILLI;

	private static final int FONT_SIZE = 20;
	private static final String FONT_NAME = "SansSerif";

	private static long UPDATE_STATS_INTERVAL = NANO_IN_SEC; // in ns, 1sec
	// private static long MAX_STATS_INTERVAL = 1000L;
	// record stats every 1 second (roughly)X

	// Number of frames with a delay of 0 ms before the animation thread yields
	// to other running threads.
	private static final int NUM_DELAYS_PER_YIELD = 16;

	// no. of frames that can be skipped in any one animation loop
	// i.e the state is updated but not rendered
	private static int MAX_FRAME_SKIPS = 5;

	// number of FPS values stored to get an average
	private static int NUM_AVG_FPS = 10;

	/******************************************************************************************************************/

	private Settings settings;
	private IAppLogic appLogic;

	private GraphicsFrame graphicsFrame;
	private final GraphicsDevice graphDevice;
	private final GraphicsConfiguration graphConfig;

	protected Thread renderThread = null;

	protected BufferedImage bufferedImage;
	protected int[] buffer;

	private volatile boolean isRunning = false;
	private boolean appOver = false;
	private boolean isPaused = false;
	private boolean finishedOff = false;

	private long period;  // period between drawing in _nanosecs_

	// used for gathering statistics
	private long startTime;
	private long statsInterval = 0L; // ns
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long totalTimeSpent = 0; // seconds

	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

	private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
//	private DecimalFormat timedf = new DecimalFormat("0.####");  // 4 dp

	private Font font;
	private FontMetrics metrics;

	private InputManager inputManager;
	private InputAction exitAction;
	private InputAction pauseAction;
	private InputAction toggleFullscreenAction;

	public GraphicsApplication(Settings settings, IAppLogic appLogic) {

		this.settings = settings;
		this.appLogic = appLogic;

		// Acquiring the current graphics device and graphics configuration
		GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.graphDevice = graphEnv.getDefaultScreenDevice();
		this.graphConfig = graphDevice.getDefaultConfiguration();

		this.graphicsFrame = new GraphicsFrame(this);

		this.bufferedImage = new BufferedImage(this.settings.width, this.settings.height, BufferedImage.TYPE_INT_RGB);
		this.buffer = ((DataBufferInt) this.bufferedImage.getRaster().getDataBuffer()).getData();

		// initialise timing elements
		this.fpsStore = new double[NUM_AVG_FPS];
		this.upsStore = new double[NUM_AVG_FPS];
		for (int i = 0; i < NUM_AVG_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}

		this.period = NANO_IN_SEC / this.settings.targetFps;

		this.font = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
		this.metrics = graphicsFrame.getCanvas().getFontMetrics(this.font);

		initInputManager();

		appLogic.init();

		// for shutdown tasks, a shutdown may not only come from the program
		Runtime.getRuntime().addShutdownHook(buildShutdownThread());

		// start the app
		start();
	}

	public Settings getSettings() {
		return settings;
	}

	private class ShutDownThread extends Thread {
		@Override
		public void run() {
//			super.run();
			isRunning = false;
			finishOff();
		}
	}

	private Thread buildShutdownThread() {
		return new ShutDownThread();
	}

	private void start() {
		if (renderThread == null || !isRunning) {
			renderThread = new Thread(this);
			renderThread.start();
		}
	}

	// TODO APP HOOK ?
	private void initInputManager() {
		inputManager = new InputManager(graphicsFrame.getCanvas());
//		inputManager.setRelativeMouseMode(true);
//		inputManager.setCursor(InputManager.INVISIBLE_CURSOR); // TODO mouse non resta invisible quando si passa in fullscreen?
		exitAction = new InputAction("Exit", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		pauseAction = new InputAction("Pause", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		toggleFullscreenAction = new InputAction("Toogle Fullscreen", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		inputManager.mapToKey(KeyEvent.VK_ESCAPE, exitAction);
		inputManager.mapToKey(KeyEvent.VK_P, pauseAction);
		inputManager.mapToKey(KeyEvent.VK_F1, toggleFullscreenAction);
	}

//	InputManager getInputManager() {
//		return inputManager;
//	}

	// https://stackoverflow.com/questions/16364487/java-rendering-loop-and-logic-loop
	public void run() {

		long beforeTime, afterTime, timeDiff, sleepTime; // times are in ns
		long overSleepTime = 0L;
		int numDelays = 0;
		long excess = 0L;

		startTime = System.nanoTime();
		prevStatsTime = startTime;
		beforeTime = startTime;

		isRunning = true;

		// Main loop
		while (isRunning) {
			// update
			update(0); // TODO fix/change arg 0?
			render();

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime; // ns

			if (sleepTime > 0) { // time left in cycle
				//System.out.println("sleepTime: " + (sleepTime/NANO_IN_MILLI));
				try {
					Thread.sleep(sleepTime / NANO_IN_MILLI);//nano->ms
					numDelays = 0;   // reset noDelays when sleep occurs // see section Sleep is like Yield https://fivedots.coe.psu.ac.th/~ad/jg/ch1/readers.html
				} catch (InterruptedException ex) { }
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
//				System.out.println("Rendering too slow"); // TODO if stats?
				excess -= sleepTime;
				overSleepTime = 0L; // store excess time value
				if (++numDelays >= NUM_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					numDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

            /* If frame animation is taking too long, update the state
             without rendering it, to get the updates/sec nearer to
             the required FPS. */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				// update state but don’t render
//				System.out.println("Skip renderFPS, run updateFPS"); // TODO if stats?
				excess -= period;
				update(0); // period as elapsedTime?
				skips++;
			}
			framesSkipped += skips;

			updateStats();
		}
		finishOff();
	}

	private void render() {
		// use active rendering
		try {
			Canvas canvas = graphicsFrame.getCanvas();
			BufferStrategy bufferStrategy = canvas.getBufferStrategy();
			Graphics2D g2d = null;
			try {
				g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
				draw(); // draw on buffered image frame
				// blit
				g2d.drawImage(bufferedImage, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
				showStats(g2d);
			} finally {
				if (g2d != null) g2d.dispose();
			}
			// TODO ok here?
			if (!bufferStrategy.contentsLost()) {
				bufferStrategy.show();
			} else {
				System.out.println("Contents Lost");
			}
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
		} catch (Exception e) {
			e.printStackTrace();
			isRunning = false;
		}
	}

	protected void showStats(Graphics2D g) {
//		appLogic.showStats(); 	// TODO APP_HOOK
		if (settings.showFps) {
			g.setFont(font);
			g.setColor(Color.YELLOW);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			//		g.drawString("Frame Count " + frameCount, 10, winBarHeight + 25);
			String debugInfo = "FPS/UPS: " + df.format(averageFPS) + ", " + df.format(averageUPS);
			g.drawString(debugInfo, 2, metrics.getHeight());  // was (10,55)
		}
	}

	private void draw() {
		appLogic.draw();
	}

	private boolean canUpdateState() {
		return !isPaused && !appOver;
	}

	private void update(long elapsedTime) {
		if (settings.showFps) {
			graphicsFrame.reportAccelMemory();
		}
		checkSystemInput(); // check input that can happen whether paused or not
		if (canUpdateState()) {
			updateState(elapsedTime);
		}
	}

	private void updateState(long elapsedTime) {
		appLogic.update(elapsedTime);
	}

	// vedi anche https://stackoverflow.com/questions/19823633/multiple-keys-in-keyevent-listener
	private void checkSystemInput() {
		if (pauseAction.isPressed()) {
			isPaused = !isPaused;
		}
		if (exitAction.isPressed()) {
			stopApp();
		}
		if (toggleFullscreenAction.isPressed()) {
			toggleFullscreenAction.release(); // to avoid a subtle bug of keyReleased not invoked after switching to fs...
			graphicsFrame.toggleFullscreen();
			inputManager.add2Component(graphicsFrame.getCanvas()); // TODO move?
		}
		// ...
	}

	/* Tasks to do before terminating. Called at end of run()
	   and via the shutdown hook in readyForTermination().

	   The call at the end of run() is not really necessary, but
	   included for safety. The flag stops the code being called
	   twice.
	*/
	protected void finishOff() {
		// System.out.println("finishOff");
		if (!finishedOff) {
			finishedOff = true;
			graphicsFrame.restoreScreen(); // make sure we restore the video mode before exiting
			appLogic.finish();
			printFinalStats();
			System.exit(0);
		}
	}

	/* The statistics:
     - the summed periods for all the iterations in this interval
       (period is the amount of time a single frame iteration should take),
       the actual elapsed time in this interval,
       the error between these two numbers;

     - the total frame count, which is the total number of calls to run();

     - the frames skipped in this interval, the total number of frames
       skipped. A frame skip is a state update without a corresponding render;

     - the FPS (frames/sec) and UPS (updates/sec) for this interval,
       the average FPS & UPS over the last NUM_FPSs intervals.

    	The data is collected every MAX_STATS_INTERVAL  (1 sec).
    */
	private void updateStats() {
		frameCount++;
		statsInterval += period;
		if (statsInterval >= UPDATE_STATS_INTERVAL) {     // record stats every MAX_STATS_INTERVAL
			long timeNow = System.nanoTime();
			totalTimeSpent = (timeNow - startTime) / NANO_IN_SEC;  // ns --> secs

			long realElapsedTime = timeNow - prevStatsTime;   // time since last stats collection
			totalElapsedTime += realElapsedTime;

			double timingError = ((double) (realElapsedTime - statsInterval) / statsInterval) * 100.0;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0.0;     // calculate the latest FPS and UPS
			double actualUPS = 0.0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * NANO_IN_SEC);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * NANO_IN_SEC);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_AVG_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_AVG_FPS] = actualUPS;
			statsCount++;

			double totalFPS = 0.0;     // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_AVG_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_AVG_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_AVG_FPS;
				averageUPS = totalUPS / NUM_AVG_FPS;
			}

//			System.out.println(timedf.format((double) statsInterval / NANO_IN_SEC) + " " +
//					timedf.format((double) realElapsedTime / NANO_IN_SEC) + "s " +
//					df.format(timingError) + "% " +
//					frameCount + "c " +
//					framesSkipped + "/" + totalFramesSkipped + " skip; " +
//					df.format(actualFPS) + " " + df.format(averageFPS) + " afps; " +
//					df.format(actualUPS) + " " + df.format(averageUPS) + " aups");

			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L;   // reset
		}
	}

	private void printFinalStats() {
		System.out.flush();
		System.out.println("Frame Count/Loss: " + frameCount + " / " + totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + totalTimeSpent + " secs");
		appLogic.printFinalStats();
		System.out.flush();
//		System.err.flush();
	}

	// called when the JFrame is activated / deiconified
	public void resumeApp() { isPaused = false; }

	// called when the JFrame is deactivated / iconified
	public void pauseApp() { isPaused = true; }

	// called when the JFrame is closing
	public void stopApp() { isRunning = false; }

	public GraphicsDevice getGraphDevice() {
		return graphDevice;
	}

	public GraphicsConfiguration getGraphConfig() {
		return graphConfig;
	}

}

//	protected void draw() {
////		int redRgb = Color.RED.getRGB();
////		int height = bufferedImage.getHeight();
////		int width = bufferedImage.getWidth();
////		for (int y = 0; y != height; ++y) {
////			for (int x = 0; x != width; ++x) {
////				bufferedImage.setRGB(x, y, redRgb);
////			}
////		}
//
////		int numPixels = bufferedImage.getWidth() * bufferedImage.getHeight();
////		for (int c = numPixels, i = 0; c != 0; --c) {
////			buffer[i++] = redRgb;
////		}
//		// or this...
////		for (int i = 0; i != numPixels; ++i)
////			buffer[i] = redRgb;
//	}
//		// fill back buffer
//		Graphics2D gBuffer = (Graphics2D) bufferedImage.getGraphics();
//		gBuffer.setColor(Color.BLACK);
//		gBuffer.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
//
//		Random rand = new Random();
//		gBuffer.setColor(Color.red);
//		int w = bufferedImage.getWidth();
//		int h = bufferedImage.getHeight();
//		for (int i = 0; i <= 1000; i++) {
//			int x0 = Math.abs(rand.nextInt()) % w;
//			int y0 = Math.abs(rand.nextInt()) % h;
//			int x1 = Math.abs(rand.nextInt()) % w;
//			int y1 = Math.abs(rand.nextInt()) % h;
//			gBuffer.drawLine(x0, y0, x1, y1);
//		}
//		// Note: render only if (!isPaused && !appOver) ? // see section Inefficient Pausing https://fivedots.coe.psu.ac.th/~ad/jg/ch1/readers.html
//	}
