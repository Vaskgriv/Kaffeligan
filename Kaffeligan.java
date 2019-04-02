import java.io.*;
import java.util.*;
import java.awt.Image;
import java.awt.image.*;
import javax.imageio.*;
public class Kaffeligan{
    final static int winners=3;
    static String backgroundImagePath="resources/background.png";
    static String logoImagePath="resources/logo.png";
    static String bronzeImagePath="resources/bronze.png";
    static String silverImagePath="resources/silver.png";
    static String goldImagePath="resources/gold.png";
    static String lp="LP1";
    public static void create(String path,CustomerData cd)throws Exception{
        if(path.matches(".*\\.png$")){
            writePNG(path,cd);
        }
        else if(path.matches(".*\\.jpg$")){
            writeJPG(path,cd);
        }
        else{
            throw new Exception("Filetype not supported");
        }
    }
    public static void writePNG(String path,CustomerData cd)throws java.io.IOException{// sends the array of sorted customers onward, gets a BufferedImage and writes it to a png file
        ImageIO.write(createBufferedImage(cd.customers),"png",new File(path));
    }
    public static void writeJPG(String path,CustomerData cd)throws java.io.IOException{// sends the array of sorted customers onward, gets a BufferedImage and writes it to a jpg file
        BufferedImage argb=createBufferedImage(cd.customers);// OpenJDK doesn't play nice with jpg, can't handle the alpha channel
        BufferedImage bgr=new BufferedImage(argb.getWidth(),argb.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        bgr.getGraphics().drawImage(argb,0,0,null);
        ImageIO.write(bgr,"jpeg",new File(path));
    }
    private static BufferedImage createBufferedImage(CustomerData.Customer[] ca)throws java.io.IOException{
        int width=1920,height=1080;// resolution
        int logoLeftMargin=100,logoRightPadding=40;// graphical design parameters
        int medalLeftMargin=190,medalRightPadding=40,medalTopPadding=20;
        int topMargin=40,amountWidth=500;
        int logoSize=300,medalWidth=120,medalHeight=200,headerSize,textSize;
        int headerFontSize=160,fontSize=90,headerDownShift=45,textDownShift=35;
        
        ca=decideWinners(ca);// decide which three are the winners and their rankings
        
        BufferedImage background=ImageIO.read(GUI.load(backgroundImagePath));// read in the graphical assets
        BufferedImage logo=ImageIO.read(GUI.load(logoImagePath));
        BufferedImage bronze=ImageIO.read(GUI.load(bronzeImagePath));
        BufferedImage silver=ImageIO.read(GUI.load(silverImagePath));
        BufferedImage gold=ImageIO.read(GUI.load(goldImagePath));
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        
        java.awt.Graphics g=image.getGraphics();// start composing the picture, background, logo, header
        g.drawImage(background.getScaledInstance(width,height,Image.SCALE_SMOOTH),0,0,null);
        g.drawImage(logo.getScaledInstance(logoSize,logoSize,Image.SCALE_SMOOTH),logoLeftMargin,topMargin,null);
        shadowText(g,"Kaffeligan "+lp,logoLeftMargin+logoSize+logoRightPadding,topMargin+headerDownShift,headerFontSize);
        
        int amountOffset=width-amountWidth;// vertical baseline for paid amount
        int x=medalLeftMargin+medalWidth+medalRightPadding;// vertical baseline for names
        int y=topMargin+logoSize+medalTopPadding;// horizontal baseline for gold medal
        if(ca.length>0){// add medal, name and paid amount for first place
            g.drawImage(gold.getScaledInstance(medalWidth,medalHeight,Image.SCALE_SMOOTH),medalLeftMargin,y,null);
            outlinedText(g,ca[0].name,x,y+textDownShift,fontSize);
            outlinedText(g,CSEKtoString(ca[0].paid),amountOffset,y+textDownShift,fontSize);
        }
        y+=medalHeight+medalTopPadding;// move horizontal baseline down to silver medal and repeat above steps for runner-up
        if(ca.length>1){// second place
            g.drawImage(silver.getScaledInstance(medalWidth,medalHeight,Image.SCALE_SMOOTH),medalLeftMargin,y,null);
            outlinedText(g,ca[1].name,x,y+textDownShift,fontSize);
            outlinedText(g,CSEKtoString(ca[1].paid),amountOffset,y+textDownShift,fontSize);
        }
        y+=medalHeight+medalTopPadding;// finally do the bronze medalist
        if(ca.length>2){// third place
            g.drawImage(bronze.getScaledInstance(medalWidth,medalHeight,Image.SCALE_SMOOTH),medalLeftMargin,y,null);
            outlinedText(g,ca[2].name,x,y+textDownShift,fontSize);
            outlinedText(g,CSEKtoString(ca[2].paid),amountOffset,y+textDownShift,fontSize);
        }
        return image;
    }
    public static String CSEKtoString(int csek){
        String kr=""+csek/100;
        if((csek%=100)<10){
            kr+=",0"+csek;
        }
        else{
            kr+=","+csek;
        }
        return kr+":-";
    }
    public static void shadowText(java.awt.Graphics g,String text,int x,int y,int fontSize){// writes white text with shadow
        java.awt.Font font=new java.awt.Font("Impact",java.awt.Font.BOLD,fontSize);
        g.setFont(font);
        y+=g.getFontMetrics().getAscent();
        g.setColor(new java.awt.Color(20,20,20));
        g.drawString(text,x+fontSize/10,y+fontSize/10);// write black text diagonlly down to the right
        g.setColor(new java.awt.Color(250,250,250));
        g.drawString(text,x,y);// write white text on top
    }
    public static void outlinedText(java.awt.Graphics g,String text,int x,int y,int fontSize){// writes white text with black outline
        java.awt.Font font=new java.awt.Font("Impact",java.awt.Font.BOLD,fontSize);
        y+=g.getFontMetrics(font).getAscent();
        g.setFont(new java.awt.Font("Impact",java.awt.Font.BOLD,fontSize));
        g.setColor(new java.awt.Color(50,50,50));
        g.drawString(text,x+1,y);// write black text shifted in each direction
        g.drawString(text,x-1,y);
        g.drawString(text,x,y+1);
        g.drawString(text,x,y-1);
        g.drawString(text,x+1,y+1);
        g.drawString(text,x+1,y-1);
        g.drawString(text,x-1,y+1);
        g.drawString(text,x-1,y-1);
        g.setColor(new java.awt.Color(250,250,250));
        g.drawString(text,x,y);// put the white text on top
    }
    private static CustomerData.Customer[] decideWinners(CustomerData.Customer[] ca){
        CustomerData.Customer[] result=new CustomerData.Customer[winners<ca.length?winners:ca.length];
        int decided=0;
        int i=0;
        while(decided<winners && decided<ca.length){
            int mem=i;
            try{
                while(ca[i].paid==ca[i+1].paid){// mem is the index of the first in the rank, i is the last
                    i++;
                }
            }
            catch(IndexOutOfBoundsException x){}// can happen for short arrays, or arrays with low spread
            CustomerData.Customer[] temp=new CustomerData.Customer[i-mem+1];// array for all customers of same rank
            for(int j=mem;j<=i;j++){
                temp[j-mem]=ca[j];
            }
            shuffle(temp);// randomize their order
            try{
                for(int j=decided;j<temp.length+decided;j++){// enter into winners
                    result[j]=temp[j-decided];
                }
            }
            catch(IndexOutOfBoundsException x){// winners is full, the work is done
                break;
            }
            decided+=temp.length;// everyone in the rank is a winner go to next
            i++;// increment i to next rank
        }
        return result;
    }
    private static void shuffle(Object[] array){// randomizes order of array
        int index;
        Object temp;
        Random random=new Random();
        for (int i=array.length-1;i>0;i--){
            index=random.nextInt(i+1);
            temp=array[index];
            array[index]=array[i];
            array[i]=temp;
        }
    }
}
