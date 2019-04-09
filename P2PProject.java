package p2p.project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class P2PProject {

    public static ArrayList<FileStruct> globalArray = new ArrayList<FileStruct>();

    public static void main(String[] args) throws IOException {
        System.out.println("1. Run as Server");
        System.out.println("2. Run as Client");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int choice = Integer.parseInt(reader.readLine());
        if (choice == 1) {
            int ServerPort;
            System.out.println("Enter the port number of the server : ");
            ServerPort = Integer.parseInt(reader.readLine());
            runServer(ServerPort);
        } else if (choice == 2) {
            try {
                Socket socket;
                ArrayList<FileStruct> arrList = new ArrayList<FileStruct>();
                Scanner scanner = new Scanner(System.in);
                ObjectInputStream ois;
                ObjectOutputStream oos;
                //ArrayList al;  
                String string;
                Object o, b;
                String directoryPath = null;
                int peerServerPort = 0;
                int PortToConnect = 0;
                System.out.println("Welcome to the Client ::");
                System.out.println("Enter the port number on which the peer should act as server ::");
                peerServerPort = Integer.parseInt(reader.readLine());
                System.out.println("Enter the port number you want to connect with :");
                PortToConnect = Integer.parseInt(reader.readLine());
                socket = new Socket("localhost", PortToConnect);
                System.out.println("Connection has been established with the client");
                System.out.println("Browse a Folder To make it Shareable .. ");
                directoryPath = Browse();
                ClientDownload objServerDownload = new ClientDownload(peerServerPort, directoryPath);
                objServerDownload.start();
                /*
                Socket clientThread = new Socket("localhost", PortToConnect);

                ObjectOutputStream objOutStream = new ObjectOutputStream(clientThread.getOutputStream());
                ObjectInputStream objInStream = new ObjectInputStream(clientThread.getInputStream());

                al = new ArrayList();
                */
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Enter the peerid for this directory :");
                int readpid = Integer.parseInt(reader.readLine());
                
                System.out.println("1) Share File with any client ");
                System.out.println("2) Choose Specific clients to share with ");
                choice = Integer.parseInt(reader.readLine());
                ArrayList <Integer> arrayOfPorts=new ArrayList<Integer>();
                
                if (choice ==1)
                {
                    arrayOfPorts.add(1);
                }
                else if (choice == 2)
                {
                    int PortToAdd;
                    while (true)
                    {
                        System.out.println(" ");
                        System.out.println("ADD Port Number for client to share the file with: ");
                        PortToAdd = Integer.parseInt(reader.readLine());
                        arrayOfPorts.add(PortToAdd);
                        System.out.println("Done.. ");
                        System.out.println("1) Add another one ");
                        System.out.println("2) Save and Continue ");
                        choice = Integer.parseInt(reader.readLine());
                        if (choice==2)
                            break;
                        }
                            
                    }
                
                File folder = new File(directoryPath);
                File[] listofFiles = folder.listFiles();
                FileStruct currentFile;
                File file;
                for (int i = 0; i < listofFiles.length; i++) {
                    currentFile = new FileStruct();
                    file = listofFiles[i];
                    currentFile.fileName = file.getName();
                    currentFile.peerid = readpid;
                    currentFile.portNumber = peerServerPort;
                    currentFile.arrOfPorts=arrayOfPorts;
                    arrList.add(currentFile);
                }
                oos.writeObject(arrList);
                //System.out.println("The complete ArrayList :::"+arrList);
                System.out.println("Enter the desired file name that you want to downloaded from the list of the files available in the Server ::");
                String fileNameToDownload = reader.readLine();
                oos.writeObject(fileNameToDownload);
                oos.writeObject(peerServerPort);
                System.out.println("Waiting for the reply from Server...!!");
                ArrayList<FileStruct> peers = new ArrayList<FileStruct>();
                peers = (ArrayList<FileStruct>) ois.readObject(); // take arr from server class
                for (int i = 0; i < peers.size(); i++) {
                    int result = peers.get(i).peerid;
                    int port = peers.get(i).portNumber;
                    System.out.println("The file is stored at peer id " + result + " on port " + port);
                }
                System.out.println("Enter the respective port number of the above peer id :");
                int clientAsServerPortNumber = Integer.parseInt(reader.readLine());
                System.out.println("Enter the desired peer id from which you want to download the file from :");
                int clientAsServerPeerid = Integer.parseInt(reader.readLine());
                clientRecieve(clientAsServerPeerid, clientAsServerPortNumber, fileNameToDownload, directoryPath);
                //runServer(peerServerPort);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(P2PProject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static String Browse () {
        
      System.out.println("ssss ");
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle("choosertitle");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);

    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile().getAbsolutePath();
    } else {
      System.out.println("No Selection ");
    }
        return null;
  }
    public static void clientRecieve(int clientAsServerPeerid, int clientAsServerPortNumber, String fileNamedwld, String directoryPath) throws ClassNotFoundException {
        try {
            Socket clientRecievesocket = new Socket("localhost", clientAsServerPortNumber);

            ObjectOutputStream clientAsServerOOS = new ObjectOutputStream(clientRecievesocket.getOutputStream());
            ObjectInputStream clientAsServerOIS = new ObjectInputStream(clientRecievesocket.getInputStream());

            clientAsServerOOS.writeObject(fileNamedwld);
            int readBytes = (int) clientAsServerOIS.readObject();

            //System.out.println("Number of bytes that have been transferred are ::"+readBytes);
            byte[] b = new byte[readBytes];
            clientAsServerOIS.readFully(b);
            OutputStream fileOPstream = new FileOutputStream(directoryPath + "//" + fileNamedwld);

            BufferedOutputStream BOS = new BufferedOutputStream(fileOPstream);
            BOS.write(b, 0, (int) readBytes);

            System.out.println("Requested file - " + fileNamedwld + ", has been downloaded to your desired directory " + directoryPath);
            System.out.println(" ");
            System.out.println("Display file " + fileNamedwld);
            
            String fileplace = (String) clientAsServerOIS.readObject();
            File myfile = new File(fileplace);
            Scanner myReader = new Scanner(myfile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();

            BOS.flush();
        } catch (IOException ex) {
            Logger.getLogger(P2PProject.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void runServer(int ServerPort) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        serverSocket = new ServerSocket(ServerPort);
        System.out.println("Server started...");
        System.out.println("Waiting for Client...");
        while (true) {
            socket = serverSocket.accept();
            new MultibleServer(socket, globalArray).start();
        }
    }
}

class MultibleServer extends Thread // to service more than one client at the same time 
{

    protected Socket socket;
    ArrayList<FileStruct> globalArray;

    public MultibleServer(Socket clientSocket, ArrayList<FileStruct> globalArray) {
        this.socket = clientSocket;
        this.globalArray = globalArray;
    }

    ArrayList<FileStruct> filesList = new ArrayList<FileStruct>();
    ObjectOutputStream oos;
    ObjectInputStream ois;
    String str;
    int index;
    int PortNum;
    
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(is);
            filesList = (ArrayList<FileStruct>) ois.readObject();
            System.out.println("All the available files from the given directory have been recieved to the Server!");
            for (int i = 0; i < filesList.size(); i++) {
                globalArray.add(filesList.get(i));
            }
            System.out.println("Total number of files available in the Server that are received from all the connected clients: " + globalArray.size());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Index out of bounds exception");
        } catch (IOException e) {
            System.out.println("I/O exception");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found exception");
        }

        try {
            str = (String) ois.readObject();
            PortNum = (Integer) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MultibleServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<FileStruct> sendingPeers = new ArrayList<FileStruct>();
        System.out.println("Searching for the file name...!!!");

        for (int j = 0; j < globalArray.size(); j++) {
            FileStruct fileInfo = globalArray.get(j);
            Boolean x = null;
            try {
                x = fileInfo.fileName.equals(str) && Check(PortNum,fileInfo.arrOfPorts);
            } catch (IOException ex) {
                Logger.getLogger(MultibleServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (x) {
                index = j;
                sendingPeers.add(fileInfo);
            }
        }

        try {
            oos.writeObject(sendingPeers); // Send arr to client class
        } catch (IOException ex) {
            Logger.getLogger(MultibleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean Check(int Port ,ArrayList<Integer> array) throws IOException {
        if (array.get(0)==1)
            return true;
        else {
        for (int i = 0; i < array.size(); i++) 
        {
            if (array.get(i)==Port)
                return true;
        }
        return false;
        }
    }
}