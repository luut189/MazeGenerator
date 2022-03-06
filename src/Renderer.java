import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class Renderer extends JPanel {

    boolean isFinished = false, gotPath = false;
    int width, height, row, col;
    int size = 10;
    
    Cell[][] grid;
    Cell current, start, end;

    Stack<Cell> cellStack = new Stack<>();
    ArrayList<Cell> neighbors, closedSet, openSet, path;
    
    Renderer(int width, int height) {
        Random rand = new Random();
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

        this.start = grid[0][0];
        this.end = grid[rand.nextInt(row)][rand.nextInt(col)];

        createMaze();
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
                    isFinished = true;
                    current = start;
                    findPath();
                }
            }
        });
        t.start();
    }

    public void findPath() {
        closedSet = new ArrayList<>();
        openSet = new ArrayList<>();
        path = new ArrayList<>();

        openSet.add(start);

        Timer t = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(openSet.size() > 0) {
                    int winner = 0;
                    for(int i = 0; i < openSet.size(); i++) {
                        if(openSet.get(i).f < openSet.get(winner).f) {
                            winner = i;
                        }
                    }
                    current = openSet.get(winner);

                    if(current == end) {
                        ((Timer)e.getSource()).stop();
                        System.out.println("Finished");
                        repaint();
                        gotPath = true;
                        tracePath();
                    }

                    openSet.remove(current);
                    closedSet.add(current);

                    neighbors = current.getWalkableNeighbor(grid, row, col);
                    for(int i = 0; i < neighbors.size(); i++) {
                        Cell neighbor = neighbors.get(i);

                        if(!closedSet.contains(neighbor)) {
                            int tempG = current.g + heuristic(neighbor, current);
                            boolean newPath = false;
                            if(openSet.contains(neighbor)) {
                                if(tempG < neighbor.g) {
                                    neighbor.g = tempG;
                                    newPath = true;
                                }
                            } else {
                                neighbor.g = tempG;
                                newPath = true;
                                openSet.add(neighbor);
                            }

                            if(newPath) {
                                neighbor.h = heuristic(neighbor, end);
                                neighbor.f = neighbor.g + neighbor.h;
                                neighbor.previous = current;
                            }
                        }
                    }
                } else {
                    System.out.println("No solution");
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            }

            public int heuristic(Cell a, Cell b) {
                return Math.abs(a.x-b.x) + Math.abs(a.y-b.y);
            }
            
        });
        t.start();
    }

    protected void tracePath() {
        Timer t = new Timer(0, new ActionListener() {
            Cell temp = current;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(temp.previous != null) {
                    path.add(temp.previous);
                    temp = temp.previous;
                }
                repaint();
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

        if(isFinished) {
            if(!gotPath) {
                for(int i = 0; i < neighbors.size(); i++) {
                    Cell neighbor = neighbors.get(i);
                    int x = neighbor.x;
                    int y = neighbor.y;
                    int w = size;
                    g.setColor(Color.cyan);
                    g.fillRect(x*w, y*w, w, w);
    
                    g.setColor(Color.white);
                    if(neighbor.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
                    if(neighbor.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
                    if(neighbor.wall[2]) g.drawLine((x*w)+w, y*w, (x*w)+w, (y*w)+w); //right
                    if(neighbor.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
                }
            }

            for(int i = 0; i < closedSet.size(); i++) {
                Cell closed = closedSet.get(i);
                int x = closed.x;
                int y = closed.y;
                int w = size;
                g.setColor(Color.red);
                g.fillRect(x*w, y*w, w, w);

                g.setColor(Color.white);
                if(closed.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
                if(closed.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
                if(closed.wall[2]) g.drawLine((x*w)+w, y*w, (x*w)+w, (y*w)+w); //right
                if(closed.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
            }

            start.pointDisplay(g, size, true);
            end.pointDisplay(g, size, false);
        }

        if(gotPath) {
            for(int i = 0; i < path.size(); i++) {
                Cell p = path.get(i);
                int x = p.x;
                int y = p.y;
                int w = size;
                g.setColor(Color.blue);
                g.fillRect(x*w, y*w, w, w);

                g.setColor(Color.white);
                if(p.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
                if(p.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
                if(p.wall[2]) g.drawLine((x*w)+w, y*w, (x*w)+w, (y*w)+w); //right
                if(p.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
            }

            start.pointDisplay(g, size, true);
            end.pointDisplay(g, size, false);
        }

        if(!isFinished) current.currentDisplay(g, size);
    }

}
