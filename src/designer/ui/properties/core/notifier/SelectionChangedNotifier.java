package designer.ui.properties.core.notifier;

import com.intellij.util.messages.Topic;
import designer.ui.editor.JSLCanvas;

/**
 *  Created by Tomas Hanus on 3/1/2015.
 */
public interface SelectionChangedNotifier {

    Topic<SelectionChangedNotifier> CHANGE_ACTION_TOPIC = Topic.create("SelectionChangedTopic", SelectionChangedNotifier.class);

    void selectionChanged(Object selectedObject, JSLCanvas jslCanvas);

//    void beforeAction(String txt);
//    void afterAction(String txt);
}