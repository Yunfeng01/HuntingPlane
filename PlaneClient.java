import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.*;

public class PlaneClient extends JApplet implements Runnable, PlaneConst 
{
    private JFrame choice;
    private String result;
    private String photoName;
    private static JFrame logoFrame;
    private static JFrame menuFrame;
    private boolean myTurn=false;
    private int playingPlayer;
    private int selfPlayer;
    private int numOfPlane;
    private static int page=0;
    private PlaneModel1 planePanel=new PlaneModel1();
    //~ private int direction=NORTH;
    private JPanel menue;
    //~ private JPanel planePanel;
    private JPanel controlPanel;
    private JLabel resultLabel;
    private JLabel titleLabel;
    private static JFrame frame;
    // Create and initialize a title label
    private JLabel jlblTitle = new JLabel();
   
    // Create and initialize a status label
    private JLabel jlblStatus = new JLabel();
    private boolean continueToPlay=true;
    private Cell[][] ourCell =  new Cell[TABLE_H][TABLE_W];
    private Cell[][] theirCell =  new Cell[TABLE_H][TABLE_W];
    private int[][] ourTable = new int[TABLE_H][TABLE_W];
    private Cell[][] theirLocat =  new Cell[TABLE_H][TABLE_W];
    private boolean online=false;
    private boolean finish = true;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
      
    // Wait for the player to mark a cell
    private boolean waiting = true;

    // Indicate if it runs as application
    private boolean isStandAlone = false;

    // Host name or ip
    private String host ="localhost";//"192.168.1.104";
        
    private void makePanel(JPanel p,
                               GridBagLayout gridbag,
                               GridBagConstraints c) 
    {
        gridbag.setConstraints(p, c);
    }
    public void init() 
    {
        for (int i = 0; i < TABLE_H; i++)
            for (int j = 0; j < TABLE_W; j++)
                ourTable[i][j] = CLOUD;
        numOfPlane=0;    
        JPanel ourSky = new JPanel();
        createOurTable(ourSky);
        JPanel theirSky = new JPanel();
        createTheirTable(theirSky);
        menue=new JPanel();
        makeMenue();
        // Place the panel and the labels to the applet
        JPanel whole=new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        whole.setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        //c.gridwidth = 6;
        //c.gridheight = 6;
        makePanel(theirSky,gridbag,c);
        whole.add(theirSky);
        
        c.gridwidth = GridBagConstraints.RELATIVE;
        makePanel(ourSky,gridbag,c);
        whole.add(ourSky);
        //c.gridwidth = 3;
        //c.gridheight = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        makePanel(menue,gridbag,c);
        whole.add(menue);
        //~ //c.gridwidth = 12;
        //~ //c.gridheight = 1;
        c.weightx = 0.0; 
        c.weighty = 0.0;   
        //~ JPanel message=new JPanel();
        //~ message.setBackground(Color.BLACK);
        //~ makePanel(message,gridbag,c);
        //~ whole.add(message);
        //~ //whole.setLayout(new GridLayout(1,3));
        whole.add(ourSky);
        whole.add(theirSky);
        whole.add(menue);
        whole.setBackground(Color.BLACK);
        add(whole,BorderLayout.CENTER);
        
        jlblTitle.setHorizontalAlignment(JLabel.CENTER);
        jlblTitle.setFont(new Font("Broadway", Font.BOLD, 32));
        jlblTitle.setBorder(new LineBorder(Color.pink, 5));
        //~ jlblTitle.setIcon(new ImageIcon("image/cloud.jpg"));
        jlblStatus.setHorizontalAlignment(JLabel.CENTER);
        jlblStatus.setFont(new Font("Broadway", Font.BOLD, 32));
        jlblStatus.setBorder(new LineBorder(Color.pink, 5));
        jlblStatus.setBackground(Color.YELLOW);
        add(jlblTitle, BorderLayout.NORTH);
        add(jlblStatus, BorderLayout.SOUTH);
        
        setBackground(Color.white);
        // Connect to the server
        connectToServer();
    }
    private void makeMenue()
    {
        planePanel=new PlaneModel1();
        //~ final PlaneModel1 planePanel=new PlaneModel1();
        class LeftListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                planePanel.rotate(LEFT);
                planePanel.repaint();
            }            
        }
        class RightListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                planePanel.rotate(RIGHT);
                planePanel.repaint();
            }            
        }
        ActionListener lListener = new LeftListener();
        ActionListener rListener = new RightListener();
        
        controlPanel=new JPanel(new GridLayout(4,1));
        JPanel rotatePanel=new JPanel(new GridLayout(1,3));
        JButton right=new JButton();
        right.setIcon(new ImageIcon("image/right.jpg"));
        right.addActionListener(rListener);
        JPanel rightPanel=new JPanel();
        rightPanel.add(right);
        rightPanel.setBackground(Color.white);
        
        JButton left=new JButton();
        left.setIcon(new ImageIcon("image/left.jpg"));
        left.addActionListener(lListener);
        JPanel leftPanel=new JPanel();
        leftPanel.add(left);
        leftPanel.setBackground(Color.white);
        
        //~ rotatePanel.add(leftPanel);
        //~ rotatePanel.add(rightPanel);
         
        JButton cancel=new JButton();
        cancel.setIcon(new ImageIcon("image/cancel.jpg"));
        JPanel cPanel=new JPanel();
        class CancelListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                numOfPlane=0;
                for (int i = 0; i < TABLE_H; i++)
                    for (int j = 0; j < TABLE_W; j++)
                        ourTable[i][j] = CLOUD;
                for (int i = 0; i < TABLE_H; i++)
                {
                    for (int j = 0; j < TABLE_W; j++)
                    {
                        if(ourTable[i][j] == CLOUD)
                            ourCell[i][j].set("image/cloud.jpg");  
                    }
                } 
            }            
        }
        ActionListener cListener = new CancelListener();
        cancel.addActionListener(cListener);
        //~ cPanel.add(cancel);
        //~ cPanel.setBackground(Color.white);
        
        rotatePanel.add(left);
        rotatePanel.add(cancel);
        rotatePanel.add(right);
        
        JButton start=new JButton();
        start.setIcon(new ImageIcon("image/start.jpg"));
        class StartListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    if(numOfPlane==TOTAL)
                    {
                        //online=true;
                        toServer.writeInt(START);
                        toServer.flush();
                        for (int i = 0; i < TABLE_H; i++)
                            for (int j = 0; j < TABLE_W; j++)
                            {
                                toServer.writeInt(ourTable[i][j]);
                                toServer.flush();
                            }
                        jlblStatus.setText("Waiting for your enemy...");
                    }
                    else
                        jlblStatus.setText("You have not located your planes");
                }
                catch (Exception ex) 
                {
                    jlblStatus.setText(ex.getMessage());
                }
            }            
        }
        ActionListener sListener = new StartListener();
        start.addActionListener(sListener);
        
        

        final JFrame myPlan= new JFrame("Hunting Plan");
        myPlan.setBackground(Color.white);
    
        JPanel planPanel= new JPanel();
        planPanel.setPreferredSize(new Dimension(500,500)); 
        planPanel.setMinimumSize(new Dimension(500,500)); 
        planPanel.setMaximumSize(new Dimension(500,500));
               
        planPanel.setLayout(new GridLayout(TABLE_H, TABLE_W));
        for (int i = 0; i < TABLE_H; i++)
            for (int j = 0; j < TABLE_W; j++)
                planPanel.add(theirLocat[i][j] = new Cell("image/cloud.jpg",i,j));
        for (int i = 0; i < TABLE_H; i++)
            for (int j = 0; j < TABLE_W; j++)
                theirLocat[i][j].set(theirCell[i][j].getName());
        planPanel.setBackground(Color.white);
        planPanel.setBorder(new LineBorder(Color.white, 1));
               
               //~ //createTheirTable(planPanel);
               
        JPanel forPlan= new JPanel();
        forPlan.add(planPanel);
        forPlan.setBackground(Color.white);
               
        JPanel buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        photoName="image/"+BODY+".jpg";
        JButton head= new JButton("HEAD");
        class HeadListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                photoName="image/head.jpg";
            }            
        }   
        ActionListener hListener = new HeadListener();
        head.addActionListener(hListener);
        
        JButton body= new JButton("BODY");
        class BodyListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                photoName="image/"+BODY+".jpg";
            }            
        }   
        ActionListener bListener = new BodyListener();
        body.addActionListener(bListener);
        JButton clear= new JButton("CLEAR");
        class ClearListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                for (int r = 0; r < TABLE_H; r++)
                for (int  c= 0; c < TABLE_W; c++)
                theirLocat[r][c].set("image/cloud.jpg");   
            }            
        }   
        ActionListener clearListener = new ClearListener();
        clear.addActionListener(clearListener);
        
        buttonPanel.add(head);
        buttonPanel.add(body);
        buttonPanel.add(clear);
        buttonPanel.setPreferredSize(new Dimension(500,50)); 
        buttonPanel.setMinimumSize(new Dimension(500,50)); 
        buttonPanel.setMaximumSize(new Dimension(500,50)); 
               
        JPanel button = new JPanel();
               
        button.add(buttonPanel);
        button.setBackground(Color.white);
        myPlan.add(forPlan);
        myPlan.add(button, BorderLayout.SOUTH);
        myPlan.setSize(510, 605);
        myPlan.setVisible(false); 
        class MouseClickListener implements MouseListener
        { 
            public void mouseEntered(MouseEvent event){}
            public void mouseExited(MouseEvent event){}
            public void mousePressed(MouseEvent event){}
            public void mouseReleased(MouseEvent event){}
            public void mouseClicked(MouseEvent event)
            {
                Cell target= (Cell)event.getComponent();
                String name= target.getName();
                if(name.equals("image/cloud.jpg"))
                    target.set(photoName); 
                else
                    target.set("image/cloud.jpg");                         
            }

        }
        MouseClickListener listener = new MouseClickListener();
        for (int r = 0; r < TABLE_H; r++)
            for (int  c= 0; c < TABLE_W; c++)
                theirLocat[r][c].addMouseListener(listener);   
        
        JButton plan=new JButton();
        //plan.setBackground(Color.white);
        plan.setIcon(new ImageIcon("image/plan.jpg"));
        class PlanListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                myPlan.setVisible(true); 
            }                  
        }
        ActionListener pListener = new PlanListener();
        plan.addActionListener(pListener);
        
        JButton quit=new JButton();
        quit.setIcon(new ImageIcon("image/quit.jpg"));
        class QuitListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    if(!finish)
                        toServer.writeInt(QUIT);//send "QUIT" message to server
                }
                catch(IOException e)
                {
                    jlblStatus.setText("Here is a network error1\n");
                }
            }            
        }
        ActionListener qlistener = new QuitListener();
        quit.addActionListener(qlistener);
        controlPanel.add(rotatePanel);   
        controlPanel.add(start);
        controlPanel.add(plan);
        controlPanel.add(quit);  
        controlPanel.setBackground(Color.BLACK);
        //~ controlPanel.setBorder(new LineBorder(new Color(0,0,0), 1));
        
        menue.setPreferredSize(new Dimension(260,520)); 
        menue.setMinimumSize(new Dimension(260,520)); 
        menue.setMaximumSize(new Dimension(260,520)); 
        menue.setLayout(new GridLayout(2,1));
        menue.add(planePanel);
        menue.add(controlPanel);  
        menue.setBorder(new LineBorder(new Color(0,0,0), 1));
    }
    private void createTheirTable(JPanel p)
    {
        class MouseFightListener implements MouseListener
        {  
            public void mousePressed(MouseEvent event){}
            public void mouseReleased(MouseEvent event){}
            public void mouseEntered(MouseEvent event){}
            public void mouseExited(MouseEvent event){}
            public void mouseClicked(MouseEvent event)
            {   
                if(finish==false&&myTurn())
                {
                    Cell target= (Cell)event.getComponent();
                    String name= target.getName();
                    //target.set("image/"+BODY+".jpg");
                    try
                    {
                        toServer.writeInt(FIGHT);
                        toServer.writeInt(target.getRow());
                        toServer.writeInt(target.getColumn());
                        toServer.flush();
                    }
                    catch(Exception e){}
                }
            }
        }
        MouseFightListener listener = new MouseFightListener();
        JPanel title=new JPanel();
        JLabel words=new JLabel();
        words.setIcon(new ImageIcon("image/theirSky.jpg"));
        title.add(words);
        title.setBackground(Color.white);
        JPanel table=new JPanel();
        p.setLayout(new BorderLayout());
        table.setLayout(new GridLayout(TABLE_H, TABLE_W));
        for (int i = 0; i < TABLE_H; i++)
            for (int j = 0; j < TABLE_W; j++)
            {
                table.add(theirCell[i][j] = new Cell("image/cloud.jpg",i,j));
                theirCell[i][j].addMouseListener(listener);
            }
        table.setBackground(Color.white);
        table.setBorder(new MatteBorder(new ImageIcon("image/fire.jpg")));
        table.setPreferredSize(new Dimension(500,500)); 
        table.setMinimumSize(new Dimension(500,500)); 
        table.setMaximumSize(new Dimension(500,500)); 
        //table.setBorder(new LineBorder(Color.white, 1));
        p.add(title,BorderLayout.NORTH);
        p.add(table,BorderLayout.CENTER);
        
    }
    private void createOurTable(JPanel p)
    {
       
        class MouseEnteredListener implements MouseListener
        {  
            public void mousePressed(MouseEvent event){}
            public void mouseReleased(MouseEvent event){}
            public void mouseClicked(MouseEvent event)
            {
                try
                {
                    if(numOfPlane == TOTAL)
                        throw new Exception("You cannot locate more planes");
                    Cell target= (Cell)event.getComponent();
                    int direct= planePanel.getDirect();
                    Point[] locat=planePanel.location(target.getRow(), target.getColumn(),direct);
                    
                    for(int i=0;i<locat.length;i++)
                    {
                        if((int)locat[i].getX()<0||(int)locat[i].getY()<0
                            ||(int)locat[i].getX()>=locat.length||(int)locat[i].getY()>=locat.length
                            ||ourTable[(int)locat[i].getX()][(int)locat[i].getY()]!=CLOUD)
                            throw new Exception("You may not locate your plane there!");
                    }
                    
                    ourTable[target.getRow()][target.getColumn()]=HEAD;
                    for(int i=1;i<locat.length;i++)
                        ourTable[(int)locat[i].getX()][(int)locat[i].getY()]=BODY;
                    for (int i = 0; i < TABLE_H; i++)
                    {
                        for (int j = 0; j < TABLE_W; j++)
                        {
                            System.out.print(ourTable[i][j]+" ");
                        }
                        System.out.print("\n");
                    }
                    System.out.print("\n");
                    numOfPlane++;
                    if(numOfPlane == 3)
                        JOptionPane.showMessageDialog(null,"Please enter START button to start your game!");    
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(null,e.getMessage());
                }
                
            }
            public void mouseEntered(MouseEvent event) 
            {
                Cell target= (Cell)event.getComponent();
                
                //~ System.out.println("Pass to server: Row:"+target.getRow()+"\nColumn:"+target.getColumn());
                
                //target.set("image/ball.jpg");
                int direct= planePanel.getDirect();
                //~ System.out.println("Direction:" + direct);
                Point[] locat=planePanel.location(target.getRow(), target.getColumn(),direct);
                for(int i=0;i<locat.length;i++)
                {
                    if((int)locat[i].getX()>=0&&(int)locat[i].getY()>=0
                        &&(int)locat[i].getX()<locat.length&&(int)locat[i].getY()<locat.length
                        &&ourTable[(int)locat[i].getX()][(int)locat[i].getY()]==CLOUD)
                        ourCell[(int)locat[i].getX()][(int)locat[i].getY()].set("image/"+direct+planePanel.IMAGES[i]);
                }
            }
            public void mouseExited(MouseEvent event)
            {
                //Cell target= (Cell)event.getComponent();
                //target.set("image/cloud.jpg"); 
                for (int i = 0; i < TABLE_H; i++)
                {
                    for (int j = 0; j < TABLE_W; j++)
                    {
                        if(ourTable[i][j] == CLOUD)
                            ourCell[i][j].set("image/cloud.jpg");  
                    }
                }                                
            }
        }
        MouseListener listener = new MouseEnteredListener();
        JPanel title=new JPanel();
        JLabel words=new JLabel();
        words.setIcon(new ImageIcon("image/ourSky.jpg"));
        title.add(words);
        title.setBackground(Color.white);
        JPanel table=new JPanel();
        table.setLayout(new GridLayout(TABLE_H, TABLE_W));
        for (int i = 0; i < TABLE_H; i++)
            for (int j = 0; j < TABLE_W; j++)
            {   
                table.add(ourCell[i][j] = new Cell("image/cloud.jpg",i,j));
                ourCell[i][j].addMouseListener(listener); 
            }
        table.setBackground(Color.white);
        //~ table.setBorder(new LineBorder(Color.BLUE, 3));
        table.setBorder(new MatteBorder(new ImageIcon("image/fire.jpg")));
        table.setPreferredSize(new Dimension(500,500)); 
        table.setMinimumSize(new Dimension(500,500)); 
        table.setMaximumSize(new Dimension(500,500)); 
        p.setLayout(new BorderLayout());
        p.add(title,BorderLayout.NORTH);
        p.add(table,BorderLayout.CENTER);
    }
        
    /**
        * override the start() in the applet
        */
    public void start()
    {
        Thread t=new Thread(this);
        t.start();
    }
    /**
        * connect to server
        */
    public void connectToServer()
    {
        try
        {
            // Create a socket to connect to the server
            Socket socket;
            if(isStandAlone)
                socket=new Socket(host,PORT);
            else
                socket=new Socket("127.0.0.1",PORT);
            // Create an input stream to receive data from the server
            fromServer=new DataInputStream(socket.getInputStream());
            // Create an output stream to send data to the server
            toServer=new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e)
        {
            jlblStatus.setText("Here is a network error2\n");
        }
    }
    /**
        * check whose turn it is
        * @return true if it is this player's turn.
                       false if itis not this player's turn
        */
    private boolean myTurn()
    {
       return (playingPlayer == selfPlayer); //compare current playing player and this player
    }
    /**
        * override the run() in Runnable
        */
    public void run()
    {
        try
        {
           // to deal with what the server sends
           // should be PLAYER1 or PLAYER2 from server
            selfPlayer = fromServer.readInt();
            if (selfPlayer == PLAYER1) 
                jlblTitle.setText("You are Player 1");
            if (selfPlayer == PLAYER2) 
                jlblTitle.setText("You are Player 2");
             //define the player does not finish
            while(true)
            {   //read command from server 
                int cmd = fromServer.readInt();
                System.out.println(cmd);
                if(cmd == WAIT)
                {
                    jlblStatus.setText("Waiting for your enemy...");
                    try
                    {
                        Thread.sleep(MOMENT); //wait for the server's decision
                    }
                    catch(InterruptedException e)
                    {
                        jlblStatus.setText("Here is a network error3\n");
                    }
                }
                if(cmd == START)
                {   
                    System.out.println("STart");
                    //get the information of current player
                    finish=false;
                    playingPlayer = fromServer.readInt();
                    if (myTurn()) //check whether it is my turn
                    {
                        jlblTitle.setText("Your Turn!");
                    }
                    else
                    {
                        jlblTitle.setText("Waiting Your enemy hunting...");
                    }
                }
                if(cmd == FIGHT)
                {
                    int row=fromServer.readInt();
                    int column=fromServer.readInt();
                    int part=fromServer.readInt();
                    //theirLocat[row][column].set(theirCell[row][column].getName());
                    showImage(row,column,part,cmd);
                    theirLocat[row][column].set(theirCell[row][column].getName());
                }
                if(cmd==SHOW)
                {
                    int row=fromServer.readInt();
                    int column=fromServer.readInt();
                    int part=fromServer.readInt();
                    showImage(row,column,part,cmd);
                }
                if(cmd==WIN) //if server tells you wins
                {
                    jlblTitle.setText("YOU WIN");
                    finish=true; //finish the game
                    createChoice();
                }
                if(cmd==LOSE) //if server tells you loses
                {
                    jlblTitle.setText("YOU LOSE");
                    finish=true; //finish the game
                    createChoice();
                }
                if (cmd == PLAYER1) 
                {
                    jlblTitle.setText("You are Player 1");
                    jlblStatus.setText("Please locate your planes");
                }
                if (cmd == PLAYER2) 
                {
                    jlblTitle.setText("You are Player 2");
                    jlblStatus.setText("Please locate your planes");
                }
            }
        }
        catch(Exception e)
        {
            jlblStatus.setText("Here is a network error4\n");
        }
    }
    public void createChoice()
    {
        choice= new JFrame();
        choice.setLayout(new GridLayout(2,1));
        JLabel infor= new JLabel();
        infor.setText("Do you want to try again?");
                    
        JPanel cButtons= new JPanel();
        cButtons.setLayout(new GridLayout(1,2));
        JButton yes= new JButton("YES");
        cButtons.add(yes);
        JButton no= new JButton("NO");
        cButtons.add(no);
                    
                    
        class yesListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    toServer.writeInt(RESTART);
                    initilize();
                    choice.setVisible(false);
                }
                catch(IOException e){}
            }            
        }
        ActionListener yListener= new yesListener();
        yes.addActionListener(yListener);
                    
        class noListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                choice.setVisible(false);
                System.exit(0);
            }            
        }
        ActionListener nListener= new noListener();
        no.addActionListener(nListener);
                    
        choice.add(infor);
        choice.add(cButtons);
        choice.setSize(400, 200);
        choice.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        choice.setVisible(true);
    }
    
    
    /**
        * initilize everything in the game
        */
    private void initilize()
    {
        numOfPlane=0;
        finish=true;
        //~ jlblTitle.setText(selfPlayer
        for (int i = 0; i < TABLE_H; i++)
        {
            for (int j = 0; j < TABLE_W; j++)
            {
                ourTable[i][j] = CLOUD;
                ourCell[i][j].set("image/cloud.jpg");
                theirCell[i][j].set("image/cloud.jpg");                
            }
        }            
        jlblStatus.setText(null);
        //~ finish=false; //initialize the game is not finished
    }
    private void showImage(int row,int column,int part,int cmd)
    {
        if(cmd==FIGHT)
        {
            if(part==HEAD)
                theirCell[row][column].set("image/head.jpg");
            if(part==BODY)
                theirCell[row][column].set("image/"+BODY+".jpg");
            if(part==CLOUD)
                theirCell[row][column].set("image/miss.jpg");
        }
        if(cmd==SHOW)
        {
            if(part==HEAD)
                ourCell[row][column].set("image/head.jpg");
            if(part==BODY)
                ourCell[row][column].set("image/"+BODY+".jpg");
            if(part==CLOUD)
                ourCell[row][column].set("image/miss.jpg");
        }
    }
    public static void main(final String[] args) 
    {
        logoFrame = new JFrame("Plane Client");
        JPanel logoPanel = new JPanel();
        final JButton newGame = new JButton();
        newGame.setIcon(new ImageIcon("image/new.jpg"));
        newGame.setPreferredSize(new Dimension(300,80)); 
        newGame.setMinimumSize(new Dimension(300,80)); 
        newGame.setMaximumSize(new Dimension(300,80)); 
        class NewGameListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                menuFrame(args);
                logoFrame.setVisible(false);
            }
        }
        ActionListener listener = new NewGameListener();
        newGame.addActionListener(listener);
        JPanel bPanel = new JPanel();
        bPanel.add(newGame);
        logoPanel.setLayout(new BorderLayout());
        logoPanel.add(new JLabel(new ImageIcon("image/logo.jpg")),BorderLayout.NORTH);
        logoPanel.add(bPanel,BorderLayout.SOUTH);
        logoFrame.add(logoPanel);
        logoFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        logoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logoFrame.setVisible(true);
    }
    private static void menuFrame(final String[] args)
    {
        menuFrame=new JFrame("User Menus");
        JPanel menu=new JPanel();
        final JLabel menuL=new JLabel();
        menuL.setIcon(new ImageIcon("image/menu"+page+".jpg"));
        JButton pre=new JButton();
        JButton next=new JButton();
        pre.setIcon(new ImageIcon("image/previous.jpg"));
        next.setIcon(new ImageIcon("image/next.jpg"));
        
        class PreListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                if(page==0){}
                if(page>0)
                {
                    page--;
                    menuL.setIcon(new ImageIcon("image/menu"+page+".jpg"));
                }
            }
        }
        PreListener plistener = new PreListener();
        pre.addActionListener(plistener);
        System.out.println("page is"+page);
        class NextListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                if(page==NUMOFPAGE)
                {
                    gameFrame(args);
                    menuFrame.setVisible(false);
                }
                if(page<NUMOFPAGE)
                {
                    page++;
                    menuL.setIcon(new ImageIcon("image/menu"+page+".jpg"));
                }
            }
        }
        NextListener nlistener = new NextListener();
        next.addActionListener(nlistener);
        JPanel button=new JPanel();
        button.setLayout(new GridLayout(1,2));
        button.add(pre);
        button.add(next);
        menu.setLayout(new BorderLayout());
        menu.add(menuL,BorderLayout.CENTER);
        menu.add(button,BorderLayout.SOUTH);
        menuFrame.add(menu);
        menuFrame.setSize(FRAME_WIDTH+250, FRAME_HEIGHT+50);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setVisible(true);
    }
    private static void gameFrame(String[] args)
    {
        // Create a frame
        frame = new JFrame("Plane Client");
              
        // Create an instance of the applet
        PlaneClient applet = new PlaneClient();
        applet.isStandAlone = true;
        
        // Get host
        if (args.length == 1) applet.host = args[0];
        
        // Add the applet instance to the frame
        frame.getContentPane().add(applet, BorderLayout.CENTER);
        
        // Invoke init() and start()
        applet.init();
        applet.start();
        
        // Display the frame
        frame.setSize(FRAME_WIDTH+250, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}