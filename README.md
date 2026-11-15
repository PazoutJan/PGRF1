# PGRF1 - rastrové algoritmy

Java/Swing aplikace pro kreslení úseček a polygonů, rastrové vyplňování oblastí
a ořezávání geometrických útvarů. Projekt je řešen jako úloha do předmětu
PGRF1.

## Rychlý návod k ovládání

Před použitím klávesových zkratek klikněte do kreslicí plochy, aby měla fokus.
Aktuálně zvolený režim určuje význam levého tlačítka myši.

| Klávesa | Režim nebo akce | Ovládání |
| --- | --- | --- |
| `L` | Úsečky | Levé tlačítko + tažení |
| `P` | Polygon | Levé tlačítko pro každý vrchol |
| `S` | Scan-line výplň | Stejně jako režim polygonu |
| `F` | Seed fill podle pozadí | Kliknutí do oblasti |
| `B` | Seed fill podle hranice | Kliknutí do oblasti |
| `R` | Cohen-Sutherland | Levé tlačítko + tažení |
| `O` | Polygon clipping | Levé tlačítko = ořezávaný polygon, pravé tlačítko = ořezávací polygon |
| `E` | Editace | Kliknutí a tažení vrcholu nebo koncového bodu |
| `D` | Mazání | Kliknutí poblíž vrcholu nebo koncového bodu |
| `C` | Vymazání scény | Odstraní všechny objekty a rozpracované stavy |
| `0`-`7` | Výběr barvy | Platí také pro numerickou klávesnici |

Při kreslení úsečky v režimu `L` držte během tažení klávesu `Shift`. Koncový
bod se potom přichytí na nejbližší vodorovný, svislý nebo úhlopříčný směr.

## Režimy podrobně

### Úsečky (`L`)

1. Stiskněte levé tlačítko v počátečním bodě.
2. Táhněte myší na požadovaný koncový bod.
3. Uvolněte tlačítko.

Úsečka se uloží do scény. Během tažení se zobrazuje její dočasný náhled.
Libovolné koncové body jsou podporovány i mimo hranice kreslicí plochy;
pixely mimo raster se pouze nevykreslí.

### Polygon (`P`)

1. Stiskněte levé tlačítko v místě prvního vrcholu.
2. Každým dalším stisknutím a uvolněním levého tlačítka přidejte další vrchol.
3. Polygon se vykresluje jako uzavřený spojením posledního vrcholu s prvním.

Při tažení se zobrazuje náhled nového vrcholu. Polygon není nutné potvrzovat
klávesou Enter.

### Scan-line výplň (`S`)

Po stisknutí `S` zadávejte polygon stejně jako v režimu `P`. Geometricky
zadaný polygon se průběžně vyplňuje scan-line algoritmem. Pro správný výsledek
zadejte alespoň tři vrcholy.

### Seed fill podle barvy pozadí (`F`)

1. Stiskněte `F`.
2. Klikněte do oblasti, kterou chcete vyplnit.

Používá se iterativní čtyř-sousední algoritmus. Vyplní se souvislá oblast
pixelů stejné barvy jako pixel pod kurzorem. Hranice proto musí být v rastru
vykreslena souvisle a jinou barvou než vyplňovaná oblast.

### Seed fill podle barvy hranice (`B`)

1. Nakreslete uzavřenou hranici.
2. Stiskněte `B`.
3. Klikněte dovnitř oblasti.

Algoritmus vyplňuje pixely až po explicitně zadanou barvu hranice. Barva
hranice se určuje podle barvy existujícího polygonu nebo úseček ve scéně.
Výplň a hranice mohou mít různé barvy.

### Cohen-Sutherland (`R`)

Po stisknutí `R` zadávejte úsečky levým tlačítkem a tažením. Program každou
úsečku ořízne pevným obdélníkovým oknem:

```text
xmin = 150, xmax = 650
ymin = 100, ymax = 450
```

Modré okno je ořezávací oblast a červeně se vykreslí pouze část úsečky, která
leží uvnitř okna. Úsečky, které jsou celé mimo okno, se nezobrazí. Algoritmus
řeší triviální přijetí, triviální zamítnutí i výpočet průsečíků s hranicemi.

### Polygon clipping (`O`)

Po stisknutí `O` se předchozí clippingové polygony vymažou. Vrcholy zadávejte
takto:

- **levé tlačítko**: polygon, který se má oříznout,
- **pravé tlačítko**: konvexní ořezávací polygon.

Výsledek se přepočítává po každém novém vrcholu. Ořezávací polygon musí mít
alespoň pět vrcholů. Je určen pro konvexní polygony a může být zadán v kladné
i záporné orientaci. Ořezávaný polygon může být libovolný uzavřený polygon.

Vykreslení používá tyto barvy:

- původní ořezávaný polygon: zvolená barva,
- ořezávací polygon: modrá,
- výsledek ořezu: červená.

### Editace (`E`)

Klikněte poblíž vrcholu polygonu nebo koncového bodu úsečky a táhněte myší.
Při zapnutém režimu Cohen-Sutherland lze stejným způsobem upravovat i vstupní
úsečky pro ořezávání. Výběrová tolerance je 10 pixelů.

### Mazání (`D`)

Klikněte poblíž vrcholu nebo koncového bodu. Vrchol polygonu se odstraní
samostatně; při výběru koncového bodu úsečky se odstraní celá úsečka.

### Vymazání scény (`C`)

Klávesa `C` odstraní všechny uložené objekty a resetuje rozpracovaný stav:

- běžné úsečky a polygon,
- seed fill výplně,
- scan-line stav,
- vstupní a výsledné úsečky Cohen-Sutherlandova algoritmu,
- ořezávaný, ořezávací i výsledný polygon,
- aktuální výběr a rozpracovaný náhled.

## Barvy

Barvu lze kdykoliv změnit klávesami `0` až `7` nebo odpovídajícími klávesami
na numerické klávesnici:

| Klávesa | Barva |
| --- | --- |
| `0` | černá |
| `1` | bílá |
| `2` | červená |
| `3` | zelená |
| `4` | modrá |
| `5` | žlutá |
| `6` | fialová |
| `7` | azurová |

Barva se ukládá ke každému nově vytvořenému objektu. Změna aktuální barvy
proto nemění již vykreslené úsečky, polygon ani výplně. Modré ořezávací
objekty a červené výsledky clippingu mají pevně dané barvy kvůli přehlednému
odlišení algoritmů.

## Použité algoritmy

- **Triviální rasterizace úsečky** - převádí geometrickou úsečku na pixely
  rastru a řeší vodorovné, svislé i obecné úsečky.
- **Rasterizace polygonu** - rasterizuje všechny sousední hrany a uzavírá
  polygon hranou mezi posledním a prvním vrcholem.
- **Seed fill** - iterativní čtyř-sousední záplavové vyplnění se dvěma
  hraničními podmínkami.
- **Scan-line fill** - vyplnění geometricky zadaného polygonu pomocí průsečíků
  vodorovných scan-line s jeho hranami.
- **Cohen-Sutherland** - ořezání úseček pomocí pozičních kódů vůči
  obdélníkovému oknu.
- **Sutherland-Hodgman** - ořezání polygonu konvexním polygonem; implementace
  podporuje obě orientace vrcholů ořezávacího polygonu.

## Struktura projektu

```text
src/
├── Main.java
├── controller/       Zpracování vstupů a řízení režimů
├── model/            Bod, úsečka, polygon, výplň a scéna
├── raster/           Rozhraní a BufferedImage raster
├── rasterize/        Rasterizace úseček a polygonů
├── fill/             Seed fill a scan-line fill
├── clipping/         Cohen-Sutherland a polygon clipping
├── geometry/         Geometrické pomocné výpočty
├── interaction/      Výběr vrcholů a koncových bodů
├── render/           Vykreslení uložené scény a náhledů
└── view/             Swing panel, okno a barevná paleta
```

Hlavní části návrhu:

- `Scene` uchovává všechny objekty scény a jejich data.
- `Controller2D` zpracovává klávesnici a myš a přepíná režimy.
- `SceneRenderer` obnovuje raster ze scény při každém překreslení.
- `ClippingManager` spravuje výpočty ořezaných úseček a polygonů.
- `LineRasterizer`, `PolygonRasterizer`, `SeedFill` a `ScanLineFill`
  implementují jednotlivé rastrové algoritmy.
