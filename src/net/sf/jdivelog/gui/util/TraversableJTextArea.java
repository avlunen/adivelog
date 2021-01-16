package net.sf.jdivelog.gui.util;

import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.text.Document;

public class TraversableJTextArea extends JTextArea {
	private static final long serialVersionUID = 1L;

	public TraversableJTextArea() {
		super();
		init();
	}

	public TraversableJTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		init();
	}

	public TraversableJTextArea(Document doc) {
		super(doc);
		init();
	}

	public TraversableJTextArea(int rows, int columns) {
		super(rows, columns);
		init();
	}

	public TraversableJTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		init();
	}

	public TraversableJTextArea(String text) {
		super(text);
		init();
	}

	private void init() {
		addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					System.out.println(e.getModifiers());
					if (e.getModifiers() > 0)
						transferFocusBackward();
					else
						transferFocus();
					e.consume();
				}

			}
		});

	}

}
