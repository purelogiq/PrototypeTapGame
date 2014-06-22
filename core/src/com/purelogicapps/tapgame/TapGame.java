package com.purelogicapps.tapgame;

import com.badlogic.gdx.Game;
import com.purelogicapps.tapgame.screens.MainMenu;
import com.purelogicapps.tapgame.screens.SinglePlayerScreen;



public class TapGame extends Game{
	public static final float TARGET_SCREEN_WIDTH = 720;
	public static final float TARGET_SCREEN_HEIGHT = 1280;

	@Override
	public void create() {
		Assets.load();
		Arrows.load();
		UIStyle.load();
		PersistantBackground.setBackground(Assets.background, 4, 4);
		SinglePlayerScreen p = new SinglePlayerScreen();
		this.setScreen(p);
//		MainMenu mainMenu = new MainMenu();
//		this.setScreen(mainMenu);
	}
	
}
