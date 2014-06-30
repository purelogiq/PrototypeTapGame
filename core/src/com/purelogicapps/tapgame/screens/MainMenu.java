package com.purelogicapps.tapgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.purelogicapps.tapgame.Assets;
import com.purelogicapps.tapgame.PersistantBackground;
import com.purelogicapps.tapgame.TapGame;
import com.purelogicapps.tapgame.UIStyle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.math.Interpolation.*;

public class MainMenu implements Screen{
	
	private static FitViewport fitViewport = new FitViewport(TapGame.TARGET_SCREEN_WIDTH,
			                                                 TapGame.TARGET_SCREEN_HEIGHT);
	private Stage stage;
	private SpriteBatch stageBatch = new SpriteBatch();
	
	@Override
	public void render(float delta) {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		// Update
		stage.act(delta);
		
		// Draw
		Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
		PersistantBackground.render(delta, 0, true, true, false, false);
		fitViewport.update(screenWidth, screenHeight, true);
		
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// No need to update viewport here, already doing it in render().
	}

	@Override
	public void show() {
		if(!Assets.bgmusic.isPlaying()) Assets.bgmusic.play();
		stage = new Stage(fitViewport, stageBatch);
		setupStage();
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		this.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
	
	private void setupStage(){
		float W = TapGame.TARGET_SCREEN_WIDTH;
		float H = TapGame.TARGET_SCREEN_HEIGHT;
		
		// Add Tap Game logo.
		Image logo = new Image(Assets.uiatlas.findRegion("logo"));
		logo.setPosition(W/2 - logo.getWidth() / 2,  H + logo.getHeight() + 50);
		logo.addAction(moveTo(logo.getX(), H - logo.getHeight() - 50, 3, elasticOut));
		stage.addActor(logo);
		
		// Add credits label.
		Label credit = new Label("Github.com/purelogiq", UIStyle.smallLabelStyle);
		credit.setPosition(W/2 - credit.getWidth()/2, 0 - credit.getHeight() - 10);
		credit.addAction(sequence(
						 delay(2),
						 moveTo(credit.getX(), credit.getHeight() + 100, 1, swingOut)
				        ));
		stage.addActor(credit);
		
		// Create Play, Edit and Options button.
		ImageTextButton btnPlay = new ImageTextButton("PLAY", UIStyle.blueImgTxtBtnStyle);
		TextButton btnEdit = new TextButton("EDIT", UIStyle.txtBtnStyle);
		TextButton btnOptions = new TextButton("OPTIONS", UIStyle.txtBtnStyle);
		
		// Add buttons to table, and add actions to table.
		Table table = new Table();
		table.add(btnPlay).width(540).height(140).padBottom(20).row();
		table.add(btnEdit).width(540).height(80).padBottom(10).row();
		table.add(btnOptions).width(540).height(80);
		table.setPosition(W/2 - table.getWidth()/2, H + table.getHeight() + 300);
		table.addAction(sequence(delay(0.5f), moveTo(table.getX(), 570, 1.2f, elasticOut)));
//		Color tableColor = table.getColor();
//		table.setColor(tableColor.r, tableColor.g, tableColor.b, 0);
//		table.addAction(fadeIn(2));
		stage.addActor(table);
	}

}
