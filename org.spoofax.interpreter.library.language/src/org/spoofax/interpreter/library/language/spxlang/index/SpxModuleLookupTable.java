package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jdbm.PrimaryHashMap;
import jdbm.RecordListener;
import jdbm.SecondaryHashMap;
import jdbm.SecondaryKeyExtractor;

import org.spoofax.interpreter.library.language.spxlang.index.data.IdentifiableConstruct;
import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxCompilationUnitInfo;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

public class SpxModuleLookupTable implements ICompilationUnitRecordListener, IPackageDeclarationRecordListener{
	
	private final PrimaryHashMap<IStrategoList, ModuleDeclaration> _moduleLookupMap; 
	
	 /**
     * Listeners which are notified about changes in records
     */
    protected List<RecordListener<IStrategoList, ModuleDeclaration>> recordListeners = new ArrayList<RecordListener<IStrategoList, ModuleDeclaration>>();

    
	/* FIXME : Using separate HashMap due to the consideration of converting them store map
	 * to load module AST lazily. 
	 */
	private final PrimaryHashMap<IStrategoList, IStrategoAppl> _moduleDefinition; 
	private final PrimaryHashMap<IStrategoList, IStrategoAppl> _moduleAnalyzedDefinition;
	
	private final SecondaryHashMap <String , IStrategoList , ModuleDeclaration> _moduleByFileAbsPath;
	private final SecondaryHashMap<IStrategoList, IStrategoList,ModuleDeclaration> _moduleByPackageId;
	
	private final ISpxPersistenceManager _manager;
	
	private final String SRC  = this.getClass().getSimpleName();
	
	/**
	 * Instantiates a lookup table for the base constructs (e.g. , packages and modules)of  Spoofaxlang.
	 *  
	 * @param tableName name of the table 
	 * @param manager an instance of {@link ISpxPersistenceManager}
	 */
	public SpxModuleLookupTable(ISpxPersistenceManager manager)
	{
		String tableName = SRC+ "_"+ manager.getIndexId();

		assert manager != null;
		_manager = manager;
		_moduleLookupMap = manager.loadHashMap(tableName+ "._lookupModuleMap.idx");

		// read-only secondary view of the the lookup table . 
		_moduleByFileAbsPath = _moduleLookupMap.secondaryHashMap(tableName+ "._moduleByFileAbsPath.idx", 
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

		_moduleByPackageId = _moduleLookupMap.secondaryHashMap(tableName+ "._moduleByPackageId.idx", 
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

		this._moduleDefinition = manager.loadHashMap(tableName+ "._moduleDefinition.idx");
		this._moduleAnalyzedDefinition = manager.loadHashMap(tableName+ "._moduleAnalyzedDefinition.idx");

		initRecordListener();
	}
	
	private void initRecordListener()
	{
		_moduleLookupMap.addRecordListener(
				new RecordListener<IStrategoList, ModuleDeclaration>() {

					public void recordInserted(IStrategoList key,
							ModuleDeclaration value) throws IOException {
						//do nothing 
						
					}

					public void recordUpdated(IStrategoList key,
							ModuleDeclaration oldValue,
							ModuleDeclaration newValue) throws IOException {
						//do nothing 
						
					}

					public void recordRemoved(IStrategoList key,
							ModuleDeclaration value) throws IOException {
					
						// cleanup other table to make it consistent 
						_moduleDefinition.remove(key);
						_moduleAnalyzedDefinition.remove(key);
						
						if(!recordListeners.isEmpty()){	
							for( RecordListener<IStrategoList, ModuleDeclaration> rl: recordListeners){
								rl.recordRemoved(key, value);
							}
						}
					}
				}
		);
	}
	/** Size of the Symbol Table 
	 * 
	 * @return
	 */
	public int size() 
	{
		assert _moduleLookupMap.size() == _moduleDefinition.size();
		assert _moduleLookupMap.size() == _moduleAnalyzedDefinition.size();
		
		return _moduleLookupMap.size();
	}
	/**
	 * Defines a new entry in this symbol table 
	 * 
	 * @param info
	 * @param compilationUnit
	 */
	SpxModuleLookupTable define(ModuleDeclaration decl)
	{	
		_moduleLookupMap.put(decl.getId(), decl);
		return this;
	}
	
	/**
	 * Defines Module Definition in the Symbole Table
	 * @param decl
	 * @param originalModuleDefinition
	 * @param analyzedModuleDefinition
	 */
	public void define(ModuleDeclaration decl, IStrategoAppl originalModuleDefinition , IStrategoAppl analyzedModuleDefinition)
	{
		this.define(decl)
			.addModuleDefinition(decl.getId(), originalModuleDefinition)
			.addAnalyzedModuleDefinition(decl.getId(), analyzedModuleDefinition);
	}
	
	private SpxModuleLookupTable addModuleDefinition(IStrategoList id, IStrategoAppl moduleDefinition)
	{
		_moduleDefinition.put(id, moduleDefinition);
		
		return this;
	}
	
	private SpxModuleLookupTable addAnalyzedModuleDefinition(IStrategoList id, IStrategoAppl moduleDefinition)
	{
		_moduleAnalyzedDefinition.put(id, moduleDefinition);
		
		return this;
	}
	
	/**
	 * Removes {@link IdentifiableConstruct} from the lookup table mapped by the {@code id}
	 * 
	 * @param id {@link IStrategoList} representing qualified ID of the Construct
	 * @return {@link IdentifiableConstruct} mapped by {@code id}
	 */
	public ModuleDeclaration remove(IStrategoList id)
	{	
		_manager.logMessage(SRC+".remove", "Removing following Module : "+ id);
		//removing module declaration from the table 
		//and returning it.
		return _moduleLookupMap.remove(id);
	}
	
	/**
	 * Returns {@link ModuleDeclaration} that is mapped by the specified {@code id} argument.
	 * 
	 * @param id
	 * @return
	 */
	public ModuleDeclaration getModuleDeclaration(IStrategoList id) {
		return _moduleLookupMap.get(id);
	}
	
	/**
	 * Check whether particular module exists in the Symbol Table 
	 * 
	 * @param id
	 * @return
	 */
	public boolean containsModuleDeclaration(IStrategoList id)
	{
		return _moduleLookupMap.containsKey(id);
	}
	
	/**
	 * Gets a module definition 
	 * 
	 * @param id
	 * @return
	 */
	public IStrategoAppl getModuleDefinition(IStrategoList id)
	{
		return this._moduleDefinition.get(id);
	}
	
	/**
	 * Gets module definition (analyzed) 
	 * 
	 * @param id
	 * @return
	 */
	public IStrategoAppl getAnalyzedModuleDefinition(IStrategoList id)
	{
		return this._moduleAnalyzedDefinition.get(id);
	}
	
	/**
	 * Returns ModuleDeclarations mapped by a filepath. It actually returns 
	 * all the module declaration exists in a file . 
	 * 
	 * @param absUri
	 * @return
	 */
	public Iterable<ModuleDeclaration> getModuleDeclarationsByUri( String absUri)
	{
		List<ModuleDeclaration> ret = new ArrayList<ModuleDeclaration>();
		
		Iterable<IStrategoList> foundModuleDecls = _moduleByFileAbsPath.get(absUri);
		
		if(foundModuleDecls  != null)
		{
			for ( IStrategoList l: foundModuleDecls)
				ret.add(_moduleByFileAbsPath.getPrimaryValue(l));
		}
		
		return ret;
	}

	void verifyUriExists(String uri){
		if(!containsUri(uri)){ 
			throw new IllegalArgumentException("Illegal URI argument " + uri) ;
		}
	}

	private boolean containsUri ( String absPath ) { return _moduleByFileAbsPath.containsKey(absPath);}
	 
	public Iterable<ModuleDeclaration> getModuleDeclarationsByPackageId(IStrategoList packageID) throws SpxSymbolTableException
	{
		List<ModuleDeclaration> ret = new ArrayList<ModuleDeclaration>();
		
		Iterable<IStrategoList> foundModuleDecls = _moduleByPackageId.get(packageID);
		
		if(foundModuleDecls  != null){
			for ( IStrategoList l: foundModuleDecls)
				ret.add(_moduleByPackageId.getPrimaryValue(l));
		}else{
			throw new SpxSymbolTableException( "Unknown Package Id "+ packageID.toString());
		}
		
		return ret;
	}
	
	private void removeModuleDeclarationByPackageId(IStrategoList packageId){
		_manager.logMessage(SRC + ".removeModuleDeclarationByPackageId", "removing all the enclosed module of package "+ packageId);
		
		List<IStrategoList> toRemove = new ArrayList<IStrategoList>();
		Iterable<IStrategoList> foundModuleDecls = _moduleByPackageId.get(packageId);
		
		if(foundModuleDecls  != null){
			for ( IStrategoList l: foundModuleDecls)
				toRemove.add(l);
		}
		
		_manager.logMessage(SRC + ".removeModuleDeclarationByPackageId", "removing all the following modules  "+ toRemove);
		
		for( IStrategoList moduleId:toRemove)
			this.remove(moduleId);
		
		_manager.logMessage(SRC + ".removeModuleDeclarationByPackageId", "operation successful");
	}
	
	public IStrategoList packageId(IStrategoList moduleId)
	{
		if( containsModuleDeclaration(moduleId))
		{
			return getModuleDeclaration(moduleId).enclosingPackageID;
		}	
		return null;
	}
	
	
	/**
	 * Removes all the module {@link ModuleDeclaration} located in the 
	 * following URI : {@code absUri}
	 *  
	 * @param absUri String representation of absolute path of the file 
	 * 
	 */
	public void removeModuleDeclarationsByUri(String absUri)
	{	
		ArrayList<IStrategoList> delList = new ArrayList<IStrategoList>();
		
		_manager.logMessage(SRC + ".removeModuleDeclarationsByUri", "Removing all the module declared in "+ absUri);
		
		if( _moduleByFileAbsPath.get(absUri) != null ){	
			// constructing a temporary list to be removed from 
			// the symbol table. 
			for ( IStrategoList l: _moduleByFileAbsPath.get(absUri))
				delList.add(l);

		}

		_manager.logMessage(SRC + ".removeModuleDeclarationsByUri", " Found  "+ delList + " to remove from the table.");
		
		// removing the package declaration from the lookup table.
		for(Object o : delList.toArray())
			remove((IStrategoList)o);
		
		_manager.logMessage(SRC + ".removeModuleDeclarationsByUri", " removed "+ delList + " to remove from the table.");
	}
	
	
	/**
	 * Clears ModuleLookup Table
	 */
	public synchronized void clear()
	{
		_manager.logMessage(SRC + ".clear", "Removing "+ this.size()+" entries ");
		
		Iterator<IStrategoList> keyIter = _moduleLookupMap.keySet().iterator();
		if(keyIter!=null){
			while (keyIter.hasNext())
				remove(keyIter.next());
		}
	}
	
	
	public RecordListener<String, SpxCompilationUnitInfo> getCompilationUnitRecordListener() {
		return new RecordListener<String, SpxCompilationUnitInfo>() {

			public void recordInserted(String key, SpxCompilationUnitInfo value)
					throws IOException {
				// do nothing 
				
			}

			public void recordUpdated(String key,
					SpxCompilationUnitInfo oldValue,
					SpxCompilationUnitInfo newValue) throws IOException {
				
				if(oldValue.getVersionNo() != newValue.getVersionNo()){
					//Whenever compilation unit version no is updated , 
					//remove all the related module declaration 
					//from the symbol table , since it is obsolete now. 
					removeModuleDeclarationsByUri(key);
				}
			}

			public void recordRemoved(String key, SpxCompilationUnitInfo value)
					throws IOException {
		
				removeModuleDeclarationsByUri(key);
			}
		};
	}

	/**
	 * Returns all the {@link ModuleDeclaration} declared in the current package. 
	 * 
	 * @return 
	 */
	public Iterable<ModuleDeclaration> getModuleDeclarations() {
		return this._moduleLookupMap.values();
	}

	
	public RecordListener<IStrategoList, PackageDeclaration> getPackageDeclarationRecordListener() {
		return new RecordListener<IStrategoList, PackageDeclaration>(){

			public void recordInserted(IStrategoList packageID,
					PackageDeclaration value) throws IOException {
				// do nothing
				
			}

			public void recordUpdated(IStrategoList packageID,
					PackageDeclaration oldValue, PackageDeclaration newValue)
					throws IOException {
				// do nothing 
			}

			public void recordRemoved(IStrategoList packageID,
					PackageDeclaration value) throws IOException {
				
				removeModuleDeclarationByPackageId(packageID) ;
			}};
	}
	
	public void addRecordListener( final IModuleDeclarationRecordListener rl){
		this.recordListeners.add(rl.getModuleDeclarationRecordListener());
	}

	public void removeRecordListener( final IModuleDeclarationRecordListener rl){
		this.recordListeners.remove(rl.getModuleDeclarationRecordListener());
	}
}

