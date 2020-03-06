/*
 * Created on 27. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms.io;

import static org.spoofax.terms.AbstractTermFactory.EMPTY_TERM_ARRAY;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.io.binary.TermReader;
import org.spoofax.terms.util.NotImplementedException;

/**
 * A Term Reader that reads a term in Textual ATerm Format. <br>
 * <b>A note on Unicode:</b> This class internally uses a {@link PushbackReader} to read the input string/stream,
 * character by character. Here, one "character" is one UTF-16 code unit, as used internally for representing Strings in
 * Java. No special handling of multi-character Unicode code points is needed, as the strings are literally copied.
 * 
 * @see TermReader An extension of this class that also supports binary ATerms.
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class TAFTermReader {

    protected final StringBuilder sharedBuilder = new StringBuilder();
    protected final ITermFactory factory;

    public TAFTermReader(ITermFactory factory) {
        this.factory = factory;
    }

    public IStrategoTerm parseFromFile(String path) throws IOException, ParseError {
        try(InputStream stream = new FileInputStream(path)) {
            return parseFromStream(stream);
        }
    }

    public IStrategoTerm parseFromString(String s) throws ParseError {
        try(PushbackReader reader = new PushbackReader(new StringReader(s))) {
            return parseFromStream(reader);
        } catch(IOException e) {
            // This should never happen, as the string reader is not closed before we're finished reading.
            throw new RuntimeException(e);
        }
    }

    public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException, ParseError {
        try {
            if(!(inputStream instanceof BufferedInputStream))
                inputStream = new BufferedInputStream(inputStream);
            PushbackReader bis = new PushbackReader(new InputStreamReader(inputStream));

            return parseFromStream(bis);
        } finally {
            inputStream.close();
        }
    }

    protected IStrategoTerm parseFromStream(PushbackReader bis) throws IOException, ParseError {
        parseSkip(bis);
        final int ch = bis.read();
        switch(ch) {
            case '[':
                return parseAnno(bis, parseList(bis));
            case '(':
                return parseAnno(bis, parseTuple(bis));
            case '"':
                return parseAnno(bis, parseString(bis));
            case '<':
                return parsePlaceholder(bis);
            case '!':
                throw new ParseError("Unsupported ATerm format: TAF");
            default:
                bis.unread(ch);
                if(isLetter(ch))
                    return parseAnno(bis, parseAppl(bis));
                else if(isDigit(ch) || ch == '-')
                    return parseAnno(bis, parseNumber(bis));
        }
        throw new ParseError("Invalid start of term: 0x" + String.format("%04x", ch) + " '" + (char) ch + "'");
    }

    private IStrategoTerm parseAnno(PushbackReader bis, IStrategoTerm term) throws IOException, ParseError {
        parseSkip(bis);
        final int ch = bis.read();
        if(ch == '{') {
            List<IStrategoTerm> annos = parseTermSequence(bis, '}');
            return factory.annotateTerm(term, factory.makeList(annos));
        } else {
            bis.unread(ch);
            return term;
        }
    }

    private IStrategoTerm parseString(PushbackReader bis) throws IOException, ParseError {
        int ch = bis.read();
        if(ch == '"')
            return factory.makeString("");
        StringBuilder sb = sharedBuilder;
        sb.setLength(0);
        boolean escaped;
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
                        sb.append('\\').append((char) ch);
                }
                ch = bis.read();
            } else if(ch != '\"') {
                if(ch == -1)
                    throw new ParseError("Unterminated string: " + sb);
                sb.append((char) ch);
                ch = bis.read();
            }
        } while(escaped || ch != '\"');
        return factory.makeString(sb.toString());
    }

    private IStrategoTerm parseAppl(PushbackReader bis) throws IOException, ParseError {
        // System.err.println("appl");
        StringBuilder sb = sharedBuilder;
        sb.setLength(0);
        int ch;

        ch = bis.read();
        do {
            sb.append((char) ch);
            ch = bis.read();
        } while(isConstructorChar(ch));

        // System.err.println(" - " + sb.toString());

        bis.unread(ch);
        parseSkip(bis);
        ch = bis.read();

        String constructor = sb.toString();
        if(ch == '(') {
            List<IStrategoTerm> l = parseTermSequence(bis, ')');
            IStrategoConstructor c = factory.makeConstructor(constructor, l.size());
            return factory.makeAppl(c, l.toArray(EMPTY_TERM_ARRAY));
        } else {
            bis.unread(ch);
            IStrategoConstructor c = factory.makeConstructor(constructor, 0);
            return factory.makeAppl(c, EMPTY_TERM_ARRAY);
        }
    }

    private IStrategoTerm parsePlaceholder(PushbackReader bis) throws IOException, ParseError {
        IStrategoTerm template = parseFromStream(bis);
        parseSkip(bis);
        if(bis.read() != '>')
            throw new ParseError("Expected: '>'");
        return factory.makePlaceholder(template);
    }

    private IStrategoTerm parseTuple(PushbackReader bis) throws IOException, ParseError {
        // System.err.println("tuple");
        return factory.makeTuple(parseTermSequence(bis, ')').toArray(EMPTY_TERM_ARRAY));
    }

    private List<IStrategoTerm> parseTermSequence(PushbackReader bis, char endChar) throws IOException, ParseError {
        // System.err.println("sequence");
        List<IStrategoTerm> els = Collections.emptyList();
        parseSkip(bis);
        int ch = bis.read();
        if(ch == endChar)
            return els;
        els = new ArrayList<>();
        bis.unread(ch);
        do {
            els.add(parseFromStream(bis));
            parseSkip(bis);
            ch = bis.read();
        } while(ch == ',');

        if(ch != endChar) {
            bis.unread(ch);
            parseSkip(bis);
            ch = bis.read();
        }

        if(ch != endChar)
            throw new ParseError("Sequence must end with '" + endChar + "', saw '" + (char) ch + "' '"
                + (char) bis.read() + "' after items " + els);

        return els;
    }

    private IStrategoTerm parseList(PushbackReader bis) throws IOException, ParseError {
        // System.err.println("list");
        return factory.makeList(parseTermSequence(bis, ']'));
    }

    private IStrategoTerm parseNumber(PushbackReader bis) throws IOException {
        // System.err.println("number");
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

    private String parseDigitSequence(PushbackReader bis) throws IOException {
        StringBuilder sb = sharedBuilder;
        sb.setLength(0);
        int ch = bis.read();
        do {
            sb.append((char) ch);
            ch = bis.read();
        } while(isDigit(ch));
        bis.unread(ch);
        return sb.toString();
    }

    private void parseSkip(PushbackReader input) throws IOException {
        for(;;) {
            int b = input.read();
            switch(b) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    continue;
                default:
                    input.unread(b);
                    return;
            }
        }
    }

    // We only support English digits/letters.
    // Character.is{Digit,Letter,LetterOrDigit} also return true for other alphabets, so we have custom methods
    private static boolean isDigit(int ch) {
        return '0' <= ch && ch <= '9';
    }

    private static boolean isLetter(int ch) {
        return 'A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z';
    }

    private static boolean isLetterOrDigit(int ch) {
        return isLetter(ch) || isDigit(ch);
    }

    private static boolean isConstructorChar(int ch) {
        if(isLetterOrDigit(ch))
            return true;
        switch(ch) {
            case '_':
            case '-':
            case '+':
            case '*':
            case '$':
                return true;
            default:
                return false;
        }
    }

    public void unparseToFile(IStrategoTerm t, OutputStream ous) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(ous));
        unparseToFile(t, out);
        out.flush();
    }

    public void unparseToFile(IStrategoTerm t, Writer out) throws IOException {
        t.writeAsString(out, Integer.MAX_VALUE);
    }

}
