package uk.co.mattcarus.hnklogger.plugins;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class HamLib extends Plugin {
	public String name = "HamLib";
	public static String identifier = "hamlib";	// Should be filename-friendly (i.e. no slashes etc)
	
	NativeHamLib hamlib;
	
	private static final Integer[] capabilities = { };
	
	public Integer[] getCapabilities() {
		return HamLib.capabilities;
	}
	
	public interface NativeHamLib extends Library {
        public String hamlib_version();
    }
	
    public void init()
    {
    	try {
	    	hamlib = (NativeHamLib) Native.loadLibrary("libhamlib-2", NativeHamLib.class);
	        System.out.println("Loaded HamLib version " + hamlib.hamlib_version());
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}
}
