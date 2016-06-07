package cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class Cache {

    /*Global variables
     k, TT(Total time), line for associative*/
    public static final int n = 4096;
    public static final int lines = 16;
    public static final int k = 8;
    public static double TT = 0;
    public static int lineAssociative = 0;

    public static void main(String[] args) {
        Line[] Cache;
        int[] RAM;
        for (int i = 0; i < 4; i++) {
            TT = 0;
            RAM = new int[n];
            Cache = new Line[lines];
            fillRAM(RAM);//Reads datos.txt
            initializeCache(Cache);//Initializes every line
            sort(RAM, Cache, i);
            if (i == 0) {
                System.out.print("\nNo cache: \t");
            } else if (i == 1) {
                System.out.print("\nDirect: \t");
            } else if (i == 2) {
                System.out.print("\nAssociative: \t");
            } else {
                System.out.print("\nSet associative: ");
            }
            System.out.printf("\t%.2f\n", TT);
        }
    }

    public static void fillRAM(int[] RAM) {
        BufferedReader br = null;
        try {
            String currentLine;
            int i = 0;
            br = new BufferedReader(new FileReader("datos.txt"));
            while ((currentLine = br.readLine()) != null) {
                RAM[i] = Integer.parseInt(currentLine);
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void sort(int[] RAM, Line[] Cache, int type) {
        int n = 4096;
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                if (read(i, RAM, Cache, type) > read(j, RAM, Cache, type)) {
                    int temp = read(i, RAM, Cache, type);
                    write(i, RAM, Cache, type, read(j, RAM, Cache, type));
                    write(j, RAM, Cache, type, temp);
                }
            }
        }
    }

    public static int read(int address, int[] RAM, Line[] Cache, int type) {
        switch (type) {
            case 0:
                break;//Method for no cache
            case 1:
                break;//Method for direct
            case 2:
                break;//Method for associative
            case 3:
                break;//Method for set associative
            default:
                break;
        }
        return 0;

    }

    public static void write(int address, int[] RAM, Line[] Cache, int type, int value) {
        switch (type) {
            case 0:
                break;//Method for no cache
            case 1:
                break;//Method for direct
            case 2:
                break;//Method for associative
            case 3:
                break;//Method for set associative
            default:
                break;
        }
    }

    public static void initializeCache(Line[] Cache) {
        for (int i = 0; i < 16; i++) {
            Cache[i] = new Line(i);
        }
    }
}
