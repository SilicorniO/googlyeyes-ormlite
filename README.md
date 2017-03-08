# googlyeyes-ormlite
Extension for ORMLite for Android

## Features
 * Creation of tables automatically with a list of classes
 * Typical actions automatically done with methods: get, add, edit, delete.
 * Can store classes as JSON if it will not be necessary to work with them
 
## Dependencies
 * ORMLite
 * Gson

##Installation

You can find the latest version of the library on jCenter repository.

### For Gradle users

In your `build.gradle` you should declare the jCenter repository into `repositories` section:
```gradle
   repositories {
       jcenter()
   }
```
Include the library as dependency:
```gradle
compile 'com.silicornio:ge-ormlite:0.2.0'
```

### For Maven users
```maven
<dependency>
  <groupId>com.silicornio</groupId>
  <artifactId>ge-ormlite</artifactId>
  <version>0.2.0</version>
  <type>pom</type>
</dependency>
```

##Usage

1. Create a Database Manager than extends from GeORMLiteManager. This class should be a shared instance to not open each time the database:

  * getClasses() - Array of classes to create as tables. They should have @DatabaseTable annotation from Ormlite.
  * getDatabaseName() - Name of the database.
  * getDatabaseVersion() - Version of the database, increment with each change.
  * onGeUpgrade() - Called when database was upgraded because of new version. Check OrmLite library, it is the same function.
 
   ```java
      public class DBManager extends GeOrmLiteManager {

    public static final String DATABASE_NAME = "GeOrmLiteExample";
    private static final int DATABASE_VERSION = 1;

    /** Instance of DBManager **/
    private static DBManager mInstance;

    private DBManager(Context context){
        super(context);
    }

    public static DBManager getInstance(Context context){
        if(mInstance==null){
            mInstance = new DBManager(context);
        }
        return mInstance;
    }

    @Override
    public Class[] getClasses() {
        return new Class[]{
                Item.class
        };
    }

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    @Override
    public void onGeUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
   ```

2. Create or configure all the classes used as tables.

  ```java
  @DatabaseTable
public class Item {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public boolean flag;

    @DatabaseField
    public String text;

    @DatabaseField
    public int numberInt;

    @DatabaseField
    public float numberFloat;

    @DatabaseField
    public double numberDouble;

    @DatabaseField
    @GeDatabaseFieldJson
    public String jsonData;

}
   ```
   
3. Now you can execute actions directly:

  We use the configuration file when we create an instance of the manager.
  
  ```java
	  //add a instance of item object
      mDbManager.add(item);
	  
	  //get item
	  Item itemDb = mDbManager.getFirst(Item.class);
	  
	  //get item with its identifier
	  Item itemDb = mDbManager.getObjectById(Item.class, "1");
	  
	  //get item with identifier inside of the object
	  Item itemDb = mDbManager.getObjectById(item);
	  
	  //get items by a field
	  List<Item> itemsDb = mDbManager.getObjectsByFields(Item.class, "text=text");
	  
	  //ge items by a field stores in the class
	  List<Item> itemsDb = mDbManager.getObjectsByFields(item, "text");
	  
	  //update the item
	  mDbManager.update(item);
	  
	  //delete item
	  mDbManager.delete(item);
	  
	  //and more...	  
	  
   ```

4. Store data of a table as JSON. To do that you need to add a variable to the class with @GeDatabaseFieldJson annotation:

  Use it to store subclasses that you don't need to use for operations. JSON variable will store all content of the class using Gson library.

```java
	    @DatabaseTable
public class Item {

    //... rest of variables

    @DatabaseField
    @GeDatabaseFieldJson
    public String jsonData;

}
	  
   ```

## Logs

Googlyeyes-ormlite has logs, showing some of the process and errors. You can enable it but remember to disable it in production releases.

  ```java
  GEL.showLogs = true;
  ```
   
## License

    Copyright 2017 SilicorniO

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.