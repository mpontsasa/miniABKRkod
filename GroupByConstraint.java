import java.util.ArrayList;

public class GroupByConstraint {

    private Field field;
    private ArrayList<SQLFunction> functions;


    public GroupByConstraint() {
        functions = new ArrayList<>();
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
}
