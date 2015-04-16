/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgame;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/**
 *
 * @author Corrina
 */
//NOTE: actual pieces will not be on other side of board
//eg, if computer is playing as white, it will still appear that the computer is playing as black
//(as it is moving the black pieces), but it is still playing as white.
    
public class ChessInterface extends JPanel implements MouseListener, MouseMotionListener{
    //get initial mouse positions(where we click down), and new mouse poitions(where mouse click is released)
    static int mouseX, mouseY, newMouseX, newMouseY;
    //set size of squares
    static int squareSize = 55;
    
    //tells program what to draw/display
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //sets the background colour of the JFrame
        this.setBackground(new Color(238, 238, 238));
        //takes in mouse movements(clicks/releases)
        this.addMouseListener(this);
        //takes in mouse movements(dragging, moving)
        this.addMouseMotionListener(this);
        
        //for every square on the board(64 squares), do this every second square(+=2)
        for (int i=0; i<64; i+=2){
            //setting colour of square
            g.setColor(new Color(255,255,255));
            //filling square with colour
            //x, y, width, heigth 
            //i%8+ i/8 gets each square
            //%2 = switches colour every second row, alternating colours begin each row
            g.fillRect((i%8+(i/8)%2)*squareSize, (i/8)*squareSize, squareSize, squareSize);
            //setting colour of square
            g.setColor(new Color(165,163,163));
            //filling square with colour
            //x, y, width, heigth 
            //i+1%8,i+1/8 gets each square for alternating colour
            //%2 = switches colour every second row, alternating colours begin each row
            g.fillRect(((i+1)%8-((i+1)/8)%2)*squareSize, ((i+1)/8)*squareSize, squareSize, squareSize);
        }
            //display chess pieces on board
            Image chessPiecesImage;
            chessPiecesImage = new ImageIcon("ChessPieces.png").getImage();
            
            //goes through each square on board
            for (int i=0; i<64; i++){
                //getting co-ordinates of the chesspieces image for each piece,j = horizontal(pieces), k=vertical(rows)
                //if after the switch statement, both j and k are still negative, then it is an empty spot and we do not display any image
                int j=-1, k=-1;
                
                //sends info from each square
                //sets what j and k will become/which piece they will be
                //imports chess board from ChessGame class
                 switch (ChessGame.chessBoard [i/8][i%8]){
                
                //if upper case P, show white pawn(j=5,k=0)
                case "P": j=5;  k=0;
                //if not, do nothing
                    break;
                    
                //if lower case p. show black pawn(j=5,k=1)
                case "p": j=5;  k=1;
                //if not, do nothing
                    break;
                    
                //if uppercase R, show white rook (j=2,k=0)
                case "R": j=2; k=0;
                //if not, then do nothing
                    break;
                
                //if lowercase r, show black rook (j=2,k=1)
                case "r": j=2; k=1;
                //if not, then do nothing
                    break;
                    
                //if uppercase K, show white knight (j=1,k=0)
                case "K": j=4; k=0;
                //if not, then do nothing
                    break;
                    
                //if lowercase k, show black knight (j=1,k=1)
                case "k": j=4; k=1;
                //if not, then do nothing
                    break;
                    
                //if uppercase B, show white bishop (j=3,k=0)
                case "B": j=3; k=0;
                //if not, then do nothing
                    break;
                
                //if lowercase b, show black bishop (j=3,k=1)
                case "b": j=3; k=1;
                //if not, then do nothing
                    break;
                
                //if uppercase Q, show white queen (j=0,k=0)
                case "Q": j=1; k=0;
                //if not, then do nothing
                    break;
                
                //if lowercase q, show black queen (j=0,k=1)
                case "q": j=1; k=1;
                //if not, then do nothing
                    break;
                   
                //if uppercase A, show white king (j=1,k=0)
                case "A": j=0; k=0;
                //if not, then do nothing
                    break;
                
                //if lowercase a, show black king (j=1,k=1)
                case "a": j=0; k=1;
                //if not, then do nothing
                    break;
                
            }
          //check if square is empty
                //don't print any image in empty square
          if(j!=-1 && k!=-1){
              //check & print/don't print board/pieces
              g.drawImage(chessPiecesImage, (i%8)*squareSize, (i/8)*squareSize, (i%8+1)*squareSize, (i/8+1)*squareSize, j*64, k*64, (j+1)*64, (k+1)*64, this);
          }
        } 
     }
    
    //
    @Override
    public void mouseMoved(MouseEvent e){
    
}
    //
    @Override
    public void mousePressed(MouseEvent e){
        //check if mouse is inside the board
        if(e.getX()<8*squareSize && e.getY()<8*squareSize){
            //if yes then set intial mouse position(mouseX,mouseY) to position inside board
            mouseX = e.getX();
            mouseY = e.getY();
            repaint();
        }
}
    //
    @Override
    public void mouseReleased(MouseEvent e){
        //check if mouse is released inside the board
        if(e.getX()<8*squareSize && e.getY()<8*squareSize){
            //if yes then set intial mouse position(mouseX,mouseY) to position inside board
            newMouseX = e.getX();
            newMouseY = e.getY();
            //check if mouse button was left button
            if (e.getButton () == MouseEvent.BUTTON1){
                String dragMove;
                //if yes then
                //dragMove will take mouseX,mouseY,newMouseX, newMouseY and change it to our move notation(e.g.7556p)
                //how to tell if pawn promotion, dragged to top row(0) from row 1, and if the piece is a pawn
                if (newMouseY/squareSize == 0 && mouseY/squareSize == 1 && "P".equals(ChessGame.chessBoard[mouseY/squareSize][mouseX/squareSize])){
                    //assumes pawn being promoted to queen
                    dragMove = "" + mouseX/squareSize + newMouseX/squareSize + ChessGame.chessBoard[newMouseY/squareSize][newMouseX/squareSize] + "QP";
                }
                //normal move, not promotion/castle, etc
                    else{
                    //gets original space(x,y) and new space(newx, newy) and captured piece(chessboard newy, chessboard newx)
                        dragMove = "" + mouseY/squareSize + mouseX/squareSize + newMouseY/squareSize + newMouseX/squareSize + ChessGame.chessBoard[newMouseY/squareSize][newMouseX/squareSize];
                }
                //create list of possible moves, compare with dragMove
                //if possible move exists within dragMove, then it is valid
                
                //get list of possible moves from ChessGame class
                String userPossibilities = ChessGame.possibleMoves();
                //everywhere we find a dragMove, replace it with blank space
                //every valid move will remove a section of 5
                //if it is a valid move, blank space is less than the possible move(4/5 characters)
                if (userPossibilities.replaceAll(dragMove, "").length()<userPossibilities.length()){
                    //if it is a valid move, then make the move
                    ChessGame.makeMove(dragMove);
                    //flip board after move
                    ChessGame.flipBoard();
                    //make move as computer,from ChessGame class methods    //depth(4), beta, alpha, move, player
                    //dragMove is human move(computer cannot drag mouse)
                    ChessGame.makeMove(ChessGame.alphaBetaAlgo(ChessGame.globalDepth, 1000000, -1000000, "", 0));
                    //flip board after move
                    ChessGame.flipBoard();
                    
                    repaint();
                }
             }
         }
    }
    //
    @Override
    public void mouseClicked(MouseEvent e){   
    }
    //
    @Override
    public void mouseDragged(MouseEvent e){
}   
    //
    @Override
    public void mouseEntered(MouseEvent e){
}
    //
    public void mouseExited(MouseEvent e){
}
    
    

    
    
    
}

