package org.spoofax.interpreter.library.index.tests.performance;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized.Parameters;
import org.spoofax.interpreter.library.index.tests.IndexTest;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class IndexPerformanceTest extends IndexTest {
	private static int MAX_NUM_FILES = 1000;

	public static IStrategoAppl def1;
	public static IStrategoAppl def2;
	public static IStrategoAppl def3;
	public static IStrategoAppl use1;
	public static IStrategoAppl type1;
	public static IStrategoAppl typeTemplate1;
	public static IStrategoAppl def1Parent;
	public static IStrategoAppl def2Parent;
	public static IStrategoAppl def3Parent;
	public static IStrategoAppl use1Parent;
	public static IStrategoAppl typeTemplate1Parent;
	public static IStrategoTerm[] files;
	public static int fileIndex;

	protected int numItems;
	protected int numFiles;

	// @formatter:off
    @Parameters
    public static List<Object[]> data() {
        Object[][] data = new Object[][] {
            { 100   , 1   , false }
          , { 100   , 10  , false }
          , { 100   , 100 , false }
          , { 100   , 500 , false }
          , { 100   , 1000, false }
          , { 100   , 1   ,  true }
          , { 100   , 10  ,  true }
          , { 100   , 100 ,  true }
          , { 100   , 500 ,  true }
          , { 100   , 1000,  true }
          , { 1000  , 1   , false }
          , { 1000  , 10  , false }
          , { 1000  , 100 , false }
          , { 1000  , 500 , false }
          , { 1000  , 1000, false }
          , { 1000  , 1   ,  true }
          , { 1000  , 10  ,  true }
          , { 1000  , 100 ,  true }
          , { 1000  , 500 ,  true }
          , { 1000  , 1000,  true }
          , { 10000 , 1   , false }
          , { 10000 , 10  , false }
          , { 10000 , 100 , false }
          , { 10000 , 500 , false }
          , { 10000 , 1000, false }
          , { 10000 , 1   ,  true }
          , { 10000 , 10  ,  true }
          , { 10000 , 100 ,  true }
          , { 10000 , 500 ,  true }
          , { 10000 , 1000,  true }
          , { 20000 , 1   , false }
          , { 20000 , 10  , false }
          , { 20000 , 100 , false }
          , { 20000 , 500 , false }
          , { 20000 , 1000, false }
          , { 20000 , 1   ,  true }
          , { 20000 , 10  ,  true }
          , { 20000 , 100 ,  true }
          , { 20000 , 500 ,  true }
          , { 20000 , 1000,  true }
          , { 50000 , 1   , false }
          , { 50000 , 10  , false }
          , { 50000 , 100 , false }
          , { 50000 , 500 , false }
          , { 50000 , 1000, false }
          , { 50000 , 1   ,  true }
          , { 50000 , 10  ,  true }
          , { 50000 , 100 ,  true }
          , { 50000 , 500 ,  true }
          , { 50000 , 1000,  true }
        /*, { 100000, 1   , false }
          , { 100000, 10  , false }
          , { 100000, 100 , false }
          , { 100000, 500 , false }
          , { 100000, 1000, false }
          , { 100000, 1   ,  true }
          , { 100000, 10  ,  true }
          , { 100000, 100 ,  true }
          , { 100000, 500 ,  true }
          , { 100000, 1000,  true }
          , { 150000, 1   , false }
          , { 150000, 10  , false }
          , { 150000, 100 , false }
          , { 150000, 500 , false }
          , { 150000, 1000, false }
          , { 150000, 1   ,  true }
          , { 150000, 10  ,  true }
          , { 150000, 100 ,  true }
          , { 150000, 500 ,  true }
          , { 150000, 1000,  true }
          , { 200000, 1   , false }
          , { 200000, 10  , false }
          , { 200000, 100 , false }
          , { 200000, 500 , false }
          , { 200000, 1000, false }
          , { 200000, 1   ,  true }
          , { 200000, 10  ,  true }
          , { 200000, 100 ,  true }
          , { 200000, 500 ,  true }
          , { 200000, 1000,  true }*/
        };
        return Arrays.asList(data);
    }
    // @formatter:on

	public IndexPerformanceTest(int numItems, int numFiles) {
		this.numItems = numItems;
		this.numFiles = numFiles;
	}

	@BeforeClass
	public static void setUpOnce() {
		IndexTest.setUpOnce();

		def1 = def("Class", "java", "lang", "String");
		def1Parent = def("Class", "java", "lang");
		def2 = def("Method", "java", "lang", "System", "out", "println");
		def2Parent = def("Method", "java", "lang", "System", "out");
		def3 = def("Field", "java", "lang", "array", "Length");
		def3Parent = def("Field", "java", "lang", "array");
		use1 = use("Class", "java", "lang", "System");
		use1Parent = use("Class", "java", "lang");
		type1 = type(constructor("Type", str("String")), "Method", "java", "lang", "Object", "toString");
		typeTemplate1 = type(tuple(), "Method", "java", "lang", "Object", "toString");
		typeTemplate1Parent = type(tuple(), "Method", "java", "lang", "Object");

		files = new IStrategoTerm[MAX_NUM_FILES];
		for(int i = 0; i < MAX_NUM_FILES; ++i) {
			files[i] = source("Source" + i);
		}

		fileIndex = -1;
	}

	@Before
	public void setUp() {
		fileIndex = -1;
	}

	protected IStrategoTerm getNextFile() {
		IStrategoTerm file = files[++fileIndex];
		if(fileIndex == this.numFiles - 1)
			fileIndex = -1;
		return file;
	}
}
