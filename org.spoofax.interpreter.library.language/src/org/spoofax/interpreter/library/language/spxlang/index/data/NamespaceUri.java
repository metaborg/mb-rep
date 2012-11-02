package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.IOException;
import java.io.Serializable;

import org.spoofax.interpreter.library.language.spxlang.index.INamespace;
import org.spoofax.interpreter.library.language.spxlang.index.INamespaceResolver;
import org.spoofax.interpreter.library.language.spxlang.index.SpxIndexUtils;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Md. Adil Akhter
 * 
 */
public class NamespaceUri implements Serializable, Comparable<NamespaceUri>
{
	private static final long serialVersionUID = 7219193145612008432L;
	private transient IStrategoList _id;
	private final String _spxID;
	private final String _formattedStringID;
	
	
	public NamespaceUri(IStrategoList id){
		_id = id ;
		_spxID = toSpxID(_id).trim(); // TODO : consider using formatted String ID 
		_formattedStringID = SpxIndexUtils.listToString(id, ".");
	}
	
	
	public int compareTo(NamespaceUri o) {
		return this._spxID.compareTo(o._spxID);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamespaceUri other = (NamespaceUri) obj;
		if (_spxID== null) {
			if (other._spxID!= null)
				return false;
		} else if (!_spxID.equals(other._spxID))
			return false;
		return true;
	}
	
	public boolean equalSpoofaxId(ITermFactory f, IStrategoList spoofaxUri){
		return toSpxID(spoofaxUri).equals(_spxID);
	}
	
	public boolean equalSpoofaxId(String spoofaxUri){
		return _spxID.equals(spoofaxUri);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_spxID == null) ? 0 : _spxID.hashCode());
		return result;
	}
	
	public String id(){	return _spxID ;	}
	public INamespace resolve(INamespaceResolver sTable) throws SpxSymbolTableException {
		INamespace retNamespace = sTable.resolveNamespace(this._spxID);
		
		
		if(retNamespace == null) {
			throw new SpxSymbolTableException("Unknown Namespace Uri " +this.toString()+ ". Namespace can not be resolved from symbol-table") ;
		}
		return retNamespace;
	}
	
	public IStrategoList strategoID( ITermFactory f) { 
		if(_id ==null)
			_id = NamespaceUri.toStrategoID(f, this._spxID) ;
		return _id;
	}

	@Override
	public String toString() {
		return _spxID ;
	}

	// TODO refactor to utils class + make is static 
	public static String toSpxID(IStrategoList id) {
		try {
			return SpxIndexUtils.termToString(id);
		} catch (IOException e) {
			return "";
		}
	}

	public static IStrategoList toStrategoID(ITermFactory termFactory , String spxID) {
		return (IStrategoList) SpxIndexUtils.stringToTerm(termFactory, spxID);
	}


	/**
	 * @return the formattedStringID
	 */
	public String getFormattedStringID() {
		return _formattedStringID;
	}
}
