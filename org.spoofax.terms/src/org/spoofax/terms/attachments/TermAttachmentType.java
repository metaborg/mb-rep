package org.spoofax.terms.attachments;

import java.util.HashMap;
import java.util.Map;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class TermAttachmentType<T extends ITermAttachment> {
	
	private static Map<Class<?>, TermAttachmentType<?>> types =
		new HashMap<Class<?>, TermAttachmentType<?>>();
	
	private final Class<?> type;
	
	private TermAttachmentType(Class<?> type) {
		this.type = type;
		// Private default constructor
	}
	
	/**
	 * Creates a new attachment type. Can only create attachment types that are not
	 * a superclass or subclass of another, existing attachment type.
	 */
	public synchronized static<T extends ITermAttachment> TermAttachmentType<T> create(Class<T> baseClass) {
		@SuppressWarnings("unchecked")
		TermAttachmentType<T> result = (TermAttachmentType<T>) types.get(baseClass);
		if (result != null) {
			return result;
		} else {
			result = new TermAttachmentType<T>(baseClass);
			for (Class<?> otherClass : types.keySet()) {
				if (otherClass.isAssignableFrom(baseClass) || baseClass.isAssignableFrom(otherClass))
					throw new IllegalArgumentException("An attachment type based on class "
							+ otherClass.getName() + " already exists; cannot add " + baseClass.getName());
			}
			types.put(baseClass, result);
			return result;
		}
	}
	
	@Override
	public String toString() {
		return type.getName(); // (reflective call)
	}
}
