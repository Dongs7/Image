/////////////////////////
// CMPT 365 Proj1 - Part2
// Main.java
// Dong-Hyun Chung
// # 301008631
// 2015-02-27
package pro5;
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
		setBounds(100, 100, 452, 214);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtFileName1st = new JTextField();
		txtFileName1st.setBounds(96, 63, 236, 20);
		contentPane.add(txtFileName1st);
		txtFileName1st.setColumns(10);
		
		JButton btnFind = new JButton("Browse");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser ff = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("compressed file (*.thr)", "thr");
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
				
			}
		});
		btnFind.setBounds(147, 94, 137, 23);
		contentPane.add(btnFind);
		
		JButton btnShow = new JButton("Decode & Show Image");
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageData img_Data = new ImageData();
				
				try {
					img_Data.init(txtFileName1st.getText());
					
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "No file to read"); // Display dialogue when error occurs.
					ex.printStackTrace();
				}
			}
		});
		btnShow.setBounds(96, 128, 236, 23);
		contentPane.add(btnShow);
		
		JLabel lblFirstImage = new JLabel(".thr File");
		lblFirstImage.setBounds(96, 47, 87, 14);
		contentPane.add(lblFirstImage);
	}
}
