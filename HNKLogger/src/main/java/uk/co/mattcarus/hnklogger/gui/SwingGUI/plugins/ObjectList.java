package uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ObjectList<T> implements Iterable<T>, Iterator<T> {

	private List<T> objects;
	private int count = 0;
	
	public ObjectList()
	{
		this.objects = new ArrayList<T>();
	}
	
	public List<T> getObjects()
	{
		return this.objects;
	}
	
	public void add(T newObject)
	{
		this.objects.add(newObject);
	}
	
	public void resetCounter()
	{
		this.count = 0;
	}
	
	@Override
	public Iterator<T> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		if ( this.count < this.objects.size() )
		{
			return true;
		}
		return false;
	}

	@Override
	public T next() {
		if ( this.count == this.objects.size() )
		{
			throw new NoSuchElementException();
		}
		this.count++;
		return this.objects.get(this.count - 1);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void run(String methodName)
	{
		for ( T object : this )
		{
			Method runMethod;
			try {
				runMethod = object.getClass().getMethod(methodName);
				runMethod.invoke(object);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		this.resetCounter();
	}

	public Object run(String methodName, Object data)
	{
		for ( T object : this )
		{
			Method runMethod;
			try {
				runMethod = object.getClass().getMethod(methodName, data.getClass());
				runMethod.invoke(object, data);
			}
			catch (NoSuchMethodException e)
			{
				System.out.println("Hook " + object.getClass().getName() + " does not implement method " + methodName + "(" + data.getClass() + ") and that's OK.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		this.resetCounter();
		return data;
	}
	
	public String run(String methodName, String data)
	{
		return (String) this.run(methodName, (Object) data);
	}
	
	public Integer run(String methodName, Integer data)
	{
		return (Integer) this.run(methodName, (Object) data);
	}

}
