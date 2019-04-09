package p2p.project;

import java.io.Serializable;
import java.util.ArrayList;

class FileStruct implements Serializable{
    	public int peerid;
	public String fileName;
	public int portNumber;
        public ArrayList <Integer> arrOfPorts;
}
