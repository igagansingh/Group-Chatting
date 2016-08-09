import java.io.*;
import java.net.*;
import java.util.*;

class ChatServer
{
	ArrayList socketList = new ArrayList();
	ArrayList users = new ArrayList();
	ServerSocket serverSocket;
	Socket socket;
	ChatServer()
	{
		try
		{
			serverSocket = new ServerSocket(9);
			System.out.println("SERVER STARTED");
			while(true)
			{
				socket = serverSocket.accept();
				socketList.add(socket);
				Runnable r = new MyThread(socket,socketList,users);
				Thread t = new Thread(r);
				t.start();
			}
		}catch(Exception e){System.out.println(e);}
	}
	public static void main(String[] args)
	{
		new ChatServer();
	}
}
class MyThread implements Runnable
{
	String name = "";
	Socket s;
	ArrayList socketList,users;
	DataInputStream din;
	DataOutputStream dout;

	MyThread(Socket s,ArrayList socketList, ArrayList users)throws Exception
	{
		this.socketList = socketList;
		this.users = users;
		this.s = s;
		din = new DataInputStream(s.getInputStream());
	
		name = din.readUTF();
		System.out.println(name + " connected");
		users.add(name);
		broadcast(name, " logged in.");
	}
	public void broadcast(String name , String message)throws Exception
	{
		Iterator i = socketList.iterator();
		while(i.hasNext())
		{
			Socket socket =(Socket)i.next();
			dout = new DataOutputStream(socket.getOutputStream());
			if(message.equals(" logged in.") || message.equals(" logged out."))
			{
				if(socket == s)
				{
					Iterator j=users.iterator();
					while(j.hasNext())
						dout.writeUTF(j.next().toString().toUpperCase()+" "+message);
				}
				else
				{
					dout.writeUTF(name.toUpperCase()+" "+message);
					dout.flush();		
				}
			}
			else
			{
				dout.writeUTF(name.toUpperCase()+": "+message);
				dout.flush();
			}
		}
	}
	public void run()
	{
		String line;
		try
		{
			while(true)
			{
				line=din.readUTF();
				if(line.equals("end"))
				{
					broadcast(name," logged out.");
					socketList.remove(this);
					users.remove(name);
					break;
				}
				broadcast(name,line);
			}
		}catch(Exception e){System.out.println(e);}
	}
}