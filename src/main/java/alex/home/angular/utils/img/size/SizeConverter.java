package alex.home.angular.utils.img.size;

public interface SizeConverter {

    byte[] convert(byte[] img, Integer size, String extension);
}
