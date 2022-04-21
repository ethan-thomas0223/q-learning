package checkers.gui;

import checkers.core.Checkerboard;

import javax.swing.*;
import java.awt.*;

public class CheckersPanel extends JPanel {

    Checkerboard board;

    Color squareRed = new Color(255, 0, 0);
    Color squareBlack = new Color(0, 0, 0);

    public CheckersPanel() {
        super();
        board = new Checkerboard();
    }

    public Checkerboard getBoard() {
        return board;
    }

    protected void paintComponent(Graphics g) {
        System.out.println(board);
        for (int row = board.minRow(); row <= board.maxRow(); ++row) {
            for (int col = board.minCol(); col <= board.maxCol(); ++col) {
                drawSquare(g, row, col);
                drawPiece(g, row, col);
            }
        }
    }

    int squareWidth() {return getWidth() / board.numCols();}
    int squareHeight() {return getHeight() / board.numRows();}

    void drawSquare(Graphics g, int row, int col) {
        if (board.blackSquareAt(row, col)) {
            g.setColor(squareBlack);
        } else {
            g.setColor(squareRed);
        }
        g.fillRect(col * squareWidth(), row * squareHeight(), squareWidth(), squareHeight());
    }

    void drawPiece(Graphics g, final int row, final int col) {
        board.pieceAt(row, col).ifPresent(p -> {
            g.setColor(p.getColor().color());
            g.fillOval(col * squareWidth(), row * squareHeight(), squareWidth(), squareHeight());
            g.setColor(Color.white);
            g.drawOval(col * squareWidth(), row * squareHeight(),squareWidth() - 1, squareHeight() - 1);
            if (p.isKing()) {
                g.drawOval(col * squareWidth() + squareWidth() / 4, row * squareHeight() + squareHeight() / 4,
                        squareWidth() / 2, squareHeight() / 2);
            }
        });
    }

    public int getRow(int yClick) {
        return yClick * board.numRows() / getHeight();
    }

    public int getCol(int xClick) {
        return xClick * board.numCols() / getWidth();
    }
}
