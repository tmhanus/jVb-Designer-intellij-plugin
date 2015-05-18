package designer.ui.editor.element;

import javax.swing.*;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/1/2015.
 */
public class SplitElementEnd extends Element {
    private final int WIDTH = 65;
    private final int HEIGHT = 65;
    private static final String TYPE = "SplitEnd";


    public SplitElementEnd(Point point) {
        super(point);
    }

    public SplitElementEnd() {
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
        if (getZoom() == 60) i = new ImageIcon(getClass().getResource("/designer/resources/SplitEnd65.png"));
        else if (getZoom() == 80) i = new ImageIcon(getClass().getResource("/designer/resources/SplitEnd65.png"));
        else i = new ImageIcon(getClass().getResource("/designer/resources/SplitEnd65.png"));
        Image img = i.getImage();
        g.drawImage(img, (int) super.getPosition().getX() - ((int) (WIDTH * getZoom() / 100) / 2), (int) super.getPosition().getY() - ((int) (HEIGHT * getZoom() / 100) / 2), null);
    }

    @Override
    public boolean canAdd(String possibleChild) {
        if ((getChildren() != null) && (getChildren().size() >= 1)) return false;
        if (possibleChild.equals("Start")) return false;
        if (possibleChild.equals("Stop") || possibleChild.equals("End") || possibleChild.equals("Fail")) return false;
        return true;
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

