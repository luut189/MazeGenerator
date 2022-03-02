import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

public class Cell {
    
    int x, y;
    boolean isVisited = false;
    boolean[] wall = {true, true, true, true};

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void display(Graphics g, int w) {

        if(isVisited) {
            g.setColor(new Color(129, 32, 129));
            g.fillRect(x*w, y*w, w, w);
        }
        
        g.setColor(Color.white);
        if(this.wall[0]) g.drawLine(x*w, y*w, (x*w)+w, y*w); //top
        if(this.wall[1]) g.drawLine(x*w, y*w, x*w, (y*w)+w); //left
        if(this.wall[2]) g.drawLine((x*w)+w, y, (x*w)+w, (y*w)+w); //rigth 
        if(this.wall[3]) g.drawLine(x*w, (y*w)+w, (x*w)+w, (y*w)+w); //bot
    }

    public void currentDisplay(Graphics g, int w) {
        g.setColor(new Color(85, 176, 108));
        g.fillRect(x*w, y*w, w, w);
    }

    public void getInfo() {
        System.out.println(x + " " + y + " " + Arrays.toString(wall));
    }
}
