package org.spoofax.interpreter.library.language.spxlang.index;

import java.util.HashMap;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.ITermFactory;


public class SpxConstructors {

	private final HashMap<ConstructorDef , IStrategoConstructor> _knownCons;
	
	private final ITermFactory _termFactory;
	
	public SpxConstructors( ITermFactory tf){
		_termFactory = tf;
		_knownCons = new HashMap<ConstructorDef , IStrategoConstructor>();
		
		initKnownConstructors();
	}
	
	private void initKnownConstructors(){
		ConstructorDef.newInstance("ModuleDef"  ,5).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("ModuleDecl" ,3).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("SymbolDef"  ,4).index(_knownCons, _termFactory);

		ConstructorDef.newInstance("PackageDecl",2).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("ImportDecl" ,2).index(_knownCons, _termFactory);
		
		ConstructorDef.newInstance("LanguageDescriptor", 5).index(_knownCons, _termFactory);
		
		ConstructorDef.newInstance("Module" ,  1).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("Package",  1).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("QName"  ,  1).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("Locals" ,  1).index(_knownCons, _termFactory);
		
		ConstructorDef.newInstance("Globals", 0).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("Package", 0).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("Module" , 0).index(_knownCons, _termFactory);
		
		ConstructorDef.newInstance("ToCompile"  , 0).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("ToGenerate" , 0).index(_knownCons, _termFactory);
	}
	
	IStrategoConstructor indexConstructor(IStrategoConstructor ctor){
		ConstructorDef def = ConstructorDef.newInstance(ctor.getName(), ctor.getArity());
		return def.index(_knownCons, ctor);
	}
	
	static class ConstructorDef
	{
		private String _name ;
		private int _arity;

		ConstructorDef( String name , int arity) {  _name =  name ; _arity = arity; }

		static ConstructorDef newInstance( String name , int arity) {  return new ConstructorDef(name, arity); }

		private IStrategoConstructor toStrategoConstructor(ITermFactory fac) {  return fac.makeConstructor(_name, _arity);}

		IStrategoConstructor index(HashMap<ConstructorDef , IStrategoConstructor> cons , ITermFactory fac){
			return this.index(cons, this.toStrategoConstructor(fac));
		}

		IStrategoConstructor index(HashMap<ConstructorDef , IStrategoConstructor> cons , IStrategoConstructor ctor){
			cons.put(this, ctor) ;
			return ctor;
		}

		@Override
		public String toString() {
			return "ConstructorDef [_name=" + _name + ", _arity=" + _arity
			+ "]";
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + _arity;
			result = prime * result + ((_name == null) ? 0 : _name.hashCode());
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
			ConstructorDef other = (ConstructorDef) obj;
			if (_arity != other._arity)
				return false;
			if (_name == null) {
				if (other._name != null)
					return false;
			} else if (!_name.equals(other._name))
				return false;
			return true;
		}

	}

	public IStrategoConstructor getPackageDeclCon() { return getConstructor("PackageDecl",2);}
	
	public IStrategoConstructor getModuleDeclCon() { return getConstructor("ModuleDecl", 3); }

	public IStrategoConstructor getModuleDefCon() {	return getConstructor("ModuleDef" , 5);}

	public IStrategoConstructor getLanguageDescriptorCon() { return getConstructor("LanguageDescriptor" , 5);}

	public IStrategoConstructor getModuleQNameCon() {return getConstructor("Module" , 1); }

	public IStrategoConstructor getPackageQNameCon() { return getConstructor("Package" , 1);}
	
	public IStrategoConstructor getQNameCon() { return getConstructor("QName" , 1); }
	
	public IStrategoConstructor getImportDeclCon() {return getConstructor("ImportDecl",2);}
	
	public IStrategoConstructor getGlobalNamespaceTypeCon() {return getConstructor("Globals",0);}
	
	public IStrategoConstructor getPackageNamespaceTypeCon() {return getConstructor("Package",0);}
	
	public IStrategoConstructor getModuleNamespaceTypeCon() {return getConstructor("Module",0);}
	
	public IStrategoConstructor getSymbolTableEntryDefCon() {return getConstructor("SymbolDef",4);}
	
	public IStrategoConstructor getLocalNamespaceTypeCon() { return getConstructor("Locals",1);  }
	
	public IStrategoConstructor getToCompileCon(){return getConstructor("ToCompile", 0);}
	
	public IStrategoConstructor getToCodeGenerateCon(){return getConstructor("ToCodeGenerate", 0);}
	
	public IStrategoConstructor getConstructor(String symbolTypeCons, int arity) {
		return _knownCons.get(ConstructorDef.newInstance(symbolTypeCons ,arity));
	}

	public boolean hasEqualConstructor ( IStrategoAppl actual , IStrategoConstructor expected){
		
		return this.isEqualConstructor(actual.getConstructor(), expected);
	}
	
	public boolean isEqualConstructor ( IStrategoConstructor actual , IStrategoConstructor expected){
		return actual == expected;
	}
}
