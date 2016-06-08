package cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/**
 *
 * @author Agile 2016
 */
public class Cache {


    public static void main(String[] args) {
        Line[] Cache = new Line[16];
        int[] RAM = new int[4096];
        fillRAM(RAM);//Reads integers.txt (file with 4096 binary numbers) Random Numbers between 0 and 255
        initializeCache(Cache);//Initializes every line
    }

    public static void fillRAM(int[] RAM) {
        BufferedReader br = null;
        try {
            String currentLine;
            int i = 0;
            br = new BufferedReader(new FileReader("integers.txt"));
            while ((currentLine = br.readLine()) != null) {
                RAM[i] = Integer.parseInt(currentLine, 2);
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
