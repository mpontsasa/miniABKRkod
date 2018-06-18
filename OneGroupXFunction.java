public class OneGroupXFunction {

    private Integer partResult;
    private Integer counter;//used in AVG() and COUNT()
    private Field groupArg;

    OneGroupXFunction(String functionName)
    {
        counter = 0;
        switch(functionName)
        {
            case("SUM"):
                partResult = 0;
                break;
            case("AVG"):
                partResult = 0;
                break;
            case("MIN"):
                partResult = null;
                break;
            case("MAX"):
                partResult = null;
                break;
            case("COUNT"):
                partResult = null;
                break;
        }
    }

    public Integer getPartResult() {
        return partResult;
    }

    public void getResult(int value){}

    public void evaluate(int value){}
}
