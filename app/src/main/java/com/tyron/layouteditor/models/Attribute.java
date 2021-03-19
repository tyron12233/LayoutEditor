package com.tyron.layouteditor.models;

import com.tyron.layouteditor.values.Value;

public class Attribute {
	
	public String key;
	public Value value;
	public String id;

	public Attribute(String id, String key, Value value){
		this.id = id;
		this.key = key;
		this.value = value;
	}

	public Attribute(String key, Value value){
		this.id = "android";
		this.key = key;
		this.value = value;
	}

	public Attribute copy() {
			return new Attribute(id, key, value.copy());
	}
}
