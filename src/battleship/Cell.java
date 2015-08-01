package battleship;

import java.io.Serializable;

/**
 * Created by Atom on 7/27/2015.
 */

//Ячейка поля
public class Cell implements Serializable {
    static final long serialVersionUID = 1;

    //Состояние ячейки
    private CellState state;
    //Состояние ячейки до помещения в нее палубы
    private CellState prevState;
    //Палуба
    private Deck deck;

    //Координаты
    private int x;
    private int y;



    public Cell() {
        state = CellState.unknown;
    }



    //Возвращает полубу, которая находится в клетке
    public Deck getDeck() {
        return deck;
    }

    //Добавляет палубу в клетку
    void addDeck(Deck deck) {
        if (deck.getCell() != null)
            deck.getCell().removeDeck();

        this.deck = deck;
        deck.setCell(this);

        prevState = state;
        state = CellState.hasDeck;
    }

    //Убирает палубу из клетки
    void removeDeck() {
        this.deck = null;
        state = prevState;
    }



    //Возвращает состояние
    public CellState getState() {
        return state;
    }

    //Устанавливает состояние клетки
    void setState(CellState state) {
        if (this.state == CellState.hasDeck) return;
        this.state = state;
    }



    //Получает позицию X
    public int getX() {
        return x;
    }

    //Получает позицию Y
    public int getY() {
        return y;
    }

    //Установка позиций
    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
