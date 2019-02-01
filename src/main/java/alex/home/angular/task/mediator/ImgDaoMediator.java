package alex.home.angular.task.mediator;

import alex.home.angular.dao.ImgDao;

public class ImgDaoMediator implements AsyncMethod {

    private final ImgDao imgDao;
    private final ImgDaoArgs args;
    
    public ImgDaoMediator(ImgDao imgDao, ImgDaoArgs args) {
        this.imgDao = imgDao;
        this.args = args;
    }
    
    @Override
    public void initMethodArguments() {
        
        if (args == null || args.getClass() != ImgDaoArgs.class || args.algoName == null) {
            throw new IllegalArgumentException(args == null ? "arg == null" : args.getClass() != ImgDaoArgs.class  
                    ? "arg != ImgDaoArgs.class" : " algoName == null");
        }
        
        argsStorage.set(args);
    }

    @Override
    public Object usyncCall() {
        ImgDaoArgs tlsi = (ImgDaoArgs) argsStorage.get();
        
        if (tlsi == null) {
            throw new IllegalStateException("TLS init in another thread.");
        }
        
        if (args.algoName.equals("IMG_SELECT") && args.imgId != null) {
            return imgDao.selectImgById(tlsi.imgId);
        } else if (args.algoName.equals("IMG_UPDATE") && args.imgId != null && args.url != null) {
            imgDao.updateImg(tlsi.imgId, tlsi.url);
            return VOID_TYPE;
        } else {
            throw new IllegalStateException("Algorithm incorrect realization.");
        }
    }

    @Override
    public boolean isArgsNull() {
        return args == null;
    }

    @Override
    public boolean isCallObjNull() {
        return imgDao == null;
    }
    
    public static class ImgDaoArgs {
        
        public static final String IMG_SELECT = "IMG_SELECT";
        public static final String IMG_UPDATE = "IMG_UPDATE";
        
        private String algoName;
        private String url;
        private Long imgId;
        
        private ImgDaoArgs(String algoName, Long imgId) {
            this.algoName = algoName;
            this.imgId = imgId;
        }
        
        private ImgDaoArgs(String algoName, Long imgId, String url) {
            this.algoName = algoName;
            this.imgId = imgId;
            this.url = url;
        }
        
        public static final ImgDaoArgs create(String algoName, Long imgId) {
            return new ImgDaoArgs(algoName, imgId);
        }
        
        public static final ImgDaoArgs create(String algoName, Long imgId, String url) {
            return new ImgDaoArgs(algoName, imgId, url);
        }
        
    }
    
}
