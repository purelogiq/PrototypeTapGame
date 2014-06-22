package com.purelogicapps.tapgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.purelogicapps.tapgame.Arrows;
import com.purelogicapps.tapgame.Note;
import com.purelogicapps.tapgame.SMFile;
import com.purelogicapps.tapgame.SMFile.Notes.DifficultyClass;
import com.purelogicapps.tapgame.SMFile.Notes.NotesType;
import com.purelogicapps.tapgame.SMFile.ParseException;
import com.purelogicapps.tapgame.Session;
import com.purelogicapps.tapgame.SinglePlayerModel;

public class SinglePlayerScreen implements Screen{
	Music music;
	SinglePlayerModel model;
	SpriteBatch b;
	OrthographicCamera camera;
	
	boolean first = true;
	float time = 0;
	@Override
	public void render(float delta) {
		float speed = 2;
		float mpos = music.getPosition();
		if(mpos < 1){
			time = mpos;
		}else{
			time += delta;
		}
		model.update(time, false, false, false, false);
		camera.position.y = model.getCurrentBeat() * speed + 2;
		camera.update();
		System.out.println(model.getCurrentBeat() + ", " + time);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		b.setProjectionMatrix(camera.combined);
		b.begin();
		// SUPER USEFUL b.setColor(...) tints the draw calls!! Can use for fading.
		for(Note n : model.col0){
			if(n.start > model.getCurrentBeat())
			b.draw(Arrows.toTex(n.fraction), 0, n.start * speed, .5f, .5f, 1, 1, 1, 1, -90);
			if(first) System.out.println(n.start);
		}
		first = false;
		for(Note n : model.col1){
			if(n.start > model.getCurrentBeat())
			b.draw(Arrows.toTex(n.fraction), 1, n.start * speed, .5f, .5f, 1, 1, 1, 1, 0);
		}
		for(Note n : model.col2){
			if(n.start > model.getCurrentBeat())
			b.draw(Arrows.toTex(n.fraction), 2, n.start * speed, .5f, .5f, 1, 1, 1, 1, 180);
		}
		for(Note n : model.col3){
			if(n.start > model.getCurrentBeat())
			b.draw(Arrows.toTex(n.fraction), 3, n.start * speed, .5f, .5f, 1, 1, 1, 1, 90);
		}
		
		b.draw(Arrows.controlUp, 0, model.getCurrentBeat() * speed, .5f, .5f, 1, 1, 1, 1, -90);
		b.draw(Arrows.controlUp, 1, model.getCurrentBeat() * speed, .5f, .5f, 1, 1, 1, 1, 0);
		b.draw(Arrows.controlUp, 2, model.getCurrentBeat() * speed, .5f, .5f, 1, 1, 1, 1, 180);
		b.draw(Arrows.controlUp, 3, model.getCurrentBeat() * speed, .5f, .5f, 1, 1, 1, 1, 90);
		b.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		try {
			music = Gdx.audio.newMusic(Gdx.files.internal("smtest/ocean.mp3"));
			String smfilestring = Gdx.files.internal("smtest/ocean.sm").readString();
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
			camera = new OrthographicCamera(4, 8);
			camera.position.x = 2;
			camera.position.y = 0;
			camera.update();
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
