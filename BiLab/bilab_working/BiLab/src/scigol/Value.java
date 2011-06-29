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

// / An internal value (can be a LValue (left value) or an actual value (right
// value),
// / a partially qualified name or a overloaded func array (with instance) )
public class Value {
    protected boolean _isLeftValue;

    protected boolean _isNamespaceComponent;

    protected Object _value;

    protected Location _loc = null;

    // construct a null rvalue
    public Value() {
        _isLeftValue = false;
        _value = null;
        _isNamespaceComponent = false;
    }

    // construct with an LValue
    public Value(final LValue lvalue) {
        Debug.Assert(lvalue != null, "lvalue can't be null");
        _isLeftValue = true;
        _value = lvalue;
        _isNamespaceComponent = false;
    }

    // construct with an rvalue, or a string partially qualified name
    // (after which you should call setValueIsNamespaceComponent() to
    // differentiate from a string rvalue)
    public Value(final Object value) {
        if (value instanceof Value) {
            // copy
            final Value v = (Value) value;
            _isLeftValue = v._isLeftValue;
            _isNamespaceComponent = v._isNamespaceComponent;
            _value = v._value;
        } else {
            _isLeftValue = false;
            _value = value;
            _isNamespaceComponent = false;
        }

        Debug.Assert(!(_value instanceof Value), "Values can't wrap Values");
    }

    public LValue getLValue() {
        Debug.Assert(_isLeftValue, "isn't an lvalue");
        Debug.Assert(_value instanceof LValue, "_value isn't an LValue");
        return ((LValue) _value);
    }

    public String getNamespaceComponentString() {
        Debug.Assert(isNamespaceComponent());
        return _value.toString();
    }

    public TypeSpec getType() {
        if (_isLeftValue) {
            return ((LValue) _value).getType();
        } else {
            if (!_isNamespaceComponent) {
                return TypeSpec.typeOf(_value);
            } else {
                ScigolTreeParser
                        .semanticError(
                                _loc,
                                "the symbol '"
                                        + _value.toString()
                                        + "' could not be found in the current or global scope");
                return null;
            }
        }
    }

    public Object getValue() {
        if (_isLeftValue) {
            return ((LValue) _value).getValue();
        } else {
            if (!_isNamespaceComponent) {
                return _value;
            } else {
                ScigolTreeParser
                        .semanticError(
                                _loc,
                                "the symbol '"
                                        + _value.toString()
                                        + "' could not be found in the current or global scope");
                return null;
            }
        }
    }

    public boolean isLValue() {
        return _isLeftValue;
    }

    public boolean isNamespaceComponent() {
        return _isNamespaceComponent;
    }

    // if this is an lvalue then it is evaluated, hence becoming an rvalue
    public Value rvalue() {
        if (!_isLeftValue) {
            return this;
        }

        final LValue lvalue = (LValue) _value;
        _value = lvalue.getValue();
        _isLeftValue = false;
        return this;
    }

    public void setValue(final Object value) {
        Debug.Assert(!(value instanceof Value));
        Debug.Assert(_isLeftValue, "can only set the value of a LValue");
        ((LValue) _value).setValue(value);
    }

    // Value can hold a namepace component as a string (e.g. System), which is
    // not a real value
    // but is useful to treat as one for the purpose of performing selection (.)
    // operations
    public void setValueIsNamespaceComponent(final Location l) {
        Debug.Assert(!_isLeftValue);
        Debug.Assert(_value instanceof String);
        _isLeftValue = false;
        _isNamespaceComponent = true;
        _loc = l;
    }

    @Override
    public String toString() {
        final Object v = _value;
        if (v == null) {
            return "null";
        } else {
            return v.toString();
        }
    }
}
