/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

// This is the main program of the Scigol interpreter
package scigol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import antlr.ANTLRException;
import antlr.CharBuffer;
import antlr.RecognitionException;
import antlr.SemanticException;
import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.collections.AST;

class Scigol {
    protected static NamespaceScope globalScope = NamespaceScope
            .newGlobalNamespaceScope();

    protected static boolean debug = false;

    public static void executeAllInDirectory(final File dir) { /*
                                                                * FileSystemInfo[
                                                                * ] infos = dir.
                                                                * GetFileSystemInfos
                                                                * ("*");
                                                                * foreach(
                                                                * FileSystemInfo
                                                                * info in infos)
                                                                * { if (info is
                                                                * DirectoryInfo)
                                                                * executeAllInDirectory
                                                                * (info as
                                                                * DirectoryInfo
                                                                * ); else {
                                                                * FileInfo fi =
                                                                * info as
                                                                * FileInfo; if
                                                                * (fi.Extension
                                                                * == ".sg") { if
                                                                * (debug)
                                                                * Console
                                                                * .WriteLine
                                                                * ("executing "
                                                                * +fi.FullName);
                                                                * executeFile
                                                                * (fi.FullName);
                                                                * } else if
                                                                * (fi.Extension
                                                                * == ".dll") {
                                                                * try { if
                                                                * (debug)
                                                                * Console
                                                                * .WriteLine(
                                                                * "loading assembly "
                                                                * +fi.FullName);
                                                                * NamespaceScope
                                                                * .
                                                                * loadAssembly(fi
                                                                * .FullName); }
                                                                * catch
                                                                * (Exception e)
                                                                * {} // ignore
                                                                * what we can't
                                                                * load } } }
                                                                */
        System.out
                .println("execute all in directory unimplemented: dir=" + dir);
    }

    protected static Object executeCommand(final String command) {
        // Debug.WriteLine("executing command:"+command);
        final boolean supressOutput = command.endsWith(";");
        Value result = null;
        try {
            result = executeLine(command);
            final Object resultValue = (result == null) ? null : result
                    .getValue();
            if (!supressOutput) {
                if ((result != null) && result.isLValue()) {
                    final Symbol s = result.getLValue().getSymbol();
                    if (!s.getName().equals("operator()")) {
                        System.out.print(s.getName() + " = ");
                    } else {
                        System.out.print(" = ");
                    }
                }

                if (resultValue == null) {
                    System.out.println("null");
                } else {
                    System.out.println(resultValue.toString());
                }
            }
        } catch (final ANTLRException e) {
            System.out.println("error:" + e);
            if (debug) {
                e.printStackTrace();
            }
        } catch (final ScigolException e) {
            System.out.println("error:" + e);
            if (debug) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected static Object executeFile(final String filename)
            throws java.io.FileNotFoundException, RecognitionException,
            TokenStreamException {
        final CombinedSharedInputState istate = new CombinedSharedInputState();
        final LexerSharedInputStateWrapper listate = new LexerSharedInputStateWrapper(
                istate, new FileReader(filename));
        final ParserSharedInputStateWrapper pistate = new ParserSharedInputStateWrapper(
                istate);

        final ScigolLexer lexer = new ScigolLexer(listate);
        lexer.setTokenObjectClass("scigol.CommonTokenWithLocation");
        lexer.setFilename(filename);

        final ScigolParser parser = new ScigolParser(pistate);
        parser.setTokenBuffer(new TokenBuffer(lexer));
        parser.setASTNodeClass("scigol.CommonASTWithLocation");
        parser.setFilename(filename);

        // parse
        parser.program();

        final AST t = parser.getAST();
        // Console.Out.WriteLine("AST:\n"+t.ToStringTree());

        final ScigolTreeParser treeParser = new ScigolTreeParser(globalScope,
                false);

        treeParser.setASTNodeClass("scigol.CommonASTWithLocation");
        // treeParser.setASTFactory

        return treeParser.program(t);
    }

    protected static Value executeLine(final String line)
            throws RecognitionException, TokenStreamException {
        final StringReader sin = new StringReader(line);
        final CombinedSharedInputState istate = new CombinedSharedInputState();
        final LexerSharedInputStateWrapper listate = new LexerSharedInputStateWrapper(
                istate, new CharBuffer(sin));
        final ParserSharedInputStateWrapper pistate = new ParserSharedInputStateWrapper(
                istate);
        final ScigolLexer lexer = new ScigolLexer(listate);
        lexer.setFilename("<interactive>");

        final ScigolParser parser = new ScigolParser(pistate);
        parser.setTokenBuffer(new TokenBuffer(lexer));
        parser.setFilename("<interactive>");

        // parse as a single expression
        parser.expression();

        final AST t = parser.getAST();
        // Console.Out.WriteLine("AST:\n"+t.ToStringTree());

        final ScigolTreeParser treeParser = new ScigolTreeParser(globalScope,
                true);
        return treeParser.expr(t);

    }

    protected static void interactive() throws java.io.IOException {
        System.out
                .println("Scigol interpreter 0.7 (BETA!) (interactive mode, '.help' for help; CTRL-D to exit)");

        boolean quit = false;

        globalScope.usingAll(); // bring all namespace into scope for
                                // conveinence

        final String defaultPrompt = "> ";
        final String contPrompt = "-> ";

        String prompt = defaultPrompt;
        String command = "";
        final BufferedReader bin = new BufferedReader(new InputStreamReader(
                System.in));

        while (!quit) {
            System.out.print(prompt);
            final String line = bin.readLine();
            if (line == null) {
                quit = true;
            }

            if (!quit) {
                if (line.endsWith("\\")) { // incomplete
                    command += line.substring(0, line.length() - 1);
                    prompt = contPrompt;
                } else if ((command + line).length() > 0) { // execute
                    prompt = defaultPrompt;
                    command += line;

                    // first handle special interactive commands
                    if (command.startsWith(".")) { // interactive command

                        if (command.startsWith(".help")) {
                            System.out
                                    .println("Input is interpreted as Scigol expressions.  Lines can be continued");
                            System.out
                                    .println(" by appending '\\'.  All interactive commands start with '.'");
                            System.out.println("Commands:");
                            System.out
                                    .println(" .run <filename.sg> - execute an external .sg file");
                            System.out
                                    .println(" .using <namespace> - bring members of 'namespace' into scope");
                            System.out
                                    .println(" .ls [<namespace>]  - list members in the specified namespace, or the current scope");
                            System.out
                                    .println(" .typeof <name>     - show full type information for 'name'");
                            System.out
                                    .println(" .exit              - exit the interpreter (same as CTRL-D)");
                            command = "";
                        } else if (command.equals(".exit")) {
                            quit = true;
                            command = "";
                        } else if (command.startsWith(".ls")) {
                            final int space = command.indexOf(' ');
                            if (space == -1) {
                                System.out.println(globalScope);
                            } else {
                                final String name = command
                                        .substring(space + 1);
                                final NamespaceScope nsScope = globalScope
                                        .getNamespaceScope(name);
                                if (nsScope == null) {
                                    System.out.println("Scigol: no namespace '"
                                            + name + "' found");
                                }
                                System.out.println(nsScope);
                            }
                            command = "";
                        } else if (command.startsWith(".typeof")) {
                            final int space = command.indexOf(' ');
                            if (space == -1) {
                                System.out
                                        .println("Scigol: invalid name, use .typeof <name>");
                            } else {
                                final String name = command
                                        .substring(space + 1);
                                try {
                                    final Value v = (Value) executeCommand(name
                                            + ";");
                                    if (v.getValue() != null) {
                                        Object t = v.getValue();
                                        if (!(t instanceof TypeSpec)) { // name
                                                                        // was a
                                                                        // value,
                                                                        // deduce
                                                                        // it's
                                                                        // type
                                            // unwrap Any or Num first
                                            t = TypeSpec.unwrapAnyOrNum(t);
                                            t = TypeSpec.typeOf(t);
                                        }
                                        final TypeSpec type = (TypeSpec) t;
                                        if (type.isClass()) {
                                            System.out.println(type
                                                    .getClassInfo()
                                                    .toStringFull());
                                        } else {
                                            System.out.println(type);
                                        }
                                    } else {
                                        System.out.println("value of " + name
                                                + " is null (type 'any')");
                                    }
                                } catch (final ScigolException e) {
                                    System.out.println(e);
                                }
                            }
                            command = "";
                        } else if (command.startsWith(".using")) {
                            final int space = command.indexOf(' ');
                            if (space == -1) {
                                System.out
                                        .println("Scigol: invalid namespace, use .using <namespace>");
                            } else {
                                final String namespaceName = command
                                        .substring(space + 1);
                                try {
                                    globalScope
                                            .addUsingNamespace(namespaceName);
                                } catch (final ScigolException e) {
                                    System.out.println(e);
                                }
                            }
                            command = "";
                        } else if (command.startsWith(".run")) {
                            final int space = command.indexOf(' ');
                            if (space == -1) {
                                System.out
                                        .println("Scigol: invalid filename, use .run <filename>");
                            } else {
                                final String filename = command
                                        .substring(space + 1);

                                try {
                                    executeFile(filename);
                                } catch (final ANTLRException e) {
                                    System.out.println("error:" + e);
                                    if (debug) {
                                        e.printStackTrace();
                                    }
                                } catch (final ScigolException e) {
                                    System.out.println("caught exception:" + e);
                                    if (debug) {
                                        e.printStackTrace();
                                    }
                                } catch (final Exception e) {
                                    System.out.println("caught exception:" + e);
                                    if (debug) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            command = "";
                        } else {
                            System.out
                                    .println("Scigol: unknown interactive command:"
                                            + command);
                            command = "";
                        }

                    }

                    if (command.length() > 0) { // execute Scigol expression
                        executeCommand(command);
                    }

                    command = "";
                }
            }

        } // while
        System.out.println();
    }

    public static void main(final String[] args) {
        final ArrayList argList = new ArrayList();
        for (final String s : args) {
            argList.add(s);
        }

        // process options
        boolean trace = false;
        boolean nolibs = false;

        final String usage = "sgint <options> [scigol filename.sg]\n"
                + "  -help    - usage\n"
                + "  -debug   - enable debugging info (for debugging the interpreter itself)\n"
                + "  -trace   - show scigol func calls\n"
                + "  -nolibs  - don't include any library files\n";

        String nextArg = (argList.size() > 0) ? ((String) argList.get(0)) : "";
        while (nextArg.startsWith("-")) {
            if ((nextArg.equals("-help")) || (nextArg.equals("--help"))) {
                System.out.println(usage);
                argList.remove(0);
                return;
            } else if (nextArg.equals("-debug")) {
                debug = true;
                argList.remove(0);
            } else if (nextArg.equals("-trace")) {
                trace = true;
                argList.remove(0);
            } else if (nextArg.equals("-nolibs")) {
                nolibs = true;
                argList.remove(0);
            } else {
                System.out.println("invalid option " + argList.get(0));
                System.out.println(usage);
                return;
            }
            nextArg = (argList.size() > 0) ? ((String) argList.get(0)) : "";
        } // while

        String filename = null;
        if (argList.size() > 0) {
            filename = (String) argList.get(0);
        }

        try {

            // listate = new LexerSharedInputStateWrapper(istate, new
            // CharBuffer(Console.In));
            // filename = "<stdin>";

            if (!nolibs) {
                // execute all the .sg files in the libs directory & load all
                // .dll assemblies
                // currently the libs must be either in the current directory,
                // or specified in the
                // environment variable SGLIBPATH
                File libsDir = null;

                // get env variable and try to find the dir
                final String libDirName = System.getenv("SGLIBPATH");
                if (libDirName != null) {
                    try {
                        libsDir = new File(libDirName);
                        if (!libsDir.exists()) {
                            libsDir = null;
                        }
                    } catch (final Exception e) {
                    }
                    if ((libsDir == null) && (libDirName != "")) {
                        System.out
                                .println("sgint: Warning - library directory '"
                                        + libDirName
                                        + "' from environment variable SGLIBPATH wasn't found");
                    }
                }

                // if no lib dir was found, try <current dir>/libs
                if (libsDir == null) {
                    libsDir = new File("libs");
                    if (!libsDir.exists()) {
                        libsDir = null;
                    }
                }

                if (libsDir != null) { // found the libs directory, read
                                       // everything in it
                    executeAllInDirectory(libsDir);
                }
            }

            if (filename != null) {
                executeFile(filename);
            } else {
                interactive();
            }

        } catch (final SemanticException e) {
            System.out.println("Scigol: semantic error - " + e);
            if (debug) {
                e.printStackTrace();
            }
        } catch (final TokenStreamException e) {
            System.out.println("Scigol: parse error - " + e);
            if (debug) {
                e.printStackTrace();
            }
        } catch (final RecognitionException e) {
            System.out.println("Scigol: parse error - " + e);
            if (debug) {
                e.printStackTrace();
            }
        } catch (final InternalScigolError e) {
            System.out
                    .println("Scigol: internal intepreter error "
                            + "(this is a Scigol implementation fault, not necessarily a problem with your program or the run-time libraries.)"
                            + "  Please report it to the Scigol maintainer.\n"
                            + e + "\n");
            e.printStackTrace();
        } catch (final ScigolException e) {
            System.out.println("Scigol: error - " + e);
            if (debug) {
                e.printStackTrace();
            }
        } catch (final Throwable t) {
            Throwable e = t;
            String s = "Scigol: runtime error - ";
            int count = 1;
            while (e != null) {
                s += "(" + count + ") " + e.toString();
                e = e.getCause();
                count++;
                if (e != null) {
                    s += "\n";
                }
            }

            System.out.println(s);
            if (debug) {
                t.printStackTrace();
            }
        }
    }
}
