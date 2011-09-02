package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import jdbm.InverseHashView;
import jdbm.PrimaryHashMap;
import jdbm.SecondaryHashMap;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.TermFactory;


class SpxModuleLookupTable {

	private final PrimaryHashMap<IStrategoList, ModuleDeclaration> _moduleLookupMap;
	private final SecondaryTreeMap <String , IStrategoList , ModuleDeclaration> _uriMap;

	private final SecondaryHashMap<IStrategoList, IStrategoList,ModuleDeclaration> _enclosingPackageIdReferences;
	

	/**
	 * Instantiates a lookup table for the base constructs (e.g. , packages and modules)of  Spoofaxlang.
	 *  
	 * @param tableName name of the table 
	 * @param manager an instance of {@link ISpxPersistenceManager}
	 */
	public SpxModuleLookupTable(String tableName , ISpxPersistenceManager manager)
	{
		assert tableName != null;
		assert manager != null;
		
		_moduleLookupMap = manager.loadHashMap(tableName+ "._lookupModuleMap.idx");
		
		// readonly secondary view of the the lookup table . 
		_uriMap = _moduleLookupMap.secondaryTreeMap(tableName+ "._urimap.idx", 
				new SecondaryKeyExtractor<String, IStrategoList, ModuleDeclaration>() {

			/**
			 * Returns the Secondary key of the lookup table. 
			 *   
			 * @param key current primary key 
			 * @param value value to be mapped using primary key
			 * @return secondary key to map the value with . 
			 */
			public String extractSecondaryKey(IStrategoList key, ModuleDeclaration value) {
				return value.resourceAbsPath;
			}
		}
		);
		
		_enclosingPackageIdReferences = _moduleLookupMap.secondaryHashMap(tableName+ "._enclosingPackageIdReferences.idx", 
				new SecondaryKeyExtractor<IStrategoList, IStrategoList, ModuleDeclaration>() {

			/**
			 * Returns the Secondary key of the lookup table. 
			 *   
			 * @param key current primary key 
			 * @param value value to be mapped using primary key
			 * @return secondary key to map the value with . 
			 */
			public IStrategoList extractSecondaryKey(IStrategoList key, ModuleDeclaration value) {
				return value.enclosingPackageID;
			}
		}
		);
	}
	
	/**
	 * Defines a new entry in this symbol table 
	 * 
	 * @param info
	 * @param compilationUnit
	 */
	public void define(ModuleDeclaration decl)
	{	
		_moduleLookupMap.put(decl.getId(), decl);
		
	}
	/**
	 * Removes {@link BaseConstructDeclaration} from the lookup table mapped by the {@code id}
	 * 
	 * @param id {@link IStrategoList} representing qualified ID of the Construct
	 * @return {@link BaseConstructDeclaration} mapped by {@code id}
	 */
	public ModuleDeclaration remove(IStrategoList id)
	{
		return _moduleLookupMap.remove(id);
	}
	/**
	 * Returns {@link BaseConstructDeclaration} that is mapped by the specified {@code id} argument.
	 * 
	 * @param id
	 * @return
	 */
	public ModuleDeclaration get(IStrategoList id) {
		return _moduleLookupMap.get(id);
	}
	
	
	public boolean containsModuleDeclaration(IStrategoList id)
	{
		return _moduleLookupMap.containsKey(id);
	}
	/**
	 * Returns ModuleDeclarations mapped by absPath
	 * 
	 * @param absUri
	 * @return
	 */
	public Iterable<ModuleDeclaration> moduleDeclarationsByUri( String absUri)
	{
		List<ModuleDeclaration> ret = new ArrayList<ModuleDeclaration>();
	
		for ( IStrategoList l: _uriMap.get(absUri))
			ret.add(_uriMap.getPrimaryValue(l));
		
		return ret;
	}
	
	
	public Iterable<ModuleDeclaration> moduleDeclarationsByPackageId(IStrategoList packageID)
	{
		List<ModuleDeclaration> ret = new ArrayList<ModuleDeclaration>();
	
		for ( IStrategoList l: _enclosingPackageIdReferences.get(packageID))
			ret.add(_enclosingPackageIdReferences.getPrimaryValue(l));
		
		return ret;
	}
	

	public IStrategoList packageId(IStrategoList moduleId)
	{
		if( containsModuleDeclaration(moduleId))
		{
			return get(moduleId).enclosingPackageID;
		}	
		
		return null;
	}
	
	/**
	 * added only for the testing purpose.
	 *  
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException { 
		//TODO :  add actual unit tests
		//TODO :  add reference to JUnit  
	 
		
		ISpxPersistenceManager manager = new SpxPersistenceManager( "test" , "c:/temp");
		
		SpxModuleLookupTable lookupTable = new SpxModuleLookupTable("lookup", manager);
		
		final TermFactory f = new TermFactory();
		final String absPathString = "c:/temp/test.spx" ;
		final String absPathString2 = "c:/temp/test2.spx" ;
		
		IStrategoList pId = f.makeList(f.makeString("test"));
		
		//module declaration 
		IStrategoList idm1 = f.makeList(f.makeString("test") , f.makeString("m1"));
		ModuleDeclaration m1 = new ModuleDeclaration(absPathString, idm1,pId );
		
		lookupTable.define(m1);
		
		IStrategoList idm2 = f.makeList(f.makeString("test") , f.makeString("m2"));
		ModuleDeclaration m2 = new ModuleDeclaration(absPathString, idm2,pId );
		
		lookupTable.define(m2);
		
		IStrategoList idm3 = f.makeList(f.makeString("test") , f.makeString("m3"));
		ModuleDeclaration m3 = new ModuleDeclaration(absPathString2, idm3,pId );
		
		lookupTable.define(m3);
		
		m2 = new ModuleDeclaration(absPathString2, idm2,pId );
		lookupTable.define(m2);
	
		
		System.out.println("lookup for ID" + idm2 );
		System.out.println("Result : " + lookupTable.get(idm2));
		System.out.println();
		
		System.out.println("lookup for URI " + absPathString);
		System.out.println("Result : " + lookupTable.moduleDeclarationsByUri(absPathString));
		System.out.println();
		
		System.out.println("lookup for URI " + absPathString2);
		System.out.println("Result : " + lookupTable.moduleDeclarationsByUri(absPathString2));
		System.out.println();
		
		System.out.println("lookup for PackageID " + pId);
		System.out.println("Result : " + lookupTable.moduleDeclarationsByPackageId(pId));
		System.out.println();
	}
}
