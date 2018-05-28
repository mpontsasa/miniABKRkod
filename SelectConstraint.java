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
}
