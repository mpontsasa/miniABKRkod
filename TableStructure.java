import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TableStructure {

    private String name;
    private ArrayList<ColumnStructure> columns;
    private int keyIndex;

    public TableStructure(String name, ArrayList<ColumnStructure> columns) throws MultiplePrimaryKeysInTableException {
        this.name = name;
        this.columns = columns;//because we are going to remove the key column from this.columns, but dont want to hurt the parameter list

        //validate multiple primary keys and set column index
        boolean hasPrimary = false;
        for(int i = 0; i < columns.size(); i++){
            if(columns.get(i).isPrimaryKey()){
                if(hasPrimary){
                    throw new MultiplePrimaryKeysInTableException();
                }
                else{
                    hasPrimary = true;
                    keyIndex = i;
                }
            }
        }

    }

    public void removeColumn(ColumnStructure c) throws Exception{
        if(c.isPrimaryKey()){
            throw new Exception("cannot remove a primary key!");
        }
        columns.remove(c);
        for(int i = 0; i < columns.size(); i++){
            if(columns.get(i).isPrimaryKey()){
                keyIndex = i;
            }
        }
    }

    public void addColumn(ColumnStructure c) throws MultiplePrimaryKeysInTableException{
        if(c.isPrimaryKey()){
            throw new MultiplePrimaryKeysInTableException();
        }
        else{
            columns.add(c);
            for(int i = 0; i < columns.size(); i++){
                if(columns.get(i).isPrimaryKey()){
                    keyIndex = i;
                }
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(name);
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

    public String getName() {
        return name;
    }

    public ArrayList<ColumnStructure> getColumns() {
        return columns;
    }

    public int getKeyIndex(){
        return keyIndex;
    }

    public String getTypeByIndex(int index){
        return columns.get(index).getType();
    }

    public String getKeyType(){
        return columns.get(keyIndex).getType();
    }

}
