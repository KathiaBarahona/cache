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
    public static double TT;
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
            fillRAM(RAM);
            initializeCache(Cache);

            switch (i) {
                case 0:
                    sort(RAM, Cache, 0);
                    System.out.print("\nNo cache: \t");
                   
                    break;
                case 1:
                    sort(RAM, Cache, 1);
                    System.out.print("\nDirect: \t");
                   
                    break;
                case 2:
                    sort(RAM, Cache, 2);
                   
                    System.out.print("\nAssociative: \t");
                    break;
                default:
                    //Builds sets for Set associative
                    buildSets(Cache);
                    sort(RAM, Cache, i);
                    System.out.print("\nSet associative: ");
               
                    break;
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
            //System.out.println(e);
        }
    }

    public static void initializeCache(Line[] Cache) {
        for (int i = 0; i < 16; i++) {
            Cache[i] = new Line();
        }
    }

    public static void sort(int[] RAM, Line[] Cache, int type) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

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
                return readNoCache(address, RAM, Cache);
            case 1:
                return readDirect(address, RAM, Cache);
            case 2:
                return readAssociative(address, RAM, Cache);
            case 3:
                return readSets(address, RAM);
            default:
                break;
        }
        return 0;

    }

    public static int readNoCache(int address, int[] RAM, Line[] Cache){
        TT += 0.1;
        return RAM[address];
    }
            
    public static int readDirect(int address, int[] RAM, Line[] Cache) {

        int line = (address / k) % lines;

        if (Cache[line].isValid()) {
            if (address >= Cache[line].getMemoryStart() && address <= Cache[line].getMemoryEnd()) {
                TT += 0.01;
                return RAM[address];
            } else if (Cache[line].getModified()) {
                Cache[line].setMemoryStart(address);
                Cache[line].setMemoryEnd(Cache[line].getMemoryStart() + k - 1);
                Cache[line].setModified(false);
                TT += 0.22;
                return RAM[address];
            } else {
                Cache[line].setMemoryStart(address);
                Cache[line].setMemoryEnd(Cache[line].getMemoryStart() + k - 1);
                Cache[line].setModified(false);
                TT += 0.11;
                return RAM[address];
            }
        } else {
            Cache[line].setMemoryStart(address);
            Cache[line].setMemoryEnd(Cache[line].getMemoryStart() + k - 1);
            Cache[line].setModified(false);
            TT += 0.11;
            return RAM[address];
        }
    }

    public static int readAssociative(int address, int[] RAM, Line[] Cache) {
        int tag = address / k;
        for (int i = 0; i < Cache.length; i++) {
            if (Cache[i].isValid()) {
                if (address >= Cache[i].getMemoryStart() && address <= Cache[i].getMemoryEnd()) {
                    TT += 0.01;
                    return RAM[address];
                }
            }
        }

        if (Cache[lineAssociative].isValid()) {
            if (Cache[lineAssociative].getModified()) {
                TT += 0.22;
            } else {
                TT += 0.11;
            }
        } else {
            TT += 0.11;
        }
        Cache[lineAssociative].setMemoryStart(tag * k);
        Cache[lineAssociative].setMemoryEnd(Cache[lineAssociative].getMemoryStart() + k - 1);
        Cache[lineAssociative].setModified(false);
        lineAssociative++;
        if (lineAssociative >= lines) {
            lineAssociative = 0;
        }
        return RAM[address];
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

    public static int readSets(int address, int[] RAM) {
        int block = address / k;
        int word = address % k;
        int set = block % 4;
        int tag = block / 4;
        int val = -1;

        for (int i = 0; i < sets[set].length; i++) {

            if (sets[set][i].getTag() == tag) {
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
                writeNoCache(address, RAM, Cache, value);
                break;//Method for no cache
            case 1:
                writeDirect(address, RAM, Cache, value);
                break;
            case 2:
                writeAssociative(address, RAM, Cache, value);
                break;
            case 3:
                writeSets(address, RAM, value);
                break;
            default:
                break;
        }
    }

    public static void writeNoCache(int address, int[] RAM, Line[] Cache, int value){
        TT += 0.1;
        RAM[address] = value;
    }
    
    public static void writeDirect(int address, int[] RAM, Line[] Cache, int value) {
        int line = (address / k) % lines;

        if (Cache[line].isValid()) {
            if (address >= Cache[line].getMemoryStart() && address <= Cache[line].getMemoryEnd()) {
                TT += 0.01;
                Cache[line].setModified(true);
                RAM[address] = value;
            } else if (Cache[line].getModified()) {
                Cache[line].setMemoryStart((address / k) * k);
                Cache[line].setMemoryEnd(Cache[line].getMemoryStart() + k - 1);
                Cache[line].setModified(true);
                TT += 0.22;
                RAM[address] = value;
            } else {
                Cache[line].setMemoryStart((address / k) * k);
                Cache[line].setMemoryEnd(Cache[line].getMemoryStart() + k - 1);
                Cache[line].setModified(true);
                TT += 0.11;
                RAM[address] = value;
            }
        } else {
            Cache[line].setMemoryStart((address / k) * k);
            Cache[line].setMemoryEnd(Cache[line].getMemoryStart() + k - 1);
            Cache[line].setModified(true);
            TT += 0.11;
            RAM[address] = value;
        }
    }

    public static void writeAssociative(int address, int[] RAM, Line[] Cache, int value) {
        for (int i = 0; i < Cache.length; i++) {
            if (Cache[i].isValid()) {
                if (address >= Cache[i].getMemoryStart() && address <= Cache[i].getMemoryEnd()) {
                    TT += 0.01;
                    RAM[address] = value;
                    Cache[i].setModified(true);
                    return;
                }
            }
        }

        if (Cache[lineAssociative].isValid()) {
            if (Cache[lineAssociative].getModified()) {
                TT += 0.22;
            } else {
                TT += 0.11;
            }
        } else {
            TT += 0.11;
        }
        int b = address / k;
        Cache[lineAssociative].setMemoryStart(b * k);
        Cache[lineAssociative].setMemoryEnd(Cache[lineAssociative].getMemoryStart() + k - 1);
        Cache[lineAssociative].setModified(true);
        lineAssociative++;

        if (lineAssociative >= lines) {
            lineAssociative = 0;
        }
        RAM[address] = value;
    }

    public static void writeSets(int address, int[] RAM, int value) {
        int block = address / 8;
        int word = address % 8;
        int set = block % 4;
        int tag = block / 4;
        boolean flag = false;
        for (int i = 0; i < sets[set].length; i++) {
            if (sets[set][i].isValid() && sets[set][i].getTag() == tag) {
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
}
