import com.sleepycat.je.DatabaseEntry;

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
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

    public String getKey(int recordIndex){

        //int keyIndex = structure.getKeyIndex();

        return data.get(recordIndex)[structure.getKeyIndex()];
    }


    public void addRecord(DatabaseEntry keyEntry, DatabaseEntry dataEntry){

        String keyString = new String(keyEntry.getData());
        String dataString = new String(dataEntry.getData());

        System.out.println("key:" + keyString + "\ndata:" + dataString);

        int keyIndex = structure.getKeyIndex();

        String[] data  = dataString.split(Finals.DATA_DELIMITER);

        String[] result = new String[data.length + 1];

        int i = 0;
        for ( i = 0; i < keyIndex; i++){
            result[i] = data[i];
        }
        result[i] = keyString;i++;
        System.arraycopy(data, i - 1, result, i, result.length - i);

        this.data.add(result);
    }

    public byte[] getKeyBytes(int recordIndex) {

        /*if (structure.getKeyType().equals( Finals.INT_TYPE ) ) {

            return toBytes(Integer.parseInt(getKey(recordIndex)));
        } else if (structure.getKeyType().equalsIgnoreCase(Finals.STRING_TYPE) ) {
            return toBytes(getKey(recordIndex));
        } else {
            return toBytes(Integer.parseInt(getKey(recordIndex))); // ha mas tipusu, visszaterit majd mast, most stringkent kezeli
        }*/

        return toBytes(getKey(recordIndex));

    }

    public byte[] getValueBytes(int recordIndex)
    {
        String res = "";

        int keyInd = structure.getKeyIndex();

        for(int i = 0; i <data.get(recordIndex).length; i++)
        {

            

            /*if (structure.getTypeByIndex(i).equals(Finals.INT_TYPE)) {

>>>>>>> 878b049e0399f62d84d47c31f8ced5f2bb009d34
                res = concat(res, toBytes(Integer.parseInt(data.get(recordIndex)[i])));
            } else if (structure.getTypeByIndex(i).equalsIgnoreCase(Finals.STRING_TYPE)) {
                res = concat(res, toBytes(data.get(recordIndex)[i]));
            }*/
            if (i != keyInd)
                res += data.get(recordIndex)[i] + Finals.DATA_DELIMITER;
        }

        return toBytes(res);
    }

    public String[] getRowByKey(String key){

        int keyIndex = structure.getKeyIndex();
        for(String[] row : data){

            if(row[keyIndex].equals(key)){
                return row;
            }
        }
        return null;
    }

    byte[] toBytes(int i)
    {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }

    byte[] toBytes(String str)
    {
        return str.getBytes();
    }

    byte[] concat (byte a[], byte b[])
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try{

            outputStream.write( a );
            outputStream.write( b );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return outputStream.toByteArray( );
    }
}
