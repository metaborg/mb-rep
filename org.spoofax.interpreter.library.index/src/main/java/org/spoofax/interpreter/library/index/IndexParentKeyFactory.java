package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermTransformer;
import org.spoofax.terms.util.TermUtils;

public class IndexParentKeyFactory {
	private class ParentURITransformer extends TermTransformer {
		private final ITermFactory factory;
		private final IStrategoConstructor uriConstructor;
		public boolean transformed = false;

		public ParentURITransformer(ITermFactory factory, boolean keepAttachments) {
			super(factory, keepAttachments);
			this.factory = factory;
			this.uriConstructor = factory.makeConstructor("URI", 2);
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

		private IStrategoTerm getParentURI(IStrategoTerm uri) {
			final IStrategoTerm language = uri.getSubterm(0);
			final IStrategoList segments = (IStrategoList) uri.getSubterm(1);
			if(segments.getSubtermCount() == 0)
				return null;
			return makeURI(language, segments.tail());
		}

		private boolean isURI(IStrategoTerm term) {
			if(!TermUtils.isAppl(term))
				return false;
			final IStrategoAppl appl = (IStrategoAppl) term;
			return appl.getConstructor().equals(uriConstructor);
		}

		private IStrategoTerm makeURI(IStrategoTerm language, IStrategoList segments) {
			return factory.makeAppl(uriConstructor, language, segments);
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
