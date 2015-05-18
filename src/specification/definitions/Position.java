package specification.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *  Created by Tomas Hanus on 4/8/2015.
 */

@XmlAccessorType(XmlAccessType.NONE)
public class Position {
    @XmlElement(required = true)
    private int x;
    @XmlElement(required = true)
    private int y;

    // *****************************************************************
    // *                      CONSTRUCTORS                             *
    // *****************************************************************

    public Position() {

    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
