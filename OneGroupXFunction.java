import java.lang.reflect.Parameter;

public class OneGroupXFunction {

    private Integer partResult;
    private Integer counter;//used in AVG() and COUNT()
    //private Field groupArg;

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
                partResult = Integer.MAX_VALUE;
                break;
            case("MAX"):
                partResult  = Integer.MIN_VALUE;
                break;
            case("COUNT"):
                partResult = null;
                break;
        }
    }

    public Integer getPartResult() {
        return partResult;
    }

    public String getResult(String functionType){
        switch(functionType)
        {
            case("SUM"):
                return "" + partResult;
            case("AVG"):
                return "" + (float)partResult/(float)counter;

            case("MIN"):
            case("MAX"):
                return "" + partResult;

            case("COUNT"):
                return "" + counter;
        }
        return null;
    }

    public void evaluate(int value, String functionType){

        switch(functionType)
        {
            case("SUM"):
                partResult += value;
                break;
            case("AVG"):
                partResult += value;
                counter++;
                break;
            case("MIN"):
                if(value < partResult){
                    partResult = value;
                }
                break;
            case("MAX"):
                if(value > partResult){
                    partResult = value;
                }
                break;
            case("COUNT"):
                counter++;
                break;
        }
    }
}
