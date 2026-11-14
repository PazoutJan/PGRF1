# Kreslení úseček a polygonu

Jednoduchá Java/Swing aplikace pro kreslení úseček a polygonu pomocí rastrové grafiky. Projekt vznikl jako úloha do předmětu PGRF1.

## Funkce

Program podporuje:

- kreslení úseček zadaných dvěma libovolnými koncovými body,
- pružnou čáru při zadávání úsečky,
- kreslení polygonu ukládaného pomocí seznamu vrcholů,
- pružné spojení nového vrcholu polygonu s prvním a posledním vrcholem,
- vykreslení již uložené scény při každém překreslení,
- editaci polohy vrcholů polygonu,
- editaci počátečního i koncového bodu úsečky,
- mazání vrcholů polygonu,
- mazání celé úsečky kliknutím na jeden z jejích koncových bodů,
- vymazání plátna a všech uložených dat,
- bezpečné vykreslování čar, jejichž část leží mimo plátno.

Při editaci a mazání se vrchol vyhledává v okolí pozice myši. Použitá tolerance je 10 pixelů.

## Ovládání

Před použitím klávesových zkratek klikněte do kreslicí plochy, aby měla fokus.

| Klávesa | Režim / akce |
| --- | --- |
| `L` | kreslení samostatných úseček |
| `P` | kreslení polygonu |
| `E` | editace vrcholů |
| `D` | mazání vrcholů nebo úseček |
| `F` | výplň oblasti podle pozice kurzoru |
| `C` | vymazání plátna a všech dat |

### Režim úseček (`L`)

1. Stiskněte levé tlačítko myši v počátečním bodě.
2. Táhněte myší na požadovaný koncový bod.
3. Během tažení se zobrazuje pružná čára společně s již uloženou scénou.
4. Uvolněte tlačítko. Úsečka se uloží do seznamu `Lines`.

### Režim polygonu (`P`)

1. Stiskněte levé tlačítko myši v místě nového vrcholu.
2. Táhněte myší. Nový bod se dočasně propojí s prvním a posledním vrcholem polygonu.
3. Uvolněte tlačítko. Aktuální pozice se přidá do seznamu vrcholů `Polygon`.

Polygon se automaticky uzavírá spojením posledního vrcholu s prvním. Při jednom nebo dvou vrcholech se vykresluje pouze aktuálně možná geometrie.

### Režim editace (`E`)

1. Klikněte levým tlačítkem poblíž vrcholu polygonu nebo koncového bodu úsečky.
2. Držte tlačítko a táhněte myší.
3. Vybraný bod se během tažení přesouvá.
4. Uvolněte tlačítko.

Pokud je vybrán konec úsečky, původní úsečka je nahrazena novou úsečkou s upraveným koncovým bodem.

### Režim mazání (`D`)

Klikněte levým tlačítkem poblíž vrcholu polygonu nebo koncového bodu úsečky. Polygonový vrchol se odstraní samostatně. Při výběru konce úsečky se odstraní celá úsečka.

### Vymazání scény (`C`)

Klávesa `C` odstraní:

- všechny vrcholy polygonu,
- všechny uložené úsečky,
- rozpracované stavy,
- obsah rastrového plátna.

### Výplň oblasti (`F`)

1. Stiskněte klávesu `F`.
2. Klikněte do oblasti, kterou chcete vyplnit.

Používá se čtyř-sousední seed fill. Výplň nahrazuje souvislou oblast stejné
barvy jako pixel pod kurzorem a respektuje rastrové hranice nakreslené bílou
barvou.

## Struktura projektu

```text
src/
├── Main.java
├── controller/
│   └── Controller2D.java
├── model/
│   ├── Point2D.java
│   ├── Line.java
│   ├── Lines.java
│   ├── Polygon.java
│   └── Fill.java
├── raster/
│   ├── Raster.java
│   └── RasterBufferedImage.java
├── rasterize/
│   ├── LineRasterizer.java
│   ├── LineRasterizerTrivial.java
│   ├── LineRasterizerGraphics.java
│   └── PolygonRasterizer.java
├── fill/
│   ├── Filler.java
│   └── SeedFill.java
└── view/
    ├── Panel.java
    └── Window.java
```

### Model

- `Point2D` uchovává souřadnice bodu.
- `Line` reprezentuje úsečku pomocí počátečního a koncového bodu.
- `Lines` uchovává seznam samostatných úseček.
- `Polygon` uchovává seznam vrcholů polygonu.

Body i úsečky jsou neměnné. Při editaci úsečky se proto vytvoří nová instance `Line`, která nahradí původní prvek v seznamu.

### Raster a rasterizace

- `Raster` definuje základní operace rastrového plátna.
- `RasterBufferedImage` používá jako úložiště `BufferedImage`.
- `LineRasterizer` definuje rozhraní pro rasterizaci úsečky a společné metody pro body a seznam úseček.
- `LineRasterizerTrivial` vykresluje úsečky pomocí výpočtu směrnice.
- `PolygonRasterizer` převádí sousední vrcholy polygonu na úsečky.

Pixelové souřadnice mimo hranice plátna jsou v `RasterBufferedImage.setPixel()` ignorovány, takže kreslení mimo plátno nezpůsobí výjimku.

### Controller a view

`Controller2D` zpracovává události myši a klávesnice, spravuje aktuální režim a rozpracovaný stav kreslení. `Panel` zobrazuje rastrový obraz a `Window` vytváří hlavní okno aplikace.


## Omezení a možné rozšíření

Bonus pro přidání nového vrcholu na nejbližší hranu polygonu není implementován. Další možná rozšíření jsou:

- implementace Bresenhamova algoritmu pro přesnější celočíselnou rasterizaci,
- vizuální zvýraznění vybraného vrcholu,
- zobrazení aktuálního režimu přímo v uživatelském rozhraní,
