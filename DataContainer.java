import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataContainer {

    //used to store a table's data
    private ArrayList<String[]> records;


    public DataContainer() {
        records = new ArrayList<>();
    }

    public void addRecord(String[] record) {

        records.add(record);
    }


    public String get(int i, int j){
        return records.get(i)[j];
    }

    public void remove(int i){
        records.remove(i);
    }


}
