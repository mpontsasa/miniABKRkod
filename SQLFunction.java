public class SQLFunction {

    private String functionName;
    private Field argument;
    private Integer partResult;
    private Integer counter;//used in AVG()
    private Integer result;


    public SQLFunction(String functionName, Field argument) {
        this.functionName = functionName;
        this.argument = argument;
        partResult = counter = result = 0;
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

    public Integer getPartResult() {
        return partResult;
    }

    public void evaluate(int evaluationOption){}

    public void getResult(int evaluationOption){}
}
