package Client;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class Gui {
	private static JFrame mainFrame;
	private static JPanel chatPane;
	private static JTextArea chatText;
	private static JTextField chatLine;

	private SendMessageHandler sendHandler;
	private ReceiveMessageHandler receiveHandler;

	private Thread receiveThread;
	private Thread sendThread;

	public String getChatText() {
		return chatText.getText();
	}

	public static String getChatLine() {
		return chatLine.getText();
	}

	public static void setChatText(String text) {
		chatText.setText(text);
	}

	public static void setChatLine(String text) {
		chatLine.setText(text);
	}

	public Gui(ObjectInputStream readStream, ObjectOutputStream writeStream) {
		this.sendHandler = new SendMessageHandler(writeStream, this);
		this.receiveHandler = new ReceiveMessageHandler(readStream, this);

		initChatRoom();
	}

	public void initChatRoom() {
		chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true); // textbox �׵θ�
		chatText.setEditable(false); // textbox ��������
		chatText.setForeground(Color.blue); // �۾���
		
		DefaultCaret caret = (DefaultCaret) chatText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);	// ��ũ�ѹٰ� �׻� textarea �ϴܿ� ��ġ 

		 JScrollPane chatTextPane = new JScrollPane(chatText,
		 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		 
		chatLine = new JTextField();
		chatLine.setEnabled(true);
		chatLine.addKeyListener(this.sendHandler); // Ű�̺�Ʈ

		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(400, 400));

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(chatPane, BorderLayout.CENTER);

		mainFrame = new JFrame("ä�� ���α׷�");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public void createThread() {
		sendThread = new Thread(this.sendHandler);
		receiveThread = new Thread(this.receiveHandler);
	}

	public void startThread() {
		receiveThread.start();
		sendThread.start();

		try {
			receiveThread.join();
			sendThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
