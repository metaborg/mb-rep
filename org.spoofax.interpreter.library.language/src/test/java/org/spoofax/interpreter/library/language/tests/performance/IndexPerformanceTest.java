package org.spoofax.interpreter.library.language.tests.performance;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized.Parameters;
import org.spoofax.interpreter.library.language.IndexPartitionDescriptor;
import org.spoofax.interpreter.library.language.tests.IndexTest;
import org.spoofax.interpreter.terms.IStrategoAppl;

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
    public static IndexPartitionDescriptor[] files;
    public static int fileIndex;
    
    @Parameters
    public static List<Object[]> data() {
        Object[][] data = new Object[][] { 
              { 100   , 1    }
            , { 1000  , 1    }
            , { 10000 , 1    }
            , { 20000 , 1    }
            , { 50000 , 1    }
            , { 100000, 1    }
            , { 150000, 1    }
            , { 200000, 1    }
            , { 100   , 10   }
            , { 1000  , 10   }
            , { 10000 , 10   }
            , { 20000 , 10   }
            , { 50000 , 10   }
            , { 100000, 10   }
            , { 150000, 10   }
            , { 200000, 10   }
            , { 100   , 100  }
            , { 1000  , 100  }
            , { 10000 , 100  }
            , { 20000 , 100  }
            , { 50000 , 100  }
            , { 100000, 100  }
            , { 150000, 100  }
            , { 200000, 100  }
            , { 100   , 500  }
            , { 1000  , 500  }
            , { 10000 , 500  }
            , { 20000 , 500  }
            , { 50000 , 500  }
            , { 100000, 500  }
            , { 150000, 500  }
            , { 200000, 500  }
            , { 100   , 1000 }
            , { 1000  , 1000 }
            , { 10000 , 1000 }
            , { 20000 , 1000 }
            , { 50000 , 1000 }
            , { 100000, 1000 }
            , { 150000, 1000 }
            , { 200000, 1000 }
        };
        return Arrays.asList(data);
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

        files = new IndexPartitionDescriptor[MAX_NUM_FILES];
        for(int i = 0; i < MAX_NUM_FILES; ++i) {
            files[i] = IndexPartitionDescriptor.fromTerm(agent, file("File" + i));
        }

        fileIndex = -1;
    }

    @Before
    public void setUp() {
        fileIndex = -1;
    }

    public static IndexPartitionDescriptor getFile(int numFiles) {
        IndexPartitionDescriptor file = files[++fileIndex];
        if(fileIndex == numFiles - 1)
            fileIndex = -1;
        return file;
    }
}
