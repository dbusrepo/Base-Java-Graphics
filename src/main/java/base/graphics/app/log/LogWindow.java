package base.graphics.app.log;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class LogWindow extends JFrame {
	private JTextPane textPane = null;
	private SimpleAttributeSet attributes;

	public LogWindow(int width, int height) {
		super("Events Log");
		attributes = new SimpleAttributeSet();
		textPane = new JTextPane();
		textPane.setBorder(new TitledBorder("Messages"));
		textPane.setEditable(false);
		textPane.setBackground(new Color(211, 216, 224));
		add(new JScrollPane(textPane));
		setSize(width, height);
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ?
		setVisible(true);
		// initMainLayout, initMenu, initActions
	}

	public void append(String msg, Color textColor) {
		SwingUtilities.invokeLater(() -> {
			StyleConstants.setForeground(attributes, textColor);
//			StyleConstants.setBackground(attributes, Color.BLUE);
			StyledDocument doc = textPane.getStyledDocument();
			try {
				doc.insertString(doc.getLength(), msg, attributes);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
	}

}
