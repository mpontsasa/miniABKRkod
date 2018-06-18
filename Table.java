import com.sleepycat.je.*;

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {

    private TableStructure structure;
    private ArrayList<String[]> data;

    public Table(ArrayList<Field> selected, ArrayList<Pair> joins, ArrayList<SelectConstraint> constraints, GroupByConstraint gbconstraint, SQLDatabaseStructure sqlDatabaseStructure, ActiveEnviornment activeEnviornment) throws Exception// a constraints nem pairs hanem Constraint lesz
    {
        //......................................beallitom az elso tablanak
        structure = new TableStructure(sqlDatabaseStructure.findTable(selected.get(0).getTableName()));
        data = new ArrayList<>();
        try
        {
            Cursor cursor = null;
            cursor = activeEnviornment.getCursor(selected.get(0).getTableName());

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                boolean isOk = true;
                for (SelectConstraint sc : constraints)
                {
                    if (!sc.checkConstrant(sqlDatabaseStructure.findTable(selected.get(0).getTableName()), foundKey, foundData))
                    {
                        isOk = false;
                        break;
                    }
                }
                if(isOk)
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

            for(SQLFunction f : gbconstraint.getFunctions())
            {
                if (cs.getName().equals(f.getArgument().getFieldName()) && selected.get(0).getTableName().equals(f.getArgument().getTableName()))
                    need = true;
            }

            if (cs.getName().equals(gbconstraint.getField().getFieldName()) && selected.get(0).getTableName().equals(gbconstraint.getField().getTableName()))
                need = true;

            if (!need)
            {
                removeColumn(cs.getName());
                i--;
            }
            else
            {
                cs.setOriginalTable(selected.get(0).getTableName());
            }
        }


        //........................eddig benne van az elso mezohoz tartozo tabla Constraint szerint szurve

        for (int i = 0; i < joins.size();)
        {
            int joinIndex = findNextJoin(joins);
            if (joinIndex == -1)
                throw new InvalidSQLCommandException("Tables joined inproperly.");

            Pair p = joins.get(joinIndex);
            joins.remove(joinIndex);

            hashJoin(p, sqlDatabaseStructure, constraints);

            for(int j = 0; j < structure.getColumns().size(); j++)
            {
                ColumnStructure cs = structure.getColumns().get(j);
                boolean need = false;

                if(cs.getOriginalTable() == null)
                {
                    cs.setOriginalTable(p.second.getTableName());
                }

                for(Field f : selected)
                {
                    if (cs.getName().equals(f.getFieldName()) && cs.getOriginalTable().equals(f.getTableName()))
                        need = true;
                }

                for (Pair pp : joins)
                {
                    if (cs.getName().equals(pp.getFirst().getFieldName()) && cs.getOriginalTable().equals(pp.getFirst().getTableName()))
                        need = true;

                    if (cs.getName().equals(pp.getSecond().getFieldName()) && cs.getOriginalTable().equals(pp.getSecond().getTableName()))
                        need = true;

                }

                for(SQLFunction f : gbconstraint.getFunctions())
                {
                    if (cs.getName().equals(f.getArgument().getFieldName()) && selected.get(0).getTableName().equals(f.getArgument().getTableName()))
                    need = true;
                }

                if (cs.getName().equals(gbconstraint.getField().getFieldName()) && selected.get(0).getTableName().equals(gbconstraint.getField().getTableName()))
                    need = true;


                if (!need)
                {
                    removeColumn(cs.getName());
                    j--;
                }
            }
        }

        //Innen kezdodik a group by

        for (int i = 0; i < data.size(); i ++)
        {
            for (SQLFunction f : gbconstraint.getFunctions())
            {
                f.evaluate(Integer.parseInt(data.get(i)[getIndexOfColumn(f.getArgument().getFieldName())]));
            }
        }


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

    public int findNextJoin(ArrayList<Pair> joins)
    {
        for (int i = 0; i < joins.size(); i++)
        {
            if (getIndexOfColumn(joins.get(i).getFirst().getFieldName()) != -1)
                return i;

            if (getIndexOfColumn(joins.get(i).getSecond().getFieldName()) != -1)
            {
                joins.get(i).swap();
                return i;
            }
        }

        return -1;
    }

    public void hashJoin(Pair p, SQLDatabaseStructure sqlDatabaseStructure, ArrayList<SelectConstraint> constraints)
    {

        ArrayList<Integer>[] firstHashes = new ArrayList[Finals.NR_OF_HASHES];


        for (int i = 0; i < Finals.NR_OF_HASHES; i++)
        {
            firstHashes[i] = new ArrayList<>();
        }

        //................................hash this table by p.first field

        int firstJoinIndex = structure.getIndexOfColumn(p.getFirst().getFieldName());

        for (int i = 0; i < data.size(); i++)
        {
            firstHashes[hash(data.get(i)[firstJoinIndex])].add(i);
        }

        //.................................hash second table by p.second field and match with first hashes

        ArrayList<String[]> newData = new ArrayList<>();

        try
        {
            Cursor cursor = null;
            cursor = Controller.activeEnviornment.getCursor(p.getSecond().getTableName());

            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();

            int i = 0;  //index of record
            int secondJoinIndex = Controller.sqlDatabaseStructure.findTable(p.getSecond().getTableName()).getIndexOfColumn(p.getSecond().getFieldName());

            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                Table tempTable = new Table(Controller.sqlDatabaseStructure.findTable(p.getSecond().getTableName()));
                tempTable.addRecord(foundKey, foundData);

                int kat = hash(tempTable.getData().get(0)[secondJoinIndex]);

                for (int first = 0; first < firstHashes[kat].size(); first++)    // vegigmegyunk a kategoria elemein az elso tombbol
                {
                        if (data.get(firstHashes[kat].get(first))[firstJoinIndex].equals(tempTable.getData().get(0)[secondJoinIndex]))
                        {


                            boolean isOk = true;
                            for (SelectConstraint sc : constraints)
                            {
                                if (!sc.checkConstrant(sqlDatabaseStructure.findTable(p.getSecond().getTableName()), foundKey, foundData))
                                {
                                    isOk = false;
                                    break;
                                }
                            }
                            if(isOk) {
                                String[] record = mergeRecord(data.get(firstHashes[kat].get(first)), tempTable.getData().get(0));
                                newData.add(record);
                            }
                        }
                }

                i++;
            }
            Controller.activeEnviornment.closeCursor(cursor);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        structure.mergeWith(sqlDatabaseStructure.findTable(p.getSecond().getTableName()));
        data = newData;
    }

    public String[] mergeRecord(String[] first, String[] second)
    {
        String[] res = new String[first.length + second.length];

        for (int i = 0; i < first.length; i++)
            res[i] = first[i];

        for (int i = 0; i < second.length; i++)
            res[i + first.length] = second[i];

        return res;
    }

    public int hash(String str)
    {
        int hash = 7;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash*31 + str.charAt(i)) % 7727;
        }

        return hash % Finals.NR_OF_HASHES;
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

    public ArrayList<String[]> getData() {
        return data;
    }

    public void setData(ArrayList<String[]> data) {
        this.data = data;
    }
}
