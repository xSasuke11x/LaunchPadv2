package launchpadv2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class CombinatorInputPage extends UserInputWizardPage {

	Text fNameField;

	//Combo fTypeCombo;

	public CombinatorInputPage(String name) {
		super(name);
	}

	public void createControl(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);

		setControl(result);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		result.setLayout(layout);

		Label label = new Label(result, SWT.NONE);
		label.setText("&Combinator name:");

		fNameField = createNameField(result);

		//label = new Label(result, SWT.NONE);
		//label.setText("&Declaring class:");

		Composite composite = new Composite(result, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//fTypeCombo= createTypeCombo(composite);
		//fTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button combinatorButton = new Button(composite, SWT.PUSH);
		combinatorButton.setText("&Create the Combinator");
		GridData data = new GridData();
		data.horizontalAlignment = GridData.END;
		combinatorButton.setLayoutData(data);

		final Button referenceButton = new Button(result, SWT.CHECK);
		referenceButton.setText("&Update references");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.verticalIndent = 2;
		referenceButton.setLayoutData(data);

		final CombinatorRefactoring refactoring = getCombinatorRefactoring();
		fNameField.setText(refactoring.getMethodName());
		//fTypeCombo.setText(refactoring.getDeclaringType().getFullyQualifiedName());

		fNameField.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				handleInputChanged();
			}
		});

		referenceButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				refactoring.setUpdateReferences(referenceButton.getSelection());
			}
		});

		/*
		fTypeCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				handleInputChanged();
			}
		});
		*/

		combinatorButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				/*
				IType type= selectDeclaringType();
				if (type == null)
					return;
				*/
				
				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IEditorPart editor = page.getActiveEditor();
				IEditorInput input = editor.getEditorInput();
				//IPath path = ((IFileEditorInput)input).getFile().getFullPath();
				IPath path = ((IFileEditorInput)input).getFile().getLocation();
				//String sourceFileName = "C:/Users/kmtran/Desktop/runtime-New_configuration" + path.toFile();
				//File sourceFile = new File(sourceFileName);
				File sourceFile = path.toFile();

				//String targetFileName = "C:/Users/kmtran/Desktop/runtime-New_configuration/Example/testDirectory/A.java";
				//IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
				//String targetFileName = workspace.getRoot().getLocation().toFile().getPath().toString() + "/testDirectory/A.java";
				// Get the root folder and set the directory name
				String targetFileName = sourceFile.getPath().toString().substring(0, sourceFile.getPath().toString().indexOf("src")) 
						+ "/testDirectory/" + fNameField.getText() + ".java";
				
				// Create the target file
				File targetFile = new File(targetFileName);
				
				// Create the directory if it does not exist
				targetFile.getParentFile().mkdirs();
				
				// Get the selected text
				IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				ITextEditor textEditor = null;
				
				if (part instanceof ITextEditor) {
					textEditor = (ITextEditor)part;
				    IDocumentProvider provider = textEditor.getDocumentProvider();
				    IDocument document = provider.getDocument(textEditor.getEditorInput());
				}
				
				if (targetFile.exists())
					targetFile.delete();
				else {
					targetFile = new File(targetFileName);
					String source = getSelectedText(textEditor);

					try {
					    FileWriter fw = new FileWriter(targetFile, false); // Set to true if you want to append instead of overwriting
					    fw.write(source);
					    fw.close();
					} catch (IOException e) {
					    e.printStackTrace();
					} 
				}
				
				//fTypeCombo.setText(type.getFullyQualifiedName());
			}
		});

		referenceButton.setSelection(true);

		fNameField.setFocus();
		fNameField.selectAll();
		handleInputChanged();
	}
	
	private ITextSelection getSelection(ITextEditor editor) {
	     ISelection selection = editor.getSelectionProvider().getSelection();
	     return (ITextSelection) selection;
	}

	private String getSelectedText(ITextEditor editor) {
	     return getSelection(editor).getText();
	}

	private Text createNameField(Composite result) {
		Text field = new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return field;
	}

	/*
	private Combo createTypeCombo(Composite composite) {
		Combo combo= new Combo(composite, SWT.SINGLE | SWT.BORDER);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setVisibleItemCount(4);
		return combo;
	}
	*/

	private CombinatorRefactoring getCombinatorRefactoring() {
		return (CombinatorRefactoring) getRefactoring();
	}

	void handleInputChanged() {
		RefactoringStatus status= new RefactoringStatus();
		CombinatorRefactoring refactoring= getCombinatorRefactoring();
		//status.merge(refactoring.setDeclaringTypeName(fTypeCombo.getText()));
		status.merge(refactoring.setMethodName(fNameField.getText()));

		setPageComplete(!status.hasError());
		int severity= status.getSeverity();
		String message= status.getMessageMatchingSeverity(severity);
		if (severity >= RefactoringStatus.INFO) {
			setMessage(message, severity);
		} else {
			setMessage("", NONE); //$NON-NLS-1$
		}
	}

	IType selectDeclaringType() {
		IJavaProject project = getCombinatorRefactoring().getMethod().getJavaProject();

		IJavaElement[] elements = new IJavaElement[] { project};
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);

		try {
			SelectionStatusDialog dialog = (SelectionStatusDialog) JavaUI.createTypeDialog(getShell(), getContainer(), scope, IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS, false);

			dialog.setTitle("Choose declaring type");
			dialog.setMessage("Choose the type where to declare the indirection method:");

			if (dialog.open() == Window.OK)
				return (IType) dialog.getFirstResult();

		} catch (JavaModelException exception) {
			RefactoringPlugin.log(exception);
		}
		return null;
	}
}