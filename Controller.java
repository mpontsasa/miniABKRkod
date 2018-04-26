import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import java.io.File;
import java.lang.module.InvalidModuleDescriptorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Controller {

    private UserLayer userLayer = null;
    private SQLDatabaseStructure sqlDatabaseStructure = null;  //structure of active database
    private ActiveEnviornment activeEnviornment = new ActiveEnviornment();

    public Controller() {

    }

    public void parseSQL(String commandSQL) throws Exception{

        String words[] = commandSQL.split(" ");

        if(words.length < 2)
        {
            throw new InvalidSQLCommandException("Too short command.");
        }

        switch(words[0].toUpperCase())
        {
            case "USE":
                useDatabaseCommand(words);
                break;
            case "CREATE":
                if (words[1].equalsIgnoreCase("DATABASE")){
                    createDatabaseCommand(words);
                }
                else if(words[1].equalsIgnoreCase("TABLE")){
                    createTableCommand(words);
                }
                else if(words[1].equalsIgnoreCase("INDEX")){
                    createIndexCommand(words);
                }
                break;
            case "DROP":
                if (words[1].equalsIgnoreCase("DATABASE")){
                    dropDatabaseCommand(words);
                }
                else if(words[1].equalsIgnoreCase("TABLE")){
                    dropTableCommand(words);
                }
                break;
            case "INSERT":
                insertIntoCommand(words);
                break;
        }
    }

    public void useDatabaseCommand(String[] words) throws InvalidSQLCommandException{
        if (words.length != 2){
            throw new InvalidSQLCommandException("Too much words after USE.");
        }
        try{
            activeEnviornment.setUpActiveEnviornment(words[1],false);

            sqlDatabaseStructure = new SQLDatabaseStructure(words[1]);
            JsonIOMaster io = new JsonIOMaster(sqlDatabaseStructure);
            io.readDBFromFile();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createDatabaseCommand(String[] words) throws Exception {
        if (words.length != 3){
            throw new InvalidSQLCommandException("Insuficient length.");
        }

        if (new File(Finals.ENVIORNMENT_PATH + words[2]).exists()) {
            throw new InvalidSQLCommandException("Database " + words[2] + " alredy exists");
        }

        try{
            activeEnviornment.setUpActiveEnviornment(words[2],true);

            sqlDatabaseStructure = new SQLDatabaseStructure(words[2]);
            sqlDatabaseStructure.toJson();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<ColumnStructure> parseCreateTableCommand(String[] words) throws InvalidSQLCommandException{

        ArrayList<ColumnStructure> result = new ArrayList<>();

        String command = String.join(" ", words);
        command = command.substring(command.indexOf("(") + 1 , command.indexOf(")"));
        //System.out.println("got the command:" + command);
        command = command.trim().replaceAll(" +"," ");//remove extra spaces
        command = command.replaceAll("^ +", "");//more space removel at end and beggining of string, probably not necessary
        command = command.replaceAll(" +$", "");
        command = command.replaceAll(", +", ",");//remove spaces ner commas
        command = command.replaceAll(" +,", ",");
        String[] fieldsCommands = command.split(",");

        for(String fieldCommand : fieldsCommands){
            String[] fieldCommandParts = fieldCommand.split(" ");
            List<String> fcpList = Arrays.asList(fieldCommandParts);
            String name = fieldCommandParts[0];
            String type = fieldCommandParts[1];
            if((!type.equals(Finals.INT_TYPE)) && (!type.equals(Finals.STRING_TYPE))){
                throw new InvalidSQLCommandException("Wrong type!");
            }
            boolean primary = fcpList.contains("primary");
            boolean foreign = fcpList.contains("references");
            boolean unique = fcpList.contains("unique");
            String reference = null;
            if(foreign)
            {
                reference = fcpList.get(fcpList.indexOf("references") + 1);
            }

            result.add(new ColumnStructure(name, type, primary, foreign, unique, reference));
        }
        //System.out.println(command);


        return result;

    }

    public void createTableCommand(String[] words)throws Exception{

        if(sqlDatabaseStructure.hasTable(words[2])){
            throw new InvalidSQLCommandException("Table already exists!(Create table error)");
        }

        if (!activeEnviornment.isSetUp())
        {
            throw new InvalidSQLCommandException("No database selected");
        }

        activeEnviornment.createDB(words[2]);

        TableStructure tableStructure = new TableStructure(words[2],parseCreateTableCommand(words));
        sqlDatabaseStructure.addTable(tableStructure);
        sqlDatabaseStructure.toJson();
    }

    public void dropDatabaseCommand(String[] words)throws Exception{
        if (words.length != 3){
            throw new InvalidSQLCommandException("Insuficient length.");
        }

        if (!deleteDirectory(new File(Finals.ENVIORNMENT_PATH + words[2])))
        {
            throw new InvalidSQLCommandException("Cant delete Database(probobly dosent exist)");
        }

        activeEnviornment = null;

        ///MEG KELL: szerkezet torlese
        if(!deleteDirectory(new File(words[2] + ".json")))
        {
            throw new InvalidSQLCommandException("Cant delete Database structure!");
        }
        sqlDatabaseStructure = null;
    }

    public void dropTableCommand(String[] words)throws Exception{

        System.out.println("Drop Table " + words[2]);

        if (words.length != 3){
            throw new InvalidSQLCommandException("Insuficient length.");
        }

        if (!activeEnviornment.isSetUp())
        {
            throw new InvalidSQLCommandException("No database selected");
        }

        activeEnviornment.deleteDB(words[2]);
        sqlDatabaseStructure.deleteTable(words[2]);
        sqlDatabaseStructure.toJson();
    }

    public void insertIntoCommand(String[] words)throws Exception{

        if (!activeEnviornment.isSetUp())
        {
            throw new InvalidSQLCommandException("No database selected");
        }

        if (!words[1].equalsIgnoreCase("INTO") || !words[3].equalsIgnoreCase("VALUES"))
        {
            throw new InvalidSQLCommandException("Unknown command format.");
        }

        String[] values = getValuesForInsert(words);

        if (validateValuesForInsert(words[2],  values))
        {
            Table tempTable = new Table(sqlDatabaseStructure.findTable(words[2]), values);

            DatabaseEntry theKey = new DatabaseEntry(tempTable.getKeyBytes(0));
            DatabaseEntry theData = new DatabaseEntry(tempTable.getValueBytes(0));

            tempTable.addRecord(theKey, theData);//matyi tesztel

            activeEnviornment.insertIntoDB(words[2], theKey, theData);
        }
        else
        {
            throw new InvalidSQLCommandException("Incorrect values given.");
        }
    }

    public void createIndexCommand(String[] words)throws Exception{

        if (!activeEnviornment.isSetUp())
        {
            throw new InvalidSQLCommandException("No database selected");
        }

        if (!words[3].equalsIgnoreCase("ON"))
        {
            throw new InvalidSQLCommandException("Unknown command format.");
        }

        makeIndexFile(words[4], words[5]);

    }

    //MEG KELL IRNI
    public boolean checkPrimaryKeyConstraintOnInsert(String tableName, String value){

        try
        {
            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(tableName);

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) ==
                    OperationStatus.SUCCESS) {

                String keyString = new String(foundKey.getData(), "UTF-8");

                if (keyString != value)
                {
                    activeEnviornment.closeCursor(cursor);
                    return false;
                }

            }
            activeEnviornment.closeCursor(cursor);
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    //MEG KELL IRNI
    public boolean checkUniqueConstraintOnInsert(String tableName, int columnIndex, String value){
        try
        {
            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(tableName);

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            TableStructure ts = sqlDatabaseStructure.findTable(tableName);

            if (ts.getKeyIndex() > columnIndex)
                columnIndex --;

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                String dataString = new String(foundData.getData(), "UTF-8");

                if (dataString.split(Finals.DATA_DELIMITER)[columnIndex].equals(value))
                {
                    activeEnviornment.closeCursor(cursor);
                    return false;
                }

            }
            activeEnviornment.closeCursor(cursor);
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    //MEG KELL IRNI
    public boolean checkForeignKeyConstraintOnInsert(){
        return true;
    }

    public boolean validateValuesForInsert(String tableName, String[] values)
    {

        System.out.println(sqlDatabaseStructure);
        System.out.println(sqlDatabaseStructure.findTable(tableName));
        System.out.println(tableName);
        ArrayList<ColumnStructure> columnStructures = sqlDatabaseStructure.findTable(tableName).getColumns();
        if(values.length != columnStructures.size()){
            return false;
        }

        ColumnStructure columnIterator = null;
        for(int i = 0; i < values.length; i++){

            columnIterator = columnStructures.get(i);
            if(columnIterator.getType().equals(Finals.INT_TYPE)) {
                try{
                    int dummy = Integer.parseInt(values[i]);
                }
                catch (NumberFormatException e){
                    return false;
                }
            }

            if(columnIterator.isPrimaryKey()){
                if(!checkPrimaryKeyConstraintOnInsert(tableName, values[i])){
                    return false;
                }
            }

            if(columnIterator.isForeignKey()){
                if(!checkForeignKeyConstraintOnInsert()){
                    return false;
                }
            }

            if(columnIterator.isUnique()){
                if(!checkUniqueConstraintOnInsert(tableName, i, values[i])){
                    return false;
                }
            }
        }

        return true;
    }

    public String[] getValuesForInsert(String[] words)
    {
        String zarojelek = String.join(" ", Arrays.copyOfRange(words, 4, words.length));
        zarojelek = zarojelek.substring(zarojelek.indexOf("(") + 1, zarojelek.indexOf(")"));

        String[] values = zarojelek.split(",");

        for (int i = 0; i < values.length; i++)
        {
            values[i] = values[i].trim();

            if (values[i].substring(0,1).equals("\"") && values[i].substring(values[i].length()-1, values[i].length()).equals("\""))
            {
                values[i] = values[i].substring(1,values[i].length()-1);
            }
        }

        return values;
    }

    private Table createIndexTable() throws Exception{
        ColumnStructure key = new ColumnStructure("key", Finals.STRING_TYPE, true,false,false,null);
        ColumnStructure data = new ColumnStructure("data", Finals.STRING_TYPE,false,false,false,null);
        ArrayList<ColumnStructure> c = new ArrayList<>();
        c.add(key);c.add(data);



        TableStructure  tableStructure = new TableStructure("index table", c);

        return (new Table(tableStructure));


    }

    private void convertIndexHashMapToIndexTable(HashMap<String,String> hm, Table indexTable){

        for(String key : hm.keySet()){

            String value = hm.get(key);
            indexTable.addRecord(key,value);
        }
    }

    public void makeIndexFile (String tableName, String columnName)
    {
        try
        {
            activeEnviornment.createDB(Finals.INDEX_FILE_NAME + tableName + "_" + columnName); // the index file

            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(tableName);

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();


            Table indexTable = createIndexTable();

            HashMap<String,String> indexHM = new HashMap<>();

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) ==
                    OperationStatus.SUCCESS) {

                String keyString = new String(foundKey.getData(), "UTF-8");
                String dataString = new String(foundData.getData(), "UTF-8");
                //System.out.println("Key | Data : " + keyString + " | " + dataString + "");


                ///itt kell elkesziteni az indexfilenak megfelelo szerkezetet
                if(indexHM.containsKey(keyString)){
                    String data = indexHM.get(keyString);
                    data += dataString;
                    data += Finals.INDEX_DATA_SEPARATOR;
                    indexHM.replace(keyString,data);
                }
                else {
                    indexHM.put(keyString,dataString);
                }


            }
            activeEnviornment.closeCursor(cursor);
            convertIndexHashMapToIndexTable(indexHM, indexTable);

            for (int i = 0; i < indexTable.getRecordCount(); i++)
            {
                DatabaseEntry key = new DatabaseEntry(indexTable.getKeyBytes(i));
                DatabaseEntry data = new DatabaseEntry(indexTable.getValueBytes(i));

                activeEnviornment.insertIntoDB(Finals.INDEX_FILE_NAME + tableName + "_" + columnName, key, data);
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }

    public SQLDatabaseStructure getSqlDatabaseStructure() {
        return sqlDatabaseStructure;
    }

    public ActiveEnviornment getActiveEnviornment() {
        return activeEnviornment;

    }
}
