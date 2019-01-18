package alex.home.angular.utils.img.write;

import alex.home.angular.utils.img.size.SizeConverter;
import alex.home.angular.utils.img.size.SizeScarlConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class ImageFSWriter extends ImageWriter{
    
    private final static Lock LOCK = new ReentrantLock();
    protected static int filesCount = 300;

    public ImageFSWriter() {}
    
    @Override @Nullable
    public String writeImageAndGetUrl(@Nullable byte[] img,@Nullable Integer convertSize,@Nullable String extension,@Nullable SizeConverter converter) {
        if (img != null) {
            converter = (converter == null) ? new SizeScarlConverter() : converter;
            extension = (extension != null) ? extension : defaultExctension;
            convertSize = (convertSize != null) ? convertSize : defaultImgSize;
            
            byte[] resizeImage = converter.convert(img, convertSize , extension);
            if (resizeImage == null) {
                return null;
            }
            try {
                String fileName = defineName(extension);
                return writeFileAndGetUrl(resizeImage, fileName);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    private static String defineName(@Nullable String extension) {
        return "/" + UUID.randomUUID() + ((extension != null) ? extension : defaultExctension);
    }
    
    @Nullable
    private String writeFileAndGetUrl(@NotNull byte[] img, @NotNull String fileName) {
        try {
            LOCK.lock();
            if (filesCount >= 300) {
                currentChildDirrectory = "/" + UUID.randomUUID().toString();
                filesCount = 0;
            }
            File parent = new File(rootImgDir + currentChildDirrectory);
            File file = new File(rootImgDir + currentChildDirrectory + fileName);
            if (!parent.exists()) {
                parent.mkdir();
            }
            file.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(img);
                fos.flush();
                filesCount++;
            }
            return "/img/products" + currentChildDirrectory + fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            LOCK.unlock();
        }
    }
}
