import java.io.ByteArrayOutputStream;
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

        int keyIndex = structure.getKeyIndex();

        return data.get(recordIndex)[keyIndex];
    }

    public byte[] getKeyBytes(int recordIndex) {
        if (structure.getKeyType() == Finals.INT_TYPE) {
            return toBytes(Integer.parseInt(getKey(recordIndex)));
        } else if (structure.getKeyType() == Finals.STRING_TYPE) {
            return toBytes(getKey(recordIndex));
        } else {
            return toBytes(Integer.parseInt(getKey(recordIndex))); // ha mas tipusu, visszaterit majd mast, most stringkent kezeli
        }
    }

    public byte[] getValueBytes(int recordIndex)
    {
        String res = "";

        for(int i = 0; i <data.get(recordIndex).length; i++)
        {
            /*if (structure.getTypeByIndex(i) == Finals.INT_TYPE) {
                res = concat(res, toBytes(Integer.parseInt(data.get(recordIndex)[i])));
            } else if (structure.getTypeByIndex(i) == Finals.STRING_TYPE) {
                res = concat(res, toBytes(data.get(recordIndex)[i]));
            }*/

            res += data.get(recordIndex)[i] +;
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
