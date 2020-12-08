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

//사용자로부터 인증키를 입력받고, 입력키가 존재하면 전화번호를 return
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
			if (Server.map.containsKey(Rand_num)) { //Rand키가 존재하면 그 안에 존재하는 Phone_number를 얻어오고 본인의 Phone_number를 집어넣음
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

//사용자로부터 인증키를 입력받음
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

//Server에서 data를 안드로이드로 보내줌
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

//While문을 돌면서 해당 난수가 있는지 검사하고 난수가 없으면 종료
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

//안드로이드에서 Server로 보낸 data를 받아서 HashMap에 저장 후 send, timer 쓰레드 실행
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



//안드로이드에서 Server로 Data를 보내줌
class Thread_Recv extends Thread { //recv thead 실행
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

class Timer extends Thread { //60초동안 서버에 Rand_num이남아있고 60초가 지나면 Rand_num을 지우고 소켓을 닫아버림
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
	// 인증키 받아올 port
	public static final int RECV_PORT = 10000;
	// 결과를 보내줄 port
	public static final int SEND_PORT = 10001;
	static Map<String, String> map = new HashMap<String, String>();

	public static void main(String[] args) {
		Thread_Send t1 = new Thread_Send();
		t1.start();
		Thread_Recv t2 = new Thread_Recv();
		t2.start();
	}
}
