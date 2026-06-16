package gui;

import util.Position;

public class Viewport {
    private int screenWidth;
    private int screenHeight;
    private final int trackingMargin;
    private final Position cameraOffset = new Position(0, 0);

    public Viewport(int screenWidth, int screenHeight, int trackingMargin) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.trackingMargin = trackingMargin;
    }

    public void updateScreenDimensions(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void updateCameraFocus(Position focusPoint, int worldMaxW, int worldMaxH) {
        if (focusPoint == null) return;

        // --- Horizontal Tracking Pipeline ---
        if (worldMaxW >= screenWidth) {
            int localizedTargetX = focusPoint.x - cameraOffset.x;
            if (localizedTargetX < trackingMargin) {
                cameraOffset.x = focusPoint.x - trackingMargin;
            } else if (localizedTargetX >= screenWidth - trackingMargin) {
                cameraOffset.x = focusPoint.x - (screenWidth - 1 - trackingMargin);
            }
            cameraOffset.x = Math.max(0, Math.min(cameraOffset.x, worldMaxW - screenWidth));
        } else {
            cameraOffset.x = -(screenWidth - worldMaxW) >> 1; // Optimized bitwise division by 2
        }

        // --- Vertical Tracking Pipeline ---
        if (worldMaxH >= screenHeight) {
            int localizedTargetY = focusPoint.y - cameraOffset.y;
            if (localizedTargetY < trackingMargin) {
                cameraOffset.y = focusPoint.y - trackingMargin;
            } else if (localizedTargetY >= screenHeight - trackingMargin) {
                cameraOffset.y = focusPoint.y - (screenHeight - 1 - trackingMargin);
            }
            cameraOffset.y = Math.max(0, Math.min(cameraOffset.y, worldMaxH - screenHeight));
        } else {
            cameraOffset.y = -(screenHeight - worldMaxH) >> 1; // Optimized bitwise division by 2
        }
    }

    public Position toWorldPosition(int screenX, int screenY) {
        return new Position(cameraOffset.x + screenX, cameraOffset.y + screenY);
    }

    public Position toScreenPosition(int worldX, int worldY) {
        return new Position(worldX - cameraOffset.x, worldY - cameraOffset.y);
    }

    public int getOffsetX() { return cameraOffset.x; }
    public int getOffsetY() { return cameraOffset.y; }
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}