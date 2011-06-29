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

package scigol;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;

import antlr.RecognitionException;
import antlr.collections.AST;

// / internal holder for a value of a function type
public class Func {

    protected enum FuncType {
        Local, External
    }

    protected Scope _scope; // Scope in which func defined

    protected FuncType _funcType; // is the Func a Scigol one or a general CLI
                                  // one?

    // Local functions only
    protected FuncInfo _info;

    protected ScigolTreeParser _treeParser; // the parser to use to interpret
                                            // _expr

    protected AST _expr;

    protected AST _pre; // precondition & postcondition contract bool
                        // expressions (or null if none specified)

    protected AST _post;

    protected boolean _isConstructor; // if this func is a class constructor,
                                      // it needs to ask the claa scope to
                                      // initialize the object first

    // extern functions
    protected Type _classType;

    protected Member _member;

    // create Local type func
    public Func(final FuncInfo info, final Scope outerScope,
            final ScigolTreeParser treeParser, final AST body,
            final AST preCondition, final AST postCondition) {
        Debug.Assert(info.isFormal(),
                "can only construct a func value with an info that has formal parameter names");
        Debug.Assert((body == null) || (outerScope != null),
                "scope required for local (non-null) funcs");

        _funcType = FuncType.Local;
        _info = info;
        _scope = outerScope;
        _treeParser = treeParser;
        _expr = body;
        _pre = preCondition;
        _post = postCondition;
        _isConstructor = false;
    }

    // create External type func
    public Func(final Type classType, final Member member) {
        _funcType = FuncType.External;
        _scope = null;
        _classType = classType;
        _member = member;
        _isConstructor = (member instanceof Constructor);
    }

    // convenience
    public Object call(final Object instance, final ArrayList args) {
        final Object[] aargs = new Object[args.size()];
        for (int a = 0; a < aargs.length; a++) {
            aargs[a] = args.get(a);
        }
        return call(instance, aargs);
    }

    // call this Func with specified args (and instance if non-static)
    // NB: this doesn't do virtual lookup on Local type funcs, only External
    // type funcs
    // The args are required to match the number and type of the formal
    // parameters
    public Object call(Object instance, final Object[] args) {
        // Debug.WriteLine("entering Func:"+this+" call("+((instance==null)?"nullinst":instance.ToString())
        // +","+((args==null)?"nullargs":args.ToString()+"["+args.Length+"]")+") - isConstructor="+_isConstructor);
        if (_funcType == FuncType.Local) {

            // if _isConstructor, need to call base constructor somewhere -
            // here??!!!

            // make a new scope for execution of the func body
            final Scope callScope = new LocalScope(_scope);

            final Scope savedScope = _treeParser.scope;
            _treeParser.scope = callScope;

            // set this - if non-static/constructor
            if (instance != null) {
                Debug.Assert(_scope.isClassScope(),
                        "can only have instances of classes");
                callScope.addEntry("this", TypeSpec.typeOf(instance), instance,
                        null, EnumSet.of(TypeSpec.Modifier.Public));
            }

            // setup args
            final String[] pnames = _info.getParamNames();
            final TypeSpec[] ptypes = _info.getParamTypes();

            // check we have an appropriate number of args
            final int numArgs = (args == null) ? 0 : args.length;
            Debug.Assert(numArgs == _info.numArgs(),
                    "call must have correct args");

            for (int a = 0; a < _info.numArgs(); a++) {
                callScope.addEntry(pnames[a], ptypes[a], args[a], null,
                        EnumSet.of(TypeSpec.Modifier.Public));
            }

            // now evaluate the pre condition expression, if any
            final TypeSpec boolType = new TypeSpec(TypeSpec.boolType);

            if (_pre != null) {
                // interpret pre
                Object preValue = null;
                try {
                    preValue = _treeParser.expressionList(_pre, false);
                } catch (final antlr.RecognitionException e) {
                    ScigolTreeParser.semanticError(
                            "error evaluating pre-condition in func " + _info
                                    + " - " + e.getMessage(), e);
                }
                if (!(preValue instanceof Value)) {
                    preValue = new Value(preValue);
                }
                final TypeSpec t = TypeSpec.typeOf(preValue);
                if (!TypeManager.existsImplicitConversion(t, boolType,
                        (Value) preValue)) {
                    ScigolTreeParser
                            .semanticError("a precondition expression must evaluate to type 'bool' (in func "
                                    + _info + ")");
                }
                final boolean pre = ((Boolean) TypeManager
                        .performImplicitConversion(t, boolType,
                                (Value) preValue).getValue()).booleanValue();
                if (!pre) {
                    ScigolTreeParser
                            .semanticError("unmet pre-condition in func "
                                    + _info);
                }
            }

            Object retValue = new Any(null);

            // if this is a no-op function (a null func), just skip to the post
            // condition expression
            if (_expr != null) {

                // interpret

                retValue = null;
                try {
                    retValue = _treeParser.expressionList(_expr, false);
                } catch (final antlr.RecognitionException e) {
                    ScigolTreeParser.semanticError("error in function " + _info
                            + " - " + e.getMessage(), e);
                }
                if (retValue instanceof Value) {
                    retValue = ((Value) retValue).getValue();
                }

                // if this call is a constructor, reutrn the instance, not the
                // _expr value
                if (_isConstructor) {
                    retValue = instance;
                } else {
                    if (_info.getReturnType() == null) {
                        retValue = null;
                    } else if ((retValue != null)
                            && !TypeManager.existsImplicitConversion(
                                    TypeSpec.typeOf(retValue),
                                    _info.getReturnType(), new Value(retValue))) {
                        ScigolTreeParser.semanticError("func " + _info
                                + " evaluated to a value of type '"
                                + TypeSpec.typeOf(retValue)
                                + "' when the return type '"
                                + _info.getReturnType() + "' was expected");
                    }
                }

            }

            // now evaluate the post condition expression (with new local,
            // 'value')

            if (_post != null) {
                // inject func body return value as local 'value' for post body
                final Value value = new Value(retValue);
                callScope.addEntry("value", TypeSpec.typeOf(value), retValue,
                        null, EnumSet.of(TypeSpec.Modifier.Public));

                // interpret post
                Object postValue = null;
                try {
                    postValue = _treeParser.expressionList(_post, false);
                } catch (final RecognitionException e) {
                    ScigolTreeParser.semanticError(
                            "error evaluating post-condition in func " + _info
                                    + " - " + e.getMessage(), e);
                }
                if (!(postValue instanceof Value)) {
                    postValue = new Value(postValue);
                }
                final TypeSpec t = TypeSpec.typeOf(postValue);
                if (!TypeManager.existsImplicitConversion(t, boolType,
                        (Value) postValue)) {
                    ScigolTreeParser
                            .semanticError("a postcondition expression must evaluate to type 'bool' (in func "
                                    + _info + ")");
                }
                final boolean post = ((Boolean) TypeManager
                        .performImplicitConversion(t, boolType,
                                (Value) postValue).getValue()).booleanValue();
                if (!post) {
                    ScigolTreeParser
                            .semanticError("unmet post-condition in func "
                                    + _info);
                }

                // fetch back 'value' from callScope, in case post conidition
                // expression modified it
                // (allows the post condition to change the return value - handy
                // for debugging)
                final Entry[] entries = callScope.getEntries("value", null);
                Debug.Assert(entries.length == 1,
                        "should be just one 'value' in the local callScope!");
                retValue = entries[0].getStaticValue();
            }

            // Debug.WriteLine("leaving local Func.call("+((instance==null)?"nullinst":instance.ToString())
            // +","+((args==null)?"nullargs":args.ToString()+"["+args.Length+"]")+") "+
            // +" with retValue="+retValue+" (type:"+((retValue!=null)?retValue.GetType().ToString():"null")+")"+" - isConstructor="+_isConstructor);

            _treeParser.scope = savedScope;
            return retValue;

        } else { // external Java call

            Object retValue = null;
            if (instance instanceof scigol.Class) {
                instance = ((scigol.Class) instance).getSysValue();
            }

            // if any of the args are external scigol.Class instances, unwrap
            // them
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof scigol.Class) {
                    final scigol.Class c = (scigol.Class) args[i];
                    if (c.isExternal()) {
                        args[i] = c.getSysValue();
                    }
                }
            }

            try {
                // Debug.WL("Member=" + _member + " instance=" + instance);//!!!
                // for(int i=0; i<args.length;i++)
                // Debug.Write(" arg"+i+":"+args[i].getClass());
                // Debug.WL("");

                if (_member instanceof Method) {
                    retValue = ((Method) _member).invoke(instance, args);
                } else {
                    retValue = ((Constructor) _member).newInstance(args);
                    // Debug.WriteLine("leaving extern Func.call("+((instance==null)?"nullinst":instance.toString())
                    // +","+((args==null)?"nullargs":args.toString()+"["+args.length+"]")+") "
                    // +" with retValue="+retValue+" of type:"+((retValue!=null)?TypeSpec.typeOf(retValue).toString():"")+" - isConstructor="+_isConstructor);
                }
            } catch (final InvocationTargetException e) {
                // rethrow anything we can (essentially unwarps it from
                // InvocationTargetException)
                if (e.getCause() != null) {
                    if (e.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) e.getCause(); // re-throw the
                                                               // cause
                                                               // directly, if
                                                               // known
                    }
                    if (e.getCause() instanceof java.lang.Error) {
                        throw (java.lang.Error) e.getCause();
                    }
                    ScigolTreeParser
                            .semanticError("error invoking method or constructor - "
                                    + e.getCause().getMessage());
                }
                ScigolTreeParser
                        .semanticError("error invoking method or constructor - "
                                + e.getMessage());
            } catch (final InstantiationException e) {
                ScigolTreeParser
                        .semanticError("cannnot instantiate abstract class "
                                + _member.getDeclaringClass().getName());
            } catch (final IllegalAccessException e) {
                ScigolTreeParser
                        .semanticError("access error invoking method or constructor - "
                                + e.getMessage());
            }

            return retValue;
        }
    }

    public FuncInfo getInfo() {
        if (_funcType == FuncType.Local) {
            return _info;
        } else {
            return new FuncInfo(_member);
        }
    }

    public Scope getOuterScope() {
        return _scope;
    };

    public ScigolTreeParser getParser() {
        return _treeParser;
    }

    // protected Object _instance; // if this func is a non-static method, this
    // is the object instance

    public AST getValue() {
        Debug.Assert(_funcType == FuncType.Local);
        return _expr;
    }

    public boolean isConstructor() {
        return _isConstructor;
    }

    public boolean isExternal() {
        return (_funcType == FuncType.External);
    }

    public void setInfo(final FuncInfo value) {
        if (_funcType == FuncType.Local) {
            _info = value;
        } else {
            Debug.Unimplemented();
        }
    }

    public void setIsConstructor(final boolean value) {
        _isConstructor = value;
        Debug.Assert(_scope.isClassScope(),
                "func can't be a constructor if it wasn't defined in a class scope!");
    }

    public void setValue(final AST value) {
        Debug.Assert(_funcType == FuncType.Local);
        _expr = value;
    }

    @Override
    public String toString() {
        String s = getInfo().toString();
        if (_funcType == FuncType.Local) {
            if (_expr != null) {
                s += " {...}";
            } else {
                s += " {null}";
            }
        } else {
            if (_member != null) {
                s += " {...}";
            } else {
                s += " {null}";
            }
        }
        return s;
    }

    public String toStringArgs(final Object[] args) {
        String s = getInfo().toStringArgs(args);
        if (_funcType == FuncType.Local) {
            if (_expr != null) {
                s += " {...}";
            } else {
                s += " {null}";
            }
        } else {
            if (_member != null) {
                s += " {...}";
            } else {
                s += " {null}";
            }
        }
        return s;
    }

}
