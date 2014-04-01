package Chapter8;
import java.io.*;
import java.util.*; 

public class DumpableGraph
{
  public static class Node {
    private Object obj;
    private List children = new ArrayList();

    public Node( Object obj ) {
      this.obj = obj;
    }

    public void addChild( Node node ) {
      children.add( node );
    }

    public boolean equals( Object node ) {
      return obj.equals( ((Node)node).obj );
    }

    public int hashCode() {
      return obj.hashCode();
    }

    public String toString() {
      return obj.toString();
    }

    public void dump() {
      // Start the dumping process with an empty
      // seen-set
      dump( "", new IdentityHashMap() );
    }

    private void dump( String prefix, Map seen ) {
      // Print out information about this node
      System.out.println( prefix+"Node: "+obj+
                          " ["+System.identityHashCode( obj )+"/"+
                          obj.hashCode()+"]" );
      if (children.size()==0) {

        // If there are no children, we've reached a leaf,
        // so we're done
        System.out.println( prefix+"  (no children)" );
      } else {

        // We only visit the children of this node if we
        // haven't already done so -- if we're not in the
        // seen-set
        if (!seen.containsKey( this )) {

          // Remember that we've processed this node by
          // putting it in the seen-set
          seen.put( this, null );

          // Dump all the children of this node
          for (Iterator it=children.iterator(); it.hasNext();) {
            Node node = (Node)it.next();

            // Indent the prefix by two spaces
            node.dump( prefix+"  ", seen );
          }
        } else {
          System.out.println( prefix+"  (loop)" );
        }
      }
    }
  }

  static public void main( String args[] ) {
    Node a = new Node( "a" );
    Node b = new Node( "b" );
    Node c = new Node( "c" );
    Node d = new Node( "d" );
    Node a2 = new Node( "a" );
    Node b2 = new Node( "b" );
    Node e = new Node( "e" );
    a.addChild( b );
    a.addChild( c );
    c.addChild( d );
    c.addChild( a );
    c.addChild( a2 );
    a2.addChild( b2 );
    a2.addChild( e );
    a.dump();
  }
}
