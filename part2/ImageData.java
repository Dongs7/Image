/////////////////////////
// CMPT 365 Proj2 - Part1
// ImageData.java
// Dong-Hyun Chung
// # 301008631
// 2015-04-13


package pro4;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;



class IMAGE extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	byte [] byteOrder = new byte[2]; // check whether little or Big endian
	byte [] TIFF_Identification = new byte[2];
	byte [] IFD_1st = new byte[4]; // offset to the first IFD entry
	byte [] ifd_1stData = null;	
	byte [] numDir = new byte [2]; // store umber of directories in IFD 0
	byte [] rgb_Byte = new byte[6]; //Store rgb bytes -- 8 8 8
	byte [] stripbyteCount = new byte[12];
	byte [] rgb_Offset = null;
	byte [] single_StripData = null; // When strip count is 1, data will be stored in the array
	
	int [] r_Data = null; //extract R data and store 
	int [] g_Data = null; //extract G data and store 
	int [] b_Data = null; //extract B data and store 
	int dirLength=0, max_Level = 0;;
	int btnCounter=1;
	final int btnMax = 4, btnMin = 1;
	
	
	String [] byte_Order = {"II - Little Endian", "MM - Big Endian"};
	String fName;
	String [] ifd_1=new String [12];
	String Order="";
	String [] p_Metric = {"WhiteIsZero","BlackIsZero","RGB","Palette","TransparencyMask"};
	String photo_Type;
	long ifd_Location=0, data_Location = 0, num_Dir=0, img_Width= 0, img_Height=0, bps = 0, stripBC_Location = 0, r_Count = 0, g_Count = 0,b_Count = 0, strip_Location = 0;
	long r_Data_Location = 0, g_Data_Location = 0, b_Data_Location = 0;
	long offset_location;
	
	
	
	short [] rgb = new short[3];
	short photoMetric = 0, r_byte = 0,g_byte = 0,b_byte = 0,strip_Count = 0,spp=0;
	
	
	// set big-Endian as default and color image as default
	boolean isLittle=false;
	boolean original = true, gray = false, histo = false, dither = false;
	
	
	IMAGE(){
	}
	
	IMAGE(long w,long h, int[]a, int []b, int []c){
		
		this.r_Data = a;
		this.g_Data = b;
		this.b_Data = c;
		this.img_Height = h;
		this.img_Width = w;
		
	}
	public void getData(String a) throws FileNotFoundException{
		
		fName = a;
		
		File aa = new File(a);
		FileInputStream fis = null;
		RandomAccessFile raf = new RandomAccessFile(a,"r");
		RandomAccessFile raf2 = new RandomAccessFile(a,"r");
		
		
		
		try{
			fis = new FileInputStream(aa);
			
			fis.read(byteOrder,0,2);
			fis.read(TIFF_Identification,0,2);
			fis.read(IFD_1st,0,4);
						
			fis.close();
			}catch(Exception e){
			e.printStackTrace();
			}
			
			
			// Convert the first two bytes to Hex values
			Order = Integer.toHexString((int)byteOrder[0]) + Integer.toHexString((int)byteOrder[1]);
			
			//If Hex value is 4949 then print II and set isLittle to true.
			//Otherwise, just print MM.
			if (Order.equals("4949") ){
				//System.out.println(byte_Order[0]);
				isLittle = true;}
			else if (Order.equals("4d4d") )
				isLittle = false;
				//System.out.println(byte_Order[1]);
			
			//find the first IFD byte offset.
						
			ifd_Location=getByteoffset(IFD_1st);
			//System.out.println(ifd_Location);
			
			
			
			// Data is not stored sequentially.
			//We need to find the data using the offset information.
			//So RandomAccessFile is used
			try{
				raf.seek(ifd_Location);
				raf.read(numDir,0,2);
				
				num_Dir = getnumDir(numDir);
				dirLength = (int)num_Dir*12; // Each entry has 12 bytes. so we can calculate how many space we need to store the information.
				ifd_1stData = new byte[dirLength];
				data_Location = ifd_Location + 2; // +2 means that frist 2 bytes has information about the number of directories. 
				
				raf.seek(data_Location);
				raf.read(ifd_1stData,0,dirLength);
				raf.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			img_Width = get_ImgWidth(ifd_1stData);
			img_Height = get_ImgHeight(ifd_1stData);
			photoMetric = get_Photometric(ifd_1stData);
			bps = get_bpsByteOff(ifd_1stData); 
			strip_Count = get_numStrip(ifd_1stData);
			strip_Location = get_SByteOff(ifd_1stData);
			stripBC_Location = get_SBCByteOff(ifd_1stData);
			spp = get_SamplePP(ifd_1stData);
			photo_Type = p_Metric[photoMetric];
			
			try{
				raf2.seek(bps);
				raf2.read(rgb_Byte,0,6);
				
				raf2.seek(stripBC_Location);
				raf2.read(stripbyteCount,0,12);
				
				
				// When the data has one strip,
				if (strip_Count == 1){
					raf2.seek(strip_Location);
					single_StripData = new byte[(int)img_Width*(int)img_Height*(int)spp]; 
					raf2.read(single_StripData);
					r_Data = get_Rdata(single_StripData,spp);
					g_Data = get_Gdata(single_StripData,spp);
					b_Data = get_Bdata(single_StripData,spp);
					
				}
				
				else{
					raf2.seek(strip_Location);
					rgb_Offset = new byte[(int)strip_Count*4]; // 12 [0000 0008 0000 0008 0000 0008]
					raf2.read(rgb_Offset);
				}
				rgb = get_RGBbyte(rgb_Byte);		
				r_byte = rgb[0];
				g_byte = rgb[1];
				b_byte = rgb[2];
				
				r_Count = get_R_StripCount_R_Location(stripbyteCount);
				g_Count = get_G_StripCount_G_Location(stripbyteCount);
				b_Count = get_B_StripCount_B_Location(stripbyteCount);
				
				
				raf2.close();
			}catch(Exception e){
				e.printStackTrace();
				e.getMessage();
				}
			
					
			
			
			
			
///////////////////////test purpose////////////////////
		/*System.out.println("Image Width  :" + img_Width);
		System.out.println("Image Height :" + img_Height);
		System.out.println(p_Metric[photoMetric]);
		System.out.println(bps);
		System.out.println(stripBC_Location);
		System.out.println(strip_Location);
		System.out.println(spp);
		System.out.println(r_Count);
		System.out.println(g_Count);
		System.out.println(b_Count);
		System.out.println(strip_Count);
		System.out.println(r_Data_Location);
		System.out.println(g_Data_Location);
		System.out.println(b_Data_Location);*/
		///////////////////////////////////////////////////////
			
}
	
	
	// get Red data
	
	public int [] get_Rdata(byte [] a, short s){
		int [] temp  = new int[(a.length)/s];
		int j =0;
		for(int i = 2; i < a.length ; i +=3){
			//We need to get unsigned value, so need to do this procedure in order to get the right value. WIthout this, we can get negative value which will
			//give you an error
			temp[j] = a[i]& 0xFF; 
			j++;
		}
		
		return temp;
	}
	
	public int [] get_Gdata(byte [] a, short s){
		int [] temp  = new int[(a.length)/s];
		int j =0;
		for(int i = 1; i < a.length ; i +=3){
			temp[j] = a[i]& 0xFF;
			j++;
		}
		
		
		return temp;
	}
	
	public int [] get_Bdata(byte [] a, short s){
		int [] temp  = new int[(a.length)/s];
		int j =0;
		for(int i = 0; i < a.length ; i +=3){
			temp[j] = a[i]& 0xFF;
			j++;
		}
		
		return temp;
	}
	
	
	//function to get sample per pixel
	public short get_SamplePP(byte [] a){
		short tag = 0;
		short result = 0;
		long temp = 0;
		
		if (isLittle == true)
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short)((a[i] & 0xff) | (a[i+1] << 8));
				if (tag == 277)
				{
					temp = ((a[i+10] & 0xff) + (a[i+11] << 8));
					
					if (temp == 0) // if last 2 bytes are 0, then 
					{
						result = (short)((a[i+8] & 0xff) + (a[i+9] << 8));
						break;
					}
					else // otherwise, we need all 4 bytes to get the value or the offset
					{
						result = (short) ((a[i+8] & 0xff) | (a[i+9] << 8 & 0xff00) | (a[i+10] << 16 & 0xff0000) | (a[i+11] << 24));
						break;
					}
				}
			}
		}
		else // when big endian
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short) (((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF));
				if (tag == 277)
				{
					temp = (((a[i+10] & 0xFF) << 8) +  (a[i+11] & 0xFF));
					
					if (temp == 0)
					{
						result =  (short) (((a[i+8] & 0xFF) << 8) +  (a[i+9] & 0xFF));
						break;
					}
					else
					{
						result =  (short) (((a[i+8] & 0xFF) << 24) | ((a[i+9] & 0xFF) << 16) | ((a[i+10] & 0xFF) << 8) | (a[i+11] & 0xFF));
						break;
					}
					
				}
			}
		}
		
		return result;
	}
	
	// function to get number of strip
	public short get_numStrip(byte [] a){
		short tag = 0;
		short result =0 ;
		
		if (isLittle == true)
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short) ((a[i] & 0xff) + (a[i+1] << 8));
				if (tag == 273)
				{
					result = (short) ((a[i+4] & 0xff) | (a[i+5] << 8 & 0xff00) | (a[i+6] << 16 & 0xff0000) | (a[i+7] << 24));
					break;
				}
			}
		}
		else 
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short) (((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF));
				if (tag == 273)
				{
					result =  (short) (((a[i+4] & 0xFF) << 24) | ((a[i+5] & 0xFF) << 16) | ((a[i+6] & 0xFF) << 8) | (a[i+7] & 0xFF));
					break;
				}
			}
		}
		
		return result;
	}
	
	
	//function to get red strip count and its location.
	// Using appropriate byte array to get the right answer.
	public long get_R_StripCount_R_Location(byte [] a){
		long result = 0;
				
		if (isLittle == true)
			result = ((a[0] & 0xff) | (a[1] << 8 & 0xff00) | (a[2] << 16 & 0xff0000) | (a[3] << 24));			
		else
			result = ((a[0] & 0xFF) << 24) | ((a[1] & 0xFF) << 16) | ((a[2] & 0xFF) << 8) | (a[3] & 0xFF);
		
		return result;
	}
	
	public long get_G_StripCount_G_Location(byte [] a){
		long result = 0;
				
		if (isLittle == true)
			result = ((a[4] & 0xff) | (a[5] << 8 & 0xff00) | (a[6] << 16 & 0xff0000) | (a[7] << 24));			
		else
			result = ((a[4] & 0xFF) << 24) | ((a[5] & 0xFF) << 16) | ((a[6] & 0xFF) << 8) | (a[7] & 0xFF);
		
		return result;
	}
	
	public long get_B_StripCount_B_Location(byte [] a){
		long result = 0;
				
		if (isLittle == true)
			result = ((a[8] & 0xff) | (a[9] << 8 & 0xff00) | (a[10] << 16 & 0xff0000) | (a[11] << 24));			
		else
			result = ((a[8] & 0xFF) << 24) | ((a[9] & 0xFF) << 16) | ((a[10] & 0xFF) << 8) | (a[11] & 0xFF);
		
		return result;
	}
	
	
	//function to get RGB byte.
	//Parameter is offset data
	public short [] get_RGBbyte(byte [] a){
		short [] result = new short [3];
				
		for(int i =0; i < result.length ; i++)
		{
			for(int j = 0; j < a.length; j +=2)
			{
				if(isLittle == true)
					result[i] =  (short) ((a[j] & 0xff) + (a[j+1] << 8));	
				else
					result[i] = (short) (((a[j] & 0xff) << 8) + (a[j+1] & 0xff));	
			}
		}
		
		return result;
	}
	
	
	//function to get the byteoffset
	
	public long getByteoffset(byte [] a){
		long result=0;
				
		
		if (isLittle == true)
			result = ((a[0] & 0xff) | (a[1] << 8 & 0xff00) | (a[2] << 16 & 0xff0000) | (a[3] << 24));			
		else
			result = ((a[0] & 0xFF) << 24) | ((a[1] & 0xFF) << 16) | ((a[2] & 0xFF) << 8) | (a[3] & 0xFF);
		
		return result;
	}
	
	
	//function to get the number of directories
	public long getnumDir(byte [] a){
		long result=0;
		
		if (isLittle == true)
			result = ((a[0] & 0xff) + (a[1] << 8));			
		else
			result = (short) (((a[0] & 0xFF) << 8) +  (a[1] & 0xFF));
		
		return result;
	}
	
	
	//Function to get image width
	public long get_ImgWidth(byte [] a){
		long tag = 0;
		long result = 0;
		
		if (isLittle == true)
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = ((a[i] & 0xff) + (a[i+1] << 8));
				if (tag == 256)
				{
					result = ((a[i+8] & 0xff) + (a[i+9] << 8));
					break;
				}
			}
		}
		else 
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = ((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF);
				if (tag == 256)
				{
					result =  ((a[i+8] & 0xFF) << 8) +  (a[i+9] & 0xFF);
					break;
				}
			}
		}
		
		return result;
	}
	
	
	//Function to get image Height
	public long get_ImgHeight(byte [] a){
		long tag = 0;
		long result = 0;
		
		if (isLittle == true)
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = ((a[i] & 0xff) + (a[i+1] << 8));
				if (tag == 257)
				{
					result = ((a[i+8] & 0xff) + (a[i+9] << 8));
					break;
				}
			}
		}
		else 
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = ((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF);
				if (tag == 257)
				{
					result =  ((a[i+8] & 0xFF) << 8) +  (a[i+9] & 0xFF);
					break;
				}
			}
		}
		
		return result;
	}
	
	
	//Function to get photometric value.
	//Once this value is determined, its value will be displayed
	//in Window form later.
	public short get_Photometric(byte [] a){
	short tag = 0;
	short result = 0;
	
	if (isLittle == true)
	{
		for(int i = 0; i < a.length ; i += 12)
		{
			tag = (short) ((a[i] & 0xff) + (a[i+1] << 8));
			if (tag == 262)
			{
				result = (short) ((a[i+8] & 0xff) + (a[i+9] << 8));
				break;
			}
		}
	}
	else 
	{
		for(int i = 0; i < a.length ; i += 12)
		{
			tag = (short) (((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF));
			if (tag == 262)
			{
				result =  (short) (((a[i+8] & 0xFF) << 8) +  (a[i+9] & 0xFF));
				break;
			}
		}
	}
	
	return result;
}
	
	public long get_bpsByteOff(byte [] a){
		short tag = 0;
		long result = 0;
		//int count = 0;
		long temp = 0;
		if (isLittle == true)
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short)((a[i] & 0xff) | (a[i+1] << 8));
				if (tag == 258)
				{
					temp = ((a[i+10] & 0xff) + (a[i+11] << 8));
					
					if (temp == 0)// if last 2 bytes are 0, then
					{
						result = (short)((a[i+8] & 0xff) + (a[i+9] << 8));
						break;
					}
					else // otherwise get all 4 bytes info to get offset
					{
						result = ((a[i+8] & 0xff) | (a[i+9] << 8 & 0xff00) | (a[i+10] << 16 & 0xff0000) | (a[i+11] << 24));
						break;
					}
				}
			}
		}
		else 
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short) (((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF));
				if (tag == 258)
				{
					temp = (((a[i+10] & 0xFF) << 8) +  (a[i+11] & 0xFF));
					
					if (temp == 0)// if last 2 bytes are 0, then
					{
						result =  (short) (((a[i+8] & 0xFF) << 8) +  (a[i+9] & 0xFF));
						break;
					}
					else
					{
						result =  (((a[i+8] & 0xFF) << 24) | ((a[i+9] & 0xFF) << 16) | ((a[i+10] & 0xFF) << 8) | (a[i+11] & 0xFF));
						break;
					}
					
				}
			}
		}
		
		return result;
	}
	
	
	//function to get stripbyteCountoffset
	public long get_SBCByteOff(byte [] a){
		short tag = 0;
		long result = 0;
		//int count = 0;
		long temp = 0;
		if (isLittle == true)
		{
			
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short)((a[i] & 0xff) | (a[i+1] << 8));
				if (tag == 279)
				{
					temp = ((a[i+10] & 0xff) + (a[i+11] << 8));
					
					if (temp == 0)
					{
						result =((a[i+8] & 0xff) + (a[i+9] << 8));
						break;
					}
					else
					{
						result = ((a[i+8] & 0xff) | (a[i+9] << 8 & 0xff00) | (a[i+10] << 16 & 0xff0000) | (a[i+11] << 24));
						break;
					}
				}
			}
		}
		else 
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short) (((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF));
				if (tag == 279)
				{
					temp = (((a[i+10] & 0xFF) << 8) +  (a[i+11] & 0xFF));
					
					if (temp == 0)
					{
						result =   (((a[i+8] & 0xFF) << 8) +  (a[i+9] & 0xFF));
						break;
					}
					else
					{
						result =  (((a[i+8] & 0xFF) << 24) | ((a[i+9] & 0xFF) << 16) | ((a[i+10] & 0xFF) << 8) | (a[i+11] & 0xFF));
						break;
					}
					
				}
			}
		}
		
		return result;
	}

	//function to get stripbyteoffset
	public long get_SByteOff(byte [] a){
		short tag = 0;
		long result = 0;
		//int count = 0;
		long temp = 0;
		if (isLittle == true)
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short)((a[i] & 0xff) | (a[i+1] << 8));
				if (tag == 273)
				{
					temp = ((a[i+10] & 0xff) + (a[i+11] << 8));
					
					if (temp == 0)
					{
						result = (short)((a[i+8] & 0xff) + (a[i+9] << 8));
						break;
					}
					else
					{
						result = ((a[i+8] & 0xff) | (a[i+9] << 8 & 0xff00) | (a[i+10] << 16 & 0xff0000) | (a[i+11] << 24));
						break;
					}
				}
			}
		}
		else 
		{
			for(int i = 0; i < a.length ; i += 12)
			{
				tag = (short) (((a[i] & 0xFF) << 8) +  (a[i+1] & 0xFF));
				if (tag == 273)
				{
					temp = (((a[i+10] & 0xFF) << 8) +  (a[i+11] & 0xFF));
					
					if (temp == 0)
					{
						result =  (short) (((a[i+8] & 0xFF) << 8) +  (a[i+9] & 0xFF));
						break;
					}
					else
					{
						result =  (((a[i+8] & 0xFF) << 24) | ((a[i+9] & 0xFF) << 16) | ((a[i+10] & 0xFF) << 8) | (a[i+11] & 0xFF));
						break;
					}
					
				}
			}
		}
		
		return result;
	}
	
	
	//function to display color image
	public void showOriginal(Graphics g, long w, long h, int[] R, int[] G, int[] B ){
		Color c;
		
		int x_Start = 0;
		int y_Start = 0;
		int counter = 0;
		out_loop:
		for(int j = 0; j < h; j++)
		{
			for(int i = 0 ; i < w ; i++)
			{
				try{
				c = new Color(B[counter],G[counter],R[counter]);
				g.setColor(c);
				g.fillRect(x_Start+i, y_Start+j, 1, 1);
				counter++;}
				catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Can't process file");
					break out_loop;}
			}
			
			
		}
		
		
	}

	public void paintComponent(Graphics g)
		{
		
			super.paintComponent(g);
			
			//Depends on the boolean value, different functions will be executed.
			
				showOriginal(g,img_Width,img_Height,r_Data,g_Data,b_Data);
				repaint();
				
		
	}
	
	
	//This is Run-Length coding I tried. 
	//
	public ArrayList<Integer> comp(int [] a, int [] b, int [] c, long w, long h){
		
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		int counter = 0;
		int value = 0;
		
		int [] combinedRGB = new int [a.length + b.length + c.length];
		
		for(int i =0; i < a.length; i++)
			combinedRGB[i] = a[i];
		
		for(int i =0; i < b.length; i++)
			combinedRGB[a.length+i] = b[i];
		
		for(int i =0; i < c.length; i++)
			combinedRGB[a.length + b.length + i] = c[i];
		//byte k =
		//long ww = (long)k;
		// put image width and height at the first two locations in the ArrayList for the future reference
		
		
		al.add(combinedRGB[value]);
		
		for(int i =0 ; i< combinedRGB.length ; i ++ ){
				
			if (combinedRGB[value] == combinedRGB[i])
				counter++;
			else if(combinedRGB[value] != combinedRGB[i]){
//				System.out.println(img.r_Data[value] + " and " + i + " counter : " + counter);
				if(counter == 1){
					value = value + counter;
					al.add(combinedRGB[value]);
					counter = 0;
					counter++;
				}
				else{
				al.add(-counter);
				value = value + counter;
				al.add(combinedRGB[value]);
				counter = 0;
				counter++;}
				}

		}
		al.add(-counter);
		al.add((int) w);
		al.add((int) h);
		
		
		
		return al;
	}

}

public class ImageData {
	public void init(String a, String b, String c) throws FileNotFoundException{
		final IMAGE img = new IMAGE();
		final IMAGE img2 = new IMAGE();
		final IMAGE img3 = new IMAGE();
		
		String f1Name = a;
		String f2Name = b;
		String f3Name = c;
		
		final JFrame frame = new JFrame("IMAGE READ");
		
		img.getData(f1Name);
		img2.getData(f2Name);
		img3.getData(f3Name);
		
		int size = (int)(img.single_StripData.length/img.spp);
		long ww = img.img_Width;
		long hh = img.img_Height;
		//System.out.println(ww + " and " + hh);
		int [] added_R = new int [size];
		int [] added_G = new int [size];
		int [] added_B = new int [size];
		
		//To get HDR image, Add each R,G,B channel, and get the average value.
		//Using these average values, I get HDR image.
		for(int i =0; i < size ; i++){
			added_R[i] = (img.r_Data[i] + img2.r_Data[i] + img3.r_Data[i])/3;
			added_G[i] = (img.g_Data[i] + img2.g_Data[i] + img3.g_Data[i])/3;
			added_B[i] = (img.b_Data[i] + img2.b_Data[i] + img3.b_Data[i])/3;
			
		}
		
		
		IMAGE img4 = new IMAGE(ww,hh, added_R,added_G,added_B);
		
		JLabel fileName1 = new JLabel("1st File Name :" + a);
		JLabel fileName2 = new JLabel("2st File Name :" + b);
		JLabel fileName3 = new JLabel("3st File Name :" + c);
		JLabel fileName4 = new JLabel("HDR_IMAGE");
		
		
		
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		JPanel p4 = new JPanel();
		JPanel p5 = new JPanel();
		JPanel p6 = new JPanel();
		JPanel p7 = new JPanel();
		JPanel p8 = new JPanel();
		
		
		
		
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(2*(int)img.img_Width, 2*(int)img.img_Height+72);
		
		frame.setLayout(new GridBagLayout());
		frame.getContentPane().setBackground(null);
		
		GridBagConstraints cc = new GridBagConstraints();
		
		cc.weightx =1;
		cc.weighty=1;
		cc.gridx = 0;
		cc.gridy = 0;
		cc.fill = GridBagConstraints.BOTH;
		frame.getContentPane().add(p1.add(img),cc);
		
		cc.weightx =1;
		cc.weighty=1;
		cc.gridx = 1;
		cc.gridy = 0;
		cc.fill = GridBagConstraints.BOTH;
		frame.getContentPane().add(p2.add(img2),cc);
		
		cc.weightx =0;
		cc.weighty=0;
		cc.gridx = 0;
		cc.gridy = 1;
		cc.fill = GridBagConstraints.CENTER;
		frame.getContentPane().add(p3.add(fileName1),cc);
		
		cc.weightx =0;
		cc.weighty=0;
		cc.gridx = 1;
		cc.gridy = 1;
		cc.fill = GridBagConstraints.CENTER;
		frame.getContentPane().add(p4.add(fileName2),cc);
		
		cc.weightx =1;
		cc.weighty=1;
		cc.gridx = 0;
		cc.gridy = 2;
		cc.fill = GridBagConstraints.BOTH;
		frame.getContentPane().add(p5.add(img3),cc);
		
		cc.weightx =1;
		cc.weighty=1;
		cc.gridx = 1;
		cc.gridy = 2;
		cc.fill = GridBagConstraints.BOTH;
		frame.getContentPane().add(p6.add(img4),cc);
		
		cc.weightx =0;
		cc.weighty=0;
		cc.gridx = 0;
		cc.gridy = 3;
		cc.fill = GridBagConstraints.CENTER;
		frame.getContentPane().add(p7.add(fileName3),cc);
		
		cc.weightx =0;
		cc.weighty=0;
		cc.gridx = 1;
		cc.gridy = 3;
		cc.fill = GridBagConstraints.CENTER;
		frame.getContentPane().add(p8.add(fileName4),cc);
		
		
		
		frame.setVisible(true);
	}
	
	public void compInit(String a, String b, String c) throws IOException{
		ArrayList<Integer> ar1 = new ArrayList<Integer>();
		ArrayList<Integer> ar2 = new ArrayList<Integer>();
		ArrayList<Integer> ar3 = new ArrayList<Integer>();
		DataOutputStream output = null;
		
		IMAGE img = new IMAGE();
		IMAGE img2 = new IMAGE();
		IMAGE img3 = new IMAGE();
				
		img.getData(a);
		img2.getData(b);
		img3.getData(c);
		
		// get RGB data from three image files
		ar1 = img.comp(img.r_Data,img.g_Data,img.b_Data,img.img_Width, img.img_Height);
		ar2 = img.comp(img2.r_Data,img2.g_Data,img2.b_Data,img2.img_Width, img2.img_Height);
		ar3 = img.comp(img3.r_Data,img3.g_Data,img3.b_Data,img3.img_Width, img3.img_Height);
		
		int [] file1= new int[ar1.size()];
		int [] file2= new int[ar2.size()];
		int [] file3= new int[ar3.size()];
		
		
		//Put RGB data from ArrayList to int Array
		for(int i =0; i < ar1.size() ; i++)
			file1[i] = ar1.get(i);
			
		for(int i =0; i < ar2.size() ; i++)	
			file2[i] = ar2.get(i);
			
		
		for(int i =0; i < ar3.size() ; i++)
			file3[i] = ar3.get(i);
		
		
		// Write .thr file
		try{
		output = new DataOutputStream(new FileOutputStream("compressed.thr"));
			
		   for (int i = 0; i < file1.length; i++)
			   output.writeInt(file1[i]);
		      
		   for (int i = 0; i < file2.length; i++)
			   output.writeInt(file2[i]);
		      
		   for (int i = 0; i < file3.length; i++)
			   output.writeInt(file3[i]);
		      
		      
		      output.close();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		    
		
		
		
	}
		
		
}