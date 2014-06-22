package com.purelogicapps.tapgame;

import java.util.ArrayList;

import com.purelogicapps.tapgame.Note.NoteType;
import com.purelogicapps.tapgame.SMFile.Notes;
import com.purelogicapps.tapgame.SMFile.SMPair;

public class Session {
	public static final char NO_NOTE = '0';
	public static final char TAP = '1';
	public static final char HOLD_START = '2';
	public static final char ROLL_START = '4';
	public static final char HOLD_END = '3';
	public static final char MINE = 'M';
	public static final char LIFT = 'L';
	public static final char FAKE = 'F';
	
	public SMFile smfile;
	public int notesIndex;
	
	// Options.
	public int speed = 1;
//	public boolean noHolds = false;
//	public boolean noDoubles = false;
	
	public SinglePlayerModel loadModel(){
		ArrayList<Note> col0 = new ArrayList<Note>();
		ArrayList<Note> col1 = new ArrayList<Note>();
		ArrayList<Note> col2 = new ArrayList<Note>();
		ArrayList<Note> col3 = new ArrayList<Note>();
		
		Notes notes = smfile.notes.get(notesIndex);
		ArrayList<ArrayList<char[]>> measures = notes.measures;
		
		for(int i = 0; i < measures.size(); i++){
			ArrayList<char[]> measure = measures.get(i);
			
			for(int j = 0; j < measure.size(); j++){
				// 4 beats = 1 measure
				float start = (i + (float)j / measure.size()) * 4;
				int wholeFraction = getFraction(j, measure.size());
				char[] line = measure.get(j);
				
				for(int k = 0; k < 4; k++){
					Note note = null;
					if(line[k] == TAP){
						note = new Note(NoteType.TAP, wholeFraction, start, start);						
					}else if(line[k] == HOLD_START){
						float end = findHoldEnd(measures, i, j, k);
						note = new Note(NoteType.HOLD, wholeFraction, start, end);
					}else if(line[k] == ROLL_START){
						float end = findHoldEnd(measures, i, j, k);
						note = new Note(NoteType.ROLL, wholeFraction, start, end);
					}else if(line[k] == MINE){
						note = new Note(NoteType.MINE, wholeFraction, start, start);						
					}else if(line[k] == LIFT){
						note = new Note(NoteType.TAP, wholeFraction, start, start);						
					}
					
					if(note != null){
						if(k == 0) col0.add(note);
						if(k == 1) col1.add(note);
						if(k == 2) col2.add(note);
						if(k == 3) col3.add(note);
					}
				}
			}
		}
		
		Note[] col0arr = new Note[col0.size()];
		Note[] col1arr = new Note[col1.size()];
		Note[] col2arr = new Note[col2.size()];
		Note[] col3arr = new Note[col3.size()];
		col0.toArray(col0arr);
		col1.toArray(col1arr);
		col2.toArray(col2arr);
		col3.toArray(col3arr);
		
		ArrayList<SMPair> bpms = smfile.bpms;
		float[] bpmBeats = new float[bpms.size()];
		float[] bpmValues = new float[bpms.size()];
		for(int i = 0; i < bpms.size(); i++){
			bpmBeats[i] = bpms.get(i).beat;
			bpmValues[i] = bpms.get(i).value;
		}
		
		ArrayList<SMPair> stops = smfile.stops;
		float[] stopBeats = new float[stops.size()];
		float[] stopValues = new float[stops.size()];
		for(int i = 0; i < stops.size(); i++){
			stopBeats[i] = stops.get(i).beat;
			stopValues[i] = stops.get(i).value;
		}
		
		float offset = smfile.offset;
		
		return new SinglePlayerModel(col0arr, col1arr, col2arr, col3arr,
				                     bpmBeats, bpmValues, stopBeats, stopValues, offset);
	}
	
	private static float findHoldEnd(ArrayList<ArrayList<char[]>> measures, 
			                   int startMeasure, int startLine, int col){
		for(int i = startMeasure; i < measures.size(); i++){
			ArrayList<char[]> measure = measures.get(i);
			int j = (i == startMeasure) ? startLine : 0;
			for(; j < measure.size(); j++){
				char[] line = measure.get(j);
				if(line[col] == HOLD_END) return (i + (float)j / measure.size()) * 4;
			}
		}
		throw new IllegalArgumentException("SMFile has holds that do not end.");
	}
	
	private static int getFraction(int lineIndex, int lineCount){
		int fraction = lineIndex * 192 / lineCount;
		if (fraction % (192/4) == 0)		return 4;
		else if (fraction % (192/8) == 0)   return 8;
		else if (fraction % (192/12) == 0)  return 12;
		else if (fraction % (192/16) == 0)  return 16;
		else if (fraction % (192/24) == 0)  return 24;
		else if (fraction % (192/32) == 0)  return 32;
		else if (fraction % (192/48) == 0)  return 48;
		else if (fraction % (192/64) == 0)  return 64;
		else if (fraction % (192/192) == 0)	return 192;
	    else throw new IllegalArgumentException("Invalid fraction.");
	}
}
