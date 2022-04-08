package com.hhoss.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Arrays;

/**
 * @author kejun
 *
 */
public class BitArray {
	private static final int USIZE=Long.SIZE;//stored every unit size
	private static final long l = 1l;
	private int count;
	private long[] data;
	private boolean scalable;//flexible
	
	/**
	 * allowed scalable, and preAssign empty data array.
	 */
	public BitArray() {
		this.scalable=true;
		this.data=new long[0];
	}
	
	/**
	 * create a flexible BitArray and preAssign data array
	 * @param bits the total numbers of preAssign bits
	 */
	public BitArray(int bits) {
		this(bits,true);
	}
	/**
	 * create a BitArray and preAssign data array
	 * @param bits the total numbers of preAssign bits
	 */
	
	public BitArray(int bits, boolean scalable) {
		this.scalable=scalable;
		int size=bits%USIZE==0?bits/USIZE:bits/USIZE+1;
		data = new long[size];
	}
	
	/**
	 * @param f file of the bitArray which serialized in file
	 * @throws IOException
	 */
	public BitArray(File f) throws IOException {
		this(read(f));
	}

	/**
	 * @param data tha array of values
	 * Used by deserialization 
	 */
	public BitArray(long[] data) {
		this.data = data;
		this.reCount();
	}	
	

    /**
     * @param idx
     * @return the substitution index of the unit include idx bit  
     */
    private static int sub(int idx) {
        return idx/USIZE;
        //return idx>>6;
    }

    /**
     * @param idx
     * @return the long value of the data which equals the bit is 1 on the idx position
     */
    private static long bit(int idx) { 
    	return l<<(idx%USIZE) ;
    	//return l<<idx;
    	//TODO: bits big-endian in each unit
        // return l << (USIZE - 1 - (idx % USIZE));
    }

	/**
	 * ensure the data array which has capability size
	 * @param size adjust the array length to size
	 */
	private void flex(int size){
		data=Arrays.copyOf(data, size + 1);   
	}
	
	/**
	 * ensure the data array which can assign bit[idx]
	 * @param idx index of the special bit
	 */
	private void ensureSize(int idx){
		if(scalable && idx>=length()){
			flex(sub(idx));
		}
	}

	/** Returns true if the bit changed value. */
	public boolean set(int idx) {
		ensureSize(idx);
		if (!get(idx)) {
			data[sub(idx)] |= bit(idx);
			count++;
			return true;
		}
		return false;
	}

	/** Returns true if the bit changed value. */
	public boolean set(int idx, boolean b) {
		return b?set(idx):clear(idx);
	}
	
	/** Returns true if the bit changed value. */
	public boolean clear(int idx) {
		if (get(idx)) {
			data[sub(idx)] &= ~bit(idx);
			count--;
			return true;
		}
		return false;
	}
	
	/**
	 * @param ba the bitArray to be removed.
	 * @return the result which param BitArray has been removed
	 */
	public BitArray remove(BitArray ba) {
		int len = Math.min(dataSize(), ba.dataSize());
		for (int i = 0; i < len; i++) {
			count-=Long.bitCount(data[i]);
			data[i] &= ~ba.data[i];
			count+=Long.bitCount(data[i]);
		}
		
		return this;
	}

	/**
	 * @param idx
	 * @return true if the bit of idx is 1 
	 * false when idx is 0 or not exists / out of index;
	 */
	public boolean get(int idx) {
		return length()>idx && (data[sub(idx)] & bit(idx)) != 0;
	}

	/**
	 * @param index
	 * @return Long of the idx bit belong to
	 */
	public long dataWith(int idx) {
		return data[sub(idx)];
	}

	/** Number of bits */
	public int length() {
		return data.length * USIZE;
	}

	/** length of data array lenth */
	public int dataSize() {
		return data.length;
	}

	/** Number of set bits (1s) */
	public int bitCount() {
		return count;
	}

	public BitArray copy() {
		return new BitArray(data.clone());
	}

	/**
	 * the two bitArray should has same length.
	 * @param array Combines the two BitArrays using bitwise OR. 
	 * @return BitArray merged BitArray using bitwise OR.
	 */
	public BitArray add(BitArray ba) {
		if (dataSize() != ba.dataSize()) {
			throw new IllegalArgumentException("BitArrays must be of equal length ");
		}
		for (int i = 0; i < data.length; i++) {
			data[i] |= ba.data[i];
		}
		reCount();
		return this;
	}
	
	/**
	 * @param BitArray another to merge
	 * @return BitArray merged BitArray using bitwise OR.
	 */
	public BitArray merge(BitArray ba) {
		if(ba==null||ba.bitCount()==0){return this;}
		if(dataSize()==ba.dataSize()){return add(ba);}
		long[] large = ba.dataSize()>dataSize()?ba.data:data;
		long[] small = ba.dataSize()>dataSize()?data:ba.data;
		long[] array = large.clone();
		for (int i = 0; i < small.length; i++) {
			array[i] |= small[i];
		}
		data=array;
		reCount();
		return this;
	}
	
	/**
	 * @return count the total number of bit which is 1
	 */
	private int reCount(){
		int c = 0;
		for (int i = 0; i < data.length; i++) {
			c += Long.bitCount(data[i]);
		}
		return count=c;
	}

    /**
     * remove the empty data in the tail 
     * @return
     */
    public BitArray truncate() {
    	int i=dataSize();
        for(;i-->0&&data[i]==0;);
        flex(i);        
        return this;
    }

	@Override
	public boolean equals(Object o) {
		if (o instanceof BitArray) {
			BitArray ba = (BitArray) o;
			return Arrays.equals(data, ba.data);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
	
    public boolean[] toBooleans() {
        boolean[] bits = new boolean[length()];

        for (int i=0; i < bits.length; i++) {
            bits[i] = get(i);
        }
        return bits;
    }
	
	public int store(File file){
		file.getParentFile().mkdirs();
		try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))){
			for(long d:data) dos.writeLong(d);
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return count;
	}	

	private static long[] read(File file) throws IOException{
		long bytesLen = file.length();
		if(bytesLen%8!=0){
			throw new InvalidObjectException(file.getName()+" bytes.length should be multi of 8");
		}
		long[] datum = new long[(int)bytesLen/8];		
		try(DataInputStream dis = new DataInputStream(new FileInputStream(file))){
			for(int i=0;i<datum.length;i++)
			datum[i]=dis.readLong();			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return datum;
	}
	
	public static BitArray from(File file){
		try {
			return new BitArray(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    private static final byte[] BITLE = {'0','1'};
    private static final byte[][] NYBBLE = {
        { '0','0','0','0'}, { '0','0','0','1'}, { '0','0','1','0'}, { '0','0','1','1'},
        { '0','1','0','0'}, { '0','1','0','1'}, { '0','1','1','0'}, { '0','1','1','1'},
        { '1','0','0','0'}, { '1','0','0','1'}, { '1','0','1','0'}, { '1','0','1','1'},
        { '1','1','0','0'}, { '1','1','0','1'}, { '1','1','1','0'}, { '1','1','1','1'}
    };

    /**
     *  Returns a string representation of this BitArray.
     */
    public String toString() {
    	if(data.length>16){
    		return "the data is greater than 1024 bits....";
    	}
    	return toString0(4);
    }

    
    /**
     * 低位先输出，高位后输出
     * @param bl break lines
     * @return
     */
    protected String toString0(int bl) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < data.length; i++) {
            if(bl>0&&i%bl==0&&i>0){out.write('\n');} 
 			out.write(charBytes0(data[i]),0,64);
        }
        return new String(out.toByteArray());
    }

    /**
     * 高位先输出，低位后输出
     * @param bl break lines
     * @return
     */
    protected String toString1(int bl) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < data.length; i++) {
            if(bl>0&&i%bl==0&&i>0){out.write('\n');} 
 			out.write(charBytes(data[i]),0,64);
        }
        return new String(out.toByteArray());
    }
  
    /**
     * 高位先输出，低位后输出
     * @param bl
     * @return
     */
    protected String toString2(int bl) {//same with the out.write(long)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < data.length; i++) {
            if(bl>0&&i%bl==0&&i>0){out.write('\n');} 
        	int f=USIZE;
        	while(f-->0)out.write(BITLE[(int)((data[i]>>>f)&1L)]);
       }
        /* in last byte of data, use only the valid bits
        for (int i = USIZE * (data.length - 1); i < length(); i++) {
            out.write(get(i) ? BITLE[1] : BITLE[0]);
        }
		*/
        return new String(out.toByteArray());
    }
    


    /**
     * 高4位先输出，低位4后输出，等同高位先输出
     * @param bl break lines
     * @return
     */
    protected String toString3(int bl) {
    	long mask = 0x0F;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < data.length; i++) {
            if(bl>0&&i%bl==0&&i>0){out.write('\n');} 
            for(int f=15;f>=0;f--){
            	out.write(NYBBLE[(int)((data[i]>>>(f*4))&mask)],0,4);
            };    
        }
        return new String(out.toByteArray());
    }
 
    /**
     * 低4位先输出，高4位后输出,无意义
     * @param bl
     * @return
     */
    protected String toString4(int bl) {
    	long mask = 0x0F;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < data.length; i++) {
            if(bl>0&&i%bl==0&&i>0){out.write('\n');} 
            
        	long n = data[i];
        	for(int f=16;f>0;f--){
            	out.write(NYBBLE[(int)(n&mask)],0,4);
                n >>>= 4;
            };            
        }
        return new String(out.toByteArray());
    }

    private static byte[] charBytes(long i) {
    	byte[] buf = new byte[64];
        int pos = 64;
        long mask = 1L;
        do {
            buf[--pos] = BITLE[(int)(i&mask)];
            i >>>= 1;
        } while(pos > 0);
        return buf;
    }
    
    private static byte[] charBytes0(long n) {
    	byte[] buf = new byte[64];
        int i = 0;
        while(i < 64) {
            buf[i++] = BITLE[(int)(n&1L)];
            n >>>= 1;
        }
        return buf;
    }
    
}