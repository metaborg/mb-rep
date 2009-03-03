package org.spoofax.interpreter.adapter.aterm;

import org.spoofax.interpreter.adapter.aterm.WrappedATerm;
import org.spoofax.interpreter.adapter.aterm.WrappedATermFactory;

import aterm.ATerm;

/**
 * A WrappedATermFactory that does not use memoization for maximal
 * sharing of term objects.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class UnsharedWrappedATermFactory extends WrappedATermFactory {
	@Override
	protected <T extends WrappedATerm> T setInterned(aterm.ATerm t, T wrapper) {
		return wrapper;
	}
	
	@Override
	protected WrappedATerm getInterned(ATerm t) {
		return null;
	}
}
