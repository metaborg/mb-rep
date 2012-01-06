package org.spoofax.interpreter.library.language;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Md. Adil Akhter <md.adilakhter add gmail.com>
 */
public class LANG_get_all_projects_in_Workspace extends AbstractPrimitive {

	private static String NAME = "LANG_get_all_projects_in_Workspace";
	
	public LANG_get_all_projects_in_Workspace() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ITermFactory factory = env.getFactory();
		IStrategoList results = factory.makeList();
		
		for (IProject project: projects) {
			if( project.isOpen()){
				IStrategoString projectName =factory.makeString( project.getName());
				IStrategoString projectPath =factory.makeString( project.getLocation().toString());
				
				// Creating tuple for each project entry. Tuple contains project name and project path.
				IStrategoTuple result = factory.makeTuple(
						projectName ,
						projectPath
				);		
				// Adding it to the head of the list 
				results = factory.makeListCons(result, results);
			}
		}
		env.setCurrent(results);
		return true;
	}

}
