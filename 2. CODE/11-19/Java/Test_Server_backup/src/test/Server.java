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

class Send_Send extends Thread
{
	Socket client;
	String Rand_num;
	Send_Send(Socket client, String Rand_num)
	{
		this.client = client;
		this.Rand_num = Rand_num;
	}
	@Override
	public void run()
	{
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
			if(Server.map.containsKey(Rand_num)) {
			out.println(Server.map.get(Rand_num));
			System.out.println("Client collect");
			Server.map.remove(Rand_num);
			}
			else {
				out.println("No Rand_num!");
				System.out.println("Client Wrong");
			}
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

//사용자로부터 인증키를 입력받음

class Send_Recv extends Thread
{
	Socket client;
	String str;
	Send_Recv(Socket client)
	{
		this.client = client;
	}
	@Override
	public void run()
	{
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			str = in.readLine();
			System.out.println(str);
			Send_Send t2 = new Send_Send(client, str);
			t2.start();
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

//Server에서 data를 안드로이드로 보내줌
class Thread_Send extends Thread{
	@Override
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(Server.SEND_PORT);
			while(true)
			{
				Socket client  = serverSocket.accept();
				Send_Recv t1 = new Send_Recv(client);
				t1.start();
			}
		}catch (Exception e)
		{
			System.out.println("S: ERR");
			e.printStackTrace();
		}
	}
}

//While문을 돌면서 해당 난수가 있는지 검사하고 난수가 없으면 종료
class Recv_Send extends Thread
{
	Socket client;
	String Rand_num;
	Recv_Send(Socket client,String Rand_num)
	{
		this.client = client;
		this.Rand_num = Rand_num;
	}
	@Override
	public void run()
	{
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
			while(true)
			{
				if(!Server.map.containsKey(Rand_num)) {
				out.print("connect!");
				client.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

//안드로이드에서 Server로 보낸 data를 받아서 HashMap에 저장 후 send 쓰레드 실행
class Recv_Recv extends Thread
{
	Socket client;
	String Phone_num;
	String Rand_num;
	Recv_Recv(Socket client)
	{
		this.client = client;
	}
	@Override
	public void run()
	{
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			Phone_num = in.readLine();
			System.out.println(Phone_num);
			Rand_num = in.readLine();
			System.out.println(Rand_num);
			Server.map.put(Rand_num,Phone_num);
			
			Recv_Send t2 = new Recv_Send(client,Rand_num);
			t2.start();
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

//안드로이드에서 Server로 Data를 보내줌
class Thread_Recv extends Thread{
	@Override
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(Server.RECV_PORT);
			while(true)
			{
				Socket client  = serverSocket.accept();
				System.out.println("Receiving...");
				Recv_Recv t1 = new Recv_Recv(client);
				t1.start();
//				for(String i : Server.map.keySet())
//				{
//					System.out.println("key : " + i + ", value : " + Server.map.get(i));
//				}
			}
		}catch (Exception e)
		{
			System.out.println("S: ERR");
			e.printStackTrace();
		}
	}
}

public class Server{
	//인증키 받아올 port
	public static final int RECV_PORT = 10000;
	//결과를 보내줄 port
	public static final int SEND_PORT = 10001;
	static Map<String,String> map = new HashMap<String,String>();
	public static void main(String[] args) {
		Thread_Send t1 = new Thread_Send();
		t1.start();
		Thread_Recv t2 = new Thread_Recv();
		t2.start();
	}
}
