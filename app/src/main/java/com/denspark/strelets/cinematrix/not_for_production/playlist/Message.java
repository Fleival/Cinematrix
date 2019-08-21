package com.denspark.strelets.cinematrix.not_for_production.playlist;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Message{

	@SerializedName("savedMovies")
	private boolean savedMovies;

	@SerializedName("timeShift")
	private List<TimeShiftItem> timeShift;

	@SerializedName("translations")
	private Translations translations;

	@SerializedName("dailyViewed")
	private List<Object> dailyViewed;

	@SerializedName("links")
	private List<Object> links;

	public void setSavedMovies(boolean savedMovies){
		this.savedMovies = savedMovies;
	}

	public boolean isSavedMovies(){
		return savedMovies;
	}

	public void setTimeShift(List<TimeShiftItem> timeShift){
		this.timeShift = timeShift;
	}

	public List<TimeShiftItem> getTimeShift(){
		return timeShift;
	}

	public void setTranslations(Translations translations){
		this.translations = translations;
	}

	public Translations getTranslations(){
		return translations;
	}

	public void setDailyViewed(List<Object> dailyViewed){
		this.dailyViewed = dailyViewed;
	}

	public List<Object> getDailyViewed(){
		return dailyViewed;
	}

	public void setLinks(List<Object> links){
		this.links = links;
	}

	public List<Object> getLinks(){
		return links;
	}

	@Override
 	public String toString(){
		return 
			"Message{" + 
			"savedMovies = '" + savedMovies + '\'' + 
			",timeShift = '" + timeShift + '\'' + 
			",translations = '" + translations + '\'' + 
			",dailyViewed = '" + dailyViewed + '\'' + 
			",links = '" + links + '\'' + 
			"}";
		}
}