package Chapter8;
import java.util.*;

public class RandomAccessifier
{
  interface ListTransform
  {
    public void transform( List list );
  }

  static class RandomTransform implements ListTransform
  {
    private int count;
    private Random rand = new Random();

    public RandomTransform( int count ) {
      this.count = count;
    }

    public void transform( List list ) {
      for (int i=0; i<count; ++i) {
        int ai = rand.nextInt( list.size() );
        int bi = rand.nextInt( list.size() );
        Collections.swap( list, ai, bi );
      }
    }
  }

  public static void transform( List list,
                                ListTransform transform ) {
    List origList = list;
    boolean ra = (list instanceof RandomAccess);
    if (!ra) {
      System.out.println( "Converting to RA" );
      list = new ArrayList( origList.size() );
      for (Iterator it=origList.iterator(); it.hasNext();) {
        list.add( it.next() );
      }
    }
    transform.transform( list );
    if (!ra) {
      origList.clear();
      int size = list.size();
      for (Iterator it=list.iterator(); it.hasNext();) {
        origList.add( it.next() );
      }
    }
  }

  static public void main( String args[] ) {
    List list = new LinkedList();
    for (int i=0; i<100; ++i) {
      list.add( new Integer( i ) );
    }

    RandomTransform rt = new RandomTransform( 10000000 );
    transform( list, rt );

    for (Iterator it=list.iterator(); it.hasNext();) {
      System.out.print( it.next()+" " );
    }
    System.out.println( "" );
  }
}
