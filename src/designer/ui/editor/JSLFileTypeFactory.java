package designer.ui.editor;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 *  Created by Tomas Hanus on 4/21/2015.
 */
public class JSLFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(JSLFileType.INSTANCE, "jsl");
    }
}
