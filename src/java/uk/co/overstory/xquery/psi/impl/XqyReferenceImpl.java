package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import uk.co.overstory.xquery.psi.ResolveUtil;
import uk.co.overstory.xquery.psi.XqyReference;
import uk.co.overstory.xquery.psi.resolve.XqyResolveProcessor;

import org.jetbrains.annotations.NotNull;


/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/14/12
 * Time: 4:15 PM
 */
public class XqyReferenceImpl<T extends PsiElement> extends PsiPolyVariantReferenceBase<T> implements XqyReference
{
	public XqyReferenceImpl (@NotNull T element, TextRange range)
	{
		super (element, range);
	}

	private static final MyResolver RESOLVER = new MyResolver();

	public PsiElement resolve() {
		final ResolveCache resolveCache = ResolveCache.getInstance (getProject());
		ResolveResult[] results = resolveCache.resolveWithCaching (this, RESOLVER, false, false);

		return results.length == 1 ? results[0].getElement() : null;
	}

	@NotNull
	@Override
	public Object[] getVariants ()
	{
		return new Object[0];  // FIXME: auto-generated
	}


	@NotNull
	public Project getProject() {
		return myElement.getContainingFile().getProject();
	}

	@Override
	public String getLocalname ()
	{
		return null;  // FIXME: auto-generated
	}

	@Override
	public String getNamespaceName ()
	{
		return null;  // FIXME: auto-generated
	}

	@Override
	public boolean hasNamespace ()
	{
		return false;  // FIXME: auto-generated
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve (boolean incompleteCode)
	{
		return new ResolveResult[0];  // FIXME: auto-generated
	}

	// ---------------------------------------------------------

	public static class MyResolver implements ResolveCache.PolyVariantResolver<XqyReference>
	{
		private static final ResolveResult[] EMPTY_RESULT = new ResolveResult[0];

		public ResolveResult[] resolve (XqyReference reference, boolean incompleteCode)
		{
//			final String localname = reference.getLocalname();
//
//			if (localname == null) {
//				return null;
//			}
//
//			XqyResolveProcessor processor = new XqyResolveProcessor (localname, reference, incompleteCode);
//
//			ResolveUtil.treeWalkUp (reference, processor);
//
//			if (reference.hasNamespace()) {
//				final String nsName = reference.getNamespaceName();
//
//				XqyResolveProcessor nsProcessor = new XqyResolveProcessor (nsName, reference, incompleteCode);
//
//				resolveNamespace (reference, nsProcessor);
//			}
//
//			ResolveResult[] candidates = processor.getCandidates();
//
//			if (candidates.length > 0) {
//				return candidates;
//			}

			return EMPTY_RESULT;
		}

		private void resolveNamespace (XqyReference symbol, XqyResolveProcessor processor)
		{
			// process namespaces
//			final Project project = symbol.getProject();
//			final GlobalSearchScope scope = GlobalSearchScope.allScope (project);
//			final Collection<ClNs> nses = StubIndex.getInstance ().get (ClojureNsNameIndex.KEY, symbol.getNameString (), project, scope);
//
//			for (ClNs ns : nses) {
//				ResolveUtil.processElement (processor, ns);
//			}
		}

/*
		private void resolveImpl (XqyReference symbol, XqyResolveProcessor processor)
		{
			final XqyReference qualifier = symbol.getQualifierSymbol ();

			//process other places
			if (qualifier == null) {
				ResolveUtil.treeWalkUp (symbol, processor);
			} else {
				for (ResolveResult result : qualifier.multiResolve (false)) {
					final PsiElement element = result.getElement();

					if (element != null) {
						final PsiElement sep = symbol.getSeparatorToken ();

						if (sep != null) {
							if ("/".equals (sep.getText ())) {

								//get class elements
								if (element instanceof PsiClass) {
									element.processDeclarations (processor, ResolveState.initial (), null, symbol);
								}

								//get namespace declarations
								if (element instanceof ClSyntheticNamespace) {
									final String fqn = ((ClSyntheticNamespace) element).getQualifiedName ();
									// namespace declarations
									for (PsiNamedElement named : NamespaceUtil.getDeclaredElements (fqn, element.getProject ())) {
										if (!ResolveUtil.processElement (processor, named)) {
											return;
										}
									}
								}

							} else if (".".equals (sep.getText ())) {
								element.processDeclarations (processor, ResolveState.initial (), null, symbol);
							}
						}
					}
				}
			}
		}
*/

	}


// ------------------------------------------------------------------------

// ------------------------------------------------------------------------


/*
	@Override
	public PsiElement resolve()
	{
System.out.println ("XqyReferenceImpl<T>.resolve()");
		return ResolveCache.getInstance (myElement.getProject()).resolveWithCaching (this, MY_RESOLVER, true, false);
	}

	private PsiElement resolveInner()
	{
		final Ref<PsiElement> result = Ref.create(null);
		final String text = getRangeInElement().substring (myElement.getText());
System.out.println ("resolveInner(): " + myElement.toString () + "=" + text);

		processResolveVariants(new Processor<PsiElement>()
		{
			@Override
			public boolean process(PsiElement psiElement)
			{
				if (psiElement instanceof PsiNamedElement)
				{
System.out.println ("process: name=" + ((PsiNamedElement)psiElement).getName());
					if (text.equals(((PsiNamedElement)psiElement).getName()))
					{
						result.set(psiElement);
						return false;
					}
				}

				return true;
			}
		});
		return result.get();
	}

	private static final ResolveCache.Resolver MY_RESOLVER =
		new ResolveCache.Resolver()
		{
			@Override
			public PsiElement resolve (PsiReference psiReference, boolean incompleteCode)
			{
				return ((XqyReferenceImpl)psiReference).resolveInner();
			}
		};

	// ----------------------------------------------------------------

	@NotNull
	@Override
	public Object[] getVariants()
	{
System.out.println ("XqyReferenceImpl<T>.getVariants()");
		final ArrayList<LookupElement> list = new ArrayList<LookupElement>();

		processResolveVariants (new Processor<PsiElement>()
		{
			@Override
			public boolean process(PsiElement psiElement)
			{
				if (psiElement instanceof XqyNamedElement)
				{
					LookupElementBuilder builder =
						LookupElementBuilder.create ((PsiNamedElement) psiElement).setIcon (psiElement.getIcon (Iconable.ICON_FLAG_OPEN));
					list.add (isBoldable (psiElement) ? builder.setBold() : builder);
				}

				return true;
			}
		});

		return list.toArray (new Object[list.size()]);
	}

	private boolean isBoldable (PsiElement e)
	{
		if ((e instanceof XqyVarname) || (e instanceof XqyFunctionname)) return true;

		return false;
	}

	private void processResolveVariants (final Processor<PsiElement> processor)
	{
		// FIXME: Need to return PsiElement instances for variables/functions defined in this file

System.out.println ("processResolveVariants: " + myElement.toString() + "=" + myElement.getText());

		PsiFile file = myElement.getContainingFile();

		if (!(file instanceof XqyFile)) return;

		if (myElement instanceof XqyRefVarName) {
			ContainerUtil.process (((XqyFile) file).getVariables(), processor);
		}

		if (myElement instanceof XqyFunctionname) {		// FIXME: Not unique to function call
			ContainerUtil.process (((XqyFile) file).getFunctions(), processor);
		}



//		final boolean ruleMode = myElement instanceof BnfStringLiteralExpression;
//
//		BnfAttrs attrs = PsiTreeUtil.getParentOfType (myElement, BnfAttrs.class);
//
//		if (attrs != null && !ruleMode) {
//			if (!ContainerUtil.process (attrs.getChildren (), processor)) return;
//			final int textOffset = myElement.getTextOffset();
//			ContainerUtil.process(((BnfFile)file).getAttributes(), new Processor<BnfAttrs>() {
//				@Override
//				public boolean process(BnfAttrs attrs) {
//					return attrs.getTextOffset() <= textOffset && ContainerUtil.process(attrs.getAttrList(), processor);
//				}
//			});
//		}
//		else {
//			ContainerUtil.process (((BnfFile)file).getRules(), processor);
//		}
	}
*/
}
