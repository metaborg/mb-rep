package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermTransformer;

public class IndexParentKeyFactory {
	private class ParentURITransformer extends TermTransformer {
		public boolean transformed = false;

		public ParentURITransformer(ITermFactory factory, boolean keepAttachments) {
			super(factory, keepAttachments);
		}

		@Override
		public IStrategoTerm preTransform(IStrategoTerm term) {
			if(isURI(term)) {
				final IStrategoTerm parentURI = getParentURI(term);
				if(parentURI != null) {
					transformed = true;
					return parentURI;
				}
			}

			return term;
		}

		private IStrategoList getParentURI(IStrategoTerm uri) {
			final IStrategoList segments = (IStrategoList) uri.getSubterm(1);
			if(segments.getSubtermCount() == 0)
				return null;
			return segments.tail();
		}

		private boolean isURI(IStrategoTerm term) {
			if(!Tools.isTermAppl(term))
				return false;
			final IStrategoAppl appl = (IStrategoAppl) term;
			return Tools.hasConstructor(appl, "URI", 2);
		}
	}

	private final ParentURITransformer transformer;

	public IndexParentKeyFactory(ITermFactory termFactory) {
		this.transformer = new ParentURITransformer(termFactory, false);
	}

	public IStrategoTerm getParentKey(IStrategoTerm key) {
		// TODO: transformer is not thread-safe because of transformed field.
		final IStrategoTerm parentKey = transformer.transform(key);
		if(!transformer.transformed)
			return null;
		return parentKey;
	}
}
