package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import common.Message;

public class Client {
	// �ӼӸ�, ���, ä��â Ŭ���� ��� �߰�
	private Socket clientSocket;
	private ObjectInputStream readStream;
	private ObjectOutputStream writeStream;

	// ���� ���� �� ������ ����
	public void setting(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			readStream = new ObjectInputStream(clientSocket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Login loginUI = new Login(); // �α���
		Gui gui = new Gui();
		gui.startThread();

	}

	private class Login extends JFrame implements ActionListener {

		private JTextField tf;
		private JButton login;
		JLabel loginText = new JLabel();

		public Login() {
			JPanel idPanel = new JPanel();
			tf = new JTextField(12);
			loginText.setForeground(Color.RED);

			JLabel idLabel = new JLabel("ID : ");

			login = new JButton("LOGIN");
			login.addActionListener(this);

			idPanel.add(idLabel);
			idPanel.add(tf);

			this.add(idPanel);
			this.add(login);
			this.add(loginText);

			setLayout(new FlowLayout());

			setTitle("Login");
			setLocation(200, 200);
			setSize(280, 100);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			loginText.setText("ID�� �Է��Ͻÿ�");

			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String id = tf.getText();
			try {
				writeStream.writeObject(new Message(Message.type_LOGIN, id));
				dispose();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public class Gui implements ActionListener {
		private JFrame mainFrame;
		private JPanel chatPane;
		private JPanel buttonPane; 
		private JTextArea chatText;
		private JTextField chatLine;
		private JButton clearBtn;
		private JButton lockBtn; 
		private JButton exitBtn; 

		private SendMessageHandler sendHandler;
		private ReceiveMessageHandler receiveHandler;

		private Thread receiveThread;
		private Thread sendThread;

		public JTextArea getChatText() {
			return chatText;
		}

		public JTextField getChatLine() {
			return chatLine;
		}

		public void setChatText(String text) {
			chatText.setText(text);
		}

		public void setChatLine(String text) {
			chatLine.setText(text);
		}
		
		public JButton getClearBtn() {
			return clearBtn;
		}

		public Gui() {
			this.sendHandler = new SendMessageHandler(writeStream, this);
			this.receiveHandler = new ReceiveMessageHandler(readStream, this);

			sendThread = new Thread(this.sendHandler);
			receiveThread = new Thread(this.receiveHandler);

			initChatRoom();
		}

		public void initChatRoom() {
			chatPane = new JPanel(new BorderLayout());
						
			/* chatPane */
			chatText = new JTextArea(10, 20);
			chatText.setLineWrap(true); // textbox �׵θ�
			chatText.setEditable(false); // textbox ��������
			chatText.setForeground(Color.blue); // �۾���

			DefaultCaret caret = (DefaultCaret) chatText.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // ��ũ�ѹٰ� �׻�  textarea �ϴܿ� ��ġ
						
			JScrollPane chatTextPane = new JScrollPane(chatText,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			chatLine = new JTextField();
			chatLine.setEnabled(true);
			chatLine.addKeyListener(this.sendHandler); // Ű�̺�Ʈ

			chatPane.add(chatLine, BorderLayout.SOUTH);
			chatPane.add(chatTextPane, BorderLayout.CENTER);
			chatPane.setPreferredSize(new Dimension(400, 400));
			/* */
			
			/* buttonPane */
			
			buttonPane = new JPanel(new GridLayout(5, 0, 0, 50)); // ���� 5 x ���� 0 , ���� 50
			buttonPane.setPreferredSize(new Dimension(80, 400));
			
			clearBtn = new JButton("clear");
			lockBtn  = new JButton("lock");
			exitBtn  = new JButton("exit");
								
			clearBtn.addActionListener(this);		
			lockBtn.addActionListener(this);					
			exitBtn.addActionListener(this);
			
			buttonPane.add(clearBtn);
			buttonPane.add(lockBtn);
			buttonPane.add(exitBtn);		
			
			/* */

			/* mainPane */
			JPanel mainPane = new JPanel(new FlowLayout());
			mainPane.add(chatPane);
			mainPane.add(buttonPane);
			/* */
			
			mainFrame = new JFrame("YettieSoft ChatProgram");
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setContentPane(mainPane);
			mainFrame.setSize(mainFrame.getPreferredSize());
			mainFrame.setLocation(200, 200);
			mainFrame.pack();
			mainFrame.setVisible(true);
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
		
		public void actionPerformed(ActionEvent e) {
			// �׼� ������ ������
			if (e.getSource().equals(clearBtn)) {
				chatText.setText("");
				receiveHandler.setContents("");
			}
			else if (e.getSource().equals(lockBtn)) {
				if( lockBtn.getText().equals("lock")) {
					chatPane.setVisible(false);
					clearBtn.setVisible(false);
					lockBtn.setText("unlock");
				}
				else {
					chatPane.setVisible(true);
					clearBtn.setVisible(true);
					lockBtn.setText("lock");
				}
			}
			else if (e.getSource().equals(exitBtn)) {
				System.exit(0);
			}
		}
	}

	public static void main(String[] args) {
		Client ct = new Client();
		//ct.setting("172.16.10.205", 3000);
		ct.setting("127.0.0.1", 3000);
		ct.start();
	}

}
