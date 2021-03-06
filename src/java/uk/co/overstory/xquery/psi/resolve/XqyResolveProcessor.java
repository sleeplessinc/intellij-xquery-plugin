package uk.co.overstory.xquery.psi.resolve;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.HashSet;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyFunctionDecl;
import uk.co.overstory.xquery.psi.XqyLibraryModule;
import uk.co.overstory.xquery.psi.XqyVarDecl;
import uk.co.overstory.xquery.psi.XqyVisibility;
import uk.co.overstory.xquery.refactor.XqyRefactoringSupportProvider;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/31/12
 * Time: 8:35 AM
 */
public class XqyResolveProcessor implements PsiScopeProcessor
{
	private final Set<PsiNamedElement> myProcessedElements = new java.util.HashSet<PsiNamedElement>();
	private final HashSet<ResolveResult> candidates = new HashSet<ResolveResult>();
	private final String name;
	private final PsiElement place;
	private final boolean incompleteCode;

	public XqyResolveProcessor (String name, PsiElement place, boolean incompleteCode)
	{
		this.name = name;
		this.place = place;
		this.incompleteCode = incompleteCode;
	}

	@Override
	public boolean execute (PsiElement element, ResolveState state)
	{
//System.out.println ("XqyResolveProcessor.execute: " + element.toString() + "/" + element.getText());
		if (element instanceof PsiNamedElement && !myProcessedElements.contains (element))
		{
			if ((state == null) && (sameNameSeen ((PsiNamedElement) element))) {
				return true;
			}

			PsiNamedElement namedElement = (PsiNamedElement) element;
			boolean isAccessible = isAccessible (namedElement);

			candidates.add (new XqyResolveResultImpl (namedElement, isAccessible));
			myProcessedElements.add (namedElement);

			return false;
		}

		return true;

	}


	@Override
	public <T> T getHint (Key<T> hintKey)
	{
		return null;
	}

	@Override
	public void handleEvent (Event event, @Nullable Object associated)
	{
	}

	public ResolveResult[] getCandidates()
	{
		return candidates.toArray (new ResolveResult[candidates.size()]);
	}

	// ----------------------------------------------------------

	// ToDo: verify that this is correct for this context
	@SuppressWarnings("unchecked")
	private boolean isAccessible (PsiNamedElement element)
	{
		XqyCompositeElement decl = PsiTreeUtil.getParentOfType (element, XqyVarDecl.class, XqyFunctionDecl.class);
		XqyVisibility visibility = PsiTreeUtil.findChildOfType (decl, XqyVisibility.class);

		if ((visibility != null) && ("private".equals (visibility.getText ()))) {
			return false;
		}

		return true;

	}

	private boolean sameNameSeen (PsiNamedElement element)
	{
		for (PsiNamedElement e : myProcessedElements) {
			if (sameName (element, e)) {
				return true;
			}
		}

		return false;
	}

	// This may need to be smartened up later to be namespace aware.  Should be a method on XqyNamedElement
	private boolean sameName (PsiNamedElement e1, PsiNamedElement e2)
	{
		return e1.getText().equals (e2.getText());
	}


}
