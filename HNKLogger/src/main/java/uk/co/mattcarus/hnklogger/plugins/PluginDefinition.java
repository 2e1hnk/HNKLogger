package uk.co.mattcarus.hnklogger.plugins;

public class PluginDefinition<Plugin> implements Comparable<PluginDefinition> {
	private Plugin pluginObject;
	private Integer priority;
	private Boolean enabled;
	
	public PluginDefinition() {}
	
	public PluginDefinition(Plugin pluginObject, int priority, boolean enabled)
	{
		this.setPluginObject(pluginObject);
		this.setPriority(priority);
		this.setEnabled(enabled);
	}
	
	public PluginDefinition(Plugin pluginObject, int priority)
	{
		this(pluginObject, priority, false);
	}
	
	public PluginDefinition(Plugin pluginObject)
	{
		this(pluginObject, 100, false);
	}
	
	public Plugin getPluginObject() {
		return pluginObject;
	}

	public void setPluginObject(Plugin pluginObject) {
		this.pluginObject = pluginObject;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int compareTo(PluginDefinition o) {
		return this.getPriority().compareTo(o.getPriority());
	}
	
}
