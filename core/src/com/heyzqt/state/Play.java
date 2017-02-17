package com.heyzqt.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.heyzqt.entity.Background;
import com.heyzqt.entity.Monkey;
import com.heyzqt.handle.Box2DContactListener;
import com.heyzqt.handle.Constant;
import com.heyzqt.xiyou.MyGdxGame;

/**
 * Created by heyzqt on 2017/2/7.
 *
 * 游戏界面
 */
public class Play extends GameState {

	//声明世界
	private World mWorld;

	//物理世界相机
	private OrthographicCamera mBox2DCamera;

	//物理世界渲染器
	private Box2DDebugRenderer mBox2DRender;

	//声明刚体监听器
	private Box2DContactListener mContactListener;

	//地图
	private TiledMap mMap;

	//瓦片大小
	private float tileSize;

	//地图宽度（瓦片数量）
	private float tileWidth;

	//地图高度（瓦片数量）
	private float tileHeight;

	//地图渲染器
	private OrthogonalTiledMapRenderer mOrthogonalTiledMapRenderer;

	//游戏渲染时间
	private float statetime;

	//刚体信息
	private BodyDef mBodyDef;

	//刚体
	private Body mBody;

	//游戏主角
	private Monkey mMonkey;

	//游戏背景
	private Background mBackground;

	//左按钮
	private ImageButton mLeftBtn;

	//右按钮
	private ImageButton mRightBtn;

	//跳跃按钮
	private ImageButton mJumpBtn;

	//攻击按钮
	private ImageButton mAttackBtn;

	public Play(GameStateManager manager) {
		super(manager);
		init();
	}

	private void init() {

		//初始化世界 初始化重力
		mWorld = new World(new Vector2(0, -9.8f), true);

		//初始化刚体世界相机
		mBox2DCamera = new OrthographicCamera();
		mBox2DCamera.setToOrtho(false, MyGdxGame.VIEW_WIDTH / Constant.RATE, MyGdxGame.VIEW_HEIGHT / Constant.RATE);

		//初始化Box2D渲染器
		mBox2DRender = new Box2DDebugRenderer();

		//创建地图
		createMap();

		//创建主角
		createActor();

		//初始化背景
		mBackground = new Background(Constant.PLAY_BG);

		//初始化界面控件
		TextureAtlas mAtlas = MyGdxGame.mAssetManager.getTextureAtlas(Constant.PLAY_WIDGET);
		mLeftBtn = new ImageButton(new TextureRegionDrawable(mAtlas.findRegion("leftBtnUp")),
				new TextureRegionDrawable(mAtlas.findRegion("leftBtnDown")));
		mLeftBtn.setPosition(100,20);
		mRightBtn = new ImageButton(new TextureRegionDrawable(mAtlas.findRegion("rightBtnUp")),
				new TextureRegionDrawable(mAtlas.findRegion("rightBtnDown")));
		mRightBtn.setPosition(260, 20);

		mAttackBtn = new ImageButton(new TextureRegionDrawable(mAtlas.findRegion("attackBtnUp")),
				new TextureRegionDrawable(mAtlas.findRegion("attackBtnDown")));
		mAttackBtn.setPosition(1000, 35);
		mJumpBtn = new ImageButton(new TextureRegionDrawable(mAtlas.findRegion("jumpBtnUp")),
				new TextureRegionDrawable(mAtlas.findRegion("jumpBtnDown")));
		mJumpBtn.setPosition(1130, 140);

		mStage.addActor(mLeftBtn);
		mStage.addActor(mRightBtn);
		mStage.addActor(mAttackBtn);
		mStage.addActor(mJumpBtn);

		initListener();
	}

	private void initListener() {
		//左按钮
		mLeftBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//添加一个水平方向速度
				mBody.setLinearVelocity(-1f, 0);
				Monkey.STATE = Monkey.STATE_LEFT;
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//添加一个水平方向速度
				mBody.setLinearVelocity(0f, 0);
				Monkey.STATE = Monkey.STATE_IDEL_LEFT;
			}
		});

		//右按钮
		mRightBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Monkey.STATE = Monkey.STATE_RIGHT;
				mBody.setLinearVelocity(1f, 0);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				Monkey.STATE = Monkey.STATE_IDEL_RIGHT;
				mBody.setLinearVelocity(0, 0);
			}
		});

		//攻击按钮
		mAttackBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("hello attack");
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});

		//跳跃按钮
		mJumpBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				mBody.applyForceToCenter(0, 250, true);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
	}

	private void createActor() {
		//初始化刚体属性
		mBodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		mBodyDef.type = BodyDef.BodyType.DynamicBody;
		//position是刚体中心点的位置
		mBodyDef.position.set(100 / Constant.RATE, 400 / Constant.RATE);
		mBody = mWorld.createBody(mBodyDef);
		shape.setAsBox(36 / Constant.RATE, 60 / Constant.RATE);
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Constant.PLAYER;
		fixtureDef.filter.maskBits = Constant.BLOCK;
		mBody.createFixture(fixtureDef).setUserData("player");

		//创建传感器 foot
		shape.setAsBox(25 / Constant.RATE, 3 / Constant.RATE, new Vector2(0, -60 / Constant.RATE), 0);
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Constant.PLAYER;
		fixtureDef.filter.maskBits = Constant.BLOCK;
		fixtureDef.isSensor = true;
		mBody.createFixture(fixtureDef).setUserData("foot");

		mMonkey = new Monkey(mBody);
	}

	private void createMap() {
		try {
			mMap = new TmxMapLoader().load("map/level_0.tmx");
		} catch (Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
		}

		//初始化相机渲染器
		mOrthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(mMap);

		//赋值地图参数
		tileSize = mMap.getProperties().get("tilewidth", Integer.class);
		tileWidth = mMap.getProperties().get("width", Integer.class);
		tileHeight = mMap.getProperties().get("height", Integer.class);

		//绑定地面图层与刚体
		TiledMapTileLayer layer = (TiledMapTileLayer) mMap.getLayers().get("ground");

		//遍历所有单元格
		BodyDef bodyDef = new BodyDef();
		FixtureDef chainFixtureDef = new FixtureDef();
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {
				TiledMapTileLayer.Cell cell = layer.getCell(col, row);
				if (cell == null || cell.getTile() == null) {
					continue;
				}

				bodyDef.type = BodyDef.BodyType.StaticBody;
				bodyDef.position.set(
						(col + 0.5f) * tileSize / Constant.RATE,
						(row + 0.5f) * tileSize / Constant.RATE);

				//设置地面是链式形状
				ChainShape chainShape = new ChainShape();
				Vector2[] vector2 = new Vector2[3];
				vector2[0] = new Vector2(-tileSize / 2 / Constant.RATE, -tileSize / 2 / Constant.RATE);
				vector2[1] = new Vector2(-tileSize / 2 / Constant.RATE, tileSize / 2 / Constant.RATE);
				vector2[2] = new Vector2(tileSize / 2 / Constant.RATE, tileSize / 2 / Constant.RATE);
				chainShape.createChain(vector2);
				//绑定夹具与链式图形
				chainFixtureDef.shape = chainShape;
				//设置恢复力为0
				chainFixtureDef.friction = 0;
				chainFixtureDef.filter.categoryBits = Constant.BLOCK;
				chainFixtureDef.filter.maskBits = Constant.PLAYER;
				//设置传感器
				chainFixtureDef.isSensor = false;

				mWorld.createBody(bodyDef).createFixture(chainFixtureDef).setUserData("block");
			}
		}
	}

	@Override
	public void update(float delta) {

		mWorld.step(1 / 60f, 1, 1);

		/**
		 * 主角死亡 方式一：掉落到屏幕之外 方式二：碰到敌人
		 */
		if (mMonkey.getBody().getPosition().y < 0) {
			mGameStateManager.setState(GameStateManager.FAILURE);
		}

		/**
		 * 主角通关
		 */
		if (mMonkey.getBody().getPosition().x * Constant.RATE > tileWidth * tileSize) {
			mGameStateManager.setState(GameStateManager.SUCCESS);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		statetime += Gdx.graphics.getDeltaTime();

		update(statetime);

		/**
		 * 绘图世界
		 */
		//设置相机投影矩阵锚点位置
		mCamera.position.set(mMonkey.getPosition().x * Constant.RATE + MyGdxGame.VIEW_WIDTH / 4,
				MyGdxGame.VIEW_HEIGHT / 2, 0);
		//调整游戏相机
		adjustCamera();
		mCamera.update();

		//设置绘图矩阵
		mBatch.setProjectionMatrix(mUICamera.combined);
		//画背景
		mBackground.render(mBatch);

		//画地图
		mOrthogonalTiledMapRenderer.setView(mCamera);
		mOrthogonalTiledMapRenderer.render();

		//画孙悟空
		mBatch.setProjectionMatrix(mCamera.combined);
		mMonkey.render(mBatch, statetime);

		/**
		 * 画舞台
		 */
		mStage.act();
		mStage.draw();

		/**
		 * 物理世界
		 */
		mBox2DCamera.position.set(mMonkey.getPosition().x + MyGdxGame.VIEW_WIDTH / 4 / Constant.RATE,
				MyGdxGame.VIEW_HEIGHT / 2 / Constant.RATE, 0);
		//调整2D相机
		adjustBox2DCamera();
		mBox2DCamera.update();
		//渲染物理世界
		mBox2DRender.render(mWorld, mBox2DCamera.combined);
	}

	/**
	 * 调整游戏相机
	 */
	private void adjustCamera() {
		//当相机锚点x坐标小于相机视距一半时，不再移动相机
		if (mCamera.position.x < mCamera.viewportWidth / 2) {
			mCamera.position.x = mCamera.viewportWidth / 2;
		}

		//当相机锚点x坐标大于地图宽度时，不再移动相机
		if (mCamera.position.x > (tileWidth * tileSize - mCamera.viewportWidth / 2)) {
			mCamera.position.x = tileWidth * tileSize - mCamera.viewportWidth / 2;
		}
	}


	/**
	 * 调整物理世界渲染相机
	 */
	private void adjustBox2DCamera() {
		//最小情况 当物理世界相机锚点x坐标小于相机视距一半时，不再移动相机
		if (mBox2DCamera.position.x < mBox2DCamera.viewportWidth / 2) {
			mBox2DCamera.position.x = mBox2DCamera.viewportWidth / 2;
		}

		//最大情况 当物理相机锚点x坐标大于地图宽度时，不再移动相机
		if (mBox2DCamera.position.x > (tileWidth / Constant.RATE * tileSize - mBox2DCamera.viewportWidth / 2)) {
			mBox2DCamera.position.x = tileWidth / Constant.RATE * tileSize - mBox2DCamera.viewportWidth / 2;
		}
	}

	@Override
	public void handleInput() {
		if (Gdx.input.justTouched()) {
			mBody.applyForceToCenter(0, 300, true);
		}
	}

	@Override
	public void dispose() {

		//清空演员
		mStage.getActors().clear();
		//清空舞台
		mStage.clear();
	}
}
