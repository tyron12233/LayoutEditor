package com.tyron.layouteditor.values;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import androidx.annotation.Nullable;

public class ObjectValue extends Value{
	
	private final HashMap<String, Value> members = new HashMap<>();
	
	@Override
	public ObjectValue copy() {
		ObjectValue result = new ObjectValue();
		for (Map.Entry<String, Value> entry : members.entrySet()) {
			result.add(entry.getKey(), entry.getValue().copy());
		}
		return result;
	}
	
	/**
* Adds a member, which is a name-value pair, to self. The name must be a String, but the value
* can be an arbitrary Value, thereby allowing you to inflate a full tree of Value
* rooted at this node.
*
* @param property name of the member.
* @param value    the member object.
*/
	public void add(String property, Value value) {
		if (value == null) {
			value = Null.INSTANCE;
		}
		members.put(property, value);
	}
	
	/**
* Removes the {@code property} from this {@link ObjectValue}.
*
* @param property name of the member that should be removed.
* @return the {@link Value} object that is being removed.
* @since 1.3
*/
	public Value remove(String property) {
		return members.remove(property);
	}
	
	/**
* Convenience method to add a primitive member. The specified value is converted to a
* Primitive of String.
*
* @param property name of the member.
* @param value    the string value associated with the member.
*/
	public void addProperty(String property, String value) {
		add(property, createValue(value));
	}
	
	/**
* Convenience method to add a primitive member. The specified value is converted to a
* Primitive of Number.
*
* @param property name of the member.
* @param value    the number value associated with the member.
*/
	public void addProperty(String property, Number value) {
		add(property, createValue(value));
	}
	
	/**
* Convenience method to add a boolean member. The specified value is converted to a
* Primitive of Boolean.
*
* @param property name of the member.
* @param value    the number value associated with the member.
*/
	public void addProperty(String property, Boolean value) {
		add(property, createValue(value));
	}
	
	/**
* Convenience method to add a char member. The specified value is converted to a
* Primitive of Character.
*
* @param property name of the member.
* @param value    the number value associated with the member.
*/
	public void addProperty(String property, Character value) {
		add(property, createValue(value));
	}
	
	/**
* Creates the proper {@link Value} object from the given {@code value} object.
*
* @param value the object to generate the {@link Value} for
* @return a {@link Value} if the {@code value} is not null, otherwise a {@link Null}
*/
	private Value createValue(java.lang.Object value) {
		return value == null ? Null.INSTANCE : new Primitive(value);
	}
	
	/**
* Returns a set of members of this object. The set is ordered, and the order is in which the
* values were added.
*
* @return a set of members of this object.
*/
	public Set<Map.Entry<String, Value>> entrySet() {
		return members.entrySet();
	}
	
	/**
* Returns the number of key/value pairs in the object.
*
* @return the number of key/value pairs in the object.
*/
	public int size() {
		return members.size();
	}
	
	/**
* Convenience method to check if a member with the specified name is present in this object.
*
* @param memberName name of the member that is being checked for presence.
* @return true if there is a member with the specified name, false otherwise.
*/
	public boolean has(String memberName) {
		return members.containsKey(memberName);
	}
	
	public boolean isPrimitive(String memberName) {
		return has(memberName) && get(memberName).isPrimitive();
	}
	
	public boolean isBoolean(String memberName) {
		if (has(memberName) && get(memberName).isPrimitive()) {
			return getAsPrimitive(memberName).isBoolean();
		}
		return false;
	}
	
	public boolean isNumber(String memberName) {
		if (has(memberName) && get(memberName).isPrimitive()) {
			return getAsPrimitive(memberName).isNumber();
		}
		return false;
	}
	
	public boolean isObject(String memberName) {
		return has(memberName) && get(memberName).isObject();
	}
	
	public boolean isNull(String memberName) {
		return has(memberName) && get(memberName).isNull();
	}
	
	public boolean isLayout(String memberName) {
		return has(memberName) && get(memberName).isLayout();
	}
	
	public Value get(String memberName) {
		return members.get(memberName);
	}
	
	/**
* Convenience method to get the specified member as a Value.
*
* @param memberName name of the member being requested.
* @return the Primitive corresponding to the specified member.
*/
	public Primitive getAsPrimitive(String memberName) {
		return (Primitive) members.get(memberName);
	}
	
	@Nullable
	public Boolean getAsBoolean(String memberName) {
		if (isBoolean(memberName)) {
			return getAsPrimitive(memberName).getAsBoolean();
		}
		return null;
	}
	
	public boolean getAsBoolean(String memberName, boolean defaultValue) {
		if (isBoolean(memberName)) {
			return getAsPrimitive(memberName).getAsBoolean();
		}
		return defaultValue;
	}
	
	@Nullable
	public Integer getAsInteger(String memberName) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsInt();
		}
		return null;
	}
	
	public int getAsInteger(String memberName, int defaultValue) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsInt();
		}
		return defaultValue;
	}
	
	@Nullable
	public Float getAsFloat(String memberName) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsFloat();
		}
		return null;
	}
	
	public float getAsFloat(String memberName, float defaultValue) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsFloat();
		}
		return defaultValue;
	}
	
	@Nullable
	public Double getAsDouble(String memberName) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsDouble();
		}
		return null;
	}
	
	public double getAsDouble(String memberName, double defaultValue) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsDouble();
		}
		return defaultValue;
	}
	
	@Nullable
	public Long getAsLong(String memberName) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsLong();
		}
		return null;
	}
	
	public long getAsLong(String memberName, long defaultValue) {
		if (isNumber(memberName)) {
			return getAsPrimitive(memberName).getAsLong();
		}
		return defaultValue;
	}
	
	@Nullable
	public String getAsString(String memberName) {
		if (isPrimitive(memberName)) {
			return getAsPrimitive(memberName).getAsString();
		}
		return null;
	}
	
	@Nullable
	public ObjectValue getAsObject(String memberName) {
		if (isObject(memberName)) {
			return (ObjectValue) members.get(memberName);
		}
		return null;
	}
	
	@Nullable
	public Layout getAsLayout(String memberName) {
		if (isLayout(memberName)) {
			return (Layout) members.get(memberName);
		}
		return null;
	}
	
	@Override
	public boolean equals(java.lang.Object o) {
		return (o == this) || (o instanceof ObjectValue && ((ObjectValue) o).members.equals(members));
	}
	
	@Override
	public int hashCode() {
		return members.hashCode();
	}
	
}