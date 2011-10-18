package org.spoofax.interpreter.library.language.spxlang.index.data;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.TermConverter;

/**
 * Represents ModuleDefinition 
 *  
 * @author Md. Adil Akhter
 */
public class ModuleDefinition  extends ModuleDeclaration 
{
	private static final long serialVersionUID = -5355795992567198473L;
	
	private IStrategoAppl ast;
	private IStrategoAppl analyzedAst;
	
	/**
	 * @param resourceAbsPath
	 * @param id
	 * @param packageID
	 * @param ast
	 * @param analyzedAst
	 */
	private ModuleDefinition(String resourceAbsPath, IStrategoList id, IStrategoList packageID , IStrategoAppl ast , IStrategoAppl analyzedAst) {
		super(resourceAbsPath, id, packageID);
		
		this.ast = ast;
		this.analyzedAst  = analyzedAst;
	}
	
	public ModuleDefinition(ModuleDeclaration moduleDecl, IStrategoAppl ast , IStrategoAppl analyzedAst) {
		this(moduleDecl.resourceAbsPath , moduleDecl.id, moduleDecl.enclosingPackageID , ast , analyzedAst);
	}
	
	/* Transform {@link ModuleDefinition} to following terms :
	 * 		ModuleDef :  Module * String * Package * Module * Module -> Def
	 *  
	 * (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.ModuleDeclaration#toTerm(org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	@Override
	public IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade) {
		ITermFactory termFactory = idxFacade.getTermFactory();
		IStrategoTerm moduleDeclarationTerm = super.toTerm(idxFacade);
		IStrategoConstructor moduleDefCons = idxFacade.getModuleDefCon();
		
		IStrategoTerm retTerm = termFactory.makeAppl(
				moduleDefCons,
				moduleDeclarationTerm.getSubterm(0),
				moduleDeclarationTerm.getSubterm(1),
				moduleDeclarationTerm.getSubterm(2),
				ast,
				analyzedAst 
				);
		
		return retTerm;
	}
	
}