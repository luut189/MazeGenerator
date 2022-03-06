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

    public void display(Graphics g, int w) {

        if(isVisited) {
            g.setColor(new Color(129, 32, 129));
            g.fillRect(x*w, y*w, w, w);
        }
        
        g.setColor(Color.white);
        if(this.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
        if(this.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
        if(this.wall[2]) g.drawLine((x*w)+w, y*w, (x*w)+w, (y*w)+w); //right
        if(this.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
    }

    public void currentDisplay(Graphics g, int w) {
        g.setColor(new Color(85, 176, 108));
        g.fillRect(x*w, y*w, w, w);

        g.setColor(Color.white);
        if(this.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
        if(this.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
        if(this.wall[2]) g.drawLine((x*w)+w, y*w, (x*w)+w, (y*w)+w); //right
        if(this.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
    }

    public void pointDisplay(Graphics g, int w, boolean isStart) {
        g.setColor(isStart ? Color.cyan : Color.orange);
        g.fillRect(x*w, y*w, w, w);

        g.setColor(Color.white);
        if(this.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
        if(this.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
        if(this.wall[2]) g.drawLine((x*w)+w, y*w, (x*w)+w, (y*w)+w); //right
        if(this.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
    }

    public ArrayList<Cell> getWalkableNeighbor(Cell[][] grid, int row, int col) {
        ArrayList<Cell> neighbor = new ArrayList<>();
        
        Cell top = !this.wall[0] && this.y - 1 >= 0 ? grid[x][y-1] : null;
        Cell left = !this.wall[1] && this.x - 1 >= 0 ? grid[x-1][y] : null;
        Cell right = !this.wall[2] && this.x + 1 <= row-1 ? grid[x+1][y] : null;
        Cell bottom = !this.wall[3] && this.y + 1 <= col-1 ? grid[x][y+1] : null;

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

    public void getInfo() {
        System.out.println(x + " " + y + " " + Arrays.toString(wall));
    }
}
