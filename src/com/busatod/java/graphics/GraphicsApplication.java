package com.busatod.java.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

// vedere:
// https://stackoverflow.com/questions/35516191/what-is-the-correct-way-to-use-createbufferstrategy

// TODO
// Threading
// fix fps ups
// refactoring main..
// logic app class
// see reader comments about the book chapters on the book website
// vedi setBufferStrategy in wormChase.java full screen e la gestione del fullscreen in generale

public class GraphicsApplication extends JFrame implements WindowListener {

	private static final String TITLE = "Java Graphics";

	private static final boolean SHOW_STATISTICS = true;

	private static final int NUM_BUFFERS = 3; // used for page flipping
	private static final boolean VSYNC = true; // TODO

	private static final long NANO_IN_MILLI = 1000000L;
	private static final long NANO_IN_SEC = 1000L * NANO_IN_MILLI;

	private static long MAX_STATS_INTERVAL = NANO_IN_SEC; // in ns
	// private static long MAX_STATS_INTERVAL = 1000L;
	// record stats every 1 second (roughly)

	// Number of frames with a delay of 0 ms before the animation thread yields
	// to other running threads.
	private static final int NUM_DELAYS_PER_YIELD = 16;

	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered
	private static int MAX_FRAME_SKIPS = 5;

	// number of FPS values stored to get an average
	private static int NUM_FPS = 10;

	private static int TARGET_FPS = 60; // -1 if not used

	//    private GraphicsEnvironment graphicsEnvironment;
	//    private GraphicsDevice screenDevice;

	private long period;  // period between drawing in _nanosecs_

	// used for gathering statistics
	private long statsInterval = 0L;    // in ns
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long timeSpentInGame = 0;    // in seconds

	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

	private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
	private DecimalFormat timedf = new DecimalFormat("0.####");  // 4 dp

	private Font font;
	private FontMetrics metrics;

	private boolean isWindowed;
	private final Dimension winDimension;

	private InputManager inputManager;
	private InputAction exitAction;
	private InputAction pauseAction;
	private InputAction toggleFullscreenAction;

	private boolean isRunning = false;
	private boolean gameOver = false;
	private boolean isPaused = false;

	private long gameStartTime;
	private long lastFpsTime;

	// init for windowed mode
	public GraphicsApplication(int width, int height, boolean isFullScreen) {
		System.out.println("Initializing the graphics application...");
		//        graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//        screenDevice = graphicsEnvironment.getDefaultScreenDevice();
		this.winDimension = new Dimension(width, height);
		this.isWindowed = !isFullScreen;

//      setDefaultLookAndFeelDecorated(true);
//      setSize(windowedWidth, windowedHeight);
		setUndecorated(isFullScreen); // do we want window decorations like the title bar?
		setIgnoreRepaint(true);
		setTitle(TITLE);
		setBackground(Color.BLACK);
		adjustWinSize();
		if (isFullScreen) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			getGraphicsConfiguration().getDevice().setFullScreenWindow(this);
		}
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null); // called after setVisible(true); to center the window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createBufferStrategy(); // TODO ma serve?

		this.inputManager = new InputManager(this);
		createInputActions();
		setFocusable(true);
		requestFocus();
		requestFocusInWindow();

		// set up message font
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);

		// initialise timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}

		// calc the frame period and print it
		period = 0L;  // rendering FPS (nanosecs/targetFPS) // da mettere tra i settings?
		period = NANO_IN_SEC / TARGET_FPS; // in ns
		System.out.println("FPS: " + TARGET_FPS + ", vsync=" + VSYNC);
		System.out.println("FPS period: " + period);

		// create app/game components
	}

	// going to fullscreen:
	// https://stackoverflow.com/questions/13064607/fullscreen-swing-components-fail-to-receive-keyboard-input-on-java-7-on-mac-os-x
	// https://stackoverflow.com/questions/19645243/keylistener-doesnt-work-after-dispose
	private void toggleFullscreen() {
		setVisible(false);
		dispose();
		setUndecorated(isWindowed);
		if (isWindowed) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			getGraphicsConfiguration().getDevice().setFullScreenWindow(this);
		} else {
			setExtendedState(JFrame.NORMAL);
			getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
		}
		isWindowed = !isWindowed;
		adjustWinSize();
		setVisible(true);
	}

	private void adjustWinSize() {
		//windowBarHeight = (int) (HEIGHT or getHeight() ? - getContentPane().getSize().getHeight());
		//setSize(WIDTH, HEIGHT + windowBarHeight);
		getContentPane().setPreferredSize(winDimension);
		pack();
	}

	private void createInputActions() {
		exitAction = new InputAction("Exit", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		pauseAction = new InputAction("Pause", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		toggleFullscreenAction = new InputAction("Toogle Fullscreen", InputAction.DetectBehavior.INITIAL_PRESS_ONLY);
		inputManager.mapToKey(exitAction, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(pauseAction, KeyEvent.VK_P);
		inputManager.mapToKey(toggleFullscreenAction, KeyEvent.VK_F1);
	}

	private void createBufferStrategy() {
		// avoid potential deadlock in 1.4.1_02
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					createBufferStrategy(NUM_BUFFERS);
				}
			});
		} catch (InterruptedException ex) {
			// ignore
		} catch (InvocationTargetException ex) {
			// ignore
		}
	}

	// TODO introdurre thread...
	public void run() {
		System.out.println("Entering the main loop...");
		try {
			execLoop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		printStats();
		System.out.println("Exiting...");
		// make sure we restore the video mode before exiting
		initFromScreen();
	}

	private void printStats() {
		System.out.println("Frame Count/Loss: " + frameCount + " / " + totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
		// TODO invoke app logic print stats
	}

	// https://stackoverflow.com/questions/16364487/java-rendering-loop-and-logic-loop
	private void execLoop() {

		// times are in ns
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int numDelays = 0;
		long excess = 0L;

		gameStartTime = System.nanoTime();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;

		// Cache the buffer strategy
		BufferStrategy bufferStrategy = getBufferStrategy();

		isRunning = true;

		// Main loop
		while (isRunning) {
			// update
			update(0);
			// rendering

			//painting
			Graphics graphics = bufferStrategy.getDrawGraphics();
			// see user comments here https://fivedots.coe.psu.ac.th/~ad/jg/ch1/readers.html
			render(graphics);
			graphics.dispose();
			if (VSYNC) {
				Toolkit.getDefaultToolkit().sync();
			}

			// Flip the buffer
			if (!bufferStrategy.contentsLost()) {
				bufferStrategy.show();
			}

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime; // ns

			if (sleepTime > 0) { // time left in cycle
				//System.out.println("sleepTime: " + (sleepTime/NANO_IN_MILLI));
				try {
					Thread.sleep(sleepTime / NANO_IN_MILLI);//nano->ms
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

            /* If frame animation is taking too long, update the game state
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
			storeStats();
		}

	}

	/* The statistics:
     - the summed periods for all the iterations in this interval
       (period is the amount of time a single frame iteration should take),
       the actual elapsed time in this interval,
       the error between these two numbers;

     - the total frame count, which is the total number of calls to run();

     - the frames skipped in this interval, the total number of frames
       skipped. A frame skip is a game update without a corresponding render;

     - the FPS (frames/sec) and UPS (updates/sec) for this interval,
       the average FPS & UPS over the last NUM_FPSs intervals.

    The data is collected every MAX_STATS_INTERVAL  (1 sec).
    */
	// TODO Fix!!
	private void storeStats() {
		frameCount++;
		statsInterval += period;
		if (statsInterval >= MAX_STATS_INTERVAL) {     // record stats every MAX_STATS_INTERVAL
			long timeNow = System.nanoTime();
			timeSpentInGame = (timeNow - gameStartTime) / NANO_IN_SEC;  // ns --> secs

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
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount++;

			double totalFPS = 0.0;     // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
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

	// prev methods
//	private void calculateFramesPerSecond(long curRenderTime) {
//		if (curRenderTime - lastFpsTime >= NANO_IN_MILLI * 1000) {
//			fps = frameCounter;
//			frameCounter = 0;
//			lastFpsTime = curRenderTime;
//		}
//		frameCounter++;
//	}

	private void update(long elapsedTime) {
//        System.out.flush();
//        System.err.flush();
		// check input that can happen whether paused or not
		checkSystemInput();
		if (!isPaused && !gameOver) {
			// TODO check app input
			updateWorld(elapsedTime);
		}
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
			toggleFullscreen();
		}
		// ...
	}

	private void updateWorld(long l) {
		// ...
	}

	private void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(font);
		g.setColor(Color.YELLOW);
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			//.getFontRenderContext()
		}
		int winBarHeight = getHeight() - getContentPane().getHeight();
//		g.drawString("Frame Count " + frameCount, 10, winBarHeight + 25);
		String statsStr = "Average FPS/UPS: " + df.format(averageFPS) + ", " + df.format(averageUPS);
		g.drawString(statsStr, 5, winBarHeight + metrics.getHeight());  // was (10,55)
//        String str = "FPS: " + fps;
//        int winBarHeight = getHeight() - getContentPane().getHeight();
//        g.drawString(str, 2, winBarHeight + g.getFontMetrics().getHeight());
	}

	/**
	 * Remove the window from the screen, if we are in full screen
	 * mode then we need to reset the video mode.
	 */
	// TODO vedi finishOff in WormChase.java fullscreen libro
	public void initFromScreen() {
		if (!this.isWindowed) {
			getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
		}
		setVisible(false); //you can't see me!
		dispose(); //Destroy the JFrame object
	}

	// called when the JFrame is activated / deiconified
	public void resumeApp() { isPaused = false; }

	// called when the JFrame is deactivated / iconified
	public void pauseApp() { isPaused = true; }

	// called when the JFrame is closing
	public void stopApp() { isRunning = false; }

	/**
	 * We may need to handle different window events
	 */
	@Override
	public void windowClosing(java.awt.event.WindowEvent evt) {
		stopApp();
	}

	@Override
	public void windowIconified(java.awt.event.WindowEvent evt) {
		pauseApp();
	}

	@Override
	public void windowDeiconified(java.awt.event.WindowEvent evt) {
		resumeApp();
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent evt) {
		resumeApp();
	}

	@Override
	public void windowDeactivated(java.awt.event.WindowEvent evt) {
		pauseApp();
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

}
