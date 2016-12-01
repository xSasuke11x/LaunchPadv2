package launchpadv2;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class CombinatorWizard extends RefactoringWizard {

	public CombinatorWizard(CombinatorRefactoring refactoring, String pageTitle) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(pageTitle);
	}

	@Override
	protected void addUserInputPages() {
		addPage(new CombinatorInputPage("CombinatorInputPage"));
	}
}