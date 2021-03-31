/*
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.function.Supplier;

import org.spoofax.terms.ParseError;
import org.spoofax.terms.util.NotImplementedException;

/**
 * An ATerm reader that visits a term in Textual ATerm Format. Despite the name of this class, it does not fully support
 * the TAF ATerm format, as it does not support sharing. <br>
 *
 * <b>A note on Unicode:</b> This class internally uses a {@link PushbackReader} to read the input string/stream,
 * character by character. Here, one "character" is one UTF-16 code unit, as used internally for representing Strings in
 * Java. No special handling of multi-character Unicode code points is needed, as the strings are literally copied.
 * 
 * @see TAFTermReader The class this class is based on.
 */
public class TAFVisitingTermReader {

    private final StringBuilder sharedBuilder = new StringBuilder();


    public TAFVisitingTermReader() {
    }


    public void parseFromFile(String path, TermVisitor visitor) throws IOException, ParseError {
        try(InputStream stream = new FileInputStream(path)) {
            parseFromStream(stream, visitor);
            return;
        }
    }

    public void parseFromString(String s, TermVisitor visitor) throws ParseError {
        try(PushbackReader reader = new PushbackReader(new StringReader(s))) {
            parseFromStream(reader, visitor);
            return;
        } catch(IOException e) {
            // This should never happen, as the string reader is not closed before we're finished reading.
            throw new RuntimeException(e);
        }
    }

    public void parseFromStream(InputStream inputStream, TermVisitor visitor) throws IOException, ParseError {
        try {
            if(!(inputStream instanceof BufferedInputStream))
                inputStream = new BufferedInputStream(inputStream);
            PushbackReader bis = new PushbackReader(new InputStreamReader(inputStream));

            parseFromStream(bis, visitor);
            return;
        } finally {
            inputStream.close();
        }
    }

    void parseFromStream(PushbackReader bis, TermVisitor visitor) throws IOException, ParseError {
        parseSkip(bis);
        final int ch = bis.read();
        switch(ch) {
            case '[':
                parseList(bis, visitor);
                break;
            case '(':
                parseTuple(bis, visitor);
                break;
            case '"':
                parseString(bis, visitor);
                break;
            case '<':
                parsePlaceholder(bis, visitor);
                break;
            case '!':
                throw new ParseError("Unsupported ATerm format: TAF");
            default:
                bis.unread(ch);
                if(isLetter(ch)) {
                    parseAppl(bis, visitor);
                } else if(isDigit(ch) || ch == '-') {
                    parseNumber(bis, visitor);
                } else {
                    throw new ParseError(
                            "Invalid start of term: 0x" + String.format("%04x", ch) + " '" + (char) ch + "'");
                }
                break;
        }
    }


    private void parseAnno(PushbackReader bis, TermVisitor visitor) throws IOException, ParseError {
        parseSkip(bis);
        final int ch = bis.read();
        if(ch == '{') {
            parseTermSequence(bis, '}', visitor::visitAnnotation);
            return;
        } else {
            bis.unread(ch);
            return;
        }
    }


    private void parseString(PushbackReader bis, TermVisitor visitor) throws IOException, ParseError {
        int ch = bis.read();
        if(ch == '"') {
            visitor.visitString("");
        } else {
            StringBuilder sb = getSharedBuilder();
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
            visitor.visitString(sb.toString());
        }
        parseAnno(bis, visitor);
        visitor.endString();
    }

    private void parseAppl(PushbackReader bis, TermVisitor visitor) throws IOException, ParseError {
        // System.err.println("appl");
        StringBuilder sb = getSharedBuilder();
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
        visitor.visitAppl(constructor);
        if(ch == '(') {
            parseTermSequence(bis, ')', visitor::visitSubTerm);
        } else {
            bis.unread(ch);
        }
        parseAnno(bis, visitor);
        visitor.endAppl();
    }

    private void parsePlaceholder(PushbackReader bis, TermVisitor visitor) throws IOException, ParseError {
        parseFromStream(bis, visitor.visitPlaceholder());
        parseSkip(bis);
        if(bis.read() != '>') {
            throw new ParseError("Expected: '>'");
        }
    }

    private void parseTuple(PushbackReader bis, TermVisitor visitor) throws IOException, ParseError {
        // System.err.println("tuple");
        visitor.visitTuple();
        parseTermSequence(bis, ')', visitor::visitSubTerm);
        parseAnno(bis, visitor);
        visitor.endTuple();
    }

    private void parseTermSequence(PushbackReader bis, char endChar, Supplier<TermVisitor> visitor)
            throws IOException, ParseError {
        // System.err.println("sequence");
        parseSkip(bis);
        int ch = bis.read();
        if(ch == endChar)
            return;
        bis.unread(ch);
        do {
            parseFromStream(bis, visitor.get());
            parseSkip(bis);
            ch = bis.read();
        } while(ch == ',');

        if(ch != endChar) {
            bis.unread(ch);
            parseSkip(bis);
            ch = bis.read();
        }

        if(ch != endChar)
            throw new ParseError(
                    "Sequence must end with '" + endChar + "', saw '" + (char) ch + "' '" + (char) bis.read() + "'");
    }

    private void parseList(PushbackReader bis, TermVisitor visitor) throws IOException, ParseError {
        // System.err.println("list");
        visitor.visitList();
        parseTermSequence(bis, ']', visitor::visitSubTerm);
        parseAnno(bis, visitor);
        visitor.endList();
    }

    private void parseNumber(PushbackReader bis, TermVisitor visitor) throws IOException {
        // System.err.println("number");
        String whole = parseDigitSequence(bis);

        int ch = bis.read();
        if(ch == '.') {
            String frac = parseDigitSequence(bis);
            ch = bis.read();
            if(ch == 'e' || ch == 'E') {
                String exp = parseDigitSequence(bis);
                double d = Double.parseDouble(whole + "." + frac + "e" + exp);
                visitor.visitReal(d);
            } else {
                bis.unread(ch);
                double d = Double.parseDouble(whole + "." + frac);
                visitor.visitReal(d);
            }
            parseAnno(bis, visitor);
            visitor.endReal();
        } else {
            bis.unread(ch);
            visitor.visitInt(Integer.parseInt(whole));
            parseAnno(bis, visitor);
            visitor.endInt();
        }
    }

    private String parseDigitSequence(PushbackReader bis) throws IOException {
        StringBuilder sb = getSharedBuilder();
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

    private StringBuilder getSharedBuilder() {
        sharedBuilder.setLength(0);
        return sharedBuilder;
    }

}