package alex.home.shop.utils.img.size;

public interface SizeConverter {

    byte[] convert(byte[] img, int size, boolean resize, String extension);
}
