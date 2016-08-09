import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

class ChatClient implements ActionListener
{
	JFrame mainFrame;
	JLabel user;
	JTextArea userArea;
	JButton login;
	ChatClient(String s)
	{
		mainFrame = new JFrame(s);
		
		user = new JLabel("Enter user name : ");
		
		userArea = new JTextArea();
		userArea.setPreferredSize(new Dimension(100,18));

		login = new JButton("login");
		login.addActionListener(this);

		mainFrame.add(user);
		mainFrame.add(userArea);
		mainFrame.add(login);
		mainFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		mainFrame.setSize(300,300);
	}
	public static void main(String[] args)
	{
		new ChatClient("Chat Client");
	}
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getSource()==login)
			{
				new AddUser(userArea.getText());
				mainFrame.dispose();
			}
		}catch(Exception ex){System.out.println(ex);}
	}
}
class AddUser implements ActionListener
{
	//Connection and chat.
	Socket s;
	DataInputStream din;
	DataOutputStream dout;

	//Frame.
	JFrame mainFrame;
	JPanel first,second,third;
	JTextArea chatArea , logArea , messageArea;
	JButton send,logout;
	JScrollPane scroll;
	String userName;

	AddUser(String userName) throws Exception
	{	//Frame
		this.userName = userName.trim();
		mainFrame = new JFrame("Connected as : " + this.userName);		
		createChatFrame();
		s= new Socket("localhost",9);
		din = new DataInputStream(s.getInputStream());
		dout = new DataOutputStream(s.getOutputStream());
		dout.writeUTF(userName);
		My m = new My();
		Thread t1 = new Thread(m);
		t1.start();
	}
	public void createChatFrame()
	{
		first = new JPanel(new GridLayout(1,2,1,0));
		second = new JPanel(new GridLayout(1,1,0,0));
		third = new JPanel(new FlowLayout());

		chatArea = new JTextArea();
		scroll = new JScrollPane(chatArea);
		chatArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Chat"),BorderFactory.createEmptyBorder(5,5,5,5)),chatArea.getBorder()));
		chatArea.setEditable(false);
		first.add(scroll);

		logArea = new JTextArea();
		scroll = new  JScrollPane(logArea);
		logArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Logged Profile"),BorderFactory.createEmptyBorder(5,5,5,5)),logArea.getBorder()));
		logArea.setEditable(false);
		first.add(scroll);

		messageArea = new JTextArea();
		scroll = new  JScrollPane(messageArea);
		messageArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Type Message Here ..."),BorderFactory.createEmptyBorder(5,5,5,5)),messageArea.getBorder()));
		second.add(messageArea);

		send = new JButton("send");
		send.addActionListener(this);
		third.add(send);

		logout = new JButton("logout");
		logout.addActionListener(this);
		third.add(logout);

		mainFrame.add(first);
		mainFrame.add(second);
		mainFrame.add(third);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new GridLayout(3,1,0,1));
		mainFrame.setVisible(true);
		mainFrame.setSize(400,400);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == send)
		{
			try
			{
				dout.writeUTF(messageArea.getText());
				messageArea.setText("");
			}catch(Exception ex){System.out.println(ex);}
		}
		if(e.getSource() == logout)
		{
			try
			{
				dout.writeUTF("end");
				System.exit(0);
			}catch(Exception ex){System.out.println(ex);}
		}
	}
	class My implements Runnable
	{
		//DataInputStream din;
		public void run()
		{
			String s2="";
			try
			{
			while(true)
				{
					s2 = din.readUTF();
					if((s2.matches("(.*)logged in.")) || (s2.matches("(.*)logged out.")))
						logArea.append(s2+"\n");
					else
					{
						chatArea.append(s2+"\n");
						chatArea.setCaretPosition(messageArea.getDocument().getLength());
					}
				}
			}catch(Exception e){System.out.println(e);}
		}
	}
}