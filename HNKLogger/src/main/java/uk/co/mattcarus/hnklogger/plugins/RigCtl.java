package uk.co.mattcarus.hnklogger.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import uk.co.mattcarus.hnklogger.HNKLoggerProperties;
import uk.co.mattcarus.hnklogger.exceptions.HNKPropertyNotFoundException;

public class RigCtl extends Plugin implements Runnable {
	public String name = "RigCtl";
	public static String identifier = "rigctl";	// Should be filename-friendly (i.e. no slashes etc)
	
	private List<String> rigctl_command;			// This will contain the rigctl command
	
	private static final Integer[] capabilities = { };
	
	public Integer[] getCapabilities() {
		return RigCtl.capabilities;
	}
	
	public void init()
	{
		this.rigctl_command = new ArrayList<String>();
		
		System.out.println("Starting RigCtl Thread...");
		
		Thread rigCtlThread = new Thread(this);
	}

	public String getName()
	{
		return this.name;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public String onCallsignEntered(String callsign)
	{
		return callsign;
	}
	
	public SystemCommandExecutor runCommand(String command)
	{
		SystemCommandExecutor commandExecutor = null;
		
		try {
			commandExecutor = new SystemCommandExecutor(this.buildCommand(command));
			int result = commandExecutor.executeCommand();
			
			System.out.println("RESULT");
			System.out.println(result);
			
			// get the output from the command
			StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
	
			// print the output from the command
			System.out.println("RESULT");
			System.out.println(result);
			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return commandExecutor;
	}
	
	private List<String> buildCommand(List<String> command)
	{
		List<String> finalCommand = this.rigctl_command;
		finalCommand.addAll(command);
		
		System.out.println(finalCommand.toString());
		
		return finalCommand;
		
	}
	
	private List<String> buildCommand(String command)
	{
		List<String> finalCommand = new ArrayList<String>();
		for ( String thisString : command.split("/\\s/") )
		{
			finalCommand.add(thisString);
		}
		
		return this.buildCommand(finalCommand);
	}
	
	public void initProperties(HNKLoggerProperties properties)
	{
		try {
			this.rigctl_command.add(properties.getProperty(this.getIdentifier() + ".binary"));
			this.rigctl_command.add("--model=" + properties.getProperty(this.getIdentifier() + ".model"));
			this.rigctl_command.add("--rig-file=" + properties.getProperty(this.getIdentifier() + ".device"));
			this.rigctl_command.add("--serial-speed=" + properties.getProperty(this.getIdentifier() + ".serial_speed"));
			System.out.println(this.rigctl_command.toString());
		}
		catch (HNKPropertyNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public class SystemCommandExecutor
	{
	  private List<String> commandInformation;
	  private String adminPassword;
	  private ThreadedStreamHandler inputStreamHandler;
	  private ThreadedStreamHandler errorStreamHandler;
	  
	  /**
	   * Pass in the system command you want to run as a List of Strings, as shown here:
	   * 
	   * List<String> commands = new ArrayList<String>();
	   * commands.add("/sbin/ping");
	   * commands.add("-c");
	   * commands.add("5");
	   * commands.add("www.google.com");
	   * SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	   * commandExecutor.executeCommand();
	   * 
	   * Note: I've removed the other constructor that was here to support executing
	   *       the sudo command. I'll add that back in when I get the sudo command
	   *       working to the point where it won't hang when the given password is
	   *       wrong.
	   *
	   * @param commandInformation The command you want to run.
	   */
	  public SystemCommandExecutor(final List<String> commandInformation)
	  {
	    if (commandInformation==null) throw new NullPointerException("The commandInformation is required.");
	    this.commandInformation = commandInformation;
	    this.adminPassword = null;
	  }

	  @SuppressWarnings("finally")
	public int executeCommand()
	  throws IOException, InterruptedException
	  {
	    int exitValue = -99;

	    try
	    {
	      ProcessBuilder pb = new ProcessBuilder(commandInformation);
	      Process process = pb.start();

	      // you need this if you're going to write something to the command's input stream
	      // (such as when invoking the 'sudo' command, and it prompts you for a password).
	      OutputStream stdOutput = process.getOutputStream();
	      
	      // i'm currently doing these on a separate line here in case i need to set them to null
	      // to get the threads to stop.
	      // see http://java.sun.com/j2se/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
	      InputStream inputStream = process.getInputStream();
	      InputStream errorStream = process.getErrorStream();

	      // these need to run as java threads to get the standard output and error from the command.
	      // the inputstream handler gets a reference to our stdOutput in case we need to write
	      // something to it, such as with the sudo command
	      inputStreamHandler = new ThreadedStreamHandler(inputStream, stdOutput, adminPassword);
	      errorStreamHandler = new ThreadedStreamHandler(errorStream);

	      // TODO the inputStreamHandler has a nasty side-effect of hanging if the given password is wrong; fix it
	      inputStreamHandler.start();
	      errorStreamHandler.start();

	      // TODO a better way to do this?
	      exitValue = process.waitFor();
	 
	      // TODO a better way to do this?
	      inputStreamHandler.interrupt();
	      errorStreamHandler.interrupt();
	      inputStreamHandler.join();
	      errorStreamHandler.join();
	    }
	    catch (IOException e)
	    {
	      // TODO deal with this here, or just throw it?
	      throw e;
	    }
	    catch (InterruptedException e)
	    {
	      // generated by process.waitFor() call
	      // TODO deal with this here, or just throw it?
	      throw e;
	    }
	    finally
	    {
	      return exitValue;
	    }
	  }

	  /**
	   * Get the standard output (stdout) from the command you just exec'd.
	   */
	  public StringBuilder getStandardOutputFromCommand()
	  {
	    return inputStreamHandler.getOutputBuffer();
	  }

	  /**
	   * Get the standard error (stderr) from the command you just exec'd.
	   */
	  public StringBuilder getStandardErrorFromCommand()
	  {
	    return errorStreamHandler.getOutputBuffer();
	  }
	}
	
	class ThreadedStreamHandler extends Thread
	{
	  InputStream inputStream;
	  String adminPassword;
	  OutputStream outputStream;
	  PrintWriter printWriter;
	  StringBuilder outputBuffer = new StringBuilder();
	  private boolean sudoIsRequested = false;
	  
	  /**
	   * A simple constructor for when the sudo command is not necessary.
	   * This constructor will just run the command you provide, without
	   * running sudo before the command, and without expecting a password.
	   * 
	   * @param inputStream
	   * @param streamType
	   */
	  ThreadedStreamHandler(InputStream inputStream)
	  {
	    this.inputStream = inputStream;
	  }

	  /**
	   * Use this constructor when you want to invoke the 'sudo' command.
	   * The outputStream must not be null. If it is, you'll regret it. :)
	   * 
	   * TODO this currently hangs if the admin password given for the sudo command is wrong.
	   * 
	   * @param inputStream
	   * @param streamType
	   * @param outputStream
	   * @param adminPassword
	   */
	  ThreadedStreamHandler(InputStream inputStream, OutputStream outputStream, String adminPassword)
	  {
	    this.inputStream = inputStream;
	    this.outputStream = outputStream;
	    this.printWriter = new PrintWriter(outputStream);
	    this.adminPassword = adminPassword;
	    this.sudoIsRequested = true;
	  }
	  
	  public void run()
	  {
	    // on mac os x 10.5.x, when i run a 'sudo' command, i need to write
	    // the admin password out immediately; that's why this code is
	    // here.
	    if (sudoIsRequested)
	    {
	      //doSleep(500);
	      printWriter.println(adminPassword);
	      printWriter.flush();
	    }

	    BufferedReader bufferedReader = null;
	    try
	    {
	      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	      String line = null;
	      while ((line = bufferedReader.readLine()) != null)
	      {
	        outputBuffer.append(line + "\n");
	      }
	    }
	    catch (IOException ioe)
	    {
	      // TODO handle this better
	      ioe.printStackTrace();
	    }
	    catch (Throwable t)
	    {
	      // TODO handle this better
	      t.printStackTrace();
	    }
	    finally
	    {
	      try
	      {
	        bufferedReader.close();
	      }
	      catch (IOException e)
	      {
	        // ignore this one
	      }
	    }
	  }
	  
	  private void doSleep(long millis)
	  {
	    try
	    {
	      Thread.sleep(millis);
	    }
	    catch (InterruptedException e)
	    {
	      // ignore
	    }
	  }
	  
	  public StringBuilder getOutputBuffer()
	  {
	    return outputBuffer;
	  }
	}

	@Override
	public void run() {
		System.out.println("I'm in the thread!");
		
		this.runCommand("get_freq");
		this.runCommand("get_mode");
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
