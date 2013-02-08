package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.terms.attachments.OriginAttachment.tryGetOrigin;

import java.io.File;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.IToken;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

/**
 * Primitive to verify whether the provided two Spx Symbol has equal origin
 * 
 * @author Md. Adil Akhter
 */
public class SPX_symtab_verify_symbols_have_equal_origin extends AbstractPrimitive {
	private static String NAME = "SPX_symtab_verify_symbols_have_equal_origin";
	private final static int NO_ARGS = 2;
	
	public SPX_symtab_verify_symbols_have_equal_origin(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
	}
	
	
	@Override
	public final boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		IStrategoTerm tvar1 = tvars[0];
		IStrategoTerm tvar2 = tvars[1];
		
		return equal(tvar1, tvar2);
		
	}
	
	private static boolean equal(IStrategoTerm t1, IStrategoTerm t2) {
		ImploderAttachment origin1 = ImploderAttachment.get(tryGetOrigin(t1));
		if (origin1 == null) return false;
		if (t1 == t2) return true;
		
		ImploderAttachment origin2 = ImploderAttachment.get(tryGetOrigin(t2));
		return verifyOriginsAreEqual(origin1, origin2);
	}
	
	/**
	 * Verify the ImploderAttachment provided as arguments and return true 
	 * if they are valid and if they contain same origin info. Thus, if both attachments 
	 * are null, it is returning false.
	 * 
	 * @param attachment First Attachment as argument 
	 * @param otherAttachment Second Attachment 
	 * @return true if the provided attachments are valid and contain same origin info, e.g. Line No , Column No , Start Offset and End Offset are same.
	 */
	private static boolean verifyOriginsAreEqual( ImploderAttachment attachment , ImploderAttachment otherAttachment ){
		if(attachment == otherAttachment) 
			return true;	
		else
		{	
			if (attachment == null || otherAttachment == null) {
				return false;
			} else {
				IToken leftToken = attachment.getLeftToken();
				IToken otherLeftToken = otherAttachment.getLeftToken();

				if (leftToken == null || otherLeftToken == null) {
					return false;
				}
				if ((leftToken.getLine() != otherLeftToken.getLine())
						|| (leftToken.getColumn() != otherLeftToken.getColumn())
						|| (leftToken.getStartOffset() != otherLeftToken
								.getStartOffset()))
					return false;

				IToken rightToken = attachment.getRightToken();
				IToken otherRightToken = otherAttachment.getRightToken();

				if (rightToken == null || otherRightToken == null) {
					return false;
				}
				if (rightToken.getEndOffset() != otherRightToken.getEndOffset())
					return false;

				String fileLocation = leftToken.getTokenizer().getFilename();
				String otherFileLocation = otherLeftToken.getTokenizer()
						.getFilename();

				if (fileLocation != null) {
					if (otherFileLocation == null) {
						return false;
					}
				} else if (otherFileLocation != null) {
					return false;
				}

				if (fileLocation == null && otherFileLocation == null) {
					return false;
				} else if (!(new File(fileLocation))
						.getAbsolutePath()
						.equalsIgnoreCase(
								(new File(otherFileLocation)).getAbsolutePath()))
					return false;
			}
		}
	
		return true;
	}
}
