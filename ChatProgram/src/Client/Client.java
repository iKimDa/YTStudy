package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import common.Message;


public class Client {		
	private Socket clientSocket;	
	private ObjectInputStream readStream;
	private ObjectOutputStream writeStream;
	
	private Gui gui;
	
	// ���� ���� �� ������ ����
	public void setting(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			readStream = new ObjectInputStream(clientSocket.getInputStream());
			
			gui = new Gui(readStream, writeStream);
			gui.createThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		login();	// �α���
		gui.startThread();
	}		
	
	private void login() {
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
		ct.setting("localhost", 3000);
		ct.start();
	}

}
