package com.mygdx.game;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.io.FileNotFoundException;

/**
 * Created by Varv on 15.12.2016.
 */

public class Main implements ApplicationListener {
    public PerspectiveCamera perspectiveCamera;
    public Model model;
    public CameraInputController cameraInputController;
    public Shader shader;
    public Renderable renderable;
    public RenderContext renderContext;
    public Box2DDebugRenderer box2DDebugRenderer;


    @Override
    public void create() {

        float viewOnY = 67;

        perspectiveCamera = new PerspectiveCamera(viewOnY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(10f, 10f, 10f);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();

        box2DDebugRenderer = new Box2DDebugRenderer();

        cameraInputController = new CameraInputController(perspectiveCamera);
        Gdx.input.setInputProcessor(cameraInputController);

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(10f, 10f, 10f, 20, 20, new Material(), Usage.Position | Usage.Normal | Usage.TextureCoordinates);

        NodePart nodePart = model.nodes.get(0).parts.get(0);
        renderable = new Renderable();
        nodePart.setRenderable(renderable);
        renderable.environment = null;
        renderable.worldTransform.idt();
        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

        loadShaders();

    }

    private void loadShaders() {

        String nameOfFirstFile = "vertex.glsl";
        String nameOfSecondFile = "fragmental.glsl";
        String vertex = Gdx.files.internal(nameOfFirstFile).readString();
        String fragmental = Gdx.files.internal(nameOfSecondFile).readString();

        Preferences preferences = createPreferences(nameOfFirstFile, nameOfSecondFile);

        if (!vertex.isEmpty() && vertex != null && !fragmental.isEmpty() && fragmental != null) {
            Gdx.app.log("LogSucces", "Wczytano: " + preferences.getString("nameOfFirst") + " " + preferences.getString("nameOfSecond"));
        }

        shader = new DefaultShader(renderable, new DefaultShader.Config(vertex, fragmental));
        shader.init();
    }

    private Preferences createPreferences(String nameOfFirstFile, String nameOfSecondFile) {
        Preferences preferences = Gdx.app.getPreferences("prefs");
        preferences.putString("nameOfFirst", nameOfFirstFile);
        preferences.putString("nameOfSecond", nameOfSecondFile);
        preferences.flush();
        return preferences;
    }

    @Override
    public void render() {
        cameraInputController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderContext.begin();
        shader.begin(perspectiveCamera, renderContext);
        shader.render(renderable);
        shader.end();
        renderContext.end();

    }

    @Override
    public void dispose() {
        shader.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}