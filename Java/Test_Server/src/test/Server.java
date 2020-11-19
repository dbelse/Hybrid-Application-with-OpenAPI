package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ListSelectionEvent;

//����ڷκ��� ����Ű�� �Է¹ް�, �Է�Ű�� �����ϸ� ��ȭ��ȣ�� return
class Send_Send extends Thread {
	Socket client;
	String Phone_num;
	String Rand_num;

	Send_Send(Socket client, String Phone_num, String Rand_num) {
		this.client = client;
		this.Phone_num = Phone_num;
		this.Rand_num = Rand_num;
	}

	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
			if (Server.map.containsKey(Rand_num)) { //RandŰ�� �����ϸ� �� �ȿ� �����ϴ� Phone_number�� ������ ������ Phone_number�� �������
				out.println(Server.map.get(Rand_num));
				Server.map.remove(Rand_num);
				Server.map.put(Rand_num, Phone_num);
			} 
			else {
				out.println("Wrong");
				//System.out.println("Client Wrong");
			}
			client.close();
			System.out.println("Send Socket close");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

//����ڷκ��� ����Ű�� �Է¹���
class Send_Recv extends Thread {
	Socket client;
	String Rand_num;
	String Phone_num;

	Send_Recv(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			Phone_num = in.readLine();
			Rand_num = in.readLine();
			System.out.println("Received from + " + Phone_num + " : " + Rand_num);
			Send_Send t2 = new Send_Send(client, Phone_num, Rand_num);
			t2.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

//Server���� data�� �ȵ���̵�� ������
class Thread_Send extends Thread {
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(Server.SEND_PORT);
			while (true) {
				Socket client = serverSocket.accept();
				Send_Recv t1 = new Send_Recv(client);
				t1.start();
			}
		} catch (Exception e) {
			System.out.println("S: ERR");
			e.printStackTrace();
		}
	}
}

//While���� ���鼭 �ش� ������ �ִ��� �˻��ϰ� ������ ������ ����
class Recv_Send extends Thread {
	Socket client;
	String Rand_num;

	Recv_Send(Socket client, String Rand_num) {
		this.client = client;
		this.Rand_num = Rand_num;
	}

	@Override
	public void run() {
		try {
			while (true)
			{	
				if (!Server.map.containsKey(Rand_num)) {
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
					out.println(Server.map.get(Rand_num));
					Server.map.remove(Rand_num);
					this.client.close();
					System.out.println("Recv Socket close(" + Rand_num + ")");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//�ȵ���̵忡�� Server�� ���� data�� �޾Ƽ� HashMap�� ���� �� send, timer ������ ����
class Recv_Recv extends Thread {
	Socket client;
	String Phone_num;
	String Rand_num;

	Recv_Recv(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			Phone_num = in.readLine();
			Rand_num = in.readLine();
			System.out.println("Received from + " + Phone_num + " : " + Rand_num);
			
			Server.map.put(Rand_num, Phone_num);
			
			Timer t1 = new Timer(client, Rand_num);
			t1.start();
			
			Recv_Send t2 = new Recv_Send(client, Rand_num);
			t2.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}



//�ȵ���̵忡�� Server�� Data�� ������
class Thread_Recv extends Thread { //recv thead ����
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(Server.RECV_PORT);
			while (true) {
				Socket client = serverSocket.accept();
				Recv_Recv t1 = new Recv_Recv(client);
				t1.start();
			}
		} catch (Exception e) {
			System.out.println("S: ERR");
			e.printStackTrace();
		}
	}
}

class Timer extends Thread { //60�ʵ��� ������ Rand_num�̳����ְ� 60�ʰ� ������ Rand_num�� ����� ������ �ݾƹ���
	Socket client;
	String Rand_num;

	Timer(Socket client, String Rand_num) {
		this.client = client;
		this.Rand_num = Rand_num;
	}

	@Override
	public void run() {
		int count = 0;
		while (count != 60) {
			try {
				sleep(1000);
				count++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("time out " + Rand_num);
		System.out.println("time out " + Rand_num);
		Server.map.remove(this.Rand_num);

	}
}

public class Server {
	// ����Ű �޾ƿ� port
	public static final int RECV_PORT = 10000;
	// ����� ������ port
	public static final int SEND_PORT = 10001;
	static Map<String, String> map = new HashMap<String, String>();

	public static void main(String[] args) {
		Thread_Send t1 = new Thread_Send();
		t1.start();
		Thread_Recv t2 = new Thread_Recv();
		t2.start();
	}
}
