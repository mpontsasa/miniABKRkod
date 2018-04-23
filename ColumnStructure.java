import org.json.JSONObject;

public class ColumnStructure {
    private boolean isPrimaryKey;
    private boolean isForeignKey;
    private String foreignReferenceName;
    private String name;
    private String type;//from Finals

    //EZEKET UGY GONDOLTAM JO OTLET MEGIRNI DE CSAK AZ UTOLSOT SZERETNEM HASZNALNI VEHHULIS
//    public ColumnStructure(String name, String type) {
//        this.name = name;
//        this.type = type;
//    }
//
//    public ColumnStructure(String name, String type,boolean isForeignKey, String foreignReferenceName) {
//        this.isForeignKey = isForeignKey;
//        this.foreignReferenceName = foreignReferenceName;
//        this.name = name;
//        this.type = type;
//    }
//
//    public ColumnStructure(String name, String type, boolean isPrimaryKey) {
//        this.isPrimaryKey = isPrimaryKey;
//        this.name = name;
//        this.type = type;
//    }

    public ColumnStructure(String name, String type,boolean isPrimaryKey, boolean isForeignKey, String foreignReferenceName) {
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
        if((foreignReferenceName == null) || (foreignReferenceName.equals(""))){
            this.foreignReferenceName = "";
        }
        else{
            this.foreignReferenceName = foreignReferenceName;
        }
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString(){
        String type = null;
        String foreign = null;
        String reference = null;
        String primary = null;

        switch (this.type){
            case Finals.INT_TYPE:
                type = "INT";
                break;
            case Finals.STRING_TYPE:
                type = "STRING";

        }

        String result = name + Finals.COLUMN_TO_STRING_SEPARATOR + type;

        if(isPrimaryKey){
            primary = Finals.IS_PRIMARY_TO_STRING;
            result = result + Finals.COLUMN_TO_STRING_SEPARATOR + primary;
        }
        if(isForeignKey){
            foreign = Finals.IS_FOREIGN_TO_STRING;
            reference = foreignReferenceName;

            result = result + Finals.COLUMN_TO_STRING_SEPARATOR + foreign + Finals.COLUMN_TO_STRING_SEPARATOR + reference;

        }



        return result;

    }

    public JSONObject toJson(){



//        String result = type;
//
//        if(isPrimaryKey){
//            primary = Finals.IS_PRIMARY_TO_STRING;
//            result = result + Finals.COLUMN_TO_STRING_SEPARATOR + primary;
//        }
//        if(isForeignKey){
//            foreign = Finals.IS_FOREIGN_TO_STRING;
//            reference = foreignReferenceName;
//
//            result = result + Finals.COLUMN_TO_STRING_SEPARATOR + foreign + Finals.COLUMN_TO_STRING_SEPARATOR + reference;
//
//        }

        JSONObject columnJSON = new JSONObject();

        columnJSON.put(Finals.JSON_COLUMN_NAME_KEY,name);
        columnJSON.put(Finals.JSON_COLUMN_TYPE_KEY, type);
        columnJSON.put(Finals.JSON_COLUMN_ISPRIMARY_KEY,isPrimaryKey);
        columnJSON.put(Finals.JSON_COLUMN_ISFOREIGN_KEY, isForeignKey);
        columnJSON.put(Finals.JSON_COLUMN_REFERENCE_KEY, foreignReferenceName);


        return columnJSON;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public String getForeignReferenceName() {
        return foreignReferenceName;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
