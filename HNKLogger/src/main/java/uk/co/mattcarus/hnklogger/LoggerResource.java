package uk.co.mattcarus.hnklogger;

import java.util.HashMap;

public interface LoggerResource {
	public HashMap<String, String> search(String searchTerm);
	public void load(String resourceLocation);
}
