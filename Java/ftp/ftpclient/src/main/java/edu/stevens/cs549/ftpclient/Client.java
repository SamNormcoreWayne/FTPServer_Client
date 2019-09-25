/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stevens.cs549.ftpclient;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

// import org.apache.log4j.PropertyConfigurator;

import edu.stevens.cs549.ftpinterface.IServer;
import edu.stevens.cs549.ftpinterface.IServerFactory;
import edu.stevens.cs549.ftpserver.ServerFactory;

/**
 * 
 * @author dduggan
 */
public class Client {

	private static String clientPropsFile = "/client.properties";
	private static String loggerPropsFile = "/log4j.properties";

	protected String clientIp;
	
	protected String serverAddr;
	
	protected int serverPort;
	
	private static Logger log = Logger.getLogger(Client.class.getCanonicalName());

	public void severe(String s) {
		log.severe(s);
	}

	public void warning(String s) {
		log.info(s);
	}

	public void info(String s) {
		log.info(s);
	}

	protected List<String> processArgs(String[] args) {
		List<String> commandLineArgs = new ArrayList<String>();
		int ix = 0;
		Hashtable<String, String> opts = new Hashtable<String, String>();

		while (ix < args.length) {
			if (args[ix].startsWith("--")) {
				String option = args[ix++].substring(2);
				if (ix == args.length || args[ix].startsWith("--"))
					severe("Missing argument for --" + option + " option.");
				else if (opts.containsKey(option))
					severe("Option \"" + option + "\" already set.");
				else
					opts.put(option, args[ix++]);
			} else {
				commandLineArgs.add(args[ix++]);
			}
		}
		/*
		 * Overrides of values from configuration file.
		 */
		Enumeration<String> keys = opts.keys();
		while (keys.hasMoreElements()) {
			String k = keys.nextElement();
			if ("clientIp".equals(k))
				clientIp = opts.get("host");
			else if ("serverAddr".equals(k))
				serverAddr = opts.get("serverAddr");
			else if ("serverPort".equals(k))
				serverPort = Integer.parseInt(opts.get("http"));
			else
				severe("Unrecognized option: --" + k);
		}

		return commandLineArgs;
	}
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		new Client(args);
	}
	
	public Client(String[] args) {
		try {
			// PropertyConfigurator.configure(getClass().getResource(loggerPropsFile));
			/*
			 * Load server properties.
			 */
			Properties props = new Properties();
			InputStream in = getClass().getResourceAsStream(clientPropsFile);
			props.load(in);
			in.close();
			clientIp = (String) props.get("client.ip");
			serverAddr = (String) props.get("server.machine");
			String serverName = (String) props.get("server.name");
			serverPort = Integer.parseInt((String) props.get("server.port"));
			
			/*
			 * Overrides from command-line
			 */
			processArgs(args);
			
			/*
			 * TODO: Get a server proxy.
			 */
			/*
			 * Tried to use ServerFactory in ftpserver.
			 * Failed because constructor needs to specify pathPrefix where should be ${server.root};
			 * But IServerFactory which is an interface will do this automatically for us.
			 *
			 * Java.rmi.registry encapsulated 2 classes for us to access another process runnin in remote JVM
			 */
			Registry reg = LocateRegistry.getRegistry(serverAddr, serverPort);
			IServerFactory serverFac = (IServerFactory) reg.lookup(serverName);
			IServer server = serverFac.createServer();
			/*
			 * Start CLI.  Second argument should be server proxy.
			 */
			cli(serverAddr, server);

		} catch (java.io.FileNotFoundException e) {
			log.severe("Client error: " + clientPropsFile + " file not found.");
		} catch (java.io.IOException e) {
			log.severe("Client error: IO exception.");
			e.printStackTrace();
		} catch (Exception e) {
			log.severe("Client exception:");
			e.printStackTrace();
		}

	}

	static void msg(String m) {
		System.out.print(m);
	}

	static void msgln(String m) {
		System.out.println(m);
	}

	static void err(Exception e) {
		System.err.println("Error : "+e);
		e.printStackTrace();
	}

	public static void cli(String svrHost, IServer svr) {

		// Main command-line interface loop

		try {
			InetAddress serverAddress = InetAddress.getByName(svrHost);
			Dispatch d = new Dispatch(svr, serverAddress);
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				msg("ftp> ");
				String line = in.readLine();
				String[] inputs = line.split("\\s+");
				if (inputs.length > 0) {
					String cmd = inputs[0];
					if (cmd.length()==0)
						;
					else if ("get".equals(cmd))
						d.get(inputs);
					else if ("put".equals(cmd))
						d.put(inputs);
					else if ("cd".equals(cmd))
						d.cd(inputs);
					else if ("pwd".equals(cmd))
						d.pwd(inputs);
					else if ("dir".equals(cmd))
						d.dir(inputs);
					else if ("ldir".equals(cmd))
						d.ldir(inputs);
					else if ("port".equals(cmd))
						d.port(inputs);
					else if ("pasv".equals(cmd))
						d.pasv(inputs);
					else if ("help".equals(cmd))
						d.help(inputs);
					else if ("quit".equals(cmd))
						return;
					else
						msgln("Bad input.  Type \"help\" for more information.");
				}
			}
		} catch (EOFException e) {
		} catch (UnknownHostException e) {
			err(e);
			System.exit(-1);
		} catch (IOException e) {
			err(e);
			System.exit(-1);
		}
		

	}

	public static class Dispatch {

		private IServer svr;
		
		private InetAddress serverAddress;

		Dispatch(IServer s, InetAddress sa) {
			svr = s;
			serverAddress = sa;
		}

		public void help(String[] inputs) {
			if (inputs.length == 1) {
				msgln("Commands are:");
				msgln("  get filename: download file from server");
				msgln("  put filename: upload file to server");
				msgln("  pwd: current working directory on server");
				msgln("  cd filename: change working directory on server");
				msgln("  dir: list contents of working directory on server");
				msgln("  ldir: list contents of current directory on client");
				msgln("  port: server should transfer files in active mode");
				msgln("  pasv: server should transfer files in passive mode");
				msgln("  quit: exit the client");
			}
		}

		/*
		 * ********************************************************************************************
		 * Data connection.
		 */

		enum Mode {
			NONE, PASSIVE, ACTIVE
		};

		/*
		 * Note: This refers to the mode of the SERVER.
		 */
		private Mode mode = Mode.NONE;

		/*
		 * If active mode, remember the client socket.
		 */

		private ServerSocket dataChan = null;

		private InetSocketAddress makeActive() throws IOException {
			dataChan = new ServerSocket(0, 5, InetAddress.getLocalHost());
			mode = Mode.ACTIVE;
			/* 
			 * Note: this only works (for the server) if the client is not behind a NAT.
			 */
			return (InetSocketAddress) (dataChan.getLocalSocketAddress());
		}

		/*
		 * If passive mode, remember the server socket address.
		 */
		private InetSocketAddress serverSocket = null;

		private void makePassive(InetSocketAddress s) {
			serverSocket = s;
			mode = Mode.PASSIVE;
		}

		/*
		 * *********************************************************************************************
		 */

		private static class GetThread implements Runnable {
			/*
			 * This client-side thread runs when the server is active mode and a
			 * file download is initiated. This thread listens for a connection
			 * request from the server. The client-side server socket (...)
			 * should have been created when the port command put the server in
			 * active mode.
			 */
			private ServerSocket dataChan = null;
			private FileOutputStream file = null;
			public GetThread(ServerSocket s, FileOutputStream f) {
				dataChan = s;
				file = f;
			}
			public void run() {
				try {
					/*
					 * TODO: Complete this thread.
					 */
					System.out.println("Start get");
					Socket xfer = dataChan.accept();
					System.out.println("Request accepted!");
					InputStream is = xfer.getInputStream();
					int size = 0;
					byte[] buffer = new byte[4096 * 5];
					System.out.println("B4 while");
					while ((size = is.read(buffer, 0, buffer.length)) > -1)
					{
						System.out.println("While-ing");
						file.write(buffer, 0, size);
					}
					file.flush();
					file.close();
					is.close();
					System.out.println("Received.");
					/*
					 * End TODO
					 */
				} catch (IOException e) {
					msg("Exception: " + e);
					e.printStackTrace();
				}
			}
		}

		public void get(String[] inputs) {
			if (inputs.length == 2) {
				try {
					if (mode == Mode.PASSIVE) {
						svr.get(inputs[1]);
						FileOutputStream f = new FileOutputStream(inputs[1]);
						Socket xfer = new Socket(serverAddress, serverSocket.getPort());
						/*
						 * TODO: connect to server socket to transfer file.
						 */
						DataInputStream is = new DataInputStream(xfer.getInputStream());
						InputStreamReader isr = new InputStreamReader(xfer.getInputStream());
						long count = 0;
						int size = 0;
						BufferedReader br = new BufferedReader(isr);
						long filesize = Long.parseLong(br.readLine().trim());
						byte[] buffer = new byte[4096 * 5];
						while (count < filesize)
						{
							size = is.read(buffer);
							f.write(buffer, 0, size);
							f.flush();
							count += size;
						}
						f.close();
					} else if (mode == Mode.ACTIVE) {
						FileOutputStream f = new FileOutputStream(inputs[1]);
						new Thread(new GetThread(dataChan, f)).start();
						svr.get(inputs[1]);
					} else {
						msgln("GET: No mode set--use port or pasv command.");
					}
				} catch (Exception e) {
					err(e);
				}
			}
		}

		public void put(String[] inputs) {
			if (inputs.length == 2) {
				try {
					/*
					 * TODO: Finish put (both ACTIVE and PASSIVE mode supported).
					 */

					if (mode == Mode.PASSIVE)
					{
					/*
						File fp = new File(inputs[1]);
						if (!fp.exists())
							System.out.println("File does not exist");
						fp = null;

					 */
						FileInputStream fis = new FileInputStream(inputs[1]);
						Socket xfer = new Socket(serverAddress, serverSocket.getPort());
						svr.put(inputs[1]);
						OutputStream os = xfer.getOutputStream();
						byte[] buffer = new byte[4096 * 5];
						int size = 0;
						while ((size = fis.read(buffer, 0, buffer.length)) > 0)
						{
							os.write(buffer, 0, size);
							System.out.println("While-ing");
						}
						os.flush();
						os.close();
						fis.close();
					}
					else if (mode == Mode.ACTIVE)
					{
						svr.put(inputs[1]);
						FileInputStream fis = new FileInputStream(inputs[1]);
						Socket xfer = dataChan.accept();
						System.out.println("Req Accepted!");
						OutputStream os = xfer.getOutputStream();
						byte[] buffer = new byte[4096 * 5];
						int size = 0;
						System.out.println("Start to trans:");
						while ((size = fis.read(buffer, 0, buffer.length)) > -1)
						{
							System.out.println("Putting!");
							os.write(buffer, 0, size);
						}
						os.flush();
						os.close();
						fis.close();
						System.out.println("Put Ends");
					}
				} catch (Exception e) {
					err(e);
				}
			}
		}

		public void cd(String[] inputs) {
			if (inputs.length == 2)
				try {
					svr.cd(inputs[1]);
					msgln("CWD: "+svr.pwd());
				} catch (Exception e) {
					err(e);
				}
		}

		public void pwd(String[] inputs) {
			if (inputs.length == 1)
				try {
					msgln("CWD: "+svr.pwd());
				} catch (Exception e) {
					err(e);
				}
		}

		public void dir(String[] inputs) {
			if (inputs.length == 1) {
				try {
					String[] fs = svr.dir();
					for (int i = 0; i < fs.length; i++) {
						msgln(fs[i]);
					}
				} catch (Exception e) {
					err(e);
				}
			}
		}

		public void pasv(String[] inputs) {
			if (inputs.length == 1) {
				try {
					makePassive(svr.pasv());
					msgln("PASV: Server in passive mode.");
				} catch (Exception e) {
					err(e);
				}
			}
		}

		public void port(String[] inputs) {
			if (inputs.length == 1) {
				try {
					InetSocketAddress s = makeActive();
					svr.port(s);
					msgln("PORT: Server in active mode.");
					msgln(dataChan.getLocalSocketAddress().toString());
				} catch (Exception e) {
					err(e);
				}
			}
		}

		public void ldir(String[] inputs) {
			if (inputs.length == 1) {
				String[] fs = new File(".").list();
				for (int i = 0; i < fs.length; i++) {
					msgln(fs[i]);
				}
			}
		}

	}

}
