package bilab;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import junit.framework.TestCase;
import org.eclipse.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.biojava.bio.Annotation;
import org.biojava.bio.BioError;
import org.biojava.bio.BioException;
import org.biojava.bio.proteomics.MassCalc;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolPropertyTable;
import org.biojava.utils.ChangeVetoException;

import scigol.Any;
import scigol.Range;
import scigol.TypeSpec;
import scigol.accessor;




public class Test extends TestCase {
 
  
    public void test() {
        //assertTrue(1 == 1);
        final String seq1 = "CGY";
        final String seq2 = "TYRA";
    
      // final String seq4 = seq1.get_rawsequence();     
       new protein (seq1 + seq2);
                  
       final String seq3 = "CGYTYRA";
       
       int i = seq1.length();
       int j = seq2.length();
      
       //double k = seq1.avgIsoMass;
       
       
        assertTrue( seq1 == "CGY"); //sequence match
         assertEquals(3, i);        //length match
            assertTrue( seq2 == "TYRA");
               assertEquals(4, j);
               assertTrue( seq3 == "CGYTYRA");
              
 // Doing some DNA,RNA testing
          final String dna1 = "ACG";
     //      dna1.get_Compliment();
          int l = dna1.length();
           assertEquals(3, l);
           assertTrue (dna1 == "ACG");
           assertFalse (dna1 =="TCG");
           System.out.println("JUnit Test Case works!");
    }
   
}



