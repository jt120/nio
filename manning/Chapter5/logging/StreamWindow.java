package Chapter5.logging;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StreamWindow extends JFrame
{
  // The text area in which we display incoming text
  private TextArea textArea;

  // Data written to this stream is appended to the
  // text area
  private StreamWindowStream out;

  /**
   * Create a new StreamWindow -- set up the interface
   * and install listeners.  Make the window visible
   * after everything else is done
   */
  public StreamWindow( String name ) {
    super( name );

    out = new StreamWindowStream();

    setupGUI();
    addListeners();

    setVisible( true );
  }

  /**
   * Add the text area to the window, and set the window
   * size
   */
  private void setupGUI() {
    Container cp = getContentPane();

    textArea = new TextArea();

    cp.setLayout( new BorderLayout() );
    cp.add( textArea, BorderLayout.CENTER );

    setLocation( 100, 100 );
    setSize( 100, 100 );
  }

  /**
   * Close the window properly if the close-button is
   * pressed
   */
  private void addListeners() {
    addWindowListener( new WindowListener() {
      public void windowActivated( WindowEvent we ) {
      }
      public void windowClosed( WindowEvent we ) {
      }
      public void windowClosing( WindowEvent we ) {
        // Remove window if the close-button is pressed
        closeWindow();
      }
      public void windowDeactivated( WindowEvent we ) {
      }
      public void windowDeiconified( WindowEvent we ) {
      }
      public void windowIconified( WindowEvent we ) {
      }
      public void windowOpened( WindowEvent we ) {
      }
    } );
  }

  /**
   * Return the output stream that is connected
   * to this window
   */
  public OutputStream getOutputStream() {
    return out;
  }

  /**
   * Close the window, and dispose of it
   */
  protected void closeWindow() {
    setVisible( false );
    dispose();
  }

  /**
   * Add text to the end of the text showing in the
   * text area
   */
  private void appendText( String string ) {
    textArea.append( string );
  }

  /**
   * Inner class: an output stream.  Writing to
   * this stream sends the data to the window
   */
  class StreamWindowStream extends OutputStream
  {
    // This is used to write a single byte.  We
    // pre-allocate it to save time
    private byte tinyBuffer[] = new byte[1];

    /**
     * Closing the stream closes the window
     */
    public void close() throws IOException {
      closeWindow();
    }

    /**
     * Write a single byte
     */
    public void write( int b ) throws IOException {
      // Store the single byte in the array and
      // write the array
      tinyBuffer[0] = (byte)b;
      write( tinyBuffer );
    }

    /**
     * Write an array of bytes
     */
    public void write( byte b[] ) throws IOException {
      // Convert the bytes to a string and append
      String s = new String( b );
      appendText( s );
    }

    /**
     * Write a sub-array of bytes
     */
    public void write( byte b[], int off, int len )
        throws IOException {
      // Convert the bytes to a string and append
      String s = new String( b, off, len );
      appendText( s );
    }
  }
}
