package designer.ui.editor.element;

import javax.swing.*;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 3/27/2015.
 */
public class FlowElement extends Element {
    private final int WIDTH = 80;
    private final int HEIGHT = 80;
    private static final String TYPE = "Flow";
    private boolean isInsideOfSplit;

    public FlowElement(Point point) {
        super(point);
    }

    public FlowElement() {
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
        if (getZoom() == 60) i = new ImageIcon(getClass().getResource("/designer/resources/Flow80.png"));
        else if (getZoom() == 80) i = new ImageIcon(getClass().getResource("/designer/resources/Flow80.png"));
        else i = new ImageIcon(getClass().getResource("/designer/resources/Flow80.png"));
        Image img = i.getImage();
        g.drawImage(img, (int) super.getPosition().getX() - ((int) (WIDTH * getZoom() / 100) / 2), (int) super.getPosition().getY() - ((int) (HEIGHT * getZoom() / 100) / 2), null);
    }

    @Override
    public boolean canAdd(String possibleChild) {
//        if ((getChildren() != null) && (getChildren().size() >= 1)) return false;
        if (isInsideOfSplit()) {
            if (possibleChild.equals(StopElement.TYPE) || possibleChild.equals(EndElement.TYPE) || possibleChild.equals(FailElement.TYPE))
                return true;
            return false;
        }
        if (possibleChild.equals("Start")) return false;
        //if (child.equals("Split")) return false;
        return true;
    }

    @Override
    public boolean canAddTransitionElement(Element transitionElement) {
        return true;
    }

    @Override
    public String getType() {
        return this.TYPE;
    }

    public boolean isInsideOfSplit() {
        return isInsideOfSplit;
    }

    public void setIsInsideOfSplit(boolean isInsideOfSplit) {
        this.isInsideOfSplit = isInsideOfSplit;
    }
}
