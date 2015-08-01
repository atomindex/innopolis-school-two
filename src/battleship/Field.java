package battleship;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Atom on 7/27/2015.
 */
public class Field implements Serializable {
    static final long serialVersionUID = 1;

    private static final String chars = "abcdefghijk";

    //Список кораблей
    protected ArrayList<Ship> ships = new ArrayList<Ship>();

    //Массив ячеек
    protected Cell[][] cells = new Cell[10][10];

    //Размер поля (количество ячеек)
    protected final int size = 10;

    //Количество живых кораблей
    protected int aliveShips;



    public Field() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell();
                cells[i][j].setPosition(j, i);
            }
        }
    }



    //Переводит строку [буква][число] в координаты
    public static int[] strToCoords(String stringCoords) {
        int[] result = new int[2];
        try {
            result[0] = (stringCoords.charAt(0) - Field.chars.charAt(0));
            result[1] = (Integer.parseInt(stringCoords.substring(1)) - 1);
        } catch (Exception e) {
            System.out.print("#" + stringCoords);
        }

        return result;
    }

    //Переводит координаты в строку [буква][число]
    public static String coordsToStr(int x, int y) {
        return Field.chars.charAt(x) + Integer.toString(y + 1);
    }

    //Получает букву по индексу
    public static char getCharAt(int x) {
        return chars.charAt(x);
    }



    //Возвращает массив клеток
    public Cell[][] getCells() {
        return cells;
    }

    //Возвращает клетку
    public Cell getCell(int x, int y) {
        return cells[y][x];
    }



    //Добавление корабля
    public void addShip(Ship ship) {
        ships.add(ship);
        ship.setField(this);
        ship.update();

        aliveShips++;
    }

    //Возвращает количество живых кораблей
    public int getAliveShipsCount() {
        return aliveShips;
    }

    //Возвращает палубу
    protected Deck getDeck(int x, int y) {
        if (x >= 0 && y >= 0 && x < size && y < size)
            return cells[y][x].getDeck();
        return null;
    }



    //Возвращает true, если корабль может быть установлен в указанное место
    public boolean checkPlace(int x, int y, int deksCount, boolean rotate) {
        if (x < 0 || y < 0 || x >= size || y >= size) return false;

        //Стартовая точка
        int startX = Math.max(0, x - 1);
        int startY = Math.max(0, y - 1);
        int endX, endY;

        if (rotate) {
            //Если повернут (горизонтальный)
            if (x + deksCount > size) return false;
            endX = Math.min(size, x + deksCount + 1);
            endY = Math.min(size, y + 2);
        } else {
            //Если не повернут (вертикальный)
            if (y + deksCount > size) return false;
            endX = Math.min(size, x + 2);
            endY = Math.min(size, y + deksCount + 1);
        }

        //Проверяем ячейки на наличие палуб
        for (x = startX; x < endX; x++)
            for (y = startY; y < endY; y++)
                if (cells[y][x].getState() == CellState.hasDeck)
                    return false;

        return true;
    }



    //Событие убития корабля
    public void onShipKilled(Ship ship) {
        ArrayList<Deck> decks = ship.getDecks();

        //Ставим ореол вокруг корабля
        for (int i = 0; i < decks.size(); i++) {
            int startX = decks.get(i).getCell().getX() - 1;
            int startY = decks.get(i).getCell().getY() - 1;
            int endX = Math.min(size, startX + 3);
            int endY = Math.min(size, startY + 3);
            if (startX < 0) startX = 0;
            if (startY < 0) startY = 0;

            for (int x = startX; x < endX; x++)
                for (int y = startY; y < endY; y++)
                    if (cells[y][x].getState() == CellState.unknown)
                        cells[y][x].setState(CellState.empty);
        }

        aliveShips--;
    }
}
