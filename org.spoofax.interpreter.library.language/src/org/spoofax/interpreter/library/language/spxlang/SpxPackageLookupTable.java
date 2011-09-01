package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jdbm.PrimaryHashMap;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.TermFactory;

/**
 * @author Md. Adil Akhter
 * Created On : Sep 1, 2011
 */

class SpxPackageLookupTable {

	private final PrimaryHashMap<IStrategoList, PackageDeclaration> _packageLookupTable;
	private final SecondaryTreeMap <String , IStrategoList , PackageDeclaration> _uriMap;

	/**
	 * Instantiates a lookup table for the base constructs (e.g. , packages and modules)of  Spoofaxlang.
	 *  
	 * @param tableName name of the table 
	 * @param manager an instance of {@link ISpxPersistenceManager}
	 */
	public SpxPackageLookupTable(String tableName , ISpxPersistenceManager manager)
	{
		assert tableName != null;
		assert manager != null;
		
		_packageLookupTable = manager.loadHashMap(tableName+ "._lookupPackageMap.idx");
		
		// readonly secondary view of the the lookup table . 
		_uriMap = _packageLookupTable.secondaryTreeMapManyToOne(tableName+ "._urimap.idx", 
				
				new SecondaryKeyExtractor<Iterable<String>, IStrategoList, PackageDeclaration>() {
					/**
					 * Returns the Secondary key of the primary lookup table. 
					 *   
					 * @param key current primary key 
					 * @param value value to be mapped using primary key
					 * @return secondary key to map the value with . 
					 */
					public Iterable<String> extractSecondaryKey(IStrategoList key, PackageDeclaration value) {
						return value.getAllFilePaths();
					}
			}
		);
	}
	
	
	public void define( PackageDeclaration packageDeclaration )
	{
		assert packageDeclaration != null;
		
		_packageLookupTable.put( packageDeclaration.getId(), packageDeclaration);
	}
	
	public boolean appendFilePath( IStrategoList key , String absPath )
	{
		assert key!= null & absPath != null ;
		
		PackageDeclaration decl  = _packageLookupTable.get(key);
		
		if ( decl != null)
		{
			decl.add(absPath);
			this.define(decl);
			return true;
		}
		
		return false;
	}
	
	public PackageDeclaration get(IStrategoList id) {
		return _packageLookupTable.get(id);
	}
	
	public PackageDeclaration remove(IStrategoList id)
	{
		return _packageLookupTable.remove(id);
	}
	
	public Iterable<PackageDeclaration> packageDeclarationsByUri( String absUri)
	{
		List<PackageDeclaration> ret = new ArrayList<PackageDeclaration>();
	
		for ( IStrategoList l: _uriMap.get(absUri))
			ret.add(_uriMap.getPrimaryValue(l));
		
		return ret;
	}
	
	
	/**
	 * Only for testing purpose
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ISpxPersistenceManager manager = new SpxPersistenceManager( "test" , "c:/temp");
		
		SpxPackageLookupTable lookupTable = new SpxPackageLookupTable("lookup", manager);
		
		final TermFactory f = new TermFactory();
		final String absPathString1 = "c:/temp/test.spx" ;
		final String absPathString2 = "c:/temp/test2.spx" ;
		
		//module declaration 
		IStrategoList idp1 = f.makeList(f.makeString("test") , f.makeString("p1"));
		PackageDeclaration p1 = new PackageDeclaration(absPathString2, idp1);
		
		lookupTable.define(p1);
		
		IStrategoList idp2 = f.makeList(f.makeString("test") , f.makeString("p2"));
		PackageDeclaration  p2 = new PackageDeclaration(absPathString2, idp2);
		p2.add(absPathString1);
		
		lookupTable.define(p2);
		//saving and closing the persistence manager
		manager.commitAndClose();
		
		
		// loading the symbol table again
		ISpxPersistenceManager manager1 = new SpxPersistenceManager( "test" , "c:/temp");
		SpxPackageLookupTable  lookupTable1 = new SpxPackageLookupTable("lookup", manager1);
		
		System.out.println("lookup for ID" + idp2 );
		System.out.println("Result : " + lookupTable1.get(idp2));
		System.out.println();
		
		System.out.println("lookup for packages in the following URI : " + absPathString1);
		System.out.println("Result : " + lookupTable1.packageDeclarationsByUri(absPathString1));
		System.out.println();
		
		//update p1 with new uri . Will verify whether the changes are persisted  
		lookupTable1.appendFilePath(idp2, absPathString2);
		
		System.out.println("lookup for ID . [Should return 2 URIs]" + idp2 );
		System.out.println("Result : " + lookupTable1.get(idp2));
		System.out.println();
		
		System.out.println("lookup for packages in the following URI : " + absPathString2);
		System.out.println("Result : " + lookupTable1.packageDeclarationsByUri(absPathString2));
		System.out.println();
	}

}
