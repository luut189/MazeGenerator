import javax.swing.JFrame;

public class Frame extends JFrame {

    Renderer panel;
    
    Frame(String title, int width, int height) {
        panel = new Renderer(width, height);

        this.setTitle(title);
        this.add(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

}
