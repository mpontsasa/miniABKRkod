public class InvalidSQLCommandException extends Exception {
    public String msg;

    public InvalidSQLCommandException(String msg) {
        this.msg = msg;
    }
}
