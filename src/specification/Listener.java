package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Listener {
    @XmlAttribute
    private String ref;

    public Listener() {
    }

    public Listener(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

}
