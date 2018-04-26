public class ColumnPath {

    private String table;
    private String column;

    public ColumnPath(String table, String column) {
        this.table = table;
        this.column = column;
    }

    public String getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }
}
