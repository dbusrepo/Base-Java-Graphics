package com.busatod.graphics.app.input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The InputManager manages input of key and mouse events.
 * Events are mapped to GameActions.
 */
public class InputManager implements KeyListener, MouseListener,
									 MouseMotionListener, MouseWheelListener
{
	
	/**
	 * An invisible cursor.
	 */
	public static final Cursor INVISIBLE_CURSOR =
			Toolkit.getDefaultToolkit().createCustomCursor(
					Toolkit.getDefaultToolkit().getImage(""),
					new Point(0, 0),
					"invisible");
	
	// mouse codes
	public static final int MOUSE_MOVE_LEFT  = 0;
	public static final int MOUSE_MOVE_RIGHT = 1;
	public static final int MOUSE_MOVE_UP    = 2;
	public static final int MOUSE_MOVE_DOWN  = 3;
	public static final int MOUSE_WHEEL_UP   = 4;
	public static final int MOUSE_WHEEL_DOWN = 5;
	public static final int MOUSE_BUTTON_1   = 6;
	public static final int MOUSE_BUTTON_2   = 7;
	public static final int MOUSE_BUTTON_3   = 8;
	
	private static final int NUM_MOUSE_CODES = 9;
	
	// key codes are defined in java.awt.KeyEvent.
	// most of the codes (except for some rare ones like
	// "alt graph") are less than 600.
	// https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.awt.event.KeyEvent
	private static final int NUM_KEY_CODES = 600;
	
	private final InputAction[] keyActions   = new InputAction[NUM_KEY_CODES];
	private final InputAction[] mouseActions = new InputAction[NUM_MOUSE_CODES];
	
	private Point   mouseLocation;
	private Point   centerLocation;
	private boolean isRecentering;
	private Robot   robot; // used for the relative mouse mode
	
	private Component comp;
	
	public InputManager(Component comp)
	{
		add2Component(comp);
	}
	
	public void add2Component(Component comp)
	{
		this.comp = comp;
		this.mouseLocation = new Point();
		this.centerLocation = new Point();
		// register key and mouse listeners
		comp.addKeyListener(this);
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		comp.addMouseWheelListener(this);
		// allow input of the TAB key and other keys normally
		// used for focus traversal
		comp.setFocusTraversalKeysEnabled(false);
		comp.setFocusable(true);
		comp.requestFocus();
		comp.requestFocusInWindow();
	}
	
	/**
	 * Sets the cursor on this InputManager's input component.
	 */
	public void setCursor(Cursor cursor)
	{
		comp.setCursor(cursor);
	}
	
	/**
	 * Returns whether or not relative mouse mode is on.
	 */
	public boolean isRelativeMouseMode()
	{
		return (robot != null);
	}
	
	/**
	 * Sets whether relative mouse mode is on or not. For
	 * relative mouse mode, the mouse is "locked" in the center
	 * of the screen, and only the changed in mouse movement
	 * is measured. In normal mode, the mouse is free to move
	 * about the screen.
	 */
	public void setRelativeMouseMode(boolean mode)
	{
		if (mode == isRelativeMouseMode()) {
			return;
		}
		if (mode) {
			try {
				robot = new Robot();
				recenterMouse();
			}
			catch (AWTException ex) {
				// couldn't create robot!
				robot = null;
			}
		} else {
			robot = null;
		}
	}
	
	/**
	 * Maps an InputAction to a specific key. The key codes are
	 * defined in java.awt.KeyEvent. If the key already has
	 * an InputAction mapped to it, the new InputAction overwrites
	 * it.
	 */
	public void mapToKey(int keyCode, InputAction gameAction)
	{
		keyActions[keyCode] = gameAction;
	}
	
	/**
	 * Maps a GameAction to a specific mouse action. The mouse
	 * codes are defined herer in InputManager (MOUSE_MOVE_LEFT,
	 * MOUSE_BUTTON_1, etc). If the mouse action already has
	 * a GameAction mapped to it, the new GameAction overwrites
	 * it.
	 */
	public void mapToMouse(int mouseCode, InputAction gameAction)
	{
		mouseActions[mouseCode] = gameAction;
	}
	
	/**
	 * Clears all mapped keys and mouse actions to this
	 * InputAction.
	 */
	public void clearMap(InputAction inputAction)
	{
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == inputAction) {
				keyActions[i] = null;
			}
		}
		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] == inputAction) {
				mouseActions[i] = null;
			}
		}
		inputAction.reset();
	}
	
	/**
	 * Resets all GameActions so they appear like they haven't
	 * been pressed.
	 */
	public void resetAllGameActions()
	{
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] != null) {
				keyActions[i].reset();
			}
		}
		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] != null) {
				mouseActions[i].reset();
			}
		}
	}
	
	/**
	 * Gets a List of names of the keys and mouse actions mapped
	 * to this InputAction. Each entry in the List is a String.
	 */
	public List<String> getMaps(InputAction inputAction)
	{
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == inputAction) {
				list.add(getKeyName(i));
			}
		}
		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] == inputAction) {
				list.add(getMouseName(i));
			}
		}
		return list;
	}
	
	/**
	 * Gets the x position of the mouse.
	 */
	public int getMouseX()
	{
		return mouseLocation.x;
	}
	
	/**
	 * Gets the y position of the mouse.
	 */
	public int getMouseY()
	{
		return mouseLocation.y;
	}
	
	/**
	 * Uses the Robot class to try to postion the mouse in the
	 * center of the screen.
	 * <p>Note that use of the Robot class may not be available
	 * on all platforms.
	 */
	public synchronized void recenterMouse()
	{
		if (robot != null && comp.isShowing()) {
			centerLocation.x = comp.getWidth() / 2;
			centerLocation.y = comp.getHeight() / 2;
			SwingUtilities.convertPointToScreen(centerLocation, comp);
			isRecentering = true;
			robot.mouseMove(centerLocation.x, centerLocation.y);
		}
	}
	
	private InputAction getKeyAction(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		return (keyCode < keyActions.length) ? keyActions[keyCode] : null;
	}
	
	private InputAction getMouseButtonAction(MouseEvent e)
	{
		int mouseCode = getMouseButtonCode(e);
		if (mouseCode != -1) {
			return mouseActions[mouseCode];
		} else {
			return null;
		}
	}
	
	// from the KeyListener interface
	@Override
	public void keyPressed(KeyEvent e)
	{
//        System.out.println(e.getKeyCode() + " pressed");
		InputAction gameAction = getKeyAction(e);
		if (gameAction != null) {
			gameAction.press();
		}
		// make sure the key isn't processed for anything else
		e.consume();
	}
	
	// from the KeyListener interface
	@Override
	public void keyReleased(KeyEvent e)
	{
//        System.out.println(e.getKeyCode() + " released");
		InputAction gameAction = getKeyAction(e);
		if (gameAction != null) {
			gameAction.release();
		}
		// make sure the key isn't processed for anything else
		e.consume();
	}
	
	// from the KeyListener interface
	@Override
	public void keyTyped(KeyEvent e)
	{
		// make sure the key isn't processed for anything else
		// https://stackoverflow.com/questions/17797231/keypressed-and-keytyped-confusion
		e.consume();
	}
	
	// from the MouseListener interface
	@Override
	public void mousePressed(MouseEvent e)
	{
		InputAction gameAction = getMouseButtonAction(e);
		if (gameAction != null) {
			gameAction.press();
		}
	}
	
	// from the MouseListener interface
	@Override
	public void mouseReleased(MouseEvent e)
	{
		InputAction inputAction = getMouseButtonAction(e);
		if (inputAction != null) {
			inputAction.release();
		}
	}
	
	// from the MouseListener interface
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// do nothing
	}
	
	// from the MouseListener interface
	@Override
	public void mouseEntered(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	// from the MouseListener interface
	@Override
	public void mouseExited(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	// from the MouseMotionListener interface
	@Override
	public void mouseDragged(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	// from the MouseMotionListener interface
	@Override
	public synchronized void mouseMoved(MouseEvent e)
	{
		// this event is from re-centering the mouse - ignore it
		if (isRecentering &&
				centerLocation.x == e.getX() &&
				centerLocation.y == e.getY()) {
			isRecentering = false;
		} else {
			int dx = e.getX() - mouseLocation.x;
			int dy = e.getY() - mouseLocation.y;
			mouseHelper(MOUSE_MOVE_LEFT, MOUSE_MOVE_RIGHT, dx);
			mouseHelper(MOUSE_MOVE_UP, MOUSE_MOVE_DOWN, dy);
			if (isRelativeMouseMode()) {
				recenterMouse();
			}
		}
		mouseLocation.x = e.getX();
		mouseLocation.y = e.getY();
	}
	
	// from the MouseWheelListener interface
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		mouseHelper(MOUSE_WHEEL_UP, MOUSE_WHEEL_DOWN,
				e.getWheelRotation());
	}
	
	private void mouseHelper(int codeNeg, int codePos, int amount)
	{
		InputAction inputAction;
		if (amount < 0) {
			inputAction = mouseActions[codeNeg];
		} else {
			inputAction = mouseActions[codePos];
		}
		if (inputAction != null) {
			inputAction.press(Math.abs(amount));
			inputAction.release();
		}
	}
	
	/**
	 * Gets the name of a key code.
	 */
	public static String getKeyName(int keyCode)
	{
		return KeyEvent.getKeyText(keyCode);
	}
	
	/**
	 * Gets the name of a mouse code.
	 */
	public static String getMouseName(int mouseCode)
	{
		switch (mouseCode) {
			case MOUSE_MOVE_LEFT:
				return "Mouse Left";
			case MOUSE_MOVE_RIGHT:
				return "Mouse Right";
			case MOUSE_MOVE_UP:
				return "Mouse Up";
			case MOUSE_MOVE_DOWN:
				return "Mouse Down";
			case MOUSE_WHEEL_UP:
				return "Mouse Wheel Up";
			case MOUSE_WHEEL_DOWN:
				return "Mouse Wheel Down";
			case MOUSE_BUTTON_1:
				return "Mouse Button 1";
			case MOUSE_BUTTON_2:
				return "Mouse Button 2";
			case MOUSE_BUTTON_3:
				return "Mouse Button 3";
			default:
				return "Unknown mouse code " + mouseCode;
		}
	}
	
	/**
	 * Gets the mouse code for the button specified in this
	 * MouseEvent.
	 */
	public static int getMouseButtonCode(MouseEvent e)
	{
		switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				return MOUSE_BUTTON_1;
			case MouseEvent.BUTTON2:
				return MOUSE_BUTTON_2;
			case MouseEvent.BUTTON3:
				return MOUSE_BUTTON_3;
			default:
				return -1;
		}
	}
}
