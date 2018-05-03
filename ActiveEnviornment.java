import com.sleepycat.je.*;

import java.io.File;
import java.util.Arrays;

public class ActiveEnviornment {

    private Environment enviornment = null;
    private String name = null;


    public void setUpActiveEnviornment(String name, boolean allowCreate)
    {
        this.name = name;

        if(allowCreate){
            File folder = new File(Finals.ENVIORNMENT_PATH + name);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(allowCreate);
        enviornment = new Environment(new File(Finals.ENVIORNMENT_PATH + name), envConfig);
        enviornment.close();
    }

    public void openEnviornment()
    {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(false);
        enviornment = new Environment(new File(Finals.ENVIORNMENT_PATH + name), envConfig);
    }

    public  boolean isSetUp(){
        return !(name == null);
    }

    public void createDB(String name)
    {
        openEnviornment();

        Database myDatabase = null;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        myDatabase = enviornment.openDatabase(null, name, dbConfig);
        myDatabase.close();

        enviornment.close();
    }

    public void deleteDB(String name)
    {
        openEnviornment();
        enviornment.removeDatabase(null, name);
        enviornment.close();
    }

    public void insertIntoDB(String tableName, DatabaseEntry key, DatabaseEntry value) throws Exception {
        openEnviornment();

        Database myDatabase = null;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(false);
        myDatabase = enviornment.openDatabase(null, tableName, dbConfig);

        myDatabase.put(null, key, value);

        myDatabase.close();
        enviornment.close();
    }

    public void printTables()
    {
        openEnviornment();
        for (String dbn : enviornment.getDatabaseNames())
        {

            System.out.println("DBs: " + dbn);
        }
        enviornment.close();
    }

    public String getValueByKey(String tableName, String aKey) throws Exception
    {
        openEnviornment();

        Database myDatabase = null;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(false);
        myDatabase = enviornment.openDatabase(null, tableName, dbConfig);
// Create a pair of DatabaseEntry objects. theKey 981176
// is used to perform the search. theData is used
// to store the data returned by the get() operation.
            DatabaseEntry theKey = new DatabaseEntry(aKey.getBytes("UTF-8"));
            DatabaseEntry theData = new DatabaseEntry();
// Perform the get.
            if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
// Recreate the data String.
                byte[] retData = theData.getData();
                String foundData = new String(retData, "UTF-8");
                //System.out.println("For key: '" + aKey + "' found data: '" +foundData + "'.");

                myDatabase.close();
                enviornment.close();

                return foundData;
            } else {
                //System.out.println("No record found for key '" + aKey + "'.");
                myDatabase.close();
                enviornment.close();

                return null;
            }
    }

    public void printDataByKey(String tableName, String aKey) throws Exception{
        openEnviornment();

        Database myDatabase = null;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(false);
        myDatabase = enviornment.openDatabase(null, tableName, dbConfig);
// Create a pair of DatabaseEntry objects. theKey 981176
// is used to perform the search. theData is used
// to store the data returned by the get() operation.
        DatabaseEntry theKey = new DatabaseEntry(aKey.getBytes("UTF-8"));
        DatabaseEntry theData = new DatabaseEntry();
// Perform the get.
        if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
// Recreate the data String.
            byte[] retData = theData.getData();
            String foundData = new String(retData, "UTF-8");
            System.out.println("For key: '" + aKey + "' found data: '" +foundData + "'.");

            myDatabase.close();
            enviornment.close();


        } else {
            System.out.println("No record found for key '" + aKey + "'.");
            myDatabase.close();
            enviornment.close();


        }
    }

    public Cursor getCursor(String tableName)
    {
        openEnviornment();

        Database myDatabase = null;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(false);
        myDatabase = enviornment.openDatabase(null, tableName, dbConfig);

        Cursor cursor = null;

        cursor = myDatabase.openCursor(null, null);

        return cursor;
    }

    public void closeCursor(Cursor cursor)
    {
        Database myDB =  cursor.getDatabase();
        cursor.close();

        myDB.close();
        enviornment.close();
    }

    public Environment getEnviornment() {
        return enviornment;
    }

}
