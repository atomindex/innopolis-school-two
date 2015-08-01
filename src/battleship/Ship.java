package battleship;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Atom on 7/27/2015.
 */

//Корабль
public class Ship implements Serializable {
    static final long serialVersionUID = 1;

    //Родительское поле
    private Field field;

    //Список палуб
    private ArrayList<Deck> decks;

    //Позиции
    private int x;
    private int y;
    //Поворот true = горизонтальный
    private boolean rotated;



    public Ship(int size) {
        decks = new ArrayList<Deck>();
        for (int i = 0; i < size; i++)
            decks.add(new Deck(this));
    }



    //Устанавливает поле
    public void setField(Field field) {
        this.field = field;
    }

    //Возращает полубы
    public ArrayList<Deck> getDecks() {
        return decks;
    }



    //Обновляет положение палуб взависимости от координат и поворота
    public void update() {
        if (field == null) return;

        if (rotated) {
            int x = this.x;
            for (int i = 0; i < decks.size(); i++) {
                field.getCell(x, y).addDeck(decks.get(i));
                x++;
            }
        } else {
            int y = this.y;
            for (int i = 0; i < decks.size(); i++) {
                field.getCell(x, y).addDeck(decks.get(i));
                y++;
            }
        }
    }

    //Устанавливает поворот
    public void setRotate(boolean rotated) {
        this.rotated = rotated;
        update();
    }

    //Возвращает поворот
    public boolean getRotate() {
        return rotated;
    }

    //Устанавливает позицию
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        update();
    }

    //Устанавливает позицию и поворот
    public void setPosition(int x, int y, boolean rotated) {
        this.rotated = rotated;
        setPosition(x, y);
    }


    //Убивает корабль
    public void kill() {
        for (int i = 0; i < decks.size(); i++)
            decks.get(i).kill();
    }

    //Событие повреждения палубы
    public void onDeckInjured() {
        //Проверяем ранены ли все ячейки
        for (int i = 0; i < decks.size(); i++)
            if (decks.get(i).getState() == DeckState.alive) return;

        //Если все ячейки ранены, убиваем их
        kill();

        //Оповещаем поле об убитом корабле
        field.onShipKilled(this);
    }




    //Присоединяет новый deck
    //deck не должен быть присоединен к другому ship и должен находится в нужном cell
    public void addDeck(Deck deck) {
        decks.add(deck);
        deck.setShip(this);
        sortDecks();
    }

    //Сортирует палубы
    private void sortDecks() {
        Cell icell, jcell;
        for (int i = 0; i < decks.size() - 1; i++)
            for (int j = i + 1; j < decks.size(); j++) {
                icell = decks.get(i).getCell();
                jcell = decks.get(j).getCell();
                if (icell.getX() < jcell.getX() || icell.getX() == jcell.getX() && icell.getY() < jcell.getY()) {
                    Deck d = decks.get(i);
                    decks.set(i, decks.get(j));
                    decks.set(j, d);
                }
            }
    }
}
