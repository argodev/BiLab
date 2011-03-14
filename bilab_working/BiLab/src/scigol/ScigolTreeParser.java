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

// $ANTLR 2.7.3 (20040901-1): "Scigol.tree.g" -> "ScigolTreeParser.java"$

package scigol;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

//

public class ScigolTreeParser extends antlr.TreeParser implements
        ScigolTreeParserTokenTypes {

    public static final String[] _tokenNames = { "<0>", "EOF", "<2>",
            "NULL_TREE_LOOKAHEAD", "DOT", "DOTDOT", "UNARY_MINUS",
            "UNARY_PLUS", "EXPRLIST", "INITLIST", "MATRIX", "LIST", "MAP",
            "FUNC", "POST_INC", "POST_DEC", "APPLICATION", "\"true\"",
            "\"false\"", "\"null\"", "LIT_FUNC", "MODIFIERS", "BUILTIN_TYPE",
            "CTOR", "PROP", "LCURLY", ";", "\"namespace\"", "}",
            "an identifier", "\"using\"", "\"as\"", "\"from\"",
            "a string literal", "\"pre\"", "\"post\"", "\"typeof\"", "LPAREN",
            "RPAREN", "\"assert\"", "COMMA", "\"debug\"", "\"logger\"",
            "ASSIGN", "\"or\"", "\"and\"", "NOT_EQUAL", "EQUAL", "\"is\"",
            "\"isnt\"", "LTHAN", "GTHAN", "LTE", "GTE", "PLUS", "MINUS",
            "STAR", "DIV", "MOD", "HAT", "INC", "DEC", "LNOT", "\"not\"",
            "HASH", "PRIME", "BAR", "SCOPE_ESCAPE", "'(", "\"try\"",
            "\"catch\"", "COLON", "a numeric literal", "NUM_DINT", "NUM_REAL",
            "NUM_SREAL", "a char literal", "LBRACK", "RBRACK", "->",
            "ANNOT_START", "\"let\"", "\"const\"", "\"static\"", "\"final\"",
            "\"class\"", "\"interface\"", "\"property\"", "\"override\"",
            "\"implicit\"", "\"public\"", "\"private\"", "\"protected\"",
            "\"func\"", "\"vector\"", "\"matrix\"", "\"range\"", "\"list\"",
            "\"map\"", "\"bool\"", "\"byte\"", "\"char\"", "\"int\"",
            "\"dint\"", "\"real\"", "\"sreal\"", "\"string\"", "\"type\"",
            "\"num\"", "\"any\"", "\"object\"", "\"if\"", "\"then\"",
            "\"else\"", "\"do\"", "\"while\"", "\"for\"", "\"foreach\"",
            "\"in\"", "\"by\"", "\"throw\"", "QUESTION", "SL", "BAND", "FROM",
            "LINE_BREAK", "NON_LINE_BREAK_WS", "WS", "SL_COMMENT",
            "ML_COMMENT", "STRING_OR_CHAR_LITERAL", "a string literal",
            "a multi-line string literal", "NON_BACKQUOTE_STRING",
            "CHAR_LIT_SUFFIX", "ESC", "HEX_DIGIT", "VOCAB", "DOT_FLOAT_EXP",
            "EXPONENT", "REAL_SUFFIX" };

    public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

    public static Location locationOf(final AST near) {
        if (near instanceof CommonASTWithLocation) {
            return ((CommonASTWithLocation) near).loc;
        } else {
            return new Location();
        }
    }

    private static final long[] mk_tokenSet_0() {
        final long[] data = { -4320059720208L, 89086830135353135L, 0L, 0L };
        return data;
    }

    public static void semanticError(final AST near, final String msg) {
        String loc = "";
        if (near instanceof CommonASTWithLocation) {
            final CommonASTWithLocation locAST = (CommonASTWithLocation) near;
            if (locAST.loc.isKnown()) {
                loc += "[" + locAST.loc.toString() + "] ";
            }
        }
        throw new ScigolException(loc + msg);
    }

    public static void semanticError(final Location l, final String msg) {
        if ((l != null) && (l.isKnown())) {
            throw new ScigolException("[" + l + "] " + msg);
        } else {
            throw new ScigolException(msg);
        }
    }

    public static void semanticError(final String msg) {
        throw new ScigolException(msg);
    }

    public static void semanticError(final String msg, final Exception inner) {
        throw new ScigolException(msg, inner);
    }

    public Scope globalScope;

    public boolean interactive; // are we being run interactively?

    public Scope scope; // current scope

    public ScigolTreeParser() {
        tokenNames = _tokenNames;
    }

    public ScigolTreeParser(final Scope initialScope, final boolean interactive) {
        super();
        this.interactive = interactive;
        globalScope = initialScope.getGlobalScope();
        scope = initialScope;
    }

    public final ScigolAnnotation annotation(AST _t)
            throws RecognitionException {
        ScigolAnnotation a = null;

        final AST annotation_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        String annotName = null;
        ArrayList args = null;

        final AST __t115 = _t;
        final AST tmp52_AST_in = (AST) _t;
        match(_t, ANNOT_START);
        _t = _t.getFirstChild();
        annotName = qualifiedIdent(_t);
        _t = _retTree;
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case LPAREN: {
                    args = applicationArgs(_t);
                    _t = _retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t115;
        _t = _t.getNextSibling();

        final java.lang.reflect.Type javaType = NamespaceScope
                .loadedLibrariesGetType(annotName);
        final TypeSpec annotType = (javaType != null) ? new TypeSpec(javaType)
                : null;

        if (annotType == null) {
            ScigolTreeParser.semanticError("the annotation type '" + annotName
                    + "' could not be found in the current scope");
        } else if (!annotType.isInterface()
                || !annotType.isA(new TypeSpec(Annotation.class))) {
            ScigolTreeParser
                    .semanticError("the type '"
                            + annotName
                            + "' specified as an annotation must implement 'Annotation'");
        }

        // now construct a ScigolAnnotation to hold the parameters
        a = new ScigolAnnotation(annotType);
        a.setMembers(args);

        _retTree = _t;
        return a;
    }

    public final ArrayList applicationArgs(AST _t) throws RecognitionException {
        final ArrayList args = new ArrayList();

        final AST applicationArgs_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value e;

        final AST __t16 = _t;
        final AST tmp29_AST_in = (AST) _t;
        match(_t, LPAREN);
        _t = _t.getFirstChild();
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case DOT:
                case DOTDOT:
                case UNARY_MINUS:
                case UNARY_PLUS:
                case EXPRLIST:
                case MATRIX:
                case LIST:
                case MAP:
                case FUNC:
                case POST_INC:
                case POST_DEC:
                case APPLICATION:
                case LIT_TRUE:
                case LIT_FALSE:
                case LIT_NULL:
                case LIT_FUNC:
                case BUILTIN_TYPE:
                case LITERAL_namespace:
                case IDENT:
                case STRING_LITERAL:
                case LITERAL_typeof:
                case LITERAL_logger:
                case ASSIGN:
                case LITERAL_or:
                case LITERAL_and:
                case NOT_EQUAL:
                case EQUAL:
                case LITERAL_is:
                case LITERAL_isnt:
                case LTHAN:
                case GTHAN:
                case LTE:
                case GTE:
                case PLUS:
                case MINUS:
                case STAR:
                case DIV:
                case MOD:
                case HAT:
                case INC:
                case DEC:
                case LNOT:
                case LITERAL_not:
                case HASH:
                case PRIME:
                case BAR:
                case SCOPE_ESCAPE:
                case LITERAL_try:
                case NUM_INT:
                case NUM_DINT:
                case NUM_REAL:
                case NUM_SREAL:
                case CHAR_LITERAL:
                case LITERAL_let:
                case LITERAL_class:
                case LITERAL_interface:
                case LITERAL_if:
                case LITERAL_do:
                case LITERAL_while:
                case LITERAL_for:
                case LITERAL_foreach:
                case LITERAL_throw: {
                    e = expr(_t);
                    _t = _retTree;
                    args.add(e);
                    break;
                }
                case 3:
                case COMMA: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        {
            _loop19: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == COMMA)) {
                    final AST tmp30_AST_in = (AST) _t;
                    match(_t, COMMA);
                    _t = _t.getNextSibling();
                    e = expr(_t);
                    _t = _retTree;
                    args.add(e);
                } else {
                    break _loop19;
                }

            } while (true);
        }
        _t = __t16;
        _t = _t.getNextSibling();
        _retTree = _t;
        return args;
    }

    public final Value applicationExpression(AST _t)
            throws RecognitionException {
        Value v = null;

        final AST applicationExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        AST app = null;

        Value f;
        ArrayList args = null;

        final AST __t14 = _t;
        app = _t == ASTNULL ? null : (AST) _t;
        match(_t, APPLICATION);
        _t = _t.getFirstChild();
        f = expr(_t);
        _t = _retTree;
        args = applicationArgs(_t);
        _t = _retTree;
        _t = __t14;
        _t = _t.getNextSibling();

        final FuncInfo callSig = new FuncInfo(args);
        callSig.setDefinitionLocation(locationOf(app));

        // first, if we have an lvalue that refers to an overloaded symbol, we
        // need
        // to resolve it to a single method based on the call signature
        boolean isFunc = false;
        Func func = null;

        // if f is either an rvalue, or an lvalue that isn't ambiguous
        if (!f.isLValue()
                || (f.isLValue() && !f.getLValue().getSymbol().isAmbiguous())) {

            // property?
            boolean isProperty = false;
            if (f.isLValue()) {
                final Symbol s = f.getLValue().getSymbol();
                isProperty = s.getEntry().isProperty();
            }

            if (isProperty) {
                isFunc = false;
            } else {
                final Object fo = f.getValue();
                if (fo != null) {
                    isFunc = f.getType().isFunc();
                    if (isFunc) {
                        func = (Func) fo;
                    }
                } else {
                    isFunc = true; // null is a no-op function that returns
                                   // null, by definition
                }
            }
        } else { // overloaded lvalue

            // !!! this doesn't work if the lhs if an overloaded lvalue that is
            // a bound property.
            // it is assuming a func, but we can't know that.

            final Symbol s = f.getLValue().getSymbol();
            // Debug.WriteLine("application of overloaded lvalue");
            s.disambiguate(callSig, args);
            // now we can go ahead and retrieve the func value
            isFunc = true;
            final Object fo = f.getValue();
            if (fo != null) {
                func = (Func) fo;
            }
        }

        if (isFunc) { // function call

            if (func == null) {
                // Debug.WriteLine("applicationExpression: call no-op");
                v = null; // a null Func is a no-op
            } else { // call it

                final Object[] convertedArgs = func.getInfo()
                        .convertParameters(callSig, args, func.isExternal());
                // Debug.WriteLine("applicationExpression: call "+func.toStringArgs(convertedArgs));

                // retrieve instance
                Object instance = null;
                if (f.isLValue()) {
                    instance = f.getLValue().getSymbol().getInstance();
                }

                v = new Value(func.call(instance, convertedArgs));

            }

        } else if (f.getType().isType()) { // construction or explicit
                                           // conversion? (looks like call of a
                                           // type)
            final TypeSpec t = (TypeSpec) f.getValue();

            // Debug.WriteLine("constructing a "+t);

            // if there is a single argument, treat it like an explicit
            // conversion (which will also
            // call any compatible single argument constructors of t)
            v = null;
            if (callSig.numArgs() == 1) {
                v = TypeManager.performExplicitConversion(
                        callSig.getParamTypes()[0], t, new Value(args.get(0)));
            }

            if (v == null) {
                v = new Value(t.constructValue(callSig, args, this));
            }

            Debug.Assert(v != null, "construction/conversion failed");
        } else { // assume property access

            final Symbol s = f.getLValue().getSymbol();

            final boolean isProperty = s.getEntry().isProperty();

            if (isProperty) {
                // we have an LValue that refers to a property member
                // turn it into an property LValue with bound property arguments
                // !!! if the property is already arg bound, evaluate it and
                // treat this as another application!
                if (f.getLValue().isBoundProperty()) {
                    Debug.Unimplemented("property already bound");
                }
                v = new Value(new LValue(f.getLValue().getSymbol(), callSig,
                        FuncInfo.toArray(args)));
            } else {
                // perhaps we have a class, which has an operator() property (an
                // indexer)?
                TypeSpec ftype = f.getType();
                boolean isClass = false;
                if (ftype.isClass() || ftype.isBuiltinClass()) {
                    isClass = true;
                } else if (ftype.isAny() || ftype.isNum()) {
                    f = TypeSpec.unwrapAnyOrNumValue(f);
                    ftype = f.getType();
                    isClass = (ftype.isClass() || ftype.isBuiltinClass());
                }

                if (isClass) {

                    // create an LValue for operator() (if it exists), bound
                    // with the arguments
                    final Scope classScope = new ClassScope(ftype); // create
                                                                    // scope for
                                                                    // class
                    final Symbol pSymbol = new Symbol(classScope, "operator()",
                            f.getValue());

                    if (!pSymbol.exists()) {
                        ScigolTreeParser
                                .semanticError(
                                        locationOf(app),
                                        "object of type '"
                                                + ftype
                                                + "' has no indexer ('operator()' property) defined, hence cannot be called (with call signature "
                                                + callSig + ")");
                    }

                    v = new Value(new LValue(pSymbol, callSig,
                            FuncInfo.toArray(args)));
                } else {
                    ScigolTreeParser
                            .semanticError(
                                    locationOf(app),
                                    "cannot call an object of type '"
                                            + f.getType()
                                            + "'.  To be `called` it must be either a type (construction), a func (call), or an object with an 'operator()' property (indexing).");
                }
            }
        }

        _retTree = _t;
        return v;
    }

    public final Value arithmeticExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST arithmeticExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        Value lhs = null, rhs;

        if (_t == null) {
            _t = ASTNULL;
        }
        switch (_t.getType()) {
            case PLUS: {
                final AST __t47 = _t;
                final AST tmp5_AST_in = (AST) _t;
                match(_t, PLUS);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t47;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator+", lhs, rhs);
                break;
            }
            case MINUS: {
                final AST __t48 = _t;
                final AST tmp6_AST_in = (AST) _t;
                match(_t, MINUS);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t48;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator-", lhs, rhs);
                break;
            }
            case UNARY_PLUS: {
                final AST __t49 = _t;
                final AST tmp7_AST_in = (AST) _t;
                match(_t, UNARY_PLUS);
                _t = _t.getFirstChild();
                rhs = expr(_t);
                _t = _retTree;
                _t = __t49;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator+", null, rhs);
                break;
            }
            case UNARY_MINUS: {
                final AST __t50 = _t;
                final AST tmp8_AST_in = (AST) _t;
                match(_t, UNARY_MINUS);
                _t = _t.getFirstChild();
                rhs = expr(_t);
                _t = _retTree;
                _t = __t50;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator-", null, rhs);
                break;
            }
            case STAR: {
                final AST __t51 = _t;
                final AST tmp9_AST_in = (AST) _t;
                match(_t, STAR);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t51;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator*", lhs, rhs);
                break;
            }
            case DIV: {
                final AST __t52 = _t;
                final AST tmp10_AST_in = (AST) _t;
                match(_t, DIV);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t52;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator/", lhs, rhs);
                break;
            }
            case MOD: {
                final AST __t53 = _t;
                final AST tmp11_AST_in = (AST) _t;
                match(_t, MOD);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t53;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator%", lhs, rhs);
                break;
            }
            case HAT: {
                final AST __t54 = _t;
                final AST tmp12_AST_in = (AST) _t;
                match(_t, HAT);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t54;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator^", lhs, rhs);
                break;
            }
            case EQUAL: {
                final AST __t55 = _t;
                final AST tmp13_AST_in = (AST) _t;
                match(_t, EQUAL);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t55;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator==", lhs, rhs);
                break;
            }
            case NOT_EQUAL: {
                final AST __t56 = _t;
                final AST tmp14_AST_in = (AST) _t;
                match(_t, NOT_EQUAL);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t56;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator!=", lhs, rhs);
                break;
            }
            case LTHAN: {
                final AST __t57 = _t;
                final AST tmp15_AST_in = (AST) _t;
                match(_t, LTHAN);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t57;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator<", lhs, rhs);
                break;
            }
            case GTHAN: {
                final AST __t58 = _t;
                final AST tmp16_AST_in = (AST) _t;
                match(_t, GTHAN);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t58;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator>", lhs, rhs);
                break;
            }
            case LTE: {
                final AST __t59 = _t;
                final AST tmp17_AST_in = (AST) _t;
                match(_t, LTE);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t59;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator<=", lhs, rhs);
                break;
            }
            case GTE: {
                final AST __t60 = _t;
                final AST tmp18_AST_in = (AST) _t;
                match(_t, GTE);
                _t = _t.getFirstChild();
                lhs = expr(_t);
                _t = _retTree;
                rhs = expr(_t);
                _t = _retTree;
                _t = __t60;
                _t = _t.getNextSibling();
                v = Math.performOverloadedOperation("operator>=", lhs, rhs);
                break;
            }
            case INC:
            case DEC: {
                v = prefixExpression(_t);
                _t = _retTree;
                break;
            }
            case POST_INC:
            case POST_DEC:
            case PRIME: {
                v = postfixExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_and: {
                v = logicalAndExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_or: {
                v = logicalOrExpression(_t);
                _t = _retTree;
                break;
            }
            case LNOT:
            case LITERAL_not: {
                v = logicalNotExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_is:
            case LITERAL_isnt: {
                v = isExpression(_t);
                _t = _retTree;
                break;
            }
            case DOTDOT: {
                v = rangeExpression(_t);
                _t = _retTree;
                break;
            }
            case BAR: {
                v = normExpression(_t);
                _t = _retTree;
                break;
            }
            case HASH: {
                v = cardinalityExpression(_t);
                _t = _retTree;

                Debug.Assert(v != null, "Value is null");

                break;
            }
            default: {
                throw new NoViableAltException(_t);
            }
        }
        _retTree = _t;
        return v;
    }

    public final Value assignmentExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST assignmentExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        AST a = null;
        Value lhs = null, rhs = null;

        final AST __t24 = _t;
        a = _t == ASTNULL ? null : (AST) _t;
        match(_t, ASSIGN);
        _t = _t.getFirstChild();
        lhs = expr(_t);
        _t = _retTree;
        rhs = expr(_t);
        _t = _retTree;
        _t = __t24;
        _t = _t.getNextSibling();

        if (lhs.isLValue()) {
            // Debug.WriteLine("assigning:"+lhs.getLValue());
            // Debug.WriteLine("  to value:"+rhs);

            // check type & convert if necessary
            final LValue l = lhs.getLValue();
            final TypeSpec tlhs = l.getType();
            final TypeSpec trhs = TypeSpec.typeOf(rhs);
            if (TypeManager.existsImplicitConversion(trhs, tlhs, rhs)) {
                lhs.setValue(TypeManager.performImplicitConversion(trhs, tlhs,
                        rhs).getValue());
            } else {
                ScigolTreeParser.semanticError(a,
                        "incompatible right-hand-side (RHS) in assignment, value of type '"
                                + trhs + "' is incompatible with LHS type '"
                                + tlhs + "'");
            }

            v = lhs;
        } else { // not assignable
            if (lhs.isNamespaceComponent()) {

                if (interactive) {
                    // If we're in interactive mode, we allow undefined
                    // identifiers to be assigned to.
                    // In this case, we declare the symbol as type 'any' or type
                    // 'num' depending on the
                    // type of the initializer
                    final String name = lhs.getNamespaceComponentString();
                    final int dotIndex = name.indexOf('.');
                    if (dotIndex == -1) { // only auto declare simple names (not
                                          // compound names like a.b)
                        final TypeSpec trhs = TypeSpec.typeOf(rhs.getValue());
                        TypeSpec tlhs = new TypeSpec(TypeSpec.anyType);// default
                                                                       // to
                                                                       // 'any'

                        if (trhs.isANum()) {
                            tlhs = new TypeSpec(TypeSpec.numType);
                            if (!(rhs.getValue() instanceof Num)) {
                                rhs = new Value(new Num(rhs.getValue())); // wrap
                                                                          // rhs
                                                                          // in
                                                                          // Num
                            }
                        } else {
                            rhs = new Value(new Any(rhs.getValue())); // wrap
                                                                      // rhs in
                                                                      // Any
                        }

                        scope.addEntry(name, tlhs, rhs.getValue(), null,
                                EnumSet.of(TypeSpec.Modifier.Public));
                        v = new Value(new LValue(new Symbol(scope, name, null)));
                    } else {
                        ScigolTreeParser
                                .semanticError(
                                        a,
                                        "unknown name '"
                                                + lhs.getNamespaceComponentString()
                                                + "' on left-hand-side (LHS) of assignment expression");
                    }
                } else {
                    ScigolTreeParser
                            .semanticError(
                                    a,
                                    "unknown name '"
                                            + lhs.getNamespaceComponentString()
                                            + "' on left-hand-side (LHS) of assignment expression");
                }
            } else {
                ScigolTreeParser
                        .semanticError(
                                a,
                                "left-hand-side (LHS) of assignment expression is not assignable (not an lvalue)");
            }
        }

        _retTree = _t;
        return v;
    }

    public final Value cardinalityExpression(AST _t)
            throws RecognitionException {
        Value v = null;

        final AST cardinalityExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        Value e = null;

        final AST __t73 = _t;
        final AST tmp49_AST_in = (AST) _t;
        match(_t, HASH);
        _t = _t.getFirstChild();
        e = expr(_t);
        _t = _retTree;
        _t = __t73;
        _t = _t.getNextSibling();

        if (e.getValue() == null) {
            v = new Value(new Integer(0));
        } else {
            v = Math.performOverloadedOperation("operator#", null, e);
        }

        _retTree = _t;
        return v;
    }

    public final Value charLit(AST _t) throws RecognitionException {
        Value v = null;

        final AST charLit_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST c = null;

        c = (AST) _t;
        match(_t, CHAR_LITERAL);
        _t = _t.getNextSibling();

        String str = new String(c.getText());
        if (str.charAt(0) == '\"') {
            str = str.substring(1, str.length() - 2); // remove surrouding
                                                      // double quotes & suffix
        }
        if (str.length() != 1) {
            ScigolTreeParser
                    .semanticError(locationOf(c),
                            "character literals must contain a single character within the quotes");
        }
        v = new Value(str.charAt(0)); // char

        _retTree = _t;
        return v;
    }

    public final ArrayList classBase(AST _t) throws RecognitionException {
        final ArrayList baseTypes = new ArrayList();

        final AST classBase_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        TypeSpec baseType;

        final AST __t152 = _t;
        final AST tmp62_AST_in = (AST) _t;
        match(_t, COLON);
        _t = _t.getFirstChild();
        {
            _loop154: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    baseType = typeExpression(_t);
                    _t = _retTree;
                    baseTypes.add(baseType);
                } else {
                    break _loop154;
                }

            } while (true);
        }
        _t = __t152;
        _t = _t.getNextSibling();
        _retTree = _t;
        return baseTypes;
    }

    public final void classConstructor(AST _t, final ClassInfo declaringClass)
            throws RecognitionException {

        final AST classConstructor_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        EnumSet<TypeSpec.Modifier> modifiers = EnumSet
                .noneOf(TypeSpec.Modifier.class);
        Func func = null;

        {
            {
                if (_t == null) {
                    _t = ASTNULL;
                }
                switch (_t.getType()) {
                    case MODIFIERS: {
                        modifiers = classModifiers(_t);
                        _t = _retTree;
                        break;
                    }
                    case LIT_FUNC: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(_t);
                    }
                }
            }
            func = functionLiteral(_t);
            _t = _retTree;
        }

        Debug.Assert(func.getOuterScope().isClassScope(),
                "members can only be in class scope");

        // if not Protected or Private, then default to Public
        if ((!modifiers.contains(TypeSpec.Modifier.Private))
                && (!modifiers.contains(TypeSpec.Modifier.Protected))) {
            modifiers.add(TypeSpec.Modifier.Public);
        }

        modifiers.add(TypeSpec.Modifier.Static); // constructors are always
                                                 // Static

        func.setIsConstructor(true);

        if ((func.getInfo().getReturnType() == null)
                || (!func.getInfo().getReturnType()
                        .equals(new TypeSpec(declaringClass)))) {
            ScigolTreeParser
                    .semanticError(func.getInfo().getDefinitionLocation(),
                            "class constructors must return the declaring class type (i.e. 'self')");
        }

        scope.addEntry(".ctor", TypeSpec.typeOf(func), func, null, modifiers);

        _retTree = _t;
    }

    public final void classMember(AST _t, final AST memberName,
            final boolean declaringInInterface) throws RecognitionException {

        final AST classMember_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST m = null;
        AST t = null;
        AST a = null;
        AST p = null;

        EnumSet<TypeSpec.Modifier> modifiers = EnumSet
                .noneOf(TypeSpec.Modifier.class);
        TypeSpec dt = null;
        final Value e = null;

        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case MODIFIERS: {
                    m = _t == ASTNULL ? null : (AST) _t;
                    modifiers = classModifiers(_t);
                    _t = _retTree;
                    break;
                }
                case 3:
                case PROP:
                case ASSIGN:
                case COLON: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case COLON: {
                    t = (AST) _t;
                    match(_t, COLON);
                    _t = _t.getNextSibling();
                    dt = typeExpression(_t);
                    _t = _retTree;
                    break;
                }
                case 3:
                case PROP:
                case ASSIGN: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case ASSIGN: {
                    a = (AST) _t;
                    match(_t, ASSIGN);
                    _t = _t.getNextSibling();
                    break;
                }
                case PROP: {
                    p = (AST) _t;
                    match(_t, PROP);
                    _t = _t.getNextSibling();
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        // !!! use declaringInInterface appropriately

        final boolean propertyDecl = (p != null);

        final boolean isConst = modifiers.contains(TypeSpec.Modifier.Const);
        final boolean isStatic = modifiers.contains(TypeSpec.Modifier.Static);
        final boolean staticOrConst = isStatic || isConst;

        // if not Protected or Private, then default to Public
        if ((!modifiers.contains(TypeSpec.Modifier.Private))
                && (!modifiers.contains(TypeSpec.Modifier.Protected))) {
            modifiers.add(TypeSpec.Modifier.Public);
        }

        if (propertyDecl) {
            //
            // handle a property declaration

            final String ident = memberName.getText();

            if (dt == null) {
                ScigolTreeParser.semanticError(locationOf(memberName),
                        "property member '" + ident
                                + "' must explicitly declare a type");
            }

            if (isStatic && (ident.equals("operator()"))) {
                ScigolTreeParser
                        .semanticError(locationOf(memberName),
                                "property member 'operator()' - an indexer - cannot be static");
            }

            // Check for existing property with the same name (properties cannot
            // be overloaded based on call signature)
            // ....!!!

            FuncInfo propSig = new FuncInfo(); // *explicit* argument of
                                               // property (with no return)
            boolean gotGetAccessor = false;
            boolean gotSetAccessor = false;
            Entry getterEntry = null;
            Entry setterEntry = null;

            // do some manual tree 'parsing' to get what we want without
            // actually
            // walking/executing the accessor bodies
            final AST propAST = p.getFirstChild();
            AST nextAST = propAST.getFirstChild();
            if (nextAST.getType() == LPAREN) { // a formal param list (possibly
                                               // empty) was supplied
                final AST paramListAST = nextAST.getFirstChild();
                propSig = formalParamList(paramListAST);

                nextAST = nextAST.getNextSibling();
            }

            // while more accessors (max of two - get & set)
            while ((nextAST != null) && (nextAST.getType() == IDENT)) {

                AST initializerAST = null;
                Object staticValue = null;

                final String accessorName = nextAST.getText();
                final CommonASTWithLocation astWithLoc = (CommonASTWithLocation) nextAST;
                final Location loc = (astWithLoc != null) ? astWithLoc.loc
                        : new Location();

                if ((!accessorName.equals("set"))
                        && (!accessorName.equals("get"))) {
                    ScigolTreeParser
                            .semanticError(loc,
                                    "the only valid property accessor names are 'get' and 'set'");
                }

                if (accessorName.equals("set")) {
                    if (gotSetAccessor) {
                        ScigolTreeParser.semanticError(loc,
                                "a property can only have one 'set' accessor");
                    }
                    gotSetAccessor = true;
                } else if (accessorName.equals("get")) {
                    if (gotGetAccessor) {
                        ScigolTreeParser.semanticError(loc,
                                "a property can only have one 'get' accessor");
                    }
                    gotGetAccessor = true;
                }

                final AST accessorBodyAST = nextAST.getFirstChild();

                // make appropriate accessor signature
                final FuncInfo accessorSig = propSig.accessorSig(accessorName,
                        dt);

                if ((accessorBodyAST.getType() == LITERAL_pre)
                        || (accessorBodyAST.getType() == LITERAL_post)
                        || (accessorBodyAST.getType() == EXPRLIST)) {
                    staticValue = functionLiteralBody(accessorBodyAST,
                            accessorSig);
                } else { // accessor is initialized with an expression
                    initializerAST = accessorBodyAST;

                    // if static or const, evaluate it now, otherwise it will be
                    // evaluated at instantiation time
                    if (staticOrConst) {
                        final Value v = expr(initializerAST);
                        staticValue = v.getValue();
                        initializerAST = null; // don't need it again
                    }

                }

                // now enter the appropriate accessor func into the current
                // class scope
                final String funcName = FuncInfo.accessorName(ident,
                        (accessorName.equals("get")));

                final Entry accessorEntry = new Entry(funcName, new TypeSpec(
                        accessorSig), staticValue, initializerAST, modifiers,
                        EnumSet.of(Entry.Flags.Method, Entry.Flags.Accessor),
                        -1, scope, loc);
                if (accessorName.equals("get")) {
                    getterEntry = accessorEntry;
                } else {
                    setterEntry = accessorEntry;
                }
                scope.addEntry(accessorEntry);

                nextAST = nextAST.getNextSibling();

            } // while

            // in addition to the accessor func members, we also add an Entry
            // for the property itself
            final EnumSet<Entry.Flags> flags = EnumSet.of(Entry.Flags.Property);
            if (gotGetAccessor) {
                flags.add(Entry.Flags.HasGetter);
            }
            if (gotSetAccessor) {
                flags.add(Entry.Flags.HasSetter);
            }
            final Entry propEntry = new Entry(ident, dt, null, null, modifiers,
                    flags, -1, scope, locationOf(memberName));

            // put a reference to the accessors into the property Entry (as it's
            // staticValue)
            final Entry.EntryPair accessors = new Entry.EntryPair();
            accessors.setter = setterEntry;
            accessors.getter = getterEntry;
            propEntry.setStaticValue(accessors);

            scope.addEntry(propEntry);

            // Also, put a reference to the propEntry in each accessor entry
            if (gotGetAccessor) {
                getterEntry.propertyEntry = propEntry;
            }
            if (gotSetAccessor) {
                setterEntry.propertyEntry = propEntry;
            }

        } else {

            //
            // member is a regular (non-property) declaration

            final boolean typeSupplied = (t != null);
            final boolean initializerSupplied = (a != null);

            if (!typeSupplied && !initializerSupplied) {
                ScigolTreeParser
                        .semanticError(t,
                                "member declaration must include a type or an initializer (or both)");
            }

            // get initializer expr AST, if supplied
            AST initializerAST = null;
            if (initializerSupplied) {
                initializerAST = a.getNextSibling();
            }

            // if (initializerSupplied) //!!!
            // Debug.WriteLine("initializerAST="+initializerAST.toStringTree());
            // else
            // Debug.WriteLine("no initializer");

            // if this is a is static or const, the initializer is evaluated
            // now,
            // otherwise it is evaluated as instantiation time (in which case
            // the type must be specified here)
            // (NB: methods are a special case as for convenience they don't
            // need to have their type
            // specified *if* the initializer is a func literal - which
            // specifies the signature/type)

            Object staticValue = null;
            boolean typeKnown = typeSupplied;

            if (initializerSupplied) {

                if (staticOrConst) {
                    final Value v = expr(initializerAST); // eval initializer
                                                          // now
                    staticValue = v.getValue();
                    initializerAST = null; // don't need it again
                    if (!typeSupplied) {
                        dt = TypeSpec.typeOf(staticValue); // deduce type from
                                                           // initializer
                        typeKnown = true;
                    }
                } else {
                    // !!! is it possible to have consistient behaviour and
                    // deduce the type too. But we need
                    // another rule that can tree parse the signature WITHOUT
                    // evaluating the default params (!)
                    // (this has the problem that the signature will be
                    // different! - without defaults)

                    // as a special convenience, if the initializer is a func
                    // literal and the type wasn't
                    // explicitly supplied, we can evaluate it now, saving the
                    // user from having
                    // to specify it in this case (avoiding the member type
                    // being 'any')
                    // (i.e. we can deduce the func type by evaluating the
                    // literal).
                    // NB: this has the side-effect that any parameter default
                    // expressions will be
                    // evaluated now, rather than at initialization time!
                    if ((!typeSupplied)
                            && (initializerAST.getType() == ScigolLexer.LIT_FUNC)) {
                        final Value v = expr(initializerAST);
                        staticValue = v.getValue();
                        initializerAST = null; // don't need it again

                        dt = TypeSpec.typeOf(staticValue);
                        typeKnown = true;
                    }

                }
            } else { // no initializer, instantiate default from type (either
                     // now or at instantiation)
                Debug.Assert(typeSupplied);

                if (staticOrConst) {
                    final Value v = dt.constructValue(null, null, this);
                    staticValue = v.getValue();
                } else { // instance
                    staticValue = null;
                }

            }

            if (!typeKnown) {
                dt = new TypeSpec(TypeSpec.anyType);
            }

            final String name = scope.topDeclarationIdent();
            if (scope.contains(name)) { // does this scope already define a
                                        // member named 'name'?
                // yes, that is only OK if it is a func (overloading) that isn't
                // ambiguous in its call signature
                if (!typeKnown || (typeKnown && !dt.isFunc())) {
                    ScigolTreeParser.semanticError(t,
                            "class already has a member named '" + name + "'");
                }
            }

            // add to scope (will thow in the case of ambiguous func
            // overloading)
            scope.addEntry(name, dt, staticValue, initializerAST, modifiers,
                    locationOf(memberName));

        }

        _retTree = _t;
    }

    public final EnumSet<TypeSpec.Modifier> classModifiers(AST _t)
            throws RecognitionException {
        EnumSet<TypeSpec.Modifier> modifiers;

        final AST classModifiers_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        modifiers = EnumSet.noneOf(TypeSpec.Modifier.class);

        final AST __t156 = _t;
        final AST tmp53_AST_in = (AST) _t;
        match(_t, MODIFIERS);
        _t = _t.getFirstChild();
        {
            _loop158: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                switch (_t.getType()) {
                    case LITERAL_public: {
                        final AST tmp54_AST_in = (AST) _t;
                        match(_t, LITERAL_public);
                        _t = _t.getNextSibling();
                        modifiers.add(TypeSpec.Modifier.Public);
                        break;
                    }
                    case LITERAL_protected: {
                        final AST tmp55_AST_in = (AST) _t;
                        match(_t, LITERAL_protected);
                        _t = _t.getNextSibling();
                        modifiers.add(TypeSpec.Modifier.Protected);
                        break;
                    }
                    case LITERAL_private: {
                        final AST tmp56_AST_in = (AST) _t;
                        match(_t, LITERAL_private);
                        _t = _t.getNextSibling();
                        modifiers.add(TypeSpec.Modifier.Private);
                        break;
                    }
                    case LITERAL_static: {
                        final AST tmp57_AST_in = (AST) _t;
                        match(_t, LITERAL_static);
                        _t = _t.getNextSibling();
                        modifiers.add(TypeSpec.Modifier.Static);
                        break;
                    }
                    case LITERAL_override: {
                        final AST tmp58_AST_in = (AST) _t;
                        match(_t, LITERAL_override);
                        _t = _t.getNextSibling();
                        modifiers.add(TypeSpec.Modifier.Override);
                        break;
                    }
                    case LITERAL_final: {
                        final AST tmp59_AST_in = (AST) _t;
                        match(_t, LITERAL_final);
                        _t = _t.getNextSibling();
                        modifiers.add(TypeSpec.Modifier.Final);
                        break;
                    }
                    default: {
                        break _loop158;
                    }
                }
            } while (true);
        }
        _t = __t156;
        _t = _t.getNextSibling();
        _retTree = _t;
        return modifiers;
    }

    public final TypeSpec classType(AST _t) throws RecognitionException {
        TypeSpec t = null;

        final AST classType_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST ct = null;
        AST i = null;

        EnumSet<TypeSpec.Modifier> modifiers = EnumSet
                .noneOf(TypeSpec.Modifier.class);
        ArrayList baseTypes = new ArrayList();
        final Object memberValue = null;

        final AST __t127 = _t;
        ct = _t == ASTNULL ? null : (AST) _t;
        match(_t, LITERAL_class);
        _t = _t.getFirstChild();
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case MODIFIERS: {
                    modifiers = classModifiers(_t);
                    _t = _retTree;
                    break;
                }
                case 3:
                case CTOR:
                case IDENT:
                case COLON: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case COLON: {
                    baseTypes = classBase(_t);
                    _t = _retTree;
                    break;
                }
                case 3:
                case CTOR:
                case IDENT: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        // start new class declaration & add interfaces
        TypeSpec baseType = TypeSpec.objectTypeSpec;
        int firstInterface = 0;
        if (baseTypes != null) {
            if (baseTypes.size() > 0) {
                final TypeSpec firstType = (TypeSpec) baseTypes.get(0);
                if (!firstType.isInterface()) {
                    baseType = firstType; // super class
                    firstInterface++;
                }
            }
        }
        final ClassInfo info = new ClassInfo(scope, baseType);
        info.setModifiers(modifiers);
        if (ct instanceof CommonASTWithLocation) {
            info.setDefinitionLocation(((CommonASTWithLocation) ct).loc);
        }
        if (baseTypes.size() > 1) {
            for (int ii = firstInterface; ii < baseTypes.size(); ii++) {
                final TypeSpec iType = (TypeSpec) baseTypes.get(ii);
                info.addInterface(iType);
            }
        }

        if (scope.topDeclarationIdent() != null) {
            info.setIdentityHint(scope.topDeclarationIdent());
        }

        // create a new scope for the class
        scope = new ClassScope(new TypeSpec(info));
        scope.setDefinitionLocation(locationOf(ct));

        {
            _loop135: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                switch (_t.getType()) {
                    case CTOR: {
                        {
                            final AST __t132 = _t;
                            final AST tmp61_AST_in = (AST) _t;
                            match(_t, CTOR);
                            _t = _t.getFirstChild();
                            classConstructor(_t, info);
                            _t = _retTree;
                            _t = __t132;
                            _t = _t.getNextSibling();

                        }
                        break;
                    }
                    case IDENT: {
                        {
                            final AST __t134 = _t;
                            i = _t == ASTNULL ? null : (AST) _t;
                            match(_t, IDENT);
                            _t = _t.getFirstChild();
                            scope.pushDeclarationIdent(i.getText());
                            classMember(_t, i, false);
                            _t = _retTree;
                            _t = __t134;
                            _t = _t.getNextSibling();

                            scope.popDeclarationIdent();

                        }
                        break;
                    }
                    default: {
                        break _loop135;
                    }
                }
            } while (true);
        }

        info.completeDefinition();
        t = ((ClassScope) scope).getClassType();

        scope = scope.getOuter(); // exit class scope

        _t = __t127;
        _t = _t.getNextSibling();
        _retTree = _t;
        return t;
    }

    public final Value declaration(AST _t) throws RecognitionException {
        Value v = null;

        final AST declaration_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST i = null;
        AST tt = null;
        AST at = null;
        TypeSpec t = null;
        Value e = null;
        Annotation annot = null;
        final LinkedList<Annotation> annotations = new LinkedList<Annotation>();
        EnumSet<TypeSpec.Modifier> modifiers = EnumSet
                .of(TypeSpec.Modifier.Public);

        final AST __t118 = _t;
        final AST tmp21_AST_in = (AST) _t;
        match(_t, LITERAL_let);
        _t = _t.getFirstChild();
        {
            _loop120: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == ANNOT_START)) {
                    annot = annotation(_t);
                    _t = _retTree;
                    annotations.add(annot);
                } else {
                    break _loop120;
                }

            } while (true);
        }
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case MODIFIERS: {
                    modifiers = classModifiers(_t);
                    _t = _retTree;
                    break;
                }
                case IDENT: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        i = (AST) _t;
        match(_t, IDENT);
        _t = _t.getNextSibling();
        scope.pushDeclarationIdent(i.getText());
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case COLON: {
                    tt = (AST) _t;
                    match(_t, COLON);
                    _t = _t.getNextSibling();
                    t = typeExpression(_t);
                    _t = _retTree;
                    break;
                }
                case 3:
                case ASSIGN: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case ASSIGN: {
                    at = (AST) _t;
                    match(_t, ASSIGN);
                    _t = _t.getNextSibling();
                    e = expr(_t);
                    _t = _retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t118;
        _t = _t.getNextSibling();

        // !!! do something with the modifiers

        scope.popDeclarationIdent();

        final String id = i.getText();

        final boolean typeSupplied = (t != null);
        final boolean initializerSupplied = (e != null);

        if (!typeSupplied) {
            if (!initializerSupplied) {
                // no initializer or type supplied
                ScigolTreeParser
                        .semanticError("must supply either a type or an initializer in let declaration of identifier '"
                                + id + "'");
                // e = new Value(new Any()); // null
                // t = new TypeSpec(e.GetType()); //huh?
            } else {
                // deduce type from type of initializer
                t = e.getType();
            }
        } else { // type supplied
            if (!initializerSupplied) {
                // construct default
                final FuncInfo callSig = new FuncInfo();
                callSig.setDefinitionLocation(locationOf(i));
                e = t.constructValue(callSig, null, this);

                // ...!!! if is a class, call empty constructor

                if (e == null) {
                    ScigolTreeParser
                            .semanticError(
                                    tt,
                                    "type "
                                            + t.typeName()
                                            + " doesn't have an accessible no-argument constructor");
                }
            } else {
                // check that the type of the initializer is equals to (or
                // convertable to)
                // the specified type

                final TypeSpec etype = e.getType();
                if (!TypeManager.existsImplicitConversion(etype, t, e)) {
                    ScigolTreeParser
                            .semanticError(
                                    at,
                                    "cannot declare variable '"
                                            + id
                                            + "' of type '"
                                            + t
                                            + "' with an incompatible initializer of type '"
                                            + etype.typeName() + "'");
                }

                // convert e to type t
                e = TypeManager.performImplicitConversion(etype, t, e);

            }
        }

        // !!! make this check appropriately for duplicate ident (considering
        // allowable overloading, etc.)

        // check if id is already defined in the current scope (ignore enclosing
        // scopes)
        if (!t.isFunc() && scope.contains(id)) {
            ScigolTreeParser.semanticError(i, "variable " + id
                    + " already declared in this scope");
        }

        scope.addEntry(id, t, e.getValue(), null, modifiers).addAnnotations(
                annotations);

        // return an LValue for the declared symbol rather than just the
        // initializer rvalue
        v = new Value(new LValue(new Symbol(scope, id, null)));

        _retTree = _t;
        return v;
    }

    public final Range eltRange(AST _t) throws RecognitionException {
        final Range r = new Range();

        final AST eltRange_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value e = null;

        final AST __t93 = _t;
        final AST tmp51_AST_in = (AST) _t;
        match(_t, COLON);
        _t = _t.getFirstChild();
        {
            _loop95: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    e = expr(_t);
                    _t = _retTree;
                } else {
                    break _loop95;
                }

            } while (true);
        }
        _t = __t93;
        _t = _t.getNextSibling();
        _retTree = _t;
        return r;
    }

    public final Value exceptionBlock(AST _t) throws RecognitionException {
        final Value v = null;

        final AST exceptionBlock_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        final Value a = null;

        final AST tmp28_AST_in = (AST) _t;
        match(_t, LITERAL_try);
        _t = _t.getNextSibling();

        final AST blockAST = exceptionBlock_AST_in;
        final AST tryBlockAST = blockAST.getFirstChild().getFirstChild();
        final AST ast = blockAST.getFirstChild().getNextSibling();

        Debug.WriteLine("blockAST=\n" + blockAST.toStringTree());
        Debug.WriteLine("\nast=\n" + ast.toStringTree());
        // ...

        _retTree = _t;
        return v;
    }

    public final Value expr(AST _t) throws RecognitionException {
        Value v = null;

        final AST expr_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        if (_t == null) {
            _t = ASTNULL;
        }
        switch (_t.getType()) {
            case EXPRLIST: {
                v = expressionList(_t, true);
                _t = _retTree;
                break;
            }
            case LIST: {
                v = listLiteral(_t);
                _t = _retTree;
                break;
            }
            case MAP: {
                v = mapLiteral(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_namespace: {
                v = namespaceScope(_t);
                _t = _retTree;
                break;
            }
            case MATRIX:
            case FUNC:
            case LIT_TRUE:
            case LIT_FALSE:
            case LIT_NULL:
            case LIT_FUNC:
            case BUILTIN_TYPE:
            case STRING_LITERAL:
            case NUM_INT:
            case NUM_DINT:
            case NUM_REAL:
            case NUM_SREAL:
            case CHAR_LITERAL:
            case LITERAL_class:
            case LITERAL_interface: {
                v = literal(_t);
                _t = _retTree;
                break;
            }
            case DOTDOT:
            case UNARY_MINUS:
            case UNARY_PLUS:
            case POST_INC:
            case POST_DEC:
            case LITERAL_or:
            case LITERAL_and:
            case NOT_EQUAL:
            case EQUAL:
            case LITERAL_is:
            case LITERAL_isnt:
            case LTHAN:
            case GTHAN:
            case LTE:
            case GTE:
            case PLUS:
            case MINUS:
            case STAR:
            case DIV:
            case MOD:
            case HAT:
            case INC:
            case DEC:
            case LNOT:
            case LITERAL_not:
            case HASH:
            case PRIME:
            case BAR: {
                v = arithmeticExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_logger: {
                v = logCall(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_let: {
                v = declaration(_t);
                _t = _retTree;
                break;
            }
            case ASSIGN: {
                v = assignmentExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_typeof: {
                v = typeofExpression(_t);
                _t = _retTree;
                break;
            }
            case IDENT:
            case SCOPE_ESCAPE: {
                v = symbol(_t);
                _t = _retTree;
                break;
            }
            case APPLICATION: {
                v = applicationExpression(_t);
                _t = _retTree;
                break;
            }
            case DOT: {
                v = selectionExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_if: {
                v = ifExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_do:
            case LITERAL_while: {
                v = whileExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_for: {
                v = forExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_foreach: {
                v = forEachExpression(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_try: {
                v = exceptionBlock(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_throw: {
                v = throwExpression(_t);
                _t = _retTree;
                break;
            }
            default: {
                throw new NoViableAltException(_t);
            }
        }
        _retTree = _t;
        return v;
    }

    public final Value expressionList(AST _t, final boolean inNewScope)
            throws RecognitionException {
        Value v = null;

        final AST expressionList_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST el = null;
        Value a = null;

        final AST __t29 = _t;
        el = _t == ASTNULL ? null : (AST) _t;
        match(_t, EXPRLIST);
        _t = _t.getFirstChild();

        if (inNewScope) {
            scope = new LocalScope(scope);
            scope.setDefinitionLocation(locationOf(el));
        }

        {
            _loop31: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    a = expr(_t);
                    _t = _retTree;
                    if (a != null) {
                        v = a;
                    }
                } else {
                    break _loop31;
                }

            } while (true);
        }

        if (inNewScope) {
            scope = scope.getOuter(); // exit scope
        }

        _t = __t29;
        _t = _t.getNextSibling();
        _retTree = _t;
        return v;
    }

    public final Value forEachExpression(AST _t) throws RecognitionException {
        final Value v = null;

        final AST forEachExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST i = null;

        Value t = null;
        Value from = null;
        Value to = null;
        Value by = null;
        Value container = null;

        final AST __t104 = _t;
        final AST tmp26_AST_in = (AST) _t;
        match(_t, LITERAL_foreach);
        _t = _t.getFirstChild();
        i = (AST) _t;
        match(_t, IDENT);
        _t = _t.getNextSibling();
        t = expr(_t);
        _t = _retTree;
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case COLON: {
                    final AST __t106 = _t;
                    final AST tmp27_AST_in = (AST) _t;
                    match(_t, COLON);
                    _t = _t.getFirstChild();
                    from = expr(_t);
                    _t = _retTree;
                    to = expr(_t);
                    _t = _retTree;
                    {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        switch (_t.getType()) {
                            case DOT:
                            case DOTDOT:
                            case UNARY_MINUS:
                            case UNARY_PLUS:
                            case EXPRLIST:
                            case MATRIX:
                            case LIST:
                            case MAP:
                            case FUNC:
                            case POST_INC:
                            case POST_DEC:
                            case APPLICATION:
                            case LIT_TRUE:
                            case LIT_FALSE:
                            case LIT_NULL:
                            case LIT_FUNC:
                            case BUILTIN_TYPE:
                            case LITERAL_namespace:
                            case IDENT:
                            case STRING_LITERAL:
                            case LITERAL_typeof:
                            case LITERAL_logger:
                            case ASSIGN:
                            case LITERAL_or:
                            case LITERAL_and:
                            case NOT_EQUAL:
                            case EQUAL:
                            case LITERAL_is:
                            case LITERAL_isnt:
                            case LTHAN:
                            case GTHAN:
                            case LTE:
                            case GTE:
                            case PLUS:
                            case MINUS:
                            case STAR:
                            case DIV:
                            case MOD:
                            case HAT:
                            case INC:
                            case DEC:
                            case LNOT:
                            case LITERAL_not:
                            case HASH:
                            case PRIME:
                            case BAR:
                            case SCOPE_ESCAPE:
                            case LITERAL_try:
                            case NUM_INT:
                            case NUM_DINT:
                            case NUM_REAL:
                            case NUM_SREAL:
                            case CHAR_LITERAL:
                            case LITERAL_let:
                            case LITERAL_class:
                            case LITERAL_interface:
                            case LITERAL_if:
                            case LITERAL_do:
                            case LITERAL_while:
                            case LITERAL_for:
                            case LITERAL_foreach:
                            case LITERAL_throw: {
                                by = expr(_t);
                                _t = _retTree;
                                break;
                            }
                            case 3: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(_t);
                            }
                        }
                    }
                    _t = __t106;
                    _t = _t.getNextSibling();
                    break;
                }
                case DOT:
                case DOTDOT:
                case UNARY_MINUS:
                case UNARY_PLUS:
                case EXPRLIST:
                case MATRIX:
                case LIST:
                case MAP:
                case FUNC:
                case POST_INC:
                case POST_DEC:
                case APPLICATION:
                case LIT_TRUE:
                case LIT_FALSE:
                case LIT_NULL:
                case LIT_FUNC:
                case BUILTIN_TYPE:
                case LITERAL_namespace:
                case IDENT:
                case STRING_LITERAL:
                case LITERAL_typeof:
                case LITERAL_logger:
                case ASSIGN:
                case LITERAL_or:
                case LITERAL_and:
                case NOT_EQUAL:
                case EQUAL:
                case LITERAL_is:
                case LITERAL_isnt:
                case LTHAN:
                case GTHAN:
                case LTE:
                case GTE:
                case PLUS:
                case MINUS:
                case STAR:
                case DIV:
                case MOD:
                case HAT:
                case INC:
                case DEC:
                case LNOT:
                case LITERAL_not:
                case HASH:
                case PRIME:
                case BAR:
                case SCOPE_ESCAPE:
                case LITERAL_try:
                case NUM_INT:
                case NUM_DINT:
                case NUM_REAL:
                case NUM_SREAL:
                case CHAR_LITERAL:
                case LITERAL_let:
                case LITERAL_class:
                case LITERAL_interface:
                case LITERAL_if:
                case LITERAL_do:
                case LITERAL_while:
                case LITERAL_for:
                case LITERAL_foreach:
                case LITERAL_throw: {
                    container = expr(_t);
                    _t = _retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t104;
        _t = _t.getNextSibling();

        Debug.Unimplemented();

        _retTree = _t;
        return v;
    }

    public final Value forExpression(AST _t) throws RecognitionException {
        final Value v = null;

        final AST forExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value first = null;
        Value test = null;
        Value third = null;

        final AST __t102 = _t;
        final AST tmp25_AST_in = (AST) _t;
        match(_t, LITERAL_for);
        _t = _t.getFirstChild();
        first = expr(_t);
        _t = _retTree;
        test = expr(_t);
        _t = _retTree;
        third = expr(_t);
        _t = _retTree;
        _t = __t102;
        _t = _t.getNextSibling();

        Debug.Unimplemented();

        _retTree = _t;
        return v;
    }

    public final FuncInfo formalParamList(AST _t) throws RecognitionException {
        FuncInfo fi = null;

        final AST formalParamList_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST i = null;

        TypeSpec t;
        Value e = null;
        final ArrayList pnames = new ArrayList();
        final ArrayList ptypes = new ArrayList();
        final ArrayList pdefaults = new ArrayList();
        final ArrayList phasdefault = new ArrayList();

        {
            _loop171: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == IDENT)) {
                    i = (AST) _t;
                    match(_t, IDENT);
                    _t = _t.getNextSibling();
                    t = typeExpression(_t);
                    _t = _retTree;
                    {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        switch (_t.getType()) {
                            case ASSIGN: {
                                final AST tmp64_AST_in = (AST) _t;
                                match(_t, ASSIGN);
                                _t = _t.getNextSibling();
                                e = expr(_t);
                                _t = _retTree;
                                break;
                            }
                            case 3:
                            case IDENT:
                            case GIVES: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(_t);
                            }
                        }
                    }

                    final String paramName = i.getText();
                    final TypeSpec paramType = t;
                    pnames.add(paramName);
                    ptypes.add(paramType);
                    phasdefault.add((e != null));
                    if (e != null) {
                        pdefaults.add(e.getValue());
                    } else {
                        pdefaults.add(null);
                    }
                    e = null;

                } else {
                    break _loop171;
                }

            } while (true);
        }

        // convert the ArrayLists to arrays
        final String[] pnamesa = new String[pnames.size()];
        final TypeSpec[] ptypesa = new TypeSpec[ptypes.size()];
        final Object[] pdefaultsa = new Object[pdefaults.size()];
        final boolean[] phasdefaulta = new boolean[phasdefault.size()];

        for (int p = 0; p < pnamesa.length; p++) {
            pnamesa[p] = (String) (String) pnames.get(p);
            ptypesa[p] = (TypeSpec) ptypes.get(p);
            pdefaultsa[p] = pdefaults.get(p);
            phasdefaulta[p] = ((Boolean) phasdefault.get(p)).booleanValue();
        }

        // return a partially filled-out FuncInfo
        fi = new FuncInfo(pnamesa, ptypesa, pdefaultsa, phasdefaulta, null);
        fi.setDefinitionLocation(locationOf(i));

        _retTree = _t;
        return fi;
    }

    public final FuncInfo funcSignature(AST _t) throws RecognitionException {
        FuncInfo v = null;

        final AST funcSignature_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST fl = null;
        TypeSpec rt = null;
        FuncInfo fi = null;

        final AST __t163 = _t;
        fl = _t == ASTNULL ? null : (AST) _t;
        match(_t, FUNC);
        _t = _t.getFirstChild();
        fi = formalParamList(_t);
        _t = _retTree;
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case GIVES: {
                    final AST tmp63_AST_in = (AST) _t;
                    match(_t, GIVES);
                    _t = _t.getNextSibling();
                    rt = typeExpression(_t);
                    _t = _retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t163;
        _t = _t.getNextSibling();

        if (rt != null) {
            fi.setReturnType(rt);
        }
        fi.setDefinitionLocation(locationOf(fl));
        v = fi;

        _retTree = _t;
        return v;
    }

    public final Func functionLiteral(AST _t) throws RecognitionException {
        Func v = null;

        final AST functionLiteral_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        FuncInfo sig;

        final AST __t173 = _t;
        final AST tmp34_AST_in = (AST) _t;
        match(_t, LIT_FUNC);
        _t = _t.getFirstChild();
        sig = funcSignature(_t);
        _t = _retTree;
        v = functionLiteralBody(_t, sig);
        _t = _retTree;
        _t = __t173;
        _t = _t.getNextSibling();
        _retTree = _t;
        return v;
    }

    public final Func functionLiteralBody(AST _t, final FuncInfo sig)
            throws RecognitionException {
        Func v = null;

        final AST functionLiteralBody_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        AST pre = null;
        AST post = null;
        AST exprList = null;

        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case LITERAL_pre: {
                    pre = (AST) _t;
                    match(_t, LITERAL_pre);
                    _t = _t.getNextSibling();
                    break;
                }
                case LITERAL_post: {
                    post = (AST) _t;
                    match(_t, LITERAL_post);
                    _t = _t.getNextSibling();
                    break;
                }
                case EXPRLIST: {
                    exprList = (AST) _t;
                    match(_t, EXPRLIST);
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        AST funcBodyAST = null;
        AST preBodyAST = null;
        AST postBodyAST = null;

        AST funcAST = functionLiteralBody_AST_in;
        // funcAST = (funcAST.getFirstChild()).getNextSibling(); // skip to
        // after signature

        // if funcAST has an EXPRLIST token type, it is the start of the func
        // body, else if it has pre or post
        // it is a pre/postcondition expression

        if (funcAST.getType() == EXPRLIST) {
            // Debug.WL("found func body first - no pre/post");
            funcBodyAST = funcAST;
        } else {
            if ((funcAST.getType() == LITERAL_pre)
                    || (funcAST.getType() == LITERAL_post)) {
                // Debug.WL("got first pre/post");
                final int type = funcAST.getType();
                funcAST = funcAST.getNextSibling(); // skip over "pre"/"post"
                if (type == LITERAL_pre) {
                    preBodyAST = funcAST;
                } else {
                    postBodyAST = funcAST;
                }
                funcAST = funcAST.getNextSibling(); // skip over condition body
            }

            if (funcAST.getType() == EXPRLIST) {
                // Debug.WL("got func body after first pre/post");
                funcBodyAST = funcAST;
            } else {
                if ((funcAST.getType() == LITERAL_pre)
                        || (funcAST.getType() == LITERAL_post)) {
                    // Debug.WL("got second pre/post");
                    final int type = funcAST.getType();
                    funcAST = funcAST.getNextSibling(); // skip over
                                                        // "pre"/"post"
                    if (type == LITERAL_post) {
                        if (postBodyAST != null) {
                            ScigolTreeParser
                                    .semanticError(
                                            sig.getDefinitionLocation(),
                                            "a func can only define a 'pre' expression, a 'post' expression or both (but not multiple of either)");
                        }
                        postBodyAST = funcAST;
                    } else {
                        if (preBodyAST != null) {
                            ScigolTreeParser
                                    .semanticError(
                                            sig.getDefinitionLocation(),
                                            "a func can only define a 'pre' expression, a 'post' expression or both (but not multiple of either)");
                        }
                        preBodyAST = funcAST;
                    }
                    funcAST = funcAST.getNextSibling(); // skip over condition
                                                        // body
                    funcBodyAST = funcAST;
                } else {
                    Debug.Assert(false, "couldn't match pre/post or body");
                }
            }
        }

        Debug.Assert(funcBodyAST != null, "no func body!?");

        // !!!
        // if (preBodyAST!=null)
        // Debug.WL("preBodyAST:"+preBodyAST.toStringTree());
        // if (postBodyAST!=null)
        // Debug.WL("postBodyAST:"+postBodyAST.toStringTree());

        v = new Func(sig, scope, this, funcBodyAST, preBodyAST, postBodyAST);
        // Debug.WriteLine("funcBody:"+v+"AST:"+funcBodyAST.toStringTree());

        _retTree = _t;
        return v;
    }

    public final FuncInfo funcType(AST _t) throws RecognitionException {
        FuncInfo v = null;

        final AST funcType_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST fl = null;
        TypeSpec rt = null;
        FuncInfo fi = null;

        final AST __t160 = _t;
        fl = _t == ASTNULL ? null : (AST) _t;
        match(_t, FUNC);
        _t = _t.getFirstChild();
        fi = paramTypeList(_t);
        _t = _retTree;
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case GIVES: {
                    final AST tmp60_AST_in = (AST) _t;
                    match(_t, GIVES);
                    _t = _t.getNextSibling();
                    rt = typeExpression(_t);
                    _t = _retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t160;
        _t = _t.getNextSibling();

        if (rt != null) {
            fi.setReturnType(rt);
        }
        fi.setDefinitionLocation(locationOf(fl));
        v = fi;

        _retTree = _t;
        return v;
    }

    public final Value ifExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST ifExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST ift = null;
        Value test = null;

        final AST __t97 = _t;
        ift = _t == ASTNULL ? null : (AST) _t;
        match(_t, LITERAL_if);
        _t = _t.getFirstChild();
        test = expr(_t);
        _t = _retTree;
        _t = __t97;
        _t = _t.getNextSibling();

        final TypeSpec t = TypeSpec.typeOf(test);
        if (!TypeManager.existsImplicitConversion(t, TypeSpec.boolTypeSpec,
                test)) {
            ScigolTreeParser.semanticError(ift,
                    "'if' test expression must be of type 'bool'");
        }
        final boolean btest = ((Boolean) TypeManager.performImplicitConversion(
                t, TypeSpec.boolTypeSpec, test).getValue()).booleanValue();
        final AST ifAST = ifExpression_AST_in;
        final AST thenAST = ifAST.getFirstChild().getNextSibling(); // skip over
                                                                    // test
        final AST elseAST = thenAST.getNextSibling();

        // conditionally walk either the then or else tree
        if (btest) {
            v = expr(thenAST);
        } else {
            if (elseAST != null) {
                v = expr(elseAST);
            }
        }

        _retTree = _t;
        return v;
    }

    public final ArrayList indexing(AST _t) throws RecognitionException {
        final ArrayList indexValues = null;

        final AST indexing_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Range r1 = null;
        Range r2 = null;

        final AST __t90 = _t;
        final AST tmp50_AST_in = (AST) _t;
        match(_t, LPAREN);
        _t = _t.getFirstChild();
        r1 = eltRange(_t);
        _t = _retTree;
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case COLON: {
                    r2 = eltRange(_t);
                    _t = _retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t90;
        _t = _t.getNextSibling();
        _retTree = _t;
        return indexValues;
    }

    public final TypeSpec interfaceType(AST _t) throws RecognitionException {
        TypeSpec t = null;

        final AST interfaceType_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST it = null;
        AST i = null;

        EnumSet<TypeSpec.Modifier> modifiers = EnumSet
                .noneOf(TypeSpec.Modifier.class);
        ArrayList baseTypes = new ArrayList();
        final Object memberValue = null;

        final AST __t137 = _t;
        it = _t == ASTNULL ? null : (AST) _t;
        match(_t, LITERAL_interface);
        _t = _t.getFirstChild();
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case MODIFIERS: {
                    modifiers = classModifiers(_t);
                    _t = _retTree;
                    break;
                }
                case 3:
                case IDENT:
                case COLON: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case COLON: {
                    baseTypes = classBase(_t);
                    _t = _retTree;
                    break;
                }
                case 3:
                case IDENT: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        // start new class declaration & add interfaces
        final ClassInfo info = new ClassInfo(scope, null); // interface
        info.setModifiers(modifiers);
        if (it instanceof CommonASTWithLocation) {
            info.setDefinitionLocation(((CommonASTWithLocation) it).loc);
        }
        if (baseTypes == null) {
            baseTypes = new ArrayList();
        }
        if (baseTypes.size() > 0) {
            for (int ii = 0; ii < baseTypes.size(); ii++) {
                final TypeSpec iType = (TypeSpec) baseTypes.get(ii);
                if (!iType.isBuiltinObject()) { // don't add object again
                    if (!iType.isInterface()) {
                        ScigolTreeParser
                                .semanticError(locationOf(it),
                                        "interfaces can only inherit other interfaces (and 'object')");
                    }
                    info.addInterface(iType);
                }
            }
        }

        if (scope.topDeclarationIdent() != null) {
            info.setIdentityHint(scope.topDeclarationIdent());
        }

        // create a new scope for the class
        scope = new ClassScope(new TypeSpec(info));
        scope.setDefinitionLocation(locationOf(it));

        {
            _loop143: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == IDENT)) {
                    {
                        final AST __t142 = _t;
                        i = _t == ASTNULL ? null : (AST) _t;
                        match(_t, IDENT);
                        _t = _t.getFirstChild();
                        scope.pushDeclarationIdent(i.getText());
                        classMember(_t, i, true);
                        _t = _retTree;
                        _t = __t142;
                        _t = _t.getNextSibling();

                        scope.popDeclarationIdent();

                    }
                } else {
                    break _loop143;
                }

            } while (true);
        }

        info.completeDefinition();
        t = ((ClassScope) scope).getClassType();

        scope = scope.getOuter(); // exit class scope

        _t = __t137;
        _t = _t.getNextSibling();
        _retTree = _t;
        return t;
    }

    public final Value isExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST isExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value lhs = null, rhs;
        boolean neg = false;

        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case LITERAL_is: {
                    final AST __t66 = _t;
                    final AST tmp47_AST_in = (AST) _t;
                    match(_t, LITERAL_is);
                    _t = _t.getFirstChild();
                    lhs = expr(_t);
                    _t = _retTree;
                    rhs = expr(_t);
                    _t = _retTree;
                    _t = __t66;
                    _t = _t.getNextSibling();
                    break;
                }
                case LITERAL_isnt: {
                    final AST __t67 = _t;
                    final AST tmp48_AST_in = (AST) _t;
                    match(_t, LITERAL_isnt);
                    _t = _t.getFirstChild();
                    lhs = expr(_t);
                    _t = _retTree;
                    rhs = expr(_t);
                    _t = _retTree;
                    neg = true;
                    _t = __t67;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        // !!! may be able to do away with much of this any logic if Value (or
        // something) takes care
        // of Any extraction automatically

        final TypeSpec anyType = new TypeSpec(TypeSpec.anyType);

        // if lhs is null it can only be identical with a null rhs
        if ((lhs.getValue() == null)
                || (TypeSpec.typeOf(lhs).equals(anyType) && (((Any) lhs
                        .getValue()).value == null))) {
            // if rhs is not null, check if it is an Any and if so, look inside
            // for a null value
            if (rhs != null) {
                TypeSpec trhs = TypeSpec.typeOf(rhs);
                if (trhs.equals(anyType)) { // extract from Any
                    rhs = new Value(((Any) rhs.getValue()).value);
                    trhs = TypeSpec.typeOf(rhs);
                }
                if (trhs.isType() && rhs.getValue().equals(anyType)) {
                    v = new Value(true);
                } else {
                    v = new Value(rhs.getValue() == null); // lhs & rhs null
                                                           // (rhs was
                                                           // Any(null))
                }
            } else {
                v = new Value(true); // lhs & rhs null
            }
        } else {
            // similarly, a null rhs can only be identical with a null lhs
            if ((rhs.getValue() == null)
                    || (TypeSpec.typeOf(rhs).equals(anyType) && (((Any) rhs
                            .getValue()).value == null))) {
                v = new Value(
                        (lhs.getValue() == null)
                                || (TypeSpec.typeOf(lhs).equals(anyType) && (((Any) lhs
                                        .getValue()).value == null)));
            } else {
                // OK, both sides are non-null (but may be Any's, if so extract
                // their values)
                TypeSpec tlhs = TypeSpec.typeOf(lhs);
                TypeSpec trhs = TypeSpec.typeOf(rhs);

                if (tlhs.equals(anyType)) {
                    lhs = new Value(((Any) lhs.getValue()).value);
                    tlhs = TypeSpec.typeOf(lhs);
                }
                if (trhs.equals(anyType)) {
                    rhs = new Value(((Any) rhs.getValue()).value);
                    trhs = TypeSpec.typeOf(rhs);
                }

                // finally, if rhs is of type 'type', do a type comparison, else
                // do identity
                if (trhs.isType()) {
                    if (rhs.getValue().equals(anyType)) {
                        v = new Value(true); // 'is any'
                    } else {
                        v = new Value(TypeSpec.typeOf(lhs).equals(
                                rhs.getValue())); // lhs is-a rhs?
                    }
                } else {
                    v = new Value(lhs.getValue() == rhs.getValue()); // is lhs
                                                                     // the same
                                                                     // object
                                                                     // instance
                                                                     // as rhs?
                                                                     // i.e.
                                                                     // identity,
                                                                     // not
                                                                     // equality
                }
            }
        }

        if (neg) {
            v = new Value(new Boolean(
                    (!((Boolean) v.getValue()).booleanValue()))); // flip sense
                                                                  // for isnt
        }

        Debug.Assert(v.getValue() instanceof Boolean);

        _retTree = _t;
        return v;
    }

    public final Value listLiteral(AST _t) throws RecognitionException {
        Value v = null;

        final AST listLiteral_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        final List l = new List();

        final AST __t4 = _t;
        final AST tmp2_AST_in = (AST) _t;
        match(_t, LIST);
        _t = _t.getFirstChild();
        {
            _loop6: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    v = expr(_t);
                    _t = _retTree;
                    l.add(v.getValue());
                } else {
                    break _loop6;
                }

            } while (true);
        }
        _t = __t4;
        _t = _t.getNextSibling();
        v = new Value(l);
        _retTree = _t;
        return v;
    }

    public final Value literal(AST _t) throws RecognitionException {
        Value v = null;

        final AST literal_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        TypeSpec t;
        Func f;

        if (_t == null) {
            _t = ASTNULL;
        }
        switch (_t.getType()) {
            case MATRIX:
            case LIT_TRUE:
            case LIT_FALSE:
            case LIT_NULL:
            case NUM_INT:
            case NUM_DINT:
            case NUM_REAL:
            case NUM_SREAL: {
                v = number(_t);
                _t = _retTree;
                break;
            }
            case STRING_LITERAL: {
                v = stringLit(_t);
                _t = _retTree;
                break;
            }
            case CHAR_LITERAL: {
                v = charLit(_t);
                _t = _retTree;
                break;
            }
            case FUNC:
            case BUILTIN_TYPE:
            case LITERAL_class:
            case LITERAL_interface: {
                t = typeLiteral(_t);
                _t = _retTree;
                v = new Value(t);
                break;
            }
            case LIT_FUNC: {
                f = functionLiteral(_t);
                _t = _retTree;
                v = new Value(f);
                break;
            }
            default: {
                throw new NoViableAltException(_t);
            }
        }
        _retTree = _t;
        return v;
    }

    public final Value logCall(AST _t) throws RecognitionException {
        Value v = null;

        final AST logCall_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST i = null;

        final AST __t177 = _t;
        final AST tmp19_AST_in = (AST) _t;
        match(_t, LITERAL_logger);
        _t = _t.getFirstChild();
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case COMMA: {
                    final AST __t179 = _t;
                    final AST tmp20_AST_in = (AST) _t;
                    match(_t, COMMA);
                    _t = _t.getFirstChild();
                    i = (AST) _t;
                    match(_t, IDENT);
                    _t = _t.getNextSibling();
                    _t = __t179;
                    _t = _t.getNextSibling();
                    break;
                }
                case DOT:
                case DOTDOT:
                case UNARY_MINUS:
                case UNARY_PLUS:
                case EXPRLIST:
                case MATRIX:
                case LIST:
                case MAP:
                case FUNC:
                case POST_INC:
                case POST_DEC:
                case APPLICATION:
                case LIT_TRUE:
                case LIT_FALSE:
                case LIT_NULL:
                case LIT_FUNC:
                case BUILTIN_TYPE:
                case LITERAL_namespace:
                case IDENT:
                case STRING_LITERAL:
                case LITERAL_typeof:
                case LITERAL_logger:
                case ASSIGN:
                case LITERAL_or:
                case LITERAL_and:
                case NOT_EQUAL:
                case EQUAL:
                case LITERAL_is:
                case LITERAL_isnt:
                case LTHAN:
                case GTHAN:
                case LTE:
                case GTE:
                case PLUS:
                case MINUS:
                case STAR:
                case DIV:
                case MOD:
                case HAT:
                case INC:
                case DEC:
                case LNOT:
                case LITERAL_not:
                case HASH:
                case PRIME:
                case BAR:
                case SCOPE_ESCAPE:
                case LITERAL_try:
                case NUM_INT:
                case NUM_DINT:
                case NUM_REAL:
                case NUM_SREAL:
                case CHAR_LITERAL:
                case LITERAL_let:
                case LITERAL_class:
                case LITERAL_interface:
                case LITERAL_if:
                case LITERAL_do:
                case LITERAL_while:
                case LITERAL_for:
                case LITERAL_foreach:
                case LITERAL_throw: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        v = expr(_t);
        _t = _retTree;
        _t = __t177;
        _t = _t.getNextSibling();

        if (i != null) {
            System.out.print(i.getText() + ": ");
        }
        System.out.println(v.toString());

        _retTree = _t;
        return v;
    }

    public final Value logicalAndExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST logicalAndExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        Value lhs;

        final AST __t75 = _t;
        final AST tmp43_AST_in = (AST) _t;
        match(_t, LITERAL_and);
        _t = _t.getFirstChild();
        lhs = expr(_t);
        _t = _retTree;
        _t = __t75;
        _t = _t.getNextSibling();

        if (!Math.isLogicalTrue(lhs)) {
            v = new Value(new Boolean(false));
        } else {
            final AST logAndAST = logicalAndExpression_AST_in;
            final AST rhsAST = logAndAST.getFirstChild().getNextSibling(); // skip
                                                                           // over
                                                                           // lhs
            final Value rhs = expr(rhsAST);
            v = new Value(Math.isLogicalTrue(rhs));
        }

        _retTree = _t;
        return v;
    }

    public final Value logicalNotExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST logicalNotExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        Value rhs;

        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case LNOT: {
                    final AST __t78 = _t;
                    final AST tmp45_AST_in = (AST) _t;
                    match(_t, LNOT);
                    _t = _t.getFirstChild();
                    rhs = expr(_t);
                    _t = _retTree;
                    _t = __t78;
                    _t = _t.getNextSibling();
                    break;
                }
                case LITERAL_not: {
                    final AST __t79 = _t;
                    final AST tmp46_AST_in = (AST) _t;
                    match(_t, LITERAL_not);
                    _t = _t.getFirstChild();
                    rhs = expr(_t);
                    _t = _retTree;
                    _t = __t79;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        if (Math.isLogicalTrue(rhs)) {
            v = new Value(new Boolean(false));
        } else {
            v = new Value(new Boolean(true));
        }

        _retTree = _t;
        return v;
    }

    public final Value logicalOrExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST logicalOrExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        Value lhs;

        final AST __t69 = _t;
        final AST tmp44_AST_in = (AST) _t;
        match(_t, LITERAL_or);
        _t = _t.getFirstChild();
        lhs = expr(_t);
        _t = _retTree;
        _t = __t69;
        _t = _t.getNextSibling();

        if (Math.isLogicalTrue(lhs)) {
            v = new Value(true);
        } else {
            final AST logOrAST = logicalOrExpression_AST_in;
            final AST rhsAST = logOrAST.getFirstChild().getNextSibling(); // skip
                                                                          // over
                                                                          // lhs
            final Value rhs = expr(rhsAST);
            v = new Value(Math.isLogicalTrue(rhs));
        }

        _retTree = _t;
        return v;
    }

    public final Value mapLiteral(AST _t) throws RecognitionException {
        Value v = null;

        final AST mapLiteral_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        final Map m = new Map();
        Value key = null;
        Value val = null;

        final AST __t8 = _t;
        final AST tmp3_AST_in = (AST) _t;
        match(_t, MAP);
        _t = _t.getFirstChild();
        {
            _loop10: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    key = expr(_t);
                    _t = _retTree;
                    final AST tmp4_AST_in = (AST) _t;
                    match(_t, GIVES);
                    _t = _t.getNextSibling();
                    val = expr(_t);
                    _t = _retTree;
                    m.set_Item(key.getValue(), new Any(val.getValue()));
                } else {
                    break _loop10;
                }

            } while (true);
        }
        _t = __t8;
        _t = _t.getNextSibling();
        v = new Value(m);
        _retTree = _t;
        return v;
    }

    public final Value matrixexpr(AST _t) throws RecognitionException {
        Value v = null;

        final AST matrixexpr_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Vector r;

        final AST __t184 = _t;
        final AST tmp65_AST_in = (AST) _t;
        match(_t, MATRIX);
        _t = _t.getFirstChild();
        r = matrixrow(_t);
        _t = _retTree;
        v = new Value(r);
        {
            _loop188: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == SEMI)) {
                    {
                        int _cnt187 = 0;
                        _loop187: do {
                            if (_t == null) {
                                _t = ASTNULL;
                            }
                            if ((_t.getType() == SEMI)) {
                                final AST tmp66_AST_in = (AST) _t;
                                match(_t, SEMI);
                                _t = _t.getNextSibling();
                            } else {
                                if (_cnt187 >= 1) {
                                    break _loop187;
                                } else {
                                    throw new NoViableAltException(_t);
                                }
                            }

                            _cnt187++;
                        } while (true);
                    }
                    r = matrixrow(_t);
                    _t = _retTree;

                    if (v.getValue() instanceof Vector) {
                        v = new Value(new Matrix((Vector) v.getValue()));
                    }
                    ((Matrix) v.getValue()).appendRowVector(r);

                } else {
                    break _loop188;
                }

            } while (true);
        }
        _t = __t184;
        _t = _t.getNextSibling();
        _retTree = _t;
        return v;
    }

    public final Vector matrixrow(AST _t) throws RecognitionException {
        final Vector v = new Vector();

        final AST matrixrow_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value e;

        {
            int _cnt191 = 0;
            _loop191: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    e = expr(_t);
                    _t = _retTree;
                    v.appendElement(e.getValue());
                } else {
                    if (_cnt191 >= 1) {
                        break _loop191;
                    } else {
                        throw new NoViableAltException(_t);
                    }
                }

                _cnt191++;
            } while (true);
        }
        _retTree = _t;
        return v;
    }

    public final Value namespaceBody(AST _t) throws RecognitionException {
        Value v = null;

        final AST namespaceBody_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        final AST tmp1_AST_in = (AST) _t;
        match(_t, LCURLY);
        _t = _t.getNextSibling();
        {
            _loop39: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == LITERAL_using)) {
                    usingDecl(_t);
                    _t = _retTree;
                } else {
                    break _loop39;
                }

            } while (true);
        }
        {
            _loop41: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    v = expr(_t);
                    _t = _retTree;
                } else {
                    break _loop41;
                }

            } while (true);
        }
        _retTree = _t;
        return v;
    }

    public final String namespaceName(AST _t) throws RecognitionException {
        String name = "";

        final AST namespaceName_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST i = null;
        AST i2 = null;

        i = (AST) _t;
        match(_t, IDENT);
        _t = _t.getNextSibling();
        name += i.getText();
        {
            _loop34: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == DOT)) {
                    final AST tmp35_AST_in = (AST) _t;
                    match(_t, DOT);
                    _t = _t.getNextSibling();
                    i2 = (AST) _t;
                    match(_t, IDENT);
                    _t = _t.getNextSibling();
                    name += "." + i2.getText();
                } else {
                    break _loop34;
                }

            } while (true);
        }
        _retTree = _t;
        return name;
    }

    public final Value namespaceScope(AST _t) throws RecognitionException {
        Value v = null;

        final AST namespaceScope_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST ns = null;
        String id;
        Scope savedOuterScope;

        final AST __t36 = _t;
        ns = _t == ASTNULL ? null : (AST) _t;
        match(_t, LITERAL_namespace);
        _t = _t.getFirstChild();
        id = namespaceName(_t);
        _t = _retTree;

        if (!scope.isNamespaceScope()) {
            ScigolTreeParser
                    .semanticError(
                            locationOf(ns),
                            "can't define namespace '"
                                    + id
                                    + "' within current scope (namespaces can only be nested directly within other namespaces)");
        }

        savedOuterScope = scope;
        scope = NamespaceScope.newOrExistingNamespaceScope(id, scope);
        scope.setDefinitionLocation(locationOf(ns));

        v = namespaceBody(_t);
        _t = _retTree;

        scope = savedOuterScope; // exit back to enclosing scope

        _t = __t36;
        _t = _t.getNextSibling();
        _retTree = _t;
        return v;
    }

    public final Value normExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST normExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST bl = null;
        Value e = null;

        final AST __t71 = _t;
        bl = _t == ASTNULL ? null : (AST) _t;
        match(_t, BAR);
        _t = _t.getFirstChild();
        e = expr(_t);
        _t = _retTree;
        _t = __t71;
        _t = _t.getNextSibling();

        if (e.getValue() == null) {
            ScigolTreeParser.semanticError(locationOf(bl),
                    "null value in operator|| (norm)");
        }

        v = Math.performOverloadedOperation("operator||", e, null);

        _retTree = _t;
        return v;
    }

    public final Value number(AST _t) throws RecognitionException {
        Value v = null;

        final AST number_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST i = null;
        AST d = null;
        AST r = null;
        AST s = null;

        if (_t == null) {
            _t = ASTNULL;
        }
        switch (_t.getType()) {
            case NUM_INT: {
                i = (AST) _t;
                match(_t, NUM_INT);
                _t = _t.getNextSibling();
                v = new Value(new Integer(i.getText()));
                break;
            }
            case NUM_DINT: {
                d = (AST) _t;
                match(_t, NUM_DINT);
                _t = _t.getNextSibling();
                v = new Value(new Long(d.getText()));
                break;
            }
            case NUM_REAL: {
                r = (AST) _t;
                match(_t, NUM_REAL);
                _t = _t.getNextSibling();
                v = new Value(new Double(r.getText()));
                break;
            }
            case NUM_SREAL: {
                s = (AST) _t;
                match(_t, NUM_SREAL);
                _t = _t.getNextSibling();
                v = new Value(new Float(s.getText()));
                break;
            }
            case LIT_TRUE: {
                final AST tmp31_AST_in = (AST) _t;
                match(_t, LIT_TRUE);
                _t = _t.getNextSibling();
                v = new Value(new Boolean(true));
                break;
            }
            case LIT_FALSE: {
                final AST tmp32_AST_in = (AST) _t;
                match(_t, LIT_FALSE);
                _t = _t.getNextSibling();
                v = new Value(new Boolean(false));
                break;
            }
            case LIT_NULL: {
                final AST tmp33_AST_in = (AST) _t;
                match(_t, LIT_NULL);
                _t = _t.getNextSibling();
                v = new Value(new Any(null));
                break;
            }
            case MATRIX: {
                v = matrixexpr(_t);
                _t = _retTree;
                break;
            }
            default: {
                throw new NoViableAltException(_t);
            }
        }
        _retTree = _t;
        return v;
    }

    public final FuncInfo paramTypeList(AST _t) throws RecognitionException {
        FuncInfo fi = null;

        final AST paramTypeList_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        TypeSpec t;
        final ArrayList ptypes = new ArrayList();

        {
            _loop167: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_tokenSet_0.member(_t.getType()))) {
                    t = typeExpression(_t);
                    _t = _retTree;

                    ptypes.add(t);

                } else {
                    break _loop167;
                }

            } while (true);
        }

        final int numArgs = ptypes.size();
        // convert the ArrayLists to arrays
        final TypeSpec[] ptypesa = new TypeSpec[numArgs];

        for (int p = 0; p < numArgs; p++) {
            ptypesa[p] = (TypeSpec) ptypes.get(p);
        }

        // return a partially filled-out FuncInfo
        fi = new FuncInfo(ptypesa, null);

        _retTree = _t;
        return fi;
    }

    public final Value postfixExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST postfixExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value e = null;
        boolean dec = false;
        boolean prime = false;

        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case POST_INC: {
                    final AST __t86 = _t;
                    final AST tmp40_AST_in = (AST) _t;
                    match(_t, POST_INC);
                    _t = _t.getFirstChild();
                    e = expr(_t);
                    _t = _retTree;
                    _t = __t86;
                    _t = _t.getNextSibling();
                    break;
                }
                case POST_DEC: {
                    final AST __t87 = _t;
                    final AST tmp41_AST_in = (AST) _t;
                    match(_t, POST_DEC);
                    _t = _t.getFirstChild();
                    e = expr(_t);
                    _t = _retTree;
                    dec = true;
                    _t = __t87;
                    _t = _t.getNextSibling();
                    break;
                }
                case PRIME: {
                    final AST __t88 = _t;
                    final AST tmp42_AST_in = (AST) _t;
                    match(_t, PRIME);
                    _t = _t.getFirstChild();
                    e = expr(_t);
                    _t = _retTree;
                    prime = true;
                    _t = __t88;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        if (prime) {
            v = Math.performOverloadedOperation("operator'", e, null);
        } else {
            // call appropriate operator (++/--)
            Debug.Unimplemented("post ++/--");
        }

        _retTree = _t;
        return v;
    }

    public final Value prefixExpression(AST _t) throws RecognitionException {
        final Value v = null;

        final AST prefixExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value e = null;
        boolean dec = false;

        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case INC: {
                    final AST __t82 = _t;
                    final AST tmp38_AST_in = (AST) _t;
                    match(_t, INC);
                    _t = _t.getFirstChild();
                    e = expr(_t);
                    _t = _retTree;
                    _t = __t82;
                    _t = _t.getNextSibling();
                    break;
                }
                case DEC: {
                    final AST __t83 = _t;
                    final AST tmp39_AST_in = (AST) _t;
                    match(_t, DEC);
                    _t = _t.getFirstChild();
                    e = expr(_t);
                    _t = _retTree;
                    dec = true;
                    _t = __t83;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }

        Debug.Unimplemented("prefix");

        _retTree = _t;
        return v;
    }

    public final Value program(AST _t) throws RecognitionException {
        Value v = null;

        final AST program_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        if (_t == null) {
            _t = ASTNULL;
        }
        switch (_t.getType()) {
            case DOT:
            case DOTDOT:
            case UNARY_MINUS:
            case UNARY_PLUS:
            case EXPRLIST:
            case MATRIX:
            case LIST:
            case MAP:
            case FUNC:
            case POST_INC:
            case POST_DEC:
            case APPLICATION:
            case LIT_TRUE:
            case LIT_FALSE:
            case LIT_NULL:
            case LIT_FUNC:
            case BUILTIN_TYPE:
            case LITERAL_namespace:
            case IDENT:
            case STRING_LITERAL:
            case LITERAL_typeof:
            case LITERAL_logger:
            case ASSIGN:
            case LITERAL_or:
            case LITERAL_and:
            case NOT_EQUAL:
            case EQUAL:
            case LITERAL_is:
            case LITERAL_isnt:
            case LTHAN:
            case GTHAN:
            case LTE:
            case GTE:
            case PLUS:
            case MINUS:
            case STAR:
            case DIV:
            case MOD:
            case HAT:
            case INC:
            case DEC:
            case LNOT:
            case LITERAL_not:
            case HASH:
            case PRIME:
            case BAR:
            case SCOPE_ESCAPE:
            case LITERAL_try:
            case NUM_INT:
            case NUM_DINT:
            case NUM_REAL:
            case NUM_SREAL:
            case CHAR_LITERAL:
            case LITERAL_let:
            case LITERAL_class:
            case LITERAL_interface:
            case LITERAL_if:
            case LITERAL_do:
            case LITERAL_while:
            case LITERAL_for:
            case LITERAL_foreach:
            case LITERAL_throw: {
                v = expr(_t);
                _t = _retTree;
                break;
            }
            case LCURLY: {
                v = namespaceBody(_t);
                _t = _retTree;
                break;
            }
            default: {
                throw new NoViableAltException(_t);
            }
        }
        _retTree = _t;
        return v;
    }

    public final String qualifiedIdent(AST _t) throws RecognitionException {
        String name = "";

        final AST qualifiedIdent_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST i = null;
        AST i2 = null;

        i = (AST) _t;
        match(_t, IDENT);
        _t = _t.getNextSibling();
        name = i.getText();
        {
            _loop113: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == IDENT)) {
                    i2 = (AST) _t;
                    match(_t, IDENT);
                    _t = _t.getNextSibling();
                    name += "." + i2.getText();
                } else {
                    break _loop113;
                }

            } while (true);
        }
        _retTree = _t;
        return name;
    }

    public final Value rangeExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST rangeExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST dd = null;
        Value lhs = null;
        Value rhs = null;

        final AST __t62 = _t;
        dd = _t == ASTNULL ? null : (AST) _t;
        match(_t, DOTDOT);
        _t = _t.getFirstChild();
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case DOT:
                case DOTDOT:
                case UNARY_MINUS:
                case UNARY_PLUS:
                case EXPRLIST:
                case MATRIX:
                case LIST:
                case MAP:
                case FUNC:
                case POST_INC:
                case POST_DEC:
                case APPLICATION:
                case LIT_TRUE:
                case LIT_FALSE:
                case LIT_NULL:
                case LIT_FUNC:
                case BUILTIN_TYPE:
                case LITERAL_namespace:
                case IDENT:
                case STRING_LITERAL:
                case LITERAL_typeof:
                case LITERAL_logger:
                case ASSIGN:
                case LITERAL_or:
                case LITERAL_and:
                case NOT_EQUAL:
                case EQUAL:
                case LITERAL_is:
                case LITERAL_isnt:
                case LTHAN:
                case GTHAN:
                case LTE:
                case GTE:
                case PLUS:
                case MINUS:
                case STAR:
                case DIV:
                case MOD:
                case HAT:
                case INC:
                case DEC:
                case LNOT:
                case LITERAL_not:
                case HASH:
                case PRIME:
                case BAR:
                case SCOPE_ESCAPE:
                case LITERAL_try:
                case NUM_INT:
                case NUM_DINT:
                case NUM_REAL:
                case NUM_SREAL:
                case CHAR_LITERAL:
                case LITERAL_let:
                case LITERAL_class:
                case LITERAL_interface:
                case LITERAL_if:
                case LITERAL_do:
                case LITERAL_while:
                case LITERAL_for:
                case LITERAL_foreach:
                case LITERAL_throw: {
                    lhs = expr(_t);
                    _t = _retTree;
                    rhs = expr(_t);
                    _t = _retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t62;
        _t = _t.getNextSibling();

        if (lhs == null) {
            lhs = new Value(0);
        }
        if (rhs == null) {
            rhs = new Value(-1);
        }

        lhs.rvalue();
        rhs.rvalue();

        final TypeSpec tlhs = TypeSpec.typeOf(lhs);
        final TypeSpec trhs = TypeSpec.typeOf(rhs);
        final TypeSpec intType = new TypeSpec(TypeSpec.intType);

        if (!TypeManager.existsImplicitConversion(tlhs, intType, lhs)
                || !TypeManager.existsImplicitConversion(trhs, intType, rhs)) {
            ScigolTreeParser
                    .semanticError(
                            locationOf(dd),
                            "both the RHS and LHS of the range operator '..' must be compatible with type 'int'");
        }

        final int from = ((Integer) TypeManager.performImplicitConversion(tlhs,
                intType, lhs).getValue()).intValue();
        final int to = ((Integer) TypeManager.performImplicitConversion(trhs,
                intType, rhs).getValue()).intValue();

        if (((from < 0) && (to < 0) && !(from <= to))
                || ((from > 0) && (to > 0) && !(from <= to))) {
            ScigolTreeParser.semanticError(locationOf(dd), "invalid range '"
                    + from + ".." + to + "'");
        }

        v = new Value(new Range(from, to));

        _retTree = _t;
        return v;
    }

    public final Value selectionExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST selectionExpression_AST_in = (_t == ASTNULL) ? null
                : (AST) _t;
        AST id = null;

        Value f;

        final AST __t12 = _t;
        final AST tmp24_AST_in = (AST) _t;
        match(_t, DOT);
        _t = _t.getFirstChild();
        f = expr(_t);
        _t = _retTree;
        id = (AST) _t;
        match(_t, IDENT);
        _t = _t.getNextSibling();
        _t = __t12;
        _t = _t.getNextSibling();

        final String name = id.getText();

        String fid = "";
        // if there is any information regarding the identity of f, capture it
        // to use
        // as extra information in error messages
        if (f.isLValue()) {
            fid = "'" + f.getLValue().getSymbol().getName() + "' ";
        }

        if (f.isNamespaceComponent()) {
            // handle 'selection' of partially or fully qualified name
            // if still partial, keep as a potential namespace component that
            // can continue
            // to have selection applied, or if fully qualified type name,
            // retrieve the type

            // see if we have a complete qually-qualified name yet
            final String component = f.getNamespaceComponentString();

            final NamespaceScope nsScope = scope.getGlobalScope()
                    .getNamespaceScope(component);
            if ((nsScope != null) && nsScope.contains(name)) { // yes, we found
                                                               // something
                final Symbol s = new Symbol(nsScope, name, null);
                final LValue lv = new LValue(s);
                v = new Value(lv);
            } else { // nope, keep as potential qualified name component
                final String qualifiedName = component + "." + name;

                // !!! temporary way to access external Java type, if there is a
                // type matching
                // the full name loaded, use it
                final java.lang.reflect.Type javaType = NamespaceScope
                        .loadedLibrariesGetType(qualifiedName);
                final TypeSpec gt = (javaType != null) ? new TypeSpec(javaType)
                        : null;

                if (gt != null) { // yes, found a type
                    v = new Value(gt);
                } else {
                    v = new Value(qualifiedName);
                    v.setValueIsNamespaceComponent(locationOf(id));
                }
            }
        } else {
            TypeSpec t = TypeSpec.typeOf(f.getValue());
            if (t.isAny()) {
                f = new Value(((Any) f.getValue()).value); // unwrap Any
                t = TypeSpec.typeOf(f.getValue());
            }

            if (t.isClass() || t.isBuiltinClass()) {
                // try to select a member
                // Debug.WL("selecting member "+name+" from type "+f.getType());
                // //!!!
                final Scope classScope = new ClassScope(t); // create scope for
                                                            // existing class
                final Symbol s = new Symbol(classScope, name, f.getValue());

                if (!s.exists()) {
                    ScigolTreeParser.semanticError(id, "object " + fid
                            + "of type '" + t + "' has no member named '"
                            + name + "'");
                }

                v = new Value(new LValue(s));
            } else if (t.isType()) {
                // try to select static field of class type

                final TypeSpec typeExpr = (TypeSpec) f.getValue();
                if (!typeExpr.isClass()) {
                    ScigolTreeParser.semanticError(id,
                            "cannot select a member of object " + fid
                                    + "of non-class type '" + t + "'");
                }

                final Scope classScope = new ClassScope(typeExpr);
                final Symbol s = new Symbol(classScope, name, null);

                if (!s.exists()) {
                    ScigolTreeParser.semanticError(id, "object " + fid
                            + "of type '" + typeExpr
                            + "' has no member named '" + name + "'");
                }

                v = new Value(new LValue(s));
            } else {
                ScigolTreeParser
                        .semanticError(id, "cannot select member '" + name
                                + "' from object " + fid + "of type '" + t
                                + "'");
            }
        }

        _retTree = _t;
        return v;
    }

    public final Value stringLit(AST _t) throws RecognitionException {
        Value v = null;

        final AST stringLit_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST s = null;

        s = (AST) _t;
        match(_t, STRING_LITERAL);
        _t = _t.getNextSibling();

        String str = new String(s.getText());
        if (str.charAt(0) == '\"') {
            str = str.substring(1, str.length() - 1); // remove surrouding
                                                      // double quotes
        } else {
            str = str.substring(2, str.length() - 2); // remove surrouding back
                                                      // quote paris quotes
        }
        v = new Value(str); // String

        _retTree = _t;
        return v;
    }

    public final Value symbol(AST _t) throws RecognitionException {
        Value v = null;

        final AST symbol_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST id = null;
        int esc = 0;

        {
            _loop22: do {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if ((_t.getType() == SCOPE_ESCAPE)) {
                    final AST tmp23_AST_in = (AST) _t;
                    match(_t, SCOPE_ESCAPE);
                    _t = _t.getNextSibling();
                    esc++;
                } else {
                    break _loop22;
                }

            } while (true);
        }
        id = (AST) _t;
        match(_t, IDENT);
        _t = _t.getNextSibling();

        final String ident = id.getText();
        Scope searchScope = scope;
        final int numEscs = esc;
        while (esc-- > 0) {
            if (searchScope.getOuter() != null) {
                searchScope = searchScope.getOuter();
            }
        }
        // Debug.WriteLine("#esc="+ numEscs+" id="+ident);
        // Debug.WriteLine("current scope:\n"+scope);
        // Debug.WriteLine("current scope.outer=\n"+scope.outer);
        // Debug.WriteLine("search scope:\n"+searchScope);
        // get instance if applicable
        final Symbol thiss = new Symbol(scope, "this", null);
        final Object instance = thiss.exists() ? thiss.getValue() : null;

        final Symbol s = new Symbol(searchScope, ident, instance);
        s.setDefinitionLocation(locationOf(id));

        // check is this is a symbol that refers to a class instance member and
        // complain if
        // no instance is available (can only do this sucessfully here for
        // non-ambiguous symbols)
        if ((instance == null) && s.exists() && (!s.isAmbiguous())) {
            final Entry entry = s.getEntry();
            if (entry.isClassMember() && !entry.isStatic()) {
                final TypeSpec declaringClass = ((ClassScope) entry.scope)
                        .getClassType();
                ScigolTreeParser
                        .semanticError(
                                id,
                                "the instance member named '"
                                        + ident
                                        + "' of class '"
                                        + declaringClass
                                        + "' cannot be accessed without an instance object");
            }
        }

        // if (s.exists()) {
        // Debug.WriteLine("got symbol "+ident);
        // Debug.WriteLine(" in scope:"+s.scope);
        // Debug.WriteLine("  having type "+s.type);
        // }
        if (!s.exists()) { // still haven't found anything

            if (numEscs == 0) {

                // perhaps this is a global fully-qualified name (i.e. with
                // namespace)
                // then create a special 'Value' with the component which will
                // be used
                // via selection '.' to build the full name and then the top
                // level
                // namespace can be checked
                v = new Value(ident);
                v.setValueIsNamespaceComponent(locationOf(id));
            } else {
                ScigolTreeParser.semanticError(id, "undefined identifier '"
                        + ident + "' in the specified scope:\n" + searchScope);
            }
        } else {
            v = new Value(new LValue(s));
        }

        _retTree = _t;
        return v;
    }

    public final Value throwExpression(AST _t) throws RecognitionException {
        final Value v = null;

        final AST throwExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST th = null;
        Value e = null;

        final AST __t110 = _t;
        th = _t == ASTNULL ? null : (AST) _t;
        match(_t, LITERAL_throw);
        _t = _t.getFirstChild();
        e = expr(_t);
        _t = _retTree;
        _t = __t110;
        _t = _t.getNextSibling();

        Object o = e.getValue();
        if (o instanceof Any) {
            o = ((Any) o).value;
        }
        if (!(o instanceof Exception)) {
            ScigolTreeParser.semanticError(th,
                    "only exceptions can be thrown (not objects of type '"
                            + TypeSpec.typeOf(e) + "')");
        }

        // throw it
        Debug.Unimplemented();
        // throw ((Exception)o);

        _retTree = _t;
        return v;
    }

    public final TypeSpec typeExpression(AST _t) throws RecognitionException {
        TypeSpec t = null;

        final AST typeExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value v = null;

        v = expr(_t);
        _t = _retTree;

        if (v.getValue() == null) {
            ScigolTreeParser
                    .semanticError("found null where type expression was required");
        }
        final TypeSpec vtype = TypeSpec.typeOf(v);
        if (!vtype.isType()) {
            // as a special convenience, if we have a func(->type) value, call
            // it (so the user can omit the ()'s to call the 'type generator
            // function')
            if (vtype.isFunc() && vtype.getFuncInfo().getReturnType().isType()
                    && (vtype.getFuncInfo().numRequiredArgs() == 0)) {
                final Func f = (Func) v.getValue();
                final FuncInfo callSig = new FuncInfo(new TypeSpec[0],
                        new TypeSpec(TypeSpec.typeType)); // func(->type)
                callSig.setDefinitionLocation(new Location());

                final Object[] convertedArgs = f.getInfo().convertParameters(
                        callSig, new Object[0], f.isExternal());

                // retrieve instance
                Object instance = null;
                if (v.isLValue()) {
                    instance = v.getLValue().getSymbol().getInstance();
                }

                final Object ret = f.call(null, convertedArgs); // call it

                Debug.Assert(TypeSpec.typeOf(ret).isType(),
                        "expected type return");

                t = (TypeSpec) ret;
            } else {
                ScigolTreeParser.semanticError("an expression of type '"
                        + TypeSpec.typeOf(v)
                        + "' was found where type expression was required");
            }
        } else {
            t = (TypeSpec) v.getValue();
        }

        _retTree = _t;
        return t;
    }

    public final TypeSpec typeLiteral(AST _t) throws RecognitionException {
        TypeSpec t = null;

        final AST typeLiteral_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST b = null;
        FuncInfo fi = null;

        if (_t == null) {
            _t = ASTNULL;
        }
        switch (_t.getType()) {
            case BUILTIN_TYPE: {
                b = (AST) _t;
                match(_t, BUILTIN_TYPE);
                _t = _t.getNextSibling();

                t = new TypeSpec(b.getText());
                if (t == null) {
                    ScigolTreeParser.semanticError(b,
                            "undefined type " + b.getText());
                }

                break;
            }
            case FUNC: {
                fi = funcType(_t);
                _t = _retTree;
                t = new TypeSpec(fi);
                break;
            }
            case LITERAL_class: {
                t = classType(_t);
                _t = _retTree;
                break;
            }
            case LITERAL_interface: {
                t = interfaceType(_t);
                _t = _retTree;
                break;
            }
            default: {
                throw new NoViableAltException(_t);
            }
        }
        _retTree = _t;
        return t;
    }

    public final Value typeofExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST typeofExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        Value e = null;

        final AST __t26 = _t;
        final AST tmp22_AST_in = (AST) _t;
        match(_t, LITERAL_typeof);
        _t = _t.getFirstChild();
        e = expr(_t);
        _t = _retTree;
        _t = __t26;
        _t = _t.getNextSibling();

        if (e == null) {
            v = new Value(TypeSpec.anyTypeSpec);
        } else {
            if (!TypeSpec.typeOf(e).isType()) {
                // as a special case, if the value is null & it's an LValue, use
                // the symbol's type
                final boolean isNull = (e.getValue() == null)
                        || ((e.getValue() instanceof Any) && (((Any) e
                                .getValue()).value == null));
                if (e.isLValue() && isNull) {
                    v = new Value(e.getLValue().getSymbol().getType());
                } else {
                    if (e.getValue() instanceof Any) {
                        e = new Value(((Any) e.getValue()).value);
                    }
                }

                if (v == null) {
                    v = new Value(TypeSpec.typeOf(e)); // deduce type from value
                }
            } else {
                v = new Value(TypeSpec.typeTypeSpec);
            }
        }

        _retTree = _t;
        return v;
    }

    public final void usingDecl(AST _t) throws RecognitionException {

        final AST usingDecl_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST ul = null;
        AST aliasId = null;
        String id = null;
        Value source = null;

        final AST __t43 = _t;
        ul = _t == ASTNULL ? null : (AST) _t;
        match(_t, LITERAL_using);
        _t = _t.getFirstChild();
        id = namespaceName(_t);
        _t = _retTree;
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case LITERAL_as: {
                    final AST tmp36_AST_in = (AST) _t;
                    match(_t, LITERAL_as);
                    _t = _t.getNextSibling();
                    aliasId = (AST) _t;
                    match(_t, IDENT);
                    _t = _t.getNextSibling();
                    break;
                }
                case 3:
                case LITERAL_from: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case LITERAL_from: {
                    final AST tmp37_AST_in = (AST) _t;
                    match(_t, LITERAL_from);
                    _t = _t.getNextSibling();
                    source = stringLit(_t);
                    _t = _retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        _t = __t43;
        _t = _t.getNextSibling();

        if (source != null) {
            Debug.WriteLine("using ... from is not implemented!");
        }
        Debug.Assert(scope.isNamespaceScope(),
                "using can only appear in namespace scope!");

        final NamespaceScope nsscope = (NamespaceScope) scope;
        final String alias = (aliasId != null) ? aliasId.getText() : null;
        String aliased = null;
        String nsName = id;

        // if nsName isn't the name of a namespace, perhaps we have
        // 'namespace.name' instead
        if (nsscope.getNamespaceScope(nsName) == null) {

            // remove the last . component to separate the namespace (if any)
            // from the name
            final int lastDot = id.lastIndexOf('.');
            if (lastDot != -1) {
                nsName = id.substring(0, lastDot);
                aliased = id.substring(lastDot + 1);
                // Debug.WL("nsName="+nsName);
                // Debug.WL("aliased="+aliased);
            }
        }

        if (alias == null) { // using namespace[.name]
            if (aliased == null) {
                nsscope.addUsingNamespace(nsName);
                // Debug.WriteLine("using namespace="+nsName);
            } else {
                nsscope.addUsingName(aliased, nsName);
                // Debug.WriteLine("using namespace="+nsName+" name="+aliased);
            }
        } else { // using alias
            if (aliased == null) {
                aliased = nsName;
                nsName = nsscope.fullNamespaceName();
            }

            nsscope.addUsingAlias(alias, aliased, nsName);
            // Debug.WriteLine("using alias="+alias+" aliased="+aliased+" namespace="+
            // nsName);
        }

        _retTree = _t;
    }

    public final Value whileExpression(AST _t) throws RecognitionException {
        Value v = null;

        final AST whileExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST w = null;
        AST d = null;
        Value test = null;

        if (_t == null) {
            _t = ASTNULL;
        }
        switch (_t.getType()) {
            case LITERAL_while: {
                final AST __t99 = _t;
                w = _t == ASTNULL ? null : (AST) _t;
                match(_t, LITERAL_while);
                _t = _t.getFirstChild();
                test = expr(_t);
                _t = _retTree;
                _t = __t99;
                _t = _t.getNextSibling();

                final AST whileAST = whileExpression_AST_in;
                final AST testAST = whileAST.getFirstChild();
                final AST bodyAST = whileAST.getFirstChild().getNextSibling(); // skip
                                                                               // over
                                                                               // test

                TypeSpec t = TypeSpec.typeOf(test);
                if (!TypeManager.existsImplicitConversion(t,
                        TypeSpec.boolTypeSpec, test)) {
                    ScigolTreeParser.semanticError(w,
                            "'while' test expression must be of type 'bool'");
                }
                boolean btest = ((Boolean) TypeManager
                        .performImplicitConversion(t, TypeSpec.boolTypeSpec,
                                test).getValue()).booleanValue();

                // keep executing the body tree while the test is true
                while (btest) {
                    v = expr(bodyAST);

                    // re-evaluate test
                    test = expr(testAST); // execute test
                    t = TypeSpec.typeOf(test);
                    if (!TypeManager.existsImplicitConversion(t,
                            TypeSpec.boolTypeSpec, test)) {
                        ScigolTreeParser
                                .semanticError(w,
                                        "'while' test expression must be of type 'bool'");
                    }
                    btest = ((Boolean) TypeManager.performImplicitConversion(t,
                            TypeSpec.boolTypeSpec, test).getValue())
                            .booleanValue();
                }

                break;
            }
            case LITERAL_do: {
                final AST __t100 = _t;
                d = _t == ASTNULL ? null : (AST) _t;
                match(_t, LITERAL_do);
                _t = _t.getFirstChild();
                v = expr(_t);
                _t = _retTree;
                test = expr(_t);
                _t = _retTree;
                _t = __t100;
                _t = _t.getNextSibling();

                final AST whileAST = whileExpression_AST_in;
                final AST bodyAST = whileAST.getFirstChild();
                final AST testAST = whileAST.getFirstChild().getNextSibling(); // skip
                                                                               // over
                                                                               // body

                TypeSpec t = TypeSpec.typeOf(test);
                if (!TypeManager.existsImplicitConversion(t,
                        TypeSpec.boolTypeSpec, test)) {
                    ScigolTreeParser
                            .semanticError(d,
                                    "'do...while' test expression must be of type 'bool'");
                }
                boolean btest = ((Boolean) TypeManager
                        .performImplicitConversion(t, TypeSpec.boolTypeSpec,
                                test).getValue()).booleanValue();

                // body & test have already been executed once automatically,
                // now
                // re-execute them again so long as test is true
                if (btest) {
                    do {
                        v = expr(bodyAST);

                        // re-evaluate test
                        test = expr(testAST); // execute test
                        t = TypeSpec.typeOf(test);
                        if (!TypeManager.existsImplicitConversion(t,
                                TypeSpec.boolTypeSpec, test)) {
                            ScigolTreeParser
                                    .semanticError(d,
                                            "'do...while' test expression must be of type 'bool'");
                        }
                        btest = ((Boolean) TypeManager
                                .performImplicitConversion(t,
                                        TypeSpec.boolTypeSpec, test).getValue())
                                .booleanValue();

                    } while (btest);
                }

                break;
            }
            default: {
                throw new NoViableAltException(_t);
            }
        }
        _retTree = _t;
        return v;
    }
}
