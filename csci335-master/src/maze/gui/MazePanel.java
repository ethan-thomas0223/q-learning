package maze.gui;
import java.awt.*;

import javax.swing.*;

import core.Direction;
import core.Pos;
import maze.core.Maze;
import maze.core.MazePath;

@SuppressWarnings("serial")
public class MazePanel extends JPanel {
    private Maze m;
    private MazePath mp;
    
    public MazePanel() {
        super();
        setBackground(Color.white);
        m = null;
        mp = null;
    }
    
    public void setMaze(Maze m) {
        this.m = m;
        mp = null;
    }
    
    public void setPath(MazePath mp) {
        this.mp = mp;
    }
    
    public double getXUnit() {
    	return (double)getWidth() / (double)m.getXSize();
    }
    
    public double getYUnit() {
    	return (double)getHeight() / (double)m.getYSize();
    }
    
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        g.drawRect(0, 1, getWidth() - 1, getHeight() - 1);
        if (m == null) {return;}
        
        drawMaze(g);
        drawMazePath(g);
    }
    
    private void drawMaze(Graphics g) {
        int xFill = (int)(getXUnit()*2)/3;
        int yFill = (int)(getYUnit()*2)/3;
        int xIndent = (int)(getXUnit() - xFill)/2;
        int yIndent = (int)(getYUnit() - yFill)/2;
        
        for (int x = 0; x < m.getXSize(); ++x) {
            for (int y = 0; y < m.getYSize(); ++y) {
                g.setColor(Color.black);
                Pos c = new Pos(x, y);
                int xBase = (int)((double)x * getXUnit());
                int yBase = (int)((double)y * getYUnit());
                
                drawCellOutline(g, c, xBase, yBase);
                drawSpecialSquares(g, c, xBase + xIndent, yBase + yIndent, xFill, yFill);
            }
        }    	
    }
    
    private void drawCellOutline(Graphics g, Pos c, int xBase, int yBase) {
    	int xNext = (int)(xBase + getXUnit());
    	int yNext = (int)(yBase + getYUnit());
        if (m.blocked(c, Direction.N)) {
            g.drawLine(xBase, yBase, xNext, yBase);
        }
        if (m.blocked(c, Direction.W)) {
            g.drawLine(xBase, yBase, xBase, yNext);
        }
        if (m.blocked(c, Direction.S)) {
            g.drawLine(xBase, yNext, xNext, yNext);
        }
        if (m.blocked(c, Direction.E)) {
            g.drawLine(xNext, yBase, xNext, yNext);
        }
    }
    
    private void drawSpecialSquares(Graphics g, Pos c, int x, int y, int xFill, int yFill) {
        if (m.isStart(c)) {
            g.setColor(Color.yellow);
            g.fillRect(x, y, xFill, yFill);
        } else if (m.isEnd(c)) {
            g.setColor(Color.blue);
            g.fillRect(x, y, xFill, yFill);
        } else if (m.isTreasure(c)) {
            g.setColor(new Color(0, 255, 255));
            g.fillRect(x, y, xFill, yFill);
        }
    }
    
    private void drawMazePath(Graphics g) {
    	if (mp == null) {return;}
        g.setColor(Color.red);
        int xFill = (int)(getXUnit()*3)/8;
        int yFill = (int)(getYUnit()*3)/8;
        int xIndent = (int)(getXUnit() - xFill)/2;
        int yIndent = (int)(getYUnit() - yFill)/2;
        for (int i = 0; i < mp.getLength(); ++i) {
            Pos p = mp.getNth(i);
            if (i == mp.getLength() - 1) {
                g.setColor(Color.green);
            }
            g.fillRect((int)(p.getX() * getXUnit()) + xIndent,
                       (int)(p.getY() * getYUnit()) + yIndent,
                       xFill, yFill);
        }
    }
}
