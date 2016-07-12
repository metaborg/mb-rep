package org.metaborg.scopegraph.resolution;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.metaborg.scopegraph.Labels.label;
import static org.metaborg.scopegraph.Labels.order;
import static org.metaborg.scopegraph.resolution.PathMatchers.pathToDecl;

import java.util.Comparator;

import org.junit.Ignore;
import org.junit.Test;
import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Labels;
import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;
import org.metaborg.scopegraph.impl.DefaultLabel;
import org.metaborg.scopegraph.impl.DefaultOccurrence;
import org.metaborg.scopegraph.impl.DefaultScopeGraph;

import com.google.common.collect.ImmutableMultimap;

public class ConsistentVisibilityTest {
 
    @Ignore @Test public void shadow() {
        DefaultScopeGraph g = new DefaultScopeGraph();

        Scope s1 = g.createScope();
        Occurrence decl1 = new DefaultOccurrence("","x",1);
        g.addDeclaration(s1, decl1);

        Scope s2 = g.createScope();
        Occurrence decl2 = new DefaultOccurrence("","x",2);
        g.addDeclaration(s2, decl2);
        Occurrence ref = new DefaultOccurrence("","x",3);
        g.addReference(ref, s2);
 
        g.addDirectEdge(s2, new DefaultLabel("P"), s1);
 
        // lexical
        Comparator<Label> order1 = order(new ImmutableMultimap.Builder<Label,Label>()
                .put(Labels.D, label("P"))
                .build());
        ConsistentVisibility v1 = new ConsistentVisibility(g,order1);
        v1.print();
        assertThat(v1.resolve(ref),not(hasItem(pathToDecl(decl1))));
        assertThat(v1.resolve(ref),hasItem(pathToDecl(decl2)));

        // inverse lexical
        Comparator<Label> order2 = order(new ImmutableMultimap.Builder<Label,Label>()
                .put(label("P"), Labels.D)
                .build());
        ConsistentVisibility v2 = new ConsistentVisibility(g,order2);
        v2.print();
        assertThat(v2.resolve(ref),hasItem(pathToDecl(decl1)));
        assertThat(v2.resolve(ref),not(hasItem(pathToDecl(decl2))));

        // no shadowing
        Comparator<Label> order3 = order(new ImmutableMultimap.Builder<Label,Label>()
                .build());
        ConsistentVisibility v3 = new ConsistentVisibility(g,order3);
        v3.print();
        assertThat(v3.resolve(ref),hasItem(pathToDecl(decl1)));
        assertThat(v3.resolve(ref),hasItem(pathToDecl(decl2)));
    }

    @Ignore @Test public void importShadow() {
        DefaultScopeGraph g = new DefaultScopeGraph();

        Scope s1 = g.createScope();
        Occurrence M1 = new DefaultOccurrence("Mod","M",1);
        g.addDeclaration(s1, M1);
        Scope s2 = g.createScope();
        g.addExportEdge(M1, label("I"), s2);
        Occurrence x2 = new DefaultOccurrence("Var","x",2);
        g.addDeclaration(s2, x2);

        Scope s3 = g.createScope();
        g.addDirectEdge(s3, new DefaultLabel("P"), s1);
        Occurrence M3 = new DefaultOccurrence("Mod","M",3);
        g.addDeclaration(s3, M3);
        Scope s4 = g.createScope();
        g.addExportEdge(M3, label("I"), s4);
        Occurrence x4 = new DefaultOccurrence("Var","x",4);
        g.addDeclaration(s4, x4);

        Scope s5 = g.createScope();
        g.addDirectEdge(s5, new DefaultLabel("P"), s3);
        Occurrence M5 = new DefaultOccurrence("Mod","M",5);
        g.addReference(M5, s5);
        g.addImportEdge(s5, label("I"), M5);
        Occurrence x6 = new DefaultOccurrence("Var","x",6);
        g.addReference(x6, s5);
 
        Comparator<Label> order = order(new ImmutableMultimap.Builder<Label,Label>()
                .put(Labels.D, label("P"))
                .put(Labels.D, label("I"))
                .put(label("I"), label("P"))
                .build());
        ConsistentVisibility v = new ConsistentVisibility(g,order);
        v.print();
        assertThat(v.resolve(M5),hasItem(pathToDecl(M3)));
        assertThat(v.resolve(x6),hasItem(pathToDecl(x4)));
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
 
        Comparator<Label> order = order(new ImmutableMultimap.Builder<Label,Label>()
                .put(Labels.D, label("P"))
                .put(Labels.D, label("I"))
                .put(label("I"), label("P"))
                .build());
        ConsistentVisibility v = new ConsistentVisibility(g,order);
        v.print();
        assertTrue(v.resolve(A_ref).isEmpty());
        assertTrue(v.resolve(B_ref).isEmpty());
    }

}