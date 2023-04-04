package model;

public class RowMap {

    public static final int [] ROW_MAP = unpackRowMap();

    private static final String ROW_MAP_PACKED =
            "\0\0\0\46\0\114\0\46\0\162\0\46\0\230\0\276"+
                    "\0\344\0\u010a\0\u0130\0\u0156\0\u017c\0\u01a2\0\u01c8\0\u01ee"+
                    "\0\u0214\0\u023a\0\u0260\0\u0260\0\u0286\0\u02ac\0\46\0\u02d2"+
                    "\0\u02f8\0\46\0\u031e\0\u0344\0\u036a\0\u0390\0\u03b6\0\u03dc"+
                    "\0\u0402\0\u0428\0\u044e\0\u0474\0\u049a\0\230\0\u04c0\0\u04e6"+
                    "\0\u050c\0\u0532\0\u0558\0\46\0\46\0\46\0\46\0\u057e"+
                    "\0\u05a4\0\46\0\u05ca\0\u05f0\0\u0616\0\u063c\0\u0662\0\u0688"+
                    "\0\u06ae\0\u06d4\0\u06fa\0\u0720\0\u0746\0\u076c\0\u0792\0\u07b8"+
                    "\0\u07de\0\u02d2\0\u0804\0\u082a\0\u0850\0\u0876\0\u089c\0\230"+
                    "\0\u08c2\0\u0804\0\u08e8\0\u090e\0\u0934\0\u095a";

    private static int [] unpackRowMap() {
        int [] result = new int[78];
        unpackRowMap(ROW_MAP_PACKED, result);
        return result;
    }

    private static void unpackRowMap(String packed, int [] result) {
        int j = 0;
        for(int i = 0;i < packed.length();i++) {
            int high = packed.charAt(i++) << 16;
            result[j++] = high | packed.charAt(i);
        }
    }
}
