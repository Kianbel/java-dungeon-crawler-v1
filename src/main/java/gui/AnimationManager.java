package gui;

import entity.Entity;
import gui.dataclass.RenderOffset;
import gui.dataclass.TextPopupData;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import util.ANIMATION_CURVE;
import util.Position;
import java.util.*;

public class AnimationManager {
    private final Map<Entity, RenderOffset> entityAnimationPixelDrawOffsets = new HashMap<>();
    private final List<TextPopupData> textPopupDataList = new ArrayList<>();
    private Rectangle currentFlashOverlay = null;

    private final StackPane canvasContainer;
    private final Canvas canvas;
    private final Runnable renderPipelineUpdater;

    public AnimationManager(StackPane canvasContainer, Canvas canvas, Runnable renderPipelineUpdater) {
        this.canvasContainer = canvasContainer;
        this.canvas = canvas;
        this.renderPipelineUpdater = renderPipelineUpdater;
    }

    public Map<Entity, RenderOffset> getEntityOffsets() { return entityAnimationPixelDrawOffsets; }
    public List<TextPopupData> getTextPopups() { return textPopupDataList; }

    public void triggerTextPopup(TextPopupData textPopupData, double durationMs) {
        textPopupDataList.add(textPopupData);
        final double MAX_FLOAT_DISTANCE_PIXELS = -30.0;

        new AnimationTimer() {
            private long startTime = -1;

            @Override
            public void handle(long now) {
                if (startTime < 0) startTime = now;

                double elapsedMs = (now - startTime) / 1_000_000.0;
                double progress = Math.min(1.0, elapsedMs / durationMs);

                textPopupData.opacity = Math.clamp(1.0 - progress, 0.0, 1.0);
                textPopupData.pixelOffsetY = progress * MAX_FLOAT_DISTANCE_PIXELS;

                renderPipelineUpdater.run();

                if (progress >= 1.0) {
                    textPopupDataList.remove(textPopupData);
                    renderPipelineUpdater.run();
                    this.stop();
                }
            }
        }.start();
    }

    public void triggerScreenFadeSequence(Color color, double fadeInMs, double holdMs, double fadeOutMs, Runnable midAction, Runnable postAction) {
        if (currentFlashOverlay != null) {
            canvasContainer.getChildren().remove(currentFlashOverlay);
        }

        currentFlashOverlay = new Rectangle();
        currentFlashOverlay.widthProperty().bind(canvasContainer.widthProperty());
        currentFlashOverlay.heightProperty().bind(canvasContainer.heightProperty());
        currentFlashOverlay.setFill(color);
        currentFlashOverlay.setOpacity(fadeInMs > 0 ? 0.0 : 1.0);
        currentFlashOverlay.setMouseTransparent(true);
        currentFlashOverlay.setManaged(false);

        canvasContainer.getChildren().add(currentFlashOverlay);

        new AnimationTimer() {
            private long startTime = -1;
            private boolean midActionExecuted = false;

            @Override
            public void handle(long now) {
                if (startTime < 0) startTime = now;
                double elapsedMs = (now - startTime) / 1_000_000.0;

                if (elapsedMs < fadeInMs) {
                    currentFlashOverlay.setOpacity(elapsedMs / fadeInMs);
                } else if (elapsedMs < (fadeInMs + holdMs)) {
                    currentFlashOverlay.setOpacity(1.0);
                    if (!midActionExecuted) {
                        midActionExecuted = true;
                        if (midAction != null) midAction.run();
                    }
                } else if (fadeOutMs > 0 && elapsedMs < (fadeInMs + holdMs + fadeOutMs)) {
                    if (!midActionExecuted) {
                        midActionExecuted = true;
                        if (midAction != null) midAction.run();
                    }
                    double progress = (elapsedMs - (fadeInMs + holdMs)) / fadeOutMs;
                    currentFlashOverlay.setOpacity(Math.clamp(1.0 - progress, 0.0, 1.0));
                } else {
                    if (!midActionExecuted) {
                        midActionExecuted = true;
                        if (midAction != null) midAction.run();
                    }
                    if (fadeOutMs > 0) {
                        canvasContainer.getChildren().remove(currentFlashOverlay);
                        currentFlashOverlay = null;
                    }
                    if (postAction != null) postAction.run();
                    renderPipelineUpdater.run();
                    this.stop();
                }
            }
        }.start();
    }

    public void clearScreenEffect() {
        if (currentFlashOverlay != null) {
            canvasContainer.getChildren().remove(currentFlashOverlay);
            currentFlashOverlay = null;
            renderPipelineUpdater.run();
        }
    }

    public void triggerEntitySlide(Entity entity, Position targetPosition, double slideMultiplier, double durationMs, ANIMATION_CURVE curve, double tileSize, int gridCols, boolean isReverse) {
        int dx = targetPosition.x - entity.position.x;
        int dy = targetPosition.y - entity.position.y;

        double targetPixelX = (gridCols > 0)
                ? dx * (canvas.getWidth() / gridCols) * slideMultiplier
                : dx * tileSize * slideMultiplier;
        double targetPixelY = dy * tileSize * slideMultiplier;

        Timeline timeline = new Timeline();
        final int TOTAL_FRAMES = 10;

        for (int i = 0; i <= TOTAL_FRAMES; i++) {
            final int frame = i;
            KeyFrame keyframe = new KeyFrame(
                    Duration.millis(durationMs / TOTAL_FRAMES * frame),
                    event -> {
                        double progress = (double) frame / TOTAL_FRAMES;
                        double computedCurve;
                        switch (curve) {
                            case TRIANGLE -> computedCurve = 1.0 - Math.abs(2.0 * progress - 1.0);
                            case EASE_OUT -> computedCurve = 1.0 - (1 - progress) * (1 - progress);
                            default -> computedCurve = progress;
                        }
                        if (isReverse) computedCurve = Math.abs(computedCurve - 1.0);

                        if (frame == TOTAL_FRAMES) {
                            entityAnimationPixelDrawOffsets.remove(entity);
                        } else {
                            entityAnimationPixelDrawOffsets.put(entity, new RenderOffset(targetPixelX * computedCurve, targetPixelY * computedCurve));
                        }
                        renderPipelineUpdater.run();
                    }
            );
            timeline.getKeyFrames().add(keyframe);
        }
        timeline.play();
    }

    public void clearAllAnimations() {
        textPopupDataList.clear();
        entityAnimationPixelDrawOffsets.clear();
    }
}