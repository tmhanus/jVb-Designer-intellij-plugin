package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.*;

/**
 *  Created by Tomas Hanus on 4/11/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PartitionMapper implements Cloneable, Serializable {
    @XmlAttribute
    private String ref;

    @XmlElement
    private Properties properties;

    public PartitionMapper() {

    }

    public PartitionMapper(PartitionMapper partitionMapper) {
        if (partitionMapper == null) return;

        this.ref = new String(partitionMapper.getRef());
        if (partitionMapper.getProperties() == null)
            this.properties = null;
        else
            this.properties = new Properties(partitionMapper.getProperties().getProperties());
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public PartitionMapper deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (PartitionMapper) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        PartitionMapper cloned = (PartitionMapper) super.clone();
        cloned.setRef(cloned.getRef());
        // the above is applicable in case of primitive member types,
        // however, in case of non primitive types
        // cloned.setNonPrimitiveType(cloned.getNonPrimitiveType().clone());
        return cloned;
    }

    public String toString() {
        return "ref:" + ref + ", properties:" + properties;
    }


}
