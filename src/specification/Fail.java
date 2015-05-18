package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Fail {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String on;
    @XmlAttribute(name = "exit-status")
    private String exitStatus;

    public Fail() {
    }

    public Fail(String id, String exitStatus) {
        this.id = id;
        this.exitStatus = exitStatus;
        this.on = new String("");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(String exitStatus) {
        this.exitStatus = exitStatus;
    }
}
