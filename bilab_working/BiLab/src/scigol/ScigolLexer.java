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

// $ANTLR 2.7.3 (20040901-1): "Scigol.g" -> "ScigolLexer.java"$

package scigol;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import antlr.ANTLRHashString;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;

public class ScigolLexer extends antlr.CharScanner implements ScigolTokenTypes,
        TokenStream {

    public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

    public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());

    public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());

    public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());

    public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());

    private static final long[] mk_tokenSet_0() {
        final long[] data = new long[1025];
        data[0] = 68719476736L;
        data[1] = 576460745995190271L;
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        final long[] data = new long[2048];
        data[0] = -9224L;
        for (int i = 1; i <= 1023; i++) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        final long[] data = new long[2048];
        data[0] = -4398046520328L;
        for (int i = 1; i <= 1023; i++) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        final long[] data = new long[2048];
        data[0] = -17179869192L;
        for (int i = 1; i <= 1023; i++) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        final long[] data = new long[1025];
        data[0] = 287948918354870272L;
        data[1] = 9007740420620414L;
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        final long[] data = new long[2048];
        data[0] = -17179869192L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1023; i++) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_6() {
        final long[] data = new long[2048];
        data[0] = -8L;
        data[1] = -4294967297L;
        for (int i = 2; i <= 1023; i++) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_7() {
        final long[] data = new long[1025];
        data[0] = 287948901175001088L;
        data[1] = 541165879422L;
        return data;
    }

    public ScigolLexer(final InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public ScigolLexer(final InputStream in) {
        this(new ByteBuffer(in));
    }

    public ScigolLexer(final LexerSharedInputState state) {
        super(state);
        caseSensitiveLiterals = true;
        setCaseSensitive(true);
        literals = new Hashtable();
        literals.put(new ANTLRHashString("map", this), new Integer(98));
        literals.put(new ANTLRHashString("interface", this), new Integer(86));
        literals.put(new ANTLRHashString("for", this), new Integer(116));
        literals.put(new ANTLRHashString("class", this), new Integer(85));
        literals.put(new ANTLRHashString("isnt", this), new Integer(49));
        literals.put(new ANTLRHashString("list", this), new Integer(97));
        literals.put(new ANTLRHashString("final", this), new Integer(84));
        literals.put(new ANTLRHashString("false", this), new Integer(18));
        literals.put(new ANTLRHashString("true", this), new Integer(17));
        literals.put(new ANTLRHashString("try", this), new Integer(69));
        literals.put(new ANTLRHashString("let", this), new Integer(81));
        literals.put(new ANTLRHashString("and", this), new Integer(45));
        literals.put(new ANTLRHashString("sreal", this), new Integer(105));
        literals.put(new ANTLRHashString("typeof", this), new Integer(36));
        literals.put(new ANTLRHashString("private", this), new Integer(91));
        literals.put(new ANTLRHashString("logger", this), new Integer(42));
        literals.put(new ANTLRHashString("string", this), new Integer(106));
        literals.put(new ANTLRHashString("pre", this), new Integer(34));
        literals.put(new ANTLRHashString("override", this), new Integer(88));
        literals.put(new ANTLRHashString("throw", this), new Integer(120));
        literals.put(new ANTLRHashString("implicit", this), new Integer(89));
        literals.put(new ANTLRHashString("func", this), new Integer(93));
        literals.put(new ANTLRHashString("debug", this), new Integer(41));
        literals.put(new ANTLRHashString("range", this), new Integer(96));
        literals.put(new ANTLRHashString("post", this), new Integer(35));
        literals.put(new ANTLRHashString("do", this), new Integer(114));
        literals.put(new ANTLRHashString("type", this), new Integer(107));
        literals.put(new ANTLRHashString("in", this), new Integer(118));
        literals.put(new ANTLRHashString("null", this), new Integer(19));
        literals.put(new ANTLRHashString("bool", this), new Integer(99));
        literals.put(new ANTLRHashString("namespace", this), new Integer(27));
        literals.put(new ANTLRHashString("static", this), new Integer(83));
        literals.put(new ANTLRHashString("char", this), new Integer(101));
        literals.put(new ANTLRHashString("while", this), new Integer(115));
        literals.put(new ANTLRHashString("const", this), new Integer(82));
        literals.put(new ANTLRHashString("or", this), new Integer(44));
        literals.put(new ANTLRHashString("real", this), new Integer(104));
        literals.put(new ANTLRHashString("from", this), new Integer(32));
        literals.put(new ANTLRHashString("is", this), new Integer(48));
        literals.put(new ANTLRHashString("matrix", this), new Integer(95));
        literals.put(new ANTLRHashString("protected", this), new Integer(92));
        literals.put(new ANTLRHashString("dint", this), new Integer(103));
        literals.put(new ANTLRHashString("vector", this), new Integer(94));
        literals.put(new ANTLRHashString("byte", this), new Integer(100));
        literals.put(new ANTLRHashString("property", this), new Integer(87));
        literals.put(new ANTLRHashString("assert", this), new Integer(39));
        literals.put(new ANTLRHashString("foreach", this), new Integer(117));
        literals.put(new ANTLRHashString("if", this), new Integer(111));
        literals.put(new ANTLRHashString("any", this), new Integer(109));
        literals.put(new ANTLRHashString("int", this), new Integer(102));
        literals.put(new ANTLRHashString("using", this), new Integer(30));
        literals.put(new ANTLRHashString("num", this), new Integer(108));
        literals.put(new ANTLRHashString("object", this), new Integer(110));
        literals.put(new ANTLRHashString("public", this), new Integer(90));
        literals.put(new ANTLRHashString("else", this), new Integer(113));
        literals.put(new ANTLRHashString("catch", this), new Integer(70));
        literals.put(new ANTLRHashString("not", this), new Integer(63));
        literals.put(new ANTLRHashString("by", this), new Integer(119));
        literals.put(new ANTLRHashString("then", this), new Integer(112));
        literals.put(new ANTLRHashString("as", this), new Integer(31));
    }

    public ScigolLexer(final Reader in) {
        this(new CharBuffer(in));
    }

    public CombinedSharedInputState istate() {
        return ((LexerSharedInputStateWrapper) getInputState()).state;
    }

    @Override
    protected Token makeToken(final int t) {
        final CommonTokenWithLocation tok = new CommonTokenWithLocation();
        tok.setType(t);
        // !!! fix this somehow (if input state is LexerSharedInputStateWrapper
        // then add public getters to it)!!!
        // tok.setColumn(getInputState().tokenStartColumn);
        // tok.setLine(getInputState().tokenStartLine);
        // tok.setFilename(getInputState().filename);

        return tok;
    }

    public final void mANNOT_START(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = ANNOT_START;
        final int _saveIndex;

        match("@[");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mASSIGN(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = ASSIGN;
        final int _saveIndex;

        match('=');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mBAND(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = BAND;
        final int _saveIndex;

        match('&');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mBAR(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = BAR;
        final int _saveIndex;

        match('|');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mCHAR_LIT_SUFFIX(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = CHAR_LIT_SUFFIX;
        final int _saveIndex;

        match('c');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mCHAR_LITERAL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = CHAR_LITERAL;
        final int _saveIndex;

        {
            match('"');
            {
                if ((LA(1) == '\\')) {
                    mESC(false);
                } else if ((_tokenSet_5.member(LA(1)))) {
                    {
                        match(_tokenSet_5);
                    }
                } else {
                    throw new NoViableAltForCharException(LA(1), getFilename(),
                            getLine(), getColumn());
                }

            }
            match('"');
        }
        mCHAR_LIT_SUFFIX(false);
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mCOLON(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = COLON;
        final int _saveIndex;

        match(':');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mCOMMA(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = COMMA;
        final int _saveIndex;

        match(',');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mDEC(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = DEC;
        final int _saveIndex;

        match("--");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mDIV(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = DIV;
        final int _saveIndex;

        match('/');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mDOT_FLOAT_EXP(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = DOT_FLOAT_EXP;
        final int _saveIndex;

        switch (LA(1)) {
            case '.': {
                match('.');
                {
                    switch (LA(1)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': {
                            {
                                int _cnt382 = 0;
                                _loop382: do {
                                    if (((LA(1) >= '0' && LA(1) <= '9'))) {
                                        matchRange('0', '9');
                                    } else {
                                        if (_cnt382 >= 1) {
                                            break _loop382;
                                        } else {
                                            throw new NoViableAltForCharException(
                                                    (char) LA(1),
                                                    getFilename(), getLine(),
                                                    getColumn());
                                        }
                                    }

                                    _cnt382++;
                                } while (true);
                            }
                            {
                                if ((LA(1) == 'E' || LA(1) == 'e')) {
                                    mEXPONENT(false);
                                } else {
                                }

                            }
                            {
                                if ((LA(1) == 'R' || LA(1) == 'S'
                                        || LA(1) == 'r' || LA(1) == 's')) {
                                    mREAL_SUFFIX(false);
                                } else {
                                }

                            }
                            break;
                        }
                        case 'E':
                        case 'R':
                        case 'S':
                        case 'e':
                        case 'r':
                        case 's': {
                            {
                                switch (LA(1)) {
                                    case 'E':
                                    case 'e': {
                                        mEXPONENT(false);
                                        break;
                                    }
                                    case 'R':
                                    case 'S':
                                    case 'r':
                                    case 's': {
                                        mREAL_SUFFIX(false);
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltForCharException(
                                                (char) LA(1), getFilename(),
                                                getLine(), getColumn());
                                    }
                                }
                            }
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char) LA(1),
                                    getFilename(), getLine(), getColumn());
                        }
                    }
                }
                break;
            }
            case 'E':
            case 'e': {
                mEXPONENT(false);
                {
                    if ((LA(1) == 'R' || LA(1) == 'S' || LA(1) == 'r' || LA(1) == 's')) {
                        mREAL_SUFFIX(false);
                    } else {
                    }

                }
                break;
            }
            case 'R':
            case 'S':
            case 'r':
            case 's': {
                mREAL_SUFFIX(false);
                break;
            }
            default: {
                throw new NoViableAltForCharException((char) LA(1),
                        getFilename(), getLine(), getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mEQUAL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = EQUAL;
        final int _saveIndex;

        match("==");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mESC(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = ESC;
        int _saveIndex;

        _saveIndex = text.length();
        match('\\');
        text.setLength(_saveIndex);
        {
            switch (LA(1)) {
                case 'n': {
                    _saveIndex = text.length();
                    match('n');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\n');
                    }
                    break;
                }
                case 'r': {
                    _saveIndex = text.length();
                    match('r');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\r');
                    }
                    break;
                }
                case 't': {
                    _saveIndex = text.length();
                    match('t');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\t');
                    }
                    break;
                }
                case 'b': {
                    _saveIndex = text.length();
                    match('b');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\b');
                    }
                    break;
                }
                case 'f': {
                    _saveIndex = text.length();
                    match('f');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\f');
                    }
                    break;
                }
                case '"': {
                    _saveIndex = text.length();
                    match('"');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\"');
                    }
                    break;
                }
                case '\'': {
                    _saveIndex = text.length();
                    match('\'');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\'');
                    }
                    break;
                }
                case '\\': {
                    _saveIndex = text.length();
                    match('\\');
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        text.append('\\');
                    }
                    break;
                }
                case 'u': {
                    {
                        int _cnt332 = 0;
                        _loop332: do {
                            if ((LA(1) == 'u')) {
                                _saveIndex = text.length();
                                match('u');
                                text.setLength(_saveIndex);
                            } else {
                                if (_cnt332 >= 1) {
                                    break _loop332;
                                } else {
                                    throw new NoViableAltForCharException(
                                            (char) LA(1), getFilename(),
                                            getLine(), getColumn());
                                }
                            }

                            _cnt332++;
                        } while (true);
                    }
                    _saveIndex = text.length();
                    mHEX_DIGIT(false);
                    text.setLength(_saveIndex);
                    _saveIndex = text.length();
                    mHEX_DIGIT(false);
                    text.setLength(_saveIndex);
                    _saveIndex = text.length();
                    mHEX_DIGIT(false);
                    text.setLength(_saveIndex);
                    _saveIndex = text.length();
                    mHEX_DIGIT(false);
                    text.setLength(_saveIndex);
                    if (inputState.guessing == 0) {
                        Debug.WriteLine("unicode chars not implemented");
                    }
                    break;
                }
                case '0':
                case '1':
                case '2':
                case '3': {
                    _saveIndex = text.length();
                    matchRange('0', '3');
                    text.setLength(_saveIndex);
                    {
                        if (((LA(1) >= '0' && LA(1) <= '7'))
                                && ((LA(2) >= '\u0003' && LA(2) <= '\uffff'))
                                && (true) && (true) && (true)) {
                            _saveIndex = text.length();
                            matchRange('0', '7');
                            text.setLength(_saveIndex);
                            {
                                if (((LA(1) >= '0' && LA(1) <= '7'))
                                        && ((LA(2) >= '\u0003' && LA(2) <= '\uffff'))
                                        && (true) && (true) && (true)) {
                                    _saveIndex = text.length();
                                    matchRange('0', '7');
                                    text.setLength(_saveIndex);
                                } else if (((LA(1) >= '\u0003' && LA(1) <= '\uffff'))
                                        && (true) && (true) && (true) && (true)) {
                                } else {
                                    throw new NoViableAltForCharException(
                                            (char) LA(1), getFilename(),
                                            getLine(), getColumn());
                                }

                            }
                        } else if (((LA(1) >= '\u0003' && LA(1) <= '\uffff'))
                                && (true) && (true) && (true) && (true)) {
                        } else {
                            throw new NoViableAltForCharException((char) LA(1),
                                    getFilename(), getLine(), getColumn());
                        }

                    }
                    if (inputState.guessing == 0) {
                        Debug.WriteLine("octal(?) chars not implemented");
                    }
                    break;
                }
                case '4':
                case '5':
                case '6':
                case '7': {
                    _saveIndex = text.length();
                    matchRange('4', '7');
                    text.setLength(_saveIndex);
                    {
                        if (((LA(1) >= '0' && LA(1) <= '7'))
                                && ((LA(2) >= '\u0003' && LA(2) <= '\uffff'))
                                && (true) && (true) && (true)) {
                            _saveIndex = text.length();
                            matchRange('0', '7');
                            text.setLength(_saveIndex);
                        } else if (((LA(1) >= '\u0003' && LA(1) <= '\uffff'))
                                && (true) && (true) && (true) && (true)) {
                        } else {
                            throw new NoViableAltForCharException((char) LA(1),
                                    getFilename(), getLine(), getColumn());
                        }

                    }
                    if (inputState.guessing == 0) {
                        Debug.WriteLine("(?) chars not implemented");
                    }
                    break;
                }
                default: {
                    throw new NoViableAltForCharException((char) LA(1),
                            getFilename(), getLine(), getColumn());
                }
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mEXPONENT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = EXPONENT;
        final int _saveIndex;

        {
            switch (LA(1)) {
                case 'e': {
                    match('e');
                    break;
                }
                case 'E': {
                    match('E');
                    break;
                }
                default: {
                    throw new NoViableAltForCharException((char) LA(1),
                            getFilename(), getLine(), getColumn());
                }
            }
        }
        {
            switch (LA(1)) {
                case '+': {
                    match('+');
                    break;
                }
                case '-': {
                    match('-');
                    break;
                }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException((char) LA(1),
                            getFilename(), getLine(), getColumn());
                }
            }
        }
        {
            int _cnt391 = 0;
            _loop391: do {
                if (((LA(1) >= '0' && LA(1) <= '9'))) {
                    matchRange('0', '9');
                } else {
                    if (_cnt391 >= 1) {
                        break _loop391;
                    } else {
                        throw new NoViableAltForCharException((char) LA(1),
                                getFilename(), getLine(), getColumn());
                    }
                }

                _cnt391++;
            } while (true);
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mFROM(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = FROM;
        final int _saveIndex;

        match("<-");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mGIVES(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = GIVES;
        final int _saveIndex;

        match("->");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mGTE(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = GTE;
        final int _saveIndex;

        match(">=");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mGTHAN(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = GTHAN;
        final int _saveIndex;

        match(">");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mHASH(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = HASH;
        final int _saveIndex;

        match('#');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mHAT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = HAT;
        final int _saveIndex;

        match('^');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mHEX_DIGIT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = HEX_DIGIT;
        final int _saveIndex;

        {
            switch (LA(1)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    matchRange('0', '9');
                    break;
                }
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F': {
                    matchRange('A', 'F');
                    break;
                }
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f': {
                    matchRange('a', 'f');
                    break;
                }
                default: {
                    throw new NoViableAltForCharException((char) LA(1),
                            getFilename(), getLine(), getColumn());
                }
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mIDENT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = IDENT;
        final int _saveIndex;

        {
            if ((LA(1) == 'o') && (LA(2) == 'p') && (LA(3) == 'e')
                    && (LA(4) == 'r') && (LA(5) == 'a')) {
                {
                    match("opera");
                    {
                        if ((LA(1) == 't')
                                && (LA(2) == 'o')
                                && (LA(3) == 'r')
                                && (LA(4) == '!' || LA(4) == '#'
                                        || LA(4) == '%' || LA(4) == '\''
                                        || LA(4) == '(' || LA(4) == '*'
                                        || LA(4) == '+' || LA(4) == '-'
                                        || LA(4) == '/' || LA(4) == '<'
                                        || LA(4) == '=' || LA(4) == '>'
                                        || LA(4) == '^' || LA(4) == '|')) {
                            {
                                match("tor");
                                {
                                    switch (LA(1)) {
                                        case '*': {
                                            match('*');
                                            break;
                                        }
                                        case '/': {
                                            match('/');
                                            break;
                                        }
                                        case '%': {
                                            match('%');
                                            break;
                                        }
                                        case '=': {
                                            match("==");
                                            break;
                                        }
                                        case '!': {
                                            match("!=");
                                            break;
                                        }
                                        case '|': {
                                            match("||");
                                            break;
                                        }
                                        case '#': {
                                            match('#');
                                            break;
                                        }
                                        case '^': {
                                            match('^');
                                            break;
                                        }
                                        case '\'': {
                                            match('\'');
                                            break;
                                        }
                                        case '(': {
                                            match("()");
                                            break;
                                        }
                                        default:
                                            if ((LA(1) == '+')
                                                    && (LA(2) == '+')) {
                                                match("++");
                                            } else if ((LA(1) == '-')
                                                    && (LA(2) == '-')) {
                                                match("--");
                                            } else if ((LA(1) == '<')
                                                    && (LA(2) == '=')) {
                                                match("<=");
                                            } else if ((LA(1) == '>')
                                                    && (LA(2) == '=')) {
                                                match(">=");
                                            } else if ((LA(1) == '-')
                                                    && (LA(2) == '>')) {
                                                match("->");
                                            } else if ((LA(1) == '+') && (true)) {
                                                match('+');
                                            } else if ((LA(1) == '-') && (true)) {
                                                match('-');
                                            } else if ((LA(1) == '>') && (true)) {
                                                match('>');
                                            } else if ((LA(1) == '<') && (true)) {
                                                match('<');
                                            } else {
                                                throw new NoViableAltForCharException(
                                                        (char) LA(1),
                                                        getFilename(),
                                                        getLine(), getColumn());
                                            }
                                    }
                                }
                            }
                        } else {
                            {
                                _loop346: do {
                                    switch (LA(1)) {
                                        case 'a':
                                        case 'b':
                                        case 'c':
                                        case 'd':
                                        case 'e':
                                        case 'f':
                                        case 'g':
                                        case 'h':
                                        case 'i':
                                        case 'j':
                                        case 'k':
                                        case 'l':
                                        case 'm':
                                        case 'n':
                                        case 'o':
                                        case 'p':
                                        case 'q':
                                        case 'r':
                                        case 's':
                                        case 't':
                                        case 'u':
                                        case 'v':
                                        case 'w':
                                        case 'x':
                                        case 'y':
                                        case 'z': {
                                            matchRange('a', 'z');
                                            break;
                                        }
                                        case 'A':
                                        case 'B':
                                        case 'C':
                                        case 'D':
                                        case 'E':
                                        case 'F':
                                        case 'G':
                                        case 'H':
                                        case 'I':
                                        case 'J':
                                        case 'K':
                                        case 'L':
                                        case 'M':
                                        case 'N':
                                        case 'O':
                                        case 'P':
                                        case 'Q':
                                        case 'R':
                                        case 'S':
                                        case 'T':
                                        case 'U':
                                        case 'V':
                                        case 'W':
                                        case 'X':
                                        case 'Y':
                                        case 'Z': {
                                            matchRange('A', 'Z');
                                            break;
                                        }
                                        case '_': {
                                            match('_');
                                            break;
                                        }
                                        case '0':
                                        case '1':
                                        case '2':
                                        case '3':
                                        case '4':
                                        case '5':
                                        case '6':
                                        case '7':
                                        case '8':
                                        case '9': {
                                            matchRange('0', '9');
                                            break;
                                        }
                                        case '$': {
                                            match('$');
                                            break;
                                        }
                                        default: {
                                            break _loop346;
                                        }
                                    }
                                } while (true);
                            }
                        }

                    }
                }
            } else if ((_tokenSet_0.member(LA(1))) && (true) && (true)
                    && (true) && (true)) {
                {
                    {
                        switch (LA(1)) {
                            case '@': {
                                match('@');
                                break;
                            }
                            case '$':
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                            case 'G':
                            case 'H':
                            case 'I':
                            case 'J':
                            case 'K':
                            case 'L':
                            case 'M':
                            case 'N':
                            case 'O':
                            case 'P':
                            case 'Q':
                            case 'R':
                            case 'S':
                            case 'T':
                            case 'U':
                            case 'V':
                            case 'W':
                            case 'X':
                            case 'Y':
                            case 'Z':
                            case '_':
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                            case 'g':
                            case 'h':
                            case 'i':
                            case 'j':
                            case 'k':
                            case 'l':
                            case 'm':
                            case 'n':
                            case 'o':
                            case 'p':
                            case 'q':
                            case 'r':
                            case 's':
                            case 't':
                            case 'u':
                            case 'v':
                            case 'w':
                            case 'x':
                            case 'y':
                            case 'z': {
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(
                                        (char) LA(1), getFilename(), getLine(),
                                        getColumn());
                            }
                        }
                    }
                    {
                        switch (LA(1)) {
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                            case 'g':
                            case 'h':
                            case 'i':
                            case 'j':
                            case 'k':
                            case 'l':
                            case 'm':
                            case 'n':
                            case 'o':
                            case 'p':
                            case 'q':
                            case 'r':
                            case 's':
                            case 't':
                            case 'u':
                            case 'v':
                            case 'w':
                            case 'x':
                            case 'y':
                            case 'z': {
                                matchRange('a', 'z');
                                break;
                            }
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                            case 'G':
                            case 'H':
                            case 'I':
                            case 'J':
                            case 'K':
                            case 'L':
                            case 'M':
                            case 'N':
                            case 'O':
                            case 'P':
                            case 'Q':
                            case 'R':
                            case 'S':
                            case 'T':
                            case 'U':
                            case 'V':
                            case 'W':
                            case 'X':
                            case 'Y':
                            case 'Z': {
                                matchRange('A', 'Z');
                                break;
                            }
                            case '_': {
                                match('_');
                                break;
                            }
                            case '$': {
                                match('$');
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(
                                        (char) LA(1), getFilename(), getLine(),
                                        getColumn());
                            }
                        }
                    }
                    {
                        _loop351: do {
                            switch (LA(1)) {
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                case 'g':
                                case 'h':
                                case 'i':
                                case 'j':
                                case 'k':
                                case 'l':
                                case 'm':
                                case 'n':
                                case 'o':
                                case 'p':
                                case 'q':
                                case 'r':
                                case 's':
                                case 't':
                                case 'u':
                                case 'v':
                                case 'w':
                                case 'x':
                                case 'y':
                                case 'z': {
                                    matchRange('a', 'z');
                                    break;
                                }
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                case 'G':
                                case 'H':
                                case 'I':
                                case 'J':
                                case 'K':
                                case 'L':
                                case 'M':
                                case 'N':
                                case 'O':
                                case 'P':
                                case 'Q':
                                case 'R':
                                case 'S':
                                case 'T':
                                case 'U':
                                case 'V':
                                case 'W':
                                case 'X':
                                case 'Y':
                                case 'Z': {
                                    matchRange('A', 'Z');
                                    break;
                                }
                                case '_': {
                                    match('_');
                                    break;
                                }
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9': {
                                    matchRange('0', '9');
                                    break;
                                }
                                case '$': {
                                    match('$');
                                    break;
                                }
                                default: {
                                    break _loop351;
                                }
                            }
                        } while (true);
                    }
                }
            } else {
                throw new NoViableAltForCharException((char) LA(1),
                        getFilename(), getLine(), getColumn());
            }

        }
        if (inputState.guessing == 0) {

            // discard '@' perfix (can't use ! suffix because we don't want to
            // testLiterals if '@' present)
            String s = new String(text.getBuffer(), _begin, text.length()
                    - _begin);
            if (s.charAt(0) == '@') {
                s = s.substring(1, s.length() - 1);
                text.setLength(_begin);
                text.append(s);
            } else {
                _ttype = testLiteralsTable(_ttype);
            }

        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mINC(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = INC;
        final int _saveIndex;

        match("++");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mLBRACK(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LBRACK;
        final int _saveIndex;

        match('[');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mLCURLY(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LCURLY;
        final int _saveIndex;

        match('{');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mLINE_BREAK(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LINE_BREAK;
        final int _saveIndex;

        {
            if ((LA(1) == '\r') && (LA(2) == '\n') && (true) && (true)
                    && (true)) {
                match("\r\n");
            } else if ((LA(1) == '\r') && (true) && (true) && (true) && (true)) {
                match('\r');
            } else if ((LA(1) == '\n')) {
                match('\n');
            } else {
                throw new NoViableAltForCharException(LA(1), getFilename(),
                        getLine(), getColumn());
            }

        }
        if (inputState.guessing == 0) {

            newline();
            istate().sawNewline = true;

        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mLISTSTART(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LISTSTART;
        final int _saveIndex;

        match("'(");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mLNOT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LNOT;
        final int _saveIndex;

        match('!');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mLPAREN(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LPAREN;
        final int _saveIndex;

        match('(');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mLTE(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LTE;
        final int _saveIndex;

        match("<=");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mLTHAN(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = LTHAN;
        final int _saveIndex;

        match('<');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mMINUS(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = MINUS;
        final int _saveIndex;

        match('-');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mML_COMMENT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = ML_COMMENT;
        final int _saveIndex;

        match("/*");
        {
            _loop305: do {
                if ((LA(1) == '\r') && (LA(2) == '\n')
                        && ((LA(3) >= '\u0003' && LA(3) <= '\uffff'))
                        && ((LA(4) >= '\u0003' && LA(4) <= '\uffff')) && (true)) {
                    match('\r');
                    match('\n');
                    if (inputState.guessing == 0) {
                        newline();
                    }
                } else if (((LA(1) == '*')
                        && ((LA(2) >= '\u0003' && LA(2) <= '\uffff')) && ((LA(3) >= '\u0003' && LA(3) <= '\uffff')))
                        && (LA(2) != '/')) {
                    match('*');
                } else if ((LA(1) == '\r')
                        && ((LA(2) >= '\u0003' && LA(2) <= '\uffff'))
                        && ((LA(3) >= '\u0003' && LA(3) <= '\uffff')) && (true)
                        && (true)) {
                    match('\r');
                    if (inputState.guessing == 0) {
                        newline();
                    }
                } else if ((LA(1) == '\n')) {
                    match('\n');
                    if (inputState.guessing == 0) {
                        newline();
                    }
                } else if ((_tokenSet_2.member(LA(1)))) {
                    {
                        match(_tokenSet_2);
                    }
                } else {
                    break _loop305;
                }

            } while (true);
        }
        match("*/");
        if (inputState.guessing == 0) {
            _ttype = Token.SKIP;
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mMOD(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = MOD;
        final int _saveIndex;

        match('%');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mMULTI_LINE_STRING_LITERAL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = MULTI_LINE_STRING_LITERAL;
        final int _saveIndex;

        {
            match('`');
            match('`');
            {
                _loop319: do {
                    if ((_tokenSet_6.member(LA(1)))) {
                        mNON_BACKQUOTE_STRING(false);
                        match('`');
                    } else {
                        break _loop319;
                    }

                } while (true);
            }
            match('`');
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mNON_BACKQUOTE_STRING(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = NON_BACKQUOTE_STRING;
        final int _saveIndex;

        {
            int _cnt323 = 0;
            _loop323: do {
                if ((_tokenSet_6.member(LA(1)))) {
                    {
                        match(_tokenSet_6);
                    }
                } else {
                    if (_cnt323 >= 1) {
                        break _loop323;
                    } else {
                        throw new NoViableAltForCharException((char) LA(1),
                                getFilename(), getLine(), getColumn());
                    }
                }

                _cnt323++;
            } while (true);
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mNON_LINE_BREAK_WS(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = NON_LINE_BREAK_WS;
        final int _saveIndex;

        {
            switch (LA(1)) {
                case ' ': {
                    match(' ');
                    break;
                }
                case '\t': {
                    match('\t');
                    break;
                }
                case '\u000c': {
                    match('\f');
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(LA(1), getFilename(),
                            getLine(), getColumn());
                }
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mNOT_EQUAL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = NOT_EQUAL;
        final int _saveIndex;

        match("!=");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mNUM_INT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = NUM_INT;
        final int _saveIndex;
        Token f1 = null;
        Token f2 = null;
        Token f3 = null;
        Token f4 = null;
        boolean isDecimal = false;
        Token t = null;

        switch (LA(1)) {
            case '.': {
                match('.');
                if (inputState.guessing == 0) {
                    _ttype = DOT;
                }
                {
                    switch (LA(1)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': {
                            {
                                {
                                    int _cnt356 = 0;
                                    _loop356: do {
                                        if (((LA(1) >= '0' && LA(1) <= '9'))) {
                                            matchRange('0', '9');
                                        } else {
                                            if (_cnt356 >= 1) {
                                                break _loop356;
                                            } else {
                                                throw new NoViableAltForCharException(
                                                        (char) LA(1),
                                                        getFilename(),
                                                        getLine(), getColumn());
                                            }
                                        }

                                        _cnt356++;
                                    } while (true);
                                }
                                {
                                    if ((LA(1) == 'E' || LA(1) == 'e')) {
                                        mEXPONENT(false);
                                    } else {
                                    }

                                }
                                {
                                    if ((LA(1) == 'R' || LA(1) == 'S'
                                            || LA(1) == 'r' || LA(1) == 's')) {
                                        mREAL_SUFFIX(true);
                                        f1 = _returnToken;
                                        if (inputState.guessing == 0) {
                                            t = f1;
                                        }
                                    } else {
                                    }

                                }
                                if (inputState.guessing == 0) {

                                    if (t != null
                                            && t.getText().toUpperCase()
                                                    .indexOf('S') >= 0) {
                                        _ttype = NUM_SREAL;
                                    } else {
                                        _ttype = NUM_REAL; // assume double
                                                           // precision
                                    }

                                }
                            }
                            break;
                        }
                        case '.': {
                            match('.');
                            if (inputState.guessing == 0) {
                                _ttype = DOTDOT;
                            }
                            break;
                        }
                        default: {
                        }
                    }
                }
                break;
            }
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                {
                    switch (LA(1)) {
                        case '0': {
                            match('0');
                            if (inputState.guessing == 0) {
                                isDecimal = true;
                            }
                            {
                                switch (LA(1)) {
                                    case 'X':
                                    case 'x': {
                                        {
                                            switch (LA(1)) {
                                                case 'x': {
                                                    match('x');
                                                    break;
                                                }
                                                case 'X': {
                                                    match('X');
                                                    break;
                                                }
                                                default: {
                                                    throw new NoViableAltForCharException(
                                                            (char) LA(1),
                                                            getFilename(),
                                                            getLine(),
                                                            getColumn());
                                                }
                                            }
                                        }
                                        {
                                            int _cnt363 = 0;
                                            _loop363: do {
                                                if ((_tokenSet_7.member(LA(1)))
                                                        && (true) && (true)
                                                        && (true) && (true)) {
                                                    mHEX_DIGIT(false);
                                                } else {
                                                    if (_cnt363 >= 1) {
                                                        break _loop363;
                                                    } else {
                                                        throw new NoViableAltForCharException(
                                                                (char) LA(1),
                                                                getFilename(),
                                                                getLine(),
                                                                getColumn());
                                                    }
                                                }

                                                _cnt363++;
                                            } while (true);
                                        }
                                        break;
                                    }
                                    case '0':
                                    case '1':
                                    case '2':
                                    case '3':
                                    case '4':
                                    case '5':
                                    case '6':
                                    case '7': {
                                        {
                                            int _cnt365 = 0;
                                            _loop365: do {
                                                if (((LA(1) >= '0' && LA(1) <= '7'))) {
                                                    matchRange('0', '7');
                                                } else {
                                                    if (_cnt365 >= 1) {
                                                        break _loop365;
                                                    } else {
                                                        throw new NoViableAltForCharException(
                                                                (char) LA(1),
                                                                getFilename(),
                                                                getLine(),
                                                                getColumn());
                                                    }
                                                }

                                                _cnt365++;
                                            } while (true);
                                        }
                                        break;
                                    }
                                    default: {
                                    }
                                }
                            }
                            break;
                        }
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': {
                            {
                                matchRange('1', '9');
                            }
                            {
                                _loop368: do {
                                    if (((LA(1) >= '0' && LA(1) <= '9'))) {
                                        matchRange('0', '9');
                                    } else {
                                        break _loop368;
                                    }

                                } while (true);
                            }
                            if (inputState.guessing == 0) {
                                isDecimal = true;
                            }
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char) LA(1),
                                    getFilename(), getLine(), getColumn());
                        }
                    }
                }
                {
                    if ((LA(1) == 'D' || LA(1) == 'd')) {
                        {
                            switch (LA(1)) {
                                case 'd': {
                                    match('d');
                                    break;
                                }
                                case 'D': {
                                    match('D');
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException(
                                            (char) LA(1), getFilename(),
                                            getLine(), getColumn());
                                }
                            }
                        }
                        if (inputState.guessing == 0) {
                            _ttype = NUM_DINT;
                        }
                    } else {
                        boolean synPredMatched372 = false;
                        if ((((LA(1) == '.' || LA(1) == 'E' || LA(1) == 'R'
                                || LA(1) == 'S' || LA(1) == 'e' || LA(1) == 'r' || LA(1) == 's')) && (isDecimal))) {
                            final int _m372 = mark();
                            synPredMatched372 = true;
                            inputState.guessing++;
                            try {
                                {
                                    mDOT_FLOAT_EXP(false);
                                }
                            } catch (final RecognitionException pe) {
                                synPredMatched372 = false;
                            }
                            rewind(_m372);
                            inputState.guessing--;
                        }
                        if (synPredMatched372) {
                            {
                                switch (LA(1)) {
                                    case '.': {
                                        match('.');
                                        {
                                            _loop375: do {
                                                if (((LA(1) >= '0' && LA(1) <= '9'))) {
                                                    matchRange('0', '9');
                                                } else {
                                                    break _loop375;
                                                }

                                            } while (true);
                                        }
                                        {
                                            if ((LA(1) == 'E' || LA(1) == 'e')) {
                                                mEXPONENT(false);
                                            } else {
                                            }

                                        }
                                        {
                                            if ((LA(1) == 'R' || LA(1) == 'S'
                                                    || LA(1) == 'r' || LA(1) == 's')) {
                                                mREAL_SUFFIX(true);
                                                f2 = _returnToken;
                                                if (inputState.guessing == 0) {
                                                    t = f2;
                                                }
                                            } else {
                                            }

                                        }
                                        break;
                                    }
                                    case 'E':
                                    case 'e': {
                                        mEXPONENT(false);
                                        {
                                            if ((LA(1) == 'R' || LA(1) == 'S'
                                                    || LA(1) == 'r' || LA(1) == 's')) {
                                                mREAL_SUFFIX(true);
                                                f3 = _returnToken;
                                                if (inputState.guessing == 0) {
                                                    t = f3;
                                                }
                                            } else {
                                            }

                                        }
                                        break;
                                    }
                                    case 'R':
                                    case 'S':
                                    case 'r':
                                    case 's': {
                                        mREAL_SUFFIX(true);
                                        f4 = _returnToken;
                                        if (inputState.guessing == 0) {
                                            t = f4;
                                        }
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltForCharException(
                                                (char) LA(1), getFilename(),
                                                getLine(), getColumn());
                                    }
                                }
                            }
                            if (inputState.guessing == 0) {

                                if (t != null
                                        && t.getText().toUpperCase()
                                                .indexOf('S') >= 0) {
                                    _ttype = NUM_SREAL;
                                } else {
                                    _ttype = NUM_REAL; // assume double
                                                       // precision
                                }

                            }
                        } else {
                        }
                    }
                }
                break;
            }
            default: {
                throw new NoViableAltForCharException((char) LA(1),
                        getFilename(), getLine(), getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mPLUS(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = PLUS;
        final int _saveIndex;

        match('+');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mPRIME(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = PRIME;
        final int _saveIndex;

        match('\'');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mQUESTION(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = QUESTION;
        final int _saveIndex;

        match('?');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mRBRACK(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = RBRACK;
        final int _saveIndex;

        match(']');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mRCURLY(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = RCURLY;
        final int _saveIndex;

        match('}');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mREAL_SUFFIX(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = REAL_SUFFIX;
        final int _saveIndex;

        switch (LA(1)) {
            case 's': {
                match('s');
                break;
            }
            case 'S': {
                match('S');
                break;
            }
            case 'r': {
                match('r');
                break;
            }
            case 'R': {
                match('R');
                break;
            }
            default: {
                throw new NoViableAltForCharException((char) LA(1),
                        getFilename(), getLine(), getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mRPAREN(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = RPAREN;
        final int _saveIndex;

        match(')');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mSCOPE_ESCAPE(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = SCOPE_ESCAPE;
        final int _saveIndex;

        match('~');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mSEMI(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = SEMI;
        final int _saveIndex;

        match(';');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mSINGLE_LINE_STRING_LITERAL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = SINGLE_LINE_STRING_LITERAL;
        final int _saveIndex;

        {
            match('"');
            {
                _loop315: do {
                    if ((LA(1) == '\\')) {
                        mESC(false);
                    } else if ((_tokenSet_5.member(LA(1)))) {
                        {
                            match(_tokenSet_5);
                        }
                    } else {
                        break _loop315;
                    }

                } while (true);
            }
            match('"');
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mSL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = SL;
        final int _saveIndex;

        match("<<");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mSL_COMMENT(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = SL_COMMENT;
        final int _saveIndex;

        match("//");
        {
            _loop299: do {
                if ((_tokenSet_1.member(LA(1)))) {
                    {
                        match(_tokenSet_1);
                    }
                } else {
                    break _loop299;
                }

            } while (true);
        }
        {
            switch (LA(1)) {
                case '\n': {
                    match('\n');
                    break;
                }
                case '\r': {
                    match('\r');
                    {
                        if ((LA(1) == '\n')) {
                            match('\n');
                        } else {
                        }

                    }
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(LA(1), getFilename(),
                            getLine(), getColumn());
                }
            }
        }
        if (inputState.guessing == 0) {
            _ttype = Token.SKIP;
            newline();
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mSTAR(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = STAR;
        final int _saveIndex;

        match('*');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mSTRING_LITERAL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = STRING_LITERAL;
        final int _saveIndex;

        switch (LA(1)) {
            case '"': {
                mSINGLE_LINE_STRING_LITERAL(false);
                break;
            }
            case '`': {
                mMULTI_LINE_STRING_LITERAL(false);
                break;
            }
            default: {
                throw new NoViableAltForCharException(LA(1), getFilename(),
                        getLine(), getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mSTRING_OR_CHAR_LITERAL(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = STRING_OR_CHAR_LITERAL;
        final int _saveIndex;
        boolean isChar = false;

        {
            boolean synPredMatched309 = false;
            if (((LA(1) == '"')
                    && (_tokenSet_3.member(LA(2)))
                    && (LA(3) == '"' || LA(3) == '\'' || LA(3) == '0'
                            || LA(3) == '1' || LA(3) == '2' || LA(3) == '3'
                            || LA(3) == '4' || LA(3) == '5' || LA(3) == '6'
                            || LA(3) == '7' || LA(3) == '\\' || LA(3) == 'b'
                            || LA(3) == 'f' || LA(3) == 'n' || LA(3) == 'r'
                            || LA(3) == 't' || LA(3) == 'u')
                    && (_tokenSet_4.member(LA(4))) && (true))) {
                final int _m309 = mark();
                synPredMatched309 = true;
                inputState.guessing++;
                try {
                    {
                        mCHAR_LITERAL(false);
                    }
                } catch (final RecognitionException pe) {
                    synPredMatched309 = false;
                }
                rewind(_m309);
                inputState.guessing--;
            }
            if (synPredMatched309) {
                mCHAR_LITERAL(false);
                if (inputState.guessing == 0) {
                    isChar = true;
                }
            } else if ((LA(1) == '"' || LA(1) == '`')
                    && ((LA(2) >= '\u0003' && LA(2) <= '\uffff')) && (true)
                    && (true) && (true)) {
                mSTRING_LITERAL(false);
            } else {
                throw new NoViableAltForCharException(LA(1), getFilename(),
                        getLine(), getColumn());
            }

        }
        if (inputState.guessing == 0) {
            if (isChar) {
                _ttype = CHAR_LITERAL;
            } else {
                _ttype = STRING_LITERAL;
            }
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    protected final void mVOCAB(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = VOCAB;
        final int _saveIndex;

        matchRange('\3', '\377');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    public final void mWS(final boolean _createToken)
            throws RecognitionException, CharStreamException,
            TokenStreamException {
        int _ttype;
        Token _token = null;
        final int _begin = text.length();
        _ttype = WS;
        final int _saveIndex;

        if (inputState.guessing == 0) {
            istate().sawNewline = false;
        }
        {
            int _cnt295 = 0;
            _loop295: do {
                switch (LA(1)) {
                    case '\t':
                    case '\u000c':
                    case ' ': {
                        mNON_LINE_BREAK_WS(false);
                        break;
                    }
                    case '\n':
                    case '\r': {
                        mLINE_BREAK(false);
                        break;
                    }
                    default: {
                        if (_cnt295 >= 1) {
                            break _loop295;
                        } else {
                            throw new NoViableAltForCharException(LA(1),
                                    getFilename(), getLine(), getColumn());
                        }
                    }
                }
                _cnt295++;
            } while (true);
        }
        if (inputState.guessing == 0) {

            if (istate().sawNewline) {
                if (istate().inMatrix) {
                    _ttype = SEMI; // treat newline in a matrix as row delimier
                                   // (';')
                } else {
                    _ttype = Token.SKIP;
                }
            } else {
                _ttype = Token.SKIP; // skip non line-break WS
            }

        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()
                    - _begin));
        }
        _returnToken = _token;
    }

    @Override
    public Token nextToken() throws TokenStreamException {
        Token theRetToken = null;
        tryAgain: for (;;) {
            final Token _token = null;
            int _ttype = Token.INVALID_TYPE;
            resetText();
            try { // for char stream error handling
                try { // for lexical error handling
                    switch (LA(1)) {
                        case '?': {
                            mQUESTION(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '(': {
                            mLPAREN(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case ')': {
                            mRPAREN(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '[': {
                            mLBRACK(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case ']': {
                            mRBRACK(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '{': {
                            mLCURLY(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '}': {
                            mRCURLY(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case ':': {
                            mCOLON(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case ',': {
                            mCOMMA(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '*': {
                            mSTAR(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '%': {
                            mMOD(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '^': {
                            mHAT(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '|': {
                            mBAR(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '&': {
                            mBAND(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case ';': {
                            mSEMI(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '#': {
                            mHASH(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '~': {
                            mSCOPE_ESCAPE(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '\t':
                        case '\n':
                        case '\u000c':
                        case '\r':
                        case ' ': {
                            mWS(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '"':
                        case '`': {
                            mSTRING_OR_CHAR_LITERAL(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        case '.':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': {
                            mNUM_INT(true);
                            theRetToken = _returnToken;
                            break;
                        }
                        default:
                            if ((LA(1) == '@') && (LA(2) == '[')) {
                                mANNOT_START(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '=') && (LA(2) == '=')) {
                                mEQUAL(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '!') && (LA(2) == '=')) {
                                mNOT_EQUAL(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '+') && (LA(2) == '+')) {
                                mINC(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '-') && (LA(2) == '-')) {
                                mDEC(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '>') && (LA(2) == '=')) {
                                mGTE(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '<') && (LA(2) == '<')) {
                                mSL(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '<') && (LA(2) == '=')) {
                                mLTE(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '-') && (LA(2) == '>')) {
                                mGIVES(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '<') && (LA(2) == '-')) {
                                mFROM(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '\'') && (LA(2) == '(')) {
                                mLISTSTART(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '/') && (LA(2) == '/')) {
                                mSL_COMMENT(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '/') && (LA(2) == '*')) {
                                mML_COMMENT(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '=') && (true)) {
                                mASSIGN(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '!') && (true)) {
                                mLNOT(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '/') && (true)) {
                                mDIV(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '+') && (true)) {
                                mPLUS(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '-') && (true)) {
                                mMINUS(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '>') && (true)) {
                                mGTHAN(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '<') && (true)) {
                                mLTHAN(true);
                                theRetToken = _returnToken;
                            } else if ((LA(1) == '\'') && (true)) {
                                mPRIME(true);
                                theRetToken = _returnToken;
                            } else if ((_tokenSet_0.member(LA(1))) && (true)) {
                                mIDENT(true);
                                theRetToken = _returnToken;
                            } else {
                                if (LA(1) == EOF_CHAR) {
                                    uponEOF();
                                    _returnToken = makeToken(Token.EOF_TYPE);
                                } else {
                                    throw new NoViableAltForCharException(
                                            LA(1), getFilename(), getLine(),
                                            getColumn());
                                }
                            }
                    }
                    if (_returnToken == null) {
                        continue tryAgain; // found SKIP token
                    }
                    _ttype = _returnToken.getType();
                    _returnToken.setType(_ttype);
                    return _returnToken;
                } catch (final RecognitionException e) {
                    throw new TokenStreamRecognitionException(e);
                }
            } catch (final CharStreamException cse) {
                if (cse instanceof CharStreamIOException) {
                    throw new TokenStreamIOException(
                            ((CharStreamIOException) cse).io);
                } else {
                    throw new TokenStreamException(cse.getMessage());
                }
            }
        }
    }

}
