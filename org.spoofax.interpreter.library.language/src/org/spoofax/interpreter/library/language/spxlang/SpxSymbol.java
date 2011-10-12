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
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

public class SpxSymbol extends SpxBaseSymbol implements Serializable{
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
		return "SpxSymbol [ Id : " + this.getId() + " Type= " + _type + ", Namespace=" + _namespace + "]";
	}
}