package designer.ui.editor;

import designer.ui.editor.element.Edge;
import designer.ui.editor.element.Element;

/**
 * Created by Tomas Hanus on 3/27/2015.
 */
public class DesignerContext {

    private String contextId;
    private DesignerContext parentContext;

    public DesignerContext(String elementId, DesignerContext parentContext) {
        this.contextId = elementId;
        this.parentContext = parentContext;
    }

    public void setContextId(String newContextId) {
        this.contextId = newContextId;
    }

    public String getContextId() {
        return this.contextId;
    }

    public DesignerContext getParentContext() {
        return this.parentContext;
    }

    public boolean belongsToContext(Element element) {
        return element.getDesignerContext().getContextId().equals(this.contextId);
    }

    public boolean belongsToContext(Edge edge) {
        return edge.getDesignerContext().getContextId().equals(this.contextId);
    }
}
