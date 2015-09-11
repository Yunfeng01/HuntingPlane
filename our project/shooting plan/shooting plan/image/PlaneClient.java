import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.net.*;

public class PlaneClient extends JApplet implements Runnable, PlaneConst 
{
    private static JFrame logoFrame;
    private boolean myTurn=false;
    private int player;
    private int numOfPlane;
    private Cell[][] smallCell;
    private int direction=NORTH;
    private JPanel menue;
    private JPanel planePanel;
    private JPanel controlPanel;
    private JLabel resultLabel;
    private JLabel titleLabel;
    // Create and initialize a title label
    private JLabel jlblTitle = new JLabel();
   
    // Create and initialize a status label
    private JLabel jlblStatus = new JLabel();
    private boolean continueToPlay=true;
    private Cell[][] cell =  new Cell[TABLE_H][TABLE_W];
    private int[][] table = new int[TABLE_H][TABLE_W];
    private DataInputStream fromServer;
    private DataOutputStream toServer;
      
    // Wait for the player to mark a cell
    private boolean waiting = true;

    // Indicate if it runs as application
    private boolean isStandAlone = false;

    // Host name or ip
    private String host = "localhost";
        
    private void makePanel(JPanel p,
                               GridBagLayout gridbag,
                               GridBagConstraints c) 
    {
        gridbag.setConstraints(p, c);
    }
    public void init() 
    {
        smallCell=new Cell[SMALL_H][SMALL_W];
        // Panel p to hold cells
        JPanel ourSky = new JPanel();
        createTable(ourSky);
        JPanel theirSky = new JPanel();
        createTable(theirSky);
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
        makePanel(ourSky,gridbag,c);
        whole.add(ourSky);
        c.gridwidth = GridBagConstraints.RELATIVE;
        makePanel(theirSky,gridbag,c);
        whole.add(theirSky);
        //c.gridwidth = 3;
        //c.gridheight = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        makePanel(menue,gridbag,c);
        whole.add(menue);
        //c.gridwidth = 12;
        //c.gridheight = 1;
        c.weightx = 0.0; 
        c.weighty = 0.0;   
        JPanel message=new JPanel();
        makePanel(message,gridbag,c);
        whole.add(message);
        //whole.setLayout(new GridLayout(1,3));
        //~ whole.add(ourSky);
        //~ whole.add(theirSky);
        //~ whole.add(menue);
        whole.setBackground(Color.white);
        add(whole,BorderLayout.CENTER);
        // Connect to the server
        connectToServer();
    }
    private void rotate(int direct)
    {
        direction+=direct;
        if(direction<NORTH)
            direction+=4;
        if(direction>EAST)
            direction-=4;
        smallPlaneInit();
    }      
    private void smallPlaneInit()
    {
        planePanel=new JPanel(new GridLayout(SMALL_H,SMALL_W));
        System.out.println(direction);
        int headX=0,headY=2;
        if(direction==NORTH)
        {
            headX=0;
            headY=2;
        }
        if(direction==WEST)
        {
            headX=2;
            headY=0;
        }
        if(direction==SOUTH)
        {
            headX=4;
            headY=2;
        }
        if(direction==EAST)
        {
            headX=2;
            headY=4;
        }
        PlaneModel1 plane1=new PlaneModel1(headX,headY,direction);
        //smallCell=new Cell[SMALL_H][SMALL_W];
        plane1.addPlane();
        for (int i = 0; i < SMALL_W; i++)
            for (int j = 0; j < SMALL_H; j++)
                planePanel.add(smallCell[i][j]);
    }
    public class PlaneModel1
    {
        private Point h;
        private Point b1;
        private Point l1;
        private Point l2;
        private Point r2;
        private Point r1;
        private Point b2;
        private Point t2;
        private Point t1;
        private Point t3;
                  
        public PlaneModel1(int x,int y,int direct)
        {
            if(direct==NORTH)
            {
                h=new Point(x,y);
                b1=new Point(x+1,y);
                l1=new Point(x+1,y-2);
                l2=new Point(x+1,y-1);
                r2=new Point(x+1,y+1);
                r1=new Point(x+1,y+2);
                b2=new Point(x+2,y);
                t2=new Point(x+3,y-1);
                t1=new Point(x+3,y);
                t3=new Point(x+3,y+1);
            }
            if(direct==WEST)
            {
                h=new Point(x,y);
                b1=new Point(x,y+1);
                l1=new Point(x+2,y+1);
                l2=new Point(x+1,y+1);
                r2=new Point(x-1,y+1);
                r1=new Point(x-2,y+1);
                b2=new Point(x,y+2);
                t2=new Point(x+1,y+3);
                t1=new Point(x,y+3);
                t3=new Point(x-1,y+3);
            }
            if(direct==SOUTH)
            {
                h=new Point(x,y);
                b1=new Point(x-1,y);
                l1=new Point(x-1,y+2);
                l2=new Point(x-1,y+1);
                r2=new Point(x-1,y-1);
                r1=new Point(x-1,y-2);
                b2=new Point(x-2,y);
                t2=new Point(x-3,y+1);
                t1=new Point(x-3,y);
                t3=new Point(x-3,y-1);
            }
            if(direct==EAST)
            {
                h=new Point(x,y);
                b1=new Point(x,y-1);
                l1=new Point(x-2,y-1);
                l2=new Point(x-1,y-1);
                r2=new Point(x+1,y-1);
                r1=new Point(x+2,y-1);
                b2=new Point(x,y-2);
                t2=new Point(x-1,y-3);
                t1=new Point(x,y-3);
                t3=new Point(x+1,y-3);
            }
        }
        public void addPlane()
        {
            for (int i = 0; i < SMALL_W; i++)
                for (int j = 0; j < SMALL_H; j++)
                    smallCell[i][j] = new Cell("image/cloud.jpg",direction);
            smallCell[(int)h.getX()][(int)h.getY()] = new Cell("image/h.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)h.getX()][(int)h.getY()].repaint();
            smallCell[(int)b1.getX()][(int)b1.getY()] = new Cell("image/b1.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)l1.getX()][(int)l1.getY()] = new Cell("image/l2.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)l2.getX()][(int)l2.getY()] = new Cell("image/l1.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)r2.getX()][(int)r2.getY()] = new Cell("image/r1.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)r1.getX()][(int)r1.getY()] = new Cell("image/r2.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)b2.getX()][(int)b2.getY()] = new Cell("image/b2.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)t2.getX()][(int)t2.getY()] = new Cell("image/t2.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)t1.getX()][(int)t1.getY()] = new Cell("image/t1.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
            smallCell[(int)t3.getX()][(int)t3.getY()] = new Cell("image/t3.jpg",direction);
            System.out.println((int)h.getX()+" "+(int)h.getY());
        }
    }
    private void makeMenue()
    {
        //menue=new JPanel();
        menue.setLayout(new GridLayout(2,1));
        planePanel=new JPanel(new GridLayout(SMALL_H,SMALL_W));
        
        smallPlaneInit();
        System.out.println("here");
        //~ for (int i = 0; i < SMALL_W; i++)
            //~ for (int j = 0; j < SMALL_H; j++)
                //~ planePanel.add(smallCell[i][j]);
        class LeftListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                rotate(LEFT);
                //~ init();
                //~ planePanel=new JPanel();
                //makeMenue();
                //~ smallPlaneInit();
                //~ planePanel=new JPanel();
                //~ planePanel=new JPanel(new GridLayout(SMALL_H,SMALL_W));
                //~ for (int i = 0; i < SMALL_W; i++)
                    //~ for (int j = 0; j < SMALL_H; j++)
                //~ planePanel.add(smallCell[i][j]);
                //~ menue.add(planePanel);
                //~ menue.add(controlPanel);  
            }            
        }
        class RightListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                rotate(RIGHT);
                //~ init();
                //~ planePanel=new JPanel();
                //makeMenue();
                //~ smallPlaneInit();
                //~ planePanel=new JPanel();
                //~ planePanel=new JPanel(new GridLayout(SMALL_H,SMALL_W));
                //~ for (int i = 0; i < SMALL_W; i++)
                    //~ for (int j = 0; j < SMALL_H; j++)
                //~ planePanel.add(smallCell[i][j]);
                //~ menue.add(planePanel);
                //~ menue.add(controlPanel);  
            }            
        }
        ActionListener lListener = new LeftListener();
        ActionListener rListener = new RightListener();
        
        controlPanel=new JPanel(new GridLayout(4,1));
        JPanel rotatePanel=new JPanel(new GridLayout(1,2));
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
        
        rotatePanel.add(leftPanel);
        rotatePanel.add(rightPanel);
         
        JButton cancel=new JButton();
        cancel.setIcon(new ImageIcon("image/cancel.jpg"));
        JButton start=new JButton();
        start.setIcon(new ImageIcon("image/start.jpg"));
        JButton plan=new JButton();
        //plan.setBackground(Color.white);
        plan.setIcon(new ImageIcon("image/plan.jpg"));
        
        
        controlPanel.add(rotatePanel);   
        controlPanel.add(cancel);       
        controlPanel.add(start);
        controlPanel.add(plan);
        menue.add(planePanel);
        menue.add(controlPanel);  
    }
    private void createTable(JPanel p)
    {
        p.setLayout(new GridLayout(TABLE_H, TABLE_W));
        for (int i = 0; i < TABLE_H; i++)
            for (int j = 0; j < TABLE_W; j++)
                p.add(cell[i][j] = new Cell("image/cloud.jpg",direction));
        p.setBackground(Color.white);
        p.setBorder(new LineBorder(Color.white, 1));
        //add(p, BorderLayout.CENTER);
        //~ jlblTitle.setHorizontalAlignment(JLabel.CENTER);
        //~ jlblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        //~ jlblTitle.setBorder(new LineBorder(Color.pink, 1));
        //~ jlblStatus.setBorder(new LineBorder(Color.pink, 1));
        //~ add(jlblTitle, BorderLayout.NORTH);
        //~ add(jlblStatus, BorderLayout.SOUTH);
    }
   private void connectToServer() 
   {
      try 
      {
         // Create a socket to connect to the server
         Socket socket;
         if (isStandAlone)
           socket = new Socket(host, PORT);
         else
           socket = new Socket(getCodeBase().getHost(), PORT);
   
         // Create an input stream to receive data from the server
         fromServer = new DataInputStream(socket.getInputStream());
   
         // Create an output stream to send data to the server
         toServer = new DataOutputStream(socket.getOutputStream());
      }
      catch (Exception ex) 
      {
         System.err.println(ex);
      }

       // Control the game on a separate thread
      Thread thread = new Thread(this);
      thread.start();
   }
   public void run() 
   {
      try 
      {
         // Get notification from the server
         int player = fromServer.readInt();
   
         // Am I player 1 or 2?
         //~ if (player == PLAYER1) 
         //~ {
            //~ myToken = 'X';
            //~ otherToken = 'O';
            //~ jlblTitle.setText("Player 1 with token 'X'");
            //~ jlblStatus.setText("Waiting for player 2 to join");
   
           //~ // Receive startup notification from the server
            //~ fromServer.readInt(); // Whatever read is ignored
   
           //~ // The other player has joined
            //~ jlblStatus.setText("Player 2 has joined. I start first");
   
           //~ // It is my turn
            //~ myTurn = true;
         //~ }
         //~ else if (player == PLAYER2) 
         //~ {
            //~ myToken = 'O';
            //~ otherToken = 'X';
            //~ jlblTitle.setText("Player 2 with token 'O'");
            //~ jlblStatus.setText("Waiting for player 1 to move");
         //~ }
   
         // Continue to play
         while (continueToPlay) 
         {
            if (player == PLAYER1)
            {
               waitForPlayerAction(); // Wait for player 1 to move
               //sendMove(); // Send the move to the server
               //receiveInfoFromServer(); // Receive info from the server
            }
            else if (player == PLAYER2) 
            {
               //receiveInfoFromServer(); // Receive info from the server
               waitForPlayerAction(); // Wait for player 2 to move
               //sendMove(); // Send player 2's move to the server
            }
         }
      }
      catch (Exception ex) {}
   }
   /** Wait for the player to mark a cell */
   private void waitForPlayerAction() throws InterruptedException 
   {
      while (waiting)
      {
         Thread.sleep(100);
      }
      waiting = true;
   }
   public static void main(final String[] args) 
   {
       logoFrame = new JFrame("Plane Client");
       JPanel logoPanel = new JPanel();
       final JButton newGame = new JButton("Start");
       class NewGameListener implements ActionListener
       {
           public void actionPerformed(ActionEvent event)
           {
               gameFrame(args);
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
   private static void gameFrame(String[] args)
   {
        // Create a frame
        JFrame frame = new JFrame("Plane Client");
              
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
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
   }
   //~ public class Cell extends JButton
   //~ {
       //~ public Cell(String image) 
       //~ {
           //~ setIcon(new ImageIcon(image));
           //~ setBackground(new Color(166,222,238));
       //~ }
   //~ }
    public class Cell extends JPanel 
    {
        Image image;  
        int direct=NORTH;
        public Cell(String photo,int d) 
        {
            super();       
            image = Toolkit.getDefaultToolkit().getImage(photo);  
            direct=d;
            //~ JLabel label=new JLabel(new ImageIcon(image));
            //~ setLayout(new BorderLayout());
            //~ add(label,BorderLayout.CENTER);
            //~ setBackground(new Color(166,222,238));
            //~ setBorder(new LineBorder(new Color(166,222,238), 1)); // Set cell's border
            //addMouseListener(new ClickListener());  // Register listener
        }
        public void paintComponent(Graphics g)
        {        
            Graphics2D g2=(Graphics2D)g;
            if(direct==NORTH)
            {
                g2.translate(0,0);     
                g2.rotate(Math.toRadians(0));     
            }
            if(direct==WEST)
            {
                g2.translate(0,50);     
                g2.rotate(Math.toRadians(270));  
            }     
            if(direct==SOUTH)
            {
                g2.translate(50,50);     
                g2.rotate(Math.toRadians(180));  
            } 
            if(direct==EAST)
            {
                g2.translate(50,0);     
                g2.rotate(Math.toRadians(90));  
            }             
            g2.drawImage(image,0,0,50,50, this);   
        }
    }
}