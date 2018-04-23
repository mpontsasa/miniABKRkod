import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TableStructure {

    private String name;
    private ArrayList<ColumnStructure> columns;
    private ColumnStructure key;

    public TableStructure(String name, ArrayList<ColumnStructure> columns) throws MultiplePrimaryKeysInTableException {
        this.name = name;
        this.columns = new ArrayList<>(columns);//because we are going to remove the key column from this.columns, but dont want to hurt the parameter list

        for (ColumnStructure c :
                columns) {
            if (c.isPrimaryKey())
            {
                if(key == null){
                    key = c;

                }
                else{
                    throw new MultiplePrimaryKeysInTableException();
                }
            }
        }
        this.columns.remove(key);
    }

    public void removeColumn(ColumnStructure c){
        columns.remove(c);
    }

    public  void addColumn(ColumnStructure c) throws MultiplePrimaryKeysInTableException{
        if(c.isPrimaryKey()){
            throw new MultiplePrimaryKeysInTableException();
        }
        else{
            columns.add(c);
        }
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append("\n");
        result.append(key);
        result.append("\n");
        for(ColumnStructure c: columns){
            result.append(c);
            result.append("\n");
        }

        return result.toString();
    }

    public JSONObject toJson(){
        JSONObject res = new JSONObject();
        res.put(Finals.JSON_TABLE_NAME_KEY, name);
        JSONArray JSONColumns = new JSONArray();

        JSONColumns.put(key.toJson());//firstly add the key
        for(ColumnStructure c:columns){
            JSONColumns.put(c.toJson());//add all other columns to a vector
        }
        res.put(Finals.JSON_TABLE_COLUMNS_KEY, JSONColumns);//add the column vector
        return res;
    }

    public void fromFile(JSONArray a){

        JSONObject jsonObject = null;
        for(int i = 1; i < a.length(); i++){
            jsonObject = a.getJSONObject(i);
            System.out.println(jsonObject);
        }
    }
}
