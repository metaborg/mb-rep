package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jdbm.PrimaryMap;
import jdbm.RecordListener;
import jdbm.SecondaryHashMap;
import jdbm.SecondaryKeyExtractor;

import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbol;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolKey;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableEntry;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SpxPrimarySymbolTable implements INamespaceResolver , IPackageDeclarationRecordListener,IModuleDeclarationRecordListener {
	private final String SRC = this.getClass().getSimpleName();
	
	private transient INamespace _activeNamespace ;
	
	private final SpxSemanticIndexFacade _facade;
	
	private final PrimaryMap <NamespaceUri,INamespace> namespaces;
	private final PrimaryMap <String,Long> timestamps;
	private final SecondaryHashMap <IStrategoList,NamespaceUri,INamespace> namespaceByStrategoId;
	
	
	private final static String INITIALIZED_ON_KEY = "INITIALIZED_ON";
	private final static String LAST_CODEGEN_ON_KEY = "LAST_CODEGEN_ON";
	
	public SpxPrimarySymbolTable(SpxSemanticIndexFacade facade) throws SecurityException, IOException{
		assert facade != null  : "SpxSemanticIndexFacade  is expected to non-null" ;

		_facade = facade;

		String tableName = facade.getPersistenceManager().getIndexId() + "primary_symbol_table.idx";
		
		timestamps = facade.getPersistenceManager().loadHashMap(tableName + "timestamps.idx");
		
		namespaces  = facade.getPersistenceManager().loadHashMap(tableName + "namespaces.idx");
		namespaceByStrategoId = namespaces.secondaryHashMap(tableName+ ".namespaceByStrategoId.idx", 
				new SecondaryKeyExtractor<IStrategoList,NamespaceUri,INamespace>(){
					public IStrategoList extractSecondaryKey(NamespaceUri k,INamespace v) {
						return k.id(); 
					}
				});
	}
	
	private ISpxPersistenceManager persistenceManager(){ 
		return _facade.getPersistenceManager(); 
	}
	
	/**
	 * Adding Global Namespace in symbol-table by default.
	 * @param facade
	 */
	public void addGlobalNamespace(SpxSemanticIndexFacade facade){
		this.defineNamespace(GlobalNamespace.createInstance(facade));
	}
	
	public void defineNamespace(INamespace namespace) {
		// if not already defined, defining this namespace
		if (!containsNamespace(namespace))
 			this.namespaces.put(namespace.namespaceUri(), namespace);
	}
	
	public NamespaceUri toNamespaceUri(IStrategoList spoofaxId) {
		NamespaceUri uri = getNamespaceUri(spoofaxId);
		if(uri == null) {
			uri = new NamespaceUri(spoofaxId);
		}
		return uri;
	}
	
	public INamespace resolveNamespace(IStrategoList id){
		if(_activeNamespace != null && _activeNamespace.namespaceUri().equalSpoofaxId(id))
			return _activeNamespace;

		boolean alreadyFoundNamespace = false;
		int foundNamespaces = 0;
		INamespace toReturn = null;

		Iterable<INamespace> resolvedNamespaces = namespaceByStrategoId.getPrimaryValues(id);

		if(resolvedNamespaces !=null){
			for( INamespace n : resolvedNamespaces) {
				if(!alreadyFoundNamespace){
					alreadyFoundNamespace = true;
					toReturn = n;
				}
				foundNamespaces = foundNamespaces + 1;  
			}
			if(foundNamespaces > 1 ){
				throw new IllegalStateException(String.format("More than one Namespace found with the following spoofax Id : %s", id.toString()));
			}
		}
		return toReturn;
	}

	public INamespace resolveNamespace(NamespaceUri id) {
		if(_activeNamespace != null  && _activeNamespace.namespaceUri().equals(id))
			return _activeNamespace;
		
		return namespaces.get(id); 
	}
	
	private INamespace removeNamespace(INamespace nsToRemove){
		if(nsToRemove != null){
			
			// Removing the internal namespace associate with the PackageNamespace
			if(nsToRemove instanceof PackageNamespace){
				NamespaceUri internalNamespaceUri = PackageNamespace.packageInternalNamespace(nsToRemove.namespaceUri(), _facade);
				INamespace internalNS = this.namespaces.remove(internalNamespaceUri);	
				
				if(internalNS  != null)
					persistenceManager().logMessage(SRC, "removenamespace | removed internal namespace: " + internalNS);
				else
					persistenceManager().logMessage(SRC, "removenamespace | could not find internal namespace");
			}
			
			persistenceManager().logMessage(SRC, "removenamespace | removing following namespace : " + nsToRemove);
			INamespace retNs =this.namespaces.remove(nsToRemove.namespaceUri());
			
			if(retNs   != null)
				persistenceManager().logMessage(SRC, "removenamespace | removed namespace: " + retNs);
			else
				persistenceManager().logMessage(SRC, "removenamespace | could not find namespace");
		}
		return nsToRemove;
	}
	
	private INamespace removeNamespaceByUri(NamespaceUri id){
		INamespace nsToRemove  = resolveNamespace(id) ;
		
		return removeNamespace(nsToRemove);
	}
	
	public INamespace removeNamespaceById(IStrategoList id){
		INamespace nsToRemove  = resolveNamespace(id) ;
		
		return removeNamespace(nsToRemove);
	}
	
	public NamespaceUri getNamespaceUri(IStrategoList id) {
		Iterable<NamespaceUri> uriIterator = namespaceByStrategoId.get(id);
		if(uriIterator != null)
			for( NamespaceUri uri : uriIterator)
				return uri;
		
		return null;
	}
	
	public boolean containsNamespace(IStrategoList id) { return namespaceByStrategoId.containsKey(id);}
	
	public boolean containsNamespace(NamespaceUri namespaceId) { return namespaces.containsKey(namespaceId);}
	
	public boolean containsNamespace(INamespace namespace) { return this.containsNamespace(namespace.namespaceUri());}

	public void clear(){  
		Set<NamespaceUri> keys = namespaces.keySet();
		
		List<NamespaceUri> namespacesToRemove = new ArrayList<NamespaceUri>();
		for(NamespaceUri u : keys){ namespacesToRemove.add(u);} 
			
		for(NamespaceUri uriToRemove : namespacesToRemove){
			removeNamespaceByUri(uriToRemove);
		}	
		
		this.ensureActiveNamespaceUnloaded(this._activeNamespace);
		timestamps.clear();
	}
	
	public int size() { return namespaces.size();}
	 
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() { return "SpxPrimarySymbolTable ( defined namespaces : " + namespaces.keySet() + ")"; 	}
	
	public Set<NamespaceUri> getAllNamespaces() { return namespaces.keySet() ; }

	public void defineSymbol(IStrategoList namespaceId, SpxSymbolTableEntry symTableEntry) throws SpxSymbolTableException {
		INamespace currentNamespace = activateNamespace(namespaceId);
		
		persistenceManager().logMessage(SRC, "defineSymbol (Thread Id :"+Thread.currentThread().getId() +")| defining symbols with the following criteria :  namespace " + namespaceId +  " with Key : "+ symTableEntry.key + " Value : "+ symTableEntry.value);
		currentNamespace.define(symTableEntry, _facade);
	}
	
	public Set<SpxSymbol> undefineSymbols(IStrategoList namespaceId, IStrategoTerm symbolId, IStrategoConstructor symbolType) throws SpxSymbolTableException {
		persistenceManager().logMessage(SRC, "undefineSymbol | undefineSymbol symbol with the following criteria :  search origin " + namespaceId +  " with Key : "+ symbolId + "of Type : "+ symbolType.getName());
			
		INamespace currentNamespace =  activateNamespace(namespaceId);
		Set<SpxSymbol> undefinedSymbols = currentNamespace.undefineSymbols(symbolId, symbolType, _facade);
	
		persistenceManager().logMessage(SRC, "undefineSymbol | undefineSymbol Symbols : " + undefinedSymbols );

		return undefinedSymbols;
	}
	
	
	public Collection<SpxSymbol> resolveSymbols(IStrategoList namespaceId, IStrategoTerm symbolId, IStrategoConstructor symbolType, boolean returnDuplicates) throws SpxSymbolTableException {
		persistenceManager().logMessage(SRC, "resolveSymbols | Resolving symbols with the following criteria :  search origin " + namespaceId +  " with Key : "+ symbolId + " of Type : "+ symbolType.getName());
		
		INamespace namespace = activateNamespace(namespaceId);
		Collection<SpxSymbol> resolvedSymbols = namespace.resolveAll(_facade, symbolId ,symbolType, returnDuplicates);
		
		persistenceManager().logMessage(SRC, "resolveSymbols | Resolved Symbols : " + resolvedSymbols);
		return resolvedSymbols;
	}
	
	
	public SpxSymbol resolveSymbol(IStrategoList namespaceId, IStrategoTerm symbolId, IStrategoConstructor symbolType) throws SpxSymbolTableException {
		persistenceManager().logMessage(SRC, "resolveSymbol | Resolving symbol with the following criteria :  search origin " + namespaceId +  " with Key : "+ symbolId + "of Type : "+ symbolType.getName());
		
		INamespace namespace =  activateNamespace(namespaceId);
		
		SpxSymbol  resolvedSymbol = namespace.resolve(symbolId, symbolType ,_activeNamespace ,_facade);
		
		persistenceManager().logMessage(SRC, "resolveSymbol | Resolved Symbol : " + resolvedSymbol );
		
		return resolvedSymbol;
	}
	
	public INamespace newAnonymousNamespace(IStrategoList enclosingNamespaceId) throws SpxSymbolTableException{
		persistenceManager().logMessage(SRC, "newAnonymousNamespace | Inserting a Anonymous Namespace in following enclosing namespace : "  + enclosingNamespaceId);
		
		INamespace currentNamespace = activateNamespace(enclosingNamespaceId);
		
		// creating and defining a new local namesapce 
		INamespace localNamespace = LocalNamespace.createInstance(_facade, currentNamespace); 
		this.defineNamespace(localNamespace);
		this.commitChanges(); // committing the unsaved chagnes in to save changes regarding its enclosed namespaces 
		
		persistenceManager().logMessage(SRC, "newAnonymousNamespace | Folloiwng namesapce is created : "  + localNamespace);
		
		return localNamespace ;
	}

	/**
	 * Destroying Namespace with following namespaceId
	 * @param enclosingNamespaceId
	 * 
	 * @return
	 * @throws SpxSymbolTableException
	 */
	public INamespace destroyNamespace(IStrategoList namespaceId) throws SpxSymbolTableException{
		persistenceManager().logMessage(SRC, "destroyNamespace | Removing the following namespace : "  + namespaceId);
		
		INamespace ns = this.removeNamespaceById(namespaceId);
		ensureActiveNamespaceUnloaded(namespaceId);
		
		persistenceManager().logMessage(SRC, "destroyNamespace | Folloiwng namesapce is removed : "  + ns);
		
		return ns;
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
				
				removeNamespaceById(packageID) ;
			}};
	}

	public RecordListener<IStrategoList, ModuleDeclaration> getModuleDeclarationRecordListener() {
		return new RecordListener<IStrategoList, ModuleDeclaration>() {

			public void recordInserted(IStrategoList key, ModuleDeclaration value) throws IOException {
				// do nothing 
				
			}

			public void recordUpdated(IStrategoList key,  ModuleDeclaration oldValue, ModuleDeclaration newValue)
					throws IOException {
				// do nothing 
				
			}

			public void recordRemoved(IStrategoList moduleId, ModuleDeclaration value)
					throws IOException {
				removeNamespaceById(moduleId) ;
				
			}
			
		};
	}

	/**
	 * Removes all the entries from the Global Namespace
	 * 
	 * @param spxSemanticIndexFacade
	 */
	public void cleanGlobalNamespace(SpxSemanticIndexFacade spxSemanticIndexFacade) {
		persistenceManager().logMessage(SRC, "clearGlobalNamespce | Remove all the entries stored currently in GlobalNamespace" );
		
		IStrategoList gnsId = GlobalNamespace.getGlobalNamespaceId(spxSemanticIndexFacade);
		INamespace gns = this.resolveNamespace(gnsId); 
		if(gns != null)
			gns.clear();
		
		this.namespaces.put(gns.namespaceUri(), gns);
		persistenceManager().logMessage(SRC, "clearGlobalNamespce | Successfully removed all the entries." );
	}
	
	public synchronized void commitChanges() {
		if(_activeNamespace != null) {
			this.namespaces.put(_activeNamespace.namespaceUri(), _activeNamespace);
		}
	}
	
	private void ensureActiveNamespaceUnloaded(INamespace namespace){
		if(namespace!=null)
			this.ensureActiveNamespaceUnloaded(namespace.namespaceUri().id());
	}
	
	private synchronized void ensureActiveNamespaceUnloaded(IStrategoList namespaceId){
		if( (_activeNamespace !=null) && _activeNamespace.namespaceUri().equalSpoofaxId(namespaceId)){
			_activeNamespace = null;
		}
	}
	
	
	private synchronized INamespace activateNamespace(IStrategoList namespaceId) throws SpxSymbolTableException{
		if((_activeNamespace == null) ||(!_activeNamespace.namespaceUri().equalSpoofaxId(namespaceId))){
			// changing active namespace
			// hence, committing the changes 
			commitChanges(); 
			
			//Keeping a transient reference to the current/active Namespace
			//More likely that there are other symbols to be defined in the
			//current and active namespace. In that case, it will improve 
			//performance as namespace resolving avoided by means of extra 
			//caching
			_activeNamespace = this.resolveNamespace(namespaceId);
			if(_activeNamespace ==null){
				throw new RuntimeException("Unknown namespaceId: "+ namespaceId+". Namespace can not be resolved from symbol-table") ;
			}
		}
		return _activeNamespace;
	}
	
	//TODO : refactor this time-stamps to a separate class. It is not the right 
	// place for the following codes.
	long getIndexLastInitializedOn(){ 
		Long initializedOn = timestamps.get(INITIALIZED_ON_KEY);
		
		if(initializedOn ==null) 
			return 0;
		
		return initializedOn;
	}
	
	void setIndexUpdatedOn(long timestamp){ 
		timestamps.put(INITIALIZED_ON_KEY, timestamp);
	}
	
	long getLastCodeGeneratedOn(){ 
		Long lastCodeGenOn = timestamps.get(LAST_CODEGEN_ON_KEY);
		
		if(lastCodeGenOn ==null) 
			return 0;
		
		return lastCodeGenOn;
	}
	
	void setLastCodeGeneratedOn(long timestamp){ 
		timestamps.put(LAST_CODEGEN_ON_KEY, timestamp);
	}
	
	
	
	/**
	 * Printing all the symbols current hashmap 
	 * 
	 * @throws IOException
	 * @throws SpxSymbolTableException 
	 */
	public void printSymbols(SpxSemanticIndexFacade f, String state , String projectPath , String indexId) throws IOException, SpxSymbolTableException{
		new File(projectPath +"/"+Utils.SPX_INDEX_DIRECTORY+ "/.log").mkdirs();
		
		FileWriter fstream = new FileWriter(projectPath +"/"+ Utils.SPX_INDEX_DIRECTORY+ "/.log/"+indexId+"_symbols_"+Utils.now("yyyy-MM-dd HH.mm.ss.SSS")+".csv" , true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(", , ,------------- Logging [" +state+ "] state of Symbol-Table at :" + Utils.now("yyyy-MM-dd HH.mm.ss")+"-------------\n");
		try
		{	
			if(namespaces != null){
				for(INamespace ns : namespaces.values()){
					out.write("\n, --- "+ Utils.getCsvFormatted(ns.toString()) +"--- \n");
					Utils.logEntries(f, ns,out) ;
				}
			}
		}catch(IOException ex){ //ignore 
			
		}
		finally{out.close();}
	}

	
	
	
}
