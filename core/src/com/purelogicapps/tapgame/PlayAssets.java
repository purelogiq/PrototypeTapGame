package com.purelogicapps.tapgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayAssets {
	public static BitmapFont playScreenFont;
	public static TextureAtlas atlas;
	public static TextureRegion n4;
	public static TextureRegion n8;
	public static TextureRegion n12;
	public static TextureRegion n16;
	public static TextureRegion n24;
	public static TextureRegion n32;
	public static TextureRegion n48;
	public static TextureRegion n64;
	public static TextureRegion n192;
	public static TextureRegion controlUp;
	public static TextureRegion controlDown;
	public static Texture holdBodyInactive;
	public static Texture holdBodyActive;
	public static TextureRegion holdEnd;
	
	public static void load(){
		playScreenFont = new BitmapFont(Gdx.files.internal("play-screen-font.fnt"));
		atlas = new TextureAtlas("arrows.pack");
		n4 = atlas.findRegion("4th");
		n8 = atlas.findRegion("8th");
		n12 = atlas.findRegion("12th");
		n16 = atlas.findRegion("16th");
		n24 = atlas.findRegion("24th");
		n32 = atlas.findRegion("32th");
		n48 = atlas.findRegion("48th");
		n64 = atlas.findRegion("64th");
		n192 = atlas.findRegion("192th");
		controlUp = atlas.findRegion("control_up");
		controlDown = atlas.findRegion("control_down");
		holdEnd = atlas.findRegion("hold_end_active");
		holdBodyInactive = new Texture("hold_inactive_down.png");
		holdBodyActive = new Texture("hold_active_down.png");
		holdBodyInactive.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		holdBodyActive.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}
	
	public static TextureRegion toTex(int fraction){
		if(fraction == 4) return n4;
		else if(fraction == 8) return n8;
		else if(fraction == 12) return n12;
		else if(fraction == 16) return n16;
		else if(fraction == 24) return n24;
		else if(fraction == 32) return n32;
		else if(fraction == 64) return n64;
		else if(fraction == 192) return n192;
		else return null;
	}
}
