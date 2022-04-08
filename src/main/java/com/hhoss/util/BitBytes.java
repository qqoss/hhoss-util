package com.hhoss.util;

/*
from  sun.security.util.BitArray
 */


import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * A packed array of booleans.
 *
 * @author kejun
 */

public class BitBytes {

    private byte[] repn;
    private int length;

    private static final int USIZE = Byte.SIZE;//store unit size

    private static int subscript(int idx) {
        return idx / USIZE;
    }

    private static int position(int idx) { // bits big-endian in each unit
        return 1 << (USIZE - 1 - (idx % USIZE));
    }

    /**
     * Creates a BitArray of the specified size, initialized to zeros.
     */
    public BitBytes(int length) throws IllegalArgumentException {
        if (length < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }

        this.length = length;

        repn = new byte[(length + USIZE - 1)/USIZE];
    }


    /**
     * Creates a BitArray of the specified size, initialized from the
     * specified byte array.  The most significant bit of a[0] gets
     * index zero in the BitArray.  The array a must be large enough
     * to specify a value for every bit in the BitArray.  In other words,
     * 8*a.length <= length.
     */
    public BitBytes(int length, byte[] a) throws IllegalArgumentException {

        if (length < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }
        if (a.length * USIZE < length) {
            throw new IllegalArgumentException("Byte array too short to represent " +
                                               "bit array of given length");
        }

        this.length = length;

        int repLength = ((length + USIZE - 1)/USIZE);
        int unusedBits = repLength*USIZE - length;
        byte bitMask = (byte) (0xFF << unusedBits);

        /*
         normalize the representation:
          1. discard extra bytes
          2. zero out extra bits in the last byte
         */
        repn = new byte[repLength];
        System.arraycopy(a, 0, repn, 0, repLength);
        if (repLength > 0) {
            repn[repLength - 1] &= bitMask;
        }
    }

    /**
     * Create a BitArray whose bits are those of the given array
     * of Booleans.
     */
    public BitBytes(boolean[] bits) {
        length = bits.length;
        repn = new byte[(length + 7)/8];

        for (int i=0; i < length; i++) {
            set(i, bits[i]);
        }
    }


    /**
     *  Copy constructor (for cloning).
     */
    private BitBytes(BitBytes ba) {
        length = ba.length;
        repn = ba.repn.clone();
    }

    /**
     *  Returns the indexed bit in this BitArray.
     */
    public boolean get(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
        }

        return (repn[subscript(index)] & position(index)) != 0;
    }

    /**
     *  Sets the indexed bit in this BitArray.
     */
    public void set(int index, boolean value)
    throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
        }
        int idx = subscript(index);
        int bit = position(index);

        if (value) {
            repn[idx] |= bit;
        } else {
            repn[idx] &= ~bit;
        }
    }

    /**
     * Returns the length of this BitArray.
     */
    public int length() {
        return length;
    }

    /**
     * Returns a Byte array containing the contents of this BitArray.
     * The bit stored at index zero in this BitArray will be copied
     * into the most significant bit of the zeroth element of the
     * returned byte array.  The last byte of the returned byte array
     * will be contain zeros in any bits that do not have corresponding
     * bits in the BitArray.  (This matters only if the BitArray's size
     * is not a multiple of 8.)
     */
    public byte[] toByteArray() {
        return repn.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || !(obj instanceof BitBytes)) return false;

        BitBytes ba = (BitBytes) obj;

        if (ba.length != length) return false;

        for (int i = 0; i < repn.length; i += 1) {
            if (repn[i] != ba.repn[i]) return false;
        }
        return true;
    }

    /**
     * Return a boolean array with the same bit values a this BitArray.
     */
    public boolean[] toBooleanArray() {
        boolean[] bits = new boolean[length];

        for (int i=0; i < length; i++) {
            bits[i] = get(i);
        }
        return bits;
    }

    /**
     * Returns a hash code value for this bit array.
     *
     * @return  a hash code value for this bit array.
     */
    public int hashCode() {
        int hashCode = 0;

        for (int i = 0; i < repn.length; i++)
            hashCode = 31*hashCode + repn[i];

        return hashCode ^ length;
    }


    public Object clone() {
        return new BitBytes(this);
    }


    private static final byte[][] NYBBLE = {
        {'0','0','0','0'}, {'0','0','0','1'}, {'0','0','1','0'}, {'0','0','1','1'},
        {'0','1','0','0'}, {'0','1','0','1'}, {'0','1','1','0'}, {'0','1','1','1'},
        {'1','0','0','0'}, {'1','0','0','1'}, {'1','0','1','0'}, {'1','0','1','1'},
        {'1','1','0','0'}, {'1','1','0','1'}, {'1','1','1','0'}, {'1','1','1','1'}
    };

    private static final int LINE_BYTES = 8;

    /**
     *  Returns a string representation of this BitArray.
     */
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (int i = 0; i < repn.length - 1; i++) {
            out.write(NYBBLE[(repn[i] >> 4) & 0x0F], 0, 4);
            out.write(NYBBLE[repn[i] & 0x0F], 0, 4);

            if (i % LINE_BYTES == LINE_BYTES - 1) {
                out.write('\n');
            } else {
                out.write(' ');
            }
        }

        // in last byte of repn, use only the valid bits
        for (int i = USIZE * (repn.length - 1); i < length; i++) {
            out.write(get(i) ? '1' : '0');
        }

        return new String(out.toByteArray());

    }

    public BitBytes truncate() {
        for (int i=length-1; i>=0; i--) {
            if (get(i)) {
                return new BitBytes(i+1, Arrays.copyOf(repn, (i + USIZE)/USIZE));
            }
        }
        return new BitBytes(1);
    }

}
