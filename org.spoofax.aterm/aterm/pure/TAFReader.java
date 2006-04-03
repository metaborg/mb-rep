/*
 * Created on 14.mar.2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk@ii.uib.no>
 * 
 * Licensed under the GNU General Public License, v2
 */
package aterm.pure;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.nio.ByteBuffer;

import aterm.ATerm;
import aterm.ATermList;
import aterm.ParseError;

public class TAFReader {

    PureFactory factory;
    // ATermReader reader;

    TAFReader(PureFactory factory) { //, ATermReader reader) {
        this.factory = factory;
        // this.reader = reader;
    }

    static boolean isBase64(int c) {
        return Character.isLetterOrDigit((char) c) || c == '+' || c == '/';
    }

    public ATerm parse(String trm) {
        try {

            ByteBuffer buffer = ByteBuffer.wrap(trm.getBytes());
            ATermReader reader = new ATermReader(buffer);
            reader.readSkippingWS();

            return parseFromReader(reader);
        }
        catch (IOException e) {
            throw new ParseError("premature end of string");
        }
    }


    private ATerm parseAbbrev(ATermReader reader) throws IOException {
        ATerm result;
        int abbrev;

        int c = reader.read();

        abbrev = 0;
        while (isBase64(c)) {
            abbrev *= 64;
            if (c >= 'A' && c <= 'Z') {
                abbrev += c - 'A';
            }
            else if (c >= 'a' && c <= 'z') {
                abbrev += c - 'a' + 26;
            }
            else if (c >= '0' && c <= '9') {
                abbrev += c - '0' + 52;
            }
            else if (c == '+') {
                abbrev += 62;
            }
            else if (c == '/') {
                abbrev += 63;
            }
            else {
                throw new RuntimeException("not a base-64 digit: " + c);
            }

            c = reader.read();
        }

        result = reader.getTerm(abbrev);

        return result;
    }

    private ATerm parseNumber(ATermReader reader) throws IOException {
        StringBuffer str = new StringBuffer();
        ATerm result;

        do {
            str.append((char) reader.getLastChar());
        }
        while (Character.isDigit((char) reader.read()));

        if (reader.getLastChar() != '.' && reader.getLastChar() != 'e' && reader.getLastChar() != 'E') {
            int val;
            try {
                val = Integer.parseInt(str.toString());
            }
            catch (NumberFormatException e) {
                throw new ParseError("malformed int");
            }
            result = factory.makeInt(val);
        }
        else {
            if (reader.getLastChar() == '.') {
                str.append('.');
                reader.read();
                if (!Character.isDigit((char) reader.getLastChar()))
                    throw new ParseError("digit expected");
                do {
                    str.append((char) reader.getLastChar());
                }
                while (Character.isDigit((char) reader.read()));
            }
            if (reader.getLastChar() == 'e' || reader.getLastChar() == 'E') {
                str.append((char) reader.getLastChar());
                reader.read();
                if (reader.getLastChar() == '-' || reader.getLastChar() == '+') {
                    str.append((char) reader.getLastChar());
                    reader.read();
                }
                if (!Character.isDigit((char) reader.getLastChar()))
                    throw new ParseError("digit expected!");
                do {
                    str.append((char) reader.getLastChar());
                }
                while (Character.isDigit((char) reader.read()));
            }
            double val;
            try {
                val = Double.valueOf(str.toString());
            }
            catch (NumberFormatException e) {
                throw new ParseError("malformed real");
            }
            result = factory.makeReal(val);
        }
        return result;
    }

    private String parseId(ATermReader reader) throws IOException {
        int c = reader.getLastChar();
        StringBuffer buf = new StringBuffer(32);

        do {
            buf.append((char) c);
            c = reader.read();
        }
        while (Character.isLetterOrDigit((char) c) || c == '_' || c == '-');

        return buf.toString();
    }

    private String parseString(ATermReader reader) throws IOException {
        boolean escaped;
        StringBuffer str = new StringBuffer();

        do {
            escaped = false;
            if (reader.read() == '\\') {
                reader.read();
                escaped = true;
            }

            if (escaped) {
                switch (reader.getLastChar()) {
                    case 'n' :
                        str.append('\n');
                        break;
                    case 't' :
                        str.append('\t');
                        break;
                    case 'b' :
                        str.append('\b');
                        break;
                    case 'r' :
                        str.append('\r');
                        break;
                    case 'f' :
                        str.append('\f');
                        break;
                    case '\\' :
                        str.append('\\');
                        break;
                    case '\'' :
                        str.append('\'');
                        break;
                    case '\"' :
                        str.append('\"');
                        break;
                    case '0' :
                    case '1' :
                    case '2' :
                    case '3' :
                    case '4' :
                    case '5' :
                    case '6' :
                    case '7' :
                        str.append(reader.readOct());
                        break;
                    default :
                        str.append('\\').append((char) reader.getLastChar());
                }
            }
            else if (reader.getLastChar() != '\"')
                str.append((char) reader.getLastChar());
        }
        while (escaped || reader.getLastChar() != '"');

        return str.toString();
    }

    private ATermList parseATerms(ATermReader reader) throws IOException {
        ATerm[] terms = parseATermsArray(reader);
        ATermList result = factory.getEmpty();
        for (int i = terms.length - 1; i >= 0; i--) {
            result = factory.makeList(terms[i], result);
        }

        return result;
    }

    private ATerm[] parseATermsArray(ATermReader reader) throws IOException {
        List list = new Vector();
        ATerm term;

        term = parseFromReader(reader);
        list.add(term);
        while (reader.getLastChar() == ',') {
            reader.readSkippingWS();
            term = parseFromReader(reader);
            list.add(term);
        }

        ATerm[] array = new ATerm[list.size()];
        ListIterator iter = list.listIterator();
        int index = 0;
        while (iter.hasNext()) {
            array[index++] = (ATerm) iter.next();
        }
        return array;
    }

    synchronized ATerm parseFromReader(ATermReader reader) throws IOException {
        ATerm result;
        int c, start, end;
        String funname;

        start = reader.getPosition();
        switch (reader.getLastChar()) {
            case -1 :
                throw new ParseError("premature EOF encountered.");

            case '#' :
                return parseAbbrev(reader);

            case '[' :

                c = reader.readSkippingWS();
                if (c == -1) {
                    throw new ParseError("premature EOF encountered.");
                }

                if (c == ']') {
                    c = reader.readSkippingWS();
                    result = (ATerm) factory.getEmpty();
                }
                else {
                    result = parseATerms(reader);
                    if (reader.getLastChar() != ']') {
                        throw new ParseError("expected ']' but got '" + (char) reader.getLastChar() + "'");
                    }
                    c = reader.readSkippingWS();
                }

                break;

            case '<' :

                c = reader.readSkippingWS();
                ATerm ph = parseFromReader(reader);

                if (reader.getLastChar() != '>') {
                    throw new ParseError("expected '>' but got '" + (char) reader.getLastChar() + "'");
                }

                c = reader.readSkippingWS();

                result = factory.makePlaceholder(ph);

                break;

            case '"' :

                funname = parseString(reader);

                c = reader.readSkippingWS();
                if (reader.getLastChar() == '(') {
                    c = reader.readSkippingWS();
                    if (c == -1) {
                        throw new ParseError("premature EOF encountered.");
                    }
                    if (reader.getLastChar() == ')') {
                        result = factory.makeAppl(factory.makeAFun(funname, 0, true));
                    }
                    else {
                        ATerm[] list = parseATermsArray(reader);

                        if (reader.getLastChar() != ')') {
                            throw new ParseError("expected ')' but got '" + reader.getLastChar() + "'");
                        }
                        result = factory.makeAppl(factory.makeAFun(funname, list.length, true), list);
                    }
                    c = reader.readSkippingWS();
                    if (c == -1) {
                        throw new ParseError("premature EOF encountered.");
                    }
                }
                else {
                    result = factory.makeAppl(factory.makeAFun(funname, 0, true));
                }

                break;

            case '(' :

                c = reader.readSkippingWS();
                if (c == -1) {
                    throw new ParseError("premature EOF encountered.");
                }
                if (reader.getLastChar() == ')') {
                    result = factory.makeAppl(factory.makeAFun("", 0, false));
                }
                else {
                    ATerm[] list = parseATermsArray(reader);

                    if (reader.getLastChar() != ')') {
                        throw new ParseError("expected ')' but got '" + reader.getLastChar() + "'");
                    }
                    result = factory.makeAppl(factory.makeAFun("", list.length, false), list);
                }
                c = reader.readSkippingWS();
                if (c == -1) {
                    throw new ParseError("premature EOF encountered.");
                }

                break;

            case '-' :
            case '0' :
            case '1' :
            case '2' :
            case '3' :
            case '4' :
            case '5' :
            case '6' :
            case '7' :
            case '8' :
            case '9' :
                result = parseNumber(reader);
                c = reader.skipWS();
                break;

            default :
                c = reader.getLastChar();
                if (Character.isLetter((char) c)) {

                    funname = parseId(reader);
                    c = reader.skipWS();
                    if (reader.getLastChar() == '(') {
                        c = reader.readSkippingWS();
                        if (c == -1) {
                            throw new ParseError("premature EOF encountered.");
                        }
                        if (reader.getLastChar() == ')') {
                            result = factory.makeAppl(factory.makeAFun(funname, 0, false));
                        }
                        else {
                            ATerm[] list = parseATermsArray(reader);

                            if (reader.getLastChar() != ')') {
                                throw new ParseError("expected ')' but got '" + reader.getLastChar() + "'");
                            }
                            result = factory.makeAppl(factory.makeAFun(funname, list.length, false), list);
                        }
                        c = reader.readSkippingWS();
                    }
                    else {
                        result = factory.makeAppl(factory.makeAFun(funname, 0, false));
                    }

                }
                else {
                    throw new ParseError("illegal character: " + reader.getLastChar());
                }
        }

        if (reader.getLastChar() == '{') {

            ATermList annos;
            if (reader.readSkippingWS() == '}') {
                reader.readSkippingWS();
                annos = factory.getEmpty();
            }
            else {
                annos = parseATerms(reader);
                if (reader.getLastChar() != '}') {
                    throw new ParseError("'}' expected");
                }
                reader.readSkippingWS();
            }
            result = result.setAnnotations(annos);

        }

        /* Parse some ToolBus anomalies for backwards compatibility */
        if (reader.getLastChar() == ':') {
            reader.read();
            ATerm anno = parseFromReader(reader);
            result = result.setAnnotation(parse("type"), anno);
        }

        if (reader.getLastChar() == '?') {
            reader.readSkippingWS();
            result = result.setAnnotation(parse("result"), parse("true"));
        }

        end = reader.getPosition();
        reader.storeNextTerm(result, end - start);

        return result;
    }
}
