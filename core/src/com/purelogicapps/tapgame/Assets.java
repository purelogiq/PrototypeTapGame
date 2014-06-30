package com.purelogicapps.tapgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {
	public static Texture background;
	public static TextureAtlas uiatlas;
	public static BitmapFont bigfont;
	public static BitmapFont smallfont;
	public static Music bgmusic;
	public static Sound clap;
	
	public static void load(){
		background = new Texture("background.jpg");
		uiatlas = new TextureAtlas("tapgameui.pack");
		bigfont = new BitmapFont(Gdx.files.internal("bigfont.fnt"), 
				                 uiatlas.findRegion("bigfont"));
		smallfont = new BitmapFont(Gdx.files.internal("smallfont.fnt"), 
				                   uiatlas.findRegion("smallfont"));
		bgmusic = Gdx.audio.newMusic(Gdx.files.internal("bgmusic.mp3"));
		bgmusic.setLooping(true);
		clap = Gdx.audio.newSound(Gdx.files.internal("clap.ogg"));
	}
	
	public static void dispose(){
		background.dispose();
		uiatlas.dispose();
		bigfont.dispose();
		smallfont.dispose();
		bgmusic.dispose();
		clap.dispose();
	}
}
