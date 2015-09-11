import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
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
public class HandleClient implements Runnable, PlaneConst
{
    private Socket socket1; //instance socket of player1
    private Socket socket2; //instance socket of player2
    private JTextArea reportArea;
    private int[][] table1;
    private int[][] table2;
    private int[] randomCardValue; //store 
    private int[] memoryCardIndex; //remember the card index the last two time the user chooses
    private int[] memoryCardValue; //remember the card value the last two time the user chooses
    private int correct1; //record how many same cards player1 gets
    private int correct2; //record how many same cards player2 gets
    private int counter; //count how many times one player clicks the cards(if the counter ==2 then decides whether covers or keeps the last two cards
    private int winner; //remeber the winner of this game
    private int turn = 0;
    DataInputStream fromClient1; //input stream from player1
    DataOutputStream toClient1; //output stream from player1
    DataInputStream fromClient2; //input stream from player2
    DataOutputStream toClient2; //output stream from player2
    /**
        * construct the HandleClient class
        * @param  s1 the socket from player1
        * @param  s2 the socket from player2
        * @param  rA the JTextArea which shows the command
        */
    public HandleClient(Socket s1,Socket s2,JTextArea rA)
    {
        socket1=s1;
        socket2=s2;
        reportArea=rA;
        //~ randomCardValue=new int[TABLE_H*TABLE_W]; //initialize the value of image of each card
        initial(); //initializes all the instance variable as the game begins
    }
    /**
        * initializes all the instance variable as the game begins
        */
    private void initial()
    {
        table1=new int[TABLE_H][TABLE_W];
        table2=new int[TABLE_H][TABLE_W];
        //~ shuffleCards(randomCardValue); //shuffles all the cards
        //~ memoryCardIndex=new int[2]; //remember last two times card index
        //~ memoryCardValue=new int[2]; //remember last two times card value
        counter=0; //how many times the player clicks
        correct1=0; //the number of player1 gets correctly
        correct2=0; //the number of player2 gets correctly
        winner=PLAYER1; //default winner is PLAYER1
    }
    /** 
        * Implement the run() method for the thread 
        */
    public void run()
    {
        try
        {
            try
            {
                fromClient1=new DataInputStream(socket1.getInputStream());
                toClient1=new DataOutputStream(socket1.getOutputStream());
                fromClient2=new DataInputStream(socket2.getInputStream());
                toClient2=new DataOutputStream(socket2.getOutputStream());
                executeCmds(); // handle the commands
            }
            catch(IOException e)
            {
                reportArea.append("Here is a network error1\n");
            }
            finally
            {
                //close both sockets
                socket1.close(); 
                socket2.close();
            }
        }
        catch(IOException e)
        {
            reportArea.append("Here is a network error2\n");
        }
    }
    /**
        * executes the commands and sends back to clients
        */
    private void executeCmds() throws IOException
    {
        while(true)
        {
            int cmd; //command from the players
            if(turn==0)
            {
                int start1 = fromClient1.readInt();
                for (int i = 0; i < TABLE_H; i++)
                    for (int j = 0; j < TABLE_W; j++)
                        table1[i][j]=fromClient1.readInt();
                reportArea.append("Play1 start");
                toClient1.writeInt(WAIT);   
                
                int start2 = fromClient2.readInt();
                for (int i = 0; i < TABLE_H; i++)
                    for (int j = 0; j < TABLE_W; j++)
                        table2[i][j]=fromClient2.readInt();
                reportArea.append("Play2 start");
                toClient2.writeInt(WAIT);
                
                if(start1==START&&start2==START)
                {
                    toClient1.writeInt(START); // lets PLAYER1 to start playing
                    toClient1.writeInt(PLAYER1);
                    toClient1.flush();
                    toClient2.writeInt(START); //also sends to PLAYER2
                    toClient2.writeInt(PLAYER1);
                    toClient2.flush();
                    turn = PLAYER1;
                }
            }
            if(turn==PLAYER1) //when it is PLAYER1's turn
            {
                cmd=fromClient1.readInt();//read command from player1
                if(cmd==QUIT) //if player presses "quit"
                {
                    handleOneQuits(turn); //handle the "quit" command
                }
                if(cmd==FIGHT)
                {
                    int row=fromClient1.readInt();
                    int column=fromClient1.readInt();
                    if(table2[row][column]==HEAD)
                        correct1++;
                    toClient1.writeInt(cmd);
                    toClient1.writeInt(row);
                    toClient1.writeInt(column);
                    toClient1.writeInt(table2[row][column]);
                    //~ toClient1.flush();
                    toClient2.writeInt(SHOW);
                    toClient2.writeInt(row);
                    toClient2.writeInt(column);
                    toClient2.writeInt(table2[row][column]);
                    //~ toClient2.flush();
                }
                //~ if(counter==2) //if this player clicks twice
                //~ {
                    //~ checkTwoCards(turn);//check the last two cards and handle it
                    if(correct1!=TOTAL) 
                    {   //if no one wins, then changes the player
                        turn=PLAYER2;
                        toClient1.writeInt(START);
                        toClient1.writeInt(PLAYER2);
                        toClient1.flush();
                        toClient2.writeInt(START);
                        toClient2.writeInt(PLAYER2);
                        toClient2.flush();
                    }
                    if(correct1==TOTAL)
                    {
                        //~ checkWhoWins(); //check who wins
                        toClient1.writeInt(WIN);
                        toClient2.writeInt(LOSE);
                        winner=PLAYER1;
                        turn=0;
                        restart(); //restart the game
                    }
            }
            if(turn==PLAYER2) //when it is PLAYER2's turn
            {
                cmd=fromClient2.readInt();//read command from player1
                if(cmd==QUIT) //if player presses "quit"
                {
                    handleOneQuits(turn); //handle the "quit" command
                }
                if(cmd==FIGHT)
                {
                    int row=fromClient2.readInt();
                    int column=fromClient2.readInt();
                    if(table1[row][column]==HEAD)
                        correct2++;
                    toClient2.writeInt(cmd);
                    toClient2.writeInt(row);
                    toClient2.writeInt(column);
                    toClient2.writeInt(table1[row][column]);
                    toClient2.flush();
                    toClient1.writeInt(SHOW);
                    toClient1.writeInt(row);
                    toClient1.writeInt(column);
                    toClient1.writeInt(table1[row][column]);
                    toClient1.flush();
                }
                //~ if(counter==2) //if this player clicks twice
                //~ {
                    //~ checkTwoCards(turn);//check the last two cards and handle it
                    if(correct2!=TOTAL) 
                    {   //if no one wins, then changes the player
                        turn=PLAYER1;
                        toClient2.writeInt(START);
                        toClient2.writeInt(PLAYER1);
                        toClient2.flush();
                        toClient1.writeInt(START);
                        toClient1.writeInt(PLAYER1);
                        toClient1.flush();
                    }
                    else
                    {
                        //~ checkWhoWins(); //check who wins
                        toClient2.writeInt(WIN);
                        toClient1.writeInt(LOSE);
                        winner=PLAYER2;
                        turn=0;
                        restart(); //restart the game
                    }                 
                //~ }
            }
        }
    }
    /**
        * handle the situation if one quits the game
        * @param  turn records whose turn
        */
    private void handleOneQuits(int turn) throws IOException
    {
        reportArea.append("QUIT command received from client");
        if(turn==PLAYER1) //if player1 quits
        {
            toClient1.writeInt(LOSE); //player1 loses
            toClient1.flush();
            toClient2.writeInt(WIN); //player2 wins
            toClient2.flush();
            winner=PLAYER2;
        }
        if(turn==PLAYER2) //is player2 quits
        {
            toClient2.writeInt(LOSE); //player2 loses
            toClient2.flush();
            toClient1.writeInt(WIN); //player1 wins
            toClient1.flush();
            winner=PLAYER1;
        }
        turn=0;
        restart(); //waiting for restarting the game
    }
    
    /**
        * restart the game
        */
    private void restart() throws IOException
    {
        if(fromClient1.readInt()==RESTART&&fromClient2.readInt()==RESTART)
        {   //if both of player1 and player2 type the "play" button, the game restarts
            initial(); //initialize the whole things
            if(winner==PLAYER1) //switch the order of players
            {   //if player1 wins, player2 will starts firstly
                toClient1.writeInt(PLAYER2);
                toClient2.writeInt(PLAYER1);
            }
            else
            {   //if player2 wins, player1 will starts early
                toClient1.writeInt(PLAYER1);
                toClient2.writeInt(PLAYER2);
            }
        }
    }
}