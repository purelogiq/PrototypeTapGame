package com.purelogicapps.tapgame;

import com.purelogicapps.tapgame.Note.HoldState;
import com.purelogicapps.tapgame.Note.NoteType;


/**
 * The purpose of this model is to time the the beats' position with the current music
 * time and to score the user based on their input.
 */
public class PlayModel {
	public static final float MAX_HOLD_SLEEP_TIME = 0.5f;
	public static final int NUM_COLS = 4;
	public Note[][] cols = new Note[NUM_COLS][];
	public int[] colIndex = new int[NUM_COLS];

	// Timing information.
	BPMChange[] bpmChanges;
	Stop[] stops;
	float currentBeat;
	float offset;
	
	// Scoring information.
	float speed;
	boolean[] colsHeld = new boolean[NUM_COLS];
	
	public PlayModel(Note[][] cols,
			                 float[] bpmBeats, float[] bpmValues,
			                 float[] stopBeats, float[] stopValues,
			                 float offset, float speed){
		this.speed = speed;
		this.offset = offset;
		for(int i = 0; i < NUM_COLS; i++){
			this.cols[i] = cols[i];
			this.colIndex[i] = 0;
		}
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
	
	public void update(float songTime, float deltaTime, boolean[] colsHeld){
		// Update panel hold states.
		for(int i = 0; i < NUM_COLS; i++){
			this.colsHeld[i] = colsHeld[i];
		}
		updateCurrentBeat(songTime);
		updateNotes(deltaTime);
	}

	public float getCurrentBeat(){
		return this.currentBeat;
	}
	
	public void touchDown(int panel){ // 0 <= panel <= 3
		Note[] col = cols[panel];
		if(colIndex[panel] >= col.length) return;
		Note note = col[colIndex[panel]];
		// Check if it is in bounds to be scored.
		if((note.start - currentBeat) * speed < 1){
			if(note.type == NoteType.TAP){
				note.hidden = true;
			}else if(note.type == NoteType.HOLD){
				note.hit();
			}
			colIndex[panel] += 1;
		}
	}
	
	private void updateNotes(float deltaTime){
		// Find the next active note in each column, skipped notes are "missed".
		for(int i = 0; i < NUM_COLS; i++){
			Note[] col = cols[i];

			// Skip all notes that are "missed".
			while(colIndex[i] < col.length &&
				  (currentBeat - col[colIndex[i]].start) * speed > 1){
				Note note = col[colIndex[i]];
				if(note.type == NoteType.HOLD) note.holdState = HoldState.DEAD;
				colIndex[i] += 1;
				System.out.println("Missed in col " + i);
			}
			
			// Update holds.
			for(int j = 0; j < col.length; j++){
				Note note = col[j];
				if(note.type == NoteType.HOLD){
					note.updateHold(colsHeld[i], currentBeat, deltaTime, MAX_HOLD_SLEEP_TIME);
				}
			}
		}
	}
	
	private void updateCurrentBeat(float songTime){
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
