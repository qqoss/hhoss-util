
package biz.zheng.util.images;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author kejun
 *java用内部类实现对图片的处理，缩放，添加水印，裁切
 */
public class ImageUtil {

   /**
     * 几种常见的图片格式
    */
   public static String IMAGE_TYPE_GIF = "gif";// 图形交换格式
   public static String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
   public static String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
   public static String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
   public static String IMAGE_TYPE_PNG = "png";// 可移植网络图形
   public static String IMAGE_TYPE_PSD = "psd";// Photoshop的专用格式Photoshop

 /**
     * 程序入口：用于测试
    * @param args
     */
    public static void main(String[] args) {
    	
    	Font[] arr =  GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    	
        // 1-缩放图像：
       // 方法一：按比例缩放
       ImageUtil.scale("w:/test_src.jpg", "w:/test_scale.jpg", 2, true);//测试OK
        // 方法二：按高度和宽度缩放
       ImageUtil.scale2("w:/test_src.jpg", "w:/test_scale2.jpg", 500, 300, true);//测试OK

       // 2-切割图像：
       // 方法一：按指定起点坐标和宽高切割
       ImageUtil.cut("w:/test_src.jpg", "w:/test_cut.jpg", 0, 0, 400, 400 );//测试OK
        // 方法二：指定切片的行数和列数
       ImageUtil.cut2("w:/test_src.jpg", "w:/test_cut2", 2, 2 );//测试OK
        // 方法三：指定切片的宽度和高度
       ImageUtil.cut3("w:/test_src.jpg", "w:/test_cut3", 300, 300 );//测试OK

       // 3-图像类型转换：
       ImageUtil.convert("w:/test_src.jpg", "GIF", "w:/test_convert.gif");//测试OK

       // 4-彩色转黑白：
       ImageUtil.gray("w:/test_src.jpg", "w:/test_gray.jpg");//测试OK

       // 5-给图片添加文字水印：
       // 方法一：
       ImageUtil.pressText("我是水印文字","w:/test_src.jpg","w:/test_pressText.jpg","宋体",Font.BOLD,Color.white,80, 0, 0, 0.5f,0);//测试OK
        // 方法二：
       ImageUtil.pressText("我也是水印文字", "w:/test_src.jpg","w:/test_pressText2.jpg", "黑体", 36, Color.white, 80, 0, 0, 0.5f,30);//测试OK
        
        // 6-给图片添加图片水印：
       ImageUtil.pressImage("w:/test_src_icon.jpg", "w:/test_src.jpg","w:/test_pressImage.jpg", 0, 0, 0.5f,45);//测试OK
       
       
       createText("郑克军1郑克军郑克军郑克军4", "w:\\test_create.jpg", "黑体", 36, Color.DARK_GRAY, 40, 60, 60, 0.3f,45);   
        
       resize( "w:/test_src.jpg","w:/test_resize.jpg", 500, 500, true);   

    }

   /**
     * 缩放图像（按比例缩放）
    * @param srcImageFile 源图像文件地址
    * @param result 缩放后的图像地址
    * @param scale 缩放比例
    * @param flag 缩放选择:true 放大; false 缩小;
     */
    public final static void scale(String srcImageFile, String result,
            int scale, boolean flag) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件
           int width = src.getWidth(); // 得到源图宽
           int height = src.getHeight(); // 得到源图长
           if (flag) {// 放大
               width = width * scale;
                height = height * scale;
            } else {// 缩小
               width = width / scale;
                height = height / scale;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);//使用默认的图像缩放算法
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);//表示一个图像，该图像具有打包成证书像素的8位RGB颜色分量
            Graphics g = tag.getGraphics();//Graphics2D，可以将它绘制到此图像中
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
           g.dispose();// 释放此图形的上下文并释放它所使用的所有系统资源
            ImageIO.write(tag, "JPEG", new File(result));// 输出到文件流
       } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /**
     * 缩放图像（按高度和宽度缩放）
    * @param srcImageFile 源图像文件地址
    * @param result 缩放后的图像地址
    * @param height 缩放后的高度
    * @param width 缩放后的宽度
    * @param bb 比例不对时是否需要补白：true为补白; false为不补白;
     */
    @SuppressWarnings("static-access")
 public final static void scale2(String srcImageFile, String result, int height, int width, boolean bb) {
        try {
            double ratio = 0.0; // 缩放比例
           File f = new File(srcImageFile);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);//bi.SCALE_SMOOTH  选择图像平滑度比缩放速度具有更高优先级的图像缩放算法。
            // 计算比例
           if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (bi.getHeight() > bi.getWidth()) {
                    ratio = (new Integer(height)).doubleValue()
                            / bi.getHeight();
                } else {
                    ratio = (new Integer(width)).doubleValue() / bi.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform//仿射转换
                        .getScaleInstance(ratio, ratio), null);//返回表示剪切变换的变换
                itemp = op.filter(bi, null);//转换源 BufferedImage 并将结果存储在目标 BufferedImage 中。
            }
            if (bb) {//补白
               BufferedImage image = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB);//构造一个类型为预定义图像类型之一的 BufferedImage。
                Graphics2D g = image.createGraphics();//创建一个 Graphics2D，可以将它绘制到此 BufferedImage 中。
                g.setColor(Color.white);//控制颜色
                g.fillRect(0, 0, width, height);// 使用 Graphics2D 上下文的设置，填充 Shape 的内部区域。
                if (width == itemp.getWidth(null))
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                else
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                g.dispose();
                itemp = image;
            }
            ImageIO.write((BufferedImage) itemp, "JPEG", new File(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 图像切割(按指定起点坐标和宽高切割)
     * @param srcImageFile 源图像地址
    * @param result 切片后的图像地址
    * @param x 目标切片起点坐标X
     * @param y 目标切片起点坐标Y
     * @param width 目标切片宽度
    * @param height 目标切片高度
    */
    public final static void cut(String srcImageFile, String result,
            int x, int y, int width, int height) {
        try {
            // 读取源图像
           BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight(); // 源图宽度
           int srcHeight = bi.getWidth(); // 源图高度
           if (srcWidth > 0 && srcHeight > 0) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight,
                        Image.SCALE_DEFAULT);
                // 四个参数分别为图像起点坐标和宽高
               // 即: CropImageFilter(int x,int y,int width,int height)
                ImageFilter cropFilter = new CropImageFilter(x, y, width, height);//用于裁剪图像的 ImageFilter 类。此类扩展了基本 ImageFilter 类，可提取现有 Image 中的给定矩形区域，为包含刚提取区域的新图像提供源。也就是它要与 FilteredImageSource 对象结合使用，以生成现有图像的裁剪版本。
                Image img = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(image.getSource(),
                                cropFilter));
                BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
               g.dispose();
                // 输出为文件
               ImageIO.write(tag, "JPEG", new File(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 图像切割（指定切片的行数和列数）
    * @param srcImageFile 源图像地址
    * @param desPath 切片目标文件路径头
    * @param rows 目标切片行数。默认2，必须是范围 [1, 20] 之内
    * @param cols 目标切片列数。默认2，必须是范围 [1, 20] 之内
    */
    public final static void cut2(String srcImageFile, String desPath,  int rows, int cols) {
        try {
            if(rows<=0||rows>20) rows = 2; // 切片行数
           if(cols<=0||cols>20) cols = 2; // 切片列数
           // 读取源图像
           BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight(); // 源图宽度
           int srcHeight = bi.getWidth(); // 源图高度
           if (srcWidth > 0 && srcHeight > 0) {
                Image img;
                ImageFilter cropFilter;
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                int destWidth = srcWidth; // 每张切片的宽度
               int destHeight = srcHeight; // 每张切片的高度
               // 计算切片的宽度和高度
               if (srcWidth % cols == 0) {
                    destWidth = srcWidth / cols;
                } else {
                    destWidth = (int) Math.floor(srcWidth / cols) + 1;
                }
                if (srcHeight % rows == 0) {
                    destHeight = srcHeight / rows;
                } else {
                    destHeight = (int) Math.floor(srcWidth / rows) + 1;
                }
                // 循环建立切片
               // 改进的想法:是否可用多线程加快切割速度
               for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                       // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); // 绘制缩小后的图
                       g.dispose();
                        // 输出为文件
                       ImageIO.write(tag, "JPEG", new File(desPath + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /**
     * 图像切割（指定切片的宽度和高度）
    * @param srcImageFile 源图像地址
    * @param descDir 切片目标文件夹
    * @param destWidth 目标切片宽度。默认200
     * @param destHeight 目标切片高度。默认150
     */
    public final static void cut3(String srcImageFile, String descDir,
            int destWidth, int destHeight) {
        try {
            if(destWidth<=0) destWidth = 200; // 切片宽度
           if(destHeight<=0) destHeight = 150; // 切片高度
           // 读取源图像
           BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight(); // 源图宽度
           int srcHeight = bi.getWidth(); // 源图高度
           if (srcWidth > destWidth && srcHeight > destHeight) {
                Image img;
                ImageFilter cropFilter;
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                int cols = 0; // 切片横向数量
               int rows = 0; // 切片纵向数量
               // 计算切片的横向和纵向数量
               if (srcWidth % destWidth == 0) {
                    cols = srcWidth / destWidth;
                } else {
                    cols = (int) Math.floor(srcWidth / destWidth) + 1;
                }
                if (srcHeight % destHeight == 0) {
                    rows = srcHeight / destHeight;
                } else {
                    rows = (int) Math.floor(srcHeight / destHeight) + 1;
                }
                // 循环建立切片
               // 改进的想法:是否可用多线程加快切割速度
               for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                       // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); // 绘制缩小后的图
                       g.dispose();
                        // 输出为文件
                       ImageIO.write(tag, "JPEG", new File(descDir
                                + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /**
     * 图像类型转换：GIF->JPG、GIF->PNG、PNG->JPG、PNG->GIF(X)、BMP->PNG
     * @param srcImageFile 源图像地址
    * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
    * @param destImageFile 目标图像地址
    */
    public final static void convert(String srcImageFile, String formatName, String destImageFile) {
        try {
            File f = new File(srcImageFile);
            f.canRead();
            f.canWrite();
            BufferedImage src = ImageIO.read(f);
            ImageIO.write(src, formatName, new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /**
     * 彩色转为黑白 
    * @param srcImageFile 源图像地址
    * @param destImageFile 目标图像地址
    */
    public final static void gray(String srcImageFile, String destImageFile) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
            ImageIO.write(src, "JPEG", new File(destImageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /**
     * 给图片添加文字水印
    * @param pressText 水印文字
    * @param srcImageFile 源图像地址
    * @param destImageFile 目标图像地址
    * @param fontName 水印的字体名称
    * @param fontStyle 水印的字体样式
    * @param color 水印的字体颜色
    * @param fontSize 水印的字体大小
    * @param x 修正值
    * @param y 修正值
    * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
    * @param degree 角度
     */  
    public static void pressText(String pressText, String srcImage,String targetImg, String fontName, int fontStyle, Color color, int fontSize, int x, int y, float alpha,double degree) {   
        try {   
            File img = new File(srcImage);   
            Image src = ImageIO.read(img);   
            int width = src.getWidth(null);   
            int height = src.getHeight(null);   
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   
            Graphics2D g = image.createGraphics();   
            g.drawImage(src, 0, 0, width, height, null);   
            g.rotate(Math.toRadians(degree), image.getWidth()/2,image.getHeight()/2); 
            g.setColor(color);   
            g.setFont(new Font(fontName, fontStyle, fontSize));   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));   
            g.drawString(pressText, (width - (getLength(pressText) * fontSize)) / 2 + x, (height - fontSize) / 2 + y);   
            g.dispose();   
            ImageIO.write((BufferedImage) image, "jpg", new File(targetImg));   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   


   /**
     * 给图片添加图片水印
    * @param pressImg 水印图片
    * @param srcImageFile 源图像地址
    * @param destImageFile 目标图像地址
    * @param x 修正值。 默认在中间
    * @param y 修正值。 默认在中间
    * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
    * @param degree 角度
     */  
    public final static void pressImage(String pressImg, String srcImage, String targetImg, int x, int y, float alpha,double degree) {   
        try {   
            File img = new File(srcImage);   
            Image src = ImageIO.read(img);   
            int wideth = src.getWidth(null);   
            int height = src.getHeight(null);   
            BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);   
            Graphics2D g = image.createGraphics();   
            g.drawImage(src, 0, 0, wideth, height, null);   
            g.rotate(Math.toRadians(degree), image.getWidth()/2,image.getHeight()/2); 
            //水印文件   
            Image src_biao = ImageIO.read(new File(pressImg));   
            int wideth_biao = src_biao.getWidth(null);   
            int height_biao = src_biao.getHeight(null);   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));   
            g.drawImage(src_biao, (wideth - wideth_biao) / 2, (height - height_biao) / 2, wideth_biao, height_biao, null);   
            //水印文件结束   
            g.dispose();   
            ImageIO.write((BufferedImage) image, "JPEG", new File(targetImg));   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   
    
    /**  
     * 文字水印  
     * @param pressText 水印文字  
     * @param targetImg 目标图片  
     * @param fontName 字体名称  
     * @param fontStyle 字体样式  
     * @param color 字体颜色  
     * @param fontSize 字体大小  
     * @param x 修正值  
     * @param y 修正值  
     * @param alpha 透明度  
     * @param degree 角度
     */  
    public static void createText(String pressText, String targetImg, String fontName, int fontStyle, Color color, int fontSize, int x, int y, float alpha,double degree) {   
        try {   
            File img = new File(targetImg);   
            double sin = Math.abs(Math.sin(Math.toRadians(degree)));
            double cos = Math.abs(Math.cos(Math.toRadians(degree)));
            
            int tsize = getLength(pressText)*fontSize;
            int width =  (int)(cos*tsize)+2*fontSize+2*x;  
            int height = (int)(sin*tsize)+2*fontSize+2*y; 
            width = height = Math.max(width,height);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   
            Graphics2D g = image.createGraphics();   
            Color bgColor = new Color(223, 232, 246);//extjs color;
            GradientPaint background = new GradientPaint(0f, 0f,bgColor, width, height, bgColor);  
            g.setPaint(background);  
            // g.setBackground(bgColor);
            g.fillRect(0,0,width,height);

            g.rotate(Math.toRadians(degree),(width)/2,height/2); 
            g.setColor(color);   
        	//Font font = g.getFont();
            g.setFont(new Font(fontName, fontStyle, fontSize));   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));   
            g.drawString(pressText,0*(fontSize+x),(height+fontSize)/2);   
           // g.drawString(pressText,(int)Math.signum(cos)*(fontSize+x),(int)Math.signum(sin)*(fontSize+y));   45 ok
            //g.drawString(pressText,fontSize/2+x,height-fontSize/2-y);   -45 ok 
            g.dispose();   
            ImageIO.write((BufferedImage) image, "jpg", img);   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   
  
    
    /**  
     * 缩放  
     * @param filePath 图片路径  
     * @param height 高度  
     * @param width 宽度  
     * @param bb 比例不对时是否需要补白  
     */  
    public static void resize(String srcImage, String targetImg, int height, int width, boolean bb) {   
        try {   
            double ratio = 0.0; //缩放比例    
            BufferedImage bi = ImageIO.read(new File(srcImage));   
            Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);   
            //计算比例   
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {   
                if (bi.getHeight() > bi.getWidth()) {   
                    ratio = (new Integer(height)).doubleValue() / bi.getHeight();   
                } else {   
                    ratio = (new Integer(width)).doubleValue() / bi.getWidth();   
                }   
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);   
                itemp = op.filter(bi, null);   
            }   
            if (bb) {   
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   
                Graphics2D g = image.createGraphics();   
                g.setColor(Color.white);   
                g.fillRect(0, 0, width, height);   
                if (width == itemp.getWidth(null))   
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);   
                else  
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);   
                g.dispose();   
                itemp = image;   
            }   
            ImageIO.write((BufferedImage) itemp, "jpg", new File(targetImg));   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
   
    /**
     * 计算text的长度（一个中文算两个字符）
    * @param text
     * @return 
     */
    public final static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (new String(text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }
}

 