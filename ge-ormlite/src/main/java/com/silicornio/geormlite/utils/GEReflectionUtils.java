package com.silicornio.geormlite.utils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.silicornio.geormlite.general.GEL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class GEReflectionUtils {



    /**
     * Get the field with the ID of an object
     * @param klass Class
     * @return Field field or NULL if error
     */
    public static Field getFieldId(Class klass){

        try {
            for(Field field : klass.getDeclaredFields()){
                Class type = field.getType();
                String name = field.getName();
                if(type==String.class) {
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    for (Annotation annotation : annotations) {
                        if ((annotation instanceof DatabaseField) && ((DatabaseField)annotation).id()){
                            field.setAccessible(true);
                            return field;
                        }
                    }
                }
            }
        }catch(Exception e){
            GEL.e("Exception getting field from object: " + e.toString());
        }

        return null;
    }

    /**
     * Get the name of a table of the class
     * @param klass Class
     * @return String name of the table
     */
    public static String getTableName(Class klass){

        try {
            Annotation[] annotations = klass.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof DatabaseTable){
                    return ((DatabaseTable)annotation).tableName();
                }
            }
        }catch(Exception e){
            GEL.e("Exception getting table name from object: " + e.toString());
        }

        return null;
    }

    /**
     * Get the field with the ID of an object
     * @param klass Class
     * @return Field field or NULL if error
     */
    public static Field getField(Class klass, String fieldName){

        try {
            for(Field field : klass.getDeclaredFields()){
                Class type = field.getType();
                String name = field.getName();
                if(name.equals(fieldName)) {
                    return field;
                }
            }
            Class superKlass = klass.getSuperclass();
            if(superKlass!=null){
                return getField(superKlass, fieldName);
            }
        }catch(Exception e){
            GEL.e("Exception getting field '" + fieldName + "' from class '" + klass.toString() + "': " + e.toString());
        }

        return null;
    }

    /**
     * Get the annotation with the ID of an object
     * @param klass Class
     * @return Field field or NULL if error
     */
    public static Field getAnnotation(Class klass, String sAnnotation){

        try {
            for(Annotation annotation : klass.getAnnotations()){

            }
        }catch(Exception e){
            GEL.e("Exception getting annotation value '" + sAnnotation + "' from class '" + klass.toString() + "': " + e.toString());
        }

        return null;
    }
}
