package de.fhe.fhemobile.events;

public interface Event {
	
	public String getType();
	public Object getSource();
	public void setSource(Object source);
}
