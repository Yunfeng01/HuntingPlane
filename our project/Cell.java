import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.net.*;
public class Cell extends JPanel implements PlaneConst 
{
        private Image image;
        private String p;        
        private int direct=NORTH;
        private JLabel label;
        private int row;
        private int column;
            
        public Cell(String photo, int aRow, int aColumn)
        {
            setPreferredSize(new Dimension(50,50)); 
            setMinimumSize(new Dimension(50,50)); 
            setMaximumSize(new Dimension(50,50)); 
            p=photo;
            label=new JLabel();
            row=aRow;
            column= aColumn;
            
        }
        public String getName()
        {
            return p;
        }
        public Cell(String photo) 
        {
            super();       
            image = Toolkit.getDefaultToolkit().getImage(photo);  
            setPreferredSize(new Dimension(50,50)); 
            setMinimumSize(new Dimension(50,50)); 
            setMaximumSize(new Dimension(50,50)); 
            p=photo;
            label=new JLabel();
            //~ JLabel label=new JLabel(new ImageIcon(p));
            //~ setLayout(new BorderLayout());
            //~ add(label,BorderLayout.CENTER);
            //~ setBackground(new Color(166,222,238));
            //~ setBorder(new LineBorder(new Color(166,222,238), 1)); // Set cell's border       
            //addMouseListener(new ClickListener());  // Register listener
        }
        public void set(String photo)
        {
            p=photo;
            repaint();
        }
        public void paintComponent(Graphics g)
        {
            label.setIcon(new ImageIcon(p));
            setLayout(new BorderLayout());
            add(label,BorderLayout.CENTER);
            setBackground(new Color(166,222,238));
            setBorder(new LineBorder(new Color(166,222,238), 1)); // Set cell's border            
        }
        public int getRow()
        {
            return row;
        }
        public int getColumn()
        {
            return column;
        }
        
}