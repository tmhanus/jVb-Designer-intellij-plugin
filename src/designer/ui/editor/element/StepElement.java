package designer.ui.editor.element;

import specification.Step;

import javax.swing.*;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 3/22/2015.
 */
public class StepElement extends Element {
    private final int WIDTH = 80;
    private final int HEIGHT = 80;
    private static final String CHUNK_ELEMENT = "Chunk";
    private static final String BATCHLET_ELEMENT = "Batchlet";

    private String type;

    public boolean isChunkOrientedStep() {
        if (this.type.equals(this.CHUNK_ELEMENT)) return true;
        else return false;
    }

    public StepElement() {
        if (((Step) this).isChunkOriented()) {
            this.type = CHUNK_ELEMENT;
        } else this.type = BATCHLET_ELEMENT;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    public StepElement(String type) {
        this.type = type;
    }

    @Override
    public void draw(Graphics g) {
        ImageIcon i;
        if (((Step) this).isChunkOriented()) {
            if (getZoom() == 60) i = new ImageIcon(getClass().getResource("/designer/resources/Chunk48.png"));
            else if (getZoom() == 80) i = new ImageIcon(getClass().getResource("/designer/resources/Chunk64.png"));
            else {
                i = new ImageIcon(getClass().getResource("/designer/resources/Chunk80.png"));
            }
        } else {
            if (getZoom() == 60) i = new ImageIcon(getClass().getResource("/designer/resources/Batchlet48.png"));
            else if (getZoom() == 80) i = new ImageIcon(getClass().getResource("/designer/resources/Batchlet64.png"));
            else i = new ImageIcon(getClass().getResource("/designer/resources/Batchlet80.png"));
        }
        Image img = i.getImage();
        g.drawImage(img, (int) super.getPosition().getX() - ((int) (WIDTH * getZoom() / 100) / 2), (int) super.getPosition().getY() - ((int) (HEIGHT * getZoom() / 100) / 2), null);
    }

    ;

    @Override
    public boolean canAdd(String possibleChild) {
        if (possibleChild.equals("End") || possibleChild.equals("Fail") || possibleChild.equals("Stop")) return true;
//        if ((getChildren() != null) && (getChildren().size() >= 1)) return false;
        if (possibleChild.equals("Start")) return false;
        return true;
    }

    @Override
    public boolean canAddTransitionElement(Element transitionElement) {
        return true;
    }

    @Override
    public String getType() {
       /* if (isChunkOrientedStep() == true) return chunkElement.getType();
        else return batchletElement.getType();*/
        return this.type;
    }
}
