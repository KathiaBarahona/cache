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
    public static Line[][] sets = new Line[4][4];
    public static int[] last_lines = new int[4];

    public static void main(String[] args) {
        Line[] Cache;
        int[] RAM;
        for (int i = 0; i < 4; i++) {
            TT = 0;
            RAM = new int[n];
            Cache = new Line[lines];
            fillRAM(RAM);//Reads datos.txt

            initializeCache(Cache);//Initializes every line
            if (i == 3) {
                buildSets(Cache);//Builds sets for Set associative
            }
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

    public static void buildSets(Line[] Cache) {
        int count = 0;

        for (int i = 0; i < 4; i++) {
            Line[] newC = new Line[4];
            int count2 = 0;
            for (int j = count; j < count + 4; j++) {
                newC[count2] = Cache[j];
                count2++;
            }
            sets[i] = newC;
            last_lines[i] = 0;
            count += 4;
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
        for (int i = 0; i < n-2; i++) {
            for (int j = i + 1; j < n-1; j++) {
   
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
                return readSets(address, RAM);
            default:
                break;
        }
        return 0;

    }

    public static int readSets(int address, int[] RAM) {
        int block = address / 8;
        int word = address % 8;
        int set = block % 4;
        int tag = block / 8;
        int val = -1;

        for (int i = 0; i < sets[set].length; i++) {

            if (sets[set][i].isValid() && sets[set][i].getTag() == tag) {
                val = sets[set][i].getValue(word);
                TT += 0.01;
                break;
            }
        }
        if (val != -1) {
            return val;
        }

        if (sets[set][last_lines[set]].isValid() && sets[set][last_lines[set]].getModified()) {
            int memoryStart = sets[set][last_lines[set]].getMemoryStart();
            int memoryEnd = sets[set][last_lines[set]].getMemoryEnd();
            int pos = 0;
            for (int i = memoryStart; i <= memoryEnd; i++) {
                RAM[i] = sets[set][last_lines[set]].getValue(pos);
                pos++;
            }
            TT += 0.22;
        } else {
            TT += 0.11;
        }
        sets[set][last_lines[set]].setValue(RAM[address], word);
        sets[set][last_lines[set]].setValid(true);
        sets[set][last_lines[set]].setModified(false);
        sets[set][last_lines[set]].setMemoryStart(block * 8);
        sets[set][last_lines[set]].setMemoryEnd(block * 8 + 7);
        int pos = 0;
        for (int i = sets[set][last_lines[set]].getMemoryStart(); i <= sets[set][last_lines[set]].getMemoryEnd(); i++) {
            sets[set][last_lines[set]].setValue(RAM[i], pos);
            pos++;
        }
        sets[set][last_lines[set]].setTag(tag);
        val = sets[set][last_lines[set]].getValue(word);
        last_lines[set]++;
        if (last_lines[set] == 4) {
            last_lines[set] = 0;
        }

        return val;

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
                writeSets(address, RAM, value);
                break;//Method for set associative
            default:
                break;
        }
    }

    public static void writeSets(int address, int[] RAM, int value) {
        int block = address / 8;
        int word = address % 8;
        int set = block % 4;
        int tag = block / 8;
        boolean flag = false;
        for (int i = 0; i < sets[set].length; i++) {
            if (sets[set][i].isValid() && sets[set][i].getTag() == tag ) {
                sets[set][i].setModified(true);
                sets[set][i].setValue(value, word);
                TT += 0.01;
                flag = true;
                break;
            }
        }

        if (!flag) {

            if (sets[set][last_lines[set]].isValid() && sets[set][last_lines[set]].getModified()) {
                int memoryStart = sets[set][last_lines[set]].getMemoryStart();
                int memoryEnd = sets[set][last_lines[set]].getMemoryEnd();
                int pos = 0;
                for (int i = memoryStart; i <= memoryEnd; i++) {
                    RAM[i] = sets[set][last_lines[set]].getValue(pos);
                    pos++;
                }
                TT += 0.22;
            } else {
                TT += 0.11;
            }
            sets[set][last_lines[set]].setModified(true);
            sets[set][last_lines[set]].setMemoryStart(block * 8);
            sets[set][last_lines[set]].setMemoryEnd(block * 8 + 7);
            int pos = 0;
            for (int i = sets[set][last_lines[set]].getMemoryStart(); i <= sets[set][last_lines[set]].getMemoryEnd(); i++) {
                sets[set][last_lines[set]].setValue(RAM[i], pos);
                pos++;
            }
            sets[set][last_lines[set]].setValue(value, word);
            sets[set][last_lines[set]].setTag(tag);
            last_lines[set]++;
            if (last_lines[set] == 4) {
                last_lines[set] = 0;
            }

        }

    }

    public static void initializeCache(Line[] Cache) {
        for (int i = 0; i < 16; i++) {
            Cache[i] = new Line();
        }
    }
}
