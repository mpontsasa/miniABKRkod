public class Field {
    private String tableName;
    private String fieldName;

    public Field(String tableName, String fieldName) {
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    public Field(String s){
        String[] oi = s.split("\\.");
        this.tableName = oi[0];
        this.fieldName = oi[1];
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
