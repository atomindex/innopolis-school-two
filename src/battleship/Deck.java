package battleship;

import java.io.Serializable;

/**
 * Created by Atom on 7/27/2015.
 */
public class Deck implements Serializable {
    static final long serialVersionUID = 1;

    private Ship ship;
    private Cell cell;

    private DeckState state;



    public Deck(Ship ship, DeckState state) {
        this.ship = ship;
        this.state = state;
    }

    public Deck(Ship ship) {
        this(ship, DeckState.alive);
    }



    //Устанавливает родительский корабль
    //Warning: Использовать Ship.addDeck для добавления полубы к кораблю
    void setShip(Ship ship) {
        this.ship = ship;
    }

    //Возвращает родительский корабль
    public Ship getShip() {
        return ship;
    }

    //Устанавливает ячейку в которой находится
    //Warning: Использовать Cell.addDeck для помещения палубы в ячейку
    void setCell(Cell cell) {
        this.cell = cell;
    }

    //Возвращает ячейку в которой находится
    public Cell getCell() {
        return cell;
    }



    //Получает состояние полубы
    public DeckState getState() {
        return state;
    }

    //Ранит палубу
    public DeckState injure() {
        //Меняем состояние и оповещаем корабль о ранении палубы
        state = DeckState.injured;
        ship.onDeckInjured();
        return state;
    }

    //Убивает палубу
    public void kill() {
        state = DeckState.killed;
    }
}
