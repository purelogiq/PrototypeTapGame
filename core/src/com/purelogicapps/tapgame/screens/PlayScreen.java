package com.purelogicapps.tapgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.oracle.jrockit.jfr.DataType;
import com.purelogicapps.tapgame.Note;
import com.purelogicapps.tapgame.Note.HoldState;
import com.purelogicapps.tapgame.PlayAssets;
import com.purelogicapps.tapgame.Session;
import com.purelogicapps.tapgame.PlayModel;
import com.purelogicapps.tapgame.Note.NoteType;

public class PlayScreen implements Screen, InputProcessor{
	public static final float TARGET_SCREEN_LONG = 1280;
	public static final float TARGET_SCREEN_SHORT = 720;
	public static final float NOTES_BELOW_CONTROLS = 1.5f;
	public static final float TOUCH_SPACE_AROUND_CONTROL = .65f;
	public static final int NUM_COLS = 4;
	
	// Game stuff.
	private Session session;
	private PlayModel model;
	private float songTime = 0;
	private float beatPos;
	
	// Render stuff.
	private SpriteBatch batch;
	private ExtendViewport worldView;
	private OrthographicCamera worldCam;
	private OrthographicCamera screenCam;
	private float worldWidth;
	private float worldHeight;
	private int screenWidth;
	private int screenHeight;
	Sprite controlSprite;
	Sprite noteSprite;
	Sprite holdEndSprite;
	Sprite holdBodySprite;
	
	// Input stuff.
	Vector3 touchVec;
	boolean[] colsHeld = new boolean[NUM_COLS];
	int[] colPointers = new int[]{ -1, -1, -1, -1 };
	
	public PlayScreen(Session session){
		this.session = session;
		model = session.createModel();
		
		// Initialize viewports.
		// Note to self, keep mins the same (square).
		float minWorldWidth = NUM_COLS;
		float maxWorldWidth = (session.strechLandscape) ? NUM_COLS * 2 : minWorldWidth;
		float minWorldHeight = NUM_COLS;
		float maxWorldHeight = 0; // No max
		worldView = new ExtendViewport(minWorldHeight, minWorldHeight, 
				                       maxWorldWidth, maxWorldHeight);
		worldCam = (OrthographicCamera) worldView.getCamera();
		screenCam = new OrthographicCamera();
		// resize() will take care of the rest.
		
		// Create fixed number of sprites used for positioning the textures.
		batch = new SpriteBatch(300);
		noteSprite = new Sprite();
		noteSprite.setSize(1, 1);
		noteSprite.setOriginCenter();
		holdEndSprite = new Sprite();
		holdEndSprite.setSize(.75f, .75f);
		holdEndSprite.setOriginCenter();
		holdBodySprite = new Sprite();
		controlSprite = new Sprite();
		controlSprite.setSize(1, 1);
		controlSprite.setOriginCenter();
		
		// Create reusable vector for touch events.
		touchVec = new Vector3();
		touchVec.z = 0;
	}
	
	//=========== Application lifecycle ===================
	@Override
	public void resize(int width, int height) {
		// Update world space.
		worldView.update(width, height);
		worldWidth = worldView.getWorldWidth();
		worldHeight = worldView.getWorldHeight();
		worldCam.position.x = worldWidth / 2; // Center camera horizontally. 0 at very left.
		worldCam.update();
		
		// Update screen space.
		screenWidth = width;
		screenHeight = height;
		screenCam.viewportWidth = width;
		screenCam.viewportHeight = height;
		screenCam.position.x = width / 2;
		screenCam.position.y = height / 2;
		screenCam.update();
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		session.music.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
		session.music.pause();
	}

	@Override
	public void resume() {
		session.music.play();
		songTime = session.music.getPosition();
	}

	@Override
	public void dispose() {
		batch.dispose();
		session.dispose();
	}
	
	//====== Play update and render loop ==============	
	@Override
	public void render(float delta) {
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Draw in screen coordinates.
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setProjectionMatrix(screenCam.combined);
		batch.begin();
		renderBackground();
		renderText();
		batch.end();
		
		// Draw in world coordinates.
		worldView.update();
		worldCam.position.y = beatPos * session.speed + worldHeight / 2 - NOTES_BELOW_CONTROLS;
		worldCam.update();
		batch.setProjectionMatrix(worldCam.combined);
		batch.begin();
		renderNotes();
		renderControls();
		batch.end();

//		Debug
//		System.out.println(Gdx.graphics.getFramesPerSecond());
//		System.out.println(batch.maxSpritesInBatch);
	}
	
	private void update(float delta){
		if(songTime < 1) songTime = session.music.getPosition();
		else songTime += delta;
		model.update(songTime, delta, colsHeld);
		beatPos = model.getCurrentBeat();
	}
	
	private void renderBackground(){
		if(session.background != null){
			batch.setColor(.5f, .5f, .5f, 1f);
			Texture background = session.background;
			float resizedHeight = background.getHeight() * screenWidth / screenHeight;
			float resizedWidth = background.getWidth() * screenHeight / screenWidth;
			if(screenWidth > screenHeight){
				batch.draw(background, 0, 0 - resizedHeight/2 + screenHeight/2,
						   screenWidth, resizedHeight);
			}else{
				batch.draw(background, 0 - resizedWidth/2 + screenWidth/2, 0, 
						   resizedWidth, screenHeight);
			}
			batch.setColor(1, 1, 1, 1);
		}
	}
	
	private void renderText(){
		BitmapFont font = PlayAssets.playScreenFont;
		font.setScale(screenHeight / TARGET_SCREEN_SHORT);
		font.draw(batch, session.smfile.title, 0, font.getLineHeight());
	}
	
	private void renderNotes(){
		for(int col = 0; col < NUM_COLS; col++){
			Note[] column = model.cols[col];
			
			for(int i = 0; i < column.length; i++){
				Note note = column[i];
				// Check if it is in bounds of the camera.
//				if(!note.hidden &&
//				   note.start * session.speed < worldCam.position.y + worldHeight/2 + 2 &&
//				   note.end * session.speed > beatPos * session.speed){
//					if(note.type == NoteType.TAP) renderArrow(note, col);
//					else if(note.type == NoteType.MINE) renderArrow(note, col);
//					else if(note.type == NoteType.HOLD) renderHold(note, col);
//				}
				if(!note.hidden &&
				   note.start * session.speed < worldCam.position.y + worldHeight/2 + 2 &&
				   note.end * session.speed > worldCam.position.y - worldHeight/2 - 2){
					if(note.type == NoteType.TAP) renderArrow(note, col, Color.WHITE);
					else if(note.type == NoteType.MINE) renderArrow(note, col, Color.RED);
					else if(note.type == NoteType.HOLD) renderHold(note, col);
				}
			}
		}
	}
	
	private void renderArrow(Note note, int col, Color tint){
		noteSprite.setSize(1, 1);
		noteSprite.setRegion(PlayAssets.toTex(note.fraction));
		noteSprite.setRotation(getRotation(col));
		noteSprite.setCenter(worldWidth/NUM_COLS/2 + worldWidth/NUM_COLS * col, 
				             note.start * session.speed);
		noteSprite.setColor(tint);
		noteSprite.draw(batch);
	}
	
	private void renderHold(Note hold, int col){
		// Setup hold textures based on state.
		if(hold.holdState == HoldState.INACTIVE || hold.holdState == HoldState.DEAD){
			holdBodySprite.setRegion(PlayAssets.holdBodyInactive);
			holdEndSprite.setRegion(PlayAssets.holdEnd);
		} else if(hold.holdState == HoldState.ALIVE || hold.holdState == HoldState.SLEEPING){
			holdBodySprite.setRegion(PlayAssets.holdBodyActive);
			holdEndSprite.setRegion(PlayAssets.holdEnd);
		}
		
		// Setup hold body and hold end size and position. 
		holdBodySprite.setSize(.75f, (hold.end - hold.start) * session.speed);
		holdBodySprite.setV(0);
		holdBodySprite.setV2(holdBodySprite.getHeight() *
				      PlayAssets.n4.getRegionHeight() / PlayAssets.holdBodyInactive.getHeight());
		holdBodySprite.setCenter(worldWidth/NUM_COLS/2 + worldWidth/NUM_COLS * col, 0);
		holdBodySprite.setY(hold.start * session.speed);
		holdEndSprite.setCenter(worldWidth/NUM_COLS/2 + worldWidth/NUM_COLS * col, 
				             hold.end * session.speed);
		
		// Draw hold body behind the arrow and the hold end. DEAD/SLEEPING holds should be darker.
		Color tint = Color.WHITE;
		if(hold.holdState == HoldState.DEAD) tint = Color.GRAY;
		else if(hold.holdState == HoldState.SLEEPING) tint = Color.LIGHT_GRAY;
		holdBodySprite.setColor(tint);
		holdEndSprite.setColor(tint);
		holdBodySprite.draw(batch);
		renderArrow(hold, col, tint);
		holdEndSprite.draw(batch);
	}
	
	private void renderControls(){
		for(int i = 0; i < NUM_COLS; i++){
			renderControl((colsHeld[i]) ? PlayAssets.controlDown : PlayAssets.controlUp, i);
		}
	}
	
	private void renderControl(TextureRegion tex, int col){
		controlSprite.setRegion(tex);
		controlSprite.setRotation(getRotation(col));
		controlSprite.setCenter(worldWidth/NUM_COLS/2 + worldWidth/NUM_COLS * col, 
				                beatPos * session.speed);
		controlSprite.draw(batch);
	}
	
	private float getRotation(int col){
		if(col == 0) return -90;
		else if(col == 1) return 0;
		else if(col == 2) return 180;
		else return 90;
	}
	
	
	//======= InputProcessor implementation ==================
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.LEFT || keycode == Input.Keys.D){
			colsHeld[0] = true;
			model.touchDown(0);
		}
		if(keycode == Input.Keys.DOWN || keycode == Input.Keys.F){
			colsHeld[1] = true;
			model.touchDown(1);
		}
		if(keycode == Input.Keys.UP || keycode == Input.Keys.J){
			colsHeld[2] = true;
			model.touchDown(2);
		}
		if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.K){
			colsHeld[3] = true;
			model.touchDown(3);
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.LEFT || keycode == Input.Keys.D){
			colsHeld[0] = false;
		}
		if(keycode == Input.Keys.DOWN || keycode == Input.Keys.F){
			colsHeld[1] = false;
		}
		if(keycode == Input.Keys.UP || keycode == Input.Keys.J){
			colsHeld[2] = false;
		}
		if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.K){
			colsHeld[3] = false;
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchVec.x = screenX;
		touchVec.y = screenY;
		Vector3 touch = worldCam.unproject(touchVec); // overwrites touchVec
		// Check if the touch is in bounds in the y dimension.
		if(!(beatPos * session.speed - TOUCH_SPACE_AROUND_CONTROL < touch.y 
		     && touch.y < beatPos * session.speed + TOUCH_SPACE_AROUND_CONTROL)){
			return false;
		}
		
		// Touch the appropriate control based on the x coordinate.
		for(int i = 0; i < NUM_COLS; i++){
			if(worldWidth / NUM_COLS * i < touch.x && touch.x <= worldWidth / NUM_COLS * (i + 1)){
				colsHeld[i] = true;
				colPointers[i] = pointer;
				model.touchDown(i);
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(int i = 0; i < NUM_COLS; i++){
			if(pointer == colPointers[i]){
				colsHeld[i] = false;
				colPointers[i] = -1;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
