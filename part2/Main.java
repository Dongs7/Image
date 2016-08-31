/////////////////////////
// CMPT 365 Proj1 - Part2
// Main.java
// Dong-Hyun Chung
// # 301008631
// 2015-02-27
package pro4;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JLabel;


public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFileName1st;
	String fileName;
	private JTextField txtFileName2nd;
	private JTextField txtFileName3rd;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("READ IMAGE");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 452, 470);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtFileName1st = new JTextField();
		txtFileName1st.setBounds(22, 306, 236, 20);
		contentPane.add(txtFileName1st);
		txtFileName1st.setColumns(10);
		
		JButton btnFind = new JButton("Browse");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser ff = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image (*.tiff,*.tif)", "tiff", "tif");
				ff.setFileFilter(filter);
				String fName = null;
		        String dirName = null;
		        ff.setDialogTitle("Select 1st Image File");
				int returnValue = ff.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	
		          File selectedFile = ff.getSelectedFile();
		          File selectedDir = ff.getCurrentDirectory();
		          dirName = selectedDir.getAbsolutePath();
		          fName = selectedFile.getName();
		          }
		        fileName = fName;
				txtFileName1st.setText(dirName +"\\"  + fName);
				
				ff.setDialogTitle("Select 2nd Image File");
				returnValue = ff.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	
		          File selectedFile = ff.getSelectedFile();
		          File selectedDir = ff.getCurrentDirectory();
		          dirName = selectedDir.getAbsolutePath();
		          fName = selectedFile.getName();
		          }
		        fileName = fName;
				txtFileName2nd.setText(dirName +"\\"  + fName);
				
				ff.setDialogTitle("Select 3rd Image File");
				returnValue = ff.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	
		          File selectedFile = ff.getSelectedFile();
		          File selectedDir = ff.getCurrentDirectory();
		          dirName = selectedDir.getAbsolutePath();
		          fName = selectedFile.getName();
		          }
		        fileName = fName;
				txtFileName3rd.setText(dirName +"\\"  + fName);
			}
		});
		btnFind.setBounds(275, 305, 137, 23);
		contentPane.add(btnFind);
		
		JButton btnShow = new JButton("Show Image");
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageData img_Data = new ImageData();
				
				try {
					img_Data.init(txtFileName1st.getText(),txtFileName2nd.getText(),txtFileName3rd.getText());
					
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "No file to read"); // Display dialogue when error occurs.
				}
			}
		});
		btnShow.setBounds(275, 339, 137, 23);
		contentPane.add(btnShow);
		
		txtFileName2nd = new JTextField();
		txtFileName2nd.setColumns(10);
		txtFileName2nd.setBounds(22, 356, 236, 20);
		contentPane.add(txtFileName2nd);
		
		txtFileName3rd = new JTextField();
		txtFileName3rd.setColumns(10);
		txtFileName3rd.setBounds(22, 399, 236, 20);
		contentPane.add(txtFileName3rd);
		
		JLabel lblSecondImage = new JLabel("Second Image");
		lblSecondImage.setBounds(22, 341, 87, 14);
		contentPane.add(lblSecondImage);
		
		JLabel lblThirdImage = new JLabel("Third Image");
		lblThirdImage.setBounds(22, 385, 87, 14);
		contentPane.add(lblThirdImage);
		
		JLabel lblFirstImage = new JLabel("First Image");
		lblFirstImage.setBounds(22, 290, 87, 14);
		contentPane.add(lblFirstImage);
		
		JButton btnCompress = new JButton("Compress!!");
		btnCompress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageData img_Data = new ImageData();
				
				try {
					img_Data.compInit(txtFileName1st.getText(),txtFileName2nd.getText(),txtFileName3rd.getText());
					
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "No file to read"); // Display dialogue when error occurs.
				}
			}
		});
		btnCompress.setBounds(275, 398, 137, 23);
		contentPane.add(btnCompress);
	}
}
