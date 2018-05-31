import com.sleepycat.je.DatabaseEntry;

public class SelectConstraint {

    private Field firstField;
    private String operand;
    private String secondField;

    public SelectConstraint(String firstField, String operand, String secondField){
        this.operand = operand;
        this.secondField = secondField;


        this.firstField = new Field(firstField);
    }

    public SelectConstraint(Field firstField, String operand, String secondField) {
        this.firstField = firstField;
        this.operand = operand;
        this.secondField = secondField;
    }

    public Field getFirstField() {
        return firstField;
    }

    public String getOperand() {
        return operand;
    }

    public String getSecondField() {
        return secondField;
    }

    public boolean checkConstrant(TableStructure ts, DatabaseEntry key, DatabaseEntry data)
    {
        Table temp = new Table(ts);
        temp.addRecord(key, data);

        if (!firstField.getTableName().equals(ts.getName()))
            return true;

        if (operand.equals(Finals.EQUALS_OPERATOR))
        {
            if (temp.getData().get(0)[temp.getIndexOfColumn(firstField.getFieldName())].equals(secondField))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            //other operators
            return false;
        }

    }
}
