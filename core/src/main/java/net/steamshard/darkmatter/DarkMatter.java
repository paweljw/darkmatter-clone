package net.steamshard.darkmatter;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DarkMatter extends Game {
	@Override
	public void create() {
		setScreen(new FirstScreen());
	}
}