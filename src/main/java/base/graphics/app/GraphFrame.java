package base.graphics.app;

import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

@SuppressWarnings("serial")
class GraphFrame extends JFrame implements WindowListener {

	private GraphApp graphApp;
	private Settings settings;
	private GraphicsDevice graphDevice;
	// private BufferStrategy bufferStrategy;
	private final DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private int accelMemory; // for reporting accl. memory usage
	private Canvas canvas;

	public GraphFrame(GraphApp graphApp) {
		super(graphApp.getGraphicsConfiguration());
		graphApp.setGraphFrame(this);
		this.graphApp = graphApp;
		this.settings = graphApp.getSettings();
		this.graphDevice = graphApp.getGraphDevice();
		if (settings.showCapabilities) {
			reportCapabilities();
		}
		init();
	}

	private void init() {
//		setDefaultLookAndFeelDecorated(true);
		setTitle(settings.title);
		setExtendedState(JFrame.NORMAL);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // JFrame.DISPOSE_ON_CLOSE
		if (settings.showMenu) {
			setUndecorated(true); // no title bar, borders if there is a menu
			JMenuBar menuBar = graphApp.buildMenuApp();
			FrameDragListener frameDragListener = new FrameDragListener(this); // to drag using the menu bar
			menuBar.addMouseListener(frameDragListener);
			menuBar.addMouseMotionListener(frameDragListener);
			setJMenuBar(menuBar);
		} else {
			setUndecorated(settings.fullScreen); // no title bar, borders etc if fullscreen
			setIgnoreRepaint(true); // only when there is no menu to avoid menu vis problems
		}
		initCanvas();
		if (settings.fullScreen) {
			initFullScreen();
		}
		setResizable(false);
		setAlwaysOnTop(settings.fullScreen);
		setFocusable(true);
		setVisible(true);
		setLocationRelativeTo(null);
		addWindowListener(this);
		requestFocus();
	}

	private void initCanvas() {
		if (canvas != null) {
			remove(canvas);
			if (canvas.getBufferStrategy() != null) {
				canvas.getBufferStrategy().dispose();
			}
		}
		canvas = new Canvas();
		canvas.setSize(settings.winWidth, settings.winHeight);
		canvas.setBackground(Color.BLACK);
		canvas.setIgnoreRepaint(true);
		add(canvas);
		pack();
		initBufferStrategy();
	}

	private void initBufferStrategy() {
		// avoid potential deadlock in 1.4.1_02
		/*
		 * Switch on page flipping: NUM_BUFFERS == 2 so there will be a 'primary surface' and one 'back buffer'.
		 * 
		 * The use of invokeAndWait() is to avoid a possible deadlock with the event dispatcher thread. Should be fixed
		 * in J2SE 1.5 ` createBufferStrategy) is an asynchronous operation, so sleep a bit so that the
		 * getBufferStrategy() call will get the correct details.
		 */
		try {
			canvas.createBufferStrategy(settings.numBuffers);
//			EventQueue.invokeAndWait(new Runnable() {
//				public void run() {
////					canvas.createBufferStrategy(settings.numBuffers);
//				}
//			});
		} catch (Exception ex) {
			System.err.println("Error while creating buffer strategy");
			System.exit(0);
		}
		try { // sleep to give time for the buffer strategy to be carried out
			Thread.sleep(100); // 0.5 sec
		} catch (InterruptedException ex) {
		}
	}

	private void initFullScreen() {
		if (!graphDevice.isFullScreenSupported()) {
			System.out.println("Full-screen mode not supported");
			settings.fullScreen = false;
//			System.exit(0);
			return;
		}
		enableFullScreen();
	}

	private void enableFullScreen() {
		graphDevice.setFullScreenWindow(this);
		setExtendedState(JFrame.MAXIMIZED_BOTH); // TODO
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		setMaximizedBounds(env.getMaximumWindowBounds());
//		setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		enableInputMethods(false);
		if (!settings.showMenu) {
			setDisplayMode(); // switch on full-screen exclusive mode
		}
	}

	void toggleFullscreen() {
		if (!graphDevice.isFullScreenSupported()) {
			return;
		}
		restoreScreen();
		settings.toggleFullscreen();
		init();
	}

	/**
	 * Remove the window from the screen, if we are in full screen mode then we need to reset the video mode.
	 */
	void restoreScreen() {
		setVisible(false); // you can't see me!
		if (graphDevice.getFullScreenWindow() != null) {
			graphDevice.setFullScreenWindow(null);
		}
		dispose();
	}

	private boolean setDisplayMode() {
		if (!graphDevice.isDisplayChangeSupported()) {
			System.out.println("Display mode changing not supported");
			return false;
		}
		DisplayMode dm = new DisplayMode(settings.winWidth, settings.winHeight, settings.bitDepth,
				DisplayMode.REFRESH_RATE_UNKNOWN); // any
													// refresh
													// rate
		if (!isDisplayModeAvailable(dm)) {
			System.out.println("Display mode (" + settings.winWidth + "," + settings.winHeight + "," + settings.bitDepth
					+ ") not available. Finding the first compatible:");
			DisplayMode compatibleMd = findFirstCompatibleMode(new DisplayMode[] { dm });
			if (compatibleMd == null) {
				System.out.println("Compatible display mode not found");
				return false;
			}
		}
		try {
			graphDevice.setDisplayMode(dm);
			System.out.println(
					"Display mode set to: (" + dm.getWidth() + "," + dm.getHeight() + "," + dm.getBitDepth() + ")");
		} catch (IllegalArgumentException e) {
			System.out.println("Error setting Display mode (" + dm.getWidth() + "," + dm.getHeight() + ","
					+ dm.getBitDepth() + ")");
			return false;
		}
		try { // sleep to give time for the display to be changed
			Thread.sleep(1000); // 1 sec
		} catch (InterruptedException ex) {
		}
		return true;
	}

	/*
	 * Check that a displayMode with this width, height, bit depth is available. We don't care about the refresh rate,
	 * which is probably REFRESH_RATE_UNKNOWN anyway.
	 */
	private boolean isDisplayModeAvailable(DisplayMode dm) {
		DisplayMode[] modes = graphDevice.getDisplayModes();
		showModes(modes);
		for (int i = 0; i < modes.length; i++) {
			if (displayModesMatch(modes[i], dm)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private void showCurrentMode() {
		DisplayMode dm = graphDevice.getDisplayMode();
		System.out.println("Current Display Mode: (" + displayModePrintStr(dm) + ")  ");
	}

	/**
	 * Returns the first compatible mode in a list of modes. Returns null if no modes are compatible.
	 */
	private DisplayMode findFirstCompatibleMode(DisplayMode[] modes) {
		DisplayMode[] goodModes = graphDevice.getDisplayModes();
		for (int i = 0; i < modes.length; i++) {
			for (int j = 0; j < goodModes.length; j++) {
				if (displayModesMatch(modes[i], goodModes[j])) {
					return modes[i];
				}
			}
		}
		return null;
	}

	private void reportCapabilities() {
		System.out.println();
		System.out.println("SYS CAPABILITIES:");
		accelMemory = graphDevice.getAvailableAcceleratedMemory();
		System.out.println("Initial Acc. Mem.: " + df.format(((double) accelMemory) / (1024 * 1024)) + " MB");
		GraphicsConfiguration graphConfig = graphApp.getGraphicsConfiguration();
		System.out.println("Color model: " + graphConfig.getColorModel());
		System.out.println("Graphics device: " + graphConfig.getDevice());
		ImageCapabilities imageCaps = graphConfig.getImageCapabilities();
		System.out.println("Image Caps. isAccelerated: " + imageCaps.isAccelerated());
		System.out.println("Image Caps. isTrueVolatile: " + imageCaps.isTrueVolatile());
		// Buffer Capabilities
		BufferCapabilities bufferCaps = graphConfig.getBufferCapabilities();
		System.out.println("Buffer Caps. isPageFlipping: " + bufferCaps.isPageFlipping());
		System.out.println("Buffer Caps. Flip Contents: " + getFlipText(bufferCaps.getFlipContents()));
		System.out.println("Buffer Caps. Full-screen Required: " + bufferCaps.isFullScreenRequired());
		System.out.println("Buffer Caps. MultiBuffers: " + bufferCaps.isMultiBufferAvailable());
		System.out.println("Buffer Caps. MultiBuffers: " + bufferCaps.isMultiBufferAvailable());
		// Front buffer caps
		imageCaps = bufferCaps.getFrontBufferCapabilities();
		System.out.println("Front buffer isAccelerated: " + imageCaps.isAccelerated());
		System.out.println("Front buffer isTrueVolatile: " + imageCaps.isTrueVolatile());
		// Back buffer caps
		imageCaps = bufferCaps.getBackBufferCapabilities();
		System.out.println("Back buffer isAccelerated: " + imageCaps.isAccelerated());
		System.out.println("Back buffer isTrueVolatile: " + imageCaps.isTrueVolatile());
		System.out.println();
		System.out.flush();
	}

	private String getFlipText(BufferCapabilities.FlipContents flip) {
		if (flip == null) {
			return "false";
		} else if (flip == BufferCapabilities.FlipContents.UNDEFINED) {
			return "Undefined";
		} else if (flip == BufferCapabilities.FlipContents.BACKGROUND) {
			return "Background";
		} else if (flip == BufferCapabilities.FlipContents.PRIOR) {
			return "Prior";
		} else // if (flip == BufferCapabilities.FlipContents.COPIED)
		{
			return "Copied";
		}
	} // end of getFlipTest()

	@Override
	public void windowClosing(java.awt.event.WindowEvent evt) {
		graphApp.stop();
	}

	@Override
	public void windowIconified(java.awt.event.WindowEvent evt) {
		graphApp.pause();
	}

	@Override
	public void windowDeiconified(java.awt.event.WindowEvent evt) {
		graphApp.resume();
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent evt) {
		graphApp.resume();
	}

	@Override
	public void windowDeactivated(java.awt.event.WindowEvent evt) {
		graphApp.pause();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	public Canvas getCanvas() {
		return canvas;
	}

	void reportAccelMemory()
	// report any change in the amount of accelerated memory
	{
		int mem = graphDevice.getAvailableAcceleratedMemory(); // in bytes
		int memChange = mem - accelMemory;
		if (memChange != 0) {
			System.out.println("Acc. Mem: " + df.format(((double) accelMemory) / (1024 * 1024)) + " MB; Change: "
					+ df.format(((double) memChange) / 1024) + " K");
		}
		accelMemory = mem;
	} // end of reportAcceleMemory()

	void drawOnCanvas() {
	}

	private static void showModes(DisplayMode[] modes) {
		System.out.println("Modes");
		for (int i = 0; i < modes.length; i++) {
			System.out.print("(" + displayModePrintStr(modes[i]) + ")  ");
			if ((i + 1) % 4 == 0) {
				System.out.println();
			}
		}
		System.out.println();
	}

	public static String displayModePrintStr(DisplayMode mode) {
		return mode.getWidth() + "x" + mode.getHeight() + "x" + mode.getBitDepth() + "@" + mode.getRefreshRate();
	}

	/**
	 * Determines if two display modes "match". Two display modes match if they have the same resolution, bit depth, and
	 * refresh rate. The bit depth is ignored if one of the modes has a bit depth of DisplayMode.BIT_DEPTH_MULTI.
	 * Likewise, the refresh rate is ignored if one of the modes has a refresh rate of DisplayMode.REFRESH_RATE_UNKNOWN.
	 */
	private static boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2) {
		if (mode1.getWidth() != mode2.getWidth() || mode1.getHeight() != mode2.getHeight()) {
			return false;
		}
		if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& mode1.getBitDepth() != mode2.getBitDepth()) {
			return false;
		}
		return mode1.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN
				|| mode2.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN
				|| mode1.getRefreshRate() == mode2.getRefreshRate();
	}

	// https://stackoverflow.com/questions/16046824/making-a-java-swing-frame-movable-and-setundecorated
	public static class FrameDragListener extends MouseAdapter {

		private final JFrame frame;
		private Point mouseDownCompCoords = null;

		public FrameDragListener(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mouseDownCompCoords = null;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseDownCompCoords = e.getPoint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point currCoords = e.getLocationOnScreen();
			frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
		}
	}
}
