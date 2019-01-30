package alex.home.angular.utils.img.write;

import alex.home.angular.utils.img.size.SizeConverter;

public abstract class ImageWriter {
    
    protected int filesCount = 300;
    protected int filesQuantInSubdirrectory = 300;
    protected volatile int defaultImgSize = 200;
    protected String rootImgDir;
    protected String cloudUrl;
    protected String currentChildDirrectory;
    protected String imgUrlDbPreffix = "/img/products";
    protected volatile String defaultExctension = ".png";
    
    public abstract String writeImageAndGetUrl(byte[] img, Integer convertSize, String extension, SizeConverter converter);
    
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
    
    public String getImgRootDir() {
        return rootImgDir;
    }
}
