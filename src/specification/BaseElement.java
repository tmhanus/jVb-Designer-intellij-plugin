package specification;

import specification.interfaces.IAttrElementSpec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BaseElement implements IAttrElementSpec {

    @XmlAttribute
    private String id;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String ID) {
        this.id = ID;
    }
}
