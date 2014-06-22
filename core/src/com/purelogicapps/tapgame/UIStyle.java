package com.purelogicapps.tapgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UIStyle {
	public static ImageTextButtonStyle blueImgTxtBtnStyle;
	public static TextButtonStyle txtBtnStyle;
	public static LabelStyle smallLabelStyle;
	
	public static void load(){
		NinePatchDrawable drawableBtnUp = new NinePatchDrawable(Assets.uiatlas.createPatch("blue_button00"));
		NinePatchDrawable drawableBtnDown = new NinePatchDrawable(Assets.uiatlas.createPatch("blue_button01"));
		NinePatchDrawable drawablePanel = new NinePatchDrawable(Assets.uiatlas.createPatch("blue_panel"));
		TextureRegionDrawable drawableBtnPlay = new TextureRegionDrawable(Assets.uiatlas.findRegion("button_play"));
		
		smallLabelStyle = new LabelStyle(Assets.smallfont, Color.WHITE);
		smallLabelStyle.background = drawablePanel;
		
		blueImgTxtBtnStyle = new ImageTextButtonStyle();
		blueImgTxtBtnStyle.up = drawableBtnUp;
		blueImgTxtBtnStyle.down = drawableBtnDown;
		blueImgTxtBtnStyle.imageUp = drawableBtnPlay;
		blueImgTxtBtnStyle.imageDown = drawableBtnPlay;
		blueImgTxtBtnStyle.font = Assets.bigfont;
		blueImgTxtBtnStyle.overFontColor = Color.YELLOW;
		blueImgTxtBtnStyle.fontColor = Color.WHITE;
		blueImgTxtBtnStyle.pressedOffsetY = -2;
		
		txtBtnStyle = new TextButtonStyle();
		txtBtnStyle.font = Assets.bigfont;
		txtBtnStyle.up = drawableBtnUp;
		txtBtnStyle.down = drawableBtnDown;
		txtBtnStyle.overFontColor = Color.YELLOW;
		txtBtnStyle.fontColor = Color.WHITE;
		txtBtnStyle.pressedOffsetY = -2;
	}
	
}
