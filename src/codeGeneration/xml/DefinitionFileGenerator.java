package codeGeneration.xml;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import specification.definitions.Definition;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;

/**
 *  Tomas Hanus  on 4/8/2015.
 */
public class DefinitionFileGenerator {
    private Project project;
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Document document;
    private VirtualFile virtualFile;
    private File file;

    public DefinitionFileGenerator(Project project, File file) {
        this.project = project;
        this.file = file;

        if (this.virtualFile == null)
            this.document = null;
        else this.document = FileDocumentManager.getInstance().getDocument(this.virtualFile);

        initJAXBContext();
    }

    /**
     * Serialize diagramDefinition into .jsd file
     */
    public void marshal(Definition diagramDefinition) throws JAXBException {
        this.marshaller.marshal(diagramDefinition, this.file);
        LocalFileSystem.getInstance().refresh(true);
    }

    /**
     * Create diagramDefinition from it's serialized form from .jsd dile
     */
    public Definition unmarshal() throws JAXBException {
        Definition definition = null;
        try {
            definition = (Definition)this.unmarshaller.unmarshal(this.file);
        } catch (Exception e) {
        }
        return definition;
    }

    /**
     * Init JAXB context, creates marshaller and unmarshaller
     */
    public void initJAXBContext() {
        try {
            this.jaxbContext = JAXBContext.newInstance(Definition.class);
            this.marshaller = jaxbContext.createMarshaller();
            this.unmarshaller = jaxbContext.createUnmarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


}