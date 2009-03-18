package model;

import prefuse.data.tuple.TableTuple;


public class UsersGroup extends TableTuple{
	private int id;
	
	public UsersGroup(int id){	
		super();
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
}
