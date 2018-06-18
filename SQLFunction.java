public class SQLFunction {

    private String functionName;
    private Field argument;


    public SQLFunction(String functionName, Field argument) {
        this.functionName = functionName.toUpperCase();
        this.argument = argument;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Field getArgument() {
        return argument;
    }

    public void setArgument(Field argument) {
        this.argument = argument;
    }

    public void evaluate(int value){}




}
