package smbai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveData {
	public static void save(String fileName) {
		createFile(fileName);
		write(fileName);
		
	}

	private static void createFile(String fileName) {
		 try {
		     File myObj = new File(fileName);
		     if (myObj.createNewFile()) {
		    	 System.out.println("File created: " + myObj.getName());
		     } else {
		         System.out.println("File already exists. Overwriting...");
		     }
		  } catch (IOException e) {
		     System.out.println("An error occurred.");
		     e.printStackTrace();
		  }
	}
	private static void write(String fileName) {
	    try {
	    	FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeInt(SMBAIManager.getAgentIndex());
            out.writeObject(SMBAIManager.getAgentList());
            file.close();
            out.close();
            System.out.println("Successfully wrote to the file.");
	    } catch (IOException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }
	  }
}
