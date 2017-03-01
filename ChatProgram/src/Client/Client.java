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
        /*
        �÷ο�? �帧?? ������ ���ݾ� ����ɵ�.
        1. �ϳ��� ���̵�� ���� ä�� �濡 �����׶� Gui ���̵� �Է¹޾� ä�ù濡�� ������ ����..
        2. ���ο� ä�ù濡 �����׶����� ���̵� �Է¹���. Gui �ȿ� loginUI ����
        3. ��.. Ȯ�尡���ϰ�.. ū �гξȿ� ���� ó���ص� �ɵ�... => ������....
         */

        // ����!!! �ȿ��� ȣ���ϸ�ȵɱ�.. ���.
        //Login loginUI = new Login(); // �α���;
        Gui gui = new Gui();

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
			//setSize(getPreferredSize());
			setSize(280, 300);
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
        private JPanel mainPane;
		private JPanel chatPane;
		private JPanel buttonPane; 
		private JTextArea chatText;
		private JTextField chatLine;
		private JButton clearBtn;
		private JButton lockBtn; 
		private JButton exitBtn;
        private JTextField lockPasswordTextField;
        private JPanel loginJPanel;
        private JButton loginBtn;
        private JTextField loginTextField;

		private SendMessageHandler sendHandler;
		private ReceiveMessageHandler receiveHandler;

		private Thread receiveThread;
		private Thread sendThread;

        private String id;

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

        public void setId(String id){
            this.id = id;
            postLoginId(id);
        }

		public Gui() {
			this.sendHandler = new SendMessageHandler(writeStream, this);
			this.receiveHandler = new ReceiveMessageHandler(readStream, this);

			sendThread = new Thread(this.sendHandler);
			receiveThread = new Thread(this.receiveHandler);

            initChatRoom();
            startThread();
		}

        private void postLoginId(String id) {
            try {
                writeStream.writeObject(new Message(Message.type_LOGIN, id));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public JPanel loginUI() {
            JPanel loginJPanel = new JPanel();

            JLabel loginText = new JLabel();
            JPanel idPanel = new JPanel();
            loginTextField = new JTextField(12);
            loginText.setForeground(Color.RED);

            JLabel idLabel = new JLabel("ID : ");

            loginBtn = new JButton("LOGIN");
            loginBtn.addActionListener(this);

            idPanel.add(idLabel);
            idPanel.add(loginTextField);

            loginJPanel.add(idPanel);
            loginJPanel.add(loginBtn);
            loginJPanel.add(loginText);

            loginJPanel.setLayout(new FlowLayout());

            loginJPanel.setLocation(200, 200);
            loginJPanel.setSize(280, 100);
            //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginText.setText("ID�� �Է��Ͻÿ�");

            //setVisible(true);

            return loginJPanel;
        }

		public void initChatRoom() {
			chatPane = new JPanel(new BorderLayout());
            loginJPanel = loginUI();
						
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

			/* lock password */
            lockPasswordTextField = new JTextField(20);
			
			/* buttonPane */
            buttonPane = new JPanel(new GridLayout(5, 0, 0, 50)); // ���� 5 x ���� 0 , ���� 50
			buttonPane.setPreferredSize(new Dimension(80, 400));

			clearBtn = new JButton("clear");
			lockBtn  = new JButton("lock");
			exitBtn  = new JButton("exit");

			clearBtn.addActionListener(this);		
			lockBtn.addActionListener(this);					
			exitBtn.addActionListener(this);

            buttonPane.add(lockPasswordTextField);
            lockPasswordTextField.setVisible(false);
			buttonPane.add(clearBtn);
			buttonPane.add(lockBtn);
			buttonPane.add(exitBtn);
			
			/* */

			/* mainPane */
			mainPane = new JPanel(new FlowLayout());
//			mainPane.add(chatPane);
//			mainPane.add(buttonPane);
            mainPane.add(loginJPanel);
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
                    /*
                     ��������� ��������...... ��������..
                     �켱.. visible���ϸ�ȵǰ�.. remove�� ó���ؼ� �ؾߵɵ�.. ��ġ�� �������� ��.....
                     swing�����ô�.... ��ɵ��� Ŀ��.
                    */
                    buttonPane.setPreferredSize(new Dimension(200, 400));
                    mainFrame.pack();

					chatPane.setVisible(false);
					clearBtn.setVisible(false);
					lockBtn.setText("unlock");
                    lockPasswordTextField.setVisible(true);
				}
				else {
                    /* lockPasswordTextField.getText() �˾Ƴ� ������ ��й�ȣ Ȯ���� Ʈ���̸� ��ǰ */
                    if(lockCheck(lockPasswordTextField.getText())){
                        // ������ ����
                        buttonPane.setPreferredSize(new Dimension(80, 400));
                        mainFrame.pack();

                        lockPasswordTextField.setVisible(false);
                        chatPane.setVisible(true);
                        clearBtn.setVisible(true);
                        lockBtn.setText("lock");
                    }
                    else {
                        /* error �޼���... ���... */
                    }
				}
			}
			else if (e.getSource().equals(exitBtn)) {
				System.exit(0);
			}
            else if(e.getSource().equals(loginBtn)) {
                postLoginId(loginTextField.getText());
                mainPane.remove(loginJPanel);

                mainFrame.setPreferredSize(new Dimension(550, 480));
                mainFrame.pack();
                mainPane.add(chatPane);
			    mainPane.add(buttonPane);
            }
		}

        private Boolean lockCheck(String password) {
            // ������ ����!
            return true;
        }
	}

	public static void main(String[] args) {
		Client ct = new Client();
		//ct.setting("172.16.10.205", 3000);
		ct.setting("127.0.0.1", 3000);
		ct.start();
	}

}
