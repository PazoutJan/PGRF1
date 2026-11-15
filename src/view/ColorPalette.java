package view;

public class ColorPalette {
    private static final int[] COLORS = {
            0x000000,
            0xFFFFFF,
            0xFF0000,
            0x00FF00,
            0x0000FF,
            0xFFFF00,
            0xFF00FF,
            0x00FFFF,
            0x000000
    };

    public int getColor(int index) {
        if (index < 0 || index >= COLORS.length) {
            throw new IllegalArgumentException(
                    "Unknown color index: " + index
            );
        }

        return COLORS[index];
    }
}