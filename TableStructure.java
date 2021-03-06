import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TableStructure {

    private String name;
    private ArrayList<ColumnStructure> columns;
    private int keyIndex;

    public TableStructure(String name, ArrayList<ColumnStructure> columns) throws MultiplePrimaryKeysInTableException,
            Exception {
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

        if (!hasPrimary) {
            throw new Exception("No primary key in table!");
        }

    }

    public TableStructure(TableStructure ts){
        name = ts.name;
        columns = new ArrayList<>(ts.columns);
        keyIndex = ts.keyIndex;
    }

    public void removeColumnByName(String columnName) throws Exception
    {
        removeColumn(getColumnStructure(getIndexOfColumn(columnName)));
    }

    public void removeColumn(ColumnStructure c) throws Exception{
        if(c.isPrimaryKey()){
            throw new Exception("cannot remove a primary key!");
        }
        columns.remove(c);
        refreshKeyIndex();

    }

    public void addColumn(ColumnStructure c) throws MultiplePrimaryKeysInTableException{
        if(c.isPrimaryKey()){
            throw new MultiplePrimaryKeysInTableException();
        }
        else{
            columns.add(c);
            refreshKeyIndex();
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

    public  void printHeader()
    {
        String toPrint = "";
        for (ColumnStructure cs : columns)
        {
            toPrint += cs.getName() + "\t";
        }
        System.out.println(toPrint);
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

    private void refreshKeyIndex(){
        for(int i = 0; i < columns.size(); i++){
            if(columns.get(i).isPrimaryKey()){
                keyIndex = i;
                break;
            }
        }
    }

    public void mergeWith(TableStructure addTable)
    {
        for (int i = 0; i < addTable.getColumns().size(); i++)
        {
            addTable.getColumns().get(i).setOriginalTable(addTable.getName());
            columns.add(addTable.getColumns().get(i));
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

    public int getIndexOfColumn(String columnName){

        for(int i = 0; i < columns.size(); i++){
            if(columns.get(i).getName().equalsIgnoreCase(columnName)){
                return  i;
            }
        }

        return -1;
    }

    public ColumnStructure findColumn(String columnName){


        for(ColumnStructure cs: columns){
            if (cs.getName().equalsIgnoreCase(columnName)){
                return cs;
            }
        }
        return null;
    }

    public ArrayList<String> getUniqueColumnNames(){

        ArrayList<String> res = new ArrayList<>();
        for(ColumnStructure c : columns){
            if (c.isUnique()){
                res.add(c.getName());
            }
        }
        return res;
    }

    public ArrayList<String> getForeignColumnNames(){

        ArrayList<String> res = new ArrayList<>();
        for(ColumnStructure c : columns){
            if (c.isForeignKey()){
                res.add(c.getName());
            }
        }
        return res;
    }

    public int getNumberOfColumns(){
        return columns.size();
    }

    public String getTypeByIndex(int index){
        return columns.get(index).getType();
    }

    public String getKeyType(){
        return columns.get(keyIndex).getType();
    }

    public ColumnStructure getColumnStructure(int index){
        return columns.get(index);
    }
}
