package designer.ui.editor.element;

import javax.swing.*;
import java.awt.*;

/**
 *  Created by Tomas Hanusas on 09.02.15.
 */
public class StartElement extends Element {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private static final String TYPE = "Start";

    private Point point;


    public StartElement() {
        super();
    }

    public StartElement(Point point) {
        super(point);
        this.point = point;
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
        if (getZoom() == 60) i = new ImageIcon(getClass().getResource("/designer/resources/Start50.png"));
        else if (getZoom() == 80) i = new ImageIcon(getClass().getResource("/designer/resources/Start50.png"));
        else i = new ImageIcon(getClass().getResource("/designer/resources/Start50.png"));
        Image img = i.getImage();

        g.drawImage(img, (int) super.getPosition().getX() - ((int) (WIDTH * getZoom() / 100) / 2), (int) super.getPosition().getY() - ((int) (HEIGHT * getZoom() / 100) / 2), null);
    }

    @Override
    public boolean canAdd(String possibleChild) {
        if ((getChildren() != null) && (getChildren().size() >= 1)) return false;
        if (possibleChild.equals("Stop") || possibleChild.equals("End") || possibleChild.equals("Fail")) return false;
        if (possibleChild.equals("Decision") || possibleChild.equals("Start")) return false;
        return true;
    }

    @Override
    public String getType() {
        return this.TYPE;
    }

}

