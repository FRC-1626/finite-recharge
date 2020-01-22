package frc.robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.wpilibj.RobotController;

public class DriverInput
{
	private long timeOffset;
	private static long recordStart;
	private Map<String, Object> inputs;
	private static List<String> inputNames=new ArrayList<String>();

	public static void nameInput(String name)
	{
		inputNames.add(name);
	}

	public String toString()
	{
		StringBuffer str=new StringBuffer();
		str.append(timeOffset);
		for (String name: inputNames)
		{
			str.append(';');
			Object tmp = inputs.get(name);
			if (tmp != null) {
				str.append(inputs.get(name).toString());
			} else {
				str.append("null");
			}
		}
		return str.toString();
	}

	public DriverInput() {
		inputs = new HashMap<String, Object>();
		long FPGAtime=RobotController.getFPGATime();
		timeOffset=FPGAtime-recordStart;
	}

	public DriverInput withInput(String name, Object obj) {

		if (inputNames.contains(name)) {
			inputs.put(name,  obj);
		}
		return this;
	}

	public DriverInput withInput(String name, double axis) {
		return withInput(name, new Double(axis));
	}

	public DriverInput withInput(String name, boolean button) {

		return withInput(name,  new Boolean(button));
	}

	public DriverInput(Object... in)
	{
		inputs=new HashMap<String, Object>();

		int nameIdx=0;

		for (Object obj : in)
		{
			inputs.put(inputNames.get(nameIdx++), obj);
		}
		long FPGAtime=RobotController.getFPGATime();
		timeOffset=FPGAtime-recordStart;
		//		System.out.println("Driver input offset is " + timeOffset + " = " + FPGAtime + " - " + recordStart);
	}

	public Object getInput(String name)
	{
		return inputs.get(name);
	}

	public Object getInput(int idx)
	{
		String name = inputNames.get(idx);
		return inputs.get(name);
	}
	
	public boolean getButton(String name) {
		Object obj = inputs.get(name);
		if (obj instanceof Boolean) { 
			return (boolean)obj;
		} else {
			return false;
		}
	}
	
	public double getAxis(String name) {
		Object obj = inputs.get(name);
		if (obj instanceof Double) { 
			return (double)obj;
		} else {
			return 0.0;
		}
	}

	public long getTimeOffset()
	{
		return timeOffset;
	}

	public DriverInput setTimeOffset(long to)
	{
		timeOffset=to;
		return this;
	}

	public static void setRecordTime()
	{
		recordStart=RobotController.getFPGATime();
		System.out.println("recordStart is " + recordStart);
	}
}
