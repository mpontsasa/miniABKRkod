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

    public String[] getRecordByKey(String key){
        return null;
    }
}
