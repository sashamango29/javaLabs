package view;

import model.GameModel;
import model.GameModel.SplitPart;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameModel model;
    private static final int BLOCK_SIZE = 30;

    public GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(500, 600));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color[][] grid    = model.getFieldGrid();
        int fieldWidth    = grid[0].length * BLOCK_SIZE; // 300
        int fieldHeight   = grid.length    * BLOCK_SIZE; // 600

        // Фон игрового поля
        g2d.setColor(new Color(40, 40, 60));
        g2d.fillRect(0, 0, fieldWidth, fieldHeight);

        // Сетка
        g2d.setColor(new Color(70, 70, 90));
        for (int px = 0; px <= fieldWidth;  px += BLOCK_SIZE) g2d.drawLine(px, 0, px, fieldHeight);
        for (int py = 0; py <= fieldHeight; py += BLOCK_SIZE) g2d.drawLine(0, py, fieldWidth, py);

        //Размещённые блоки
        for (int row = 0; row < grid.length; row++)
            for (int col = 0; col < grid[0].length; col++)
                if (grid[row][col] != null) drawBlock(g2d, col, row, grid[row][col]);

        //Фигура / режим разреза
        if (model.isSplitFallMode()) {
            drawSplitFallPieces(g2d);
        } else if (model.isSplitSelectionMode()) {
            drawSplitSelectionPiece(g2d);
        } else {
            drawNormalPiece(g2d);
        }
        
        drawSidebar(g2d, fieldWidth, fieldHeight);
    }

    private void drawNormalPiece(Graphics2D g2d) {
        int[][] shape   = model.getCurrentTetrominoMatrix();
        int     offX    = model.getCurrentX();
        int     offY    = model.getCurrentY();
        int     shadowY = model.getShadowY();
        Color   color   = model.getCurrentColor();

        // тень
        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1) {
                    g2d.setColor(new Color(50, 50, 50, 120));
                    g2d.fillRect((offX + c) * BLOCK_SIZE, (shadowY + r) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g2d.setColor(new Color(100, 100, 100, 120));
                    g2d.drawRect((offX + c) * BLOCK_SIZE, (shadowY + r) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }

        // фигура
        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1) drawBlock(g2d, offX + c, offY + r, color);
    }

    private void drawSplitSelectionPiece(Graphics2D g2d) {
        int[][] shape     = model.getCurrentTetrominoMatrix();
        int     offX      = model.getCurrentX();
        int     offY      = model.getCurrentY();
        Color   color     = model.getCurrentColor();
        int     splitLine = model.getSplitLine();

        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1) {
                    Color blockColor = (r < splitLine) ? color : color.darker().darker();
                    drawBlock(g2d, offX + c, offY + r, blockColor);
                }

        // красная пунктирная линия разреза
        int lineY  = (offY + splitLine) * BLOCK_SIZE;
        int startX = offX * BLOCK_SIZE;
        int endX   = (offX + shape[0].length) * BLOCK_SIZE;

        float[] dash = {6f, 3f};
        g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        g2d.setColor(new Color(255, 80, 80));
        g2d.drawLine(startX, lineY, endX, lineY);
        g2d.setStroke(new BasicStroke(1f));
    }

    private void drawSplitFallPieces(Graphics2D g2d) {
        SplitPart partA  = model.getPartA();
        SplitPart partB  = model.getPartB();
        int       active = model.getControlledPart();

        // сначала рисуем неактивную часть, поверх — активную (чтобы highlight был виден)
        if (active == 0) {
            if (partB != null) drawSplitPart(g2d, partB, false);
            if (partA != null) drawSplitPart(g2d, partA, true);
        } else {
            if (partA != null) drawSplitPart(g2d, partA, false);
            if (partB != null) drawSplitPart(g2d, partB, true);
        }
    }

    private void drawSplitPart(Graphics2D g2d, SplitPart part, boolean isActive) {
        // тень только для активной части
        if (isActive && !part.landed) {
            int shadowY = model.getShadowYForPart(part);
            if (shadowY != part.y) {
                for (int[] cell : part.cells) {
                    int bx = part.x + cell[1];
                    int by = shadowY + cell[0];
                    g2d.setColor(new Color(50, 50, 50, 120));
                    g2d.fillRect(bx * BLOCK_SIZE, by * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g2d.setColor(new Color(100, 100, 100, 120));
                    g2d.drawRect(bx * BLOCK_SIZE, by * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        // блоки фрагмента
        for (int[] cell : part.cells)
            drawBlock(g2d, part.x + cell[1], part.y + cell[0], part.color);

        // белая пунктирная рамка вокруг активной части
        if (isActive && !part.landed) {
            float[] dash = {4f, 2f};
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
            g2d.setColor(Color.WHITE);
            for (int[] cell : part.cells) {
                int px = (part.x + cell[1]) * BLOCK_SIZE;
                int py = (part.y + cell[0]) * BLOCK_SIZE;
                g2d.drawRect(px + 1, py + 1, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
            }
            g2d.setStroke(new BasicStroke(1f));
        }
    }

    private void drawSidebar(Graphics2D g2d, int fieldWidth, int fieldHeight) {
        int sx = fieldWidth + 10;
        int sw = 160;

        g2d.setColor(new Color(30, 30, 50));
        g2d.fillRect(sx, 0, sw, fieldHeight);

        int bottomY; // куда закончился верхний блок информации

        if (model.isSplitSelectionMode()) {
            bottomY = drawSplitSelectionInfo(g2d, sx);
        } else if (model.isSplitFallMode()) {
            bottomY = drawSplitFallInfo(g2d, sx);
        } else {
            bottomY = drawNextPiece(g2d, sx, sw);
        }

        drawPlayerInfo(g2d, sx, bottomY + 12);
    }

    private int drawNextPiece(Graphics2D g2d, int sx, int sw) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("СЛЕДУЮЩИЙ:", sx + 10, 28);

        int[][] ns  = model.getNextTetrominoMatrix();
        Color   nc  = model.getNextColor();
        int     nbs = 20;
        int     nsx = sx + (sw - ns[0].length * nbs) / 2;
        int     nsy = 46;

        for (int r = 0; r < ns.length; r++)
            for (int c = 0; c < ns[0].length; c++)
                if (ns[r][c] == 1) {
                    g2d.setColor(nc);
                    g2d.fillRect(nsx + c * nbs, nsy + r * nbs, nbs, nbs);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(nsx + c * nbs, nsy + r * nbs, nbs, nbs);
                }

        int bottom = nsy + ns.length * nbs + 6;
        // разделитель
        g2d.setColor(new Color(70, 70, 90));
        g2d.drawLine(sx + 5, bottom, sx + 155, bottom);
        return bottom;
    }

    private int drawSplitSelectionInfo(Graphics2D g2d, int sx) {
        int x = sx + 8;

        // заголовок
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(new Color(255, 100, 100));
        g2d.drawString("✂  ВЫБОР РАЗРЕЗА", x, 26);

        // подсказки
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(220, 220, 220));
        g2d.drawString("↑ ↓   двигать линию", x, 50);
        g2d.drawString("ENTER  разрезать", x, 68);
        g2d.drawString("ESC    отмена", x, 86);

        g2d.setColor(new Color(70, 70, 90));
        g2d.drawLine(sx + 5, 96, sx + 155, 96);
        return 96;
    }

    // о режиме падения двух частей. Возвращает нижнюю Y. 
    private int drawSplitFallInfo(Graphics2D g2d, int sx) {
        int x = sx + 8;
        boolean controlTop = (model.getControlledPart() == 0);

        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(new Color(100, 255, 100));
        g2d.drawString("✂  РАЗРЕЗАНО!", x, 26);

        // текущая управляемая часть
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Часть: " + (controlTop ? "▲ верхняя" : "▼ нижняя"), x, 48);

        // подсказки
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(220, 220, 220));
        g2d.drawString("TAB    переключить", x, 70);
        g2d.drawString("← →   двигать", x, 88);
        g2d.drawString("SPACE  бросить часть", x, 106);

        g2d.setColor(new Color(70, 70, 90));
        g2d.drawLine(sx + 5, 116, sx + 155, 116);
        return 116;
    }

    private void drawPlayerInfo(Graphics2D g2d, int sx, int startY) {
        int x = sx + 10;
        int y = startY;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("ИГРОК:", x, y);
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.drawString(model.getPlayer().getName(), x, y + 20);

        y += 52;
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("ОЧКИ:", x, y);
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.drawString(String.valueOf(model.getScore()), x, y + 20);

        y += 50;
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("УРОВЕНЬ:", x, y);
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.drawString(String.valueOf(model.getLevel()), x, y + 20);

        y += 50;
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("ЛИНИИ:", x, y);
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.drawString(String.valueOf(model.getLinesCleared()), x, y + 20);

        // управление
        y += 52;
        g2d.setColor(new Color(170, 170, 170));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("УПРАВЛЕНИЕ:", x, y);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        int dy = 15;
        y += 3;
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("← → : движение",    x, y += dy);
        g2d.drawString("↑ : поворот",        x, y += dy);
        g2d.drawString("↓ : вниз",           x, y += dy);
        g2d.drawString("SPACE: сброс",        x, y += dy);
        g2d.setColor(new Color(255, 150, 150));
        g2d.drawString("S : разрезать",      x, y += dy);
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("P : пауза",           x, y += dy);
    }

    private void drawBlock(Graphics2D g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

        g.setColor(color.brighter());
        g.drawLine(x * BLOCK_SIZE,                y * BLOCK_SIZE,
                   x * BLOCK_SIZE + BLOCK_SIZE-1, y * BLOCK_SIZE);
        g.drawLine(x * BLOCK_SIZE, y * BLOCK_SIZE,
                   x * BLOCK_SIZE, y * BLOCK_SIZE + BLOCK_SIZE-1);

        g.setColor(color.darker());
        g.drawLine(x * BLOCK_SIZE + BLOCK_SIZE-1, y * BLOCK_SIZE,
                   x * BLOCK_SIZE + BLOCK_SIZE-1, y * BLOCK_SIZE + BLOCK_SIZE-1);
        g.drawLine(x * BLOCK_SIZE,                y * BLOCK_SIZE + BLOCK_SIZE-1,
                   x * BLOCK_SIZE + BLOCK_SIZE-1, y * BLOCK_SIZE + BLOCK_SIZE-1);

        g.setColor(new Color(0, 0, 0, 50));
        g.drawRect(x * BLOCK_SIZE + 2, y * BLOCK_SIZE + 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
    }
}
