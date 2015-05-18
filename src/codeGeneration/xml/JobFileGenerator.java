package codeGeneration.xml;

import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.util.IncorrectOperationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 *  Created by Tomas Hanus on 5/10/2015.
 */
public class JobFileGenerator {
    private Project project;
    private VirtualFile finalJobVirtualFile;


    public JobFileGenerator(Project project) {
        this.project = project;
    }


    /**
     * Creates final batch file in directory META-INF/batch-jobs. Convert file .jsl by skipping attribute id for elements Stop, Fail and End.
     *
     * @param virtualFile
     * @param jobId
     */
    public void generate(final VirtualFile virtualFile, final String jobId) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputStream stream = new ByteArrayInputStream(FileDocumentManager.getInstance().getDocument(virtualFile).getText().getBytes(StandardCharsets.UTF_8));
        Document doc = null;
        try {
            doc = docBuilder.parse(stream);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc == null) return;

        NodeList stops = doc.getElementsByTagName("stop");
        NodeList ends = doc.getElementsByTagName("end");
        NodeList fails = doc.getElementsByTagName("fail");

        if (stops != null) {
            for (int i = 0; i < stops.getLength(); i++) {
                ((Element) stops.item(i)).removeAttribute("id");
            }
        }
        if (ends != null) {
            for (int i = 0; i < ends.getLength(); i++) {
                ((Element) ends.item(i)).removeAttribute("id");
            }
        }
        if (fails != null) {
            for (int i = 0; i < fails.getLength(); i++) {
                ((Element) fails.item(i)).removeAttribute("id");
            }
        }


        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();

            transformer.transform(source, new StreamResult(writer));

            final String sss = writer.getBuffer().toString().replace("\r\n", "\n");

            PsiPackage pack = JavaPsiFacade.getInstance(project).findPackage("");
            PsiDirectory[] psiDirectories = pack.getDirectories();
            PsiDirectory basePsiDirectory = null;

            if (psiDirectories != null && psiDirectories.length != 0) {
                for (PsiDirectory psiDirectory : psiDirectories) {
                    if (psiDirectory.getName().equals("src")) basePsiDirectory = psiDirectory;
                }
            }

            final PsiDirectory finalBasePsiDirectory = basePsiDirectory;
            ApplicationManager.getApplication().runWriteAction(
                    new Runnable() {
                        public void run() {
                            Module module = ProjectFileIndex.SERVICE.getInstance(project).getModuleForFile(virtualFile);
                            PsiDirectory psiDir = PackageUtil.findOrCreateDirectoryForPackage(module, "META-INF.batch-jobs", finalBasePsiDirectory, false, false);
                            PsiFile psiFile = null;
                            String jobFileName = jobId + ".xml";
                            try {
                                psiDir.checkCreateFile(jobFileName);
                                psiFile = psiDir.createFile(jobFileName);
                            } catch (IncorrectOperationException e) {
                                // If exception was thrown, it means that file already exists
                                if (psiDir.getFiles() != null) {
                                    for (PsiFile file : psiDir.getFiles()) {
                                        if (file.getName().equals(jobFileName)) psiFile = file;
                                    }
                                }
                            }
                            FileDocumentManager.getInstance().getDocument(psiFile.getVirtualFile()).setText(sss);
                        }
                    });
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
