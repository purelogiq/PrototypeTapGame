package com.purelogicapps.tapgame;

public class Note {
	public static enum NoteType  { TAP, MINE, HOLD, ROLL }
	public static enum HoldState { INACTIVE, ALIVE, SLEEPING, DEAD }
	
	public NoteType type;
	/** 4, 8, 12, 16, 24, 32, 48, 64, 192 only. */
	public int fraction;
	/** Start position of the note in "beat space".
	 * You can use this as a "seed" for animation time. */
	public float start;
	/** End hold note position, also in beat space. */
	public float end;
	public HoldState holdState = HoldState.INACTIVE;
	public float sleepingTime;
	
	public Note(NoteType type, int fraction, float start, float end){
		this.type = type;
		this.fraction = fraction;
		this.start = start;
		this.end = end;
	}
	
	/** Transitions from the initial hold/roll tap into the hold/roll.
	 *  Also call this every time a roll is tapped. */
	public void hit(){
		if(holdState != HoldState.DEAD){
			holdState = HoldState.ALIVE;
			sleepingTime = 0;
		}
	}
	
	/** Call this every frame to update the hold's state. */
	public void updateHold(boolean isHeld, float deltaTime, float maxSleepTime){
		if(holdState == HoldState.INACTIVE || holdState == HoldState.DEAD) return;
		if(isHeld){
			holdState = HoldState.ALIVE;
			sleepingTime = 0;
		}else{
			holdState = HoldState.SLEEPING;
			sleepingTime += deltaTime;
			if(sleepingTime > maxSleepTime) holdState = HoldState.DEAD;
		}
	}
	
	/** Call this every frame to update the roll's state */
	public void updateRoll(float deltaTime, float maxDelayTime){
		if(holdState == HoldState.INACTIVE || holdState == HoldState.DEAD) return;
		sleepingTime += deltaTime;
		if(sleepingTime > maxDelayTime) holdState = HoldState.DEAD;
	}
	
}