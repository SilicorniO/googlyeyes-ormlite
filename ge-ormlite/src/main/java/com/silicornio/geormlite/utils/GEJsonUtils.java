package com.silicornio.geormlite.utils;

import com.google.gson.Gson;
import com.silicornio.geormlite.general.GEL;

import java.lang.reflect.Field;

/**
 * @author silicornio
 */
public class GEJsonUtils {

    /**
     * Update the JSON data of the object received
     * @param object Object to update JSON data
     * @param jsonField Field where to store the generated JSON
     */
    public static void updateJsonData(Object object, Field jsonField){

        //check if object and jsonField are defined
        if(object==null || jsonField==null){
            return;
        }

        try {
            //clean field to not put old json inside of json
            jsonField.set(object, null);

            //convert object to json
            Gson gson = new Gson();
            String jsonText = gson.toJson(object);

            //set the json to the field
            jsonField.set(object, jsonText);

        }catch (Exception e){
            GEL.e("Exception updating JSON data of object '" + object.getClass().toString() + "'");
        }

    }

    /**
     * Convert the JSON received in an object
     * @param t T Object with data
     * @param fieldJsonData Field with JSON text
     * @return T Converted object
     */
    public static <T>T getObjectFromJsonData(T t, Field fieldJsonData){

        try {

            //prepare gson
            Gson gson = new Gson();

            //get JSON
            String jsonData = (String)fieldJsonData.get(t);

            //return object
            return (T)gson.fromJson(jsonData, t.getClass());
        }catch(Exception e){
            GEL.e("Exception getting object of class '" + t.getClass() + "' from JsonData. Check jsonData is a String variable");
        }

        return t;
    }

}
