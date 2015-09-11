public interface PlaneConst
{
    int LEFT=1;
    int RIGHT=-1;
    
    int NORTH=0;
    int WEST=1;
    int SOUTH=2;
    int EAST=3;
    
    int CLOUD=0;
    int HEAD=1;
    int BODY=2;
    int NUMOFPAGE=1;
    //total number of plane user can locate in the table
    int TOTAL=3;
    /**
        * the port of the game server
        */
    int PORT=1181; 
    /**
        * the width of the frame
        */
    int FRAME_WIDTH = 1100;
    /**
        * the height of the frame
        */
    int FRAME_HEIGHT = 700; 
    /**
        * the length of the card table
        */
    int TABLE_H=10; 
    /**
        * the width of the card table
        */
    int TABLE_W=10; 
    /**
        * the length of the card table
        */
    int SMALL_H=5; 
    /**
        * the width of the card table
        */
    int SMALL_W=5; 
    /**
        * let user to wait a moment
        */
    int WAIT=-4;
    int SHOW=-17;  
    int FIGHT=-7;
    /**
        * cover two images 
        */
    int COVER=-5; 
    /**
        * keep the last two cards uncovered
        */
    int KEEP=-6; 
    /**
        * tells the player wins
        */
    int WIN=-8; 
    /**
        * tells the player loses
        */
    int LOSE=-9; 
    /**
        * one player wants to quit the game
        */
    int QUIT=-11;
    /**
        * label the player is PLAYER1
        */
    int PLAYER1=-1;
    /**
        * label the player is PLAYER2
        */
    int PLAYER2=-2;
    /**
        * tells the player can start
        */
    int START=-3;  
    int RESTART=-30;    
    /**
        * the time of a moment
        */
    int MOMENT=1000;
    /**
        * the maximum of the palne can be locate atb the table
        */
    int MAX=3;
}