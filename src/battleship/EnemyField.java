package battleship;

/**
 * Created by Atom on 7/28/2015.
 */

//Поле противника (для отметки ответов)
public class EnemyField extends Field {
    //Возвращает ячейку
    public void addDeck(int x, int y, Deck deck) {
        cells[y][x].addDeck(deck);
    }

    //Рекурсивное объекдинение смежных палуб
    private void recursiveJoin(Ship ship, Deck deck) {
        if (deck == null || deck.getShip() != null) return;
        ship.addDeck(deck);

        int x = deck.getCell().getX();
        int y = deck.getCell().getY();
        recursiveJoin(ship, getDeck(x,  y - 1));
        recursiveJoin(ship, getDeck(x + 1, y));
        recursiveJoin(ship, getDeck(x, y + 1));
        recursiveJoin(ship, getDeck(x - 1, y));
    }

    //Ищет смежные палубы, не имеющие родителя и объекдиняет их в корабль
    public Ship joinAdjacentDecks(Deck startDeck) {
        Ship ship = new Ship(0);
        addShip(ship);
        recursiveJoin(ship, startDeck);
        return ship;
    }
}
