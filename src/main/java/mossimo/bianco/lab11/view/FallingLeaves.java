package mossimo.bianco.lab11.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

// Decorative falling-leaves animation drawn over a window.
// Motion idea adapted from https://github.com/ernkerr/falling-leaves (MIT license).
public class FallingLeaves extends Canvas {

    private static final int LEAF_COUNT = 25;
    private static final double MAX_FRAME_STEP = 3;

    // Autumn colors for maple, oak and oval leaves.
    private static final Color[] AUTUMN_COLORS = {
        Color.web("#d9480f"), Color.web("#e8590c"), Color.web("#f08c00"),
        Color.web("#c92a2a"), Color.web("#a61e4d"), Color.web("#b5651d"),
        Color.web("#e0a800")
    };

    // Gold colors for ginkgo leaves.
    private static final Color[] GINKGO_COLORS = {
        Color.web("#e0a800"), Color.web("#f2c12e"),
        Color.web("#d99a00"), Color.web("#c9a227")
    };

    private enum Shape { MAPLE, OAK, OVAL, GINKGO }

    // One leaf and its own random motion values.
    private static class Leaf {
        double x, y, size;
        double speed, wind, sway, rotationSpeed;
        double swayFreq, flipFreq;
        double rotation, swayPhase, flipPhase;
        Shape shape;
        Color color;
    }

    private final Random random = new Random();
    private final List<Leaf> leaves = new ArrayList<>();
    private long lastTime;
    private boolean wasShowing;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            // Wait until the window is showing, and stop once it is closed.
            Scene scene = getScene();
            boolean showing = scene != null && scene.getWindow() != null
                    && scene.getWindow().isShowing();
            if (!showing) {
                if (wasShowing) {
                    stop();
                }
                return;
            }
            wasShowing = true;

            // Measure time in 60 fps frames so speed is the same on any screen.
            double dt = lastTime == 0
                    ? 1
                    : Math.min((now - lastTime) / 16_666_667.0, MAX_FRAME_STEP);
            lastTime = now;

            for (Leaf leaf : leaves) {
                step(leaf, dt);
            }
            draw();
        }
    };

    // Add the leaves on top of a scene's window, once per scene.
    public static void addTo(Scene scene) {
        if (!(scene.getRoot() instanceof Pane)) {
            return;
        }
        Pane root = (Pane) scene.getRoot();
        for (Object child : root.getChildren()) {
            if (child instanceof FallingLeaves) {
                return;
            }
        }

        FallingLeaves canvas = new FallingLeaves();

        // Float over the layout without taking up space or blocking clicks.
        canvas.setManaged(false);
        canvas.setMouseTransparent(true);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        root.getChildren().add(canvas);
        canvas.timer.start();
    }

    private FallingLeaves() {
        for (int i = 0; i < LEAF_COUNT; i++) {
            Leaf leaf = new Leaf();
            reset(leaf);
            // Spread the first leaves over the whole window height.
            leaf.y = random.nextDouble() * 900 - 300;
            leaves.add(leaf);
        }
    }

    // Give a leaf new random values and place it above the window.
    private void reset(Leaf leaf) {
        leaf.shape = Shape.values()[random.nextInt(Shape.values().length)];
        Color[] palette = leaf.shape == Shape.GINKGO ? GINKGO_COLORS : AUTUMN_COLORS;
        leaf.color = palette[random.nextInt(palette.length)];

        leaf.size = range(14, 30);
        leaf.speed = range(0.6, 1.5);
        leaf.wind = range(-0.3, 0.8);
        leaf.sway = range(0.4, 1.4);
        leaf.rotationSpeed = range(-1.5, 1.5);
        leaf.swayFreq = 0.01 + random.nextDouble() * 0.02;
        leaf.flipFreq = 0.02 + random.nextDouble() * 0.04;

        // Ginkgo leaves sway more and spin less.
        if (leaf.shape == Shape.GINKGO) {
            leaf.sway *= 1.4;
            leaf.rotationSpeed *= 0.4;
        }

        leaf.rotation = random.nextDouble() * 360;
        leaf.swayPhase = random.nextDouble() * Math.PI * 2;
        leaf.flipPhase = random.nextDouble() * Math.PI * 2;
        leaf.x = random.nextDouble() * Math.max(getWidth(), 600);
        leaf.y = -leaf.size - random.nextDouble() * 100;
    }

    // Move one leaf forward by one step.
    private void step(Leaf leaf, double dt) {
        leaf.swayPhase += leaf.swayFreq * dt;
        leaf.flipPhase += leaf.flipFreq * dt;

        double sway = Math.sin(leaf.swayPhase) * leaf.sway;
        leaf.x += (leaf.wind + sway) * dt;

        // Fall a little slower at the ends of each swing.
        double flutter = 0.6 + 0.4 * Math.abs(Math.cos(leaf.swayPhase));
        leaf.y += leaf.speed * flutter * dt;
        leaf.rotation += leaf.rotationSpeed * dt;

        double width = getWidth();
        if (leaf.y > getHeight() + leaf.size) {
            reset(leaf);
            leaf.x = random.nextDouble() * width;
        } else if (leaf.x > width + leaf.size) {
            leaf.x = -leaf.size;
        } else if (leaf.x < -leaf.size) {
            leaf.x = width + leaf.size;
        }
    }

    // Draw every leaf.
    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setGlobalAlpha(0.85);

        for (Leaf leaf : leaves) {
            double rock = leaf.shape == Shape.GINKGO ? 35 * Math.cos(leaf.swayPhase) : 0;
            double half = leaf.size / 2;

            gc.save();
            gc.translate(leaf.x, leaf.y);
            gc.rotate(leaf.rotation + rock);
            // Squash sideways to fake a 3D tumble.
            gc.scale(Math.cos(leaf.flipPhase) * half, half);

            gc.setFill(leaf.color);
            gc.beginPath();
            tracePath(gc, leaf.shape);
            gc.closePath();
            gc.fill();

            // Center vein.
            gc.setStroke(leaf.color.darker());
            gc.setLineWidth(0.07);
            gc.strokeLine(0, 1, 0, -0.75);

            gc.restore();
        }
    }

    // Trace a leaf outline in a box from -1 to 1 (tip at the top).
    private static void tracePath(GraphicsContext gc, Shape shape) {
        switch (shape) {
            case MAPLE:
                mirrored(gc, new double[][] {
                    {0, -1}, {0.15, -0.6}, {0.45, -0.78}, {0.38, -0.35},
                    {0.88, -0.45}, {0.7, -0.15}, {0.95, 0.05}, {0.55, 0.2},
                    {0.6, 0.45}, {0.2, 0.35}, {0.06, 0.75}, {0.03, 1}
                });
                break;
            case OAK:
                mirrored(gc, new double[][] {
                    {0, -1}, {0.3, -0.85}, {0.25, -0.6}, {0.55, -0.5},
                    {0.35, -0.25}, {0.62, -0.1}, {0.4, 0.15}, {0.55, 0.38},
                    {0.25, 0.5}, {0.1, 0.75}, {0.03, 1}
                });
                break;
            case OVAL:
                gc.moveTo(0, -1);
                gc.bezierCurveTo(0.75, -0.7, 0.75, 0.7, 0, 1);
                gc.bezierCurveTo(-0.75, 0.7, -0.75, -0.7, 0, -1);
                break;
            case GINKGO:
                gc.moveTo(0.04, 1);
                gc.lineTo(0.04, 0.3);
                gc.quadraticCurveTo(0.9, 0.0, 0.95, -0.45);
                gc.quadraticCurveTo(0.6, -0.95, 0.1, -0.85);
                gc.lineTo(0, -0.55);
                gc.lineTo(-0.1, -0.85);
                gc.quadraticCurveTo(-0.6, -0.95, -0.95, -0.45);
                gc.quadraticCurveTo(-0.9, 0.0, -0.04, 0.3);
                gc.lineTo(-0.04, 1);
                break;
        }
    }

    // Trace the right half of a leaf, then the same points flipped for the left.
    private static void mirrored(GraphicsContext gc, double[][] rightHalf) {
        gc.moveTo(rightHalf[0][0], rightHalf[0][1]);
        for (int i = 1; i < rightHalf.length; i++) {
            gc.lineTo(rightHalf[i][0], rightHalf[i][1]);
        }
        for (int i = rightHalf.length - 1; i >= 0; i--) {
            gc.lineTo(-rightHalf[i][0], rightHalf[i][1]);
        }
    }

    private double range(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}
