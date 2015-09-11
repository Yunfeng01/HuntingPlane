import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.net.*;

public class RotateImage extends JPanel
{    // Declare an Image object for us to use.    
    private int a=1;
    Image image;        
    // Create a constructor method    
    public RotateImage(){       
        //~ super();       
    // Load an image to play with.       
        image = Toolkit.getDefaultToolkit().getImage("image/h.jpg");    
        //setMaximumSize(new Dimension(50, 50));
    
        setBackground(new Color(0,0,0));
        setBorder(new LineBorder(new Color(0,0,0), 1)); // Set cell's border
    }      
    public void setDegree(int d)
    {
        a+=d;
    }
    public void paintComponent(Graphics g)
    {        
        Graphics2D g2=(Graphics2D)g;
        //~ // Create a Java2D version of g.
        //~ if(degree==0)
        //~ {
            //~ g2.translate(0,0); 
            //~ // Translate the center of our coordinates.        
            //~ g2.rotate(Math.toRadians(degree));  
            //~ // Rotate the image by 1 radian.
        //~ }
        //~ if(degree==90)
        //~ {
            //~ g2.translate(50,0); 
            //~ // Translate the center of our coordinates.        
            //~ g2.rotate(Math.toRadians(degree));  
            //~ // Rotate the image by 1 radian.
        //~ }
        //~ if(degree==180)
        //~ {
            //~ g2.translate(50,50); 
            //~ // Translate the center of our coordinates.        
            //~ g2.rotate(Math.toRadians(degree));  
            //~ // Rotate the image by 1 radian.
        //~ }
        //~ if(degree==270)
        //~ {
            //~ g2.translate(0,50); 
            //~ // Translate the center of our coordinates.        
            //~ g2.rotate(Math.toRadians(degree));  
            //~ // Rotate the image by 1 radian.
        //~ }        
        //~ resize(new Dimension(50, 50));
        //~ g2.drawImage(image,0,0, 50,50, this);   
        JLabel label=new JLabel(new ImageIcon("image/b1.jpg"));
        setLayout(new BorderLayout());
        add(label,BorderLayout.CENTER);
    }    
    public static void main(String arg[])
    {     
        JFrame frame = new JFrame("RotateImage");      
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        frame.setSize(600,600);  
        final RotateImage panel = new RotateImage();    
        RotateImage panel1 = new RotateImage();  
        JButton b=new JButton("STart");
        class Listener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                panel.setDegree(1);    
                panel.repaint();
            }            
        }
        ActionListener listener = new Listener();
        b.addActionListener(listener);
        JPanel p=new JPanel(new GridLayout(3,1));p.add(panel1);
        p.add(panel);
        
        p.add(b);
        frame.add(p);  
        frame.setVisible(true);     
    }
}
