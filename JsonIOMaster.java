import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JsonIOMaster {

    private String path;
    private ArrayList<TableStructure> tables;
    private SQLDatabaseStructure SQLDatabaseStructure;



    public JsonIOMaster(SQLDatabaseStructure db) {
        SQLDatabaseStructure = db;
        this.path = SQLDatabaseStructure.getName() + ".json";
    }

    private ColumnStructure parseJSONObject(JSONObject jsonObject){

        //fejvakaros modja hogy nyerjem ki valahogy a dolgokat

        String columnName = jsonObject.getString(Finals.JSON_COLUMN_NAME_KEY);
        String columnType = jsonObject.getString(Finals.JSON_COLUMN_TYPE_KEY);
        boolean isColumnPrimaryKey = jsonObject.getBoolean(Finals.JSON_COLUMN_ISPRIMARY_KEY);
        boolean isColumnForeignKey = jsonObject.getBoolean(Finals.JSON_COLUMN_ISFOREIGN_KEY);
        String columnForeignReference = jsonObject.getString(Finals.JSON_COLUMN_REFERENCE_KEY);

        return new ColumnStructure(columnName,columnType,isColumnPrimaryKey,isColumnForeignKey,columnForeignReference);

    }

    public void readDBFromFile(){

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String data = null;
        try {

            data = bufferedReader.readLine();
        }
        catch (IOException | NullPointerException e1) {
            e1.printStackTrace();
        }

        //System.out.println(e);




        JSONObject tableIterator = null;
        JSONObject columnIterator = null;

        JSONObject database = new JSONObject(data);

        //SQLDatabaseStructure = new SQLDatabaseStructure((String)database.get(Finals.JSON_DATABASE_NAME_KEY));
        JSONArray tables = (JSONArray)database.get(Finals.JSON_DATABASE_TABLES_KEY);


        for(Object table: tables){

            ArrayList<ColumnStructure> columnStructureArrayList = new ArrayList<>();
            tableIterator = (JSONObject) table;
            String tableName = (String)tableIterator.get(Finals.JSON_TABLE_NAME_KEY);
            tableIterator.remove(Finals.JSON_TABLE_NAME_KEY);
            JSONArray columns = (JSONArray) tableIterator.get(Finals.JSON_TABLE_COLUMNS_KEY);
            //SQLDatabaseStructure.addTable(new TableStructure(tableIterator));
            for(Object column: columns){

                ColumnStructure columnStructure = parseJSONObject((JSONObject) column);
                columnStructureArrayList.add(columnStructure);
            }

            try {
                SQLDatabaseStructure.addTable(new TableStructure(tableName,columnStructureArrayList));
            } catch (MultiplePrimaryKeysInTableException e) {
                e.printStackTrace();
            }
        }

    }

    public void writeDBTofile(){
        try {
            SQLDatabaseStructure.toJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
