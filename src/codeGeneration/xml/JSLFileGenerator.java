package codeGeneration.xml;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import specification.Job;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import java.io.*;

/**
 *  Created by Tomas Hanus on 3/25/2015.
 */
public class JSLFileGenerator {
    private Project project;
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Document document;
    private VirtualFile virtualFile;


    public JSLFileGenerator(Project project, VirtualFile virtualFile){
        this.project = project;
        this.virtualFile = virtualFile;
        this.document = FileDocumentManager.getInstance().getDocument(this.virtualFile);

        initJAXBContext();
    }

    /** Serialize jobDiagram of rootElement into it's serialized form to .jsl file */
    public void marshal(Object rootElement) throws JAXBException {
       // File file = new File(this.virtualFile.getCanonicalPath());
        final StringWriter sw = new StringWriter();
        this.marshaller.marshal(rootElement, new StreamResult(sw));
        ApplicationManager.getApplication().runWriteAction(
                new Runnable() {
                    public void run() {
                        document.setText(sw.toString());
                    }
                });
    }

    /** Creates jobDiagram structure from .jsl file */
    public Job unmarshal() throws JAXBException {
        LocalFileSystem.getInstance().refresh(true);
        StringReader reader = new StringReader(this.document.getText());
        Job job = null;

        try {
            job = (Job)this.unmarshaller.unmarshal(reader);
        }
        catch (Exception e){}
        return job;
    }

    /** Init JAXB context, creates marshaller and unmarshaller */
    public void initJAXBContext(){
        try {
            this.jaxbContext = JAXBContext.newInstance(Job.class);
            this.marshaller = jaxbContext.createMarshaller();
            this.unmarshaller = jaxbContext.createUnmarshaller();

            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
