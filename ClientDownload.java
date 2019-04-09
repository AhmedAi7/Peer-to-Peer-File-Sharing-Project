
package p2p.project;
 
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
   
public class ClientDownload extends Thread
{
    int peerServerPort;
    String  directoryPath=null;
    ServerSocket dwldServerSocket;
    Socket dwldSocket=null;
    
    ClientDownload(int peerServerPort,String directoryPath) {        
    	this.peerServerPort=peerServerPort;
    	this.directoryPath=directoryPath;    
    }
    @Override
    public void run(){
        try {
            dwldServerSocket = new ServerSocket(peerServerPort);
            dwldSocket = dwldServerSocket.accept();
            new ClientDownloadThread(dwldSocket,directoryPath).start();
        } catch (IOException ex) {
            Logger.getLogger(ClientDownload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
} 
class ClientDownloadThread extends Thread
{
    Socket dwldThreadSocket;
    String directoryPath;
    public ClientDownloadThread(Socket dwldThreadSocket,String directoryPath)
    {
        this.dwldThreadSocket=dwldThreadSocket;       
        this.directoryPath=directoryPath;
    }
	public void run()
    {
            ObjectOutputStream objOS = null;
            try {
            objOS = new ObjectOutputStream(dwldThreadSocket.getOutputStream());
            ObjectInputStream objIS = new ObjectInputStream(dwldThreadSocket.getInputStream());
            String fileName = null;
                try {
                    fileName = (String)objIS.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClientDownloadThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            String fileLocation;// Stores the directory name
            while(true)
            {
                String fileplace =directoryPath+"//"+fileName;
                File myFile = new File(fileplace);
                long length = myFile.length();
                
                byte [] byte_arr = new byte[(int)length];
                
                objOS.writeObject((int)myFile.length());
                objOS.flush();
                
                FileInputStream FIS=new FileInputStream(myFile);
                BufferedInputStream objBIS = new BufferedInputStream(FIS);
                objBIS.read(byte_arr,0,(int)myFile.length());
                
                //System.out.println("Sending the file of " +byte_arr.length+ " bytes");
                
                objOS.write(byte_arr,0,byte_arr.length);
                //objOS.flush();                  
                objOS.writeObject((String)fileplace);
                objOS.flush();
            }
        }
         catch (IOException ex) {
            Logger.getLogger(ClientDownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                objOS.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientDownloadThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
     }
    }