package alex.home.angular.utils.img.write;

import alex.home.angular.utils.img.size.SizeConverter;
import alex.home.angular.utils.img.size.SizeScarlConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class ImageFSWriter extends ImageWriter {
    
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
    
    private String defineName(@Nullable String extension) {
        return "/" + UUID.randomUUID() + ((extension != null) ? extension : defaultExctension);
    }
    
    @Nullable
    private synchronized String writeFileAndGetUrl(byte[] img, String fileName) {
        try {
            if (filesCount >= filesQuantInSubdirrectory) {
                currentChildDirrectory = "/" + UUID.randomUUID().toString();
            }
            
            File parent = new File(rootImgDir + currentChildDirrectory);
            File file = new File(rootImgDir + currentChildDirrectory + fileName);
            
            if (!parent.exists()) {
                parent.mkdir();
            }
            
            file.createNewFile();
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                filesCount++;
                fos.write(img);
                fos.flush();
            }
                    
            return imgUrlDbPreffix + currentChildDirrectory + fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
