import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class Renderer extends JPanel {

    int width, height, row, col;
    int size = 10;
    
    Cell[][] grid;
    Cell current;

    Stack<Cell> cellStack = new Stack<>();
    
    Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.row = Math.round(width/size);
        this.col = Math.round(height/size);
        this.grid = new Cell[row][col];

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.black);

        makeCells();
        this.current = grid[0][0];
        this.current.isVisited = true;
        createMaze();
    }

    public Cell tracingStep(Cell current, int x, int y) {
        Random rand = new Random();
        ArrayList<Cell> neighbor = new ArrayList<>();
        
        Cell top = !current.wall[0] && y - 1 >= 0 ? grid[x][y-1] : null;
        Cell left = !current.wall[1] && x - 1 >= 0 ? grid[x-1][y] : null;
        Cell right = !current.wall[2] && x + 1 < row-1 ? grid[x+1][y] : null;
        Cell bottom = !current.wall[3] && y + 1 < col-1 ? grid[x][y+1] : null;

        if(top != null) {
            neighbor.add(top);
        }
        if(left != null) {
            neighbor.add(left);
        }
        if(right != null) {
            neighbor.add(right);
        }
        if(bottom != null) {
            neighbor.add(bottom);
        }

        if(neighbor.size() > 0) {
            int i = rand.nextInt(neighbor.size());
            return neighbor.get(i);
        } else {
            return null;
        }
    }

    public void createMaze() {
        Timer t = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cell next = getNeighbor(current.x, current.y);
                if(next != null) {
                    next.isVisited = true;
                    cellStack.push(current);
                    removeWall(current, next);
                    current = next;
                    repaint();
                } else if(cellStack.size() > 0) {
                    current = cellStack.pop();
                    repaint();
                } else if(allCellVisited()) {
                    ((Timer) e.getSource()).stop();
                    findPath();
                }
            }
        });
        t.start();
    }

    public void findPath() {
        Timer t = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(current != grid[row-1][col-1]) {
                    Cell trace = tracingStep(current, current.x, current.y);
                    if(trace != null) {
                        cellStack.push(trace);
                        current = trace;
                        repaint();
                    }
                }
            }
        });
        t.start();
    }

    public void makeCells() {
        for(int x = 0; x < row; x++) {
            for(int y = 0; y < col; y++) {
                grid[x][y] = new Cell(x,y);
            }
        }
    }

    public boolean allCellVisited() {
        for(int x = 0; x < row; x++) {
            for(int y = 0; y < col; y++) {
                if(!grid[x][y].isVisited) {
                    return false;
                }
            }
        }
        return true;
    }

    public void removeWall(Cell a, Cell b) {
        int x = a.x - b.x;
        if(x == 1) {
            a.wall[1] = false;
            b.wall[2] = false;
        } else if(x == -1) {
            a.wall[2] = false;
            b.wall[1] = false;
        }

        int y = a.y - b.y;
        if(y == 1) {
            a.wall[0] = false;
            b.wall[3] = false;
        } else if(y == -1) {
            a.wall[3] = false;
            b.wall[0] = false;
        }
    }

    public void randomCell() {
        Random rand = new Random();
        int x = rand.nextInt(row);
        int y = rand.nextInt(col);

        current = grid[x][y];
        repaint();
    }

    public Cell getNeighbor(int x, int y) {
        Random rand = new Random();
        ArrayList<Cell> neighbor = new ArrayList<>();

        Cell top = y-1 >= 0 ? grid[x][y-1] : null;
        Cell bottom = y+1 < col ? grid[x][y+1] : null;
        Cell left = x-1 >= 0 ? grid[x-1][y] : null;
        Cell right = x+1 < row ? grid[x+1][y] : null;

        if(top != null && !top.isVisited) {
            neighbor.add(top);
        }
        if(bottom != null && !bottom.isVisited) {
            neighbor.add(bottom);
        }
        if(left != null && !left.isVisited) {
            neighbor.add(left);
        }
        if(right != null && !right.isVisited) {
            neighbor.add(right);
        }
        if(neighbor.size() > 0) {
            int i = rand.nextInt(neighbor.size());
            return neighbor.get(i);
        } else {
            return null;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for(int x = 0; x < row; x++) {
            for(int y = 0; y < col; y++) {
                grid[x][y].display(g, size);
            }
        }
        current.currentDisplay(g, size);
    }

}
