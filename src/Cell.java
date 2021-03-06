import java.awt.*;
import java.util.*;

public class Cell {
    
    int x, y, f, h, g;
    boolean isVisited = false;
    boolean[] wall = {true, true, true, true};
    Cell previous;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;

        this.f = 0;
        this.g = 0;
        this.h = 0;

        previous = null;
    }

    public void drawWall(Graphics g, int w) {
        g.setColor(Color.white);
        if(this.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
        if(this.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
        if(this.wall[2]) g.drawLine((x*w)+w, y*w, (x*w)+w, (y*w)+w); //right
        if(this.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
    }

    public void display(Graphics g, int w) {
        if(isVisited) {
            this.displayCell(g, w, new Color(129, 32, 129));
        }
        drawWall(g, w);
    }

    public void pointDisplay(Graphics g, int w, boolean isStart) {
        g.setColor(isStart ? Color.cyan : Color.orange);
        g.fillRect(x*w, y*w, w, w);
        drawWall(g, w);
    }

    public void displayCell(Graphics g, int w, Color color) {
        g.setColor(color);
        g.fillRect(x*w, y*w, w, w);
        drawWall(g, w);
    }

    public ArrayList<Cell> getWalkableNeighbor(Cell[][] grid, int row, int col) {
        ArrayList<Cell> neighbor = new ArrayList<>();
        
        Cell top = !this.wall[0] && y - 1 >= 0 ? grid[x][y-1] : null;
        Cell left = !this.wall[1] && x - 1 >= 0 ? grid[x-1][y] : null;
        Cell right = !this.wall[2] && x + 1 <= row-1 ? grid[x+1][y] : null;
        Cell bottom = !this.wall[3] && y + 1 <= col-1 ? grid[x][y+1] : null;

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
        
        return neighbor;
    }

    public Cell getNeighbor(Cell[][] grid, int row, int col) {
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

    public void getInfo() {
        System.out.println(x + " " + y + " " + Arrays.toString(wall));
    }
}
