import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class Renderer extends JPanel {

    boolean isFinished = false, gotPath = false;
    int width, height, row, col;
    int size = 5;
    
    Cell[][] grid;
    Cell current, start, end;

    Stack<Cell> cellStack = new Stack<>();
    ArrayList<Cell> neighbors, closedSet, openSet, path;

    boolean withAnimation = false, livePathfinding = false;
    
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
        if(withAnimation) {
            // maze with animation
            Timer t = new Timer(0, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Cell next = current.getNeighbor(grid, row, col);
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
        } else {
            // make maze without animation
            while(!allCellVisited()) {
                Cell next = current.getNeighbor(grid, row, col);
                if(next != null) {
                    next.isVisited = true;
                    cellStack.push(current);
                    removeWall(current, next);
                    current = next;
                } else if(cellStack.size() > 0) {
                    current = cellStack.pop();
                }
            }
            repaint();
            isFinished = true;
            current = start;
            findPath();
        }
    }

    public void findPath() {
        closedSet = new ArrayList<>();
        openSet = new ArrayList<>();
        if(!livePathfinding) path = new ArrayList<>();

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
                        if(!livePathfinding) tracePath();
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

                    /* live path finding */
                    if(livePathfinding) {
                        path = new ArrayList<>();
                        Cell temp = current;
                        while(temp.previous != null) {
                            path.add(temp.previous);
                            temp = temp.previous;
                        }
                    }
                    
                } else {
                    System.out.println("No solution");
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            }

            private int heuristic(Cell a, Cell b) {
                return Math.abs(a.x-b.x) + Math.abs(a.y-b.y);
            }
            
        });
        t.start();
    }

    private void tracePath() {
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
                /* doesn't seem to be necessary
                for(int i = 0; i < neighbors.size(); i++) {
                    Cell neighbor = neighbors.get(i);
                    neighbor.displayCell(g, size, Color.cyan);
                }
                */
            }

            for(int i = 0; i < closedSet.size(); i++) {
                Cell closed = closedSet.get(i);
                closed.displayCell(g, size, Color.red);
            }

            /* for live path finding */
            if(livePathfinding) {
                int x = 0;
                boolean isReverse = false;
                for(int i = 0; i < path.size(); i++) {
                    Cell p = path.get(i);
                    p.displayCell(g, size, new Color(0, x, 255));
                    if(isReverse) {
                        x--;
                        if(x == 0) isReverse = false;
                    } else {
                        x++;
                        if(x == 255) isReverse = true;
                    }
                }
            }

            start.pointDisplay(g, size, true);
            end.pointDisplay(g, size, false);
        }
        
        // for path tracing
        if(gotPath && !livePathfinding) {
            int x = 0;
            boolean isReverse = false;
            for(int i = 0; i < path.size(); i++) {
                Cell p = path.get(i);
                p.displayCell(g, size, new Color(0, x, 255));
                if(isReverse) {
                    x--;
                    if(x == 0) isReverse = false;
                } else {
                    x++;
                    if(x == 255) isReverse = true;
                }
            }

            start.pointDisplay(g, size, true);
            end.pointDisplay(g, size, false);
        }

        if(!isFinished) current.displayCell(g, size, new Color(85, 176, 108));
    }

}
