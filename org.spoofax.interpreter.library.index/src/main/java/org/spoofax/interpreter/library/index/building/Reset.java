package org.spoofax.interpreter.library.index.building;

import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexManager;

public class Reset {
	public static void main(String[] args) {
		IIndex index = IndexManager.getInstance().getIndex(args[0]);
		if(index != null)
			index.clearAll();
		else
			IndexManager.getInstance().getCurrent().clearAll();
	}
}
