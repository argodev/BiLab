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

public interface ScigolTreeParserTokenTypes {
    int EOF = 1;
    int NULL_TREE_LOOKAHEAD = 3;
    int DOT = 4;
    int DOTDOT = 5;
    int UNARY_MINUS = 6;
    int UNARY_PLUS = 7;
    int EXPRLIST = 8;
    int INITLIST = 9;
    int MATRIX = 10;
    int LIST = 11;
    int MAP = 12;
    int FUNC = 13;
    int POST_INC = 14;
    int POST_DEC = 15;
    int APPLICATION = 16;
    int LIT_TRUE = 17;
    int LIT_FALSE = 18;
    int LIT_NULL = 19;
    int LIT_FUNC = 20;
    int MODIFIERS = 21;
    int BUILTIN_TYPE = 22;
    int CTOR = 23;
    int PROP = 24;
    int LCURLY = 25;
    int SEMI = 26;
    int LITERAL_namespace = 27;
    int RCURLY = 28;
    int IDENT = 29;
    int LITERAL_using = 30;
    int LITERAL_as = 31;
    int LITERAL_from = 32;
    int STRING_LITERAL = 33;
    int LITERAL_pre = 34;
    int LITERAL_post = 35;
    int LITERAL_typeof = 36;
    int LPAREN = 37;
    int RPAREN = 38;
    int LITERAL_assert = 39;
    int COMMA = 40;
    int LITERAL_debug = 41;
    int LITERAL_logger = 42;
    int ASSIGN = 43;
    int LITERAL_or = 44;
    int LITERAL_and = 45;
    int NOT_EQUAL = 46;
    int EQUAL = 47;
    int LITERAL_is = 48;
    int LITERAL_isnt = 49;
    int LTHAN = 50;
    int GTHAN = 51;
    int LTE = 52;
    int GTE = 53;
    int PLUS = 54;
    int MINUS = 55;
    int STAR = 56;
    int DIV = 57;
    int MOD = 58;
    int HAT = 59;
    int INC = 60;
    int DEC = 61;
    int LNOT = 62;
    int LITERAL_not = 63;
    int HASH = 64;
    int PRIME = 65;
    int BAR = 66;
    int SCOPE_ESCAPE = 67;
    int LISTSTART = 68;
    int LITERAL_try = 69;
    int LITERAL_catch = 70;
    int COLON = 71;
    int NUM_INT = 72;
    int NUM_DINT = 73;
    int NUM_REAL = 74;
    int NUM_SREAL = 75;
    int CHAR_LITERAL = 76;
    int LBRACK = 77;
    int RBRACK = 78;
    int GIVES = 79;
    int ANNOT_START = 80;
    int LITERAL_let = 81;
    int LITERAL_const = 82;
    int LITERAL_static = 83;
    int LITERAL_final = 84;
    int LITERAL_class = 85;
    int LITERAL_interface = 86;
    int LITERAL_property = 87;
    int LITERAL_override = 88;
    int LITERAL_implicit = 89;
    int LITERAL_public = 90;
    int LITERAL_private = 91;
    int LITERAL_protected = 92;
    int LITERAL_func = 93;
    int LITERAL_vector = 94;
    int LITERAL_matrix = 95;
    int LITERAL_range = 96;
    int LITERAL_list = 97;
    int LITERAL_map = 98;
    int LITERAL_bool = 99;
    int LITERAL_byte = 100;
    int LITERAL_char = 101;
    int LITERAL_int = 102;
    int LITERAL_dint = 103;
    int LITERAL_real = 104;
    int LITERAL_sreal = 105;
    int LITERAL_string = 106;
    int LITERAL_type = 107;
    int LITERAL_num = 108;
    int LITERAL_any = 109;
    int LITERAL_object = 110;
    int LITERAL_if = 111;
    int LITERAL_then = 112;
    int LITERAL_else = 113;
    int LITERAL_do = 114;
    int LITERAL_while = 115;
    int LITERAL_for = 116;
    int LITERAL_foreach = 117;
    int LITERAL_in = 118;
    int LITERAL_by = 119;
    int LITERAL_throw = 120;
    int QUESTION = 121;
    int SL = 122;
    int BAND = 123;
    int FROM = 124;
    int LINE_BREAK = 125;
    int NON_LINE_BREAK_WS = 126;
    int WS = 127;
    int SL_COMMENT = 128;
    int ML_COMMENT = 129;
    int STRING_OR_CHAR_LITERAL = 130;
    int SINGLE_LINE_STRING_LITERAL = 131;
    int MULTI_LINE_STRING_LITERAL = 132;
    int NON_BACKQUOTE_STRING = 133;
    int CHAR_LIT_SUFFIX = 134;
    int ESC = 135;
    int HEX_DIGIT = 136;
    int VOCAB = 137;
    int DOT_FLOAT_EXP = 138;
    int EXPONENT = 139;
    int REAL_SUFFIX = 140;
}
