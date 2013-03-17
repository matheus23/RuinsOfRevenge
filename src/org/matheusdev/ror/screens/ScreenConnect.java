package org.matheusdev.ror.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import org.matheusdev.ror.ResourceLoader;
import org.matheusdev.ror.RuinsOfRevenge;

/**
 * Created with IntelliJ IDEA.
 * Author: matheusdev
 * Date: 3/13/13
 * Time: 4:36 PM
 */
public class ScreenConnect extends AbstractScreen {

    private final Table table;

    public ScreenConnect(final ResourceLoader res, final RuinsOfRevenge game) {
        super(new Stage(), game);

        Skin skin = res.getSkin("uiskin");

        final TextField ipField = new TextField("localhost", skin);
        final TextButton back = new TextButton("Back", skin);
        final TextButton connect = new TextButton("Connect", skin);

        ipField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if (key == Input.Keys.ENTER) {
                    game.popScreen();
                    game.pushScreen(new ScreenGameMap(res, game, textField.getText(), "data/maps/newmap/map004.tmx"));
                }
            }
        });
        back.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.popScreen();
            }
        });
        connect.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.popScreen();
                game.pushScreen(new ScreenGameMap(res, game, ipField.getText(), "data/maps/newmap/map004.tmx"));
            }
        });

        table = new Table(skin);

        table.add("Connect to IP: ").space(8);
        table.add(ipField).space(8);
        table.row();
        table.add(back).size(160, 64).space(8);
        table.add(connect).size(160, 64).space(8);
        table.row();

        table.setPosition(Gdx.graphics.getWidth()/ 2f, Gdx.graphics.getHeight() / 2f);

        stage.addActor(table);
    }
    @Override
    public void tick(float delta) {
        stage.act(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
        table.setPosition(width/2f, height/2f);
    }

    @Override
    public boolean isParentVisible() {
        return true;
    }
}
