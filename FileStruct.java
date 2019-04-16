package p2p.project;

import java.io.Serializable;
import java.util.ArrayList;

class FileStruct implements Serializable{
	public String fileName;
	public int portNumber;
        public ArrayList <Integer> arrOfPorts;
}
