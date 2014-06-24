package com.purelogicapps.tapgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.purelogicapps.tapgame.Arrows;
import com.purelogicapps.tapgame.Assets;
import com.purelogicapps.tapgame.Note;
import com.purelogicapps.tapgame.SMFile;
import com.purelogicapps.tapgame.SMFile.Notes.DifficultyClass;
import com.purelogicapps.tapgame.SMFile.Notes.NotesType;
import com.purelogicapps.tapgame.SMFile.ParseException;
import com.purelogicapps.tapgame.Session;
import com.purelogicapps.tapgame.SinglePlayerModel;


/*
 * Theme contains:
 * Noteskins
 * Scores (e.g. "perfect", "OK")
 * "Combo" as an image
 * 0-9 numbers for combo
 * Life bar images
 * Pause button
 * Pause menu buttons
 * 
 * Font that looks good on 1280x720 used for rendering text in screen coordinates.
 * 
 * 
 */
public class SinglePlayerScreen implements Screen{
	Music music;
	SinglePlayerModel model;
	SpriteBatch b;
	OrthographicCamera worldCam;
	BitmapFont font = Assets.smallfont;
	BitmapFontCache cache;
	Viewport viewport;
	Texture tex = new Texture("smtest/japan.png");
	
	boolean first = true;
	float time = 0;
	@Override
	public void render(float delta) {
		// Update model
		float speed = 1.5f;
		float mpos = music.getPosition();
		if(mpos < 2) time = mpos;
		else time += delta;
		model.update(time, false, false, false, false);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Draw in screen coordinates.
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		b.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		b.begin();
		
		float resizedHeight = tex.getHeight() * Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
		float resizedWidth = tex.getWidth() * Gdx.graphics.getHeight()/Gdx.graphics.getWidth();
		if(Gdx.graphics.getWidth() > Gdx.graphics.getHeight())
			b.draw(tex, 0, 0 - resizedHeight/2 + Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), resizedHeight);
		else
			b.draw(tex, 0 - resizedWidth/2 + Gdx.graphics.getWidth()/2, 0, resizedWidth, Gdx.graphics.getHeight());
		font.setScale(Gdx.graphics.getHeight()/720f);
		font.draw(b, "Hello world how are you", 0, font.getLineHeight());
		b.end();
		
		// Draw in world coordinates.
		viewport.update();
		worldCam.position.y = model.getCurrentBeat() * speed + viewport.getWorldHeight()/2 - 1.25f;
		worldCam.update();
//		System.out.println(model.getCurrentBeat() + ", " + time);
		b.setProjectionMatrix(worldCam.combined);
		b.begin();
//		b.setColor(1, 1, 1, 0.5f);
//		b.draw(tex, -2, model.getCurrentBeat() * speed -2, viewport.getWorldWidth() + 2, viewport.getWorldHeight());
//		b.setColor(Color.WHITE);
		// SUPER USEFUL b.setColor(...) tints the draw calls!! Can use for fading.
		for(Note n : model.col0){
			if(n.start > model.getCurrentBeat() -.35f && n.start < viewport.getWorldHeight() + model.getCurrentBeat() + 1)
			b.draw(Arrows.toTex(n.fraction), -2, n.start * speed, .5f, .5f, 1, 1, 1, 1, -90);
//			if(first) System.out.println(n.start);
		}
		first = false;
		for(Note n : model.col1){
			if(n.start > model.getCurrentBeat()-.35f && n.start < viewport.getWorldHeight() + model.getCurrentBeat() + 1)
			b.draw(Arrows.toTex(n.fraction), 0, n.start * speed, .5f, .5f, 1, 1, 1, 1, 0);
		}
		for(Note n : model.col2){
			if(n.start > model.getCurrentBeat() -.35f && n.start < viewport.getWorldHeight() + model.getCurrentBeat() + 1)
			b.draw(Arrows.toTex(n.fraction), 2, n.start * speed, .5f, .5f, 1, 1, 1, 1, 180);
		}
		for(Note n : model.col3){
			if(n.start > model.getCurrentBeat() -.35f
					&& n.start < viewport.getWorldHeight() + model.getCurrentBeat() + 1)
			b.draw(Arrows.toTex(n.fraction), 4, n.start * speed, .5f, .5f, 1, 1, 1, 1, 90);
		}
		
		b.draw(Arrows.controlUp, -2, model.getCurrentBeat() * speed-.5f, .5f, .5f, 1, 1, 1, 1, -90);
		b.draw(Arrows.controlUp, 0, model.getCurrentBeat() * speed-.5f, .5f, .5f, 1, 1, 1, 1, 0);
		b.draw(Arrows.controlUp, 2, model.getCurrentBeat() * speed-.5f, .5f, .5f, 1, 1, 1, 1, 180);
		b.draw(Arrows.controlUp, 4, model.getCurrentBeat() * speed-.5f, .5f, .5f, 1, 1, 1, 1, 90);
		b.end();
//		System.out.println("Max sprites: " + b.maxSpritesInBatch + ", Render calls: " + b.renderCalls);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void show() {
		try {
			music = Gdx.audio.newMusic(Gdx.files.internal("smtest/japan.mp3"));
			String smfilestring = Gdx.files.internal("smtest/japan.sm").readString();
			SMFile smfile;
			smfile = SMFile.parseSMFile("internal", "song", smfilestring);
			Session session = new Session();
			session.smfile = smfile;
			for(int i = 0; i < smfile.notes.size(); i++){
				if(smfile.notes.get(i).difficultyclass == DifficultyClass.HARD &&
						smfile.notes.get(i).notestype == NotesType.SINGLE){
					session.notesIndex = i;
				}
			}

			model = session.loadModel();
			b = new SpriteBatch();
//			camera = new OrthographicCamera(4, 8);
//			camera.position.x = 2;
//			camera.position.y = 0;
//			camera.update();
			viewport = new ExtendViewport(4, 5, 8, 10000);
			viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			worldCam = (OrthographicCamera) viewport.getCamera();
			worldCam.position.x = 2;
			worldCam.update();
			
			music.play();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
