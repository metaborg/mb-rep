package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

class BaseSymbol implements Serializable{

	private static final long serialVersionUID = 3160588874266553126L;
	
	protected transient IStrategoTerm _id;
	//protected  IStrategoTerm _id;
	protected String _idString;
	
	public BaseSymbol(IStrategoTerm id){
		_id = id ;
		_idString = id.toString(Integer.MAX_VALUE);
	
	}
	
	public IStrategoTerm Id(ITermFactory _fac ){
		if (_id == null){
			_id = _fac.parseFromString(_idString);
		}
		return _id;
	}
	
	void setId(IStrategoTerm id){_id = id;}
	
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_idString == null) ? 0 : _idString.hashCode());
		//result = prime * result + ((_idString == null) ? 0 : _id.hashCode());
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
		BaseSymbol other = (BaseSymbol) obj;
//		if (_id == null) {
//			if (other._id != null)
//				return false;
//		}else if(!verifyEquals(this._id, other._id)){ 
//			return false;
//		}
		if (_idString == null) {
			if (other._idString != null)
				return false;
		}else if( !this._idString.equals(other._idString ) ){
			return false;
		}
		return true;
	}
}

public class SpxSymbol extends BaseSymbol implements Serializable{
	private static final long serialVersionUID = -5293805213473800423L;
	
	private String _data;
	private String _type;
	private NamespaceUri _namespace; // refer to the namespace uri . 
	
	public SpxSymbol (IStrategoTerm id){super(id);}
	
	public SpxSymbol (IStrategoTerm id , String type){ 
		this(id) ;

		_type = type;
	}
	
	public String type() {
		assert _type != null : "Non-Null _type is expected. ";
		
		return _type; 
	}

	public String getDataString () {return _data;}
	
	public NamespaceUri namespaceUri() { return _namespace; }
	
	void setType(String type) {	_type = type; }

	IStrategoTerm deserializedDataToTerm(ITermFactory fac , TermAttachmentSerializer serializer) { 
		IStrategoTerm deserializedAtermWithAnnotation = fac.parseFromString(_data);
		IStrategoTerm deserializedAterm  = serializer.fromAnnotations(deserializedAtermWithAnnotation, true);
		
		return deserializedAterm;
	}
	
	void serializerDataString(TermAttachmentSerializer serializer, IStrategoTerm data) throws IOException { 
		IStrategoTerm annotatedTerm = serializer.toAnnotations(data);
		
		StringBuilder sb = new StringBuilder();
		annotatedTerm.writeAsString(sb ,Integer.MAX_VALUE);
		
		_data = sb.toString(); 
	}
	
	public IStrategoConstructor typeCons(SpxSemanticIndexFacade facade){
		return facade.getConstructor( type() , 0);
	}
	
	void setNamespace(NamespaceUri id){ _namespace = id;}
	
	boolean equalType (IStrategoConstructor term) { 
		return _type.equals(term.getName()); 
	}
	
	/**
	 * Return symbols that has type equals expectedType. In case of expectedType equals null,
	 * it returns all the symbols.
	 * 
	 * @param expectedType
	 * @param symbols
	 * @return {@link List} of {@link SpxSymbol}
	 */
	static List<SpxSymbol> filterByType(IStrategoConstructor expectedType , Iterable<SpxSymbol> symbols){
		List<SpxSymbol> retSymbols = new ArrayList<SpxSymbol>();
		if( symbols != null){
			for(SpxSymbol s : symbols){
				if( (expectedType==null) ||  s.equalType(expectedType) ){ retSymbols.add(s) ;}
			}
		}
		return retSymbols;
	}
	
	public IStrategoTerm toTerm (SpxSemanticIndexFacade facade) throws SpxSymbolTableException{
		final ITermFactory termFactory = facade.getTermFactory();
		
		//Type 
		IStrategoConstructor spxTypeCtor = this.typeCons(facade);
		IStrategoAppl spxTypeCtorAppl = termFactory.makeAppl(spxTypeCtor); 
		
		//Data
		IStrategoTerm deserializedDataToTerm = this.deserializedDataToTerm(termFactory, facade.getTermAttachmentSerializer());
		
		//Enclosing Namespace 
		IStrategoConstructor qnameCons = facade.getQNameCon();
		IStrategoAppl nsQNameAppl = this.namespaceUri().resolve(facade.persistenceManager().spxSymbolTable()).toTypedQualifiedName(facade);
		
		//ID/Key 
		IStrategoTerm id = this.Id(termFactory); //TODO : It might require term conversion.

		return (IStrategoTerm)termFactory.makeAppl( facade.getSymbolTableEntryDefCon(),
				nsQNameAppl,  //ns qname 
				spxTypeCtorAppl,  // type
				id,			  //id	
				deserializedDataToTerm )	;
	}
	
	static IStrategoTerm toTerms(SpxSemanticIndexFacade facade , Iterable<SpxSymbol> symbols) throws SpxSymbolTableException{
		IStrategoList result = facade.getTermFactory().makeList();
		
		if( symbols != null)
			for( SpxSymbol s : symbols) { 
				result = facade.getTermFactory().makeListCons(  s.toTerm(facade) , result);
			}
		
		return result;
	} 
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_data == null) ? 0 : _data.hashCode());
		result = prime * result
				+ ((_namespace == null) ? 0 : _namespace.hashCode());
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpxSymbol other = (SpxSymbol) obj;
		if (_data == null) {
			if (other._data != null)
				return false;
		} else if (!_data.equals(other._data))
			return false;
		if (_namespace == null) {
			if (other._namespace != null)
				return false;
		} else if (!_namespace.equals(other._namespace))
			return false;
		if (_type == null) {
			if (other._type != null)
				return false;
		} else if (!_type.equals(other._type))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxSymbol [ Id : " + this._idString + " Type= " + _type + ", Namespace=" + _namespace + "]";
	}
}

class SpxSymbolKey extends BaseSymbol implements Serializable{
	
	//TODO : verify it is working with two stage serialization.
	//Again easy way out is to serialize it to String and persist that 
	//in disk. 
	
	//Since we dont care about term attachments of the key, we are stripping any term attachment.
	private static final long serialVersionUID = 7804281029276443583L;
	
	public SpxSymbolKey(IStrategoTerm id){ super(id); }

	@Override
	public String toString() {
		return "SpxIndexKey {_id=" + _id +"}";
	}
}
