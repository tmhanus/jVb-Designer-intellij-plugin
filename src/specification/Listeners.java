package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Listeners {
    @XmlElements({
            @XmlElement(name = "listener", type = Listener.class)
    })
    private List<Listener> listeners;

    public Listeners() {
    }

    public Listeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public void addNewListener(String ref) {
        if (this.listeners == null) this.listeners = new ArrayList<Listener>();
        this.listeners.add(new Listener(ref));
    }

    public void removeListener(Listener listener) {
        if (this.listeners == null) return;
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    public List<Listener> getListeners() {
        return listeners;
    }

    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

}
