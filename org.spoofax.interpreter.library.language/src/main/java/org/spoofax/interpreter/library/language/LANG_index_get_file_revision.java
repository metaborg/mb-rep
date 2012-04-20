package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_get_file_revision extends AbstractPrimitive {
	private static String NAME = "LANG_index_get_file_revision";
	
	private final SemanticIndexManager index;
	
	public LANG_index_get_file_revision(SemanticIndexManager index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
			throws InterpreterException {
		if (isTermTuple(tvars[0]) || isTermString(tvars[0])) {
			ISemanticIndex ind = index.getCurrent();
			SemanticIndexFileDescriptor fileDescriptor = ind.getFileDescriptor(tvars[0]);
			SemanticIndexFile file = ind.getFile(fileDescriptor);
			long revision = file.getRevision();
			env.setCurrent(env.getFactory().makeInt((int)revision));
			return true;
		} else {
			return false;
		}
	}
}
