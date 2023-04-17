package at.gotzi.minestrum.content.camera;

import at.gotzi.minestrum.Game;
import at.gotzi.minestrum.config.dynamic.CameraConfig;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class CameraHandler {

    @Getter
    public final List<Camera> cameras = new LinkedList<>();

    private final Game game;

    private final CameraConfig config;

    public CameraHandler(Game game, CameraConfig config) {
        this.game = game;
        this.config = config;
    }

    public void initCameras() {





    }














}
