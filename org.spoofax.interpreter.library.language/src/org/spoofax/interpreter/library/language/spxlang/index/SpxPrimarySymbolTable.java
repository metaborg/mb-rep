package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import jdbm.PrimaryMap;
import jdbm.RecordListener;
import jdbm.RecordManager;

import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbol;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableEntry;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SpxPrimarySymbolTable implements INamespaceResolver , IPackageDeclarationRecordListener,IModuleDeclarationRecordListener {
	private final String SRC = this.getClass().getSimpleName();
	
	private transient INamespace _activeNamespace ;
	private final SpxSemanticIndexFacade _facade;
	
	private final PrimaryMap <String,INamespace> namespaces;
	private final PrimaryMap <String,Long> timestamps; // store it to global namespace 
	
	private final static String INITIALIZED_ON_KEY = "INITIALIZED_ON";
	private final static String LAST_CODEGEN_ON_KEY = "LAST_CODEGEN_ON";
	
	public SpxPrimarySymbolTable(SpxSemanticIndexFacade facade , RecordManager recordManagerInstance) throws SecurityException, IOException{
		assert facade != null  : "SpxSemanticIndexFacade  is expected to non-null" ;

		String tableName = facade.getPersistenceManager().getIndexId() + "primary_symbol_table.idx";
		
		timestamps = recordManagerInstance.hashMap(tableName + "timestamps.idx");  
		namespaces = recordManagerInstance.hashMap(tableName + "namespaces.idx"); // TODO make it a tree map

		_facade = facade;
	}

	/**
	 * Adding Global Namespace in symbol-table by default.
	 * @param facade
	 */
	public void addGlobalNamespace(SpxSemanticIndexFacade facade){ this.defineNamespace(GlobalNamespace.createInstance(facade)); }
	
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
		
		this.namespaces.put(gns.namespaceUri().id(), gns);
	}
	
	public void clear(){  
		Set<String> namespacesToRemove = namespaces.keySet();
		
		if(namespacesToRemove !=null)
			for(String uriToRemove : namespacesToRemove){ removeNamespaceByUri(uriToRemove);}	
		
		ensureActiveNamespaceUnloaded(this._activeNamespace);
		initTimestamps();
	}
	
	public synchronized void commitChanges() {
		if(_activeNamespace != null)
			this.namespaces.put(_activeNamespace.namespaceUri().id(), _activeNamespace);
	}
		
	public boolean containsNamespace(String id) { return namespaces.containsKey(id);}

	public void defineNamespace(INamespace namespace) {
		// if not already defined, defining this namespace
		if (!containsNamespace(namespace)){
 			this.namespaces.put(namespace.namespaceUri().id(), namespace);
 			System.out.println("Adding following namespace : "+ namespace);
		}	
	}
	
	public void defineSymbol(IStrategoList namespaceId, SpxSymbolTableEntry symTableEntry) throws SpxSymbolTableException {
		INamespace currentNamespace = activateNamespace(namespaceId);
		
		persistenceManager().logMessage(SRC, "defineSymbol (Thread Id :"+Thread.currentThread().getId() +")| defining symbols with the following criteria :  namespace " + namespaceId +  " with Key : "+ symTableEntry.key + " Value : "+ symTableEntry.value);
		currentNamespace.define(symTableEntry, _facade);
	}
	
	/**
	 * Destroying Namespace with following namespaceId
	 * @param enclosingNamespaceId
	 * 
	 * @return
	 * @throws SpxSymbolTableException
	 */
	public INamespace destroyNamespace(IStrategoList namespaceId) throws SpxSymbolTableException{
		persistenceManager().logMessage(SRC, "Removing following Namspace: "  + namespaceId);
		
		List<INamespace> ns = this.removeNamespacesById(namespaceId);
		for (INamespace n : ns)
			ensureActiveNamespaceUnloaded(n);
		
		return ((ns != null) & (ns.size()>0))? ns.get(0) : null;
	}
	
	public Set<String> getAllNamespaceSpxId() { return namespaces.keySet() ; }
	
	public RecordListener<IStrategoList, ModuleDeclaration> getModuleDeclarationRecordListener() {
		return new RecordListener<IStrategoList, ModuleDeclaration>() {

			public void recordInserted(IStrategoList key, ModuleDeclaration value) throws IOException {
				// do nothing 
			}

			public void recordRemoved(IStrategoList moduleId, ModuleDeclaration value)
					throws IOException {
				removeNamespacesById(moduleId) ;
			}

			public void recordUpdated(IStrategoList key,  ModuleDeclaration oldValue, ModuleDeclaration newValue)
					throws IOException {
				// do nothing 
			}
		};
	}
	
	// public boolean containsNamespace(IStrategoList id) { return containsNamespace(NamespaceUri.toSpxID(id)); }

	public RecordListener<IStrategoList, PackageDeclaration> getPackageDeclarationRecordListener() {
		return new RecordListener<IStrategoList, PackageDeclaration>(){

			public void recordInserted(IStrategoList packageID,
					PackageDeclaration value) throws IOException {
				// do nothing
				
			}

			public void recordRemoved(IStrategoList packageID,
					PackageDeclaration value) throws IOException {
				removeNamespacesById(packageID) ;
			}

			public void recordUpdated(IStrategoList packageID,
					PackageDeclaration oldValue, PackageDeclaration newValue)
					throws IOException {
				// do nothing 
			}};
	}

	public void initTimestamps(){timestamps.clear(); }
	
	
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
	 * Printing all the symbols current hashmap 
	 * 
	 * @throws IOException
	 * @throws SpxSymbolTableException 
	 */
	public void printSymbols(SpxSemanticIndexFacade f, String state , String projectPath , String indexId) throws IOException, SpxSymbolTableException{
		// setting up directory + log file
		new File(projectPath +"/"+SpxIndexConfiguration.SPX_INDEX_DIRECTORY+ "/.log").mkdirs();
		String filepath = projectPath +"/"+ SpxIndexConfiguration.SPX_INDEX_DIRECTORY+ "/.log/"+indexId+"_symbols_"+SpxIndexUtils.now("yyyy-MM-dd HH.mm")+".csv";

		if (!new File(filepath).exists()){

			FileWriter fstream = new FileWriter(filepath, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Namespace, Key, Type , Symbol\n");
			try
			{	
				if(namespaces != null){
					for(INamespace ns : namespaces.values()){
						SpxIndexUtils.logEntries(f, ns,out) ;
					}
				}

				// logging context
				out.write("\n, ,#Logging Context#  ");
				out.write(", ,#Logging [" +state+ "] state of Symbol-Table at :" + SpxIndexUtils.now("yyyy-MM-dd HH.mm.ss")+"#\n");
			}catch(IOException ex){
				//ignore 
			}
			finally{out.close();}
		}
	}
	
	public INamespace resolveNamespace(IStrategoList id){
		return resolveNamespace(NamespaceUri.toSpxID(id));
	}
	
	public INamespace resolveNamespace(String id){
		if(_activeNamespace != null && _activeNamespace.namespaceUri().equalSpoofaxId(id))
			return _activeNamespace;
		return namespaces.get(id);
	}
	
	public INamespace resolveNamespace(NamespaceUri id) {
		if(_activeNamespace != null  && _activeNamespace.namespaceUri().equals(id))
			return _activeNamespace;
		
		return namespaces.get(id); 
	}

	public SpxSymbol resolveSymbol(IStrategoList namespaceId, IStrategoTerm symbolId, IStrategoConstructor symbolType, int lookupDepth ) throws SpxSymbolTableException {
		persistenceManager().logMessage(SRC, "resolveSymbol | Resolving symbol with the following criteria :  search origin " + namespaceId +  " with Key : "+ symbolId + "of Type : "+ symbolType.getName());
		
		INamespace namespace =  activateNamespace(namespaceId);
		
		SpxSymbol  resolvedSymbol = namespace.resolve(symbolId, symbolType ,_activeNamespace ,_facade, lookupDepth);
		
		persistenceManager().logMessage(SRC, "resolveSymbol | Resolved Symbol : " + resolvedSymbol );
		
		return resolvedSymbol;
	}
	
	public Collection<SpxSymbol> resolveSymbols(IStrategoList namespaceId, IStrategoTerm symbolId, IStrategoConstructor symbolType, int lookupDepth, boolean returnDuplicates) throws SpxSymbolTableException {
		persistenceManager().logMessage(SRC, "resolveSymbols | Resolving symbols with the following criteria :  search origin " + namespaceId +  " with Key : "+ symbolId + " of Type : "+ symbolType.getName());
		
		INamespace namespace = activateNamespace(namespaceId);
		Collection<SpxSymbol> resolvedSymbols = namespace.resolveAll(_facade, symbolId ,symbolType, lookupDepth, returnDuplicates);
		
		persistenceManager().logMessage(SRC, "resolveSymbols | Resolved Symbols : " + resolvedSymbols);
		return resolvedSymbols;
	}
	
	public int size() { return namespaces.size();}
	
	public NamespaceUri toNamespaceUri(IStrategoList spoofaxId) {
		return new NamespaceUri(spoofaxId);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() { return "SpxPrimarySymbolTable ( Defined namespaces : " + namespaces.keySet() + ")"; 	}

	public Set<SpxSymbol> undefineSymbols(IStrategoList namespaceId, IStrategoTerm symbolId, IStrategoConstructor symbolType) throws SpxSymbolTableException {
		persistenceManager().logMessage(SRC, "undefineSymbol | undefineSymbol symbol with the following criteria :  search origin " + namespaceId +  " with Key : "+ symbolId + "of Type : "+ symbolType.getName());
			
		INamespace currentNamespace =  activateNamespace(namespaceId);
		Set<SpxSymbol> undefinedSymbols = currentNamespace.undefineSymbols(symbolId, symbolType, _facade);
	
		persistenceManager().logMessage(SRC, "undefineSymbol | undefineSymbol Symbols : " + undefinedSymbols );

		return undefinedSymbols;
	} 
	
	
	private synchronized INamespace activateNamespace(IStrategoList namespaceId) throws SpxSymbolTableException{
		return activateNamespace(NamespaceUri.toSpxID(namespaceId));
	}
	
	private synchronized INamespace activateNamespace(String namespaceId) throws SpxSymbolTableException{
		
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
	
	private boolean containsNamespace(INamespace namespace) { return this.containsNamespace(namespace.namespaceUri().id());}

	private void ensureActiveNamespaceUnloaded(INamespace namespace){
		if(namespace!=null)
			this.ensureActiveNamespaceUnloaded(namespace.namespaceUri().id());
	}
	
	private synchronized void ensureActiveNamespaceUnloaded(String namespaceId){
		if( (_activeNamespace !=null) && _activeNamespace.namespaceUri().equalSpoofaxId(namespaceId)){
			_activeNamespace = null;
		}
	}
	
	private ISpxPersistenceManager persistenceManager(){ return _facade.getPersistenceManager(); }
	
	private INamespace removeNamespace(INamespace nsToRemove){
		if(nsToRemove != null){
			System.out.println("Removing following namespace : "+ nsToRemove);
			
			if(nsToRemove instanceof PackageNamespace){
				NamespaceUri internalNamespaceUri = PackageNamespace.packageInternalNamespace(nsToRemove.namespaceUri(), _facade);
				this.namespaces.remove(internalNamespaceUri.id());
				this.ensureActiveNamespaceUnloaded(internalNamespaceUri.id());
			}
			this.namespaces.remove(nsToRemove.namespaceUri().id());
			this.ensureActiveNamespaceUnloaded(nsToRemove.namespaceUri().id());
		}
		return nsToRemove;
	}

	private INamespace removeNamespaceByUri(String id){
		INamespace ns = this.resolveNamespace(id);
		
		return removeNamespace(ns);
	}
		
	private List<INamespace> removeNamespacesById(IStrategoList id){
		List<INamespace> namespaces = resolveNamespaces(id);
		
		for(INamespace n : namespaces)
			removeNamespace(n);
		
		return namespaces;
	}
	
	private List<INamespace> resolveNamespaces(IStrategoList id){
		return resolveNamespaces(NamespaceUri.toSpxID(id));
	}
	
	private List<INamespace> resolveNamespaces(String id){
		List<INamespace> toReturn = new ArrayList<INamespace>();

		INamespace n = namespaces.get(id);
		if(n !=null){ 
			toReturn.add(n); 
		}
		
		return toReturn;
	}
	
	//TODO : refactor this time-stamps to a separate class. It is not the right 
	// place for the following codes.
	long getIndexLastInitializedOn(){ 
		Long initializedOn = timestamps.get(INITIALIZED_ON_KEY);
		
		if(initializedOn ==null) 
			return 0;
		
		return initializedOn;
	}

	long getLastCodeGeneratedOn(){ 
		Long lastCodeGenOn = timestamps.get(LAST_CODEGEN_ON_KEY);
		
		if(lastCodeGenOn ==null) 
			return 0;
		
		return lastCodeGenOn;
	}

	void setIndexUpdatedOn(long timestamp){ 
		timestamps.put(INITIALIZED_ON_KEY, timestamp);
	}
	
	void setLastCodeGeneratedOn(long timestamp){ 
		timestamps.put(LAST_CODEGEN_ON_KEY, timestamp);
	}
}
