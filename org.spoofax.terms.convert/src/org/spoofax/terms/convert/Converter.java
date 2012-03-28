package org.spoofax.terms.convert;

import java.io.File;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.binary.SAFWriter;
import org.spoofax.terms.io.binary.TermReader;

/*
 * Acts as a container for the intermediate Stratego Term
 * 
 * Provides methods to Read and Write a term file from/to 
 * implemented formats.
 * 
 */

public class Converter {
	
	private final ITermFactory factory;
	private final IStrategoTerm term;
	
	/*
	 * Constructor which sets the default Term factory 
	 * and constructs the internal Stratego term based
	 * on @inputFile
	 */
	public Converter(String inputFile) throws Exception
	{
		factory = new TermFactory();
		term = new TermReader(factory).parseFromFile(inputFile);
	}
	
	/*
	 * Writes the term in desired format to a file 
	 */
	public void WriteTerm(TermFormats targetFormat, String outputFile) throws Exception
	{
		switch (targetFormat)
		{
			case SAF:
				SAFWriter.writeTermToSAFFile(term, new File(outputFile));
				break;
			default:
				throw new Exception("Term write format not implemented");
		}
	}
}
