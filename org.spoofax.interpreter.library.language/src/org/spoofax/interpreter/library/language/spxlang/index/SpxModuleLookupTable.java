package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jdbm.PrimaryStoreMap;
import jdbm.PrimaryTreeMap;
import jdbm.RecordListener;
import jdbm.RecordManager;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.language.spxlang.index.data.IdentifiableConstruct;
import org.spoofax.interpreter.library.language.spxlang.index.data.LanguageDescriptor;
import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxCompilationUnitInfo;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class SpxModuleLookupTable implements ICompilationUnitRecordListener,
		IPackageDeclarationRecordListener {

	/**
	 * Listeners which are notified about changes in records
	 */
	protected List<RecordListener<IStrategoList, ModuleDeclaration>> recordListeners = new ArrayList<RecordListener<IStrategoList, ModuleDeclaration>>();

	/*
	 * FIXME : Using separate HashMap due to the consideration of converting
	 * them store map to load module AST lazily.
	 */

	private final PrimaryStoreMap<Long, String> _moduleDefinition;
	private final PrimaryStoreMap<Long, String> _moduleAnalyzedDefinition;
	private final PrimaryTreeMap<String, ModuleDeclaration> _moduleLookupMap;

	private final SecondaryTreeMap<String, String, ModuleDeclaration> _moduleByFileAbsPath;
	private final SecondaryTreeMap<String, String, ModuleDeclaration> _moduleByPackageId;
	private final SecondaryTreeMap<String, String, ModuleDeclaration> _modulesByLangaugeName;

	private final ISpxPersistenceManager _manager;
	private final ITermFactory _termFactory;
	private final String SRC = this.getClass().getSimpleName();

	/**
	 * Instantiates a lookup table for the base constructs (e.g. , packages and
	 * modules)of Spoofaxlang.
	 * 
	 * @param tableName
	 *            name of the table
	 * @param manager
	 *            an instance of {@link ISpxPersistenceManager}
	 */
	public SpxModuleLookupTable(ITermFactory f, ISpxPersistenceManager manager , RecordManager recordManagerInstance) {
		String tableName = SRC + "_" + manager.getIndexId();

		assert manager != null;

		_manager = manager;
		_termFactory = f;

		_moduleLookupMap = recordManagerInstance.treeMap(tableName + "._lookupModuleMap.idx");

		// read-only secondary view of the the lookup table .
		_moduleByFileAbsPath = _moduleLookupMap.secondaryTreeMap(tableName
				+ "._moduleByFileAbsPath.idx",
				new SecondaryKeyExtractor<String, String, ModuleDeclaration>() {

					/**
					 * Returns the Secondary key of the lookup table.
					 * 
					 * @param key
					 *            current primary key
					 * @param value
					 *            value to be mapped using primary key
					 * @return secondary key to map the value with .
					 */
					public String extractSecondaryKey(String key,
							ModuleDeclaration value) {
						return value.resourceAbsPath;
					}
				});

		_moduleByPackageId = _moduleLookupMap.secondaryTreeMap(tableName
				+ "._moduleByPackageId.idx",
				new SecondaryKeyExtractor<String, String, ModuleDeclaration>() {

					/**
					 * Returns the Secondary key of the lookup table.
					 * 
					 * @param key
					 *            current primary key
					 * @param value
					 *            value to be mapped using primary key
					 * @return secondary key to map the value with .
					 */
					public String extractSecondaryKey(String key,
							ModuleDeclaration value) {
						try {
							return SpxIndexUtils
									.termToString(value.enclosingPackageID);
						} catch (IOException e) {
							return "";
						}
					}
				});

		this._moduleDefinition = recordManagerInstance.storeMap(tableName + "._moduleDefinition.idx");
		this._moduleAnalyzedDefinition = recordManagerInstance.storeMap(tableName	+ "._moduleAnalyzedDefinition.idx");

		_modulesByLangaugeName = _moduleLookupMap.secondaryTreeMapManyToOne(
						tableName + "._modulesByLangaugeName.idx",
						new SecondaryKeyExtractor<Iterable<String>, String, ModuleDeclaration>() {
							public Iterable<String> extractSecondaryKey(
									String key, ModuleDeclaration value) {
								
								if (value.getLanguageDescriptor() == null)
									return new ArrayList<String>();

								return value.getLanguageDescriptor()
										.asLanguageNameStrings();
							}

						});

		initRecordListener();
	}

	private void initRecordListener() {
		_moduleLookupMap
				.addRecordListener(new RecordListener<String, ModuleDeclaration>() {

					public void recordInserted(String key,
							ModuleDeclaration value) throws IOException {
						// do nothing
					}

					public void recordUpdated(String key,
							ModuleDeclaration oldValue,
							ModuleDeclaration newValue) throws IOException {
						// do nothing
					}

					public void recordRemoved(String key,
							ModuleDeclaration value) throws IOException {

						// cleanup other table to make it consistent

						_moduleDefinition.remove(value.getModuleAstRecId());
						_moduleAnalyzedDefinition.remove(value
								.getModuleAnalyzedAstRecId());

						if (!recordListeners.isEmpty()) {
							for (RecordListener<IStrategoList, ModuleDeclaration> rl : recordListeners) {
								rl.recordRemoved(value.getId(), value);
							}
						}
					}
				});
	}

	void verifyModuleIDExists(IStrategoList moduleId) {
		if (!containsModuleDeclaration(moduleId)) {
			throw new IllegalArgumentException("Unknown Module ID : "
					+ moduleId);
		}
	}

	/**
	 * size of the symbol-table
	 * 
	 * @return
	 */
	public int size() {
		assert _moduleLookupMap.size() == _moduleDefinition.size();
		assert _moduleLookupMap.size() == _moduleAnalyzedDefinition.size();

		return _moduleLookupMap.size();
	}

	/**
	 * Defines Module Definition in the SymbolTable
	 * 
	 * @param f
	 *            an instance of {@link SpxSemanticIndexFacade}
	 * @param decl
	 * @param originalModuleDefinition
	 * @param analyzedModuleDefinition
	 * @throws IOException
	 */
	public void define(SpxSemanticIndexFacade f, ModuleDeclaration decl,
			IStrategoAppl originalModuleDefinition,
			IStrategoAppl analyzedModuleDefinition) throws IOException {

		if (!this.containsModuleDeclaration(decl.getId())) {
			Long resId1 = addModuleDefinition(f, originalModuleDefinition);
			Long resId2 = addAnalyzedModuleDefinition(f,analyzedModuleDefinition);

			decl.setModuleAstRecId(resId1);
			decl.setModuleAnalyzedAstRecId(resId2);

			
			define(decl);
		} else {
			ModuleDeclaration mdecl2  = this.getModuleDeclaration(decl.getId());
			// module declaration is already in the map . Hence,
			// this operation will only updates analyzedModuleDefinition.
			updateAnalyzedModuleDefinition(f, mdecl2.getModuleAnalyzedAstRecId(),
					analyzedModuleDefinition);
		}
	}


	/**
	 * Defines a new entry in this symbol table
	 * 
	 * @param info
	 * @param compilationUnit
	 */
	void define(ModuleDeclaration decl) {
		_moduleLookupMap.put(toSpxID(decl.getId()), decl);
	}


	private Long addModuleDefinition(SpxSemanticIndexFacade f,
			IStrategoAppl moduleDefinition) throws IOException {
		return _moduleDefinition.putValue(SpxIndexUtils.serializeToString(
				f.getTermAttachmentSerializer(), moduleDefinition));
	}

	private Long addAnalyzedModuleDefinition(SpxSemanticIndexFacade f,
			IStrategoAppl moduleDefinition) throws IOException {
		return _moduleAnalyzedDefinition.putValue(SpxIndexUtils
				.serializeToString(f.getTermAttachmentSerializer(),
						moduleDefinition));
	}

	private void updateAnalyzedModuleDefinition(SpxSemanticIndexFacade f,
			Long resID, IStrategoAppl moduleDefinition) throws IOException {
		
		_moduleAnalyzedDefinition.put(resID, SpxIndexUtils.serializeToString(f.getTermAttachmentSerializer(), moduleDefinition));
	}

	public void defineLanguageDescriptor(IStrategoList moduleId,
			LanguageDescriptor newDesc) {
		ModuleDeclaration moduleDeclaration = getModuleDeclaration(moduleId);
		ModuleDeclaration newModuleDeclaration = ModuleDeclaration.newInstance(_termFactory, moduleDeclaration);
		
		if (moduleDeclaration != null) {
			newModuleDeclaration.setLanguageDescriptor(newDesc);
		} else
			throw new IllegalArgumentException("Unknown Module ID : "
					+ moduleId.toString());

		define(newModuleDeclaration);
	}

	/**
	 * Returns language descriptor associated with id
	 * 
	 * @param id
	 *            module id whose language descriptor is to be returned
	 * @return {@link LanguageDescriptor}
	 */
	public LanguageDescriptor getLangaugeDescriptor(IStrategoList id) {
		ModuleDeclaration moduleDeclaration = getModuleDeclaration(id);

		if (moduleDeclaration != null) {
			return moduleDeclaration.getLanguageDescriptor();
		} else
			throw new IllegalArgumentException("Unknown Module ID : "
					+ id.toString());
	}

	/**
	 * Removes {@link IdentifiableConstruct} from the lookup table mapped by the
	 * {@code id}
	 * 
	 * @param id
	 *            {@link IStrategoList} representing qualified ID of the
	 *            Construct
	 * @return {@link IdentifiableConstruct} mapped by {@code id}
	 */
	public ModuleDeclaration remove(IStrategoList id) {

		_manager.logMessage(SRC + ".remove", "Removing following Module : "
				+ id);

		return remove(toSpxID(id));
	}

	private ModuleDeclaration remove(String id) {
		// removing module declaration from the table
		// and returning it.
		ModuleDeclaration ret = _moduleLookupMap.remove(id);

		if (ret != null) {
			_manager.logMessage(SRC + ".remove", "Removed : " + ret);
		} else
			_manager.logMessage(SRC + ".remove", "Could not find : " + ret);

		return ret;
	}

	/**
	 * Returns {@link ModuleDeclaration} that is mapped by the specified
	 * {@code id} argument.
	 * 
	 * @param id
	 * @return
	 */
	public ModuleDeclaration getModuleDeclaration(IStrategoList id) {
		return _moduleLookupMap.get(toSpxID(id));
	}

	/**
	 * Check whether particular module exists in the Symbol Table
	 * 
	 * @param id
	 * @return
	 */
	public boolean containsModuleDeclaration(IStrategoList id) {
		return _moduleLookupMap.containsKey(toSpxID(id));
	}

	/**
	 * Gets a module definition
	 * 
	 * @param facade
	 *            an instance of {@links SpxSemanticIndexFacade}
	 * @param id
	 * 
	 * @return
	 */
	public IStrategoAppl getModuleDefinition(SpxSemanticIndexFacade facade,
			IStrategoList id) {

		ModuleDeclaration decl = getModuleDeclaration(id);
		if (decl == null)
			throw new IllegalArgumentException("Unknown Module ID : " + id.toString());

		IStrategoTerm deserializedTerm = SpxIndexUtils.deserializeToTerm(
				facade.getTermFactory(), facade.getTermAttachmentSerializer(),
				this._moduleDefinition.get(decl.getModuleAstRecId()));

		assert deserializedTerm instanceof IStrategoAppl : "Expected IStrategoAppl";
		return (IStrategoAppl) deserializedTerm;
	}

	/**
	 * Gets module definition (analysed)
	 * 
	 * @param f
	 *            an instance of {@links SpxSemanticIndexFacade}
	 * @param id
	 * 
	 * @return
	 */
	public IStrategoAppl getAnalyzedModuleDefinition(SpxSemanticIndexFacade f,
			IStrategoList id) {

		ModuleDeclaration decl = getModuleDeclaration(id);
		if (decl == null)
			throw new IllegalArgumentException("Unknown Module ID : "
					+ id.toString());

		IStrategoTerm deserializedTerm = SpxIndexUtils.deserializeToTerm(f
				.getTermFactory(), f.getTermAttachmentSerializer(),
				this._moduleAnalyzedDefinition.get(decl
						.getModuleAnalyzedAstRecId()));

		assert deserializedTerm instanceof IStrategoAppl : "Expected IStrategoAppl";

		return (IStrategoAppl) deserializedTerm;
	}

	/**
	 * Returns ModuleDeclarations mapped by a file path. It actually returns all
	 * the module declaration exists in a file .
	 * 
	 * @param absUri
	 * @return
	 */
	public Iterable<ModuleDeclaration> getModuleDeclarationsByUri(String absUri) {
		List<ModuleDeclaration> ret = new ArrayList<ModuleDeclaration>();
		Iterable<ModuleDeclaration> foundModuleDecls = _moduleByFileAbsPath
				.getPrimaryValues(absUri);

		if (foundModuleDecls != null) {
			for (ModuleDeclaration m : foundModuleDecls)
				ret.add(m);
		}

		return ret;
	}

	void verifyUriExists(String uri) throws SpxSymbolTableException {
		if (!containsUri(uri)) {
			throw new SpxSymbolTableException("Illegal URI argument " + uri);
		}
	}

	private boolean containsUri(String absPath) {
		return _moduleByFileAbsPath.containsKey(absPath);
	}

	public Iterable<ModuleDeclaration> getModuleDeclarationsByPackageId(
			IStrategoList packageID) {
		List<ModuleDeclaration> ret = new ArrayList<ModuleDeclaration>();
		String id = this.toSpxID(packageID);

		Iterable<ModuleDeclaration> foundModuleDecls = _moduleByPackageId
				.getPrimaryValues(id);
		if (foundModuleDecls != null) {
			for (ModuleDeclaration m : foundModuleDecls) {
				ret.add(m);
			}
		}
		return ret;
	}

	private void removeModuleDeclarationByPackageId(IStrategoList packageId) {
		_manager.logMessage(SRC + ".removeModuleDeclarationByPackageId",
				"removing all the enclosed module of package " + packageId);

		String id = this.toSpxID(packageId);
		Iterable<ModuleDeclaration> foundModuleDecls = _moduleByPackageId.getPrimaryValues(id);
		List<IStrategoList> toRemove = new ArrayList<IStrategoList>();
		
		if (foundModuleDecls != null) {
			for (ModuleDeclaration module : foundModuleDecls)
				toRemove.add(module.getId());
		}
		
		for ( IStrategoList mId : toRemove){ this.remove(mId) ;}
	}

	/**
	 * Returns the enclosing PackageID of the ModuleDeclaration with
	 * {@code moduleId}
	 * 
	 * @param moduleId
	 * @return {@link IStrategoList}
	 */
	public IStrategoList packageId(IStrategoList moduleId) {
		if (containsModuleDeclaration(moduleId)) {
			return getModuleDeclaration(moduleId).enclosingPackageID;
		}
		return null;
	}

	/**
	 * Removes all the module {@link ModuleDeclaration} located in the following
	 * URI : {@code absUri}
	 * 
	 * @param absUri
	 *            String representation of absolute path of the file
	 * 
	 */
	public void removeModuleDeclarationsByUri(String absUri) {
		_manager.logMessage(SRC + ".removeModuleDeclarationsByUri",
				"Removing all the module declared in " + absUri);

		Iterable<ModuleDeclaration> delList = _moduleByFileAbsPath.getPrimaryValues(absUri);

		List<IStrategoList> mIdsToRemove = new ArrayList<IStrategoList>();
		if (delList != null) {
			for (ModuleDeclaration m : delList) {
				mIdsToRemove.add(m.getId());
			}
		}

		for (IStrategoList l : mIdsToRemove){this.remove(l); }

		_manager.logMessage(SRC + ".removeModuleDeclarationsByUri", " removed "
				+ delList + " to remove from the table.");
	}

	/**
	 * Clears ModuleLookup Table
	 */
	public synchronized void clear() {
		_manager.logMessage(SRC + ".clear", "Removing " + this.size()
				+ " entries ");

		Iterator<String> keyIter = _moduleLookupMap.keySet().iterator();
		if (keyIter != null) {
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

				if (oldValue.getVersionNo() != newValue.getVersionNo()) {
					// Whenever compilation unit version no is updated ,
					// remove all the related module declaration
					// from the symbol table , since it is obsolete now.
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
	 * Returns all the {@link ModuleDeclaration} declared in the current
	 * package.
	 * 
	 * @return
	 */
	public Iterable<ModuleDeclaration> getModuleDeclarations() {
		return this._moduleLookupMap.values();
	}

	public RecordListener<IStrategoList, PackageDeclaration> getPackageDeclarationRecordListener() {
		return new RecordListener<IStrategoList, PackageDeclaration>() {

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

				removeModuleDeclarationByPackageId(packageID);
			}
		};
	}

	public void addRecordListener(final IModuleDeclarationRecordListener rl) {
		this.recordListeners.add(rl.getModuleDeclarationRecordListener());
	}

	public void removeRecordListener(final IModuleDeclarationRecordListener rl) {
		this.recordListeners.remove(rl.getModuleDeclarationRecordListener());
	}

	/**
	 * Returns the packages indexed using languageName
	 * 
	 * @param langaugeName
	 * @return
	 */
	public Iterable<IStrategoList> getModuleIdsByLangaugeName(
			String langaugeName) {
		Set<IStrategoList> mIds = new HashSet<IStrategoList>();

		Iterable<ModuleDeclaration> mDeclarations = this._modulesByLangaugeName
				.getPrimaryValues(langaugeName);

		if (mDeclarations != null) {
			for (ModuleDeclaration m : mDeclarations) {
				mIds.add(m.getId());
			}
		}
		return mIds;
	}

	public Iterable<IStrategoList> getModuleIdsByLangaugeName(
			IStrategoString langaugeName) {
		return getModuleIdsByLangaugeName(Tools.asJavaString(langaugeName));
	}

	protected String toSpxID(IStrategoList strategoId) {
		try {
			return SpxIndexUtils.termToString(strategoId);
		} catch (IOException e) {
			return "";
		}
	}

	protected IStrategoList toStrategoID(String moduleID) {
		return (IStrategoList) SpxIndexUtils.stringToTerm(_termFactory,
				moduleID);
	}

	// FOR TRACING
	// FIX ME: REMOVE IT AFTER TESTING
	@Override
	public String toString() {
		return "";
	}

}