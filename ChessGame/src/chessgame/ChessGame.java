/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgame;

import java.util.*;
import javax.swing.*;
public class ChessGame {
    //general comments regarding chessboard layout & how it works
    //NOTE: actual pieces will not be on other side of board
    //eg, if computer is playing as white, it will still appear that the computer is playing as black
    //(as it is moving the black pieces), but it is still playing as white.
    
    //how chess pieces can move:
    //King:The king can only move one square in any direction - up, down, to the sides, and diagonally.
    //Queen:Can move in any one straight direction - forward, backward, sideways, or diagonally - as far as possible as long as she does not move through any of her own pieces. 
    //Rook:The rook may move as far as it wants, but only forward, backward, and to the sides.
    //Bishop:The bishop may move as far as it wants, but only diagonally. Each bishop starts on one color (light or dark) and must always stay on that color.
    //Knight:Can move two squares in one direction, and then one more move at a 90 degree angle, just like the shape of an “L”. 
    //Pawn:Pawns can only move forward one square at a time, except for their very first move where they can move forward two squares. Pawns can only capture one square diagonally in front of them.
    
    //representing chess board, using array; easy to display, deploy, debug
    
    //A/a = king, K/k = knight
    
    //lowercase = black, uppercase = white
    
    //        0   1   2   3   4   5   6   7
    //0-8/0 {"r","k","b","q","a","b","k","r"},
    //8-16/1{"p","p","p","p","p","p","p","p"},
    //16-24/2{" "," "," "," "," "," "," "," "},
    //24-32/3{" "," "," "," "," "," "," "," "},
    //32-40/4{" "," "," "," "," "," "," "," "},
    //40-48/5{" "," "," "," "," "," "," "," "},
    //48-56/6{"P","P","P","P","P","P","P","P"},
    //56-64/7{"R","K","B","Q","A","B","K","R"}};
    
    //row-1 means that it is the row above the current row in question
        //for example, row-1 would mean going from row 56-64 to row 48-56
    static String chessBoard[][]={
        {"r","k","b","q","a","b","k","r"},
        {"p","p","p","p","p","p","p","p"},
        {" "," "," "," "," "," "," "," "},
        {" "," "," "," "," "," "," "," "},
        {" "," "," "," "," "," "," "," "},
        {" "," "," "," "," "," "," "," "},
        {"P","P","P","P","P","P","P","P"},
        {"R","K","B","Q","A","B","K","R"}};
     //Monitors king position in form of solid number, 0-63, 0 being first square on board and 63 last
        //good to know where king is when making moves so as to make sure king is not in check
    static int kingPositionCap, kingPositionLow;
     //set who is white/who is black, human or computer
    //1= humanAsWhite, 0 = humanAsBlack
    static int humanAsWhite=-1;//1=human as white, 0=human as black
        //need to search to a depth of 4, 4 = root
    static int globalDepth=4;
    
    public static void main(String[] args) {
                //scans through every space/square looking for a/A(king),gets king's position   //++,continue
        while (!"A".equals(chessBoard[kingPositionCap/8][kingPositionCap%8])) {kingPositionCap++;}//get King's location
        while (!"a".equals(chessBoard[kingPositionLow/8][kingPositionLow%8])) {kingPositionLow++;}//get king's location
        
         //strategy is to create an alpha-beta tree diagram wich returns
         //the best outcome

        //create the JFrame to graphically display the board, names it "Chess Game"
        JFrame f=new JFrame("Chess Game");
        //close the JFrame when you close the window
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setting the user interface class/where UI code will be
        //creating the UI
        ChessInterface ui=new ChessInterface();
        f.add(ui);
        //setting size of JFrame
        f.setSize(500, 500);
        //display the GUI in a window
        f.setVisible(true);
        
        //display possible moves in the form of x1, y1, x2, y2 & captured piece(p, k, b, etc), captured piece good for when you have to undo a move
        // x1, y1 = row 1 column 1(initial postiion), x2, y2 = row 2 column 2(position moved to)
        
        //print the possible moves
        System.out.println(possibleMoves());
        //who is playing what side/colour
        //asks user
        Object[] option={"Computer","Human"};
        //opens dialog to choose who is playing as white, null, question to user, title of dialog, option type available to user, question message icon, null, option as above(computer/human),option[1]=default option(human as white)
        humanAsWhite=JOptionPane.showOptionDialog(null, "Who should play as white?", "ChessGame Options", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        //if human is black(1 = human IS white, 0 = means human is black, computer is white
        if (humanAsWhite==0) {
            //times how long the move takes
            long startTime = System.currentTimeMillis();
            //depth(4), beta, alpha, move, player
            //set to numbers that rating can never reach
            //if human is black, computer is white, so computer makes first move
            makeMove(alphaBetaAlgo(globalDepth, 1000000, -1000000, "", 0));
            //times how long the move took(using the best move)
            long endTime = System.currentTimeMillis();
            //displays how long the move took in milliseconds
            System.out.println("That move took "+(endTime-startTime)+" milliseconds");
            //flip board so we are seeing it from right perspective, default position of board is that human is playing white
            //we need to flip the board so that the black pieces are on the right side if human is playing black
            //NOTE: actual pieces will not be on other side of board, eg, if computer is playing as white, it will still appear that the computer is playing as black(as it is moving the black pieces), but it is still playing as white.
            flipBoard();
            //repaint board after computer makes first move
            f.repaint();
        }
        //testing makeMove & undoMove
        makeMove("7655 ");
        undoMove("7655 ");
        //print board, go for 8 lines, each  line prints out a new line of the chess board array turned into a string notation
        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }
    //algorithim
    //searches tree and prunes(looks at moves & prunes worst(sort method)
    //string because it returns move(string) and score(int)
    //return in the form of 1234b and score
    //depth = , beta = , alpha = , move = move string, player = 0 or 1 needed as we must negate the score every 2nd turn
    public static String alphaBetaAlgo(int depth, int beta, int alpha, String move, int player) {
        //list of possible moves to run through each time
        String list=possibleMoves();
        //if we hit maximum depth or if there are no possible moves then stop searching & return the move, rating and flip the board/change player
        if (depth==0 || list.length()==0) {return move+(Rating.rating(list.length(),depth)*(player*2-1));}
        //sort the possible moves(best moves in order from best to worst)
        list = sortMoves(list);
        //decides whose turn it is/flips board
        //player is either 1 or 0
        player=1-player;
        //for each move in the list, i loops over, adding 5 each time, getting new list of 5
        for (int i=0;i<list.length();i+=5) {
            //move we want to make
            makeMove(list.substring(i,i+5));
            //flip board upside down/change player
            flipBoard();
            
            //recursion, string calls itself again until maximum depth is reached
            String returnString=alphaBetaAlgo(depth-1, beta, alpha, list.substring(i,i+5), player);
            int value=Integer.valueOf(returnString.substring(5));
            //need to flip board right side up
            flipBoard();
            undoMove(list.substring(i,i+5));
            if (player==0) {
                if (value<=beta) {beta=value; if (depth==globalDepth) {move=returnString.substring(0,5);}}
            } else {
                if (value>alpha) {alpha=value; if (depth==globalDepth) {move=returnString.substring(0,5);}}
            }
            if (alpha>=beta) {
                if (player==0) {return move+beta;} else {return move+alpha;}
            }
        }
        if (player==0) {return move+beta;} else {return move+alpha;}
    }
    
    //method that flips board upside down, no need to duplicate code for white/black, only board has to change
    //only want it to flip when necessary
    public static void flipBoard() {
       //store pieces to know what was overwritten 
        String temp;
        //goes through half of the pieces & swaps them with other pieces, lowercase to bottom, uppercase to top
        for (int i=0;i<32;i++) {
            //sends info from each square
            int row=i/8, col=i%8;
            //if character is uppercase, then change to lowercase
            if (Character.isUpperCase(chessBoard[row][col].charAt(0))) {
                temp=chessBoard[row][col].toLowerCase();
                //otherwise(if not uppercase) do opposite
            } else {
                temp=chessBoard[row][col].toUpperCase();
            }
            //if character at opposite corner(7-row,7-col) is uppercase, then change to lowercase
            if (Character.isUpperCase(chessBoard[7-row][7-col].charAt(0))) {
                chessBoard[row][col]=chessBoard[7-row][7-col].toLowerCase();
                //otherwise(if not uppercase) do opposite
            } else {
                chessBoard[row][col]=chessBoard[7-row][7-col].toUpperCase();
            }
            //swap sides, uppercase & lowercase
            chessBoard[7-row][7-col]=temp;
        }
        //when board flips, locations of king positions change
        //store king to know overwrite
        int kingTemp=kingPositionCap;
        //swap uppercase and lowercase kings
        kingPositionCap=63-kingPositionLow;
        kingPositionLow=63-kingTemp;
    }
     //method for making moves, takes in move string, actually makes move
    //String move is the string containing the move characters(row1,col1,row2,col2,captured piece, "P"(if applicable)
    public static void makeMove(String move) {
        //if not dealing with pawn promotion(P)
        if (move.charAt(4)!='P') {
            //x2 y2 changes to x1 y1
            //x2 = charat2(third character in string), y2 = charat3(fourth character), x1 = charat0(first character), y1 = charat1(second character)
            chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))]=chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))];
            //makes old position empty
            //x1,y1 = charat0, charat1, makes that position blank
            chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))]=" ";
            //if moving A to new location
            if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))])) {
                //8 times row plus horizontal position, gives position
                kingPositionCap=8*Character.getNumericValue(move.charAt(2))+Character.getNumericValue(move.charAt(3));
            }
            //if dealing with pawn promotion
        //col 1 = charat0, col 2 = charat1, captured piece = charat2, new piece(captured piece) = charat3, P
        } else {
            //already know the rows, 1 & 0
            //1 turns into empty space
            chessBoard[1][Character.getNumericValue(move.charAt(0))]=" ";
            //0 is the new location(whatever pawn will turn in to(Q,R,B,K)
            chessBoard[0][Character.getNumericValue(move.charAt(1))]=String.valueOf(move.charAt(3));
        }
    }
    
    //method for undoing moves, takes in move string, undos move
    //String move is the string containing the move characters(row1,col1,row2,col2,captured piece, "P"(if applicable)
    public static void undoMove(String move) {
        //if not dealing with pawn promotion(P)
        if (move.charAt(4)!='P') {
            //x1 y1 changes to x2 y2
            //x1 = charat0(first character in string), y1 = charat1(second character), x2 = charat2(third character), y2 = charat3(fourth character)
            chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))]=chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))];
            //put captured piece back in original position
            //x2,y2 = charat2, charat3
            //charat4 = captured piece
            chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))]=String.valueOf(move.charAt(4));
            //moving king back to original location
            if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))])) {
                //gets original position
                kingPositionCap=8*Character.getNumericValue(move.charAt(0))+Character.getNumericValue(move.charAt(1));
            }
            //if dealing with pawn promotion
         //col 1 = charat0, col 2 = charat1, captured piece = charat2, new piece(captured piece) = charat3, P
        } else {
            //already know the rows, 1 & 0
            //put pawn back in original position
            chessBoard[1][Character.getNumericValue(move.charAt(0))]="P";
            //put captured piece back
            chessBoard[0][Character.getNumericValue(move.charAt(1))]=String.valueOf(move.charAt(2));
        }
    }
    public static String possibleMoves() {
        //stores move
        String list="";
        //goes through each square
        for (int i=0; i<64; i++) {
            //sends info from each square
            switch (chessBoard[i/8][i%8]) {
                //if Pawn, then get possible moves for Pawn at location i(speciifying which pawn)
                //add moves to list
                case "P": list+=possibleP(i);
                //if not, do nothing
                    break;
                //if Rook, then get possible moves for Rook at location i(specifying which rook)
                //add moves to list
                case "R": list+=possibleR(i);
                //if not, then do nothing
                //so on for other pieces.
                    break;
                case "K": list+=possibleK(i);
                    break;
                case "B": list+=possibleB(i);
                    break;
                case "Q": list+=possibleQ(i);
                    break;
                case "A": list+=possibleA(i);
                    break;
            }
        }
        //return list of possible moves
        return list;
    }
    public static String possibleP(int i) {
        //list of possible pawn moves, oldPiece = piece that was in old position/before movement
        String list="", oldPiece;
        //repeating method from possibleMoves above
        int row=i/8, col=i%8;
        //goes from -1 to 1, allows you to go all directions pawn can go, checking all places pawn can go
        for (int j=-1; j<=1; j+=2) {
            //check all regular captures, upwards diagonally
            
            //try/catch = try method(if statement), if there is an error, do nothing, move on
            try {
                //i>=16 is to ensure that the move is not a pawn promotion/going past the top 2 lines
                // && if character at intended position is lower case
                //then can capture
                if (Character.isLowerCase(chessBoard[row-1][col+j].charAt(0)) && i>=16) {
                    //location of old piece/where pawn intends to move
                    oldPiece=chessBoard[row-1][col+j];
                    //make space where pawn was blank
                    chessBoard[row][col]=" ";
                    //place pawn in new position
                    chessBoard[row-1][col+j]="P";
                    //check if king is safe and record moves
                    if (kingSafe()) {
                        //original position, new position, captured piece
                        list=list+row+col+(row-1)+(col+j)+oldPiece;
                    }
                    //moving the pawn back
                    chessBoard[row][col]="P";
                    chessBoard[row-1][col+j]=oldPiece;
                }
            } catch (Exception e) {}
            //checking promotion && capture
           
            //try/catch = try method(if statement), if there is an error, do nothing, move on
            try {
                //less than 16, in top 2 rows and piece in intended place is lowercase
                if (Character.isLowerCase(chessBoard[row-1][col+j].charAt(0)) && i<16) {
                    //pawn can be promoted to bishop, queen, rook, knight, store options in string
                    String[] temp={"Q","R","B","K"};
                    //go through string, find which piece pawn has been promoted to
                    for (int k=0; k<4; k++) {
                        //location of old piece/where pawn intends to move
                        oldPiece=chessBoard[row-1][col+j];
                        //make space where pawn was blank
                        chessBoard[row][col]=" ";
                        //place what pawn has become (temp[k]) in new position
                        chessBoard[row-1][col+j]=temp[k];
                        //check if king is safe & record moves and capture
                        if (kingSafe()) {
                      //column1, column2,(already know rows) captured-piece, new-piece, P , "P" signals that there was a promotion
                            list=list+col+(col+j)+oldPiece+temp[k]+"P";
                        }
                         //moving pawn back
                        chessBoard[row][col]="P";
                        chessBoard[row-1][col+j]=oldPiece;
                    }
                }
            } catch (Exception e) {}
        }
        //moving a pawn one space up
        
        //try/catch = try method(if statement), if there is an error, do nothing, move on
        try {
            //if square on space up is blank and it is greater than/equal to 16 (out of top 2 rows)
            if (" ".equals(chessBoard[row-1][col]) && i>=16) {
                //location of old piece/where pawn intends to move
                oldPiece=chessBoard[row-1][col];
                //make space where pawn was blank
                chessBoard[row][col]=" ";
                //place pawn in new position
                chessBoard[row-1][col]="P";
                //check if king is safe and record move and capture
                if (kingSafe()) {
                    //original position, new position, captured piece
                    list=list+row+col+(row-1)+col+oldPiece;
                }
                //moving the pawn back
                chessBoard[row][col]="P";
                chessBoard[row-1][col]=oldPiece;
            }
        } catch (Exception e) {}
        //promotion && no capture
         
         //try/catch = try method(if statement), if there is an error, do nothing, move on
        try {
            //if square on space up is blank and it is less than 16 (in top 2 rows)
            if (" ".equals(chessBoard[row-1][col]) && i<16) {
                //pawn can be promoted to bishop, queen, rook, knight, store options in string
                String[] temp={"Q","R","B","K"};
                 //go through string, find which piece pawn has been promoted to
                for (int k=0; k<4; k++) {
                    //location of old piece/where pawn intends to move
                    oldPiece=chessBoard[row-1][col];
                    //make space where pawn was blank
                    chessBoard[row][col]=" ";
                    //place pawn in new position
                    chessBoard[row-1][col]=temp[k];
                    //check if king is safe and record move and capture
                    if (kingSafe()) {
                        //column1, column2,(already know rows) captured postion/piece, new-piece, P , "P" signals that there was a promotion
                        list=list+col+col+oldPiece+temp[k]+"P";
                    }
                    //moving pawn back
                    chessBoard[row][col]="P";
                    chessBoard[row-1][col]=oldPiece;
                }
            }
        } catch (Exception e) {}
        //move pawn TWO  spaces up
         
         //try/catch = try method(if statement), if there is an error, do nothing, move on
        try {
            //if square 2 spaces up is blank and in bottom 2 rows
            if (" ".equals(chessBoard[row-1][col]) && " ".equals(chessBoard[row-2][col]) && i>=48) {
                //location of old piece/where pawn intends to move
                oldPiece=chessBoard[row-2][col];
                //make space where pawn was blank
                chessBoard[row][col]=" ";
                //place pawn in new position
                chessBoard[row-2][col]="P";
                //check if king is safe and record move and capture
                if (kingSafe()) {
                    //original position, new position, captured piece/position
                    list=list+row+col+(row-2)+col+oldPiece;
                }
                //moving pawn back
                chessBoard[row][col]="P";
                chessBoard[row-2][col]=oldPiece;
            }
        } catch (Exception e) {}
        //return list of possible moves/captures
        return list;
    }
   public static String possibleR(int i){
        //list of possible rook moves, oldPiece = piece that was in old position/before movement
        String list="", oldPiece;
        //repeating method from possibleMoves
        int row = i/8, col = i%8; 
        //used for moving in indefinite length
        int temp = 1;
        //goes from -1 to 1, allows you to go all directions rook can go, checking all places rook can go
        for (int j=-1; j<=1; j+=2){
            //try/catch = try method(if statement), if there is an error, do nothing, move on
            try{
                //checking vertical
             //checking that intended path is empty, temp*j (temp++) will fan out in the direction & check
              while(" ".equals(chessBoard[row][col+temp*j]))  
              {
                  //location of old piece/where rook intends to move
                  oldPiece = chessBoard[row][col+temp*j];
                  //make space where rook was blank
                  chessBoard[row][col] = " ";
                  //place rook in new position
                  chessBoard[row][col+temp*j]="R";
                  
                  //check if king is safe and record move and capture
                  if (kingSafe()){
                      //original position, new position, captured piece/position
                      list=list+row+col+row+(col+temp*j)+oldPiece;
                  }
                  
                  //moving the rook back
                  chessBoard[row][col] = "R";
                  chessBoard[row][col+temp*j] = oldPiece;
                  
                  //keep increasing until nowhere further to go
                  temp++;
                  
              }
              //when not all spaces on intended path is blank; can we capture piece?
                    //if character at intended position is lower case
              if(Character.isLowerCase(chessBoard[row][col+temp*j].charAt(0))){
                  //location of old piece/where rook intends to move
                  oldPiece = chessBoard[row][col+temp*j];
                  //make space where rook was blank
                  chessBoard[row][col] = " ";
                  //place rook in new position
                  chessBoard[row][col+temp*j]="R";
                  
                  //check if king is safe and record moves and capture
                  if (kingSafe()){
                      //original position, new position, captured piece
                      list=list+row+col+row+(col+temp*j)+oldPiece;
                  }
                  //moving the rook back
                  chessBoard[row][col] = "R";
                  chessBoard[row][col+temp*j] = oldPiece;
                  
              }
            } catch (Exception e) {}
        
            //begin again in different direction
            temp = 1;
        
         try{
             //checking horizontal, same methods as above only checking in different direction
              while(" ".equals(chessBoard[row+temp*j][col]))  
              {
                  oldPiece = chessBoard[row+temp*j][col];
                  chessBoard[row][col] = " ";
                  chessBoard[row+temp*j][col]="R";
                  if (kingSafe()){
                      //original position, new position, captured piece/position
                      list=list+row+col+(row+temp*j)+col+oldPiece;
                  }
                  chessBoard[row][col] = "R";
                  chessBoard[row+temp*j][col] = oldPiece;
                  temp++;
              }
              if(Character.isLowerCase(chessBoard[row+temp*j][col].charAt(0))){
                  oldPiece = chessBoard[row+temp*j][col];
                  chessBoard[row][col] = " ";
                  chessBoard[row+temp*j][col]="R";
                  if (kingSafe()){
                      //original position, new position, captured piece
                      list=list+row+col+(row+temp*j)+col+oldPiece;
                  }
                  chessBoard[row][col] = "R";
                  chessBoard[row+temp*j][col] = oldPiece;
                  
              }
            } catch (Exception e) {}
         temp = 1;
     }
        //return list of possible moves/captures
        return list; 
    }
     public static String possibleK(int i){
        //list of possible Knight moves, oldPiece = piece that was in old position/before movement
        String list="", oldPiece;
        //repeating method from possibleMoves
        int row = i/8, col = i%8;
         //=+2 will avoid j and k turning into 0
        //goes from -1 to 1, allows you to go all directions knight can go/checks all places knight can go
        for (int j=-1; j<=1; j+=2){
             for (int k=-1; k<=1; k+=2){
                 //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{ 
                    //checking squares around knight to see if they are lower case(cannot take own pieces) or blank.
                    //checks over 1, up/down 2
                    if(Character.isLowerCase(chessBoard[row+j][col+k*2].charAt(0)) || " ".equals(chessBoard[row+j][col+k*2])){
                        //location of old piece/where knight intends to move
                        oldPiece = chessBoard[row+j][col+k*2];
                        //make space blank
                        chessBoard[row][col] = " ";
                        
                        //check if king is safe and record move & capture
                        if (kingSafe()){
                            //original position, new position, captured piece/position
                             list=list+row+col+(row+j)+(col+k*2)+oldPiece;
                        }
                        
                        //moving knight back
                        chessBoard[row][col]="K";
                        chessBoard[row+j][col+k*2] = oldPiece;
                    }
                } catch (Exception e) {}
                
                 //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{
                    //checking squares around knight to see if they are lower case(cannot take own pieces) or blank.
                    //checks over 2, up/down 1
                    if(Character.isLowerCase(chessBoard[row+j*2][col+k].charAt(0)) || " ".equals(chessBoard[row+j*2][col+k])){
                        //location of old piece/where knight intends to move
                        oldPiece = chessBoard[row+j*2][col+k];
                        //make space blank
                        chessBoard[row][col] = " ";
                        //check if king is safe and record move & capture
                        if (kingSafe()){
                            //original position, new position, captured piece/position
                             list=list+row+col+(row+j*2)+(col+k)+oldPiece;
                        }
                        //moving knight back
                        chessBoard[row][col]="K";
                        chessBoard[row+j*2][col+k] = oldPiece;
                    }
                } catch (Exception e) {}
            }
        }
        //return list of possible moves/captures
        return list; 
    }
     public static String possibleB(int i){
        //list of possible bishop moves, oldPiece = piece that was in old position/before movement
        String list="", oldPiece;
        //repeating method from possibleMoves
        int row = i/8, col = i%8;
        //used for moving in indefinite length
        int temp = 1;
        //=+2 will avoid j and k turning into 0
        //goes from -1 to 1, allows you to go all directions bishop can go/checks all places bishop can go
        //negatives & positives referring to diagonal directions
        for (int j=-1; j<=1; j+=2){
            for (int k=-1; k<=1; k+=2){
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{
                    //checking that intended path is empty, temp*j and temp*k (temp++) will fan out in each direction & check
                    while(" ".equals(chessBoard[row+temp*j][col+temp*k]))
                    {
                        //location of old piece/where bishop intends to move
                        oldPiece = chessBoard[row+temp*j][col+temp*k];
                         //make space where bishop was blank
                        chessBoard[row][col] = " ";
                        //place in new location
                        chessBoard[row+temp*j][col+temp*k]="B";
                        
                        //check if king is safe and record move & capture
                        if (kingSafe()){
                        //original position, new position, captured piece/position
                        list = list+row+col+(row+temp*j)+(col+temp*k)+oldPiece;
                }
                        //moving bishop back
                        chessBoard[row][col] = "B";
                        chessBoard[row+temp*j][col+temp*k]= oldPiece;
                 
                        //keep increasing until nowhere further to go
                        temp++;
                    }
                     //when not all spaces on intended path is blank; can we capture piece?
                    //if character at intended position is lower case
                    if (Character.isLowerCase(chessBoard[row+temp*j][col+temp*k].charAt(0))){
                        //location of old piece/where pawn intends to move
                        oldPiece = chessBoard[row+temp*j][col+temp*k];
                       //make space where bishop was blank
                        chessBoard[row][col] = " ";
                        //place bishop in space
                        chessBoard[row+temp*j][col+temp*k]="B";
                        
                         //check if king is safe and record move & capture
                        if (kingSafe()){
                        //original position, new position, captured piece
                        list = list+row+col+(row+temp*j)+(col+temp*k)+oldPiece;
                }
                        //moving bishop back
                        chessBoard[row][col] = "B";
                        chessBoard[row+temp*j][col+temp*k]= oldPiece;
                 
                        
                    }
                } catch (Exception e) {} 
                //begin checking in new direction
                temp = 1;
                
            }
        }
        //return list of possible moves/captures
        return list; 
     
    }
     public static String possibleQ(int i){
        //list of possible queen moves, oldPiece = piece that was in old position/before movement
        String list="", oldPiece;
        //repeating method from possibleMoves
        int row = i/8, col = i%8;
        //used for moving in indefinite length
        int temp = 1;
        //goes from -1 to 1, allows you to go all 8 directions queen can go/checks all places queen can go
        //negatives & positives referring to diagonal directions
        for (int j=-1; j<=1; j++){
            for (int k=-1; k<=1; k++){
                //if both are 0 then temp methods are useless
                if (j!=0 || k!=0){
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{
                    //checking that intended path is empty, temp*j and temp*k (temp++) will fan out in each direction & check
                    //
                    while(" ".equals(chessBoard[row+temp*j][col+temp*k]))
                    {
                        //location of old piece/where queen intends to move
                        oldPiece = chessBoard[row+temp*j][col+temp*k];
                        //make space where queen was blank
                        chessBoard[row][col] = " ";
                        //place queen in space
                        chessBoard[row+temp*j][col+temp*k]="Q";
                        //check if king is safe and record move & capture
                        if (kingSafe()){
                        //original position, new position, captured piece/position
                        list = list+row+col+(row+temp*j)+(col+temp*k)+oldPiece;
                }
                        //moving queen back
                        chessBoard[row][col] = "Q";
                        chessBoard[row+temp*j][col+temp*k]= oldPiece;
                        
                        //keep increasing until nowhere further to go
                        temp++;
                    }
                    //when not all spaces on intended path is blank; can we capture piece?
                    //if character at intended position is lower case
                    if (Character.isLowerCase(chessBoard[row+temp*j][col+temp*k].charAt(0))){
                        //location of old piece/where knight intends to move
                        oldPiece = chessBoard[row+temp*j][col+temp*k];
                        //make space where queen was blank
                        chessBoard[row][col] = " ";
                        //place queen in new space
                        chessBoard[row+temp*j][col+temp*k]="Q";
                        //check if king is safe and record move & capture
                        if (kingSafe()){
                        //original position, new position, captured piece
                        list = list+row+col+(row+temp*j)+(col+temp*k)+oldPiece;
                }
                        //moving queen back
                        chessBoard[row][col] = "Q";
                        chessBoard[row+temp*j][col+temp*k]= oldPiece;
                 
                        
                    }
                } catch (Exception e) {} 
                
                //begin checking in new direction
                temp = 1;
                }
            }
        }
        //return list of possible moves/captures
        return list; 
    }
     
     public static String possibleA(int i){
         //list of possible king moves, oldPiece = piece that was in old position/before movement
        String list="", oldPiece;
        //repeating method from possibleMoves
        int row = i/8, col = i%8;
        //goes through each square king can move to,9 because king can move to 9 places.
        for (int j=0; j<9; j++) {
            //if not moving to ourself/current square/position
            if (j!=4){
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                try {
                    //checking squares around king to see if they are lower case(cannot take own pieces) or blank.
                if (Character.isLowerCase(chessBoard[row-1+j/3][col-1+j%3].charAt(0)) || " ".equals(chessBoard[row-1+j/3][col-1+j%3])){
                    //location of old piece/where king intends to move
                    oldPiece = chessBoard[row-1+j/3][col-1+j%3];
                    //make space where king was blank
                    chessBoard[row][col] = " ";
                    //moving the king to new position
                    chessBoard[row-1+j/3][col-1+j%3] = "A";
                    //get new position of king, store position in kingTemp
                    int kingTemp = kingPositionCap;
                    kingPositionCap = i+(j/3)*8+j%3-9;
                    //check if king is safe & record move & capture(oldPiece)
                    if (kingSafe()){
                        //original position, new position, captured piece/position
                        list = list+row+col+(row-1+j/3)+(col-1+j%3)+oldPiece;
                }
                    //returning king to original place
                    chessBoard[row][col] = "A";
                    chessBoard[row-1+j/3][col-1+j%3] = oldPiece;
                    kingPositionCap = kingTemp;
            }
                  }catch (Exception e){}
     }
   }
        //return list of possible moves/captures
        return list; 
    }
      
        
        //sort method
        //sorts moves in order of best to worst moves
        //looks for best 5/6 moves and puts them in correct order
        //no need to sort more as don't really need the worst moves
        public static String sortMoves(String list){
            //goes through each move and look at rating for each in order to determing which are best
            int[] score = new int [list.length()/5];
            for (int i=0; i<list.length(); i+=5){
                makeMove(list.substring(i, i+5));
                //check score/rating for the moves, call the rating method from rating class
                score[i/5] =- Rating.rating(-1, 0);
                undoMove(list.substring(i, i+5));
            }
            //sorting the best moves
            //above method scans through for the best & second best moves, they will be added to newListA
            //moves added to listA will be removed from listB(initially containing all possible moves)
            String newListA = "";
            String newListB = list;
            //go through first few moves looking for best moves
            for (int i=0; i<Math.min(6, list.length()/5); i++){
                //
                int max = -1000000;
                int maxLocation = 0;
                //
                for (int j=0; j<list.length()/5; j++){
                    if(score[j]>max) {max = score[j]; maxLocation = j;}
                }
                //when scanning through again, do not want to find best move again, look for 2nd best move
                score [maxLocation] =-1000000;
                //add best move to list
                newListA += list.substring(maxLocation*5, maxLocation*5+5);
                //remove move from initial list after it has been added to listA
                //replace move found with blank space
                newListB = newListB.replace(list.substring(maxLocation*5, maxLocation*5+5), "");
            }
            
            //return list of best moves(listA) and the new list of all possible moves minus the best moves(listB)
            return newListA + newListB;
        }
        //checks if king is in any danger
        public static boolean kingSafe(){
            //check if opposing bishop/queen is threatening our king
            
            //used for moving in indefinite length
            int temp = 1;
            //=+2 will avoid j and k turning into 0
            //goes from -1 to 1, allows you to go all directions bishop/queen can go/checks all places bishop/queen can go
            //negatives & positives referring to diagonal directions
            for (int i=-1; i<=1; i+=2){
            for (int j=-1; j<=1; j+=2){
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{
                    //going in diagonal indefinitely while the spaces/squares are empty             keep going until nowhere further to go
                 while(" ".equals(chessBoard[kingPositionCap/8+temp*i][kingPositionCap%8+temp*j])) {temp++;}
                 //is piece in space/square a bishop OR a queen? 
                 //king in danger
                 if("b".equals(chessBoard[kingPositionCap/8+temp*i][kingPositionCap%8+temp*j]) ||
                         "q".equals(chessBoard[kingPositionCap/8+temp*i][kingPositionCap%8+temp*j])){
                     //if above is true, king is NOT safe, kingSafe = false
                     //will stop kingSafe method here without having to check below methods for other pieces
                     return false;
                     }
                    
                } catch (Exception e) {} 
                //begin again in different direction
                temp = 1;
            }
          }
            
        //check if opposing rook/queen is threatening our king
      
          //goes from -1 to 1, allows you to go all directions rook/queen can go/checks all places rook/queen can go
            for (int i=-1; i<=1; i+=2){
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{
                    //going vertically indefinitely while the spaces/squares are empty       keep going until nowhere further to go
                 while(" ".equals(chessBoard[kingPositionCap/8][kingPositionCap%8+temp*i])) {temp++;}
                 //is piece in space/square a bishop OR a queen? 
                 //king in danger
                 if("r".equals(chessBoard[kingPositionCap/8][kingPositionCap%8+temp*i]) ||
                         "q".equals(chessBoard[kingPositionCap/8][kingPositionCap%8+temp*i])){
                     //if above is true, king is NOT safe, kingSafe = false
                     //will stop kingSafe method here without having to check below methods for other pieces
                     return false;
                     }
                 
                } catch (Exception e) {} 
                //begin again in different direction
                temp = 1;
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{
                //going horizontally indefinitely while the spaces/squares are empty    keep going until nowhere further to go
                 while(" ".equals(chessBoard[kingPositionCap/8+temp*i][kingPositionCap%8])) {temp++;}
                 //is piece in space/square a bishop OR a queen? 
                 //king in danger
                 if("r".equals(chessBoard[kingPositionCap/8+temp*i][kingPositionCap%8]) ||
                         "q".equals(chessBoard[kingPositionCap/8+temp*i][kingPositionCap%8])){
                     //if above is true, king is NOT safe, kingSafe = false
                     //will stop kingSafe method here without having to check below methods for other pieces
                     return false;
                     }
                 
                } catch (Exception e) {} 
                //begin again in different direction
                temp = 1;
            }
          
            //check if opposing knight is threatening our king
            
            //=+2 will avoid j and k turning into 0
            //goes from -1 to 1, allows you to go all directions knight can go/checks all places knight can go
            for (int i=-1; i<=1; i+=2){
            for (int j=-1; j<=1; j+=2){
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                try{
                    //Checking 1 up, 2 over;is piece in space a knight?
                    //king in danger
                 if("k".equals(chessBoard[kingPositionCap/8+i][kingPositionCap%8+j*2])){
                     //if above is true, king is NOT safe, kingSafe = false
                     //will stop kingSafe method here without having to check below methods for other pieces
                     return false;
                 }
                    
                } catch (Exception e) {} 
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                 try{
                     //Checking 2 over, 1 up; is piece in space a knight?
                     //king is in danger
                 if("k".equals(chessBoard[kingPositionCap/8+i*2][kingPositionCap%8+j])){
                     //if above is true, king is NOT safe, kingSafe = false
                     //will stop kingSafe method here without having to check below methods for other pieces
                     return false;
                 }
                    
                } catch (Exception e) {} 
            }
            
        }
        
        //check if opposing pawn is threatening our king
            
            //as long as king is NOT in top 2 rows
            if (kingPositionCap>=16){
                //try/catch = try method(if statement), if there is an error, do nothing, move on
                 try{
                     //go one up, check each side(left/right)is piece in a space a pawn?
                     //king is in danger
                    if("p".equals(chessBoard[kingPositionCap/80-1][kingPositionCap%8-1])){
                     //if above is true, king is NOT safe, kingSafe = false
                     //will stop kingSafe method here without having to check below methods for other pieces
                     return false;
                     }
                    
                } catch (Exception e) {} 
                 
                 //try/catch = try method(if statement), if there is an error, do nothing, move on
                 try{
                     //go one down, check each side(left/right)is piece in a space a pawn?
                     //king is in danger
                    if("p".equals(chessBoard[kingPositionCap/80-1][kingPositionCap%8+1])){
                      //if above is true, king is NOT safe, kingSafe = false
                     //will stop kingSafe method here without having to check below methods for other pieces
                     return false;
                     }
                    
                } catch (Exception e) {} 
                 
        //check if opposing king is threatening our king
                 
              //goes all directions a king can go
              for (int i=-1; i<=1; i++){
                for (int j=-1; j<=1; j++){
                    //providing i & j do not equal zero
                    if (i!=0 || j!=0){
                        //try/catch = try method(if statement), if there is an error, do nothing, move on
                        try{
                             //is piece in space opposing king? Checks spaces, if yes;
                             //king is in danger
                             if("a".equals(chessBoard[kingPositionCap/8+i][kingPositionCap%8+j])){
                                 //if above is true, king is NOT safe, kingSafe = false
                               
                                 return false;
                             } 
                            } catch (Exception e) {} 
                         }
                     }
                  }      
                }    
            //king is not in danger, kingSafe = true
            return true;
        }
    }

