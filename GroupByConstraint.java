import java.util.ArrayList;

public class GroupByConstraint {

    private Field field;
    private ArrayList<SQLFunction> functions;
    private ArrayList<Group> groups;


    public GroupByConstraint() {
        functions = new ArrayList<>();
        groups = new ArrayList<>();
        field = null;
    }

    public void addFunction(SQLFunction f) {
        functions.add(f);
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public ArrayList<SQLFunction> getFunctions() {
        return functions;
    }

    public void evaluate(String[] evaluatedRow,TableStructure rowStructure){

        int i;
        for (i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            ArrayList<OneGroupXFunction> gxs = group.getgXfs();



            if(group.getGroupIdentifier().
                        equals(evaluatedRow[rowStructure.getIndexOfColumn(field.getFieldName())])){
                //feldolgozunk egy groupot ha megfelelo
                for(int j = 0; j < gxs.size();j++){
                    int valueToEvaluate =
                            Integer.parseInt(
                                    evaluatedRow[
                                            rowStructure.getIndexOfColumn(functions.get(j).getArgument().getFieldName())]);
                    gxs.get(j).evaluate(valueToEvaluate,functions.get(j).getFunctionName());

                }
                break;
            }

        }
        //ha nincs egy megfelelo group se, akkor letrehozunk egyet ami megfelelo
        if(i >= groups.size()){
            groups.add(new Group(evaluatedRow[rowStructure.getIndexOfColumn(field.getFieldName())], functions));
            ArrayList<OneGroupXFunction> gxs = groups.get(groups.size()-1).getgXfs();
            for(int j = 0; j < gxs.size();j++){
                int valueToEvaluate =
                        Integer.parseInt(
                                evaluatedRow[
                                        rowStructure.getIndexOfColumn(functions.get(j).getArgument().getFieldName())]);
                gxs.get(j).evaluate(valueToEvaluate,functions.get(j).getFunctionName());

            }
        }
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }
}
