package alex.home.shop.task.mediator;

//package alex.home.angular.task.mediator;
//
//import static alex.home.angular.task.mediator.AsyncMethod.argsStorage;
//import alex.home.angular.utils.img.size.SizeConverter;
//import alex.home.angular.utils.img.write.ImageWriter;
//
//public class ImageWriterMediator implements AsyncMethod {
//
//    private final ImageWriter imageWriter;
//    private final TLSImageWriterArgs args;
//    
//    public ImageWriterMediator(ImageWriter imageWriter, TLSImageWriterArgs args) {
//        this.imageWriter = imageWriter;
//        this.args = args; 
//    }
//    
//    @Override
//    public void initMethodArguments() {
//        if (args == null || args.getClass() != TLSImageWriterArgs.class) {
//            throw new IllegalArgumentException(args == null ? "arg == null" : "arg != TLSImageWriterArgs.class");
//        }
//        
//        argsStorage.set(args);
//    }
//
//    @Override
//    public Object usyncCall() {
//        TLSImageWriterArgs tlsi = (TLSImageWriterArgs) argsStorage.get();
//        
//        if (tlsi == null) {
//            throw new IllegalStateException("TLS init in another thread.");
//        }
//        
//        if (tlsi.bytes != null && tlsi.path == null) {
//            return imageWriter.writeImageAndGetUrl(tlsi.bytes, tlsi.convertSize, tlsi.extension, tlsi.converter);
//        } else if (tlsi.bytes == null && tlsi.path != null) {
//            imageWriter.deleteImage(imageWriter.getRootImgDir() + tlsi.path);
//            return VOID_TYPE;
//        } else {
//            throw new IllegalStateException("Algorithm incorrect realization.");
//        }
//    }
//
//    @Override
//    public boolean isArgsNull() {
//        return args == null;
//    }
//
//    @Override
//    public boolean isCallObjNull() {
//        return imageWriter == null;
//    }
//    
//    public static class TLSImageWriterArgs {
//        
//        public byte[] bytes;
//        public String path;
//        public Integer convertSize;
//        public String extension; 
//        public SizeConverter converter;
//        
//        private TLSImageWriterArgs(byte[] bytes, String path, Integer convertSize, String extension, SizeConverter converter) {
//            this.bytes = bytes;
//            this.path = path;
//            this.convertSize = convertSize;
//            this.extension = extension;
//            this.converter = converter;
//        }
//        
//        public static TLSImageWriterArgs create(byte[] bytes, String path, Integer convertSize, String extension, SizeConverter converter) {
//            return new TLSImageWriterArgs(bytes, path, convertSize,  extension, converter);
//        }
//        
//    }
//}
