package app;



public class Constant {

    public static final int EOF = -1;
    public static final int BUFFER_SIZE = 16384;
    public static final int INITIAL = 0;
    public static final int LEX_STATE[] = {0, 0};
    public static class ERROR_STATE {
        public static final int UNKNOWN_ERROR = 0;
        public static final int NO_MATCH = 1;
        public static final int PUSHBACK_2BIG = 2;
    }
    public static final String ERROR_MSG[] = {
            "Unknown internal scanner error",
            "Error: could not match input",
            "Error: pushback value was too large"
    };
}
