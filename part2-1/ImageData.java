/////////////////////////
// CMPT 365 Proj2 - Part2-2
// ImageData.java
// Dong-Hyun Chung
// # 301008631
// 2015-04-13


package pro5;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import pro5.IMAGE;



class IMAGE extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	int [] fileData = null; // check whether little or Big endian
	int [] decompData = null;
	int [] Rdata_1;
	int [] Gdata_1;
	int [] Bdata_1;
	
	int [] Rdata_2;
	int [] Gdata_2;
	int [] Bdata_2;
	
	int [] Rdata_3;
	int [] Gdata_3;
	int [] Bdata_3;
	
	int img_Width;
	int img_Height;
	int size;
	String fName;
	
	
	IMAGE(){
	}
	
	public void getData(String a) throws IOException{
		ArrayList<Integer> list = new ArrayList<Integer>();
		fName = a;
		
		File aa = new File(a);
		DataInputStream input = new DataInputStream(new FileInputStream(aa));
				
		
		try{
			while(input.available() != 0){
				int x = input.readInt();
				list.add(x);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally {
		    try {
		        input.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		/*for(int i=0 ; i < list.size(); i++){
		int ii = list.get(i);
		System.out.println(ii + " ");}*/
		
	img_Width = list.get(list.size()-2);
	img_Height = list.get(list.size()-1);
	size = img_Width * img_Height;
	
	Rdata_1 = new int[size];
	Gdata_1 = new int[size];
	Bdata_1 = new int[size];
	
	Rdata_2 = new int[size];
	Gdata_2 = new int[size];
	Bdata_2 = new int[size];
	
	Rdata_3 = new int[size];
	Gdata_3 = new int[size];
	Bdata_3 = new int[size];
	
	decompData = get_totalData(list,size,img_Width, img_Height);
	
	for(int i = 0; i < Rdata_1.length; i++)
		Rdata_1[i] = decompData[i];
	
	for(int i = 0; i < Gdata_1.length; i++)
		Gdata_1[i] = decompData[i+Rdata_1.length];
	
	for(int i = 0; i < Bdata_1.length; i++)
		Bdata_1[i] = decompData[i+Rdata_1.length+ Gdata_1.length];
	
	for(int i = 0; i < Rdata_2.length; i++)
		Rdata_2[i] = decompData[i+Rdata_1.length+ Gdata_1.length+Bdata_1.length];
	
	for(int i = 0; i < Gdata_2.length; i++)
		Gdata_2[i] = decompData[i+Rdata_1.length+ Gdata_1.length+Bdata_1.length + Rdata_2.length ];
	
	for(int i = 0; i < Bdata_2.length; i++)
		Bdata_2[i] = decompData[i+Rdata_1.length+ Gdata_1.length+Bdata_1.length + Rdata_2.length + Gdata_2.length];
	
	for(int i = 0; i < Rdata_3.length; i++)
		Rdata_3[i] = decompData[i+Rdata_1.length+ Gdata_1.length+Bdata_1.length + Rdata_2.length + Gdata_2.length + Bdata_2.length];
	
	for(int i = 0; i < Gdata_3.length; i++)
		Gdata_3[i] = decompData[i+Rdata_1.length+ Gdata_1.length+Bdata_1.length + Rdata_2.length + Gdata_2.length + Bdata_2.length + Rdata_3.length];
	
	for(int i = 0; i < Bdata_3.length; i++)
		Bdata_3[i] = decompData[i+Rdata_1.length+ Gdata_1.length+Bdata_1.length + Rdata_2.length + Gdata_2.length + Bdata_2.length + Rdata_3.length  + Gdata_3.length];
}
	
	//Decompress total Data
	//Param : ArrayList holding Compressed Data and one image size
	public int [] get_totalData(ArrayList<Integer> a, int sizeIn, int w, int h){
		int [] temp = new int [9 * sizeIn+4];
		
		int numCounter = 0;
		int i =0;
		int j = 0;
		while (i < a.size()){
			
		
		
		if(a.get(i) >=0){
			if (a.get(i) == 389)
				i++;
			else if(a.get(i) == 292)
				i++;
			else{
			temp[j] = a.get(i);
			i++;}
			j++;
		}
		
		else if(a.get(i) < 0){
			numCounter = Math.abs(a.get(i))-1;
			while(numCounter > 0){
				temp[j] = a.get(i-1);
				j++;
				numCounter--;
				
				}		
				i++;
		}
		}
		return temp;
			
	}

	
	//function to display first image
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

	//function to display second image
	public void showOriginal2(Graphics g, long w, long h, int[] R, int[] G, int[] B ){
		Color c;
		
		int x_Start = 389;
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
	
	//function to display third image
	public void showOriginal3(Graphics g, long w, long h, int[] R, int[] G, int[] B ){
		Color c;
		
		int x_Start = 778;
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
			
				showOriginal(g,img_Width,img_Height,Rdata_1,Gdata_1,Bdata_1);
				repaint();
				showOriginal2(g,img_Width,img_Height,Rdata_2,Gdata_2,Bdata_2);
				repaint();
				showOriginal3(g,img_Width,img_Height,Rdata_3,Gdata_3,Bdata_3);
				repaint();
				
	}
}

public class ImageData {
	public void init(String a) throws IOException{
		final IMAGE img = new IMAGE();
		 
		String f1Name = a;
		
		
		final JFrame frame = new JFrame("IMAGE READ");
		
		img.getData(f1Name);
		
		
		
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(3*(int)img.img_Width+10, (int)img.img_Height+72);
		frame.setLayout(new BorderLayout());
		
		frame.getContentPane().setBackground(null);
		
		
		frame.getContentPane().add(img, BorderLayout.CENTER);
		
		
		
		
		
		
		
		frame.setVisible(true);
	}
	
	
		
		
}