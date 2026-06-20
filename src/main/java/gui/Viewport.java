package gui;

import util.Position;

public class Viewport {
    private int screenWidth;
    private int screenHeight;
    private final Position cameraOffset = new Position(0, 0);

    public Viewport(int screenWidth, int screenHeight, int trackingMargin) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        // trackingMargin is preserved to prevent breaking existing code instantiation instantiations
    }

    public void updateScreenDimensions(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    /**
     * Instantly centers the camera on the focus point, securely clamped within room boundaries.
     */
    public void updateCameraFocus(Position focusPoint, int worldMaxW, int worldMaxH) {
        if (focusPoint == null) return;

        // --- Horizontal Centering Logic ---
        if (worldMaxW >= screenWidth) {
            // Instantly offset the camera so the focus point lands dead center
            cameraOffset.x = focusPoint.x - (screenWidth / 2);
            // Secure bounds clamp
            cameraOffset.x = Math.max(0, Math.min(cameraOffset.x, worldMaxW - screenWidth));
        } else {
            // Center smaller rooms horizontally on the canvas screen
            cameraOffset.x = -(screenWidth - worldMaxW) >> 1;
        }

        // --- Vertical Centering Logic ---
        if (worldMaxH >= screenHeight) {
            // Instantly offset the camera so the focus point lands dead center
            cameraOffset.y = focusPoint.y - (screenHeight / 2);
            // Secure bounds clamp
            cameraOffset.y = Math.max(0, Math.min(cameraOffset.y, worldMaxH - screenHeight));
        } else {
            // Center smaller rooms vertically on the canvas screen
            cameraOffset.y = -(screenHeight - worldMaxH) >> 1;
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