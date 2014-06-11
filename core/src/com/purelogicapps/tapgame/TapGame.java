package com.purelogicapps.tapgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TapGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Stage stage;
	Texture background;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		background = new Texture("background.jpg");
		PersistantBackground.setBackground(background, 2, 2);
//		background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}
	
	
	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		PersistantBackground.render(0, 0, true, true, false, true);
		
//		Gdx.gl.glClearColor(0, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(background, 0, 0, 360, 640, 0, 0.275f, 360/background.getWidth(), 0.275f + 640/background.getHeight());
//		batch.end();
	}
}
