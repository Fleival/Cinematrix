package com.denspark.strelets.cinematrix.not_for_production.playlist;

import com.google.gson.annotations.SerializedName;

public class TimeShiftItem{

	@SerializedName("post_id")
	private String postId;

	@SerializedName("translation")
	private String translation;

	@SerializedName("season")
	private String season;

	@SerializedName("episode")
	private String episode;

	@SerializedName("id")
	private String id;

	@SerializedName("time")
	private String time;

	public void setPostId(String postId){
		this.postId = postId;
	}

	public String getPostId(){
		return postId;
	}

	public void setTranslation(String translation){
		this.translation = translation;
	}

	public String getTranslation(){
		return translation;
	}

	public void setSeason(String season){
		this.season = season;
	}

	public String getSeason(){
		return season;
	}

	public void setEpisode(String episode){
		this.episode = episode;
	}

	public String getEpisode(){
		return episode;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setTime(String time){
		this.time = time;
	}

	public String getTime(){
		return time;
	}

	@Override
 	public String toString(){
		return 
			"TimeShiftItem{" + 
			"post_id = '" + postId + '\'' + 
			",translation = '" + translation + '\'' + 
			",season = '" + season + '\'' + 
			",episode = '" + episode + '\'' + 
			",id = '" + id + '\'' + 
			",time = '" + time + '\'' + 
			"}";
		}
}