package Chapter3;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.print.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ImagePrinter extends JFrame
{
  private String filename;
  private BufferedImage image;
  private JTextArea statusTA;
  private PrintService printServices[];
  private PrintRequestAttributeSet attributeSet;

  public ImagePrinter( String filename ) throws IOException {
    super( "ImagePrinter" );
    this.filename = filename;

    setupAttributeSet();
    findPrinters();
    loadImage( filename );
    setupGUI();

    showStatus( "Ready." );
  }

  private void setupAttributeSet() {
    // The required attributes of the printer(s) we
    // will display in the printer list
    attributeSet = new HashPrintRequestAttributeSet();
    attributeSet.add( new Copies( 1 ) );
  }

  private void doPrint( int service ) {
    try {
      PrintService ps = printServices[service];

      DocPrintJob job = ps.createPrintJob();

      job.addPrintJobListener( new PrintJobListener() {
        public void printDataTransferCompleted(
            PrintJobEvent pje ) {
          showStatus( "Transfer Completed." );
        }
        public void printJobCanceled( PrintJobEvent pje ) {
          showStatus( "Print Job Canceled." );
        }
        public void printJobCompleted( PrintJobEvent pje ) {
          showStatus( "Print Job Completed." );
        }
        public void printJobFailed( PrintJobEvent pje ) {
          showStatus( "Print Job Failed." );
        }
        public void printJobNoMoreEvents( PrintJobEvent pje ) {
        }
        public void printJobRequiresAttention( PrintJobEvent pje ) {
          showStatus( "Print Job Requires Attention." );
        }
      } );

      FileInputStream fin = new FileInputStream( filename );
      Doc doc = new SimpleDoc( fin,
                               DocFlavor.INPUT_STREAM.GIF, null );

      job.print( doc, attributeSet );

      fin.close();
    } catch( IOException ie ) {
      ie.printStackTrace();
    } catch( PrintException pe ) {
      pe.printStackTrace();
    }
  }

  private void findPrinters() {
    printServices =
      PrintServiceLookup.lookupPrintServices(
        DocFlavor.INPUT_STREAM.GIF, attributeSet );

    if (printServices.length==0)
      throw new RuntimeException(
        "No printer services available." );
  }

  private void loadImage( String filename ) throws IOException {
    image = ImageIO.read( new File( filename ) );
  }

  private void setupGUI() {
    setBackground( Color.white );
    JPanel panel0 = new JPanel();
    panel0.setBorder(
      BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    panel0.setLayout( new BorderLayout() );

    JPanel printerPanel = new JPanel();
    printerPanel.setLayout( new BorderLayout() );
    printerPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder( Color.black ),
        "Select Printer" ) );

    String printServiceNames[] = new String[printServices.length];
    for (int i=0; i<printServices.length; ++i) {
      printServiceNames[i] = printServices[i].getName();
    }
    final JList printerList = new JList( printServiceNames );
    printerList.addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged( ListSelectionEvent lse ) {
          if (!lse.getValueIsAdjusting()) {
            int ind = printerList.getSelectedIndex();
            if (ind==-1) {
              showStatus( "No printer selected." );
            } else {
              String printerName = printServices[ind].getName();
              showStatus( printerName+" selected." );
            }
          }
        }
      } );
    printerList.setSelectionMode(
      ListSelectionModel.SINGLE_SELECTION );
    printerList.setPreferredSize( new Dimension( 20, 80 ) );

    printerPanel.add( printerList, BorderLayout.CENTER );

    JButton printButton = new JButton( "Print" );
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(
      BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    buttonPanel.setLayout( new BorderLayout() );
    printButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent ae ) {
        int ind = printerList.getSelectedIndex();
        if (ind==-1) {
          showStatus( "No printer selected." );
        } else {
          doPrint( ind );
        }
      }
    } );
    buttonPanel.add( printButton, BorderLayout.CENTER );

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout( new BorderLayout() );
    rightPanel.add( printerPanel, BorderLayout.CENTER );
    rightPanel.add( buttonPanel, BorderLayout.SOUTH );

    JPanel imagePanel = new JPanel();
    imagePanel.setLayout( new BorderLayout() );
    imagePanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder( Color.black ),
        "Image" ) );

    ImageCanvas imageCanvas = new ImageCanvas( image );
    imageCanvas.setPreferredSize( new Dimension( 140, 80 ) );
    imageCanvas.setMinimumSize( new Dimension( 140, 80 ) );
    imagePanel.add( imageCanvas, BorderLayout.CENTER );

    JPanel panel1 = new JPanel();
    panel1.setLayout( new BoxLayout( panel1, BoxLayout.X_AXIS ) );
    panel1.add( imagePanel );
    panel1.add( rightPanel );

    JPanel statusPanel = new JPanel();
    statusPanel.setLayout( new BorderLayout() );
    statusPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder( Color.black ),
        "Status" ) );

    statusTA = new JTextArea( 6, 80 );
    statusTA.setEditable( false );
    JScrollPane statusSP = new JScrollPane( statusTA,
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
    statusPanel.add( statusSP, BorderLayout.CENTER );
    panel0.add( statusPanel, BorderLayout.SOUTH );

    panel0.add( panel1, BorderLayout.CENTER );

    getContentPane().setBackground( Color.white );
    getContentPane().add( panel0, BorderLayout.CENTER );

    setSize( 450, 350 );

    addWindowListener( new WindowListener() {
      public void windowActivated( WindowEvent we ) {
      }
      public void windowClosed( WindowEvent we ) {
      }
      public void windowClosing( WindowEvent we ) { 
        System.exit( 0 );
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

  private void showStatus( String status ) {
    statusTA.setText( statusTA.getText()+status+"\n" );
  }

  static class ImageCanvas extends JPanel
  {
    private BufferedImage image;
    public ImageCanvas( BufferedImage image ) {
      this.image = image;
    }
    public void paintComponent( Graphics g ) {
      int width = getWidth();
      int height = getHeight();
      g.drawImage( image, 0, 0, width, height, null );
    }
  }

  static public void main( String args[] ) throws IOException {
    if (args.length != 1) {
      System.err.println(
        "Usage: java ImagePrinter <image filename>" );
      System.exit( 1 );
    }

    String filename = args[0];
    if (!filename.toLowerCase().endsWith( ".gif" ))
      throw new RuntimeException(
        "Image must be a gif: "+filename );

    ImagePrinter ip = new ImagePrinter( filename );
    ip.setVisible( true );
  }
}
