package model;

public class CharacterMap {

    public static final String CHAR_MAP_PACKED =
            "\11\0\1\3\1\2\1\45\1\3\1\1\22\0\1\3\1\35\1\41"+
                    "\3\0\1\37\1\0\1\40\1\40\1\5\1\32\1\40\1\32\1\43"+
                    "\1\4\12\7\1\0\1\40\1\33\1\34\1\33\2\0\4\6\1\44"+
                    "\25\6\1\40\1\42\1\40\1\0\1\6\1\0\1\14\1\10\1\20"+
                    "\1\27\1\13\1\25\1\6\1\31\1\22\1\6\1\17\1\12\1\6"+
                    "\1\15\1\11\2\6\1\16\1\24\1\21\1\23\1\26\1\30\3\6"+
                    "\1\40\1\36\1\40\7\0\1\45\u1fa2\0\1\45\1\45\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

    public static final char [] CHAR_MAP = unpackCharMap(CHAR_MAP_PACKED);


    private static char [] unpackCharMap(String packed) {
        char[] map = new char[0x110000];
        int j = 0;
        for(int i = 0;i < packed.length();i++) {
            int count = packed.charAt(i++);
            char value = packed.charAt(i);
            do map[j++] = value; while (--count > 0);
        }
        return map;
    }
}
