package battleInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import battleship.*;

/**
 * Created by Atom on 7/28/2015.
 */

public class View extends JPanel {

    class MouseEvents extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (locked) return;

            int x = e.getX() - cellSize - cellSpacing;
            int y = e.getY() - cellSize - cellSpacing;

            if (x < 0 || y < 0) return;

            x /= (cellSize + cellSpacing);
            y /= (cellSize + cellSpacing);

            CellState cs = cells[y][x].getState();
            if (cs != CellState.unknown && cs != CellState.empty)
                return;

            player.getGame().step(Field.coordsToStr(x, y));
        }
    }

    //Массив ячеек
    private Cell[][] cells;

    //Размер ячеек
    private int cellSize = 21;
    private int cellSpacing = 1;

    //Цвета отрисовки ячеек и палуб
    private Color unknownColor;
    private Color emptyColor;
    private Color pastColor;
    private Color pastCircleColor;
    private Color aliveColor;
    private Color deadColor;
    private Color deadXColor;

    //Игрок, к которому принадлежит поле
    private Player player;

    //Определяет показывать ли empty ячейки
    private boolean showEmptyCell;

    //Определяет заблокирован ли клик
    private boolean locked;
    //Определяет показывается ли затемененный цвет
    private boolean lockView;



    public View() {
        addMouseListener(new MouseEvents());

        unknownColor = Color.decode("#80CBC4");
        emptyColor = Color.decode("#80CBC4");
        pastColor = Color.decode("#80CBC4");
        pastCircleColor = Color.decode("#26A69A");
        aliveColor = Color.decode("#6D4C41");
        deadColor = Color.decode("#6D4C41");
        deadXColor = Color.decode("#66BB6A");

        showEmptyCell = true;

        setBackground(new Color(0,0,0,1));
        setBounds(getX(), getY(), (cellSize + cellSpacing) * 11, (cellSize + cellSpacing) * 11);
    }



    //Устанавливает массив ячеек
    public void setMap(Cell[][] cells) {
        this.cells = cells;
        repaint();
    }

    //Установка пользователя
    public void setPlayer(Player player) {
        this.player = player;
    }

    //Устанавливает показывать ли empty ячейки
    public void setShowEmptyCell(boolean value) {
        showEmptyCell = value;
    }


    //Блокирует клик
    public void lock() {
        locked = true;
    }

    //Разблокирует клик
    public void unlock() {
        locked = false;
    }

    //Устанавливает затемненный цвет
    public void lockView(boolean val) {
        lockView = val;
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cells == null) return;

        Font font = new Font(g.getFont().getName(), Font.BOLD, 12);
        g.setColor(new Color(0.7f, 0.7f, 0.7f));
        g.setFont(font);

        for (int i = 0; i < 10; i++) {
            int pos = (i + 1) * (cellSize + cellSpacing);
            g.drawString(Character.toString(Field.getCharAt(i)), pos + 8, 16);
            g.drawString(Integer.toString(i + 1), 6, pos + 16);
        }

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                int x = (j + 1) * (cellSize + cellSpacing);
                int y = (i + 1) * (cellSize + cellSpacing);

                switch (cells[i][j].getState()) {
                    case unknown:
                        g.setColor(unknownColor);
                        g.fillRect(x,y,cellSize,cellSize);
                        break;

                    case empty: {
                        g.setColor(emptyColor);
                        g.fillRect(x, y, cellSize, cellSize);
                        if (showEmptyCell) {
                            int hs = cellSize / 3;
                            int qs = (cellSize - hs) / 2;
                            g.setColor(pastCircleColor);
                            g.fillOval(x + qs, y + qs, hs, hs);
                        }
                        break;
                    }

                    case past: {
                        g.setColor(pastColor);
                        g.fillRect(x, y, cellSize, cellSize);
                        int hs = cellSize / 2;
                        int qs = cellSize / 4;
                        g.setColor(pastCircleColor);
                        g.fillOval(x + qs, y + qs, hs, hs);
                        break;
                    }

                    case hasDeck:
                        switch (cells[i][j].getDeck().getState()) {
                            case alive:
                                g.setColor(aliveColor);
                                g.fillRect(x, y, cellSize, cellSize);
                                break;
                            case injured:
                            case killed:
                                g.setColor(deadColor);
                                g.fillRect(x, y, cellSize, cellSize);

                                g.setColor(deadXColor);
                                int offset = 5;

                                g2.setStroke(new BasicStroke(3));
                                g2.drawLine(x + offset, y + offset, x + cellSize - offset - 1, y + cellSize - offset - 1);
                                g2.drawLine(x + offset, y + cellSize - offset - 1, x + cellSize - offset - 1, y + offset);
                                break;
                        }
                        break;
                }
            }
        }

        if (lockView) {
            g.setColor(new Color(0,0,0, 0.15f));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

}
