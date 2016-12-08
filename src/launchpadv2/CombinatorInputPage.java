package launchpadv2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.window.Window;

import org.eclipse.ui.dialogs.SelectionStatusDialog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;

import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;

public class CombinatorInputPage extends UserInputWizardPage {

	Text fNameField;

	//Combo fTypeCombo;

	public CombinatorInputPage(String name) {
		super(name);
	}

	public void createControl(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);

		setControl(result);

		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);

		Label label= new Label(result, SWT.NONE);
		label.setText("&Combinator name:");

		fNameField= createNameField(result);

		label= new Label(result, SWT.NONE);
		label.setText("&Declaring class:");

		Composite composite= new Composite(result, SWT.NONE);
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//fTypeCombo= createTypeCombo(composite);
		//fTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button browseButton= new Button(composite, SWT.PUSH);
		browseButton.setText("&Browse...");
		GridData data= new GridData();
		data.horizontalAlignment= GridData.END;
		browseButton.setLayoutData(data);

		final Button referenceButton= new Button(result, SWT.CHECK);
		referenceButton.setText("&Update references");
		data= new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan= 2;
		data.verticalIndent= 2;
		referenceButton.setLayoutData(data);

		final CombinatorRefactoring refactoring= getCombinatorRefactoring();
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

		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				/*
				IType type= selectDeclaringType();
				if (type == null)
					return;
				*/
				
				try {
					//String sourceFileName = fNameField.getText() + ".java";
					
					// Create the source file
					String sourceFileName = "C:/Users/kmtran/Desktop/runtime-New_configuration/Example/src/TestPackage/A.java";
					File sourceFile = new File(sourceFileName);
					
					/*
					String targetDirectoryName = "C:/Users/kmtran/Desktop/runtime-New_configuration/Example/src/TestPackage/testDirectory";
					File targetDirectory = new File(targetDirectoryName);
					targetDirectory.mkdir();
					*/
					//new File("C:/Users/kmtran/Desktop/runtime-New_configuration/Example/src/TestPackage/testDirectory").mkdir();
					/*
					Path path = Paths.get("C:/Users/kmtran/Desktop/runtime-New_configuration/Example/src/TestPackage/testDirectory");
					Files.createDirectories(path);
					*/
					
					// Create the target file
					String targetFileName = "C:/Users/kmtran/Desktop/runtime-New_configuration/Example/testDirectory/A.java";
					File targetFile = new File(targetFileName);
					
					// Create the directory if it does not exist
					targetFile.getParentFile().mkdirs();
					
					// Copy the source file to the target file
					Files.copy(sourceFile.toPath(), targetFile.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//fTypeCombo.setText(type.getFullyQualifiedName());
			}
		});

		referenceButton.setSelection(true);

		fNameField.setFocus();
		fNameField.selectAll();
		handleInputChanged();
	}

	private Text createNameField(Composite result) {
		Text field= new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
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
		IJavaProject project= getCombinatorRefactoring().getMethod().getJavaProject();

		IJavaElement[] elements= new IJavaElement[] { project};
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);

		try {
			SelectionStatusDialog dialog= (SelectionStatusDialog) JavaUI.createTypeDialog(getShell(), getContainer(), scope, IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS, false);

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