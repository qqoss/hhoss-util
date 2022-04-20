package com.hhoss.util;

import com.hhoss.lang.Constant;
// * TODO:move to com.hhoss.util
public class Num implements Constant {
	//S: size small
	public static final int S3 = 1<<8; // 0x100 = 256;
	public static final int S4 = 1<<12; // 0x1000 = 4096;
	public static final int S5 = 1<<15; // 0x8000 = 32768;
	public static final int S6 = 1<<18; // 0x40000 = 26_2144;
	public static final int S7 = 1<<22; // 0x40_0000 = 419_4304;
	public static final int S8 = 1<<25; // 0x200_0000 = 3355_4432;
	public static final int S9 = 1<<28; // 0x1000_0000 = 2_6843_5456;
	//public static final int S10  = 1<<30; // 0x1000_0000 = 10_7374_1824;

	protected static final int S61 = 1<<17; // 0x20000 = 13_1072;
	protected static final int S71 = 1<<20; // 0x10_0000 = 104_8576;
	protected static final int S72 = 1<<21; // 0x20_0000 = 209_7152;
	protected static final int S81 = 1<<24; // 0x100_0000 = 1677_7216;
	protected static final int S91 = 1<<27; // 0x800_0000 = 1_3421_7728;
	
	public static final long S10 = 1L<<32; // 0x1_0000_0000L = 42_9496_7296L;
	public static final long S11 = 1L<<35; // 0x8_0000_0000L = 343_5973_8368L;
	public static final long S12 = 1L<<38; // 0x40_0000_0000L = 2748_7790_6944L;
	public static final long S13 = 1L<<42; // 0x400_0000_0000L = 4_3980_4651_1104L;
	public static final long S14 = 1L<<45; // 0x2000_0000_0000L = 35_1843_7208_8832L;
	public static final long S15 = 1L<<48; // 0x1_0000_0000_0000L = 281_4749_7671_0656L;
	public static final long S16 = 1L<<52; // 0x10_0000_0000_0000L = 4503_5996_2737_0496L;

	public static final long S17 = 1L<<55; // 0x80_0000_0000_0000L = 3_6028_7970_1896_3968L;
	public static final long S18 = 1L<<58; // 0x400_0000_0000_0000L = 28_8230_3761_5171_1744L;
	public static final long S19 = 1L<<61; // 0x2000_0000_0000_0000L = 230_5843_0092_1369_3952L;

	protected static final long S131 = 1L<<41; // 0x200_0000_0000L = 2_1990_2325_5552L;
	protected static final long S141 = 1L<<44; // 0x1000_0000_0000L = 17_5291_8604_4416L;
	protected static final long S151 = 1L<<47; // 0x8000_0000_0000L = 140_7374_8835_5328L;
	protected static final long S161 = 1L<<50; // 0x4_0000_0000_0000L = 1125_8999_0684_2624L;

}
