/*
 * Created on 27. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.util.NotImplementedException;
import org.spoofax.terms.util.PushbackStringIterator;

/**
 * @author Karl T. Kalleberg <karltk add strategoxt.org>
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class StringTermReader {

	protected final ITermFactory factory;
	
    public StringTermReader(ITermFactory factory) {
    	this.factory = factory;
    }
    
    public ITermFactory getFactory() {
		return factory;
	}

    public IStrategoTerm parseFromString(String s) throws ParseError {
        return parseFromString(new PushbackStringIterator(s));
    }

    protected IStrategoTerm parseFromString(PushbackStringIterator bis) throws ParseError {
        parseSkip(bis);
        final int ch = bis.read();
        switch(ch) {
        case '[': return parseAnno(bis, parseList(bis));
        case '(': return parseAnno(bis, parseTuple(bis));
        case '"': return parseAnno(bis, parseString(bis));
        case '<': return parsePlaceholder(bis);
        default:
            bis.unread(ch);
            if (Character.isLetter((char)ch)) {
                return parseAnno(bis, parseAppl(bis));
            }
            else if (Character.isDigit((char)ch) || ch == '-')
                return parseAnno(bis, parseNumber(bis));
        }
        throw new ParseError("Invalid term: '" + (char)ch + "'");
    }
    
    private IStrategoTerm parseAnno(PushbackStringIterator bis, IStrategoTerm term) throws ParseError {
        parseSkip(bis);
        final int ch = bis.read();
        if (ch == '{') {
            List<IStrategoTerm> annos = parseTermSequence(bis, '}');
            if (annos.size()==0)
                return factory.annotateTerm(term, TermFactory.EMPTY_LIST); 
            return factory.annotateTerm(term, factory.makeList(annos)); 
        } else {
            bis.unread(ch);
            return term;
        }
    }

    private IStrategoTerm parseString(PushbackStringIterator bis) throws ParseError {
        int ch = bis.read();
        if(ch == '"')
            return factory.makeString("");
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        do {
            escaped = false;
            if(ch == '\\') {
                escaped = true;
                ch = bis.read();
            }
            if(escaped) {
                switch(ch) {
                case 'n':
                    sb.append('\n');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case '\\':
                    sb.append('\\');
                    break;
                case '\'':
                    sb.append('\'');
                    break;
                case '\"':
                    sb.append('\"');
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    throw new NotImplementedException();
                default:
                    sb.append("\\" + (char)ch); 
                }
                ch = bis.read();
            } else if(ch != '\"') {
                if (ch == -1)
                    throw new ParseError("Unterminated string: " + sb);
                sb.append((char)ch);
                ch = bis.read();
            }
        } while(escaped || ch != '\"');
        return factory.makeString(sb.toString());
    }

    private IStrategoTerm parseAppl(PushbackStringIterator bis) throws ParseError {
        //System.err.println("appl");
        // TODO: share stringbuilder instances?
        StringBuilder sb = new StringBuilder();
        int ch;
        
        ch = bis.read();
        do {
            sb.append((char)ch);
            ch = bis.read();
        } // TODO: use a switch for this
          while(Character.isLetterOrDigit((char)ch) || ch == '_' || ch == '-'
            || ch == '+' || ch == '*' || ch == '$');
        
        //System.err.println(" - " + sb.toString());
        
        bis.unread(ch);
        parseSkip(bis);
        ch = bis.read();

        if(ch == '(') {
            List<IStrategoTerm> l = parseTermSequence(bis, ')');
            IStrategoConstructor c = factory.makeConstructor(sb.toString(), l.size());
            return factory.makeAppl(c, l.toArray(new IStrategoTerm[l.size()]));
        } else {
            bis.unread(ch);
            IStrategoConstructor c = factory.makeConstructor(sb.toString(), 0);
            return factory.makeAppl(c, AbstractTermFactory.EMPTY);
        }
    }
    
    private IStrategoTerm parsePlaceholder(PushbackStringIterator bis) throws ParseError {
        IStrategoTerm template = parseFromString(bis);
        parseSkip(bis);
        if (bis.read() != '>')
            throw new ParseError("Expected: '>'");
        return factory.makePlaceholder(template);
    }

    private IStrategoTerm parseTuple(PushbackStringIterator bis) throws ParseError {
        //System.err.println("tuple");
        return factory.makeTuple(parseTermSequence(bis, ')').toArray(AbstractTermFactory.EMPTY));
    }

    private List<IStrategoTerm> parseTermSequence(PushbackStringIterator bis, char endChar) throws ParseError {
        //System.err.println("sequence");
        List<IStrategoTerm> els = Collections.emptyList();
        parseSkip(bis);
        int ch = bis.read();
        if(ch == endChar)
            return els;
        els = new ArrayList<IStrategoTerm>();
        bis.unread(ch);
        do {
            els.add(parseFromString(bis));
            parseSkip(bis);
            ch = bis.read();
        } while(ch == ',');
        
        if (ch != endChar) {
            bis.unread(ch);
            parseSkip(bis);
            ch = bis.read();
        }

        if(ch != endChar)
            throw new ParseError("Sequence must end with '" + endChar + "', saw '" + (char)ch + "'");
        
        return els;
    }

    private IStrategoTerm parseList(PushbackStringIterator bis) throws ParseError {
        //System.err.println("list");
        return factory.makeList(parseTermSequence(bis, ']'));
    }

    private IStrategoTerm parseNumber(PushbackStringIterator bis) throws ParseError {
        //System.err.println("number");
        String whole = parseDigitSequence(bis);
        
        int ch = bis.read();
        if(ch == '.') {
            String frac = parseDigitSequence(bis);
            ch = bis.read();
            if(ch == 'e' || ch == 'E') {
                String exp = parseDigitSequence(bis);
                double d = Double.parseDouble(whole + "." + frac + "e" + exp);
                return factory.makeReal(d);
            }
            bis.unread(ch);
            double d = Double.parseDouble(whole + "." + frac);
            return factory.makeReal(d);
        }
        bis.unread(ch);
        return factory.makeInt(Integer.parseInt(whole));
    }

    private String parseDigitSequence(PushbackStringIterator bis) throws ParseError {
        StringBuilder sb = new StringBuilder();
        int ch = bis.read();
        do {
            sb.append((char)ch);
            ch = bis.read();
        } while(Character.isDigit((char)ch));
        bis.unread(ch);
        return sb.toString(); 
    }
    
    private void parseSkip(PushbackStringIterator input) throws ParseError {
        for (;;) {
            int b = input.read();
            switch (b) {
                case ' ': case '\t': case '\n':
                    continue;
                default:
                    input.unread(b);
                    return;
            }
        }
    }

}
