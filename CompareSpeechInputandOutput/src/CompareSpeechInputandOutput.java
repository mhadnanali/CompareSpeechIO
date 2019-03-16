import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import java.io.FileOutputStream;

public class CompareSpeechInputandOutput {
	JFrame mainFrame;
	JFileChooser fc;
	JTextArea log;
	int serialnum;
	String[] ar;
	File outputfile, inputfile,excelfile,inputmatchfile,inputtransfile;
	FileOutputStream outputStream= null;
	FileInputStream inputStream= null;
	XSSFWorkbook workbook=null;
	XSSFSheet sheet = null;
	DefaultTableModel model;
	Row row; Cell cell;           int colNum = 0;
	 int rowNum = 0;
	CompareSpeechInputandOutput(){
		mainFrame = new JFrame("Compare Speech Input and Output");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		 fc = new JFileChooser();
//		mainFrame.setResizable(false);
		
	}
	public void drawGui(){
		
		JPanel parentpanel= new JPanel(new BorderLayout());
		JPanel subpanel= new JPanel(); //use FlowLayout
		JPanel subpanel2= new JPanel(); //use FlowLayout
		JPanel buttonPanel = new JPanel(); //use FlowLayout
        
		JButton inputbutton= new JButton("Select Input File");
		JButton outputbutton= new JButton("Select Output File");
		JButton excelbutton= new JButton("Select Excel File");
		JButton compare= new JButton("Compare Text Files");
		/////
		JButton transfilebutton= new JButton("Transcription File");
		JButton transbutton= new JButton("Create Text File");
		transfilebutton.setBackground(Color.green);
		transbutton.setBackground(Color.green);
		JButton matchfilebutton= new JButton("Match File");
		JButton matchbutton= new JButton("Create Text File");
		log=new JTextArea("");
		log.setEditable(false);
		JScrollPane jsp1=new JScrollPane(log);
        //jsp.setBounds(10, 0, 457, 103);
        jsp1.setVisible(true);
		
        subpanel.add(inputbutton);
        subpanel.add(outputbutton);
        subpanel.add(excelbutton);
        subpanel.add(compare);
        subpanel.setBackground(Color.GRAY);
        buttonPanel.add(subpanel);
        subpanel2.add(transfilebutton); 
        subpanel2.add(transbutton);
        
        subpanel2.add(matchfilebutton);
        subpanel2.add(matchbutton);
        subpanel2.setBackground(Color.DARK_GRAY);
        buttonPanel.add(subpanel2);
        parentpanel.add(buttonPanel, BorderLayout.PAGE_START);
        parentpanel.add(jsp1, BorderLayout.CENTER);
        
        
         model = new DefaultTableModel();      
        JTable table = new JTable(model);
        table.setFont(new Font("Times New Roman", Font.BOLD, 15));
        
        model.addColumn("ID");
        model.addColumn("Input Word");
        model.addColumn("Replaced With");
        JScrollPane jsp=new JScrollPane(table);
        //jsp.setBounds(10, 0, 457, 103);
        jsp.setVisible(true);
        parentpanel.add(jsp, BorderLayout.SOUTH);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        
        inputbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(mainFrame);
				inputfile = fc.getSelectedFile();
				log.append("Selected " + inputfile.getName()+"\n");
				//JOptionPane.showMessageDialog(null, "File path is "+inputfile);
			}
	    });
        outputbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(mainFrame);
				 outputfile = fc.getSelectedFile();
				 log.append("Selected " + outputfile.getName()+"\n");
				//JOptionPane.showMessageDialog(null, "File path is "+outputfile);
			}
	    });
        excelbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(mainFrame);
				 excelfile = fc.getSelectedFile();
				 log.append("Selected " + excelfile.getName()+"\n");
				//JOptionPane.showMessageDialog(null, "File path is "+outputfile);
			}
	    });
        compare.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(inputfile!=null && outputfile!=null && excelfile !=null){
					try {
						compareFiles(inputfile,outputfile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				if(inputfile==null){
					JOptionPane.showMessageDialog(null, "Select input file First ");
					
				}
				else if(outputfile==null){
					JOptionPane.showMessageDialog(null, "Select output file First ");
					
				}
				else if(excelfile==null){
					JOptionPane.showMessageDialog(null, "Select Excel file First ");
					
				}
				
			}
	    });
        
        
        //dgfsfdg
        transfilebutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(mainFrame);
				inputtransfile = fc.getSelectedFile();
				log.append("Selected " + inputtransfile.getName()+"\n");
				//JOptionPane.showMessageDialog(null, "File path is "+inputfile);
			}
	    });
        matchfilebutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(mainFrame);
				 inputmatchfile = fc.getSelectedFile();
				 log.append("Selected " + inputmatchfile.getName()+"\n");
				//JOptionPane.showMessageDialog(null, "File path is "+outputfile);
			}
	    });
        transbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(inputtransfile!=null){
					onlyTextTranscipt(inputtransfile); //return
				}
				else
				if(inputtransfile==null){
					JOptionPane.showMessageDialog(null, "Select Transcription file First ");
				}				 
			}
	    });
        matchbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				 if (inputmatchfile!=null){
					cleanMatchfile(inputmatchfile);
				}
				else if(inputmatchfile==null){
					JOptionPane.showMessageDialog(null, "Select Match file First ");
				}
					
			}
	    });
        
        mainFrame.add(parentpanel);
        mainFrame.setVisible(true);
		
	}
	void compareFiles(File inputfile , File outputfiles ) throws IOException{
		
		 BufferedReader br10 = null;
        BufferedReader br20 = null;
       
       // String file ="D:\\input\\combiner\\249 line analysis\\Transcriptfile 249 lines.txt";
	//	String file2 ="D:\\input\\combiner\\249 line analysis\\249 without LM.txt";
		String textoutput ="D:\\input\\differentwords in 2490 lines.txt";
        try{
       	 br10 = new BufferedReader(new FileReader(inputfile));
    		 br20 = new BufferedReader(new FileReader(outputfiles));
        while(true){
    		ArrayList<String> output = new ArrayList<String>();
    		//String lineA = "stuff and a few things";
    		//String lineB = "stuff and maybe many things";
    		String lineA=br10.readLine();
			String lineB=br20.readLine();
			if(lineA==null || lineB==null)
				break;


    		String[] a = lineA.split(" ");
    		String[] b = lineB.split(" ");
    		int counterA = 0;
    		int counterB = 0;

    		while(counterA < a.length && counterB < b.length)
    		{
    		    if(a[counterA].equals(b[counterB])) //check if next two elements are equal
    		    {
    		        counterA++;
    		        counterB++;
    		        continue;
    		    }

    		    //search from array a looking for matches in array b
    		    int aStart = -1;
    		    int aStop = -1;
    		    for(aStart = counterA; aStart < a.length && aStop == -1; aStart++)
    		        for(int i = counterB; i < b.length && aStop == -1; i++)
    		            if(a[aStart].equals(b[i]))
    		                aStop = i;
    		    if(aStop == -1) //no matches, select rest of both lists
    		    {
    		        aStart++; //need to inc start one extra time
    		        aStop = b.length;
    		    }

    		    //search from array b looking for matches in array a
    		    int bStart = -1;
    		    int bStop = -1;
    		    for(bStart = counterB; bStart < b.length && bStop == -1; bStart++)
    		        for(int i = counterA; i < a.length && bStop == -1; i++)
    		            if(b[bStart].equals(a[i]))
    		                bStop = i;
    		    if(bStop == -1) //no matches, select rest of both lists
    		    {
    		        bStart++; //need to inc one extra time
    		        bStop = a.length;
    		    }

    		    //find which one is more similar
    		    int aDist = Math.abs((--aStart - counterA) - (aStop - counterB)); //aStart and bStart are incremented 1 too many times
    		    int bDist = Math.abs((--bStart - counterB) - (bStop - counterA));

    		    if(aDist < bDist) //a's findings are a better match
    		    {
    		        String out = "";
    		        for(int i = counterA; i < aStart; i++)
    		            out += a[i] + " ";
    		        out += "=";
    		        for(int i = counterB; i < aStop; i++)
    		            out += b[i] + " ";
    		        out = out.substring(0, out.length() - 1); //remove last space
    		        output.add(out);
    		        counterA = aStart;
    		        counterB = aStop;
    		    }
    		    else //b's findings are a better match
    		    {
    		        String out = "";
    		        for(int i = counterA; i < bStop; i++)
    		            out += a[i] + " ";
    		        out += "=";
    		        for(int i = counterB; i < bStart; i++)
    		            out += b[i] + " ";
    		        out = out.substring(0, out.length() - 1); //remove last space
    		        output.add(out);
    		        counterA = bStop;
    		        counterB = bStart;
    		    }
    		}
    		serialnum++;
    		System.out.println(output.toString());
    		//log.append(output.toString()+"\n");
    		for(int i=1;i<output.size();i++){
    			//if(!output.isEmpty()){
    			//System.out.println("Reading from array "+output.get(i));
    			String st=output.get(i);
    			 ar= st.split("=");
    			
    			if(ar.length==2){
    				System.out.println(ar[0]+ "  " + ar[1]);
    				model.addRow(new Object[]{serialnum,ar[0],ar[1]});
    				secTry(serialnum,ar[0],ar[1]);
    				
    			}
    			else if(ar.length==1)
    			{
    				model.addRow(new Object[]{serialnum,ar[0]});
    				secTry(serialnum,ar[0]," ");
    			}
    				
    			else if(ar.length==0)
    				continue;
    			else
    				continue;
    			//}
    		}
    		filewriter(output.toString(),textoutput,"");
    		
        }//while
    		
        }
        catch(Exception e){
       	 System.out.println("Exception caught "+e.getMessage());
        }
        //System.out.println(output.size());
	}
	//Write Text File 
	  void filewriter(String fileone,String filename, String filetwo) throws IOException{
		 BufferedWriter fbw =null;
		 String files;
		 try{
		        files=fileone+""+filetwo;
	         OutputStreamWriter writer = new OutputStreamWriter(
	               new FileOutputStream(filename, true), "UTF-8");
	          fbw = new BufferedWriter(writer);
	         fbw.write(files);
	         fbw.newLine();
	         
	     }catch (Exception e) {
	         System.out.println("Error: " + e.getMessage());
	     }
	     finally {
	    	 fbw.close();
			}
	 }
	  //Create Excel Sheet
	  void secTry(int id, String before, String after) throws InvalidFormatException{
		  String excelFilePath = excelfile.getPath();
		  
			
			try {
				FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
				org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(inputStream);

				org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

				Object[][] bookData = {
						{id, before, after}
				};

				int rowCount = sheet.getLastRowNum();

				for (Object[] aBook : bookData) {
					Row row = sheet.createRow(++rowCount);

					int columnCount = 0;
					
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(rowCount);
					
					for (Object field : aBook) {
						cell = row.createCell(++columnCount);
						if (field instanceof String) {
							cell.setCellValue((String) field);
						} else if (field instanceof Integer) {
							cell.setCellValue((Integer) field);
						}
					}

				}

				inputStream.close();

				FileOutputStream outputStream = new FileOutputStream(excelFilePath);
				workbook.write(outputStream);
				
				outputStream.close();
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
	  }

	  String onlyTextTranscipt(File filename){
			 
		   String file =filename.getAbsolutePath();
			String outputfile =filename.getParent()+"\\"+filename.getName()+".txt";
			try {		
				BufferedReader br = new BufferedReader(new FileReader(file));
				int counter=0;
				String nextLine;
				while ((nextLine=br.readLine())!=null) {
				        String lastWord = nextLine.substring( nextLine.indexOf("("));
				        nextLine=nextLine.replace(lastWord, "").replace("<s>", "").replace("</s>", "");
				        System.out.println(nextLine);
				        filewriter(nextLine,outputfile,"");
				        log.append(nextLine+"\n");
				}
				 }
				 catch(Exception e){
					   	System.out.println(e.getMessage());
				 }
			JOptionPane.showMessageDialog(mainFrame, outputfile+ " File Created ");
			return outputfile;
		
		}
	  String cleanMatchfile(File filename){
			//It axtracts urdu from trans lines.		 
		   String file =filename.getAbsolutePath();
			String outputfile =filename.getParent()+"\\"+filename.getName()+".txt";
			try {		
				BufferedReader br = new BufferedReader(new FileReader(file));
				int counter=0;
				String nextLine;
				while ((nextLine=br.readLine())!=null) {
				        String lastWord = nextLine.substring( nextLine.indexOf("("));
				        nextLine=nextLine.replace(lastWord, "").replace("<s>", "").replace("</s>", "");
				        System.out.println(nextLine);
				        filewriter(nextLine,outputfile,"");
				        log.append(nextLine+"\n");
				}
				 }
				 catch(Exception e){
					   	System.out.println(e.getMessage());
				 }
			JOptionPane.showMessageDialog(mainFrame, outputfile+ " File Created ");
			return outputfile;
		
		}
	  
	public static void main(String[] args) throws InvalidFormatException{
		CompareSpeechInputandOutput obj= new CompareSpeechInputandOutput();
		obj.drawGui();
		//obj.excelWriter();
		//obj.secondTry();
		//obj.secTry();
	
	}
}

