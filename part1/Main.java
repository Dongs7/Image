/////////////////////////
// CMPT 365 Proj1 - Part2
// Main.java
// Dong-Hyun Chung
// # 301008631
// 2015-02-27




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


public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFileName;
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
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtFileName = new JTextField();
		txtFileName.setBounds(10, 195, 236, 20);
		contentPane.add(txtFileName);
		txtFileName.setColumns(10);
		
		JButton btnFind = new JButton("Browse");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser ff = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image (*.tiff)", "tiff");
				ff.setFileFilter(filter);
				String fName = null;
		        String dirName = null;
				int returnValue = ff.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	
		          File selectedFile = ff.getSelectedFile();
		          File selectedDir = ff.getCurrentDirectory();
		          dirName = selectedDir.getAbsolutePath();
		          fName = selectedFile.getName();
		          }
		        fileName = fName;
				txtFileName.setText(dirName +"\\"  + fName);
			}
		});
		btnFind.setBounds(275, 178, 137, 23);
		contentPane.add(btnFind);
		
		JButton btnShow = new JButton("Show Image");
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageData img_Data = new ImageData();
				try {
					img_Data.init(txtFileName.getText());
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "No file to read"); // Display dialogue when error occurs.
				}
			}
		});
		btnShow.setBounds(275, 212, 137, 23);
		contentPane.add(btnShow);
	}
}
