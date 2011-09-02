package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 * Created On : Aug 20, 2011
 */
class SpxSemanticIndexEntry {
	
	private Scope _scope; 
	
	private IStrategoTerm data;
	
	public SpxSemanticIndexEntry (Scope scope)
	{
		setScope(scope);
	}

	public Scope getScope() {
		return _scope;
	}

	private void setScope(Scope scope) {
		this._scope = scope;
	}

	/**
	 * @return the data
	 */
	protected IStrategoTerm getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	protected void setData(IStrategoTerm data) {
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IndexEntry [_scope=" + _scope + ", data=" + data
				+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_scope == null) ? 0 : _scope.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		SpxSemanticIndexEntry other = (SpxSemanticIndexEntry) obj;
		if (_scope == null) {
			if (other._scope != null)
				return false;
		} else if (!_scope.equals(other._scope))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.match(other.data))
			return false;
		return true;
	}
}


/**
 * @author Md. Adil Akhter
 * Created On : Sep 1, 2011
 */
class SpxSemanticIndexKey extends SpxSemanticIndexEntry
{	
	public SpxSemanticIndexKey(Scope scope, IStrategoTerm key)
	{
		super(scope);
	}
	
	public IStrategoTerm getKey()
	{
		return getData(); 
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxSemanticIndexKey [ " +
				"key =" + getData().toString() +
				"scope="  + getScope().toString() +
				"]";
	}
}


/**
 * @author Md. Adil Akhter
 * Created On : Sep 1, 2011
 */
class SpxSemanticIndexSymbol extends SpxSemanticIndexEntry
{
	private final IStrategoTerm _key ;

	public SpxSemanticIndexSymbol ( Scope scope, IStrategoTerm symbol, IStrategoTerm key)
	{
		super(scope);
		
		_key = key;
		setData(symbol);
	}
	
	/**
	 * @return the _key
	 */
	public IStrategoTerm getKey() {
		return _key;
	}

	public IStrategoTerm getValue()
	{
		return getData();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_key == null) ? 0 : _key.hashCode());
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
		SpxSemanticIndexSymbol other = (SpxSemanticIndexSymbol) obj;
		if (_key == null) {
			if (other._key != null)
				return false;
		} else if (!_key.match(other._key))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxSemanticIndexSymbol [ " +
				"key=" + _key.toString() + 
				"symbol=" + getData().toString() +
				"scope="  + getScope().toString() +
				"]";
	}
}

abstract class BaseConstructDeclaration implements Serializable
{
	private static final long serialVersionUID = 1055862481052307186L;
	
	protected static final int PackageType = 1;
	protected static final int ModuleType = 2;
	
	final IStrategoList id;
	
	public BaseConstructDeclaration(IStrategoList uri) {
		super();
		this.id = uri;
	}
	
	public IStrategoList getId()
	{
		//returns String representation of the BaseConstruct.
		return id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		BaseConstructDeclaration other = (BaseConstructDeclaration) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.match(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseConstructDeclaration [id=" + id + "]";
	}
}

class ModuleDeclaration extends BaseConstructDeclaration 
{
	private static final long serialVersionUID = -6249406731326662111L;
	
	final String resourceAbsPath; 
	
	final IStrategoList enclosingPackageID;
	
	public ModuleDeclaration(String resourceAbsPath, IStrategoList id , IStrategoList packageID) {
		super(id);
		
		this.resourceAbsPath = resourceAbsPath;
		this.enclosingPackageID = packageID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModuleDeclaration [ id= " + id 
				+ ", packageId ="+ enclosingPackageID 
				+ ", resourceAbsPath= " + resourceAbsPath+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((resourceAbsPath == null) ? 0 : resourceAbsPath.hashCode());
		
		result = prime * result + ((enclosingPackageID == null) ? 0 : enclosingPackageID.hashCode());
		
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
		ModuleDeclaration other = (ModuleDeclaration) obj;
		if (resourceAbsPath == null) {
			if (other.resourceAbsPath != null)
				return false;
		} else if (!resourceAbsPath.equals(other.resourceAbsPath))
			return false;
		
		if (enclosingPackageID == null) {
			if (other.enclosingPackageID!= null)
				return false;
		} else if (!enclosingPackageID.match(other.enclosingPackageID))
			return false;
		
		return true;
	}
}

class PackageDeclaration extends BaseConstructDeclaration
{
	private static final long serialVersionUID = -9081890582103567413L;
	
	final Set<String> resourceAbsPaths = new HashSet<String>();
	
	public PackageDeclaration(String resourceAbsPath, IStrategoList id) {
		super(id);
		
		resourceAbsPaths.add(resourceAbsPath); 
	}
	
	public  void add(String resAbsolutePath)
	{
		resourceAbsPaths.add(resAbsolutePath);
	}
	
	public Set<String> getAllFilePaths()
	{
		 return resourceAbsPaths;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PackageDeclaration [id=" + id + ", resourceAbsPaths=" + resourceAbsPaths + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((resourceAbsPaths == null) ? 0 : resourceAbsPaths.hashCode());
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
		PackageDeclaration other = (PackageDeclaration) obj;
		if (resourceAbsPaths == null) {
			if (other.resourceAbsPaths != null)
				return false;
		} else if (!resourceAbsPaths.equals(other.resourceAbsPaths))
			return false;
		return true;
	}
}

