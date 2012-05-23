package de.piramja.robotics.robotarm;

import java.util.ArrayList;

public class Movement
{
	private ArrayList<Byte> movement1;
	private ArrayList<Byte> movement2;
	
	public Movement()
	{
		this.movement1 = new ArrayList<Byte>();
		this.movement2 = new ArrayList<Byte>();
	}
	
	public Movement(ArrayList<Byte> movement1, ArrayList<Byte> movement2)
	{
		this.movement1 = movement1;
		this.movement2 = movement2;
	}
	
	public void addStep(byte move1, byte move2)
	{
		// cut out pause between movements
		//if (move1 == 0 && move2 == 0)
		//	return;
		
		this.movement1.add(move1);
		this.movement2.add(move2);
	}
	
	public byte getMovement1Step(int index)
	{
		return this.movement1.get(index);
	}
	
	public byte getMovement2Step(int index)
	{
		return this.movement2.get(index);
	}
	
	public int size()
	{
		return this.movement1.size();
	}
	
	public void clear()
	{
		this.movement1.clear();
		this.movement2.clear();
	}

	public ArrayList<Byte> getMovement1()
	{
		return movement1;
	}

	public void setMovement1(ArrayList<Byte> movement1)
	{
		this.movement1 = movement1;
	}

	public ArrayList<Byte> getMovement2()
	{
		return movement2;
	}

	public void setMovement2(ArrayList<Byte> movement2)
	{
		this.movement2 = movement2;
	}
}