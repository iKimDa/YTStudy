package firstClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Client {		
	private Socket clientSocket = null;	
	private ObjectInputStream readStream = null;
	private ObjectOutputStream writeStream = null;
	
	private Thread receiveThread = null;
	private Thread sendThread = null;
	
	// ���� ���� �� ������ ����
	void bind(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			readStream = new ObjectInputStream(clientSocket.getInputStream());
			
			login();	// �α��� �޽��� ����
			
			sendThread = new Thread(new sendMessageHandler(writeStream));
			receiveThread = new Thread(new receiveMessageHandler(readStream));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ä�� ����
	void start() {
		// ������ �ޱ�/������ �ڵ鷯 start
		if (clientSocket != null) {
			sendThread.start();		// ������ ������
			receiveThread.start();	// �ޱ� ������
		}
	}
	
	void login() throws IOException {		
		System.out.print("ID�Է�: ");
		String id = new Scanner(System.in).nextLine();
		
		try {
			writeStream.writeObject(new Message(Message.type_LOGIN, id));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		Client ct = new Client();
		ct.bind("localhost", 2222);
		ct.start();
	}

}
