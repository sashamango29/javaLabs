package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final GameField field;
    private final Player player;
    private Tetromino current;
    private Tetromino next;
    private int x, y;
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    private boolean gameOver = false;
    private boolean paused = false;
//сплит фигня
    private boolean splitSelectionMode = false; // игрок выбирает линию разреза
    private boolean splitFallMode      = false; // две половины падают независимо
    private int     splitLine          = 1;     // строка внутри фигуры (локальная)
    private SplitPart partA            = null;  // верхняя половина
    private SplitPart partB            = null;  // нижняя половина
    private int     controlledPart     = 1;     // 0=partA (верх), 1=partB (низ)

    public static class SplitPart {
        public final List<int[]> cells;
        public int x, y;
        public final Color color;
        public boolean landed;

        public SplitPart(List<int[]> cells, int x, int y, Color color) {
            this.cells  = new ArrayList<>(cells);
            this.x      = x;
            this.y      = y;
            this.color  = color;
            this.landed = false;
        }
    }

    public GameModel(int rows, int cols, Player player) {
        this.field  = new GameField(rows, cols);
        this.player = player;
        this.next   = Tetromino.getRandomTetromino();
        spawnNew();
    }

    public void spawnNew() {
        current = next;
        next    = Tetromino.getRandomTetromino();
        x       = field.getGrid()[0].length / 2 - current.getShape()[0].length / 2;
        y       = 0;
        if (collides(x, y)) gameOver = true;
    }

    public void moveDown() {
        if (paused || gameOver || splitSelectionMode || splitFallMode) return;
        if (!collides(x, y + 1)) {
            y++;
        } else {
            merge();
            int removed = clearLines();
            updateScore(removed);
            spawnNew();
        }
    }

    public void moveLeft() {
        if (paused || gameOver || splitSelectionMode || splitFallMode) return;
        if (!collides(x - 1, y)) x--;
    }

    public void moveRight() {
        if (paused || gameOver || splitSelectionMode || splitFallMode) return;
        if (!collides(x + 1, y)) x++;
    }

    public void rotate() {
        if (paused || gameOver || splitSelectionMode || splitFallMode) return;
        Tetromino rotated = current.rotate();
        if (!collides(x, y, rotated)) { current = rotated; return; }
        int[] kicks = {-1, 1, -2, 2};
        for (int k : kicks) {
            if (!collides(x + k, y, rotated)) { current = rotated; x += k; return; }
        }
    }

    public void drop() {
        if (paused || gameOver || splitSelectionMode || splitFallMode) return;
        int dropY = getShadowY();
        if (dropY > y) {
            y = dropY;
            merge();
            int removed = clearLines();
            updateScore(removed);
            spawnNew();
        }
    }

    public int getShadowY() {
        int sy = y;
        while (!collides(x, sy + 1)) sy++;
        return sy;
    }

    public void enterSplitMode() {
        if (paused || gameOver || splitSelectionMode || splitFallMode) return;
        if (current.getShape().length < 2) return; // горизонтальную I нельзя резать

        // ищем допустимую начальную линию (посередине)
        int height = current.getShape().length;
        splitLine = Math.max(1, height / 2);

        for (int t = splitLine; t < height; t++) {
            if (isValidSplitLine(t)) { splitLine = t; splitSelectionMode = true; return; }
        }
        for (int t = splitLine - 1; t >= 1; t--) {
            if (isValidSplitLine(t)) { splitLine = t; splitSelectionMode = true; return; }
        }
        // нет допустимой линии (все клетки в одной строке) — ничего не делаем
    }

    private boolean isValidSplitLine(int line) {
        boolean above = false, below = false;
        int[][] s = current.getShape();
        for (int r = 0; r < s.length; r++)
            for (int c = 0; c < s[0].length; c++)
                if (s[r][c] == 1) { if (r < line) above = true; else below = true; }
        return above && below;
    }

    public void moveSplitLineUp() {
        if (!splitSelectionMode) return;
        for (int nl = splitLine - 1; nl >= 1; nl--)
            if (isValidSplitLine(nl)) { splitLine = nl; return; }
    }

    public void moveSplitLineDown() {
        if (!splitSelectionMode) return;
        int h = current.getShape().length;
        for (int nl = splitLine + 1; nl < h; nl++)
            if (isValidSplitLine(nl)) { splitLine = nl; return; }
    }

    public void confirmSplit() {
        if (!splitSelectionMode) return;
        int[][] shape = current.getShape();
        List<int[]> ca = new ArrayList<>(), cb = new ArrayList<>();
        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1)
                    (r < splitLine ? ca : cb).add(new int[]{r, c});
        if (ca.isEmpty() || cb.isEmpty()) return;

        partA = normalize(ca, x, y, current.getColor());
        partB = normalize(cb, x, y, current.getColor());

        controlledPart     = 1;    
        splitSelectionMode = false;
        splitFallMode      = true;
    }

    private SplitPart normalize(List<int[]> cells, int wx, int wy, Color color) {
        int minR = Integer.MAX_VALUE, minC = Integer.MAX_VALUE;
        for (int[] c : cells) { minR = Math.min(minR, c[0]); minC = Math.min(minC, c[1]); }
        List<int[]> norm = new ArrayList<>();
        for (int[] c : cells) norm.add(new int[]{c[0] - minR, c[1] - minC});
        return new SplitPart(norm, wx + minC, wy + minR, color);
    }

    public void cancelSplit() {
        splitSelectionMode = false;
        splitFallMode      = false;
        partA = partB      = null;
    }

    public void switchControlledPart() {
        if (!splitFallMode) return;
        int other = (controlledPart == 0) ? 1 : 0;
        SplitPart target = (other == 0) ? partA : partB;
        if (target != null && !target.landed) controlledPart = other;
    }

    public void moveSplitPartLeft()  { moveActivePart(-1); }
    public void moveSplitPartRight() { moveActivePart(+1); }

    private void moveActivePart(int dx) {
        if (!splitFallMode) return;
        SplitPart p = activePart();
        if (p != null && !p.landed && !collidesWithField(p, p.x + dx, p.y)) p.x += dx;
    }

    public void dropActiveSplitPart() {
        if (!splitFallMode) return;
        SplitPart p = activePart();
        if (p == null || p.landed) return;
        while (!collidesWithField(p, p.x, p.y + 1)) p.y++;
        mergePart(p);
        p.landed = true;
        // автоматически переключаемся на другую часть
        controlledPart = (p == partA) ? 1 : 0;
        checkSplitFinished();
    }

    public void updateSplitFall() {
        if (!splitFallMode) return;
        fallOnePart(partB); // нижняя часть падает первой
        fallOnePart(partA);
        checkSplitFinished();
    }

    private void fallOnePart(SplitPart p) {
        if (p == null || p.landed) return;
        if (!collidesWithField(p, p.x, p.y + 1)) {
            p.y++;
        } else {
            mergePart(p);
            p.landed = true;
        }
    }

    private void checkSplitFinished() {
        boolean aDown = (partA == null || partA.landed);
        boolean bDown = (partB == null || partB.landed);
        if (aDown && bDown) {
            int removed = clearLines();
            updateScore(removed);
            splitFallMode = false;
            partA = partB = null;
            spawnNew();
        }
    }

    public int getShadowYForPart(SplitPart part) {
        int sy = part.y;
        while (!collidesWithField(part, part.x, sy + 1)) sy++;
        return sy;
    }

    private SplitPart activePart() {
        SplitPart p = (controlledPart == 0) ? partA : partB;
        if (p != null && !p.landed) return p;
        SplitPart q = (controlledPart == 0) ? partB : partA;
        return (q != null && !q.landed) ? q : null;
    }

    public boolean collidesWithField(SplitPart part, int nx, int ny) {
        Color[][] grid = field.getGrid();
        int cols = grid[0].length, rows = grid.length;
        for (int[] cell : part.cells) {
            int bx = nx + cell[1], by = ny + cell[0];
            if (bx < 0 || bx >= cols || by >= rows) return true;
            if (by >= 0 && grid[by][bx] != null) return true;
        }
        return false;
    }

    public boolean collides(int nx, int ny) { return collides(nx, ny, current); }

    public boolean collides(int nx, int ny, Tetromino t) {
        int[][] s = t.getShape();
        Color[][] grid = field.getGrid();
        for (int r = 0; r < s.length; r++)
            for (int c = 0; c < s[0].length; c++)
                if (s[r][c] == 1) {
                    int bx = nx + c, by = ny + r;
                    if (bx < 0 || bx >= grid[0].length || by >= grid.length) return true;
                    if (by >= 0 && grid[by][bx] != null) return true;
                }
        return false;
    }

    private void merge() {
        int[][] shape = current.getShape();
        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1) field.occupy(y + r, x + c, current.getColor());
    }

    private void mergePart(SplitPart p) {
        for (int[] cell : p.cells) field.occupy(p.y + cell[0], p.x + cell[1], p.color);
    }

    private int clearLines() {
        Color[][] grid = field.getGrid();
        int removed = 0;
        for (int row = 0; row < grid.length; row++) {
            boolean full = true;
            for (Color c : grid[row]) if (c == null) { full = false; break; }
            if (full) { field.clearLine(row); removed++; }
        }
        linesCleared += removed;
        return removed;
    }

    private void updateScore(int removed) {
        if (removed == 0) return;
        int[] pts = {0, 100, 300, 500, 800};
        score += pts[Math.min(removed, 4)] * level;
        level  = (linesCleared / 10) + 1;
    }

    public int       getCurrentX()               { return x; }
    public int       getCurrentY()               { return y; }
    public int[][]   getCurrentTetrominoMatrix() { return current.getShape(); }
    public Color     getCurrentColor()           { return current.getColor(); }
    public int[][]   getNextTetrominoMatrix()    { return next.getShape(); }
    public Color     getNextColor()              { return next.getColor(); }
    public Color[][] getFieldGrid()              { return field.getGrid(); }
    public int       getScore()                  { return score; }
    public int       getLevel()                  { return level; }
    public int       getLinesCleared()           { return linesCleared; }
    public boolean   isGameOver()                { return gameOver; }
    public boolean   isPaused()                  { return paused; }
    public Player    getPlayer()                 { return player; }
    public void      togglePause()               { paused = !paused; }

    // Split геттеры
    public boolean   isSplitSelectionMode()  { return splitSelectionMode; }
    public boolean   isSplitFallMode()       { return splitFallMode; }
    public int       getSplitLine()          { return splitLine; }
    public SplitPart getPartA()              { return partA; }
    public SplitPart getPartB()              { return partB; }
    public int       getControlledPart()     { return controlledPart; }
}