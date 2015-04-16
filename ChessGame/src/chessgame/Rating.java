/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgame;

/**
 *
 * @author Corrina
 */
//NOTE: actual pieces will not be on other side of board
//eg, if computer is playing as white, it will still appear that the computer is playing as black
//(as it is moving the black pieces), but it is still playing as white.
    
public class Rating {
    //different scores for position of pawn in various spaces on the board
     static int pawnBoard[][]={
        { 0,  0,  0,  0,  0,  0,  0,  0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        { 5,  5, 10, 25, 25, 10,  5,  5},
        { 0,  0,  0, 20, 20,  0,  0,  0},
        { 5, -5,-10,  0,  0,-10, -5,  5},
        { 5, 10, 10,-20,-20, 10, 10,  5},
        { 0,  0,  0,  0,  0,  0,  0,  0}};
     //different scores for position of rook in various spaces on the board
    static int rookBoard[][]={
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 5, 10, 10, 10, 10, 10, 10,  5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        { 0,  0,  0,  5,  5,  0,  0,  0}};
    //different scores for position of knight in various spaces on the board
    static int knightBoard[][]={
        {-50,-40,-30,-30,-30,-30,-40,-50},
        {-40,-20,  0,  0,  0,  0,-20,-40},
        {-30,  0, 10, 15, 15, 10,  0,-30},
        {-30,  5, 15, 20, 20, 15,  5,-30},
        {-30,  0, 15, 20, 20, 15,  0,-30},
        {-30,  5, 10, 15, 15, 10,  5,-30},
        {-40,-20,  0,  5,  5,  0,-20,-40},
        {-50,-40,-30,-30,-30,-30,-40,-50}};
    //different scores for position of bishop in various spaces on the board
    static int bishopBoard[][]={
        {-20,-10,-10,-10,-10,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5, 10, 10,  5,  0,-10},
        {-10,  5,  5, 10, 10,  5,  5,-10},
        {-10,  0, 10, 10, 10, 10,  0,-10},
        {-10, 10, 10, 10, 10, 10, 10,-10},
        {-10,  5,  0,  0,  0,  0,  5,-10},
        {-20,-10,-10,-10,-10,-10,-10,-20}};
    //different scores for position of queen in various spaces on the board
    static int queenBoard[][]={
        {-20,-10,-10, -5, -5,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5,  5,  5,  5,  0,-10},
        { -5,  0,  5,  5,  5,  5,  0, -5},
        {  0,  0,  5,  5,  5,  5,  0, -5},
        {-10,  5,  5,  5,  5,  5,  0,-10},
        {-10,  0,  5,  0,  0,  0,  0,-10},
        {-20,-10,-10, -5, -5,-10,-10,-20}};
    //different scores for position of king in various spaces on the board, midway through or at the start of the game
    static int kingMidBoard[][]={
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-20,-30,-30,-40,-40,-30,-30,-20},
        {-10,-20,-20,-20,-20,-20,-20,-10},
        { 20, 20,  0,  0,  0,  0, 20, 20},
        { 20, 30, 10,  0,  0, 10, 30, 20}};
    //different scores for position of king in various spaces on the board in an endgame situation
    static int kingEndBoard[][]={
        {-50,-40,-30,-20,-20,-30,-40,-50},
        {-30,-20,-10,  0,  0,-10,-20,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-30,  0,  0,  0,  0,-30,-30},
        {-50,-30,-30,-30,-30,-30,-30,-50}};
    
      //evaluate board
        public static int rating(/*returns list of moves, let rating method know what moves are possible*/int list,
                /*deeper depth returns more important ratings/moves*/int depth){
            //counter = "points" received for move
            int counter = 0; 
            int material = rateMaterial();
            counter += rateAttack();
            counter += material;
            counter += rateMovability(list, depth, material);
            counter += ratePositional(material);
            //subtract all values that other side has
            ChessGame.flipBoard();
            material = rateMaterial();
            counter -= rateAttack();
            counter -= material;
            counter -= rateMovability(list, depth, material);
            counter -= ratePositional(material);
            ChessGame.flipBoard();
            //return score
            //negative = from black perspective
            //get more points for deeper searches
            return -(counter+depth*50);
        }
        
          //rate the attack, who is attacting who, etc
        public static int rateAttack(){
            //score
            int counter = 0;
            //record king position before kingPositionCap = i
            int tempPositionCap = ChessGame.kingPositionCap;
            //goes through each square
             for (int i= 0; i<64; i++){
                //switch based on position in array/on board
                switch (ChessGame.chessBoard[i/8][i%8]){
                    //if Pawn, then get score for Pawn at location i(speciifying which pawn)
                    //how to check if something is attacking
                    //set king position to i(location of square we are looking at), can check if king is safe in that square, will know if we are under attack
                    //if king is not safe, you get a bad/negative score
                    case "P": {ChessGame.kingPositionCap = i; if(!ChessGame.kingSafe()) {counter -= 64;}}
                    //if not, do nothing
                        break;
                        
                    //how to check if something is attacking
                    //set king position to i(location of square we are looking at), can check if king is safe in that square, will know if we are under attack
                    //if Rook, then get score for Rook at location i(speciifying which Rook)
                    //if king is not safe, you get a bad/negative score
                    case "R": {ChessGame.kingPositionCap = i; if(!ChessGame.kingSafe()) {counter -= 500;}}
                    //if not, do nothing
                    //so on for other pieces
                        break;
                        
                    case "K": {ChessGame.kingPositionCap = i; if(!ChessGame.kingSafe()) {counter -= 300;}}
                        break;
                        
                    case "B": {ChessGame.kingPositionCap = i; if(!ChessGame.kingSafe()) {counter -= 300;}}
                        break;
                        
                    case "Q": {ChessGame.kingPositionCap = i; if(!ChessGame.kingSafe()) {counter -= 900;}}
                        break;
                }
        }
            //reset king position to it's original position after kingPositionCap = i
            ChessGame.kingPositionCap = tempPositionCap;
            //putting outside the switch as it only occurs once on the board
            //if king is not safe then we get a bad/negative score
            if (!ChessGame.kingSafe()) {counter -= 200;}
            //return score, divided by 2 as attacking is not as negative as taking/destroying the piece
            return counter/2;
        }
             
          //evalutes the pieces, measured in centipawns(unit of measure used in chess as measure of the advantage)
          //A centipawn is equal to 1/100 of a pawn. Therefore 100 centipawns = 1 pawn
        public static int rateMaterial(){
            //score
            int counter = 0;
            //check bishops(bad to lose a bishop, less moves can be made)
            int bishopCounter = 0;
            //goes through each square
            for (int i= 0; i<64; i++){
                //switch based on position in array/on board
                switch (ChessGame.chessBoard[i/8][i%8]){
                    //if Pawn, then get score for Pawn at location i(speciifying which pawn)
                    case "P": counter+= 100;
                    //if not, do nothing
                        break;
                    //if Rook, then get score for Rook at location i(speciifying which Rook)
                    case "R": counter+= 500;
                    //if not, do nothing
                    //so on for other pieces
                        break;
                    case "K": counter+= 300;
                        break;
                    case "B": bishopCounter+= 1;
                        break;
                    case "Q": counter+= 900;
                        break;
                }
        }
            //if theres 2 bishops, they are worth 300 each
            if (bishopCounter>=2){
                counter += 300*bishopCounter;
            }
            //otherwise, if there is only one, they are worth 250 each
            else{
                if (bishopCounter == 1) {
                    counter +=250;
                }
            }
           
            return counter;
        }
        
          //evaluate moves, how flexible is piece, what are our options, which is best?
        //list length = how many moves possible, 
        public static int rateMovability(int listLength, int depth, int material){
            //score
            int counter = 0;
            //get more points/more beneficial for having more moves, 5 points per vaild move
            counter += listLength;
            //check if stalemate or checkmate, if no moves available = current side is in checkmate/stalemate
            if (listLength == 0) {
                //check if king is in check = king cannot move & is in danger
                if (!ChessGame.kingSafe()){
                    //losing score
                    counter += -200000*depth;
                }
                //if king is not in check, then stalemate(king cannot move, but not in danger
                else{
                    //also very bad score
                    counter += -150000*depth;
                }
            }
            return 0;
        }
        
          //evaluate the position, e.g. good for king to be in middle at endgame, not good for king to be in the middle midway through the game
        public static int ratePositional(int material /*materials will help decide wheter the game is at the start, beginning or end(how many pieces there are)*/){
            //score
            int counter = 0;
            //goes through each square
            for (int i= 0; i<64; i++){
                //switch based on position in array/on board
                switch (ChessGame.chessBoard[i/8][i%8]){
                    //if Pawn, then get score for Pawn at location i(speciifying which pawn)
                    case "P": counter+= pawnBoard[i/8][1%8];
                    //if not, do nothing
                        break;
                    //if Rook, then get score for Rook at location i(speciifying which Rook)
                    case "R": counter+= rookBoard[i/8][1%8];;
                    //if not, do nothing
                    //so on for other pieces
                        break;
                    case "K": counter+= knightBoard[i/8][1%8];
                        break;
                    case "B": counter+= bishopBoard[i/8][1%8];
                        break;
                    case "Q": counter+= queenBoard[i/8][1%8];
                        break;
                    case "A": if(material >= 1750){ 
                        counter += kingMidBoard[i/8][1%8];
                        //pay attention to moves available to king
                        counter += ChessGame.possibleA(ChessGame.kingPositionCap).length()*10;
                    }
                    else {
                         counter += kingEndBoard[i/8][1%8];
                        //pay attention to moves available to king
                                                                                    //more important for king to be able to move around in endgame
                        counter += ChessGame.possibleA(ChessGame.kingPositionCap).length()*30;
                    }
                        break;
                }
            }
            return counter;
        }
    
}
