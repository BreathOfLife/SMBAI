package smbai;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ReadData {

	public static void read(String fileName) {
		try
        {   
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);
            SMBAIManager.setAgentIndex(in.readInt());
            try {
				SMBAIManager.setCatIndexes((int[])(in.readObject()));
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            ArrayList<Agent> agentList = null;
            try {
				agentList = (ArrayList<Agent>)in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            SMBAIManager.setAgentList(agentList);
              
            in.close();
            file.close();
              
            System.out.println("Successfully Retrieved Data");
        } catch (IOException e) {
        	System.out.println("An error occurred.");
		    e.printStackTrace();
        }
	}

}
