package alex.home.shop.utils;

import alex.home.shop.annotation.Default;
import alex.home.shop.annotation.Length;
import alex.home.shop.annotation.NotNullVal;
import alex.home.shop.annotation.NotUndefined;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class ValidationUtil {

    private static boolean returN(RuntimeException ex) {
        if (ex == null) {
            return false;
        }

        throw ex;
    }

    public static boolean validateFields(Object entity) {
        return validateFields(entity, null);
    }

    public static boolean validateFields(Object entity, RuntimeException exept) {
        if (entity == null) {
            return returN(exept);
        }

        int i  = 0, j = 0, sharedSize;
        Field[] fields = entity.getClass().getDeclaredFields();
        Field[] supperClassFields = entity.getClass().getSuperclass().getDeclaredFields();
        
        if (fields != null) {
            i = fields.length;
        }
        
        if (supperClassFields != null) {
            j = supperClassFields.length;
        }
        
        sharedSize = i + j;
        Field[] sharedFields = new Field[sharedSize];
        
        for (int k = 0; k < sharedFields.length; k++) {
            if (k < i) {
                sharedFields[k] = fields[k];
            }
            
            if (j != 0 && sharedSize-- > i) {
                sharedFields[sharedSize] = supperClassFields[--j];
            }
        }
        
        Annotation[] anotations;
        int size;

        for (Field field : sharedFields) {
            try {
                field.setAccessible(true);
                anotations = field.getDeclaredAnnotations();

                if (anotations != null) {
                    for (Annotation annotation : anotations) {
                        try {
                            if (annotation.annotationType() == Default.class) {
                                Default def = (Default) annotation;

                                if (field.getType() == Short.class || field.getType() == short.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, Short.parseShort(def.value()));
                                    }  
                                } else if (field.getType() == Integer.class || field.getType() == int.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, Integer.parseInt(def.value()));
                                    }
                                } else if (field.getType() == Long.class || field.getType() == long.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, Long.parseLong(def.value()));
                                    }
                                } else if (field.getType() == Float.class || field.getType() == float.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, Float.parseFloat(def.value()));
                                    }
                                } else if (field.getType() == Double.class || field.getType() == double.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, Double.parseDouble(def.value()));
                                    }
                                } else if (field.getType() == Character.class || field.getType() == char.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, def.value().charAt(0));
                                    }
                                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, Boolean.parseBoolean(def.value()));
                                    }
                                } else if (field.getType() == Byte.class || field.getType() == byte.class) {
                                    if (field.get(entity) == null && !def.value().equals("")) {
                                        field.set(entity, Byte.parseByte(def.value()));
                                    }
                                } else if (field.getType() == String.class) {
                                    if (field.get(entity) == null) {
                                        field.set(entity, def.value());
                                    }
                                } else if (field.getType() == Date.class || field.getType() == Timestamp.class) {
                                    if (field.get(entity) == null) {
                                        field.set(entity, DateUtil.getCurrentTimestamp());
                                    }
                                }
                            } else if (annotation.annotationType() == NotNullVal.class) {
                                if (field.get(entity) == null) {
                                    return returN(exept);
                                }
                            } else if (annotation.annotationType() == Length.class) {
                                Length length = (Length) annotation;
                                if (field.get(entity) == null) {
                                    return returN(exept);
                                }
                                
                                size = field.get(entity).toString().length();

                                if (field.getType() == String.class && length.min() > size || length.max() < size) {
                                    return returN(exept);
                                } else if (field.get(entity) instanceof Collection) {
                                    size = ((Collection) field.get(entity)).size();

                                    if (size < length.structMin() || size > length.structMax()) {
                                        return returN(exept);
                                    }  
                                } else if (field.get(entity) instanceof Map) {
                                    size = ((Map) field.get(entity)).size();

                                    if (size < length.structMin() || size > length.structMax()) {
                                        return returN(exept);
                                    }
                                } else if (field.get(entity).getClass().getName().charAt(0) == '[') {
                                    size = Array.getLength(field.get(entity));

                                    if (size < length.structMin() || size > length.structMax()) {
                                        return returN(exept);
                                    }
                                }
                            } else if (annotation.annotationType() == NotUndefined.class) {
                                if (field.getType() == String.class && field.get(entity) != null && field.get(entity).equals("undefined")) {
                                    field.set(entity, null);
                                }
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                            return returN(exept);
                        }
                    }
                }
            } catch (SecurityException ex) {
                ex.printStackTrace();
            } finally {
                field.setAccessible(false);
            }
        }

        return true;
    }
    
    public static boolean validateNull(Object ... args) {
        if (args == null) {
            return false;
        }
        
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                return false;
            } 
        }
        return true;
    }
    
    public static boolean validateEmptyString(String ... args) {
        if (args == null) {
            return false;
        }
        
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null || args[i].trim().equals("")) {
                return false;
            } 
        }
        
        return true;
    }
    
    public static boolean validateAnyNullEmptyString(Object ... args) {
        if (args == null) {
            return false;
        }
        
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                return false;
            } 
            
            if (args[i].getClass() == String.class && ((String) args[i]).trim().equals("")) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean isCorrectNick(String nick) {
        return nick != null;
    }
    
    public static boolean isCorrectPassword(String password) {
        return password != null;
    }
    
    public static boolean isCorrectEmail(String email) {
        return email != null;
    }
    
    public static boolean isAuthenticated(Principal principal) {
        return principal != null;
    }
    
}
