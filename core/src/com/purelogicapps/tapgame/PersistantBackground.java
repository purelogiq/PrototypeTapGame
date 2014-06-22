package com.purelogicapps.tapgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Static class that manages a scrolling background texture that fills the screen.
 * Being static, this class will render the background in a continuous fashion even
 * when the application switches screens.
 * @author purelogiq
 */
public class PersistantBackground {
	private static final OrthographicCamera cam = new OrthographicCamera();
	private static final SpriteBatch batch = new SpriteBatch(2);
	private static Texture background;
	private static float backgroundWidth; 
	private static float backgroundHeight; 
	private static float scrollSpeedHoriz;
	private static float scrollSpeedVert;
	private static float scrollTimeHoriz;
	private static float scrollTimeVert;
	private static float scrollCoordU = 0;
	private static float scrollCoordU2 = 1;
	private static float scrollCoordV = 0;
	private static float scrollCoordV2 = 1;
	
	/**
	 * Set the background and initial scrolling state.
	 * @param texture The background texture. Recommended that it is a power of two size.
	 * @param scrollSpeedHoriz Seconds needed to completely scroll through the background's width.
	 * @param scrollSpeedVert Seconds needed to completely scroll through the background's height.
	 */
	public static void setBackground(Texture texture, float scrollSpeedHoriz, float scrollSpeedVert){
		if(scrollSpeedHoriz <= 0 || scrollSpeedVert <= 0){
			scrollSpeedHoriz = 1;
			scrollSpeedVert = 1;
		}
		PersistantBackground.batch.disableBlending(); //performance.
		PersistantBackground.background = texture;
		// Following line requires power of two texture size on some mobile devices.
		PersistantBackground.background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		PersistantBackground.backgroundWidth = texture.getWidth();
		PersistantBackground.backgroundHeight = texture.getHeight();
		PersistantBackground.scrollSpeedHoriz = scrollSpeedHoriz;
		PersistantBackground.scrollSpeedVert = scrollSpeedVert;
		PersistantBackground.scrollTimeHoriz = 0;
		PersistantBackground.scrollTimeVert = 0;
		cam.viewportWidth = Gdx.graphics.getWidth();
		cam.viewportHeight = Gdx.graphics.getHeight();
		cam.position.x = cam.viewportWidth/2;
		cam.position.y = cam.viewportHeight/2;
		cam.update();
	}

	
	/** This method uses its own sprite batch AND it clears the screen for you with black.<br />
	 * <b>Delta time:</b> seconds passed since last frame, set one or both to 0 if you do not want to 
	 * scroll in that direction. <br />
	 * <b>Repeat:</b> if true the texture will be repeated in that direction if it cannot fill the 
	 * screen otherwise it will be stretched.<br />
	 * <b>Reverse:</b> if true the background and will be reversed in that direction.
	 */
	public static void render(float horizDeltaTime, float vertDeltaTime, 
				              boolean repeatHoriz, boolean repeatVert,
				              boolean reverseHoriz, boolean reverseVert){
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		if(cam.viewportWidth != screenWidth || cam.viewportHeight != screenHeight){
			cam.viewportWidth = screenWidth;
			cam.viewportHeight = screenHeight;
			cam.position.x = cam.viewportWidth/2;
			cam.position.y = cam.viewportHeight/2;
			cam.update();
		}
		
		float repeatUOffset = (repeatHoriz) ? screenWidth / backgroundWidth : 0;
		scrollTimeHoriz += horizDeltaTime;
		if(scrollTimeHoriz > scrollSpeedHoriz) scrollTimeHoriz = 0;
		float U1 = scrollTimeHoriz / scrollSpeedHoriz;
		scrollCoordU = (reverseHoriz) ? U1 + 1 + repeatUOffset : U1;
		scrollCoordU2 = (reverseHoriz) ? U1: U1 + 1 + repeatUOffset;
		
		float repeatVOffset = (repeatVert) ? screenHeight / backgroundHeight : 0;
		scrollTimeVert += vertDeltaTime;
		if(scrollTimeVert > scrollSpeedVert) scrollTimeVert = 0;
		float V1 = scrollTimeVert / scrollSpeedVert;
		scrollCoordV = (reverseVert) ? V1 + 1 + repeatVOffset : V1;
		scrollCoordV2 = (reverseVert) ? V1: V1 + 1 + repeatVOffset;
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(background, 0, 0, screenWidth, screenHeight, 
				   scrollCoordU, scrollCoordV, scrollCoordU2, scrollCoordV2);
		batch.end();
	}

}
