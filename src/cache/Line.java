package cache;

import java.util.Arrays;

/**
 *
 * @author Agile 2016
 */
public class Line {

    private boolean valid = false;
    private boolean modified = false;
    private int tag;
    private int[] values = new int[8];
    private int memoryStart;
    private int memoryEnd;

    public Line() {
    }

    public Line(boolean valid, boolean modified, int tag, int[] values) {
        this.valid = valid;
        this.modified = modified;
        this.tag = tag;
        this.values = values;
        this.memoryStart = 0;
        this.memoryEnd = 0;
    }//Constructor for an existing line

    public Line(int tag) {
        this.tag = tag;
    }//Constructor only to initialize tag

    /**
     * Set a value in an specific position
     *
     * @param value new value to add,
     * @param position - where to add value
     */
    public void changeValue(int value, int position) {
        this.values[position] = value;
    }

    /**
     *
     * @param position - position of the desired value
     * @return value
     */
    public int getValue(int position) {
        return this.values[position];
    }

    /**
     * Get the value of values
     *
     * @return the value of values
     */
    public int[] getValues() {
        return values;
    }

    /**
     * Set the value of values
     *
     * @param values new value of values
     */
    public void setValues(int[] values) {
        this.values = values;
    }

    /**
     * Get the value of tag
     *
     * @return the value of tag
     */
    public int getTag() {
        return tag;
    }

    /**
     * Set the value of tag
     *
     * @param tag new value of tag
     */
    public void setTag(int tag) {
        this.tag = tag;
    }

    /**
     * Get the value of modified
     *
     * @return the value of modified
     */
    public boolean getModified() {
        return modified;
    }

    /**
     * Set the value of modified
     *
     * @param modified new value of modified
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * Get the value of valid
     *
     * @return the value of valid
     */
    public boolean getValid() {
        return valid;
    }

    /**
     * Set the value of valid
     *
     * @param valid new value of valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return !(memoryStart == 0 && memoryEnd == 0);
    }
    
    @Override
    public String toString() {
        return "Line{" + "valid=" + valid + ", modified=" + modified + ", tag=" + tag + ", values=" + Arrays.toString(values) + '}';
    }

}
