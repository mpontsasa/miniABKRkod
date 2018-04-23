import java.util.ArrayList;

public class Table {

    private TableStructure structure;
    private ArrayList<String[]> data;

    public Table(TableStructure structure) {
        this.structure = structure;
        data = new ArrayList<>();
    }

    public Table(TableStructure structure, ArrayList<String[]> data) {
        this.structure = structure;
        this.data = data;
    }

    public Table(TableStructure structure, String[] onlyRecord) {
        this.structure = structure;
        data = new ArrayList<>();
        data.add(onlyRecord);
    }

    public String getKeyValue(int rowIndex){

        int keyIndex = structure.getKeyIndex();

        return data.get(rowIndex)[keyIndex];
    }

    public String[] getRowByKeyValue(String key){

        int keyIndex = structure.getKeyIndex();
        for(String[] row : data){

            if(row[keyIndex].equals(key)){
                return row;
            }
        }
        return null;
    }
}
