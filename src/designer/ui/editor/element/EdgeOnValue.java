package designer.ui.editor.element;

import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/17/2015.
 */
public class EdgeOnValue extends Edge {
    private String on;

    public EdgeOnValue(Element parent, Element child) {
        super(parent, child);
    }

    public EdgeOnValue(Element parent, Point tmpChildrenPoint) {
        super(parent, tmpChildrenPoint);
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }
}
