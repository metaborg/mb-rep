package org.spoofax.terms.attachments;

import java.util.HashMap;
import java.util.Map;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class TermAttachmentType<T extends ITermAttachment> {
	
	private static Map<Class<?>, TermAttachmentType<?>> asyncTypes =
		new HashMap<Class<?>, TermAttachmentType<?>>();
	
	private final Class<T> type;
	
	private final IStrategoConstructor constructor;
	
	/**
	 * @param constructorName  The constructor for this attachment in term form, or null if not supported.
	 */
	protected TermAttachmentType(Class<T> type, String constructorName, int constructorArity) {
		this.type = type;
		this.constructor = new TermFactory().makeConstructor(constructorName, constructorArity);
		assert isNotOverlapping(type) : "Term attachments do not support inheritance, failed on: " + type.getName();
	}
	
	/**
	 * Sanity check: can only create attachment types that are not
	 * a superclass or subclass of another, existing attachment type.
	 */
	private boolean isNotOverlapping(Class<T> baseClass) {
		synchronized (TermAttachmentType.class) {
			for (Class<?> otherClass : asyncTypes.keySet()) {
				if (otherClass.isAssignableFrom(baseClass) || baseClass.isAssignableFrom(otherClass))
					return true;
			}
			asyncTypes.put(baseClass, this);
		}
		return false;
	}
	
	public boolean isSerializationSupported() {
		return constructor != null;
	}
	
	public final IStrategoAppl toTerm(ITermFactory factory, T attachment) {
		return factory.makeAppl(constructor, toSubterms(factory, attachment));
	}

	public final T fromTerm(IStrategoAppl term) {
		if (term.getConstructor() == constructor) {
			return fromSubterms(term.getAllSubterms());
		} else {
			return null;
		}
	}
	
	protected abstract IStrategoTerm[] toSubterms(ITermFactory factory, T attachment);
	
	protected abstract T fromSubterms(IStrategoTerm[] subterms);
	
	@Override
	public String toString() {
		return type.getName(); // (reflective call)
	}
}
