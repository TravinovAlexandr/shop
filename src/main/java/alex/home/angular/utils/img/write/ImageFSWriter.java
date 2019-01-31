package alex.home.angular.utils.img.write;

import alex.home.angular.utils.img.size.SizeConverter;
import alex.home.angular.utils.img.size.SizeScarlConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;

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

    @Override
    public void deleteImage(String url) {
        if (url == null) {
            System.out.println("url == null.");
            return;
        }
        
        File file = new File(url);
        
        if (!file.exists()) {
            System.out.println("FileNotFoundException.");
            return;
        } else if (!file.canWrite()) {
            System.out.println("Permission denied.");
            return;
        } else if (!file.isFile()) {
            System.out.println("Dirrectory.");
            return;
        }
        
        if (!file.delete()) {
            System.out.println("File have not deleted.");
        }
        
    }

    @Override
    public Object usyncCall() {
        TLSImageWriterArgs tlsi = (TLSImageWriterArgs) argsStorage.get();
        
        if (tlsi.bytes != null && tlsi.path == null) {
            return writeImageAndGetUrl(tlsi.bytes, tlsi.convertSize, tlsi.extension, tlsi.converter);
        } else if (tlsi.bytes == null && tlsi.path != null) {
            deleteImage(rootImgDir + tlsi.path);
            return "VOID";
        } else {
            throw new IllegalStateException("Arguments based algorithm is incorrect.");
        }
    }

    @Override
    public void initMethodArguments(Object args) {
        if (args == null || args.getClass() != TLSImageWriterArgs.class) {
            throw new IllegalArgumentException("Argument != TLSImageWriterArgs.class");
        }
        
        argsStorage.set(args);
    }
}
