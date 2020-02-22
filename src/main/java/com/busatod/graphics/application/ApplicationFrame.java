package com.busatod.graphics.application;

import com.busatod.graphics.input.InputManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

public class ApplicationFrame extends JFrame implements WindowListener {

	private final GraphicsApplication graphApp;
	private final Settings settings;

	//	private final int width;
//	private final int height;
//	private final boolean fullScreen;
//	private final boolean debugInfo;
	private Canvas canvas;

	private BufferStrategy bufferStrategy;

	public ApplicationFrame(GraphicsConfiguration gc, GraphicsApplication graphApp) {
		super(gc);
		this.graphApp = graphApp;
		this.settings = graphApp.getSettings();
//		this.debugInfo = settings.debugInfo; // TODO
//		setDefaultLookAndFeelDecorated(true);
		setUndecorated(settings.fullScreen); // no menu bar, borders, etc. or Swing components? // TODO
		setIgnoreRepaint(true); // turn off all paint events since doing active rendering
		setTitle(settings.title);
		initCanvas();
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null); // called after setVisible(true); to center the window (first screen only?)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initBufferStrategy();
		if (settings.fullScreen) {
//			initFullScreen();
		}
		addWindowListener(this);
	}

	private void initCanvas() {
		canvas = new Canvas();
		canvas.setSize(settings.width, settings.height);
		canvas.setBackground(Color.BLACK);
		canvas.setIgnoreRepaint(true);
		add(canvas);
		pack();
	}

	private void initBufferStrategy() {
		// avoid potential deadlock in 1.4.1_02
		/* Switch on page flipping: NUM_BUFFERS == 2 so
		 there will be a 'primary surface' and one 'back buffer'.

		 The use of invokeAndWait() is to avoid a possible deadlock
		 with the event dispatcher thread. Should be fixed in J2SE 1.5

		 createBufferStrategy) is an asynchronous operation, so sleep
		 a bit so that the getBufferStrategy() call will get the
		 correct details.
	  */
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					canvas.createBufferStrategy(settings.numBuffers);
				}
			});
		} catch (Exception e) {
			System.out.println("Error while creating buffer strategy");
			System.exit(0);
		}

		try {  // sleep to give time for the buffer strategy to be carried out
			Thread.sleep(500);  // 0.5 sec
		} catch (InterruptedException ex) {}

		// Cache the buffer strategy
		bufferStrategy = canvas.getBufferStrategy();
	}

	@Override
	public void windowClosing(java.awt.event.WindowEvent evt) {
		graphApp.stopApp();
	}

	@Override
	public void windowIconified(java.awt.event.WindowEvent evt) {
		graphApp.pauseApp();
	}

	@Override
	public void windowDeiconified(java.awt.event.WindowEvent evt) {
		graphApp.resumeApp();
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent evt) {
		graphApp.resumeApp();
	}

	@Override
	public void windowDeactivated(java.awt.event.WindowEvent evt) {
		graphApp.pauseApp();
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	public BufferStrategy getBufferStrategy() {
		return bufferStrategy;
	}

}
