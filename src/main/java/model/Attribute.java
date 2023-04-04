package model;


public class Attribute {

    public static final int [] ATTRIBUTE = unpackAttribute();

    private static final String ATTRIBUTE_PACKED =
            "\1\0\1\11\1\1\1\11\1\1\1\11\20\1\1\11"+
                    "\2\1\1\11\1\1\1\0\17\1\4\11\2\1\1\11"+
                    "\1\0\14\1\1\0\10\1\2\0\4\1";

    private static int [] unpackAttribute() {
        int [] result = new int[78];
        unpackAttribute(ATTRIBUTE_PACKED, result);
        return result;
    }

    private static void unpackAttribute(String packed, int [] result) {
        int j = 0;
        for(int i = 0;i < packed.length();i++) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i);
            do result[j++] = value; while (--count > 0);
        }
    }

}
