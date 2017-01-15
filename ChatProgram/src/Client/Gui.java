package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui {
	private static JFrame mainFrame;
	private static JPanel chatPane;
	private static JTextArea chatText;
	private static JTextField chatLine;
	
	public String getChatText() {
		return chatText.getText();
	}

	public static String getChatLine() {
		return chatLine.getText();
	}
	
	public static void setChatText(String text) {
		Gui.chatText.setText(text);
	}

	public static void setChatLine(String text) {
		Gui.chatLine.setText(text);
	}
	
	public Gui() {
		chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true); // textbox �׵θ�
		chatText.setEditable(false); // textbox ��������
		chatText.setForeground(Color.blue); // �۾���
		
		JScrollPane chatTextPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		chatLine = new JTextField();
		chatLine.setEnabled(true);
		chatLine.addKeyListener(new KeyEventListener());

		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(400, 400));

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(chatPane, BorderLayout.CENTER);

		mainFrame = new JFrame("Multi Chatting");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
}
