package launchpadv2;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

public class CombinatorRefactoringContribution extends RefactoringContribution {

	@Override
	public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment, Map arguments, int flags) {
		return new CombinatorDescriptor(project, description, comment, arguments);
	}

	@Override
	public Map retrieveArgumentMap(RefactoringDescriptor descriptor) {
		if (descriptor instanceof CombinatorDescriptor)
			return ((CombinatorDescriptor) descriptor).getArguments();
		return super.retrieveArgumentMap(descriptor);
	}
}