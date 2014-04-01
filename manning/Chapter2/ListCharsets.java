package Chapter2;
import java.util.*;
import java.nio.charset.*;

public class ListCharsets
{
  static public void main( String args[] ) throws Exception {
    SortedMap charsets = Charset.availableCharsets();
    Set names = charsets.keySet();
    for (Iterator e=names.iterator(); e.hasNext();) {
      String name = (String)e.next();
      Charset charset = (Charset)charsets.get( name );
      System.out.println( charset );
      Set aliases = charset.aliases();
      for (Iterator ee=aliases.iterator(); ee.hasNext();) {
        System.out.println( "    "+ee.next() );
      }
    }
  }
}
