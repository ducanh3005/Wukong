package com.heyzqt.widget;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.heyzqt.handle.Constant;
import com.heyzqt.state.Start;
import com.heyzqt.xiyou.MyGdxGame;

/**
 * Created by heyzqt on 2017/3/2.
 *
 * 设置界面关于游戏对话框
 */
public class AboutGameDialog extends BaseDialog {

	public AboutGameDialog(float x, float y) {
		super(x, y);
		init();
	}

	private void init() {
		//获取资源
		mAtlas = MyGdxGame.assetManager.getTextureAtlas(Constant.COMMON_COMPONENTS);
		mBackground = MyGdxGame.assetManager.getTexture(Constant.ABOUT_DIALOG);

		//初始化对话框
		mWindow = new Image(new TextureRegion(mBackground));
		mWindow.setPosition(x - mBackground.getWidth() / 2, y - mBackground.getHeight() / 2);

		//初始化返回按钮
		mBackBtn = new ImageButton(new TextureRegionDrawable(mAtlas.findRegion("close48")));
		mBackBtn.setPosition(x + mBackground.getWidth() / 2 - 55, y + mBackground.getHeight() / 2 - 53);

		mBackBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Start.isShowAboutDialog = false;
				return true;
			}
		});
	}
}
