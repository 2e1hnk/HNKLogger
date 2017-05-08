package uk.co.mattcarus.hnklogger.plugins;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import javax.lang.model.element.Element;


/**
 * This object contains a list of plugins, along with their priority and status
 * 
 * Priority is an integer, where a lower number represents higher priority
 * 
 * 0-10 are reserved for core functions
 * 100 is default
 * 
 * Multiple plugins can have the same priority, in which case there is no guarantee
 * of the order in which they are loaded/executed.
 * 
 * During initialisation and plugin execution, plugins are run in priority order,
 * starting with priority 0 plugins, then 1 etc...
 * 
 * During deinitialisation, the order is reversed so lowest priority plugins are unloaded
 * first.
 * 
 * @author macarus
 *
 */

public class PluginRegistry<T> implements Iterable<PluginDefinition>, Iterator<PluginDefinition> {
	
	private List<PluginDefinition> pluginList;
	private int count = 0;
	
	public PluginRegistry()
	{
		this.pluginList = new ArrayList<PluginDefinition>();
	}
	
	public void add(T pluginObject, int priority, boolean enabled)
	{
		PluginDefinition pluginDefinition = new PluginDefinition(pluginObject, priority, enabled);
		this.pluginList.add(pluginDefinition);
	}

	public void add(T pluginObject, int priority)
	{
		this.add(pluginObject, priority);
	}

	public void add(T pluginObject)
	{
		this.add(pluginObject);
	}

	@Override
	public boolean hasNext() {
		if ( this.count < this.pluginList.size() )
		{
			return true;
		}
		return false;
	}

	@Override
	public PluginDefinition next() {
		if ( this.count == this.pluginList.size() )
		{
			throw new NoSuchElementException();
		}
		this.count++;
		return this.pluginList.get(this.count - 1);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<PluginDefinition> iterator() {
		return this;
	}
	
	public void resetCounter()
	{
		this.count = 0;
	}
	
	public void run(String methodName)
	{
		this.run(methodName, false);
	}

	public void run(String methodName, boolean runIfPluginDisabled)
	{
		for ( PluginDefinition pluginDefinition : this.getSortedPluginDefinitions() )
		{
			if ( pluginDefinition.isEnabled() || runIfPluginDisabled )
			{
				Method runMethod;
				try {
					runMethod = pluginDefinition.getPluginObject().getClass().getMethod(methodName);
					runMethod.invoke(pluginDefinition.getPluginObject());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		this.resetCounter();
	}

	public void run(String methodName, Object data)
	{
		this.run(methodName, data, false);
	}

	public Object run(String methodName, Object data, boolean runIfPluginDisabled)
	{
		for ( PluginDefinition pluginDefinition : this.getSortedPluginDefinitions() )
		{
			if ( pluginDefinition.isEnabled() || runIfPluginDisabled )
			{
				Method runMethod;
				try {
					runMethod = pluginDefinition.getPluginObject().getClass().getMethod(methodName, data.getClass());
					runMethod.invoke(pluginDefinition.getPluginObject(), data);
				}
				catch (NoSuchMethodException e)
				{
					System.out.println("Hook " + pluginDefinition.getPluginObject().getClass().getName() + " does not implement method " + methodName + "(" + data.getClass() + ") and that's OK.");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		this.resetCounter();
		return data;
	}
	
	public String run(String methodName, String data)
	{
		return (String) this.run(methodName, (Object) data, false);
	}
	
	public Integer run(String methodName, Integer data)
	{
		return (Integer) this.run(methodName, (Object) data, false);
	}

	private PriorityQueue<PluginDefinition> getSortedPluginDefinitions() {
		Comparator<PluginDefinition> comp = new Comparator<PluginDefinition>() {
		    @Override
		    public int compare(PluginDefinition ela, PluginDefinition elb) {
		      return ela.getPriority().compareTo(elb.getPriority());
		    }
		};

		PriorityQueue<PluginDefinition> pq = new PriorityQueue<PluginDefinition>(256, comp);
		// If you had the access to the whole list, you wouldn't have to iterate, you could just pass it into the pq constructor
		while( this.hasNext() ) {
		    pq.add(this.next());
		}

		return pq;
	}

	private PriorityQueue<PluginDefinition> getReverseSortedPluginDefinitions() {
		Comparator<PluginDefinition> comp = new Comparator<PluginDefinition>() {
		    @Override
		    public int compare(PluginDefinition ela, PluginDefinition elb) {
		      return elb.getPriority().compareTo(ela.getPriority());
		    }
		};
		
		PriorityQueue<PluginDefinition> pq = new PriorityQueue<PluginDefinition>(256, comp);
		// If you had the access to the whole list, you wouldn't have to iterate, you could just pass it into the pq constructor
		while( this.hasNext() ) {
		    pq.add(this.next());
		}

		return pq;
	}
}
