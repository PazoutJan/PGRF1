package fill;

import model.Point2D;
import raster.Raster;

/**
 * Vyplňuje souvislou oblast pixelů od zadaného počátečního bodu.
 *
 * <p>Algoritmus funguje tak, že si nejdříve zapamatuje barvu pixelu pod
 * seedem. Potom navštěvuje jeho sousedy a obarvuje pouze pixely, které mají
 * stejnou původní barvu. Pixel jiné barvy je považován za hranici oblasti a
 * dále se přes něj nepokračuje.</p>
 *
 * <p>Je použito čtyř-sousedství: z každého pixelu se kontroluje pixel vlevo,
 * vpravo, nahoře a dole. Diagonální pixely proto nejsou součástí stejné
 * oblasti.</p>
 */
public class SeedFill implements Filler {
    /** Raster, do kterého se výplň zapisuje a ze kterého se čtou barvy. */
    private final Raster raster;

    /** Bod, od kterého začne výplň při zavolání fill() bez parametrů. */
    private Point2D seed;

    /** Barva výplně uložená ve formátu RGB, například 0xFF0000 pro červenou. */
    private int fillColor = 0xFF0000;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    /**
     * Spustí výplň od dříve nastaveného seedu.
     *
     * <p>Seed se nastavuje metodou {@link #setSeed(Point2D)}. Pokud nastavený
     * není, není k dispozici počáteční bod a metoda nemá co vyplňovat.</p>
     */
    @Override
    public void fill() {
        if (seed == null) {
            return;
        }
        fill(seed.getX(), seed.getY());
    }

    /**
     * Vyplní oblast, která obsahuje pixel na souřadnicích {@code (x, y)}.
     *
     * <p>Výplň je implementována rekurzivně. Po obarvení aktuálního pixelu se
     * metoda zavolá pro jeho čtyři přímé sousedy.</p>
     *
     * @param x vodorovná souřadnice počátečního pixelu
     * @param y svislá souřadnice počátečního pixelu
     */
    public void fill(int x, int y) {
        // Seed mimo raster nemůže označovat žádný pixel.
        if (!isInside(x, y)) {
            return;
        }

        // Všechny pixely oblasti musí mít stejnou původní barvu jako seed.
        int targetColor = raster.getPixel(x, y);

        // Pokud je cílová barva stejná jako barva výplně, není co měnit.
        if (targetColor == fillColor) {
            return;
        }

        fillRecursive(x, y, targetColor);
    }

    /**
     * Nastaví počáteční bod pro variantu {@link #fill()} bez parametrů.
     */
    public void setSeed(Point2D seed) {
        this.seed = seed;
        ;
    }

    /**
     * Nastaví barvu, kterou se budou pixely oblasti obarvovat.
     *
     * <p>Raster používá RGB barvy. Bitová maska odstraní případný alfa kanál,
     * pokud by volající předal například hodnotu ve formátu ARGB.</p>
     */
    @Override
    public void setFillColor(int color) {
        this.fillColor = color & 0xFFFFFF;
    }

    /**
     * Rekurzivně zpracuje jeden pixel a jeho čtyři přímé sousedy.
     *
     * <p>Kontrola barvy před obarvením je důležitá: zastaví rekurzi na hranici
     * oblasti a současně zabrání opakovanému zpracování již obarvených pixelů.</p>
     */
    private void fillRecursive(int x, int y, int targetColor) {
        // Rekurze skončí na okraji rastru nebo na pixelu mimo vyplňovanou oblast.
        if (!isInside(x, y) || raster.getPixel(x, y) != targetColor) {
            return;
        }

        // Pixel obarvíme před zpracováním sousedů, aby se rekurze nevracela
        // zpět do stejného pixelu přes některého z okolních pixelů.
        raster.setPixel(x, y, fillColor);

        // Rekurzivně zpracujeme všechny čtyři přímé sousedy.
        fillRecursive(x + 1, y, targetColor);
        fillRecursive(x - 1, y, targetColor);
        fillRecursive(x, y + 1, targetColor);
        fillRecursive(x, y - 1, targetColor);
    }

    /**
     * Ověří, zda souřadnice označují platný pixel v rastru.
     *
     * <p>Kontrola je před voláním {@code getPixel()} nutná, protože přístup
     * mimo hranice rastru není platný. Díky tomu může výplň bezpečně dorazit
     * až k okraji plátna.</p>
     */
    private boolean isInside(int x, int y) {
        return x >= 0 && x < raster.getWidth()
                && y >= 0 && y < raster.getHeight();
    }
}