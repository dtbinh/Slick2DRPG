package org.bakkes.game;

import java.util.ArrayList;

import org.bakkes.game.entity.Player;
import org.bakkes.game.events.GameKeyListener;
import org.bakkes.game.map.LayerBasedMap;
import org.bakkes.game.math.GridGraphicTranslator;
import org.bakkes.game.math.Vector2;
import org.bakkes.game.scripting.ScriptManager;
import org.bakkes.game.ui.DrawableGameComponent;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;

public class Game extends BasicGame implements KeyListener {

	private ArrayList<DrawableGameComponent> drawables;
	private ArrayList<GameKeyListener> keyListeners;
	private Player player;
	private Camera camera;
	private Vector2 destinationTile;
	public Game(String title) {
		super(title);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		player = new Player();
		player.init(gc);
		camera = new Camera(gc, World.getWorld().getMap());
		
		drawables = new ArrayList<DrawableGameComponent>();
		keyListeners = new ArrayList<GameKeyListener>();
		
		ScriptManager.loadScripts();
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		
		Input input = gc.getInput();
		
		if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			Vector2 mousePos = new Vector2(input.getMouseX(), input.getMouseY());
			mousePos = new Vector2(mousePos.getX() + camera.cameraX, mousePos.getY() + camera.cameraY);
			destinationTile = GridGraphicTranslator.PixelsToGrid(mousePos);
			
			if(!World.getWorld().getLayerMap().blocked(null, (int)destinationTile.getX(), (int)destinationTile.getY())) {
				player.MoveTo(destinationTile);
			} else {
				destinationTile = null;
			}
		}
		
		player.update(gc, delta);
	}
	
	public void render(GameContainer gc, Graphics g) throws SlickException {
		Input input = gc.getInput();
		Vector2 mousePos = new Vector2(input.getMouseX(), input.getMouseY());
		Vector2 paintPos = GridGraphicTranslator.PixelsToGridPixels(mousePos);
		
		camera.centerOn((int)player.getPixelPosition().getX(), (int)player.getPixelPosition().getY());
		camera.drawMap();
		
		camera.translateGraphics();
		if(destinationTile != null && destinationTile != player.getGridPosition()) {
			g.setColor(new Color(0, 0, 255, 64));
			g.fillRect(destinationTile.getX() * 16, destinationTile.getY() * 16, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
		}
		player.render(gc, g);
		camera.untranslateGraphics();
		
		g.setColor(Color.black);
		g.drawRect(paintPos.getX(), paintPos.getY(), Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
		for(DrawableGameComponent drawable : drawables) {
			drawable.Render(gc, g);
		}
	}
	
	@Override
    public void keyPressed(int key, char c) {
		for(GameKeyListener listener : keyListeners) {
			listener.KeyDown(key, c);
		}
    }

    @Override
    public void keyReleased(int key, char c) {
		for(GameKeyListener listener : keyListeners) {
			listener.KeyUp(key, c);
		}
    }
	
	public void addComponent(DrawableGameComponent gameComponent) {
		this.drawables.add(gameComponent);
	}
	
	public void addKeyListener(GameKeyListener keylistener) {
		this.keyListeners.add(keylistener);
	}
	
	public void removeComponent(DrawableGameComponent gameComponent) {
		this.drawables.remove(gameComponent);
	}
	
	public void removeKeyListener(GameKeyListener keylistener) {
		this.keyListeners.remove(keylistener);
	}

}
