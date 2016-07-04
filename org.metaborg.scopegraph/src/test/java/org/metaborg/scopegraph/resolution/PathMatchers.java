package org.metaborg.scopegraph.resolution;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.resolution.path.Path;
import org.metaborg.scopegraph.resolution.path.Paths;

public class PathMatchers {

    public static Matcher<Path> pathToDecl(final Occurrence decl) {
        return new TypeSafeMatcher<Path>() {
           @Override public boolean matchesSafely(final Path item) {
               return decl.equals(Paths.getDeclaration((Path)item));
           }
           @Override
           public void describeTo(final Description description) {
              description.appendText("a Path to ").appendValue(decl);
           }
        };
     } 
    
    public static Matcher<Path> pathFromRef(final Occurrence ref) {
        return new TypeSafeMatcher<Path>() {
           @Override public boolean matchesSafely(final Path item) {
               return ref.equals(Paths.getReference((Path)item));
           }
           @Override
           public void describeTo(final Description description) {
              description.appendText("a Path from ").appendValue(ref);
           }
        };
     } 
 
}
