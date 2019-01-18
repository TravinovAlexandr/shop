package alex.home.angular.utils.img.size;

import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;

public class SizeScarlConverter implements SizeConverter {

    @Override @Nullable 
    public byte[] convert(@Nullable byte[] img, @Nullable Integer size, @Nullable String extension) {
        if (img != null) {  
            try {
                InputStream in = new ByteArrayInputStream(img);
                BufferedImage image = ImageIO.read(in);
                BufferedImage scaledImg = Scalr.resize(image,
                    Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, size, Scalr.OP_ANTIALIAS);
                byte[] newImage;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(scaledImg, extension , baos);
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