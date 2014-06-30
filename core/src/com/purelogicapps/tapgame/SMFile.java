package com.purelogicapps.tapgame;

import java.util.ArrayList;
import java.util.Scanner;

import com.purelogicapps.tapgame.SMFile.Notes.DifficultyClass;
import com.purelogicapps.tapgame.SMFile.Notes.NotesType;

public class SMFile {
	public static class SMPair{ 
		public float beat, value;
		public SMPair(float beat, float value){ this.beat = beat; this.value = value; }
		public String toString(){ return beat + "=" + value;}
	}
	
	//========== SM file structure ==============
	public static class Notes{
		public static enum NotesType{
			SINGLE ("dance-single"), 
			DOUBLE ("dance-double"),
			COUPLE ("dance-couple"),
			SOLO ("dance-solo");
			
			public String name;
			private NotesType(String name){
				this.name= name;
			}
		}
		
		public static enum DifficultyClass{
			BEGINNER ("beginner"),
			EASY ("easy"),
			MEDIUM ("medium"),
			HARD ("hard"),
			CHALLENGE ("challenge"),
			EDIT ("edit");
			
			public String name;
			private DifficultyClass(String name){
				this.name = name;
			}
		}
		public NotesType notestype;
		public String description = "";
		public DifficultyClass difficultyclass;
		public int difficultymeter = 0;
		public ArrayList<ArrayList<char[]>> measures;
		
		public Notes(){
			measures = new ArrayList<ArrayList<char[]>>();
		}
		
		public static int lineSize(NotesType type){
			if(type == NotesType.DOUBLE || type == NotesType.COUPLE) return 8;
			else if(type == NotesType.SOLO) return 6;
			else return 4;
		}
		
		@Override
		public String toString(){
			String result = "\n#NOTES:\n";
			result += notestype.name + ":\n";
			result += description + ":\n";
			result += difficultyclass.name + ":\n";
			result += difficultymeter + ":\n";
			result += "0.500,0.500,0.500,0.500,0.500:\n"; // Fill in radar with defaults.
			result += "\n"; // Give a little space.
			for(int i = 0; i < measures.size(); i++){
				ArrayList<char[]> measure = measures.get(i);
				for(char[] line : measure){
					for(char note : line){
						result += note;
					}
					result += "\n";
				}
				if(i < measures.size() - 1) result += ",\n";
			}
			
			result += ";";
			return result;
		}
	}
	
	// String values are UTF-8
	public String filepath = "";
	public String filename = "";
	public String title = "";
	public String subtitle = "";
	public String artist = "";
	public String titletranslit = "";
	public String subtitletranslit = "";
	public String artisttranslit = "";
	public String credit = "";
	public String banner = "";
	public String background = "";
	public String cdtitle = "";
	public String music = "";
	public float offset = 0;
	public float samplestart = 0;
	public float samplelength = 0;
	public ArrayList<SMPair> bpms;
	public ArrayList<SMPair> stops;
	public ArrayList<Notes> notes;
	
	public SMFile(){
		bpms = new ArrayList<SMPair>();
		stops = new ArrayList<SMPair>();
		notes = new ArrayList<SMFile.Notes>();
	}
	
	@Override
	public String toString(){
		String result = "";
		result += "//File path:" + filepath + ";\n";
		result += "//File name:" + filename + ";\n";
		result += "#TITLE:" + title + ";\n";
		result += "#SUBTITLE:" + subtitle + ";\n";
		result += "#ARTIST:" + artist + ";\n";
		result += "#TITLETRANSLIT:" + titletranslit + ";\n";
		result += "#SUBTITLETRANSLIT:" + subtitletranslit + ";\n";
		result += "#ARTISTTRANSLIT:" + artisttranslit + ";\n";
		result += "#CREDIT:" + credit + ";\n";
		result += "#BANNER:" + banner + ";\n";
		result += "#BACKGROUND:" + background + ";\n";
		result += "#CDTITLE:" + cdtitle + ";\n";
		result += "#MUSIC:" + music + ";\n";
		result += "#OFFSET:" + offset + ";\n";
		result += "#SAMPLESTART:" + samplestart + ";\n";
		result += "#SAMPLELENGTH:" + samplelength + ";\n";
		result += "#SELECTABLE:YES;\n";
		
		result += "#BPMS:";
		for(int i = 0; i < bpms.size(); i++){
			SMPair pair = bpms.get(i);
			result += pair.toString();
			if(i < bpms.size() - 1) result += ",";
		}
		result += ";\n";
		
		result += "#STOPS:";
		for(int i = 0; i < stops.size(); i++){
			SMPair pair = stops.get(i);
			result += pair.toString();
			if(i < stops.size() - 1) result += ",";
		}
		result += ";\n";
		
		for(Notes note : notes){ result += note.toString() + "\n"; }
		
		return result;
	}
	
	//========= Parser ==========================
	public static SMFile parseSMFile(String filetext){
		SMFile smfile = new SMFile();
		
		Scanner smScanner = new Scanner(filetext);
		smScanner.useDelimiter(";");
		String buffer = "";
		while (smScanner.hasNext()) {
			buffer = smScanner.next().trim();
			if (buffer.contains("#")) { // Info tag
				// Ignore comments and the byte order mark (xEF BB BF)
				if (buffer.charAt(0) != '#') {
					buffer = buffer.substring(buffer.indexOf('#'));
				}

				// Start filling in the info...
				if (buffer.contains("#TITLE:")) {
					smfile.title = stripSM(buffer);
				}
				else if (buffer.contains("#SUBTITLE:")) {
					smfile.subtitle = stripSM(buffer);
				}
				else if (buffer.contains("#ARTIST:")) {
					smfile.artist = stripSM(buffer);
				} 
				else if (buffer.contains("#TITLETRANSLIT:")) {
					smfile.titletranslit = stripSM(buffer);
				} 
				else if (buffer.contains("#SUBTITLETRANSLIT:")) {
					smfile.subtitletranslit = stripSM(buffer);
				} 
				else if (buffer.contains("#ARTISTTRANSLIT:")) {
					smfile.artisttranslit = stripSM(buffer);
				} 
				else if (buffer.contains("#CREDIT:")) {
					smfile.credit = stripSM(buffer);
				} 
				else if (buffer.contains("#BANNER:")) {
					smfile.banner = stripSM(buffer);
				} 
				else if (buffer.contains("#BACKGROUND:")) {
					smfile.background = stripSM(buffer);
				} 
				else if (buffer.contains("#CDTITLE:")) {
					smfile.cdtitle = stripSM(buffer);
				} 
				else if (buffer.contains("#MUSIC:")) {
					smfile.music = stripSM(buffer);
				} 
				else if (buffer.contains("#OFFSET:")) {
					smfile.offset = Float.parseFloat(stripSM(buffer));
				} 
				else if (buffer.contains("#SAMPLESTART:")) {
					smfile.samplestart = Float.parseFloat(stripSM(buffer));
				} 
				else if (buffer.contains("#SAMPLELENGTH:")) {
					smfile.samplelength = Float.parseFloat(stripSM(buffer));
				} 
				else if (buffer.contains("#SELECTABLE:")) {
					// Ignore.
				} 
				else if (buffer.contains("#BPMS:")) {
					parseBPM(smfile, stripSM(buffer));
				} 
				else if (buffer.contains("#DISPLAYBPM:")) {
					// Ignore
				} 
				else if (buffer.contains("#STOPS:")) {
					parseStop(smfile, stripSM(buffer));
				} 
				else if (buffer.contains("#BGCHANGES:")) {
					// Ignore.
				} 
				else if (buffer.contains("#NOTES:")) { // This will occur for every #NOTES tag.
					parseNotes(smfile, stripSM(buffer));
				} 
				else {
					// Unsupported tag outside of SM 3.9's specification?
				}
			}
		}
		smScanner.close();
		
		return smfile;
	}
	
	private static void parseBPM(SMFile smfile, String buffer){
		Scanner bpmStringBuffer = new Scanner(buffer);
		bpmStringBuffer.useDelimiter(",");
		while (bpmStringBuffer.hasNext()) {
			String pair = bpmStringBuffer.next().trim();
			try {
				if (pair.indexOf('=') < 0) {
					throw new Exception("No '=' found");
				} else {
					float beat = Float.parseFloat(pair.substring(0, pair.indexOf('=')));
					float value = Float.parseFloat(pair.substring(pair.indexOf('=') + 1));
					smfile.bpms.add(new SMPair(beat, value));
				}
			} catch (Exception e) { // Also catch NumberFormatExceptions
				bpmStringBuffer.close();
				throw new IllegalArgumentException(
						"Improperly formatted #BPMS pair \"" + pair + "\": " + e.getMessage(), e);
			}
		}
		bpmStringBuffer.close();
	}
	
	private static void parseStop(SMFile smfile, String buffer) {
		Scanner stopStringBuffer = new Scanner(buffer);
		stopStringBuffer.useDelimiter(",");
		while (stopStringBuffer.hasNext()) {
			String pair = stopStringBuffer.next().trim();
			try {
				if (pair.indexOf('=') < 0) {
					throw new Exception("No '=' found");
				} else {
					float beat = Float.parseFloat(pair.substring(0, pair.indexOf('=')));
					float value = Float.parseFloat(pair.substring(pair.indexOf('=') + 1));
					smfile.stops.add(new SMPair(beat, value));
				}
			} catch (Exception e) { // Also catch NumberFormatExceptions
				stopStringBuffer.close();
				throw new IllegalArgumentException(
						"Improperly formatted #STOPS pair \"" + pair + "\": " + e.getMessage(), e);
			}
		}
		stopStringBuffer.close();
	}

	private static void parseNotes(SMFile smfile, String buffer) {
		// Expected format:
		// #NOTES:
		// <NotesType>:
		// <Description>:
		// <DifficultyClass>:
		// <DifficultyMeter>:
		// <RadarValues>:
		// <NoteData>;
		
		Notes notes = new Notes();
		Scanner notesScanner = new Scanner(buffer);
		notesScanner.useDelimiter(":");
		String notesBuffer = "";
		
		try {
			// Notes Type
			notesBuffer = notesScanner.next().trim();
			if(notesBuffer.equalsIgnoreCase("dance-single")){
				notes.notestype = NotesType.SINGLE;
			}
			else if(notesBuffer.equalsIgnoreCase("dance-double")){
				notes.notestype = NotesType.DOUBLE;
			}
			else if(notesBuffer.equalsIgnoreCase("dance-couple")){
				notes.notestype = NotesType.COUPLE;
			}
			else if(notesBuffer.equalsIgnoreCase("dance-solo")){
				notes.notestype = NotesType.SOLO;
			}
			else{
				notesScanner.close(); // Do not add a new notes object to the smfile.
				return;
			}
			
			// Description
			notesBuffer = notesScanner.next().trim();
			notes.description = notesBuffer;
			
			// Difficulty
			notesBuffer = notesScanner.next().trim();
			if(notesBuffer.equalsIgnoreCase("beginner")){
				notes.difficultyclass = DifficultyClass.BEGINNER;
			}
			else if(notesBuffer.equalsIgnoreCase("easy")){
				notes.difficultyclass = DifficultyClass.EASY;
			}
			else if(notesBuffer.equalsIgnoreCase("medium")){
				notes.difficultyclass = DifficultyClass.MEDIUM;
			}
			else if(notesBuffer.equalsIgnoreCase("hard")){
				notes.difficultyclass = DifficultyClass.HARD;
			}
			else if(notesBuffer.equalsIgnoreCase("challenge")){
				notes.difficultyclass = DifficultyClass.CHALLENGE;
			}
			else{
				notes.difficultyclass = DifficultyClass.EDIT;
			}
			
			// Skip difficulty Meter
			notesBuffer = notesScanner.next().trim();
			if (notesBuffer.length() > 0) {
				notes.difficultymeter = Integer.parseInt(notesBuffer);
			}

			// Skip Radar Values
			notesBuffer = notesScanner.next().trim();
			
			// Notes Data
			notesBuffer = notesScanner.next().trim();
			parseNotesData(notes, notesBuffer);
			
			smfile.notes.add(notes);
			notesScanner.close();
		} catch (Exception e) {
			notesScanner.close();
			throw new IllegalArgumentException(
					"Improperly formatted #NOTES data: " + e.getMessage(), e);
		}
	}
	
	private static void parseNotesData(Notes notes, String buffer) {
		Scanner notesDataScanner = new Scanner(buffer);
		notesDataScanner.useDelimiter(",");
		String lineString = "";
		String measureString = "";
		int lineSize = Notes.lineSize(notes.notestype);
		while (notesDataScanner.hasNext()) {
			ArrayList<char[]> measure = new ArrayList<char[]>();
			measureString = notesDataScanner.next().trim();
			
			// Assume that stepfile makers separate lines within a measure by page breaks.
			Scanner measureScanner = new Scanner(measureString);
			while (measureScanner.hasNextLine()) {
				lineString = measureScanner.nextLine().trim();
				if (lineString.charAt(0) == '/') { // comment
					continue;
				}
				
				if (lineString.length() != lineSize) {
					measureScanner.close();
					notesDataScanner.close();
					throw new IllegalArgumentException("line length " + lineString.length() +
							" does not match note type " + lineSize);
				}
				
				char[] line = new char[lineSize];
				for(int i = 0; i < lineSize; i++){
					line[i] = lineString.charAt(i);
				}
				
				measure.add(line);
			}
			
			notes.measures.add(measure);
			measureScanner.close();
		}		
		notesDataScanner.close();
	}
	
	private static String stripSM(String buffer) {
		if (!buffer.contains(":")) {
			throw new IllegalArgumentException("Info tag missing ':' char: " + buffer);
		} else {
			return buffer.substring(buffer.indexOf(":") + 1).trim();
		}
	}
}
