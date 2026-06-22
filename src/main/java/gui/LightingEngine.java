package gui;

import util.TILE;
import world.InteractableTile;
import java.util.Arrays;

public class LightingEngine {
    public enum LIGHT_LEVEL {
        ILLUMINATED,
        DIM,
        DIMMER,
        DIMMEST,
        PURE_DARKNESS,
    }

    private double[][] lightGridCache;

    public void setupLightGridCache(int width, int height) {
        if (lightGridCache == null || lightGridCache.length != height || lightGridCache[0].length != width) {
            lightGridCache = new double[height][width];
        }
        for(double[] row : lightGridCache) {
            Arrays.fill(row, 0.0);
        }
    }

    public double getLightLevel(int x, int y) {
        if (lightGridCache == null || y < 0 || y >= lightGridCache.length || x < 0 || x >= lightGridCache[0].length) {
            return 0.0;
        }
        return lightGridCache[y][x];
    }

    public void blitLightSource(int sourceX, int sourceY, int brightRange, TILE[][] roomLayout, int roomWidth, int roomHeight, InteractableTile[][] interactableGridCache) {
        final int dimRange = 5;
        final int maximumRange = brightRange + dimRange;

        final int startX = Math.max(0, sourceX - maximumRange);
        final int endX = Math.min(roomWidth - 1, sourceX + maximumRange);
        final int startY = Math.max(0, sourceY - maximumRange);
        final int endY = Math.min(roomHeight - 1, sourceY + maximumRange);

        // Snapshot grid to store pure direct light before any ambient blurring
        double[][] localDirectLight = new double[roomHeight][roomWidth];

        // ==========================================
        // PASS 1: Calculate Direct Line-of-Sight Light
        // ==========================================
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                final int dx = x - sourceX;
                final int dy = y - sourceY;
                final double distanceSquared = (dx * dx) + (dy * dy);

                if (distanceSquared <= (maximumRange * maximumRange)) {
                    if (isPathClear(sourceX, sourceY, x, y, roomLayout, interactableGridCache)) {
                        final double actualDistance = Math.sqrt(distanceSquared);
                        double lightBrightnessPercent;

                        if (actualDistance <= brightRange) {
                            lightBrightnessPercent = 1.0;
                        } else {
                            double fadingDistance = actualDistance - brightRange;
                            lightBrightnessPercent = Math.clamp(1.0 - fadingDistance / dimRange, 0.0, 1.0);
                        }

                        localDirectLight[y][x] = lightBrightnessPercent;
                    }
                }
            }
        }

        // ==========================================
        // PASS 2: Calculate Ambient Spill & Apply Occlusion
        // ==========================================
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                final int dx = x - sourceX;
                final int dy = y - sourceY;
                final double distanceSquared = (dx * dx) + (dy * dy);

                if (distanceSquared > (maximumRange * maximumRange)) continue;

                double finalTileLight = localDirectLight[y][x];

                // If this tile is in shadow, check its neighbors for light bleeding
                if (finalTileLight == 0.0) {
                    double neighborBrightnessSum = 0.0;
                    int lightedNeighbors = 0;

                    // Look UP
                    if (y - 1 >= startY) {
                        TILE neighborTile = roomLayout[y - 1][x];
                        boolean isInteractableBlocking = interactableGridCache[y - 1][x] != null && interactableGridCache[y - 1][x].isLightOccluding;
                        boolean isTileBlocking = neighborTile != null && neighborTile.isLightOccluding();

                        if (!isTileBlocking && !isInteractableBlocking && localDirectLight[y - 1][x] > 0.0) {
                            neighborBrightnessSum += localDirectLight[y - 1][x];
                            lightedNeighbors++;
                        }
                    }

                    // Look DOWN
                    if (y + 1 <= endY) {
                        TILE neighborTile = roomLayout[y + 1][x];
                        boolean isInteractableBlocking = interactableGridCache[y + 1][x] != null && interactableGridCache[y + 1][x].isLightOccluding;
                        boolean isTileBlocking = neighborTile != null && neighborTile.isLightOccluding();

                        if (!isTileBlocking && !isInteractableBlocking && localDirectLight[y + 1][x] > 0.0) {
                            neighborBrightnessSum += localDirectLight[y + 1][x];
                            lightedNeighbors++;
                        }
                    }

                    // Look LEFT
                    if (x - 1 >= startX) {
                        TILE neighborTile = roomLayout[y][x - 1];
                        boolean isInteractableBlocking = interactableGridCache[y][x - 1] != null && interactableGridCache[y][x - 1].isLightOccluding;
                        boolean isTileBlocking = neighborTile != null && neighborTile.isLightOccluding();

                        if (!isTileBlocking && !isInteractableBlocking && localDirectLight[y][x - 1] > 0.0) {
                            neighborBrightnessSum += localDirectLight[y][x - 1];
                            lightedNeighbors++;
                        }
                    }

                    // Look RIGHT
                    if (x + 1 <= endX) {
                        TILE neighborTile = roomLayout[y][x + 1];
                        boolean isInteractableBlocking = interactableGridCache[y][x + 1] != null && interactableGridCache[y][x + 1].isLightOccluding;
                        boolean isTileBlocking = neighborTile != null && neighborTile.isLightOccluding();

                        if (!isTileBlocking && !isInteractableBlocking && localDirectLight[y][x + 1] > 0.0) {
                            neighborBrightnessSum += localDirectLight[y][x + 1];
                            lightedNeighbors++;
                        }
                    }

                    if (lightedNeighbors > 0) {
                        finalTileLight = (neighborBrightnessSum / lightedNeighbors) * 0.4;
                    }
                }

                // Write the final balanced lighting value into your primary grid cache
                lightGridCache[y][x] = Math.clamp(Math.max(lightGridCache[y][x], finalTileLight), 0.0, 1.0);
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