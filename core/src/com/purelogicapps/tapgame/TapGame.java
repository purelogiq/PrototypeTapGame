package com.purelogicapps.tapgame;

import com.badlogic.gdx.Game;
import com.purelogicapps.tapgame.screens.PlayScreen;

public class TapGame extends Game{
	public static final float TARGET_SCREEN_WIDTH = 720;
	public static final float TARGET_SCREEN_HEIGHT = 1280;
	public static final String CHAOZ_JAPAN = "Songs/Chaoz Japan/Chaoz Japan.sm";
	public static final String FLOWERS = "Songs/HANA RANMAN (Flowers)/HANA RANMAN (Flowers).sm";
	public static final String CALLING_MARS = "Songs/Calling Mars/callingmars.sm";
	public static final String SPRINGTIME = "Songs/Springtime/Springtime.sm";
	@Override
	public void create() {
		Assets.load();
		PlayAssets.load();
		UIStyle.load();
		Session session = new Session();
		session.loadNewSMFile(CHAOZ_JAPAN);
		session.notesIndex = 1;
		PlayScreen play = new PlayScreen(session);
		this.setScreen(play);
		
//		SinglePlayerTestScreen p = new SinglePlayerTestScreen();
//		this.setScreen(p);
//		PersistantBackground.setBackground(Assets.background, 4, 4);
//		MainMenu mainMenu = new MainMenu();
//		this.setScreen(mainMenu);
	}
	
}
