package org.spoofax.interpreter.library.language.spxlang.index.data;

import static org.spoofax.interpreter.core.Tools.isTermTuple;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spoofax.interpreter.library.language.spxlang.index.INamespace;
import org.spoofax.interpreter.library.language.spxlang.index.SpxIndexUtils;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

public class SpxSymbol extends SpxBaseSymbol implements Serializable{
	
	private static final long serialVersionUID = -5293805213473800423L;
	
	private String _data;
	private NamespaceUri _namespace; // refer to the namespace uri . 
	
	//TODO : consider to add arity of the signature in the SPX Symbol . Currently
	// all the signature used in Spoofaxlang has arity 0;
	
	public SpxSymbol (IStrategoTerm id, IStrategoConstructor signature,boolean isOverridable){super(id,signature,isOverridable);}
		
	public String getDataString () {return _data;}
	
	public NamespaceUri namespaceUri() { return _namespace; }
	
	IStrategoTerm deserializedDataToTerm(ITermFactory fac , TermAttachmentSerializer serializer){ 
		return SpxIndexUtils.deserializeToTerm(fac, serializer, _data);
	}
	
	void serializerDataString(TermAttachmentSerializer serializer, IStrategoTerm data) throws IOException { 
		_data  = SpxIndexUtils.serializeToString(serializer , data);
}
	
	public IStrategoConstructor typeCons(SpxSemanticIndexFacade facade){
		return facade.getCons().getConstructor( getSignatureString() , 0);
	}
	
	public void setNamespace(NamespaceUri id){ _namespace = id;}
	
	/**
	 * Return symbols that has type equals expectedType. In case of expectedType equals null,
	 * it returns all the symbols.
	 * 
	 * @param expectedType
	 * @param symbols
	 * @return {@link List} of {@link SpxSymbol}
	 */
	public static List<SpxSymbol> filterByType(IStrategoConstructor expectedType , Iterable<SpxSymbol> symbols){
		List<SpxSymbol> retSymbols = new ArrayList<SpxSymbol>();
		if( symbols != null){
			for(SpxSymbol s : symbols){
				if(SpxBaseSymbol.equalSignature(expectedType, s) ){ retSymbols.add(s) ;}
			}
		}
		return retSymbols;
	}

	public IStrategoTerm toTerm (SpxSemanticIndexFacade facade) throws SpxSymbolTableException{
		final ITermFactory termFactory = facade.getTermFactory();
		
		//Type 
		IStrategoConstructor spxTypeCtor = this.typeCons(facade);
		IStrategoAppl spxTypeCtorAppl = termFactory.makeAppl(spxTypeCtor); 
		
		//Enclosing Namespace 
		INamespace symbolNamespace = this.namespaceUri().resolve(facade.getPersistenceManager().spxSymbolTable());
		IStrategoAppl nsQNameAppl = symbolNamespace.toTypedQualifiedName(facade);
		
		//Data
		IStrategoTerm deserializedDataToTerm = this.deserializedDataToTerm(termFactory, facade.getTermAttachmentSerializer());
		//deserializedDataToTerm = forceImploderAttachment( deserializedDataToTerm , symbolNamespace.getAbosoluteFilePath());
		
		
		//ID/Key 
		IStrategoTerm id = this.Id(termFactory); //TODO : It might require term conversion.

		IStrategoConstructor symbolSortCtor = this.isOverridable()? facade.getCons().getOverridableSymbolTypeCon() 
													              : facade.getCons().getUniqueSymbolTypeCon();
				
		return (IStrategoTerm) termFactory.makeAppl(
				facade.getCons().getSymbolTableEntryDefCon(), 
				nsQNameAppl, // ns qname
				id, // id
				spxTypeCtorAppl, // signature
				deserializedDataToTerm, // symbol def  
				termFactory.makeAppl(symbolSortCtor) // type of symbol = unique or overridable
			);
	}
	
	private IStrategoTerm forceImploderAttachment ( IStrategoTerm term  , String uri){
		
		ImploderAttachment attach = ImploderAttachment.get(term);
		
		if( attach != null){
			term.removeAttachment(ImploderAttachment.TYPE);
			ImploderAttachment.putImploderAttachment(term, isListOrTuple(term), attach.getSort(), attach.getLeftToken(), attach.getRightToken());
		}
		else
		{	
			term.putAttachment(ImploderAttachment.createCompactPositionAttachment(uri, 0, 0, 0, -1));
		}
		return term;
	}
	
	private static boolean isListOrTuple(IStrategoTerm term) {
		return term.isList() || isTermTuple(term);
	}
	
	public static IStrategoTerm toTerms(SpxSemanticIndexFacade facade , Collection<SpxSymbol> symbols) throws SpxSymbolTableException{
		IStrategoList result = facade.getTermFactory().makeList();
		
		if( symbols != null){
			Object[] arrSymbols = symbols.toArray() ;
			for( int i = arrSymbols.length-1 ; i>= 0  ; i--) { 
				result = facade.getTermFactory().makeListCons(
						((SpxSymbol) arrSymbols[i]).toTerm(facade), result);
			}
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
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxSymbol [ Id : " + this.getId() + " Type= " + this.getSignatureString() + ", Namespace=" + _namespace + "value = "+ this._data +"]";
	}
	
	public String printSymbol(SpxSemanticIndexFacade f) throws IOException{
		return  SpxIndexUtils.getCsvFormatted(this.getSignatureString()) 
				+ ", "
				+ SpxIndexUtils.getCsvFormatted( SpxIndexUtils.termToString(this.deserializedDataToTerm(f.getTermFactory(), f.getTermAttachmentSerializer()) ));
	}
	
}