package org.metaborg.scopegraph.resolution;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.metaborg.scopegraph.resolution.PathMatchers.*;

import org.junit.Ignore;
import org.junit.Test;
import static org.metaborg.scopegraph.Labels.*;
import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;
import org.metaborg.scopegraph.impl.DefaultLabel;
import org.metaborg.scopegraph.impl.DefaultOccurrence;
import org.metaborg.scopegraph.impl.DefaultScopeGraph;

public class ReachabilityTest {

    @Ignore @Test public void single() {
        DefaultScopeGraph g = new DefaultScopeGraph();
        Scope s = g.createScope();
        Occurrence decl = new DefaultOccurrence("","x",1);
        g.addDeclaration(s, decl);
        Occurrence ref = new DefaultOccurrence("","x",2);
        g.addReference(ref, s);
        Reachability r = new Reachability(g);
        r.print();
        assertThat(r.resolve(new DefaultOccurrence("","x",2)),
                hasItem(pathToDecl(decl)));
    }

    @Ignore @Test public void direct() {
        DefaultScopeGraph g = new DefaultScopeGraph();

        Scope s1 = g.createScope();
        Occurrence decl = new DefaultOccurrence("","x",1);
        g.addDeclaration(s1, decl);

        Scope s2 = g.createScope();
        Occurrence ref = new DefaultOccurrence("","x",2);
        g.addReference(ref, s2);
        
        g.addDirectEdge(s2, new DefaultLabel("P"), s1);
        
        Reachability r = new Reachability(g);
        r.print();
        assertThat(r.resolve(ref),hasItem(pathToDecl(decl)));
    }

    @Ignore @Test public void named() {
        DefaultScopeGraph g = new DefaultScopeGraph();

        Scope s1 = g.createScope();
        Occurrence mdecl = new DefaultOccurrence("","M",2);
        Occurrence mref = new DefaultOccurrence("","M",3);
        g.addDeclaration(s1, mdecl);
        g.addReference(mref, s1);

        Scope s3 = g.createScope();
        Occurrence xdecl = new DefaultOccurrence("","x",1);
        g.addDeclaration(s3, xdecl);
        g.addExportEdge(mdecl, new DefaultLabel("I"), s3);
        
        Scope s2 = g.createScope();
        Occurrence xref = new DefaultOccurrence("","x",4);
        g.addReference(xref, s2);
        g.addImportEdge(s2, new DefaultLabel("I"), mref);

        Reachability r = new Reachability(g);
        r.print();
        assertThat(r.resolve(mref),hasItem(pathToDecl(mdecl)));
        assertThat(r.resolve(xref),hasItem(pathToDecl(xdecl)));
    }

    @Ignore @Test public void directCycle() {
        DefaultScopeGraph g = new DefaultScopeGraph();

        Scope s1 = g.createScope();
        Occurrence decl = new DefaultOccurrence("","x",1);
        g.addDeclaration(s1, decl);

        Scope s2 = g.createScope();
        Occurrence ref = new DefaultOccurrence("","x",2);
        g.addReference(ref, s2);
        
        g.addDirectEdge(s2, new DefaultLabel("P"), s1);
        g.addDirectEdge(s1, new DefaultLabel("P"), s2);
        
        Reachability r = new Reachability(g);
        r.print();
        assertThat(r.resolve(ref),hasItem(pathToDecl(decl)));
    }
 
    @Ignore @Test public void nestedNames() {
        DefaultScopeGraph g = new DefaultScopeGraph();

        Scope s1 = g.createScope();
        Occurrence outer_decl = new DefaultOccurrence("","x",1);
        Occurrence ref = new DefaultOccurrence("","x",2);
        g.addDeclaration(s1, outer_decl);
        g.addReference(ref, s1);

        Scope s2 = g.createScope();
        Occurrence inner_decl = new DefaultOccurrence("","x",3);
        g.addDeclaration(s2, inner_decl);
 
        g.addExportEdge(outer_decl, new DefaultLabel("I"), s2);
        g.addImportEdge(s1, new DefaultLabel("I"), ref);

        Reachability r = new Reachability(g);
        r.print();
        assertThat(r.resolve(ref),hasItem(pathToDecl(outer_decl)));
        assertThat(r.resolve(ref),not(hasItem(pathToDecl(inner_decl))));
    }
 
    @Test public void anomaly() {
        DefaultScopeGraph g = new DefaultScopeGraph();

        Scope s1 = g.createScope();
        
        Occurrence outer_A_decl = new DefaultOccurrence("","A",1);
        g.addDeclaration(s1, outer_A_decl);
        Scope s2 = g.createScope();
        g.addExportEdge(outer_A_decl, label("I"), s2);
        Occurrence inner_B_decl = new DefaultOccurrence("","B",2);
        g.addDeclaration(s2, inner_B_decl);

        Occurrence outer_B_decl = new DefaultOccurrence("","B",3);
        g.addDeclaration(s1, outer_B_decl);
        Scope s3 = g.createScope();
        g.addExportEdge(outer_B_decl, label("I"), s3);
        Occurrence inner_A_decl = new DefaultOccurrence("","A",4);
        g.addDeclaration(s3, inner_A_decl);
        
        Scope s4 = g.createScope();
        g.addDirectEdge(s4, label("P"), s1);
        
        Occurrence A_ref = new DefaultOccurrence("","A",5);
        g.addReference(A_ref, s4);
        g.addImportEdge(s4, label("I"), A_ref);

        Occurrence B_ref = new DefaultOccurrence("","B",6);
        g.addReference(B_ref, s4);
        g.addImportEdge(s4, label("I"), B_ref);
        
        Reachability r = new Reachability(g);
        r.print();
    }
    
}