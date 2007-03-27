package org.spoofax;

import java.io.IOException;

import org.spoofax.interpreter.Interpreter;
import org.spoofax.interpreter.InterpreterException;
import org.spoofax.interpreter.InterpreterExit;
import org.spoofax.interpreter.adapter.aterm.WrappedATermFactory;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class ATermInterpreter {

	private Interpreter interpreter;
	private WrappedATermFactory termFactory;
	
	public ATermInterpreter() {
		termFactory = new WrappedATermFactory();
		interpreter = new Interpreter(termFactory);
	}

	public static void main(String args[]) throws IOException {

        ATermInterpreter itp = new ATermInterpreter();
        String[] files = null;
        boolean waitForProfiler = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--debug")) {
                DebugUtil.setDebug(true);
            } else if (args[i].equals("-i")) {
                files = args[i + 1].split(",");
            } else if (args[i].equals("--wait-for-profiler")) {
                waitForProfiler = true;
            } else if (args[i].equals("/trace")) {
                DebugUtil.tracing = true;
            }
        }

        try {
//            long loadTime = System.nanoTime();
            for(String fn : files) {
//                System.out.println("Loading " + fn);
                itp.load(fn);
            }
//            System.out.println("Load time: " + (System.nanoTime() - loadTime)/1000/1000 + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterpreterException e) {
            e.printStackTrace();
        }

        try {
//            long runTime = System.nanoTime();
            itp.setCurrent(itp.getFactory().makeList());
            
            boolean r = itp.invoke("main_0_0");
            
//            System.out.println("Run time: " + (System.nanoTime() - runTime)/1000/1000 + "ms");
            
            if(r) {
                System.out.println("" + itp.current());
            } else {
                System.err.println("rewriting failed");
                System.exit(-1);
            }
        } catch (InterpreterExit e) {
            System.out.println("Exit with status: "  + e.getValue());
        } catch (InterpreterException e) {
            e.printStackTrace();
        }
        
        if(waitForProfiler)
            System.in.read();
    }

	private void setCurrent(IStrategoTerm term) {
		interpreter.setCurrent(term);
	}

	private IStrategoTerm current() {
		return interpreter.current();
	}

	private boolean invoke(String string) throws InterpreterException {
		return interpreter.invoke(string);
	}

	private ITermFactory getFactory() {
		return interpreter.getFactory();
	}

	private void load(String fn) throws IOException, InterpreterException {
		interpreter.load(fn);
	}
	
}

