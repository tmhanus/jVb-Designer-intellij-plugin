package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Created by Tomas Hanus on 4/12/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class IncludeClass {
    @XmlAttribute(name = "class")
    private String classToInclude;

    public IncludeClass() {
    }

    public IncludeClass(String classToInclude) {
        this.classToInclude = classToInclude;
    }

    public String getClassToInclude() {
        return classToInclude;
    }

    public void setClassToInclude(String classToInclude) {
        this.classToInclude = classToInclude;
    }
}