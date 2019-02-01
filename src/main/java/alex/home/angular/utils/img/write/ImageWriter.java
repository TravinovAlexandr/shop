package alex.home.angular.utils.img.write;

import alex.home.angular.utils.img.size.SizeConverter;

public abstract class ImageWriter {
    
    protected int filesCount = 300;
    protected int defFilesInSubDir = 300;
    protected volatile int defImgSize = 200;
    protected String rootImgDir;
    protected String cloudUrl;
    protected String currentChildDirrectory;
    protected String imgUrlDbPreffix = "/img/products";
    protected volatile String defExctension = ".png";
    
    public abstract String writeImageAndGetUrl(byte[] img, Integer convertSize, String extension, SizeConverter converter);
    
    public abstract void deleteImage(String url);

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }
    
    public int getDefFilesInSubDir() {
        return defFilesInSubDir;
    }

    public void setDefFilesInSubDir(int defFilesInSubDir) {
        this.defFilesInSubDir = defFilesInSubDir;
    }

    public int getDefImgSize() {
        return defImgSize;
    }

    public void setDefImgSize(int defImgSize) {
        this.defImgSize = defImgSize;
    }

    public String getRootImgDir() {
        return rootImgDir;
    }

    public void setRootImgDir(String rootImgDir) {
        this.rootImgDir = rootImgDir;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public String getCurrentChildDirrectory() {
        return currentChildDirrectory;
    }

    public void setCurrentChildDirrectory(String currentChildDirrectory) {
        this.currentChildDirrectory = currentChildDirrectory;
    }

    public String getImgUrlDbPreffix() {
        return imgUrlDbPreffix;
    }

    public void setImgUrlDbPreffix(String imgUrlDbPreffix) {
        this.imgUrlDbPreffix = imgUrlDbPreffix;
    }

    public String getDefExctension() {
        return defExctension;
    }

    public void setDefExctension(String defExctension) {
        this.defExctension = defExctension;
    }
    
}
