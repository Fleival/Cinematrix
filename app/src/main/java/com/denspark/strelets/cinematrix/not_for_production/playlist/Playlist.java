package com.denspark.strelets.cinematrix.not_for_production.playlist;

import com.google.gson.annotations.SerializedName;

public class Playlist {

	@SerializedName("field")
	private String field;

	@SerializedName("message")
	private Message message;

	@SerializedName("type")
	private String type;

	public void setField(String field){
		this.field = field;
	}

	public String getField(){
		return field;
	}

	public void setMessage(Message message){
		this.message = message;
	}

	public Message getMessage(){
		return message;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"Playlist{" +
			"field = '" + field + '\'' + 
			",message = '" + message + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}