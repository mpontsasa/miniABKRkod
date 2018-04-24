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



        myDatabase.close();
        enviornment.close();
    }
}
