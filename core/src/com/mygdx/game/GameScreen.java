package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
	final Drop game;
	SpriteBatch batch;
	Texture background;
	Texture playerImage;
	Array<Texture> objectClassTexture = new Array<>();
	Music music;
	OrthographicCamera camera;
	Rectangle player;
	Array<Raindrop> raindrops = new Array<>();
	long dropTime;
	int greenAppleCollected = 0;
	int cherriesCollected = 0;
	int orangesCollected = 0;
	int redAppleCollected = 0;
    int fruitsCollected = 0;
	long startTime;
	long finishTime;


	public GameScreen(final Drop kik) {
		this.game = kik;
		background = new Texture("pictures/background.png");
		playerImage = new Texture(Gdx.files.internal("pictures/sportacus.png"));
		objectClassTexture.add(new Texture(Gdx.files.internal("pictures/greenApple.png")));
		objectClassTexture.add(new Texture(Gdx.files.internal("pictures/cherry.png")));
		objectClassTexture.add(new Texture(Gdx.files.internal("pictures/orange.png")));
		objectClassTexture.add(new Texture(Gdx.files.internal("pictures/redApple.png")));
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
		music.setLooping(true);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
        spawnPlayer();
		spawnFruitdrops();
		startTime = System.currentTimeMillis();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(background, 0, 0);

        game.font.draw(game.batch, "Fruits collected: " + fruitsCollected, 0, 470);
        game.font.draw(game.batch, "GreenApples collected: " + greenAppleCollected, 0, 450);
		game.font.draw(game.batch, "Cherries collected: " + cherriesCollected, 0, 430);
		game.font.draw(game.batch, "Oranges collected: " + orangesCollected, 0, 410);
		game.font.draw(game.batch, "RedApples collected: " + redAppleCollected, 0, 390);

		game.batch.draw(playerImage, player.x, player.y);

		for(Raindrop raindrop: raindrops) {
			game.batch.draw(raindrop.texture, raindrop.x, raindrop.y);
		}

		game.batch.end();

		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			player.x = touchPos.x - 32;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			player.x -= 500 * Gdx.graphics.getDeltaTime();}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			player.x += 500 * Gdx.graphics.getDeltaTime();}

		if(player.x < 0){
			player.x = 0;}
		if(player.x > 800 - player.width){
			player.x = 800 - player.width;}

		if(TimeUtils.nanoTime() - dropTime > 1000000000){
			spawnFruitdrops();}

		Iterator<Raindrop> iterator = raindrops.iterator();
		while(iterator.hasNext()) {
			Raindrop raindrop = iterator.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0){
				iterator.remove();
			}
			if(raindrop.overlaps(player)) {
				switch (raindrop.type){
					case "greenApple":
						greenAppleCollected++;
						fruitsCollected++;
						break;
					case "cherry":
						cherriesCollected++;
						fruitsCollected++;
						break;
					case "orange":
						orangesCollected++;
						fruitsCollected++;
						break;
					case "redApple":
						redAppleCollected++;
						fruitsCollected++;
						break;
				}
				iterator.remove();
			}
		}
		finishTime = System.currentTimeMillis();
		if (finishTime - startTime > 62000){
			raindrops.clear();
			music.stop();
			objectClassTexture.clear();
			game.setScreen(new EndGameScreen(game, fruitsCollected));
		}
	}
	
	@Override
	public void dispose () {
		playerImage.dispose();
		music.dispose();
		batch.dispose();
		background.dispose();
	}

	private void spawnFruitdrops() {
		Raindrop raindrop = new Raindrop();
		raindrop.x = MathUtils.random(0, 800-70);
		raindrop.y = 550; // скорость падения
		raindrop.width = 64;
		raindrop.height = 64;
		Random random = new Random();
		int randomObjectTexture = random.nextInt(4);
		switch (randomObjectTexture){
			case 0:
				raindrop.texture = objectClassTexture.get(0);
				raindrop.type = "greenApple";
				break;
			case 1:
				raindrop.texture = objectClassTexture.get(1);
				raindrop.type = "cherry";
				break;
			case 2:
				raindrop.texture = objectClassTexture.get(2);
				raindrop.type = "orange";
				break;
			case 3:
				raindrop.texture = objectClassTexture.get(3);
				raindrop.type = "redApple";
				break;
		}
		raindrops.add(raindrop);
		dropTime = TimeUtils.nanoTime();
	}

    private void spawnPlayer() {
        player = new Rectangle();
        player.x = 340;
        player.y = 0;
        player.width = 140;
        player.height = 100;
    }

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		music.play();
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
}
