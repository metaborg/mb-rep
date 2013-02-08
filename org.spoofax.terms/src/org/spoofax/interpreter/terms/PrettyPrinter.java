/*
 * Created on 29. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

public class PrettyPrinter implements ITermPrinter {

    private int indentationLevel;
    private char[] indentationPrefix;
    private char[] spaces;
    private StringBuilder builder;
    
    // FIXME this is butt ugly
    private boolean nextIndentOff;
    
    public PrettyPrinter() {
        
        spaces = new char[1024];
        for(int i = 0; i < spaces.length; i++)
            spaces[i] = ' ';
        
        indentationLevel = 0;
        
        builder = new StringBuilder();
        indentationPrefix = "".toCharArray();
        
    }

    public void println(String s) {
        println(s, true);
    }

    public void println(String s, boolean indent) {
        print(s, indent);
        builder.append('\n');
    }

    public void indent(int delta) {
        indentationLevel += delta;
        recomputeIndentationPrefix();
    }

    public void print(String s) {
        print(s, true);
    }

    public void outdent(int delta) {
        indentationLevel -= delta;        
        recomputeIndentationPrefix();
    }
    
    private void recomputeIndentationPrefix() {
        // FIXME improve
        indentationPrefix = new char[indentationLevel];
        for(int i = 0; i < indentationLevel; i++) {
            indentationPrefix[i] = spaces[i];
        }
    }

    public String getString() {
        return builder.toString();
    }

    public void print(String s, boolean indent) {
        printIndent(indent);
        builder.append(s);    
    }

    private void printIndent(boolean doit) {
        if(doit && !nextIndentOff)
            builder.append(indentationPrefix);
        nextIndentOff = false;
    }
    
    public void nextIndentOff() {
        nextIndentOff = true;
    }

}
