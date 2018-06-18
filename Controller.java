import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import java.io.File;
//import java.lang.module.InvalidModuleDescriptorException;
import java.lang.reflect.Array;
import java.nio.channels.SelectableChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Controller {

    private UserLayer userLayer = null;
    public static SQLDatabaseStructure sqlDatabaseStructure = null;  //structure of active database
    public static ActiveEnviornment activeEnviornment = new ActiveEnviornment();

    public Controller() {

    }

    public void parseSQL(String commandSQL) throws Exception {

        commandSQL = commandSQL.replaceAll("\\s+", " ");

        String words[] = commandSQL.split(" ");
//
//        if(words.length >= 9){
//            System.out.println(words[9]);
//            System.out.println(words[10]);
//        }

        if (words.length < 2) {
            throw new InvalidSQLCommandException("Too short command.");
        }

        switch (words[0].toUpperCase()) {
            case "USE":
                useDatabaseCommand(words);
                break;
            case "CREATE":
                if (words[1].equalsIgnoreCase("DATABASE")) {
                    createDatabaseCommand(words);
                } else if (words[1].equalsIgnoreCase("TABLE")) {
                    createTableCommand(words);
                } else if (words[1].equalsIgnoreCase("INDEX")) {
                    createIndexCommand(words);
                }
                break;
            case "DROP":
                if (words[1].equalsIgnoreCase("DATABASE")) {
                    dropDatabaseCommand(words);
                } else if (words[1].equalsIgnoreCase("TABLE")) {
                    dropTableCommand(words);
                }
                break;
            case "INSERT":
                insertIntoCommand(words);
                break;
            case "SELECT":
                selectCommand(words);
                break;
        }
    }

    public void useDatabaseCommand(String[] words) throws InvalidSQLCommandException {
        if (words.length != 2) {
            throw new InvalidSQLCommandException("Too much words after USE.");
        }
        try {
            activeEnviornment.setUpActiveEnviornment(words[1], false);

            sqlDatabaseStructure = new SQLDatabaseStructure(words[1]);
            JsonIOMaster io = new JsonIOMaster(sqlDatabaseStructure);
            io.readDBFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDatabaseCommand(String[] words) throws Exception {
        if (words.length != 3) {
            throw new InvalidSQLCommandException("Insuficient length.");
        }

        if (new File(Finals.ENVIORNMENT_PATH + words[2]).exists()) {
            throw new InvalidSQLCommandException("Database " + words[2] + " alredy exists");
        }

        try {
            activeEnviornment.setUpActiveEnviornment(words[2], true);

            sqlDatabaseStructure = new SQLDatabaseStructure(words[2]);
            sqlDatabaseStructure.toJson();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ColumnStructure> parseCreateTableCommand(String[] words) throws InvalidSQLCommandException {

        ArrayList<ColumnStructure> result = new ArrayList<>();

        String command = String.join(" ", words);
        command = command.substring(command.indexOf("(") + 1, command.indexOf(")"));
        //System.out.println("got the command:" + command);
        command = command.trim().replaceAll(" +", " ");//remove extra spaces
        command = command.replaceAll("^ +", "");//more space removel at end and beggining of string, probably not necessary
        command = command.replaceAll(" +$", "");
        command = command.replaceAll(", +", ",");//remove spaces ner commas
        command = command.replaceAll(" +,", ",");
        String[] fieldsCommands = command.split(",");

        for (String fieldCommand : fieldsCommands) {
            String[] fieldCommandParts = fieldCommand.split(" ");
            List<String> fcpList = Arrays.asList(fieldCommandParts);
            String name = fieldCommandParts[0];
            String type = fieldCommandParts[1];
            if ((!type.equals(Finals.INT_TYPE)) && (!type.equals(Finals.STRING_TYPE))) {
                throw new InvalidSQLCommandException("Wrong type!");
            }
            boolean primary = fcpList.contains("primary");
            boolean foreign = fcpList.contains("references");
            boolean unique = fcpList.contains("unique");
            String reference = null;
            if (foreign) {
                reference = fcpList.get(fcpList.indexOf("references") + 1);
            }

            result.add(new ColumnStructure(name, type, primary, foreign, unique, reference, false));//itt false-ra inicializalom a hasIndex-et mert a create tablenem meg nincs index file
        }
        //System.out.println(command);

        return result;

    }

    public void createTableCommand(String[] words) throws Exception {

        if (sqlDatabaseStructure.hasTable(words[2])) {
            throw new InvalidSQLCommandException("Table already exists!(Create table error)");
        }

        if (!activeEnviornment.isSetUp()) {
            throw new InvalidSQLCommandException("No database selected");
        }

        activeEnviornment.createDB(words[2]);

        TableStructure tableStructure = new TableStructure(words[2], parseCreateTableCommand(words));
        sqlDatabaseStructure.addTable(tableStructure);


        for (String columnName : tableStructure.getUniqueColumnNames()) {
            makeIndexFile(tableStructure.getName(), columnName);
        }

        for (String columnName : tableStructure.getForeignColumnNames()) {
            makeIndexFile(tableStructure.getName(), columnName);
        }

        sqlDatabaseStructure.toJson();
    }

    public void dropDatabaseCommand(String[] words) throws Exception {
        if (words.length != 3) {
            throw new InvalidSQLCommandException("Insuficient length.");
        }

        if (!deleteDirectory(new File(Finals.ENVIORNMENT_PATH + words[2]))) {
            throw new InvalidSQLCommandException("Cant delete Database(probobly dosent exist)");
        }

        activeEnviornment = null;

        ///MEG KELL: szerkezet torlese
        if (!deleteDirectory(new File(words[2] + ".json"))) {
            throw new InvalidSQLCommandException("Cant delete Database structure!");
        }
        sqlDatabaseStructure = null;
    }

    public void dropTableCommand(String[] words) throws Exception {

        System.out.println("Drop Table " + words[2]);

        if (words.length != 3) {
            throw new InvalidSQLCommandException("Insuficient length.");
        }

        if (!activeEnviornment.isSetUp()) {
            throw new InvalidSQLCommandException("No database selected");
        }

        activeEnviornment.deleteDB(words[2]);
        sqlDatabaseStructure.deleteTable(words[2]);
        sqlDatabaseStructure.toJson();
    }

    private void updateIndexes(String tableName, String[] values) {
        try {
            Table tempTable = new Table(sqlDatabaseStructure.findTable(tableName), values);

            for (int i = 0; i < sqlDatabaseStructure.findTable(tableName).getNumberOfColumns(); i++) {

                if (tempTable.getColumnStructure(i).isHasIndex()) {

                    String indexFileName = Finals.INDEX_FILE_NAME + tableName + "_" + tempTable.getColumnStructure(i).getName();
                    String data = activeEnviornment.getValueByKey(indexFileName, values[i]);
                    DatabaseEntry keyEnrty = new DatabaseEntry(values[i].getBytes());
                    DatabaseEntry dataEntry;
                    //ha nincs az index fileban ilyen kulcsu entry, akkor letrehozunk egy ujat, ha van, akkor a vegere fuzzuk az uj adatot
                    if (data == null) {
                        dataEntry = new DatabaseEntry((Finals.INDEX_DATA_SEPARATOR + values[tempTable.getStructure().getKeyIndex()]).getBytes());
                    } else {
                        data += (Finals.INDEX_DATA_SEPARATOR + values[tempTable.getStructure().getKeyIndex()]);
                        dataEntry = new DatabaseEntry(data.getBytes());

                    }
                    activeEnviornment.insertIntoDB(indexFileName, keyEnrty, dataEntry);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertIntoCommand(String[] words) throws Exception {

        if (!activeEnviornment.isSetUp()) {
            throw new InvalidSQLCommandException("No database selected");
        }

        if (!words[1].equalsIgnoreCase("INTO") || !words[3].equalsIgnoreCase("VALUES")) {
            throw new InvalidSQLCommandException("Unknown command format.");
        }

        String[] values = getValuesForInsert(words);

        if (validateValuesForInsert(words[2], values)) {
            Table tempTable = new Table(sqlDatabaseStructure.findTable(words[2]), values);

            DatabaseEntry theKey = new DatabaseEntry(tempTable.getKeyBytes(0));
            DatabaseEntry theData = new DatabaseEntry(tempTable.getValueBytes(0));

            updateIndexes(words[2], values);

            //tempTable.addRecord(theKey, theData);//matyi tesztel

            activeEnviornment.insertIntoDB(words[2], theKey, theData);
        } else {
            throw new InvalidSQLCommandException("Incorrect values given.");
        }
    }

    public void createIndexCommand(String[] words) throws Exception {

        if (!activeEnviornment.isSetUp()) {
            throw new InvalidSQLCommandException("No database selected");
        }

        if (!words[3].equalsIgnoreCase("ON")) {
            throw new InvalidSQLCommandException("Unknown command format.");
        }

        makeIndexFile(words[4], words[5]);

    }

    public void selectCommand(String[] words) throws Exception {
        // SELECT tabla1.mezo1 tabla2.mezo2 FROM tabla1 tabla2 WHERE tabla1.mezo1=tabla2.mezo2
        if (!activeEnviornment.isSetUp()) {
            throw new InvalidSQLCommandException("No database selected");
        }

        ArrayList<String> selectedFields = new ArrayList<>();
        ArrayList<String> selectionTables = new ArrayList<>();
        ArrayList<String> selectionConstraints = new ArrayList<>();

        int i;
        for (i = 1; i < words.length && !words[i].equalsIgnoreCase("FROM"); i++) {
            selectedFields.add(words[i]);
        }

        if (i == words.length) {
            throw new InvalidSQLCommandException("Missing FROM statement");
        }

        for (i++; i < words.length && !words[i].equalsIgnoreCase("WHERE") && !words[i].equalsIgnoreCase("WHERE"); i++) {
            selectionTables.add(words[i]);
        }


        //=====================================================================
        //          ITT KELL A SZETSZEDESE AZ EGESZNEK
        ArrayList<Pair> joins = new ArrayList<>();
        ArrayList<SelectConstraint> constraints = new ArrayList<>();    // a constraints nem pairs hanem Constraint lesz
        ArrayList<Field> selected = new ArrayList<>();


        //selected felepitese
        for (String s : selectedFields) {
            if (!s.contains(".")) {
                throw new InvalidSQLCommandException("A field does not contain a dot for example table1.id");
            } else {
                selected.add(new Field(s));
            }
        }

        //lekezelem azt az esetet ha mar nincs join, azaz feldolgoztuk az egesz select parancsot
        if (i != words.length) {

            //constraint es join parseolas
            StringBuilder tempSB = new StringBuilder();
            for (i++; i < words.length; i++) {
                tempSB.append(words[i]);
                tempSB.append(" ");
            }
            String rem = tempSB.toString();
            rem = rem.replaceAll(" ", "");
            String[] remainder = rem.split("");
            i = 0;
            while (i < remainder.length && !(remainder[i].equalsIgnoreCase("group")&&
                                            remainder[i + 1].equalsIgnoreCase("by"))) {
                tempSB.setLength(0);

                while ((i < remainder.length) &&
                        (!remainder[i].equals(Finals.EQUALS_OPERATOR)) &&
                        (!remainder[i].equals(Finals.LESS_THAN_OPERATOR)) &&
                        (!remainder[i].equals(Finals.GREATER_THAN_OPERATOR))) {

                    tempSB.append(remainder[i]);
                    i++;
                }
                String firstField = tempSB.toString();
                if (!firstField.contains(".")) {
                    throw new InvalidSQLCommandException("A field does not contain a dot for example table1.id");
                }
                String op;
                String secondField;
                if (remainder[i].equals(Finals.GREATER_THAN_OPERATOR)) {
                    if (remainder[i + 1].equals(Finals.EQUALS_OPERATOR)) {
                        //>= eset
                        op = Finals.GREATER_THAN_OR_EQUAL_OPERATOR;
                        i += 2;
                    } else {
                        //> eset
                        op = Finals.GREATER_THAN_OPERATOR;
                        i++;
                    }
                } else {
                    if (remainder[i].equals(Finals.LESS_THAN_OPERATOR)) {
                        if (remainder[i + 1].equals(Finals.EQUALS_OPERATOR)) {
                            //<= eset
                            op = Finals.LESS_THAN_OR_EQUAL_OPERATOR;
                            i += 2;
                        } else {
                            //< eset
                            op = Finals.LESS_THAN_OPERATOR;
                            i++;
                        }
                    } else {
                        //= eset
                        op = Finals.EQUALS_OPERATOR;
                        i++;
                    }
                }

                //System.out.print(firstField + " " + op);
                //MOST JON AZ EGYENLO UTANI RESZ LEKEZELESE


           /*selected.add(new Field("Tabla2", "alma"));
            selected.add(new Field("Tabla2", "korte"));

            selected.add(new Field("Tabla1", "nev"));
            selected.add(new Field("Tabla1", "kor"));

            selected.add(new Field("Tabla3", "szo"));

            joins.add(new Pair(new Field("Tabla1", "kor"), new Field("Tabla2", "dbszam")));
            joins.add(new Pair(new Field("Tabla1", "id"), new Field("Tabla3", "szam")));
*/
                if (remainder[i].equals("\"")) {
                    //egyenloseget nezunk egy STRINGRE time
                    tempSB.setLength(0);
                    i++;
                    while (!remainder[i].equals("\"")) {
                        tempSB.append(remainder[i]);
                        i++;
                    }
                    secondField = tempSB.toString();
                    constraints.add(new SelectConstraint(firstField, op, secondField));
                    i += 2;//atugrok a vesszo utanig


                } else if (isANumber(remainder[i])) {
                    //egyenloseget nezunk egy SZAMRA time
                    tempSB.setLength(0);
                    while (i < remainder.length && isANumber(remainder[i])) {
                        tempSB.append(remainder[i]);
                        i++;
                    }
                    secondField = tempSB.toString();
                    constraints.add(new SelectConstraint(firstField, op, secondField));
                    i++;
                } else {
                    //JOIN feltetel masodik elemenek feldolgozasa time
                    tempSB.setLength(0);
                    while (i < remainder.length && !remainder[i].equals(Finals.AND_SYNTAX)) {
                        tempSB.append(remainder[i]);
                        i++;
                    }
                    secondField = tempSB.toString();
                    if (!secondField.contains(".")) {
                        throw new InvalidSQLCommandException("A field does not contain a dot for example table1.id");
                    }
                    joins.add(new Pair(firstField, secondField));
                    i++;

                }
                //System.out.println(secondField);

            }

            System.out.println();
        }


        //=====================================================================

        Table result = new Table(selected, joins, constraints, sqlDatabaseStructure, activeEnviornment);


        result.print();
    }

    private boolean isANumber(String candidate) {

        try {
            Integer.parseInt(candidate);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean checkPrimaryKeyConstraintOnInsert(String tableName, String value) {

        try {
            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(tableName);

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                String keyString = new String(foundKey.getData(), "UTF-8");

                if (keyString.equals(value)) {
                    activeEnviornment.closeCursor(cursor);
                    return false;
                }

            }
            activeEnviornment.closeCursor(cursor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkUniqueConstraintOnInsert(String tableName, int columnIndex, String value) {
        try {
            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(tableName);

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            TableStructure ts = sqlDatabaseStructure.findTable(tableName);

            if (ts.getKeyIndex() < columnIndex)
                columnIndex--;

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                String dataString = new String(foundData.getData(), "UTF-8");

                if (dataString.split(Finals.DATA_DELIMITER)[columnIndex].equals(value)) {
                    activeEnviornment.closeCursor(cursor);
                    return false;
                }

            }
            activeEnviornment.closeCursor(cursor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //NEM MUSZAJ: ellenorzes, hogy a forign key referencere nincs-e indextomb
    public boolean checkForeignKeyConstraintOnInsert(String tableName, int columnIndex, String value) {

        //HA VAN INDEX MEG KELL IRNI
        //..else: (ha nincs index):
        try {
            Cursor cursor = null;

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            TableStructure ts = sqlDatabaseStructure.findTable(tableName);

            String tableName2 = ts.getColumns().get(columnIndex).getForeignReferenceName().split("\\.")[0];
            int columnIndex2 = sqlDatabaseStructure.findTable(tableName2).getIndexOfColumn(ts.getColumns().get(columnIndex).getForeignReferenceName().split("\\.")[1]);

            TableStructure ts2 = sqlDatabaseStructure.findTable(tableName2);

            if (ts.getKeyIndex() < columnIndex)
                columnIndex--;

            if (ts2.getKeyIndex() < columnIndex2) {
                columnIndex2--;
            }

            cursor = activeEnviornment.getCursor(tableName2);

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                String dataString = new String(foundData.getData(), "UTF-8");

                if (dataString.split(Finals.DATA_DELIMITER)[columnIndex2].equals(value)) {
                    activeEnviornment.closeCursor(cursor);
                    return true;
                }

            }
            activeEnviornment.closeCursor(cursor);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean validateValuesForInsert(String tableName, String[] values) {

        System.out.println(sqlDatabaseStructure);
        System.out.println(sqlDatabaseStructure.findTable(tableName));
        System.out.println(tableName);
        ArrayList<ColumnStructure> columnStructures = sqlDatabaseStructure.findTable(tableName).getColumns();
        if (values.length != columnStructures.size()) {
            return false;
        }

        ColumnStructure columnIterator = null;
        for (int i = 0; i < values.length; i++) {

            columnIterator = columnStructures.get(i);
            if (columnIterator.getType().equals(Finals.INT_TYPE)) {
                try {
                    int dummy = Integer.parseInt(values[i]);
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            if (columnIterator.isPrimaryKey()) {
                if (!checkPrimaryKeyConstraintOnInsert(tableName, values[i])) {
                    return false;
                }
            }

            if (columnIterator.isForeignKey()) {
                if (!checkForeignKeyConstraintOnInsert(tableName, i, values[i])) {
                    return false;
                }
            }

            if (columnIterator.isUnique()) {
                if (!checkUniqueConstraintOnInsert(tableName, i, values[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    public String[] getValuesForInsert(String[] words) {
        String zarojelek = String.join(" ", Arrays.copyOfRange(words, 4, words.length));
        zarojelek = zarojelek.substring(zarojelek.indexOf("(") + 1, zarojelek.indexOf(")"));

        String[] values = zarojelek.split(",");

        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();

            if (values[i].substring(0, 1).equals("\"") && values[i].substring(values[i].length() - 1, values[i].length()).equals("\"")) {
                values[i] = values[i].substring(1, values[i].length() - 1);
            }
        }

        return values;
    }

    private Table createIndexTable(String name) throws Exception {
        ColumnStructure key = new ColumnStructure("key", Finals.STRING_TYPE, true, false, false, null, false);
        ColumnStructure data = new ColumnStructure("data", Finals.STRING_TYPE, false, false, false, null, false);
        ArrayList<ColumnStructure> c = new ArrayList<>();
        c.add(key);
        c.add(data);

        TableStructure tableStructure = new TableStructure(name, c);

        return (new Table(tableStructure));

    }

    private void convertIndexHashMapToIndexTable(HashMap<String, String> hm, Table indexTable) {

        for (String key : hm.keySet()) {

            String value = hm.get(key);
            indexTable.addIndexRecord(key, value);
        }
    }

    public void makeIndexFile(String tableName, String columnName) {
        try {
            activeEnviornment.createDB(Finals.INDEX_FILE_NAME + tableName + "_" + columnName); // the index file

            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(tableName);

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();


            Table indexTable = createIndexTable(columnName);
            HashMap<String, String> indexHM = new HashMap<>();
            int indexOfIndexedColumn = sqlDatabaseStructure.findTable(tableName).getIndexOfColumn(columnName);

            if (indexOfIndexedColumn == -1) {
                throw new Exception("The column does no exist in the table(index)");
            }
            if (indexOfIndexedColumn > sqlDatabaseStructure.findTable(tableName).getKeyIndex()) {
                indexOfIndexedColumn--;
            }


            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                String keyString = new String(foundKey.getData(), "UTF-8");
                String dataString = new String(foundData.getData(), "UTF-8");
                //System.out.println("Key | Data : " + keyString + " | " + dataString + "");


                String indexedColumnValue = dataString.split(Finals.DATA_DELIMITER)[indexOfIndexedColumn];
                ///itt kell elkesziteni az indexfilenak megfelelo szerkezetet
                if (indexHM.containsKey(indexedColumnValue)) {
                    String data = indexHM.get(indexedColumnValue);
                    data += Finals.INDEX_DATA_SEPARATOR;
                    data += keyString;
                    indexHM.replace(indexedColumnValue, data);
                } else {
                    indexHM.put(indexedColumnValue, keyString);
                }
            }
            activeEnviornment.closeCursor(cursor);
            convertIndexHashMapToIndexTable(indexHM, indexTable);

            for (int i = 0; i < indexTable.getRecordCount(); i++) {
                DatabaseEntry key = new DatabaseEntry(indexTable.getKeyBytes(i));
                DatabaseEntry data = new DatabaseEntry(indexTable.getIndexValueBytes(i));

                //cut off the data delimeter

                activeEnviornment.insertIntoDB(Finals.INDEX_FILE_NAME + tableName + "_" + columnName, key, data);
            }


            sqlDatabaseStructure.findTable(tableName).findColumn(columnName).setHasIndex(true);
            sqlDatabaseStructure.toJson();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }

    public SQLDatabaseStructure getSqlDatabaseStructure() {
        return sqlDatabaseStructure;
    }

    public ActiveEnviornment getActiveEnviornment() {
        return activeEnviornment;

    }

    public void printDatabases() {
        final File folder = new File("Enviornments");

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println(fileEntry.getName());
            }
        }
    }



}
