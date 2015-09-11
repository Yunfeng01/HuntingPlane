import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.net.*;
public class PlaneModel1 extends JPanel implements PlaneConst 
{
        private int direct=NORTH;
        private int x=0;
        private int y=0;
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
        public final String[] IMAGES={"h.jpg","b1.jpg","l2.jpg","l1.jpg","r1.jpg",
            "r2.jpg","b2.jpg","t2.jpg","t1.jpg","t3.jpg"};
        private Cell[][] smallCell=new Cell[SMALL_H][SMALL_W];
        public PlaneModel1()
        {
            super();
            for (int i = 0; i < SMALL_W; i++)
                for (int j = 0; j < SMALL_H; j++)
                    smallCell[i][j] = new Cell("image/cloud.jpg");
        }
        public void rotate(int d)
        {
            direct+=d;
            if(direct<NORTH)
                direct+=4;
            if(direct>EAST)
                direct-=4;
            //~ repaint();
        } 
        public int getDirect()
        {
            return direct;
        }            
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            //~ Graphics2D g2=(Graphics2D)g;
            setPreferredSize(new Dimension(255,255)); 
            setMinimumSize(new Dimension(255,255)); 
            setMaximumSize(new Dimension(255,255)); 
            if(direct==NORTH)
            {
                x=0;
                y=2;
            }
            if(direct==WEST)
            {
                x=2;
                y=0;
            }
            if(direct==SOUTH)
            {
                x=4;
                y=2;
            }
            if(direct==EAST)
            {
                x=2;
                y=4;
            }
            Point[] result= new Point[IMAGES.length];
            result=location(x,y,direct);
        
            for (int i = 0; i < SMALL_W; i++)
                for (int j = 0; j < SMALL_H; j++)
                    remove(smallCell[i][j]);

            for (int i = 0; i < SMALL_W; i++)
                for (int j = 0; j < SMALL_H; j++)
                    smallCell[i][j].set("image/cloud.jpg");
            for(int i=0;i<IMAGES.length;i++)
                smallCell[(int)result[i].getX()][(int)result[i].getY()].set("image/"+direct+IMAGES[i]);

            setLayout(new GridLayout(SMALL_H,SMALL_W));
            for (int i = 0; i < SMALL_W; i++)
                for (int j = 0; j < SMALL_H; j++)
                    add(smallCell[i][j]);
        }
    public Point[] location(int x, int y, int direct)
    {
        Point[] result= new Point[10];
        Point h=null;
        Point b1=null;
        Point l1=null;
        Point l2=null;
        Point r2=null;
        Point r1=null;
        Point b2=null;
        Point t2=null;
        Point t1=null;
        Point t3=null;

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
        
        result[0]=h;
        result[1]=b1;
        result[2]=l1;
        result[3]=l2;
        result[4]=r2;
        result[5]=r1;
        result[6]=b2;
        result[7]=t2;
        result[8]=t1;
        result[9]=t3;
        
        return result;        
    }
}