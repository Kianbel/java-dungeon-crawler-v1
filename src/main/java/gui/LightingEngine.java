package gui;

import util.TILE;
import world.InteractableTile;
import java.util.Arrays;

public class LightingEngine {
    public enum LIGHT_LEVEL {
        ILLUMINATED,
        DIM,
        PURE_DARKNESS,
    }

    private LIGHT_LEVEL[][] lightGridCache;

    public void synchronizeCache(int width, int height) {
        if (lightGridCache == null || lightGridCache.length != height || lightGridCache[0].length != width) {
            lightGridCache = new LIGHT_LEVEL[height][width];
        }
        for (int row = 0; row < height; row++) {
            Arrays.fill(lightGridCache[row], LIGHT_LEVEL.PURE_DARKNESS);
        }
    }

    public LIGHT_LEVEL getLightLevel(int x, int y) {
        if (lightGridCache == null || y < 0 || y >= lightGridCache.length || x < 0 || x >= lightGridCache[0].length) {
            return LIGHT_LEVEL.PURE_DARKNESS;
        }
        return lightGridCache[y][x];
    }

    public void blitLightSource(int sourceX, int sourceY, int brightRange, TILE[][] roomLayout, int roomWidth, int roomHeight, InteractableTile[][] interactableGridCache) {
        final int dimRange = 2;
        final int maximumRange = brightRange + dimRange;

        int startX = Math.max(0, sourceX - maximumRange);
        int endX = Math.min(roomWidth - 1, sourceX + maximumRange);
        int startY = Math.max(0, sourceY - maximumRange);
        int endY = Math.min(roomHeight - 1, sourceY + maximumRange);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (lightGridCache[y][x] == LIGHT_LEVEL.ILLUMINATED) continue;

                int dx = x - sourceX;
                int dy = y - sourceY;
                double distanceSquared = (dx * dx) + (dy * dy);

                if (distanceSquared <= (maximumRange * maximumRange)) {
                    if (isPathClear(sourceX, sourceY, x, y, roomLayout, interactableGridCache)) {
                        LIGHT_LEVEL generatedLevel = (distanceSquared <= (brightRange * brightRange)) ? LIGHT_LEVEL.ILLUMINATED : LIGHT_LEVEL.DIM;

                        if (generatedLevel == LIGHT_LEVEL.ILLUMINATED || lightGridCache[y][x] == LIGHT_LEVEL.PURE_DARKNESS) {
                            lightGridCache[y][x] = generatedLevel;
                        }
                    }
                }
            }
        }
    }

    public boolean isPathClear(int startX, int startY, int targetX, int targetY, TILE[][] roomLayout, InteractableTile[][] interactableGridCache) {
        if (startX == targetX && startY == targetY) return true;

        int currentX = startX;
        int currentY = startY;

        final int deltaX = Math.abs(targetX - currentX);
        final int deltaY = Math.abs(targetY - currentY);
        final int stepX = currentX < targetX ? 1 : -1;
        final int stepY = currentY < targetY ? 1 : -1;
        int errorValue = deltaX - deltaY;

        while (true) {
            if (currentX != startX || currentY != startY) {
                if (currentX == targetX && currentY == targetY) break;

                if (currentY >= 0 && currentY < roomLayout.length && currentX >= 0 && currentX < roomLayout[currentY].length) {
                    TILE tile = roomLayout[currentY][currentX];
                    if (tile.isLightOccluding()) return false;

                    InteractableTile interactableTile = interactableGridCache[currentY][currentX];
                    if (interactableTile != null && interactableTile.isLightOccluding) return false;
                }
            }

            if (currentX == targetX && currentY == targetY) break;

            int errorDoubleAdjustment = 2 * errorValue;
            if (errorDoubleAdjustment > -deltaY) {
                errorValue -= deltaY;
                currentX += stepX;
            }
            if (errorDoubleAdjustment < deltaX) {
                errorValue += deltaX;
                currentY += stepY;
            }
        }
        return true;
    }
}