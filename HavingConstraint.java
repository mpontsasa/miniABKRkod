public class HavingConstraint {

    private SQLFunction function;
    private String operator;
    private String literal;

    public SQLFunction getFunction() {
        return function;
    }

    public void setFunction(SQLFunction function) {
        this.function = function;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public boolean isEmpty() {
        return function == null || literal == null || operator == null;
    }
}
