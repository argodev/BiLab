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

// / represents something that can be assigned (i.e. a Left Value)
// / (e.g. a Symbol, a vector/matrix or Symbols or an indexed Symbol)
public class LValue {
    protected Symbol _symbol;

    protected boolean _isProperty;

    protected FuncInfo _propCallSig;

    protected Object[] _propArgs;

    public LValue(final Symbol s) {
        _isProperty = false;
        _symbol = s;
    }

    public LValue(final Symbol s, final FuncInfo callSig,
            final Object[] propArgs) {
        _isProperty = true;
        _propArgs = propArgs;
        _propCallSig = callSig;
        _symbol = s;
    }

    public Symbol getSymbol() {
        return _symbol;
    }

    public TypeSpec getType() {
        if (!_isProperty) {
            return _symbol.getType();
        } else {
            if (_propCallSig.numArgs() == 0) {
                return _symbol.getType();
            } else {
                return _symbol.getType(_propCallSig, _propArgs);
            }
        }
    }

    public Object getValue() {
        if (!_isProperty) {
            return _symbol.getValue();
        } else {
            if (_propCallSig.numArgs() == 0) {
                return _symbol.getValue();
            } else {
                return _symbol.getValue(_propCallSig, _propArgs);
            }
        }
    }

    public boolean isBoundProperty() {
        return _isProperty;
    }

    public void setValue(final Object value) {
        if (!_isProperty) {
            _symbol.setValue(value);
        } else {
            if (_propCallSig.numArgs() == 0) {
                _symbol.setValue(value);
            } else {
                _symbol.setValue(_propCallSig, _propArgs, value);
            }
        }
    }

    @Override
    public String toString() {
        return _symbol.toString();
    }
}
