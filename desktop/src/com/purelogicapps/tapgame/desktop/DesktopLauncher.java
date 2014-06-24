package com.purelogicapps.tapgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.purelogicapps.tapgame.TapGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 540;
		config.height = 960;
		config.x = 450;
		config.y = 20;
		new LwjglApplication(new TapGame(), config);
	}
}
