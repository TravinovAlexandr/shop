package alex.home.angular.utils.img.write;

import alex.home.angular.task.AsyncMethod;
import alex.home.angular.utils.img.size.SizeConverter;
import alex.home.angular.utils.img.write.ImageWriter.TLSImageWriterArgs;

public abstract class ImageWriter implements AsyncMethod {
    
    protected int filesCount = 300;
    protected int filesQuantInSubdirrectory = 300;
    protected volatile int defaultImgSize = 200;
    protected String rootImgDir;
    protected String cloudUrl;
    protected String currentChildDirrectory;
    protected String imgUrlDbPreffix = "/img/products";
    protected volatile String defaultExctension = ".png";
    
    public abstract String writeImageAndGetUrl(byte[] img, Integer convertSize, String extension, SizeConverter converter);
    
    public abstract void deleteImage(String url);
    
    public ImageWriter confRootImgDir(String var) {
        rootImgDir = var;
        return this;
    }
    
    public ImageWriter confCloudURL(String var) {
        cloudUrl = var;
        return this;
    }
    
    public ImageWriter confCurrentChildDir(String var) {
        currentChildDirrectory = var;
        return this;
    }
    
    public ImageWriter confExctention(String var) {
        defaultExctension = var;
        return this;
    }
    
    public ImageWriter confSize(int var) {
        defaultImgSize = var;
        return this;
    }
    
    public  String getImgRootDir() {
        return rootImgDir;
    }

    public static class TLSImageWriterArgs {
        
        public byte[] bytes;
        public String path;
        public Integer convertSize;
        public String extension; 
        public SizeConverter converter;
        
        public TLSImageWriterArgs(byte[] bytes, String path, Integer convertSize, String extension, SizeConverter converter) {
            this.bytes = bytes;
            this.path = path;
            this.convertSize = convertSize;
            this.extension = extension;
            this.converter = converter;
        }
        
        public static TLSImageWriterArgs create(byte[] bytes, String path, Integer convertSize, String extension, SizeConverter converter) {
            return new TLSImageWriterArgs(bytes, path, convertSize,  extension, converter);
        }
        
    }
    
}
