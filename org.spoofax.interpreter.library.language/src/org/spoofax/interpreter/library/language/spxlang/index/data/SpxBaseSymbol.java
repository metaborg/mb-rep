package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

public abstract class SpxBaseSymbol implements Serializable{

	private static final long serialVersionUID = 3160588874266553126L;
	
	private transient IStrategoTerm _id; 
	
	/**
	 *  @serial
	 */
	// TODO : why keep it as string ? Why not IStrategoTerm? 
	// ANSWER: Problem with the serialization of the IStrategoAppl . Construction sharing .
	private final String _idString; 
	 
	private final String _signatureString;
	private final boolean _isOverridable;

	/**
	 * Returns String representation of ID
	 * 
	 * @return {@code String} representation of ID 
	 */
	public String getId(){  return _idString ; }
	
	public String getSignatureString() {
		assert _signatureString != null : "Non-Null Signature is expected. ";
		return _signatureString; 
	}

	/**
	 * @return the isOverridable
	 */
	public boolean isOverridable() {
		return _isOverridable;
	}
	

	public SpxBaseSymbol(IStrategoTerm id, IStrategoConstructor signature,boolean isOveridable){
		this._id = id ;
		
		this._idString = id.toString(Integer.MAX_VALUE);
		this._signatureString = signature.getName();
		this._isOverridable = isOveridable;
	}
	
	public IStrategoTerm Id(ITermFactory _fac){
		if (_id == null){
			_id = _fac.parseFromString(_idString);
		}
		return _id;
	}
	
	public static boolean verifyEquals(IStrategoConstructor ctor1 , IStrategoConstructor ctor2){
		if( (ctor1.getArity() == ctor2.getArity()) && (ctor1.getName().equals(ctor2.getName()))){
			return true;
		}
		return false;
	}
	
	public static boolean verifyEquals(IStrategoTerm current, IStrategoTerm other){
		boolean retValue = false;
		if ( current instanceof IStrategoAppl){
			if(other instanceof IStrategoAppl) {
				IStrategoAppl currentAppl = (IStrategoAppl)current;
				IStrategoAppl otherAppl = (IStrategoAppl)other;

				if( verifyEquals(currentAppl.getConstructor() , otherAppl.getConstructor()) )
				{
					IStrategoTerm[] currentTerms= currentAppl.getAllSubterms();
					IStrategoTerm[] otherTerms = currentAppl.getAllSubterms();

					retValue = verifyEquals(currentTerms, otherTerms);
				}	
			}
		}	
		else if( current instanceof IStrategoTuple){
			if(other instanceof IStrategoTuple) {
				retValue = verifyEquals(current.getAllSubterms(), other.getAllSubterms());
			}
		}	
		else if( current instanceof IStrategoList){
			if(other instanceof IStrategoList) {
				retValue = verifyEquals(current.getAllSubterms(), other.getAllSubterms());
			}
		}
		else
			retValue = current.match(other);

		return retValue;
	}

	public static boolean verifyEquals( IStrategoTerm[] currentTerms, IStrategoTerm[] otherTerms) {
		boolean retValue = false;
		
		if(currentTerms == null && otherTerms == null) {retValue = true;} 
		else if( currentTerms.length == otherTerms.length){
			if(currentTerms.length ==0) {retValue = true;}
			else {  
				for ( int i = 0 ; i< currentTerms.length ; i++){
					if( !verifyEquals(currentTerms[i], otherTerms[i])){
						retValue = false;
						break;
					}
					else { retValue = true;}
				}
			}
		}
		return retValue;
	}	
	
	/**
	 * @param expectedType
	 * @param s
	 * @return
	 */
	public static boolean equalSignature(IStrategoConstructor expectedType,	SpxBaseSymbol s) {
		return (expectedType==null) ||  s.equalSignature(expectedType);
	}
	
	
	// TODO FIX : bring the signature to the base class 
	public boolean equalSignature (IStrategoConstructor term) { 
		return _signatureString.equals(term.getName()); 
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_idString == null) ? 0 : _idString.hashCode());
		result = prime * result
		+ ((_signatureString == null) ? 0 : _signatureString.hashCode());

		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpxBaseSymbol other = (SpxBaseSymbol) obj;
		if (_idString == null) {
			if (other._idString != null)
				return false;
		}else if( !this._idString.equals(other._idString ) ){
			return false;
		}
		if (_signatureString == null) {
			if (other._signatureString != null)
				return false;
		} else if (!_signatureString.equals(other._signatureString))
			return false;
		return true;
	}
}
