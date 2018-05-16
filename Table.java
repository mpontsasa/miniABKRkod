import com.sleepycat.je.*;

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {

    private TableStructure structure;
    private ArrayList<String[]> data;

    public Table(ArrayList<Field> selected, ArrayList<Pair> joins, ArrayList<Pair> constraints, SQLDatabaseStructure sqlDatabaseStructure, ActiveEnviornment activeEnviornment)// a constraints nem pairs hanem Constraint lesz
    {
        //......................................beallitom az elso tablanak
        structure = sqlDatabaseStructure.findTable(selected.get(0).getTableName());
        data = new ArrayList<>();
        try
        {
            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(selected.get(0).getTableName());

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                //IDE KELL A CONSTRAINT ELLENORZESE
                this.addRecord(foundKey, foundData);
            }
            activeEnviornment.closeCursor(cursor);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < structure.getColumns().size(); i++)
        {
            ColumnStructure cs = structure.getColumns().get(i);
            boolean need = false;
            for(Field f : selected)
            {
                if (cs.getName().equals(f.getFieldName()) && selected.get(0).getTableName().equals(f.getTableName()))
                    need = true;
            }

            for (Pair p : joins)
            {
                if (cs.getName().equals(p.getFirst().getFieldName()) && selected.get(0).getTableName().equals(p.getFirst().getTableName()))
                    need = true;

                if (cs.getName().equals(p.getSecond().getFieldName()) && selected.get(0).getTableName().equals(p.getSecond().getTableName()))
                    need = true;

            }

            if (!need)
            {
                removeColumn(cs.getName());
                i--;
            }
        }

        //........................eddig benne van az elso mezohoz tartozo tabla Constraint szerint szurve


        print();

    }

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

    public int getIndexOfColumn(String columnName){

        return structure.getIndexOfColumn(columnName);

    }

    public String getKey(int recordIndex){

        //int keyIndex = structure.getKeyIndex();

        return data.get(recordIndex)[structure.getKeyIndex()];
    }

    public void addIndexRecord(String indexKey, String indexData){

        data.add(new String[]{indexKey, indexData});
    }

    public void addRecord(DatabaseEntry keyEntry, DatabaseEntry dataEntry){

        String keyString = new String(keyEntry.getData());
        String dataString = new String(dataEntry.getData());

        //System.out.println("key:" + keyString + "\ndataRecord:" + dataString);

        int keyIndex = structure.getKeyIndex();

        String[] dataRecord  = dataString.split(Finals.DATA_DELIMITER);

        String[] result = new String[dataRecord.length + 1];

        int i = 0;
        for ( i = 0; i < keyIndex; i++){
            result[i] = dataRecord[i];
        }
        result[i] = keyString;i++;
        System.arraycopy(dataRecord, i - 1, result, i, result.length - i);

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

    public void removeColumn(String columnName)
    {
        int index = structure.getIndexOfColumn(columnName);

        structure.getColumnStructure(index).setPrimaryKey(false);

        try
        {
            structure.removeColumnByName(columnName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        for (int j = 0; j < data.size(); j++)
        {
            String[] record = data.get(j);
            for (int i = index; i < record.length - 1; i++)
            {
                record[i] = record[i + 1];
            }
            //data.get(j) = Arrays.copyOf(record, record.length-1);
            data.set(j, Arrays.copyOfRange(data.get(j), 0, data.get(j).length -1));
        }
    }

    public void print()
    {
        structure.printHeader();

        for (String[] record : data)
        {
            String toPrint = "";
            for (String field : record)
            {
                toPrint += field + "\t";
            }

            System.out.println(toPrint);
        }

    }

    public byte[] getValueBytes(int recordIndex) {
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

    public byte[] getIndexValueBytes(int recordIndex) {
        String res = data.get(recordIndex)[1];
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

    byte[] toBytes(int i) {
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

    int getRecordCount()
    {
        return data.size();
    }

    public TableStructure getStructure() {
        return structure;
    }

    public ColumnStructure getColumnStructure(int index){
        return structure.getColumnStructure(index);
    }
}
