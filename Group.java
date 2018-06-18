import java.util.ArrayList;

public class Group {

    private ArrayList<OneGroupXFunction> gXfs;
    private String groupIdentifier;

    public Group(String value, ArrayList<SQLFunction> functions) {
        gXfs = new ArrayList<>();
        this.groupIdentifier = value;

        for (SQLFunction function :
                functions) {
            gXfs.add(new OneGroupXFunction(function.getFunctionName()));
        }
    }

    public void addGXfs(OneGroupXFunction g){
        gXfs.add(g);
    }

    public ArrayList<OneGroupXFunction> getgXfs() {
        return gXfs;
    }

    public String getGroupIdentifier() {
        return groupIdentifier;
    }
}
