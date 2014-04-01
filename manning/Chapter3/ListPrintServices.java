package Chapter3;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

public class ListPrintServices
{
  public ListPrintServices() {
    PrintService pss[] =
      PrintServiceLookup.lookupPrintServices( null, null );

    for (int i=0; i<pss.length; ++i) {
      System.out.println( pss[i] );
      PrintService ps = pss[i];

      PrintServiceAttributeSet psas = ps.getAttributes();
      Attribute attributes[] = psas.toArray();
      for (int j=0; j<attributes.length; ++j) {
        Attribute attribute = attributes[j];
        System.out.println( "  attribute: "+attribute.getName() );

        if (attribute instanceof PrinterName) {
          PrinterName pn = (PrinterName)attribute;
          System.out.println( "    printer name: "+pn.getValue() );
        }
      }

      DocFlavor supportedFlavors[] = ps.getSupportedDocFlavors();
      for (int j=0; j<supportedFlavors.length; ++j) {
        System.out.println( "  flavor: "+supportedFlavors[j] );
      }
    }
  }

  static public void main( String args[] ) throws Exception {
    new ListPrintServices();
  }
}
