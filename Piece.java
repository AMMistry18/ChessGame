//Author: Anshul Mistry

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
// This is the code for the parent class
//Allows us to make all the pieces with the basic functions
public class Piece
{
    //Makes variables which can be used throughout the code
    public int color;
    public int x;
    public int y;
    public int row;
    public int col;
    BufferedImage image;
    // makes the code for the parent piece function
    public Piece (int color, int row, int col)
    {
        this.color = color;
        this.row = row;
        this.col = col;
        x = col*Board.SQUARE_SIZE;
        y = row*Board.SQUARE_SIZE;

    }
    public void draw(Graphics2D g2)
    {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
    //checks to see if the rules are right or not
    public boolean checkRules(int x1, int y1) {
        return false;
    }
    // checks the position of the rook before removing it if needed
    public boolean checkRook(int x1, int y1)
    {
        if (x1 != x && y1 == y)
        {
            if (x1 - x > 0)
            {
                for (int i = x+100; i < x1; i+=100)
                {
                    if (GamePanel.getPieceAt(i, y1) != null)
                    {
                        return false;
                    }
                }
            }
            else
            {
                for (int i = x-100; i > x1; i-=100)
                {
                    if (GamePanel.getPieceAt(i, y1) != null)
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        else if (x1 == x && y1 != y)
        {
            if (y1 - y > 0)
            {
                for (int i = y+100; i < y1; i+=100)
                {
                    if (GamePanel.getPieceAt(x1, i) != null)
                    {
                        return false;
                    }
                }
            }
            else
            {
                for (int i = y-100; i > y1; i-=100)
                {
                    if (GamePanel.getPieceAt(x1, i) != null)
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    //checks the bishop rules
    public boolean checkBishop(int x1, int y1)
    {
        if (Math.abs(y1-y) == Math.abs(x1-x))
        {
            if (y1 - y > 0)
            {
                int j = y+100;
                if (x1 - x > 0)
                {
                    for (int i = x+100; i < x1; i+=100)
                    {
                        if (GamePanel.getPieceAt(i, j) != null)
                        {
                            return false;
                        }
                        j+=100;
                    }
                }
                else
                {
                    for (int i = x-100; i > x1; i-=100)
                    {
                        if (GamePanel.getPieceAt(i, j) != null)
                        {
                            return false;
                        }
                        j+=100;
                    }
                }
                return true;
            }
            else
            {
                int j = y-100;
                if (x1 - x > 0)
                {
                    for (int i = x+100; i < x1; i+=100)
                    {
                        if (GamePanel.getPieceAt(i, j) != null)
                        {
                            return false;
                        }
                        j-=100;
                    }
                }
                else
                {
                    for (int i = x-100; i > x1; i-=100)
                    {
                        if (GamePanel.getPieceAt(i, j) != null)
                        {
                            return false;
                        }
                        j-=100;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
// Code for the pawn piece
class Pawn extends Piece
{
    //Allows the piece to show up
    public Pawn(int color, int row, int col)
    {
        super(color, row, col);
        if (color == 0)
        {
            try {
                image = ImageIO.read(new File("Pieces/wP.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if (color == 1)
        {
            try {
                image = ImageIO.read(new File("Pieces/bP.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // Makes sure that the pieces only move up
    public boolean checkRules(int x1, int y1)
    {
        x1/=100;
        x1*=100;
        y1/=100;
        y1*=100;
        if (Math.abs(x1-x) == 100 && ((y1-y == -100 && color == 0) || (y1-y == 100 && color == 1)))
        {
            if (Chess.checkCapture(x1, y1))
            {
                return true;
            }
            Piece lastMovePiece = GamePanel.lastPiece;
            if (lastMovePiece instanceof Pawn && lastMovePiece.color != this.color &&
                lastMovePiece.y == y && Math.abs(lastMovePiece.x - x) == 100 &&
                (lastMovePiece.row == 4 || lastMovePiece.row == 3)) {
                GamePanel.pieces.remove(lastMovePiece);
                return true;
            }
            if (GamePanel.getPieceAt(x1, y1) instanceof King) return true;
        }
        if (x1 != x) return false;
        if (y == 600 || y == 100)
        {
            if (Math.abs(y1-y) == 200 && GamePanel.getPieceAt(x1, y1) == null && ((GamePanel.getPieceAt(x1,y1+100) == null && color == 0) || (GamePanel.getPieceAt(x1,y1-100) == null && color == 1)))
            {
                return true;
            }
            else if (Math.abs(y1-y) ==  100 && GamePanel.getPieceAt(x1, y1) == null)
            {
                return true;
            }
        }
        else if (Math.abs(y1-y) ==  100 && GamePanel.getPieceAt(x1, y1) == null)
        {
            return true;
        }
        return false;
    }

    public boolean isPromoting() {
        return (color == 0 && row == 0) || (color == 1 && row == 7);
    }
}
// Makes the bishop for the game
class Bishop extends Piece
{
    //Allows the  Bishop to show up
    public Bishop(int color, int row, int col)
    {
        super(color, row, col);
        if (color == 0)
        {
            try {
                image = ImageIO.read(new File("Pieces/wB.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            try {
                image = ImageIO.read(new File("Pieces/bB.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // Makes so that the Bishop only moves in diagonal directions and abides by the rules
    public boolean checkRules(int x1, int y1)
    {
        x1/=100;
        x1*=100;
        y1/=100;
        y1*=100;
        return checkBishop(x1, y1);
    }
}
// Makes the rook piece for the game
class Rook extends Piece
{
    public boolean moved = false;
    //Allows the piece to show up
    public Rook(int color, int row, int col)
    {
        super(color, row, col);
        if (color == 0)
        {
            try {
                image = ImageIO.read(new File("Pieces/wR.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            try {
                image = ImageIO.read(new File("Pieces/bR.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // makes it so that the rook pieces only go in a straight line both hortizontal and vertically
    public boolean checkRules(int x1, int y1)
    {
        x1/=100;
        x1*=100;
        y1/=100;
        y1*=100;
        if (checkRook(x1, y1)) moved = true;
        return checkRook(x1, y1);
    }
}
//Makes the knight/horse for the game
class Knight extends Piece
{
    //Allows the knight piece to show up
    public Knight(int color, int row, int col)
    {
        super(color, row, col);
        if (color == 0)
        {
            try {
                image = ImageIO.read(new File("Pieces/wN.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            try {
                image = ImageIO.read(new File("Pieces/bN.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // Makes it so the knight only moves in the "L" shape
    public boolean checkRules(int x1, int y1)
    {
        x1/=100;
        x1*=100;
        y1/=100;
        y1*=100;
        if ((Math.abs(y1-y) == 200 && Math.abs(x1-x) == 100) || (Math.abs(x1-x) == 200 && Math.abs(y1-y) == 100)) return true;
        return false;
    }
}
// Creates the King piece
class King extends Piece
{
    public boolean moved = false;
    public boolean check = false;
    public boolean checkMate = false;
    //Allows the king piece to show up
    public King(int color, int row, int col)
    {
        super(color, row, col);
        if (color == 0)
        {
            try {
                image = ImageIO.read(new File("Pieces/wK.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            try {
                image = ImageIO.read(new File("Pieces/bK.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // Checks the rules for the king so it only moves one place
    public boolean checkRules(int x1, int y1)
    {
        x1/=100;
        x1*=100;
        y1/=100;
        y1*=100;

        if (!moved && Math.abs(x1 - x) == 200 && y1 == y) {
            if (x1 - x > 0) {
                // King-side castling
                Piece rook = GamePanel.getPieceAt(700, y);
                if (rook instanceof Rook && !((Rook)rook).moved) {
                    if (GamePanel.getPieceAt(500, y) == null && GamePanel.getPieceAt(600, y) == null) {
                        return true;
                    }
                }
            } else {
                // Queen-side castling
                Piece rook = GamePanel.getPieceAt(0, y);
                if (rook instanceof Rook && !((Rook)rook).moved) {
                    if (GamePanel.getPieceAt(100, y) == null && GamePanel.getPieceAt(200, y) == null &&
                        GamePanel.getPieceAt(300, y) == null) {
                        return true;
                    }
                }
            }
        }

        if (Math.abs(x1-x) <= 100 && Math.abs(y1-y) <= 100 && ((GamePanel.getPieceAt(x1, y1) ==  null)))
        {
            moved = true;
            return true;
        }
        return false;
    }
}
//Makes the queen piece
class Queen extends Piece
{
    //Allows the king piece to show up
    public Queen(int color, int row, int col)
    {
        super(color, row, col);
        if (color == 0)
        {
            try {
                image = ImageIO.read(new File("Pieces/wQ.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            try {
            image = ImageIO.read(new File("Pieces/bQ.png"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // Checks the rules for the queen so she can move according to the rules
    public boolean checkRules(int x1, int y1)
    {
        x1/=100;
        x1*=100;
        y1/=100;
        y1*=100;
        return checkRook(x1, y1) || checkBishop(x1, y1);
    }
}