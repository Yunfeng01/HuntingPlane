import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.JScrollPane;
/**
* CPSC 1181 Assignment #10
* <p>
* I pledge that I have completed the programming assignment independently.
* I have not copied the code from a student or any other source.
* I have not given my code to any student. <br />
* Zhao,Yunfeng(Wilson), April 5th, 2012
* <p>
* Write a program that to implement the server part of the board memory card game 
* There are at least two players can start this game.
* If there are more players join the game. Then, each pair can join one game
* <p>
* @author Zhao,Yunfeng(Wilson) 100169103
* @version 1
*/
public class PlaneServer extends JFrame implements PlaneConst
{
    public static void main(String[] args)
    {
        //start the server
        new PlaneServer();
    }
    /**
        * construct the GameServer
        */
    public PlaneServer()
    {
        setLayout(new BorderLayout()); //set layout
        JTextArea resultArea=new JTextArea();
        resultArea.setFont(new Font("Sanserif",Font.BOLD,20)); //set Font
        add(new JScrollPane(resultArea),BorderLayout.CENTER); //implement the scoller
        setTitle("Hunting Plane Server"); 
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        try
        {
            ServerSocket serverSocket=new ServerSocket(PORT);
            resultArea.append("Server started at "+new Date()+"\n");
            resultArea.append("This server's compuetr name is "+InetAddress.getLocalHost().getHostName()+"\n");
            resultArea.append("This server's IP address is "+InetAddress.getLocalHost().getHostAddress()+"\n");
            //number the game session
            int session=1;
            while(true)
            {
                //I get the idea of handling two uers one thread from D. Y. Liang?
                resultArea.append(new Date() +": Wait for players to join session " + session + '\n');
                Socket socket1=serverSocket.accept(); //accept first player
                resultArea.append("Opening socket for client 1 "+"at "+new Date()+"\n");
                InetAddress addr1=socket1.getInetAddress();
                resultArea.append("Client 1"+"'s name is "+addr1.getHostName()+" and IP address is "+addr1.getHostAddress()+"\n");
                // Notify that the player is Player 1
                new DataOutputStream(socket1.getOutputStream()).writeInt(PLAYER1);
                
                Socket socket2=serverSocket.accept(); //accept second player
                resultArea.append("Opening socket for client 2 "+"at "+new Date()+"\n");
                InetAddress addr2=socket2.getInetAddress();
                resultArea.append("Client 2"+"'s name is "+addr2.getHostName()+" and IP address is "+addr2.getHostAddress()+"\n");
                // Notify that the player is Player 2
                new DataOutputStream(socket2.getOutputStream()).writeInt(PLAYER2);
                HandleClient task=new HandleClient(socket1,socket2,resultArea);
                //start new Thread
                new Thread(task).start();
                session++;
            }
        }
        catch(IOException e)
        {
            resultArea.append("Here is a network error\n");
        }
    }
}