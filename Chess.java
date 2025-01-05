//Author: Anshul Mistry and Muhammad Haris
//Chess Pieces Source: https://github.com/ImKadaga/chess-pieces/blob/master/chess_maestro_bw/wR.svg
//all the imported classes we need in order to make code work
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.JOptionPane;
//the make public class for check which makes the frame and other aspects of it
public class Chess {
    public static void main(String[] args) {

        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        GamePanel loop = new GamePanel();


        loop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                for (Piece i : GamePanel.getPieces()) {
                    if (x >= i.x && x <= i.x + Board.SQUARE_SIZE && y >= i.y && y <= i.y + Board.SQUARE_SIZE) {
                        if ((GamePanel.getPiece() != null && GamePanel.getPiece().color == i.color) || GamePanel.getPiece() == null)
                        {
                            GamePanel.selectPiece(i);
                            break;
                        }
                    }
                }
                if (GamePanel.getPiece() != null)
                {
                    if (GamePanel.getPieceAt(x, y) == null)
                    {
                        GamePanel.update(x, y);
                    }
                    else if (checkCapture(x, y) && GamePanel.getPiece().checkRules(x, y))
                    {
                        Piece target = GamePanel.getPieceAt(x, y);
                        GamePanel.update(x, y);
                        GamePanel.pieces.remove(target);
                    }
                }

            }
        });

        window.add(loop);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        loop.launchGame();
    }
    // Sees if the piece is about to be killed
    public static boolean checkCapture(int x1, int y1)
    {
        Piece target = GamePanel.getPieceAt(x1, y1);
        if (target != null && target.color != GamePanel.getPiece().color) {
            return true;
        }
        return false;
    }
}
//All the methods and variables are contained in this
class GamePanel extends JPanel implements Runnable {
    private static final double WIDTH = 800;
    private static final double HEIGHT = 800;
    private final int FPS = 60;
    private Thread gameThread;
    private Board b = new Board();
    private static final int WHITE = 0;
    private static final int BLACK = 1;
    public static ArrayList<Piece> pieces = new ArrayList<>();
    private static Piece selected = null;
    public static int turn = WHITE;
    public static Piece lastPiece = null;

    //checks to see if the king is in check
 public static boolean isInCheck(int color) {
        Piece king = findKing(color);
        if (king == null) {
            return false;
        }
        for (Piece piece : pieces) {
            if (piece.color != color && piece.checkRules(king.x, king.y)) {
                ((King)king).check = true;
                return true;
            }
        }
        ((King)king).check = false;
        return false;
    }

    // Find the king piece of the given color
    public static Piece findKing(int color) {
        for (Piece piece : pieces) {
            if (piece instanceof King && piece.color == color) {
                return piece;
            }
        }
        return null;
    }




//switches the player turns after each player goes once
   private static void switchTurn() {
        if (turn == WHITE) {
            turn = BLACK;
        } else {
            turn = WHITE;
        }

        if (isInCheck(turn)) {
            JOptionPane.showMessageDialog(null, "Player " + (turn == WHITE ? "White" : "Black") + " is in check!");
        }
        else if (staleMate(turn))
        {
            JOptionPane.showMessageDialog(null, "The game is a tie, both players stalemated!");
            System.exit(0);
        }
        if (!checkMate(turn))
        {
            JOptionPane.showMessageDialog(null, "Player " + (turn == WHITE ? "White" : "Black") + " is checkmated!");
            System.exit(0);
        }
   }


//Makes the game panel
    public GamePanel() {
        setPreferredSize(new Dimension((int) WIDTH, (int) HEIGHT));
        setBackground(Color.BLACK);
        addPieces();
    }
//Makes array list containing all the pieces
    public static ArrayList<Piece> getPieces() {
        return pieces;
    }
//returns the player selected
    public static Piece getPiece() {
        return selected;
    }
//starts the game
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }
//highlights the piece selected
    public static void selectPiece(Piece a) {
        if (a != null && a.color == turn) {
            selected = a;
        } else {
            selected = null;
        }
    }
//causes the board to continuously update/repaint
    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                repaint();
                delta--;
            }
        }
    }
    // when the king is in check, a piece can block it to uncheck it
    public static boolean canBlock(Piece piece, int x1, int y1)
    {
        x1 /= 100;
        x1 *= 100;
        y1 /= 100;
        y1 *= 100;
        if (piece instanceof King || ((King)findKing(piece.color)).check != true) return true;
        else
        {
            Piece target = getPieceAt(x1, y1);
            int tempX = piece.x;
            int tempY = piece.y;
            piece.x = x1;
            piece.y = y1;
            if (target != null) {
                pieces.remove(target);
            }
            boolean valid = !isInCheck(piece.color);
            piece.x = tempX;
            piece.y = tempY;
            if (target != null) pieces.add(target);
            return valid;
        }
    }
    //Makes sure a piece can't move to cause a check
    public static boolean canCause(Piece piece, int x1, int y1)
    {
        x1 /= 100;
        x1 *= 100;
        y1 /= 100;
        y1 *= 100;
        Piece target = getPieceAt(x1, y1);
        int tempX = piece.x;
        int tempY = piece.y;
        piece.x = x1;
        piece.y = y1;
        if (target != null) {
            pieces.remove(target);
        }
        boolean valid = isInCheck(piece.color);
        piece.x = tempX;
        piece.y = tempY;
        if (target != null) pieces.add(target);
        return valid;
    }
    // When the king has no other options, the game is over
    public static boolean checkMate(int color)
    {
        if (isInCheck(color))
        {
            King temp = (King) findKing(color);
            boolean canMove = false;
            for (int i = -100; i < 200; i+=100)
            {
                for (int j = -100; j < 200; j+=100)
                {
                    if (!(i == 0 && j == 0) && (temp.x+i >= 0 && temp.x+i <= 700) && (temp.y+j >= 0 && temp.y+j <= 700))
                    {
                        if (!canCause(temp, temp.x+i, temp.y+j) && temp.checkRules(temp.x+i, temp.y+j))
                        {
                            canMove = true;
                        }
                    }
                }
            }
            return canMove;
        }
        else
        {
            return true;
        }
    }
    // This method occurs when there is no winner yet nothing can move
    public static boolean staleMate(int color)
    {
        for (Piece piece : pieces) {
            if (piece.color == color) {
                for (int x = 0; x < 800; x += 100) {
                    for (int y = 0; y < 800; y += 100) {
                        if (piece.checkRules(x, y) && canBlock(piece, x, y) && !canCause(piece, x, y)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
//updates the board and moves the selelcted piece around
    public static void update(int x, int y) {
        if (selected != null && selected.checkRules(x, y) && canBlock(selected, x, y) && !canCause(selected, x, y)) {
            x /= 100;
            x *= 100;
            y /= 100;
            y *= 100;


            if (selected instanceof King && Math.abs(x - selected.x) == 200) {
                if (x - selected.x > 0) {
                    Piece rook = GamePanel.getPieceAt(700, y);
                    if (rook != null) {
                        rook.x = 500;
                        rook.col = 5;
                    }
                } else {
                    Piece rook = GamePanel.getPieceAt(0, y);
                    if (rook != null) {
                        rook.x = 300;
                        rook.col = 3;
                    }
                }
            }

            selected.x = x;
            selected.y = y;
            selected.row = y / Board.SQUARE_SIZE;
            selected.col = x / Board.SQUARE_SIZE;

            if (selected instanceof Pawn && ((Pawn) selected).isPromoting())
                promotePawn((Pawn) selected);

            switchTurn();
            GamePanel.lastPiece = GamePanel.getPiece();
            selectPiece(null);
        }
    }
//promotion method for the pawn when they reach the other side of the board
    private static void promotePawn(Pawn pawn) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(null, "Promote to:", "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        Piece newPiece;
        switch (choice) {
            case 0: // Queen
                newPiece = new Queen(pawn.color, pawn.row, pawn.col);
                break;
            case 1: // Rook
                newPiece = new Rook(pawn.color, pawn.row, pawn.col);
                break;
            case 2: // Bishop
                newPiece = new Bishop(pawn.color, pawn.row, pawn.col);
                break;
            case 3: // Knight
                newPiece = new Knight(pawn.color, pawn.row, pawn.col);
                break;
            default:
                newPiece = new Queen(pawn.color, pawn.row, pawn.col);
        }

        pieces.remove(pawn);
        pieces.add(newPiece);
        selectPiece(null);
    }
//gets the piece and sees if it is about to be killed
    public static Piece getPieceAt(int x, int y) {
        x /= 100;
        x *= 100;
        y /= 100;
        y *= 100;
        for (Piece piece : pieces) {
            if (piece.x == x && piece.y == y) {
                return piece;
            }
        }
        return null;
    }


// the paint function which displays the colors such as the boarders
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        b.draw(g2);
        for (Piece a : pieces) {
            a.draw(g2);
        }
        if (selected != null) {
            g2.setStroke(new BasicStroke(10));
            g2.setColor(Color.YELLOW);
            g2.drawRect(selected.x, selected.y, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
        }
    }
//adds all the pieces onto the board
    public void addPieces() {
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(WHITE, 6, i));
            pieces.add(new Pawn(BLACK, 1, i));
        }
        pieces.add(new Rook(WHITE, 7, 0));
        pieces.add(new Knight(WHITE, 7, 1));
        pieces.add(new Bishop(WHITE, 7, 2));
        pieces.add(new Queen(WHITE, 7, 3));
        pieces.add(new King(WHITE, 7, 4));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 7, 6));
        pieces.add(new Bishop(WHITE, 7, 5));

        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Knight(BLACK, 0, 1));
        pieces.add(new Bishop(BLACK, 0, 2));
        pieces.add(new Queen(BLACK, 0, 3));
        pieces.add(new King(BLACK, 0, 4));
        pieces.add(new Rook(BLACK, 0, 7));
        pieces.add(new Knight(BLACK, 0, 6));
        pieces.add(new Bishop(BLACK, 0, 5));
    }
}
//makes the board fot chess
class Board {
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;

    public void draw(Graphics2D g2) {
        boolean swap = false;
        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                if (!swap) {
                    g2.setColor(new Color(155, 53, 61));
                    if (j != MAX_COL - 1) swap = true;
                } else {
                    g2.setColor(new Color(0,0,0));
                    if (j != MAX_COL - 1) swap = false;
                }
                g2.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}