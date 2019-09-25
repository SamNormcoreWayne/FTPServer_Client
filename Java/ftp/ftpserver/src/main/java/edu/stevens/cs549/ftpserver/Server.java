package edu.stevens.cs549.ftpserver;

import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Stack;
import java.util.logging.Logger;

import edu.stevens.cs549.ftpinterface.IServer;

/**
 *
 * @author dduggan
 * @author zzhan
 */
public class Server extends UnicastRemoteObject
        implements IServer {
	
	static final long serialVersionUID = 0L;
	
	public static Logger log = Logger.getLogger("edu.stevens.cs.cs549.ftpserver");
    
	/*
	 * For multi-homed hosts, must specify IP address on which to 
	 * bind a server socket for file transfers.  See the constructor
	 * for ServerSocket that allows an explicit IP address as one
	 * of its arguments.
	 */
	private InetAddress host;
	
	final static int backlog = 5;
	
	/*
	 *********************************************************************************************
	 * Current working directory.
	 */
    static final int MAX_PATH_LEN = 1024;
    private Stack<String> cwd = new Stack<String>();
    
    /*
     *********************************************************************************************
     * Data connection.
     */
    
    enum Mode { NONE, PASSIVE, ACTIVE };
    
    private Mode mode = Mode.NONE;
    
    /*
     * If passive mode, remember the server socket.
     */
    
    private ServerSocket dataChan = null;
    
    private InetSocketAddress makePassive () throws IOException {
    	dataChan = new ServerSocket(0, backlog, host);
    	mode = Mode.PASSIVE;
    	System.out.println(dataChan.getLocalSocketAddress());
    	return (InetSocketAddress)(dataChan.getLocalSocketAddress());
    }
    
    /*
     * If active mode, remember the client socket address.
     */
    private InetSocketAddress clientSocket = null;
    
    private void makeActive (InetSocketAddress s) {
    	clientSocket = s;
    	mode = Mode.ACTIVE;
    }
    
    /*
     **********************************************************************************************
     */
            
    /*
     * The server can be initialized to only provide subdirectories
     * of a directory specified at start-up.
     */
    private final String pathPrefix;

    public Server(InetAddress host, int port, String prefix) throws RemoteException {
    	super(port);
    	this.host = host;
    	this.pathPrefix = prefix + "/";
        log.info("A client has bound to a server instance.");
    }
    
    public Server(InetAddress host, int port) throws RemoteException {
        this(host, port, "/");
    }
    
    private boolean valid (String s) {
        // File names should not contain "/".
        return (s.indexOf('/')<0);
    }
    private static class GetThread implements Runnable {
    	private ServerSocket dataChan = null;
    	private Socket cli = null;
    	private FileInputStream file = null;
    	private FileOutputStream fos = null;
    	public GetThread (ServerSocket s, FileInputStream f) { dataChan = s; file = f; }
    	public GetThread (Socket s, FileOutputStream f) {cli = s; fos = f;}
    	public void run () {
    		/*
    		 * TODO: Process a client request to transfer a file.
    		 */
    		try
		    {
		        if (cli != null)
		        {
			        System.out.println("Ostream opened.");
			        InputStream is = cli.getInputStream();
			        System.out.println("b4 FileStream");
			        int size = 0;
			        byte[] buffer = new byte[4096 * 5];
			        /*
			        try
			        {
				        Thread.sleep(2000);
			        }
			        catch (InterruptedException ie)
			        {
				        ie.printStackTrace();
			        }
			         */
			        System.out.println("start while: ");
			        while ((size = is.read(buffer, 0, buffer.length)) > -1)
			        {
				        System.out.println("Read");
				        System.out.println("Write: " + size);
				        fos.write(buffer, 0, size);
			        }
			        fos.flush();
			        fos.close();
			        System.out.println("received!");
		        }
		        else
		        {
			        int reqNum = dataChan.getReceiveBufferSize();
			        int num = 0;
			        for (num = 0; num < reqNum; ++num)
			        {
				        Socket cli = dataChan.accept();
				        PrintStream ps = new PrintStream(cli.getOutputStream());
				        ps.println(file.available());
				        ps.flush();
				        InputStream in = file;
				        Thread.sleep(2000);
				        OutputStream os = cli.getOutputStream();
				        int size = 0;
				        byte[] buffer = new byte[4096 * 5];
				        while ((size = in.read(buffer)) != -1)
				        {
					        System.out.println("Sending package size: " + size);
					        os.write(buffer, 0, size);
					        os.flush();
				        }
			        }
		        }
		    }
		    catch (RemoteException re)
		    {
		        System.out.println("Server Error: cannot ");
		    }
		    catch (SocketException se)
		    {
		        System.out.println("Server Error: Cannot get BufferSize.");
		    }
		    catch (IOException ioe)
		    {
		        System.out.println("Server Error: Cannot get Request");
		    }
		    catch(InterruptedException ie)
		    {
			    ie.printStackTrace();
		    }


    	}
    }
    
    public void get (String file) throws IOException, FileNotFoundException, RemoteException {
        if (!valid(file)) {
            throw new IOException("Bad file name: " + file);
        } else if (mode == Mode.ACTIVE) {
        	Socket xfer = new Socket (clientSocket.getAddress(), clientSocket.getPort());
        	/*
        	 * TODO: connect to client socket to transfer file.
        	 */
        	System.out.println(clientSocket.getAddress());
	        FileInputStream in = new FileInputStream(path()+file);
	        OutputStream os = xfer.getOutputStream();
	        int size = 0;
	        byte[] buffer = new byte[4096 * 5];
			while ((size = in.read(buffer, 0, buffer.length)) > 0)
			{
				System.out.println("Sending package size: " + size);
				os.write(buffer, 0, size);
			}
	        os.flush();
	        os.close();
			in.close();
	        System.out.println("Success");
        	/*
			 * End TODO.
			 * TODO FINISHED
			 */
        } else if (mode == Mode.PASSIVE) {
            FileInputStream f = new FileInputStream(path()+file);
            new Thread (new GetThread(dataChan, f)).start();
        }
    }
    
    public void put (String file) throws IOException, FileNotFoundException, RemoteException {
    	/*
    	 * TODO: Finish put (both ACTIVE and PASSIVE).
    	 */
    	if(!valid(file))
    	    throw new IOException("Bad file name: " + file);
    	else if (mode == Mode.ACTIVE)
	    {
		    System.out.println("In port put");
		    Socket xfer = new Socket (clientSocket.getAddress(), clientSocket.getPort());
		    FileOutputStream fos = new FileOutputStream(path() + file);
		    new Thread (new GetThread(xfer, fos)).start();
	    }
	    else if (mode == Mode.PASSIVE)
	    {
		    System.out.println("In pasv put");
		    Socket cli = dataChan.accept();
		    System.out.println("accepted");
	        FileOutputStream fos = new FileOutputStream(path() + file);
	        new Thread (new GetThread(cli, fos)).start();
	    }
    }
    
    public String[] dir () throws RemoteException {
        // List the contents of the current directory.
        return new File(path()).list();
    }

	public void cd(String dir) throws IOException, RemoteException {
		// Change current working directory (".." is parent directory)
		if (!valid(dir)) {
			throw new IOException("Bad file name: " + dir);
		} else {
			if ("..".equals(dir)) {
				if (cwd.size() > 0)
					cwd.pop();
				else
					throw new IOException("Already in root directory!");
			} else if (".".equals(dir)) {
				;
			} else {
				File f = new File(path());
				if (!f.exists())
					throw new IOException("Directory does not exist: " + dir);
				else if (!f.isDirectory())
					throw new IOException("Not a directory: " + dir);
				else
					cwd.push(dir);
			}
		}
	}

    public String pwd () throws RemoteException {
        // List the current working directory.
        String p = "/";
        for (Enumeration<String> e = cwd.elements(); e.hasMoreElements(); ) {
            p = p + e.nextElement() + "/";
        }
        return p;
    }
    
    private String path () throws RemoteException {
    	return pathPrefix+pwd();
    }
    
    public void port (InetSocketAddress s) {
    	makeActive(s);
    }
    
    public InetSocketAddress pasv () throws IOException {
    	return makePassive();
    }

}
