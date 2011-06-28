/*
 *                  BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 * 
 * Created on Jan 23, 2007
 *
 */
package bilab;
import scigol.Debug;



public class JmolUtils {


    /**
     * Bits which indicate whether or not an element symbol is valid.
     *<p>
     * If the high bit is set, then it is valid as a standalone char.
     * otherwise, bits 0-25 say whether or not is valid when followed
     * by the letters a-z.
     */
    final static int[] elementCharMasks = {
      //   Ac Ag Al Am Ar As At Au
      1 << ('c' - 'a') |
      1 << ('g' - 'a') |
      1 << ('l' - 'a') |
      1 << ('m' - 'a') |
      1 << ('r' - 'a') |
      1 << ('s' - 'a') |
      1 << ('t' - 'a') |
      1 << ('u' - 'a'),
      // B Ba Be Bh Bi Bk Br
      1 << 31 |
      1 << ('a' - 'a') |
      1 << ('e' - 'a') |
      1 << ('h' - 'a') |
      1 << ('i' - 'a') |
      1 << ('k' - 'a') |
      1 << ('r' - 'a'),
      // C Ca Cd Ce Cf Cl Cm Co Cr Cs Cu
      1 << 31 |
      1 << ('a' - 'a') |
      1 << ('d' - 'a') |
      1 << ('e' - 'a') |
      1 << ('f' - 'a') |
      1 << ('l' - 'a') |
      1 << ('m' - 'a') |
      1 << ('o' - 'a') |
      1 << ('r' - 'a') |
      1 << ('s' - 'a') |
      1 << ('u' - 'a'),
      //  D Db Dy
      1 << 31 |
      1 << ('b' - 'a') |
      1 << ('y' - 'a'),
      //   Er Es Eu
      1 << ('r' - 'a') |
      1 << ('s' - 'a') |
      1 << ('u' - 'a'),
      // F Fe Fm Fr
      1 << 31 |
      1 << ('e' - 'a') |
      1 << ('m' - 'a') |
      1 << ('r' - 'a'),
      //   Ga Gd Ge
      1 << ('a' - 'a') |
      1 << ('d' - 'a') |
      1 << ('e' - 'a'),
      // H He Hf Hg Ho Hs
      1 << 31 |
      1 << ('e' - 'a') |
      1 << ('f' - 'a') |
      1 << ('g' - 'a') |
      1 << ('o' - 'a') |
      1 << ('s' - 'a'),
      // I In Ir
      1 << 31 |
      1 << ('n' - 'a') |
      1 << ('r' - 'a'),
      //j
      0,
      // K Kr
      1 << 31 |
      1 << ('r' - 'a'),
      //   La Li Lr Lu
      1 << ('a' - 'a') |
      1 << ('i' - 'a') |
      1 << ('r' - 'a') |
      1 << ('u' - 'a'),
      //   Md Mg Mn Mo Mt
      1 << ('d' - 'a') |
      1 << ('g' - 'a') |
      1 << ('n' - 'a') |
      1 << ('o' - 'a') |
      1 << ('t' - 'a'),
      // N Na Nb Nd Ne Ni No Np
      1 << 31 |
      1 << ('a' - 'a') |
      1 << ('b' - 'a') |
      1 << ('d' - 'a') |
      1 << ('e' - 'a') |
      1 << ('i' - 'a') |
      1 << ('o' - 'a') |
      1 << ('p' - 'a'),
      // O Os
      1 << 31 |
      1 << ('s' - 'a'),
      // P Pa Pb Pd Pm Po Pr Pt Pu
      1 << 31 |
      1 << ('a' - 'a') |
      1 << ('b' - 'a') |
      1 << ('d' - 'a') |
      1 << ('m' - 'a') |
      1 << ('o' - 'a') |
      1 << ('r' - 'a') |
      1 << ('t' - 'a') |
      1 << ('u' - 'a'),
      //q
      0,
      //   Ra Rb Re Rf Rh Rn Ru
      1 << ('a' - 'a') |
      1 << ('b' - 'a') |
      1 << ('e' - 'a') |
      1 << ('f' - 'a') |
      1 << ('h' - 'a') |
      1 << ('n' - 'a') |
      1 << ('u' - 'a'),
      // S Sb Sc Se Sg Si Sm Sn Sr
      1 << 31 |
      1 << ('b' - 'a') |
      1 << ('c' - 'a') |
      1 << ('e' - 'a') |
      1 << ('g' - 'a') |
      1 << ('i' - 'a') |
      1 << ('m' - 'a') |
      1 << ('n' - 'a') |
      1 << ('r' - 'a'),
      //  T Ta Tb Tc Te Th Ti Tl Tm
      1 << 31 |
      1 << ('a' - 'a') |
      1 << ('b' - 'a') |
      1 << ('c' - 'a') |
      1 << ('e' - 'a') |
      1 << ('h' - 'a') |
      1 << ('i' - 'a') |
      1 << ('l' - 'a') |
      1 << ('m' - 'a'),
      // U
      1 << 31,
      // V
      1 << 31,
      // W
      1 << 31,
      //   Xe Xx
      1 << ('e' - 'a') |
      1 << ('x' - 'a'), // don't know if I should have Xx here or not?
      // Y Yb
      1 << 31 |
      1 << ('b' - 'a'),
      //   Zn Zr
      1 << ('n' - 'a') |
      1 << ('r' - 'a')
    };

    
    public static String deduceElementSymbol(String line) {
       
        char ch12 = line.charAt(0);
        char ch13 = line.charAt(1);
        
        if (isValidElementSymbolNoCaseSecondChar(ch12, ch13))
          return "" + ch12 + ch13;
        if (isValidElementSymbol(ch13))
          return "" + ch13;
        if (isValidElementSymbol(ch12))
          return "" + ch12;
        return "Xx";
      }
    
    
    
    static boolean isValidElementSymbol(char ch) {
      return ch >= 'A' && ch <= 'Z' && elementCharMasks[ch - 'A'] < 0;
    }

    static boolean isValidElementSymbol(char chFirst, char chSecond) {
      if (chFirst < 'A' || chFirst > 'Z' || chSecond < 'a' || chSecond > 'z')
        return false;
      return ((elementCharMasks[chFirst - 'A'] >> (chSecond - 'a')) & 1) != 0;
    }

    static boolean isValidElementSymbolNoCaseSecondChar(char chFirst,
                                                        char chSecond) {
      if (chSecond >= 'A' && chSecond <= 'Z')
        chSecond += 'a' - 'A';
      if (chFirst < 'A' || chFirst > 'Z' || chSecond < 'a' || chSecond > 'z')
        return false;
      return ((elementCharMasks[chFirst - 'A'] >> (chSecond - 'a')) & 1) != 0;
    }

    static boolean isValidFirstSymbolChar(char ch) {
      return ch >= 'A' && ch <= 'Z' && elementCharMasks[ch - 'A'] != 0;
    }

    static boolean isValidElementSymbolNoCaseSecondChar(String str) {
      if (str == null)
        return false;
      int length = str.length();
      if (length == 0)
        return false;
      char chFirst = str.charAt(0);
      if (length == 1)
        return isValidElementSymbol(chFirst);
      if (length > 2)
        return false;
      char chSecond = str.charAt(1);
      return isValidElementSymbolNoCaseSecondChar(chFirst, chSecond);
    }
    
    
}

