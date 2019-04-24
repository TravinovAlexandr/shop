package alex.home.shop.utils.img.size;

import alex.home.shop.utils.ValidationUtil;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;

public class SizeScarlConverter implements SizeConverter {

    @Override @Nullable 
    public byte[] convert(byte[] img, int size, boolean isResize, String extension) {
        if (!ValidationUtil.validateNull(img, size, extension)) { 
            try {
                InputStream in = new ByteArrayInputStream(img);
                BufferedImage image = ImageIO.read(in);
                if (isResize) {
                    image = Scalr.resize(image,Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, size, Scalr.OP_ANTIALIAS);
                }
                
                if (extension.contains("jp")) {
                    BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    ColorConvertOp op = new ColorConvertOp(null);
                    op.filter(image, rgbImage);
                    image = rgbImage;
                }
                
                byte[] newImage;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, (extension.charAt(0) == '.') ?  extension.substring(1) : extension, baos);
                    newImage = baos.toByteArray();
                    baos.flush();
                }
                return newImage;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
}