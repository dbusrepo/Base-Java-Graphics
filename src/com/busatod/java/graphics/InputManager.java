package com.busatod.java.graphics;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputManager implements KeyListener {

    // key codes are defined in java.awt.KeyEvent.
    // most of the codes (except for some rare ones like
    // "alt graph") are less than 600.
    // https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.awt.event.KeyEvent
    private static final int NUM_KEY_CODES = 600;

    private InputAction[] keyActions =
            new InputAction[NUM_KEY_CODES];

    private Component comp;

    public InputManager(Component comp) {
        this.comp = comp;
        // register key and mouse listeners
        comp.addKeyListener(this);
        // allow input of the TAB key and other keys normally
        // used for focus traversal
        comp.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Maps an InputAction to a specific key. The key codes are
     * defined in java.awt.KeyEvent. If the key already has
     * an InputAction mapped to it, the new InputAction overwrites
     * it.
     */
    public void mapToKey(InputAction gameAction, int keyCode) {
        keyActions[keyCode] = gameAction;
    }

    /**
     * Clears all mapped keys and mouse actions to this
     * InputAction.
     */
    public void clearMap(InputAction inputAction) {
        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] == inputAction) {
                keyActions[i] = null;
            }
        }

        // TODO
//        for (int i=0; i<mouseActions.length; i++) {
//            if (mouseActions[i] == gameAction) {
//                mouseActions[i] = null;
//            }
//        }

        inputAction.reset();
    }

    /**
     * Gets a List of names of the keys and mouse actions mapped
     * to this InputAction. Each entry in the List is a String.
     */
    public List<String> getMaps(InputAction inputAction) {
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] == inputAction) {
                list.add(getKeyName(i));
            }
        }

        // TODO
//        for (int i = 0; i < mouseActions.length; i++) {
//            if (mouseActions[i] == inputAction) {
//                list.add(getMouseName(i));
//            }
//        }

        return list;
    }

    /**
     * Gets the name of a key code.
     */
    public static String getKeyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }

    // from the KeyListener interface
    @Override
    public void keyTyped(KeyEvent e) {
        // make sure the key isn't processed for anything else
        // https://stackoverflow.com/questions/17797231/keypressed-and-keytyped-confusion
        e.consume();
    }

    // from the KeyListener interface
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode() + " pressed");
        InputAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }

    // from the KeyListener interface
    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println(e.getKeyCode() + " released");
        InputAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }

    private InputAction getKeyAction(KeyEvent e) {
        int keyCode = e.getKeyCode();
        return (keyCode < keyActions.length) ? keyActions[keyCode] : null;
    }
}
