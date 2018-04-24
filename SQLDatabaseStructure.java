import javafx.scene.control.Tab;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SQLDatabaseStructure {

    private ArrayList<TableStructure> tables;
    private String name;

    public SQLDatabaseStructure(String name) {
        this.name = name;
        tables = new ArrayList<>();
    }

    public void addTable(TableStructure t){
        tables.add(t);
    }

    public void deleteTable(String tableName){

        for(TableStructure ts : tables){
            if(tableName.equals(ts.getName())){
                tables.remove(ts);
            }
        }
    }

    public void deleteTable(TableStructure t){
        tables.remove(t);
    }

    public void toJson() throws IOException{

        FileWriter fw = new FileWriter(name + ".json");

        JSONObject res = new JSONObject();
        res.put(Finals.JSON_DATABASE_NAME_KEY, name);
        JSONArray JSONTables = new JSONArray();
        for(TableStructure table:tables){
            JSONTables.put(table.toJson());
        }
        res.put(Finals.JSON_DATABASE_TABLES_KEY, JSONTables);

        fw.write(res.toString());
        fw.flush();
        fw.close();
    }


    @Override
    public String toString(){


        StringBuilder result = new StringBuilder();
        result.append("Adatbazis:").append(name).append("\n");


        for(TableStructure ts: tables){
            result.append(ts.toString());
            result.append("\n");
        }

        return result.toString();


    }

    public String getName() {
        return name;
    }

    public ArrayList<TableStructure> getTables() {
        return tables;
    }


    public TableStructure findTable(String tableName){

        for(TableStructure ts : tables){
            if(tableName.equals(ts.getName())){
                return ts;
            }
        }
        return null;

    }
}
