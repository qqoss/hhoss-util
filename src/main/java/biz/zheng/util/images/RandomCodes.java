package biz.zheng.util.images;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

public class RandomCodes {
	private static final Random random = new Random(System.currentTimeMillis());
	private static final String charsSet = "ABCDEFGHJKLMNPQRSTUVWXY23456789";
	public static String GRAPHIC_JPEG = "JPEG"; //MIME:image/jpeg; jpg,JPG,jpeg
	public static String GRAPHIC_PNG  = "PNG"; //MIME:image/png
	private static final Font font = new Font("Times New Roman",Font.PLAIN,18);
    
	public static Color genRandomColor(int num1,int num2){
		if(num1>255||num1<0) num1=0;
		if(num2>255||num2<0) num2=255;
		
		int start = Math.min(num1,num2);
		int diff = Math.abs(num1-num2);
		int r=start+random.nextInt(diff);
		int g=start+random.nextInt(diff);
		int b=start+random.nextInt(diff);
		return new Color(r,g,b);
	}

	public static char[] genRandomChars(int length){
    	char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
        	chars[i] = charsSet.charAt(random.nextInt(charsSet.length() - 1));
        }
        return chars;
    }

    public static RenderedImage genCharsImage(char[] chars) {
    	int fontSize = 18;
    	int imgWidth = fontSize*chars.length;
    	int imgHeight= fontSize*2;
        Font font = new Font("Arial", Font.PLAIN, fontSize);
        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_3BYTE_BGR);
        java.awt.Graphics d = image.getGraphics();
        d.setFont(font);
        d.setColor(Color.WHITE);//backGround
        d.fillRect(0, 0, imgWidth, imgHeight);
        for (int i = 0; i < fontSize; i++) {
			int x1 = random.nextInt(imgWidth);
			int y1 = random.nextInt(imgHeight);
			int x2 = random.nextInt(imgWidth);
			int y2 = random.nextInt(imgHeight);
	        d.setColor(genRandomColor(100,200));
			d.drawLine(x1,y1,x2,y2);
        }
        int x,y;
        for (int i = 0; i < chars.length; i++) {
            x = fontSize*i+1 ;
            y = 14 + random.nextInt(imgHeight-14);
            d.setColor(genRandomColor(50,150));
            d.drawChars(chars, i, 1, x, y);
            //d.drawString(chars[i] + "", x, y);
        }
        d.dispose();
        return image;
    }
    
    public static String drawImage(OutputStream output,int length) throws IOException{
    	char[] chars = genRandomChars(length);
    	RenderedImage image = genCharsImage(chars);
  	 	ImageIO.write(image, GRAPHIC_JPEG, output);
	    return String.valueOf(chars);
    }
   
	
	public static BufferedImage genCharsImage2(char[] chars){
		int height=font.getSize()+6 ;
		BufferedImage image = new BufferedImage(height*(chars.length), height, BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 设定背景色
		g.setColor(genRandomColor(200,250));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		//画边框
		//g.setColor(new Color());
		//g.drawRect(0,0,width-1,height-1);
   		g.setColor(genRandomColor(160,200));
		g.setFont(font);
		for (int i = 0; i < 15; i++) {
   		// 随机产生干扰线，使图象中的认证码不易被其它程序探测到
			int x1 = random.nextInt(image.getWidth());
			int y1 = random.nextInt(image.getHeight());
			int x2 = random.nextInt(image.getWidth());
			int y2 = random.nextInt(image.getHeight());
			g.drawLine(x1,y1,x2,y2);
		}
		
		// 将认证码显示到图象中
		for (int i=0;i<chars.length;i++){
			// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.setColor(genRandomColor(20,130));
            g.drawString(String.valueOf(chars[i]), i*font.getSize(), random.nextInt(image.getHeight()-font.getSize()) + font.getSize());
		}

		// 图象生效
		g.dispose();
		return image;
	}
	
    public static BufferedImage genCharsImage3(char[] chars){
	   int height=font.getSize()+6 ;
       BufferedImage image = new BufferedImage(height*(chars.length), height, BufferedImage.TYPE_3BYTE_BGR);
       Graphics g = image.getGraphics();
       g.setColor(Color.WHITE);
       g.fillRect(0, 0, image.getWidth(), image.getHeight());
       g.setColor(genRandomColor(100,200));
       for (int i = 0; i < 10; i++) {
			int x1 = random.nextInt(image.getWidth());
			int y1 = random.nextInt(image.getHeight());
			int x2 = random.nextInt(image.getWidth());
			int y2 = random.nextInt(image.getHeight());
			g.drawLine(x1,y1,x2,y2);
       }
       g.setColor(Color.BLACK);
       g.setFont(font);
       for (int i = 0; i < chars.length; i++) {
           g.setColor(genRandomColor(50,150));
           g.drawString(String.valueOf(chars[i]), i*height, random.nextInt(image.getHeight()-font.getSize()) + font.getSize());
       }
       g.dispose();
       return image;
    }
   
	public static void main(String[] args){
    	char[] chars = genRandomChars(4);
 		System.out.println(String.valueOf(chars));		
	}
	

}
