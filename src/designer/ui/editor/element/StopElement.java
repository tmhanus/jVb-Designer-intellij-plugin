package designer.ui.editor.element;

import javax.swing.*;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class StopElement extends Element {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    public static final String TYPE = "Stop";


    public StopElement(Point point) {
        super(point);
    }

    public StopElement() {
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
        if (getZoom() == 60) i = new ImageIcon(getClass().getResource("/designer/resources/Stop50.png"));
        else if (getZoom() == 80) i = new ImageIcon(getClass().getResource("/designer/resources/Stop50.png"));
        else i = new ImageIcon(getClass().getResource("/designer/resources/Stop50.png"));
        Image img = i.getImage();
        g.drawImage(img, (int) super.getPosition().getX() - ((int) (WIDTH * getZoom() / 100) / 2), (int) super.getPosition().getY() - ((int) (HEIGHT * getZoom() / 100) / 2), null);
    }

    @Override
    public boolean canAdd(String possibleChild) {
        return false;
    }

    @Override
    public boolean canAddTransitionElement(Element transitionElement) {
        return false;
    }

    @Override
    public String getType() {
        return this.TYPE;
    }
}
