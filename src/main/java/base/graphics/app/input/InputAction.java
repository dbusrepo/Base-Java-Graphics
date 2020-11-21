package base.graphics.app.input;

/**
 * The InputAction class is an abstract to a user-initiated
 * action, like jumping or moving. InputActions can be mapped
 * to keys or the mouse with the InputManager.
 */
public class InputAction
{
	
	public enum DetectBehavior
	{
		/**
		 * Normal behavior. The isPressed() method returns true
		 * as long as the key is held down.
		 */
		NORMAL,
		/**
		 * Initial press behavior. The isPressed() method returns
		 * true only after the key is first pressed, and not again
		 * until the key is released and pressed again.
		 */
		INITIAL_PRESS_ONLY
	}
	
	private enum State
	{
		RELEASED,
		PRESSED,
		WAITING_FOR_RELEASE
	}
	
	private final String         name;
	private final DetectBehavior behavior;
	private       int            amount;
	private       State          state;
	
	public InputAction(String name)
	{
		this(name, DetectBehavior.NORMAL);
	}
	
	public InputAction(String name, DetectBehavior behavior)
	{
		this.name = name;
		this.behavior = behavior;
		reset();
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Resets this InputAction so that it appears like it hasn't
	 * been pressed.
	 */
	public void reset()
	{
		state = State.RELEASED;
		amount = 0;
	}
	
	/**
	 * Taps this GameAction. Same as calling press() followed
	 * by release().
	 */
	public synchronized void tap()
	{
		press();
		release();
	}
	
	/**
	 * Signals that the key was pressed.
	 */
	public synchronized void press()
	{
		press(1);
	}
	
	/**
	 * Signals that the key was pressed a specified number of
	 * times, or that the mouse move a specified distance.
	 */
	public synchronized void press(int amount)
	{
		if (state != State.WAITING_FOR_RELEASE) {
			this.amount += amount;
			state = State.PRESSED;
		}
	}
	
	/**
	 * Signals that the key was released
	 */
	public synchronized void release()
	{
		state = State.RELEASED;
	}
	
	/**
	 * Returns whether the key was pressed or not since last
	 * checked.
	 */
	public synchronized boolean isPressed()
	{
		return (getAmount() != 0);
	}
	
	/**
	 * For keys, this is the number of times the key was
	 * pressed since it was last checked.
	 * For mouse movement, this is the distance moved.
	 */
	public synchronized int getAmount()
	{
		int retVal = amount;
		if (retVal != 0) {
			if (state == State.RELEASED) {
				amount = 0;
			} else if (behavior == DetectBehavior.INITIAL_PRESS_ONLY) {
				state = State.WAITING_FOR_RELEASE;
				amount = 0;
			}
		}
		return retVal;
	}
}
