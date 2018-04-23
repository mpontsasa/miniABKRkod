import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DBEntries {

    //used to store a table's data
    private HashMap<String,ArrayList<String>> records;


    public DBEntries() {
        records = new HashMap<>();
    }

    public void addRecord(String key, ArrayList<String> values) throws ExistingKeyException{
        if(records.containsKey(key)){
            throw new ExistingKeyException();
        }
        records.put(key,values);
    }

    public void addRecord(String key, String[] values) throws ExistingKeyException{
        if(records.containsKey(key)){
            throw new ExistingKeyException();
        }

        records.put(key,new ArrayList<>(Arrays.asList(values)));
    }

    public ArrayList<String> get(String key){
        if(!records.containsKey(key)){
            return null;
        }
        return records.get(key);
    }



}
