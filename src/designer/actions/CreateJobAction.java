package designer.actions;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import designer.ui.editor.JSLFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Class implements functionality of creating new .jsl in project structure. It can be done via RMBClick -> New -> Job Definition FIle.
 *  Created by Tomas Hanus on 4/21/2015.
 */
public class CreateJobAction extends CreateElementActionBase implements DumbAware {
    private static final String newFileMenuItemName = "Job Definition File";

    public CreateJobAction() {
        super(newFileMenuItemName, "Creates new " + newFileMenuItemName, null);
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory psiDirectory) {
        MyInputValidator validator = new MyInputValidator(project, psiDirectory);
        Messages.showInputDialog(project, "Enter name for new " + newFileMenuItemName, "New " + newFileMenuItemName, Messages.getQuestionIcon(), "", validator);
        return validator.getCreatedElements();
    }

    @NotNull
    @Override
    protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
        PsiElement createdFile;
        PsiClass newClass = null;
        try {
            final PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
            assert aPackage != null;

            final String fileName = newName + ".jsl";
            String newFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
            newFileContent += "<job version=\"1.0\" id=\"" + newName + "\" xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"/>";
            final PsiFile formFile = PsiFileFactory.getInstance(directory.getProject())
                    .createFileFromText(fileName, new JSLFileType(), newFileContent);
            createdFile = directory.add(formFile);

        } catch (IncorrectOperationException e) {
            throw e;
        } catch (Exception e) {
            return PsiElement.EMPTY_ARRAY;
        }

        if (newClass != null) {
            return new PsiElement[]{newClass.getContainingFile(), createdFile};
        }
        return new PsiElement[]{createdFile};
    }


    @Override
    protected String getErrorTitle() {
        return null;
    }

    @Override
    protected String getCommandName() {
        return "Create " + newFileMenuItemName;
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, String s) {
        return newFileMenuItemName;
    }


}
