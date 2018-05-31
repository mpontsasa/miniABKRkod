public class Pair {

    Field first;
    Field second;

    public  Pair(String f, String s){


        this.first = new Field(f);

        this.second = new Field(s);
    }

    public Pair(Field first, Field second) {
        this.first = first;
        this.second = second;
    }

    public Pair(String firstTableName, String firstFieldName, String secondTableName, String secondFieldName) {
        this.first = new Field(firstTableName, firstFieldName);
        this.second = new Field(secondTableName, secondFieldName);
    }

    public Field getFirst() {
        return first;
    }

    public void setFirst(Field first) {
        this.first = first;
    }

    public Field getSecond() {
        return second;
    }

    public void setSecond(Field second) {
        this.second = second;
    }

    public void swap()
    {
        Field temp = first;
        first = second;
        second = temp;
    }
}
