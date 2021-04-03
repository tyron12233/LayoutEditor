package com.tyron.layouteditor.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Choice implements Parcelable{
	
	public final static Parcelable.Creator<Choice> CREATOR = new Creator<Choice>() {
		@Override
		public Choice createFromParcel(Parcel source) {
			return new Choice(source);
		}
		
		@Override
		public Choice[] newArray(int size) {
			return new Choice[size];
		}
	};
	
	public List<Item> data;
	
	public boolean singleChoice = false;
	
	public Choice(List<Item> data, boolean singleChoice){
		this.data = data;
		this.singleChoice = singleChoice;
	}
	
	public Choice(Parcel parcel){
		data = new ArrayList<>();
	    parcel.readTypedList(data, Item.CREATOR);
		this.singleChoice = parcel.readBoolean();
	}
	
	public static Choice createBoolean(){
		return new Choice(Arrays.asList(new Item(0, "true", "true"), new Item(0, "false", "false")), true);
	}
	
	public static Choice createLayoutDimension(){
	    return new Choice(Arrays.asList(new Item(0, "match parent", "match_parent"), new Item(0, "wrap content", "wrap_content"), new Item(1, "Custom", "0dp")), true);
    }
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(data);
		dest.writeBoolean(singleChoice);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static class Item implements Parcelable {
		
		public final static Parcelable.Creator<Item> CREATOR = new Creator<Item>() {
			@Override
			public Item createFromParcel(Parcel source) {
				return new Item(source);
			}
			
			@Override
			public Item[] newArray(int size) {
				return new Item[size];
			}
		};
		
		public static final int TYPE_NORMAL = 0;
		public static final int TYPE_EDITTEXT = 1;
		
		public int type;
		
		public String name;
		
		public String value;
		
		public Item(int type, String name, String value){
			this.type = type;
			this.name = name;
			this.value = value;
		}
		
		public Item(Parcel parcel){
			this.type = parcel.readInt();
			this.name = parcel.readString();
			this.value = parcel.readString();
		}
		
		@Override
		public boolean equals(Object obj){
			
			if(!(obj instanceof Item)){
				return false;
			}
			
			Item item = (Item) obj;
			
			return (this.type == item.type && this.name.equals(item.name));
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags){
			dest.writeInt(type);
			dest.writeString(name);
			dest.writeString(value);
	    }
		
		@Override
		public int describeContents() {
			return 0;
		}
		
	}
}
