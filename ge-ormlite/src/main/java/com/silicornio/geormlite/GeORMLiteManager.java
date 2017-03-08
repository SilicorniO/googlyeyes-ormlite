package com.silicornio.geormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.silicornio.geormlite.general.GEL;
import com.silicornio.geormlite.utils.GEDateUtils;
import com.silicornio.geormlite.utils.GEReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Database manager
 * Contains methods to manage and work with the database
 */
public abstract class GeORMLiteManager {

    public static final String DATETIME_FORMAT_ORMLITE = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    public static final String DATE_FORMAT_ORMLITE = "yyyy-MM-dd";

    //data to generate daos and open database
    protected static Class[] classes;
    protected static String databaseName;
    protected static int databaseVersion;

    //database helper
    private OrmLiteSqliteOpenHelper mDatabaseHelper = null;

    /** Instance **/
    private static GeORMLiteManager mInstance;

    //daos
    private RuntimeExceptionDao[] mDaos;
    private Field[] mDaosId;

    private GeORMLiteManager(){

    }

    public GeORMLiteManager(Context context){
        classes = getClasses();
        databaseName = getDatabaseName();
        databaseVersion = getDatabaseVersion();
        mDatabaseHelper = OpenHelperManager.getHelper(context, GeOrmLiteHelper.class);
        generateDaos();
    }

    //configuration to apply
    public abstract Class[] getClasses();
    public abstract String getDatabaseName();
    public abstract int getDatabaseVersion();
    public abstract void onGeUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion);

    public static void onGeDbUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion){
        onGeDbUpgrade(database, connectionSource, oldVersion, newVersion);
    }

    //------ CONSTRUCTION -----

    /**
     * Generate DAOS of database with the list of given classes
     */
    private void generateDaos(){

        //initialize arrays
        mDaos = new RuntimeExceptionDao[classes.length];
        mDaosId = new Field[classes.length];

        mDaos = new RuntimeExceptionDao[classes.length];
        for(int i = 0; i< classes.length; i++){
            mDaos[i] = mDatabaseHelper.getRuntimeExceptionDao(classes[i]);
            mDaosId[i] = GEReflectionUtils.getFieldId(classes[i]);
        }
    }

    /**
     * Destroy instance
     */
    public void destroy(){

        mInstance = null;
    }

    //----- INTERNAL CORE ------

    //-------------- GENERAL -------------

    private int getIndexClass(Class oClass) throws IllegalArgumentException{
        for(int i=0; i<mDaos.length; i++){
            if(classes[i].equals(oClass)){
                return i;
            }
        }

        throw new IllegalArgumentException("Dao NOT FOUND for class: " + oClass.toString());
    }

    /**
     * Get DAO of an object
     * @param o Object to check
     * @return RuntimeExceptionDao
     * @throws Exception if it is not found
     */
    private RuntimeExceptionDao getDao(Object o) throws IllegalArgumentException{
        return getDao(o.getClass());
    }

    /**
     * Get DAO of an object
     * @param c Class to check
     * @return RuntimeExceptionDao
     * @throws Exception if it is not found
     */
    private RuntimeExceptionDao getDao(Class c) throws IllegalArgumentException{
        return mDaos[getIndexClass(c)];
    }

    /**
     * Get DAO of an object
     * @param o Object to check
     * @return RuntimeExceptionDao
     * @throws Exception if it is not found
     */
    private String getId(Object o) throws IllegalArgumentException{
        return (String)getValue(mDaosId[getIndexClass(o.getClass())], o);
    }

    /**
     * Get the value of an object with the name of the field
     * @param obj Object
     * @param fieldName String name of the field
     * @return Object return or NULL if error
     */
    private static Object getFieldValue(Object obj, String fieldName){
        try {
            Field field = GEReflectionUtils.getField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return getValue(field, obj);
        }catch(Exception e){
            GEL.e("Exception getting value '" + fieldName + "' from an object ' " + obj.getClass().toString() + "': " + e.toString());
        }

        return null;
    }

    /**
     * Get a string Id from a fields received
     * @param field Field
     * @param o Object to get the value
     * @return String with the value
     */
    private static Object getValue(Field field, Object o){
        try{
            return field.get(o);
        }catch(Exception e){
            GEL.e("Exception getting value from an object: " + e.toString());
        }

        return null;
    }

    //------ HELPER METHODS -----

    /**
     * Add an object
     * @param object Object to add
     */
    public void addOrUpdate(Object object){
        //first try to update
        if(!update(object)){
            add(object);
        }
    }

    /**
     * Add an object
     * @param object Object to add
     */
    public void add(Object object){
        try {
            getDao(object).create(object);
        }catch(Exception e){
            GEL.e("Exception adding object: " + e.toString());
        }
    }

    /**
     * Get all objects
     * @param t Object to get All
     * @return List of objects
     */
    public <T> List<T> getAll(Class<T> t){
        return getAll(t, null, false, 0);
    }

    /**
     * Get all objects
     * @param t Object to get all
     * @param orderBy String name of the variable to order
     * @param ascending boolean TRUE ascend, FALSE not
     * @param limit int maximum number of objects to return
     * @return List of objects
     */
    public <T> List<T> getAll(Class<T> t, String orderBy, boolean ascending, long limit){
        try {
            QueryBuilder<T, String> queryBuilder = getDao(t).queryBuilder();
            if(orderBy!=null) {
                queryBuilder.orderBy(orderBy, ascending);
                if(limit>0) {
                    queryBuilder.limit(limit);
                }
            }
            return queryBuilder.query();
        }catch(Exception e){
            GEL.e("Exception getting all objects: " + e.toString());
        }

        //error getting all objects
        return null;
    }

    /**
     * Get all objects
     * @param t Object to get all
     */
    public <T> T getFirst(Class<T> t){
        try {
            List<T> listT = getAll(t, null, false, 1);
            if(listT.size()>0){
                return listT.get(0);
            }
        }catch(Exception e){
            GEL.e("Exception getting first object: " + e.toString());
        }

        //error getting all objects
        return null;
    }

    /**
     * Get all objects
     * @param klass Class of the object
     * @param id String identifier
     */
    public <T> T getObjectById(Class klass, String id){
        try {
            return (T)getDao(klass).queryForId(id);
        }catch(Exception e){
            GEL.e("Exception getting object by id: " + e.toString());
        }

        //error getting all object
        return null;
    }

    /**
     * Get all objects
     * @param t Object with Id defined
     */
    public <T> T getObjectById(T t){
        try {
            return (T)getDao(t).queryForId(getId(t));
        }catch(Exception e){
            GEL.e("Exception getting object by id: " + e.toString());
        }

        //error getting all object
        return null;
    }

    /**
     * Get the first object of the list of objects with a specific value in a field
     * @param t T instance of object
     * @param fields String name of the fields
     * @return List of objects or null if ERROR
     */
    public <T> T getFirstObjectByFields(T t, String fields){
        try {

            List<T> listT = getObjectsByFields(t, fields);
            if(listT.size()>0){
                return listT.get(0);
            }

        }catch(Exception e){
            GEL.e("Exception getting first object by id: " + e.toString());
        }

        //error getting all object
        return null;
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param t T instance of object
     * @param fields String list of fields separated by commas
     * @return List of objects or null if ERROR
     */
    public <T> List<T> getObjectsByFields(T t, String fields){
        return getObjectsByFields(t, fields, null, false);
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param klass Class<T> class of object
     * @param fields String list of fields separated by commas
     * @return List of objects or null if ERROR
     */
    public <T> List<T> getObjectsByFields(Class<T> klass, String fields){
        return getObjectsByFields(klass, fields, null, false);
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param t T instance of object
     * @param fields String list of fields separated by commas
     * @param orderBy String value to order or null if not necessary
     * @param ascending boolean TRUE for order ascending, FALSE descending
     * @return List of objects or null if ERROR
     */
    public <T> List<T> getObjectsByFields(T t, String fields, String orderBy, boolean ascending){
        try{
            return getWhereByFields(t, fields, orderBy, ascending).query();
        }catch(Exception e){
            GEL.e("Exception getting objects by fields: " + e.toString());
        }

        return null;
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param klass Class<T> class of object
     * @param fields String list of fields separated by commas
     * @param orderBy String value to order or null if not necessary
     * @param ascending boolean TRUE for order ascending, FALSE descending
     * @return List of objects or null if ERROR
     */
    public <T> List<T> getObjectsByFields(Class<T> klass, String fields, String orderBy, boolean ascending){
        try{
            return getWhereByFields(klass, fields, orderBy, ascending).query();
        }catch(Exception e){
            GEL.e("Exception getting objects by fields: " + e.toString());
        }

        return null;
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param t T instance of object
     * @param fields String list of fields separated by commas
     * @param orderBy String value to order or null if not necessary
     * @param ascending boolean TRUE for order ascending, FALSE descending
     * @return List of objects or null if ERROR
     */
    public <T> Where getWhereByFields(T t, String fields, String orderBy, boolean ascending){
        QueryBuilder<T, String> queryBuilder = getDao(t).queryBuilder();
        Where where = whereByFields(queryBuilder, t, fields);
        if(orderBy!=null) {
            queryBuilder.orderBy(orderBy, ascending);
        }
        return where;
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param klass Class<T> class of object
     * @param fields String list of fields separated by commas
     * @param orderBy String value to order or null if not necessary
     * @param ascending boolean TRUE for order ascending, FALSE descending
     * @return List of objects or null if ERROR
     */
    public <T> Where getWhereByFields(Class<T> klass, String fields, String orderBy, boolean ascending){
        QueryBuilder<T, String> queryBuilder = getDao(klass).queryBuilder();
        Where where = whereByFields(queryBuilder, null, fields);
        if(orderBy!=null) {
            queryBuilder.orderBy(orderBy, ascending);
        }
        return where;
    }

    /**
     * Get the where statement to get a list of objects with a specific value in a field
     * @param t T instance of object
     * @param fields String list of fields separated by commas
     * @return List of objects or null if ERROR
     */
    public <T> Where whereByFields(StatementBuilder builder, T t, String fields){
        try {

            Where where = builder.where();

            //generate the WHERE statement with fields separated by commas
            String[] aFields = fields.split(",");
            for(int i=0; i<aFields.length; i++){
                String field = aFields[i].replace(" ", "");

                //check if field has a value
                Pattern pattern = Pattern.compile("[=<>]+");
                String[] aField = pattern.split(field);
                Object oValue;
                String operation = "=";
                if(aField.length==2){
                    operation = field.substring(aField[0].length(), field.length()-aField[1].length());
                    oValue = aField[1];
                    field = aField[0];
                }else {
                    if(t!=null) {
                        oValue = getFieldValue(t, field);
                    }else{
                        oValue = null;
                        GEL.e("Trying to get the value of the field '" + field + "' but not sending the object or the value with '='");
                    }
                }

                where = applyWhere(where, operation, field, oValue, i==0);
            }

            return where;

        }catch(Exception e){
            GEL.e("Exception getting object by fields: " + e.toString());
        }

        //error getting all object
        return null;
    }

    private Where applyWhere(Where where, String operation, String field, Object value, boolean firstValue){
        try {
            if (firstValue) {
                if(operation.equals("<")){
                    where = where.lt(field, value);
                }else if(operation.equals(">")){
                    where = where.gt(field, value);
                }else if(operation.equals("<=")){
                    where = where.le(field, value);
                }else if(operation.equals(">=")){
                    where = where.ge(field, value);
                }else {
                    where = where.eq(field, value);
                }
            } else {
                if(operation.equals("<")){
                    where = where.and().lt(field, value);
                }else if(operation.equals(">")){
                    where = where.and().gt(field, value);
                }else if(operation.equals("<=")){
                    where = where.and().le(field, value);
                }else if(operation.equals(">=")){
                    where = where.and().ge(field, value);
                }else {
                    where = where.and().eq(field, value);
                }
            }
            return where;
        }catch (Exception e){
            GEL.e("Exception applying where with field and value: " + e.toString());
        }
        return null;
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param t T instance of object
     * @param fields String name of the fields
     * @return List of objects or null if ERROR
     */
    public <T> int getNumObjectsByFields(T t, String fields){
        try{
            return (int)getWhereByFields(t, fields, null, false).countOf();
        }catch(Exception e){
            GEL.e("Exception getting objects by fields: " + e.toString());
        }

        //error getting all object
        return 0;
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param klass Class<T> to get dao
     * @param fields String name of the fields
     * @return List of objects or null if ERROR
     */
    public <T> int getNumObjectsByFields(Class<T> klass, String fields){
        try{
            return (int)getWhereByFields(klass, fields, null, false).countOf();
        }catch(Exception e){
            GEL.e("Exception getting objects by fields: " + e.toString());
        }

        //error getting all object
        return 0;
    }

    /**
     * Get a list of objects with a specific value in a field
     * @param t T instance of object to get the table
     * @param values List<Object> list of objects
     * @param field String name of the field
     * @return List of objects or null if ERROR
     */
    public <T> List<T> getObjectsByField(T t, List<String> values, String field){
        try {

            //check if no values to return empty list
            if(values.size()==0){
                return new ArrayList<>();
            }

            QueryBuilder<T, String> queryBuilder = getDao(t).queryBuilder();
            Where where = queryBuilder.where();
            boolean first = true;
            for(String s : values){
                if(first) {
                    where = where.eq(field, s);
                    first = false;
                }else{
                    where = where.or().eq(field, s);
                }
            }

            return where.query();

        }catch(Exception e){
            GEL.e("Exception getting object by field with list: " + e.toString());
        }

        //error getting all object
        return null;
    }

    /**
     * Get a list of attendances between dates given
     * @param cStart Calendar with date start
     * @param cEnd Calendar with date end
     * @return List<T>
     */
    public <T> List<T> getObjectsBetweenDates(T t, String value, Calendar cStart, Calendar cEnd){

        try{

            String sql =  "SELECT * " +
                    "FROM " + GEReflectionUtils.getTableName(t.getClass())  + " " +
                    "WHERE " + value +" BETWEEN DATE('" + GEDateUtils.formatDate(cStart, DATE_FORMAT_ORMLITE) + "') " +
                    "AND DATE('" + GEDateUtils.formatDate(cEnd, DATE_FORMAT_ORMLITE) + "')";

            RuntimeExceptionDao dao = getDao(t);
            GenericRawResults<T> rawResults = dao.queryRaw(sql, dao.getRawRowMapper());
            return rawResults.getResults();

        }catch(Exception e){
            GEL.e("Exception getting object between dates average: " + e.toString());
        }

        return null;
    }

    /**
     * Get all objects
     * @param object Object with Id defined
     */
    public boolean update(Object object){
        try {
            return getDao(object).update(object)>0;
        }catch(Exception e){
            GEL.e("Exception updating object: " + e.toString());
        }

        //error getting all object
        return false;
    }

    /**
     * Update the id of an object
     * @param object Object to update
     * @param newId String identifier to set
     * @return boolean TRUE if updated, FALSE if there was an error
     */
    public boolean updateId(Object object, String newId){

        try{
            return getDao(object).updateId(object, newId)>0;
        }catch(Exception e){
            GEL.e("Exception updating id of an object: " + e.toString());
        }

        //error updating
        return false;
    }


    /**
     * Delete an object with its ID
     * @param object Object to delete
     * @return boolean TRUE if deleted, FALSE if not
     */
    public boolean delete(Object object){
        try {
            return getDao(object).deleteById(getId(object))>0;
        }catch(Exception e){
            GEL.e("Exception deleting object by Id: " + e.toString());
        }

        //not deleted
        return false;
    }

    /**
     * Remove a list of objects with a specific value in a field
     * @param t T instance of object
     * @param fields String list of fields separated by commas
     */
    public <T> int deleteObjectsByFields(T t, String fields){
        try{
            DeleteBuilder<T, String> deleteBuilder = getDao(t).deleteBuilder();
            whereByFields(deleteBuilder, t, fields);
            return deleteBuilder.delete();
        }catch(Exception e){
            GEL.e("Exception deleting objects by fields: " + e.toString());
        }

        //nothing deleted
        return 0;
    }

    /**
     * Remove a list of objects with a specific value in a field
     * @param klass Class<T> to get dao
     * @param fields String list of fields separated by commas
     */
    public <T> int deleteObjectsByFields(Class<T> klass, String fields){
        try{
            DeleteBuilder<T, String> deleteBuilder = getDao(klass).deleteBuilder();
            whereByFields(deleteBuilder, null, fields);
            return deleteBuilder.delete();
        }catch(Exception e){
            GEL.e("Exception deleting objects by fields: " + e.toString());
        }

        //nothing deleted
        return 0;
    }

    /**
     * Clean all table
     * @param c Class type to delete
     * @return int number of objects deleted
     */
    public int deleteAll(Class c){
        try {
            return getDao(c).deleteBuilder().delete();
        }catch(Exception e){
            GEL.e("Exception deleting all objects: " + e.toString());
        }

        //nothing deleted
        return 0;
    }

    /**
     * Delete all from all tables
     */
    public void deleteAllTables(){
        try {
            for(RuntimeExceptionDao dao : mDaos) {
                dao.deleteBuilder().delete();
            }
        }catch(Exception e){
            GEL.e("Exception deleting all objects from all tables: " + e.toString());
        }
    }

    //----- HELPER -----



}
