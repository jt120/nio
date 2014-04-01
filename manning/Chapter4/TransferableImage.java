package Chapter4;
import java.awt.*;
import java.awt.datatransfer.*;

/**
 * Wrapper class for copying image to system clipboard
 */
public class TransferableImage implements Transferable
{
  private Image image;

  public TransferableImage( Image image ) {
    this.image = image;
  }

  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] { DataFlavor.imageFlavor };
  }

  public boolean isDataFlavorSupported( DataFlavor flavor ) {
    return DataFlavor.imageFlavor.equals( flavor );
  }

  public Object getTransferData( DataFlavor flavor )
      throws UnsupportedFlavorException {
    if (!isDataFlavorSupported( flavor ))
      throw new UnsupportedFlavorException( flavor );

    return image;
  }
}
