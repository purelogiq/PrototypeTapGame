package com.purelogicapps.tapgame;


/*
 * 
 */
public class SinglePlayerModel {
	public Note[] col0;
	public Note[] col1;
	public Note[] col2;
	public Note[] col3;
	int note0; // Index of note eligible to receive input/be missed.
	int note1;
	int note2;
	int note3;
	
	BPMChange[] bpmChanges;
	Stop[] stops;
	float offset;
	
	// Current values
	float currentBeat;
	
	boolean col0Held = false;
	boolean col1Held = false;
	boolean col2Held = false;
	boolean col3Held = false;
	
	public SinglePlayerModel(Note[] col0, Note[] col1, Note[] col2, Note[] col3,
			                 float[] bpmBeats, float[] bpmValues,
			                 float[] stopBeats, float[] stopValues,
			                 float offset){
		this.offset = offset;
		this.col0 = col0;
		this.col1 = col1;
		this.col2 = col2;
		this.col3 = col3;
		this.note0 = 0;
		this.note1 = 0;
		this.note2 = 0;
		this.note3 = 0;
		this.bpmChanges = new BPMChange[bpmBeats.length];
		this.stops = new Stop[stopBeats.length];
		
		// Assuming bpmBeats and bpmValues are of same length (should be).
		for(int i = 0; i < bpmBeats.length; i++){
			BPMChange bpmChange = new BPMChange();
			bpmChange.beat = bpmBeats[i];
			bpmChange.bpm = bpmValues[i];
			if(bpmChange.beat < 0 || bpmChange.bpm < 0){
				throw new IllegalArgumentException("BPM changes cannot be negative.");
			}
			if(i == 0){
				if(bpmChange.beat != 0) {
					throw new IllegalArgumentException("First BPM change must have beat 0.");
				}
				bpmChange.startSongTime = 0; // Beat 0 starts at time 0.
			}
			this.bpmChanges[i] = bpmChange;
		}
		
		for(int i = 0; i < stopBeats.length; i++){
			Stop stop = new Stop();
			stop.beat = stopBeats[i];
			stop.stopTime = stopValues[i];
			if(stop.beat < 0 || stop.stopTime < 0){
				throw new IllegalArgumentException("Stops cannot be negative.");
			}
			this.stops[i] = stop;
		}
		
		loadBpmAndStopInfo();
	}
	
	public void update(float songTime,
			            boolean col0Held, boolean col1Held, 
			            boolean col2Held, boolean col3Held){
		// Update panel hold states.
		this.col0Held = col0Held;
		this.col1Held = col1Held;
		this.col2Held = col2Held;
		this.col3Held = col3Held;
		
		float time = songTime + offset;
		
		// Calculate current beat based on time, stops, and bpm.
		boolean stopped = false;
		// Check for stops first.
		for(int i = 0; i < stops.length; i++){
			Stop stop = stops[i];
			if(stop.startSongTime <= time && time <= stop.endSongTime){
				currentBeat = stop.beat;
				stopped = true;
				break;
			}
		}
		
		if(!stopped){
			// Find the bpmChange that is immediately before the time.
			BPMChange bpm = bpmChanges[0];
			for(int i = 1; i < bpmChanges.length; i++){
				if(bpmChanges[i].startSongTime <= time){
					bpm = bpmChanges[i];
				}
			}
			
			// Subtract all stops that have occurred between the bpm.startSongTime and the time.
			float stopSubtraction = 0;
			for(int i = 0; i < stops.length; i++){
				Stop stop = stops[i];
				if(bpm.beat <= stop.beat &&
					stop.endSongTime < time){
					stopSubtraction += stop.stopTime;
				}
			}
			
			time -= stopSubtraction;
			currentBeat = (time - bpm.startSongTime) / 60 * bpm.bpm + bpm.beat;
		}
	}
	
	public float getCurrentBeat(){
		return this.currentBeat;
	}
	
	public void touchDown(int panel){ // 0 <= panel <= 3
		
	}
	
	
	/**
	 * The goal of this method is to create BPMChange and Stop objects which
	 * can map any song time to its correct beat. To do this the objects must know
	 * the time in the song when their effect should occur.
	 */
	private void loadBpmAndStopInfo(){
		// Calculate the time (seconds) in the song's play back at which each BPM change occurs.
		// bpmChanges[0].startSongTime is always equal to 0.
		for(int i = 1; i < bpmChanges.length; i++){
			BPMChange bpm2 = bpmChanges[i];
			BPMChange bpm1 = bpmChanges[i - 1];
			bpm2.startSongTime = (bpm2.beat - bpm1.beat) / bpm1.bpm * 60 + bpm1.startSongTime;
		}
		
		// For each stop, add the stop time to each bpmChange's startSongTime so that those
		// times reflect the fact that there may be stops inbetween.
		for(int i = 0; i < stops.length; i++){
			Stop stop = stops[i];
			for(int j = 0; j < bpmChanges.length; j++){
				if(stop.beat < bpmChanges[j].beat){
					bpmChanges[j].startSongTime += stop.stopTime;
				}
			}
		}
		
		// Finally, for each stop calculate the startSongTime and endSongTime.
		for(int i = 0; i < stops.length; i++){
			Stop stop = stops[i];
			BPMChange bpm = bpmChanges[0];
			
			// Find the bpmChange immediately before the stop.
			for(int j = 1; j < bpmChanges.length; j++){
				if(bpmChanges[j].beat <= stop.beat){
					bpm = bpmChanges[j];
				}
			}
			
			stop.startSongTime = (stop.beat - bpm.beat) / bpm.bpm * 60 + bpm.startSongTime;
			
			// Add all stops between the bpm and this stop.
			for(int j = 0; j < i; j++){
				Stop inbetweenStop = stops[j];
				if(bpm.beat <= inbetweenStop.beat){
					stop.startSongTime += inbetweenStop.stopTime;
				}
			}
			
			stop.endSongTime = stop.startSongTime + stop.stopTime;
		}
		
		// Debug
		for(int i = 0; i < bpmChanges.length; i++){
			BPMChange b = bpmChanges[i];
			System.out.println("BPM Change " + i + ": Beat=" + b.beat + ", BPM=" + b.bpm 
					           + ", StartTime=" + b.startSongTime);
		}
		
		for(int i = 0; i < stops.length; i++){
			Stop b = stops[i];
			System.out.println("Stop " + i + ": Beat=" + b.beat + ", StopTime=" + b.stopTime 
					           + ", StartTime=" + b.startSongTime + ", EndTime=" + b.endSongTime);
		}
	}
	
	
	private class BPMChange{
		public float beat;
		public float bpm;
		public float startSongTime;
	}
	
	private class Stop{
		public float beat;
		public float stopTime;
		public float startSongTime;
		public float endSongTime;
	}
	
	
}
