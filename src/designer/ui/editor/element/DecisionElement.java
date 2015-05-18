package designer.ui.editor.element;

import javax.swing.*;
import java.awt.*;

/**
 *  Created by Tomas Hanusas on 12.02.15.
 */
public abstract class DecisionElement extends Element {
    private final int WIDTH = 80;
    private final int HEIGHT = 80;
    private static final String TYPE = "Decision";


    public DecisionElement(Point point) {
        super(point);
    }

    public DecisionElement() {
        super();
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(Graphics g) {
        ImageIcon i;
        if (getZoom() == 60) i = new ImageIcon(getClass().getResource("/designer/resources/Decision48.png"));
        else if (getZoom() == 80) i = new ImageIcon(getClass().getResource("/designer/resources/Decision64.png"));
        else i = new ImageIcon(getClass().getResource("/designer/resources/Decision80.png"));
        Image img = i.getImage();
        g.drawImage(img, (int) super.getPosition().getX() - ((int) (WIDTH * getZoom() / 100) / 2), (int) super.getPosition().getY() - ((int) (HEIGHT * getZoom() / 100) / 2), null);
    }

    @Override
    public boolean canAdd(String possibleChild) {
        if (possibleChild.equals("Start")) return false;
        return true;
    }


    @Override
    public String getType() {
        return this.TYPE;
    }
}
