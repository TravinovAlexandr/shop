package alex.home.shop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Length {
    
    public int min() default 0;
    public int max() default Integer.MAX_VALUE;
    public int structMin() default 0;
    public int structMax() default Integer.MAX_VALUE;
}
