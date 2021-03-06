package com.tcg.superstardefender;

import java.net.URL;
import java.util.Scanner;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.tcg.superstardefender.MyConstants.States;
import com.tcg.superstardefender.managers.*;

public class Game extends ApplicationAdapter {
	
	public static DatabaseManager DBM;
	
	public static final String TITLE = "Super Star Defender";

	private static int frames;
	private static float ftime;
	public static int fps;
	
	public static boolean ANDROID;
	
	public static Vector2 SIZE, CENTER;
	
	public static float VOLUME;
	
	public static Content res;
	
	public static String NEWS;
	
	private GameStateManager gsm;
	
	public static int[] highscore;
	
	public static int level;
	
	public static int levelsUnlocked;
	
	public static boolean LOGGED_IN;
	
	public static String username, password;
	
	public static int USER_ID;
	
	final static Save defaultSave = new Save(new int[] {0, 0, 0, 0}, 1);
	
	public final static String[] levelNames = new String[] {
			"Main Hanger", "Storage", "Hallway", "Control Room"
	};
	
	private static Save save;
	
	public Game(DatabaseManager db) {
		System.out.println("constructor");
		Game.DBM = db;
	}
	
	@Override
	public void create () {
		System.out.println("create");
		
		Game.LOGGED_IN = false;
		
		Game.load();
		
		boolean p = true;
		try {
			URL url = new URL("https://tinycountrygames.com/news.txt");
			Scanner s = new Scanner(url.openStream());
			Game.NEWS = s.nextLine();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
			p = false;
		}
		
		if(p == false) {
			Game.NEWS = "Unable to get News";
		}

		System.out.println(Game.NEWS);
		
		Game.NEWS = "News: " + Game.NEWS;

		ANDROID = Gdx.app.getType() == ApplicationType.Android;
		
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		Game.SIZE = new Vector2();
		Game.CENTER = new Vector2();
		Game.SIZE.set(width, height);
		Game.CENTER.set(width * .5f, height * .5f);
		
		res = new Content();

		res.loadMusic("music", "splash.mp3", "splash", false);
		res.loadMusic("music", "title.mp3", "title", true);
		res.loadMusic("music", "credits.mp3", "credits", true);
		res.loadMusic("music", "levelselect.mp3", "levelselect", true);
		res.loadMusic("music", "gameover.mp3", "gameover", false);
		res.loadMusic("music", "victory.mp3", "victory", false);

		res.loadMusic("music", "level0.mp3", "level0", true);
		res.loadMusic("music", "level1.mp3", "level1", true);
		res.loadMusic("music", "level2.mp3", "level2", true);
		res.loadMusic("music", "level3.mp3", "level3", true);

		res.loadSound("sound", "buzzer.mp3", "buzzer");
		res.loadSound("sound", "jump.mp3", "jump");
		res.loadSound("sound", "select.mp3", "select");
		res.loadSound("sound", "shoot.mp3", "shoot");
		res.loadSound("sound", "death.mp3", "death");
		
		res.loadBitmapFont("font", "nasalization_rg.ttf", "small", 12, Color.WHITE);
		res.loadBitmapFont("font", "nasalization_rg.ttf", "large", 56, Color.WHITE);
		res.loadBitmapFont("font", "nasalization_rg.ttf", "mItems", 42, Color.WHITE);
		res.loadBitmapFont("font", "nasalization_rg.ttf", "main", 24, Color.WHITE);
		
		if(Game.ANDROID) Game.VOLUME = 1f;
		
		res.setVolumeAll(Game.VOLUME); 
		
		gsm = new GameStateManager(States.SPLASH);
		
		Gdx.input.setInputProcessor(new MyInputProcessor());
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCursorCatched(Gdx.graphics.isFullscreen());
		Controllers.addListener(new MyControllerProcessor());
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float dt = Gdx.graphics.getDeltaTime();
		
		gsm.handleInput();
		gsm.update(dt);
		gsm.draw(dt);
		
		ftime += dt;
		frames++;
		if(ftime > 1) {
			fps = frames;
			ftime = 0;
			frames = 0;
		}
		
		Gdx.graphics.setTitle(Game.TITLE + " | " + Game.fps + " fps");
		
		MyInput.update();
	}

	@Override
	public void resize(int width, int height) {
		Game.SIZE.set(width, height);
		Game.CENTER.set(width * .5f, height * .5f);
		gsm.resize(width, height);
	}

	@Override
	public void pause() {
		Game.save();
	}

	@Override
	public void resume() {
		Game.load();
	}

	@Override
	public void dispose() {
		gsm.dispose();
		res.removeAll();
	}
	
	public static void load() {
		if (Gdx.files.local(MyConstants.saveFile).exists()) {
			try {
				save = Save.load();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			resetDefaultSave();
		}
		Game.highscore = save.getHighscore();
		Game.levelsUnlocked = save.getNumLevelsUnlocked();
	}
	
	public static void resetDefaultSave() {
		save = new Save(defaultSave);
		Game.highscore = save.getHighscore();
		Game.levelsUnlocked = save.getNumLevelsUnlocked();
		Game.save();
	}
	
	public static void save() {
		save.setHighscore(Game.highscore);
		save.setNumLevelsUnlocked(Game.levelsUnlocked);
		try {
			save.save(save);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
