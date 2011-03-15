/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
 */

package bilab;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import scigol.CombinedSharedInputState;
import scigol.InternalScigolError;
import scigol.LexerSharedInputStateWrapper;
import scigol.NamespaceScope;
import scigol.ParserSharedInputStateWrapper;
import scigol.ScigolException;
import scigol.ScigolLexer;
import scigol.ScigolParser;
import scigol.ScigolTreeParser;
import scigol.Symbol;
import scigol.TypeSpec;
import scigol.Value;
import antlr.ANTLRException;
import antlr.CharBuffer;
import antlr.RecognitionException;
import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.collections.AST;

// A scigol interactive console
public class ConsoleView extends ViewPart implements ISelectionProvider {
    protected class InputListener implements KeyListener {
        ConsoleView console;

        public InputListener(final ConsoleView c) {
            console = c;
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            console.handleInputKeyEvent(e);
        }

        @Override
        public void keyReleased(final KeyEvent e) {
        }
    }

    public static final String prompt = ">";

    protected static final int minContentHeight = 1032;
    protected static final int maxContentHeight = 10000;
    protected static final int minContentWidth = 640;

    public static final int userTextColor = SWT.COLOR_BLUE;

    public static final int fontPixelWidth = 8;
    public static final int fontPixelHeight = 10;

    // ISelectionProvider methods
    static ArrayList<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

    Value selectedValue = null;

    NamespaceScope globalScope;

    Display display;

    ScrolledComposite scrolledComposite;

    Composite content;

    FormLayout formLayout;

    ArrayList<Control> controls;

    Text inputControl;

    Font consoleFont;

    ArrayList<String> lineHistory;

    int historyPosition = 0;

    /**
     * The constructor.
     */
    public ConsoleView() {
        final BilabPlugin bilab = BilabPlugin.getDefault();
        globalScope = bilab.getGlobalScope();
    }

    @Override
    public void addSelectionChangedListener(
            final ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    protected void append(final Control control) {
        final FormData formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        control.setLayoutData(formData);

        controls.add(control);

        // determine the natural height for the content by summing for each
        // control
        // if height is above max content area height, discard controls from top
        int height = 0;

        final ArrayList<Control> removeList = new ArrayList<Control>();

        for (int i = controls.size() - 1; i >= 0; i--) {
            final Control c = controls.get(i);
            final Point s = c.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);

            if (height + s.y < maxContentHeight) {
                height += s.y;
            } else {
                removeList.add(c);
            }
        }

        height = Math.max(minContentHeight, height);

        for (final Control c : removeList) {
            controls.remove(c);
            c.dispose();
        }

        setLayoutData();
        content.setSize(currentWidth(), height);
        content.layout(true);
        content.redraw();

        scrollToEnd();
        scrolledComposite.layout(true); // size of content changed
        scrolledComposite.redraw();
    }

    public void append(final String lines) {
        append(splitLines(lines), false,
                display.getSystemColor(SWT.COLOR_BLACK));
    }

    protected void append(final String line, final boolean addToHistory,
            final Color color) {
        if (addToHistory) {
            lineHistory.add(line);
            if (lineHistory.size() > 1000) {
                lineHistory.remove(lineHistory.get(0));
            }
            historyPosition = lineHistory.size();
        }

        final Label l = new Label(getContentParent(), SWT.NONE);
        l.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        l.setText(line);
        l.setFont(consoleFont);
        l.setForeground(color);
        append(l);
    }

    public void append(final Value v, final String prefix) {
        if (v != null) {
            final Value value = TypeSpec.unwrapAnyOrNumValue(v);
            if (value == null) {
                append(prefix + "null");
            } else {
                boolean hasPrefix = false;
                Composite container;
                if ((prefix != null) && (prefix.length() > 0)) {
                    hasPrefix = true;
                    container = new Composite(content, SWT.NONE);
                    container.setBackground(display
                            .getSystemColor(SWT.COLOR_WHITE));

                    // setup a Grid layout with 2 columns, so that the prefix
                    // doesn't resize if the container
                    // resizes, but the Value does, taking the available space
                    final GridLayout gridLayout = new GridLayout();
                    gridLayout.numColumns = 2;
                    container.setLayout(gridLayout);

                    final Label prefixLabel = new Label(container, SWT.NONE);
                    prefixLabel.setBackground(display
                            .getSystemColor(SWT.COLOR_WHITE));
                    prefixLabel.setText(prefix);

                    final GridData gridData = new GridData();
                    gridData.horizontalAlignment = GridData.FILL;
                    gridData.verticalAlignment = GridData.BEGINNING;
                    prefixLabel.setLayoutData(gridData);
                } else {
                    container = content;
                }

                // instantiate a ValueViewer
                final SizedComposite sized = new SizedComposite(container,
                        SWT.NONE);

                final ValueViewer viewer = new ValueViewer(sized);
                viewer.setInput(value);

                final Point viewerPreferedSize = viewer.preferedSize();
                if (viewerPreferedSize.x == SWT.DEFAULT) {
                    viewerPreferedSize.x = currentWidth();
                }

                final Point viewerMaximumSize = viewer.maximumSize();
                if (viewerMaximumSize.x == SWT.MAX) {
                    viewerMaximumSize.x = currentWidth();
                }
                if (viewerMaximumSize.y == SWT.MAX) {
                    viewerMaximumSize.y = maxContentHeight / 2;
                }

                sized.setPreferedSize(viewerPreferedSize);
                sized.setMinimumSize(50, 24);
                sized.setMaximumSize(viewerMaximumSize.x,
                        Math.min(maxContentHeight / 2, viewerMaximumSize.y));

                if (hasPrefix) {
                    final GridData gridData = new GridData();
                    gridData.horizontalAlignment = GridData.FILL;
                    gridData.verticalAlignment = GridData.FILL;
                    gridData.grabExcessHorizontalSpace = true;
                    sized.setLayoutData(gridData);
                    append(container);
                } else {
                    append(sized);
                }
            }
        }
    }

    public void appendError(final String errorLines) {
        append(splitLines(errorLines), false,
                display.getSystemColor(SWT.COLOR_RED));
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(final Composite parent) {
        display = Display.getCurrent();

        scrolledComposite = new ScrolledComposite(parent, /* SWT.H_SCROLL | */
        SWT.V_SCROLL | SWT.BORDER);
        scrolledComposite.setAlwaysShowScrollBars(false);
        scrolledComposite.setBackground(display.getSystemColor(SWT.COLOR_RED));
        scrolledComposite.setExpandHorizontal(true);

        content = new Composite(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(content);
        // content.setBounds(0,0,640,maxContentHeight);
        content.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        scrolledComposite.setMinSize(content.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, false));

        inputControl = new Text(content, SWT.SINGLE | SWT.LEFT);
        inputControl.setText(prompt);
        inputControl.setSelection(prompt.length());
        inputControl.setEditable(true);

        final FontData fontData = new FontData("Courier New", fontPixelHeight,
                SWT.NORMAL);
        consoleFont = new Font(display, fontData);
        inputControl.setFont(consoleFont);
        inputControl.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        inputControl.setForeground(display.getSystemColor(userTextColor));

        inputControl.addKeyListener(new InputListener(this));

        final FormData formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        formData.bottom = new FormAttachment(100, 0);
        inputControl.setLayoutData(formData);

        formLayout = new FormLayout();
        formLayout.marginWidth = 3;
        formLayout.marginHeight = 3;
        content.setLayout(formLayout);
        controls = new ArrayList<Control>();
        lineHistory = new ArrayList<String>();

        append(scigol.Utils.get_IntroHeader());
        append("type '.help' for help");

        scrollToEnd();

        setFocus();

        getSite().setSelectionProvider(this);
    }

    protected int currentWidth() {
        final int parentWidth = scrolledComposite.getClientArea().width;// -
        // scrolledComposite.getBorderWidth();
        return Math.max(parentWidth, minContentWidth);
    }

    @Override
    public void dispose() // !!! is this called?
    {
        consoleFont.dispose();
    }

    public Value evaluateScigolExpression(final String expr)
            throws ANTLRException {
        if (expr.startsWith(".")) {
            return evaluateSpecialInteractiveCommand(expr);
        }

        final StringReader sin = new StringReader(expr);
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

        final ScigolTreeParser treeParser = new ScigolTreeParser(globalScope,
                true);
        return treeParser.expr(t);
    }

    public Value evaluateSpecialInteractiveCommand(final String command)
            throws RecognitionException, TokenStreamException {
        if (command.startsWith(".run")) {
            if (command.length() < 5) {
                throw new BilabException("usage: .run <file-name>");
            }
            final String resourceName = command.substring(5);
            URL resourceURL = null;
            try {
                resourceURL = BilabPlugin.findResource(resourceName);
            } catch (final IOException e) {
                appendError("unable to find resource: " + resourceName + " - "
                        + e.getMessage());
                return null;
            }

            try {
                BilabPlugin.executeScigolSourceStream(globalScope, resourceURL,
                        resourceName); // execute source (& ignore result value)
                return null;
            } catch (final IOException e) {
                appendError("input/output error with " + resourceName + ":"
                        + e.getMessage());
                e.printStackTrace(); // !!! for debugging only
            } catch (final ANTLRException e) {
                appendError("error in " + resourceName + ":" + e.getMessage());
                e.printStackTrace();
            } catch (final InternalScigolError e) {
                appendError("internal scigol error in " + resourceName + ":"
                        + e);
                e.printStackTrace();
            } catch (final ScigolException e) {
                appendError("error in " + resourceName + ":" + e.getMessage());
                e.printStackTrace();
            } catch (final BilabException e) {
                appendError("error in " + resourceName + ":" + e.getMessage());
                e.printStackTrace();
            } catch (final Exception e) {
                appendError("error in " + resourceName + ":" + e);
                e.printStackTrace();
            }
            return null;
        } else if (command.startsWith(".using")) {
            final int space = command.indexOf(' ');
            if (space == -1) {
                appendError("invalid namespace, use .using <namespace>");
            } else {
                final String namespaceName = command.substring(space + 1);
                try {
                    globalScope.addUsingNamespace(namespaceName);
                } catch (final ScigolException e) {
                    appendError("error using namespace '" + namespaceName
                            + "' - " + e);
                }
            }
            return null;
        } else if (command.startsWith(".typeof")) {
            final int space = command.indexOf(' ');
            if (space == -1) {
                appendError("invalid name, use .typeof <name>");
            } else {
                final String name = command.substring(space + 1);
                try {
                    final Value v = evaluateScigolExpression(name + ";");
                    if (v.getValue() != null) {
                        Object t = v.getValue();
                        if (!(t instanceof TypeSpec)) { // name was a value,
                                                        // deduce it's type
                            // unwrap Any or Num first
                            t = TypeSpec.unwrapAnyOrNum(t);
                            t = TypeSpec.typeOf(t);
                        }
                        final TypeSpec type = (TypeSpec) t;
                        if (type.isClass()) {
                            append(type.getClassInfo().toStringFull());
                        } else {
                            append(type.toString());
                        }
                    } else {
                        append("value of " + name + " is null (type 'any')");
                    }
                } catch (final ScigolException e) {
                    appendError("error in type expression: " + e.getMessage());
                } catch (final ANTLRException e) {
                    appendError("error in type expression: " + e.getMessage());
                }
            }
            return null;
        } else if (command.startsWith(".show")) {
            final int space = command.indexOf(' ');
            if (space == -1) {
                appendError("invalid name, use .show <expression>");
            } else {
                final String name = command.substring(space + 1);
                try {
                    final Value v = evaluateScigolExpression(name + ";");
                    if (v.getValue() != null) {
                        // broadcast 'selection'
                        selectedValue = v;
                        fireSelectionChangedListeners();
                    }
                } catch (final ScigolException e) {
                    appendError("error in expression: " + e.getMessage());
                } catch (final ANTLRException e) {
                    appendError("error in expression: " + e.getMessage());
                }
            }
            return null;
        } else if (command.startsWith(".help")) {
            final scigol.Any v = Util.help(command);
            if (v.value instanceof String) {
                append((String) v.value);
                return null;
            } else {
                return new Value(v);
            }
        } else if (command.equals(".list")) {
            append(Util.list());
            return null;
        } else {
            throw new BilabException("interactive command '" + command
                    + "' not recognised.");
        }
    }

    // methods to manage scigol execution
    public Value executeConsoleCommand(final String command) {
        final boolean supressOutput = command.endsWith(";");
        Value result = null;

        try {

            /*
             * // run the evaluate in a non-UI thread IProgressService
             * progressService = PlatformUI.getWorkbench().getProgressService();
             * ScigolInterpreterRunnable runnable = new
             * ScigolInterpreterRunnable(this,command);
             * progressService.busyCursorWhile(runnable);
             * 
             * //!!! until we implement concurrency properly, just busy wait on
             * it! while (!runnable.done) this.wait(200);
             */
            result = evaluateScigolExpression(command);

            if (BilabPlugin.existsRegisteredViewer(TypeSpec.typeOf(TypeSpec
                    .unwrapAnyOrNum(result)))) {
                // yes, there is a special viewer for this type, so append it
                // directly
                String prefix = null;

                if (!supressOutput) {
                    if ((result != null) && result.isLValue()) {
                        final Symbol s = result.getLValue().getSymbol();
                        if (!s.getName().equals("operator()")) {
                            prefix = s.getName() + " = ";
                        } else {
                            prefix = " = ";
                        }
                    }

                    append(result, prefix);
                }

            } else { // no specific viewer, construct an appropriate string
                String resultString = null;
                if (result != null) {
                    final Object resultValue = result.getValue();
                    resultString = (resultValue != null) ? resultValue
                            .toString() : "null";
                }
                if (!supressOutput) {
                    String output = "";
                    if ((result != null) && result.isLValue()) {
                        final Symbol s = result.getLValue().getSymbol();
                        if (!s.getName().equals("operator()")) {
                            output = output + s.getName() + " = ";
                        } else {
                            output = output + " = ";
                        }
                    }

                    if (resultString != null) {
                        output += resultString;
                    }
                    append(output);
                }
            }

        } // catch (ANTLRException e) {
          // appendError("error:"+e.getMessage());
          // e.printStackTrace(); //!!! for debugging only
        // }
        catch (final ScigolException e) {
            appendError("error:" + e.getMessage());
            e.printStackTrace();
        } catch (final BilabException e) {
            appendError("error:" + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            appendError("error:" + e);
            e.printStackTrace();
        } catch (final Throwable e) {
            appendError("error:" + e);
            e.printStackTrace();
        }
        return result;
    }

    protected void fireSelectionChangedListeners() {
        final ValueSelection s = new ValueSelection(selectedValue);
        for (final ISelectionChangedListener l : selectionChangedListeners) {
            l.selectionChanged(new SelectionChangedEvent(this, s));
        }
    }

    public Composite getContentParent() {
        return content;
    }

    @Override
    public ISelection getSelection() {
        return new ValueSelection(selectedValue);
    }

    protected void handleInputKeyEvent(final KeyEvent e) {
        if (e.stateMask == SWT.NONE) {
            if (e.keyCode == SWT.CR) {
                final String command = inputControl.getText().substring(
                        prompt.length()); // remove prompt
                if (command.length() > 0) {
                    append(inputControl.getText(), true,
                            display.getSystemColor(userTextColor));
                    inputControl.setText("");

                    executeConsoleCommand(command);

                    inputControl.setText(prompt);
                    inputControl.setSelection(prompt.length());
                    inputControl.setFocus();
                } else {
                    append(prompt, false, display.getSystemColor(userTextColor));
                }
            } else if (e.keyCode == SWT.ARROW_UP) {
                historyPosition--;
                if (historyPosition < 0) {
                    historyPosition = 0;
                }
                if (lineHistory.size() > 0) {
                    inputControl.setText(lineHistory.get(historyPosition));
                    inputControl.setSelection(inputControl.getText().length());
                }
                e.doit = false;
            } else if (e.keyCode == SWT.ARROW_DOWN) {
                historyPosition++;
                if (historyPosition > lineHistory.size()) {
                    historyPosition = lineHistory.size();
                }
                if (historyPosition == lineHistory.size()) {
                    inputControl.setText(prompt);
                } else {
                    inputControl.setText(lineHistory.get(historyPosition));
                }
                inputControl.setSelection(inputControl.getText().length());
                e.doit = false;
            } else if ((inputControl.getCaretPosition() == prompt.length())
                    && ((e.keyCode == SWT.BS) || (e.keyCode == SWT.ARROW_LEFT))) {
                // ignore BACKSPACE/LEFT-ARROW if caret is just after the prompt
                // (so as not to erase/edit the prompt)
                e.doit = false;
            }
        } else if (e.stateMask == SWT.CTRL) {
            // close if CTRL^d pressed on new line
            if ((e.keyCode == 'd') && (inputControl.getText().length() == 1)) {
                PlatformUI.getWorkbench().close();
                e.doit = false;
                return;
            } else if (e.keyCode == 'a') { // emacs, beginning of line
                                           // //!!! this is being
                                           // grabbed as select-all
                inputControl.setSelection(1);
                e.doit = false;
            } else if (e.keyCode == 'e') { // emacs, end-of-line //!!!
                                           // doesn't work - some other
                                           // feature is grabbing this
                                           // key...
                inputControl.setSelection(inputControl.getText().length());
                e.doit = false;
            }
        }

        scrollToEnd();
        scrolledComposite.layout(); // force scrolled composite to move content
                                    // according to new scrollbar position
    }

    @Override
    public void removeSelectionChangedListener(
            final ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    public void scrollToEnd() {
        final ScrollBar vbar = scrolledComposite.getVerticalBar();
        vbar.setSelection(vbar.getMaximum());
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        inputControl.setFocus();
    }

    protected void setLayoutData() {
        if (controls.size() == 0) {
            return;
        }

        for (int c = controls.size() - 1; c >= 0; c--) {
            final Control control = controls.get(c);
            final FormData data = (FormData) control.getLayoutData();

            if (c < controls.size() - 1) {
                data.bottom = new FormAttachment(controls.get(c + 1));
            } else {
                // bottom control
                data.bottom = new FormAttachment(inputControl);
            }
        }
    }

    @Override
    public void setSelection(final ISelection selection) {
        // not possible
    }

    protected String splitLines(final String lines) {
        final int contentCharWidth = (currentWidth() / fontPixelWidth) - 1;

        if (lines.length() > contentCharWidth) {

            final StringBuilder sb = new StringBuilder();
            int col = 0;
            for (int c = 0; c < lines.length(); c++) {
                final char ch = lines.charAt(c);
                sb.append(ch);
                col++;
                if ((ch == '\n') || (ch == '\r')) {
                    col = 0; // string had embedded newline
                }
                if (col == contentCharWidth) {
                    sb.append("\u25C4\n");
                    col = 0;
                }
            }
            return sb.toString();
        } else {
            return lines;
        }
    }
}