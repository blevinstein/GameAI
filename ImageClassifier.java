import java.awt.image.BufferedImage;

// An extension of a NetAdapter, mapping from images to ints
class ImageClassifier<T>
  extends NetAdapter<BufferedImage, T> {

  public ImageClassifier(Channel c, Converter<T> conv) {
    super(c, conv);
  }
}
