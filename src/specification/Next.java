package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Created by Tomas Hanus on 2/22/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Next {
    @XmlAttribute
    private String on;
    @XmlAttribute
    private String to;

    public Next(String on, String to) {
        if (on == null) this.on = new String("");
        else this.on = on;
        this.to = to;
    }

    public Next() {
        this.on = "";
        this.to = "";
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
