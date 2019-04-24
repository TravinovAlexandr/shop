package alex.home.shop.utils.img.write;

import alex.home.shop.annotation.NullableAll;
import alex.home.shop.utils.DateUtil;
import alex.home.shop.utils.img.size.SizeConverter;
import alex.home.shop.utils.img.size.SizeScarlConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nullable;

public class ImageFSWriter extends ImageWriter {
    
    public ImageFSWriter() {}
    
    @Override @NullableAll 
    public String writeImageAndGetUrl(byte[] img, Integer convertSize, String extension, SizeConverter converter, Boolean isResize) {
        if (img != null) {
            converter = (converter == null) ? new SizeScarlConverter() : converter;
            extension = (extension == null) ? defExctension : extension;
            convertSize = (convertSize == null) ? defImgSize : convertSize;
            isResize = (isResize == null) ? false : isResize;
            
            byte[] resizeImage = converter.convert(img, convertSize, isResize, extension);
            
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
    
    private String defineName(String extension) {
        return "/" + System.currentTimeMillis() + (extension == null ? defExctension : extension);
    }
    
    @Nullable
    private synchronized String writeFileAndGetUrl(byte[] img, String fileName) {
        try {
            if (filesCount >= defFilesInSubDir) {
                System.out.println(1);
                filesCount = 0;
                currentChildDirrectory = "/" + DateUtil.getPathCurrentDateTime();
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
                    
            return currentChildDirrectory + fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteImage(String url) {
        if (url == null) {
            return;
        }

        try {
            Path file = Paths.get(url);
            if (Files.deleteIfExists(file)) {
                System.out.println("FileNotFoundException.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
