package model;


public class Action {

    public static final int [] ACTION = unpackAction();

    private static final String ACTION_PACKED =
            "\1\0\1\1\2\2\2\3\1\4\1\5\11\4\1\6"+
                    "\1\7\1\10\2\1\1\11\1\12\1\1\1\13\1\14"+
                    "\1\0\2\15\7\4\1\16\5\4\1\6\1\17\1\10"+
                    "\1\20\1\12\2\14\1\0\1\21\1\15\12\4\1\0"+
                    "\1\12\1\20\1\21\4\4\1\22\2\0\4\4";

    private static int [] unpackAction() {
        int [] result = new int[78];
        unpackAction(ACTION_PACKED, result);
        return result;
    }

    private static void unpackAction(String packed, int [] result) {
        int j = 0;
        for(int i = 0;i < packed.length();i++) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i);
            do result[j++] = value; while (--count > 0);
        }
    }
}
