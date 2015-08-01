package battleship;

import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Atom on 7/27/2015.
 */

//Игрок
public abstract class Player implements Serializable {
    static final long serialVersionUID = 1;

    private Game game;

    //Поле
    protected Field field;
    //Поле для отметок
    protected EnemyField enemyField;

    //Тип игрока
    protected PlayerType playerType;

    //Показывает что игрок победитель
    protected boolean winner;

    //Имя
    private String name;



    public Player(String name) {
        field = new Field();
        enemyField = new EnemyField();

        this.name = name;
    }



    //Возвращает поле пользователя
    public Field getField() {
        return field;
    }

    //Возвращает поле для отметок
    public EnemyField getEnemyField() {
        return enemyField;
    }



    //Устанока игры
    void setGame(Game game) {
        this.game = game;
    }

    //Возвращает игру, к которой принадлежит игрок
    public Game getGame() {
        return game;
    }

    //Возвращает тип игрока
    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getName() {
        return name;
    }



    //Проверяет наличие корабля в клетке, возвращает ответ буквой
    public char check(String coords) {
        int[] coordsArr = Field.strToCoords(coords);

        Cell cell = field.getCell(coordsArr[0], coordsArr[1]);

        char result = ' ';

        switch (cell.getState()) {
            case unknown: //Не тронутое
            case empty:   //Пустое
            case past:    //Промах
                cell.setState(CellState.past);
                result = 'M';
                break;

            case hasDeck: //Имеет палубу
                switch (cell.getDeck().injure()) {
                    case injured:
                        result = 'R';
                        break;
                    case killed:
                        result = 'K';
                        if (field.getAliveShipsCount() == 0) {
                            result = 'L';
                        }
                        break;
                }
                break;
        }

        return result;
    }

    //Делает отметку на поле
    public void mark(String coords, char value) {
        int[] coordsArr = Field.strToCoords(coords);
        int x = coordsArr[0];
        int y = coordsArr[1];

        switch (value) {
            case 'M': //Мимо
                enemyField.getCell(x, y).setState(CellState.past);
                break;

            case 'R': //Ранен
                enemyField.addDeck(x, y, new Deck(null, DeckState.injured));
                break;

            case 'K': //Убит
            case 'L': //Проиграл
                //Создаем палубу и добавляем ее
                Deck deck = new Deck(null, DeckState.injured);
                enemyField.addDeck(x, y, deck);
                //Создаем корабль и объединяем смежные палубы
                Ship ship = enemyField.joinAdjacentDecks(deck);
                //Убиваем корабль
                ship.kill();
                enemyField.onShipKilled(ship);

                //Если противник проиграл, ставим отметку что текущий игрок выиграл
                if (value == 'L') winner = true;
                break;
        }
    }

    //Возвращает координаты, по которым хочет ударить игрок
    public abstract String getDecision();



    //Автоматическая расстановка
    public void autoArrangeShips() {
        Random random = new Random();
        int[] shipsCounts = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

        for (int i = 0; i < shipsCounts.length; i++) {
            Ship ship = new Ship(shipsCounts[i]);

            int x, y;
            boolean rotate = false;

            while (true) {
                x = random.nextInt(field.size);
                y = random.nextInt(field.size);
                rotate = random.nextInt(100) % 2 == 0;

                if (field.checkPlace(x, y, ship.getDecks().size(), rotate)) break;
            }

            ship.setPosition(x,  y, rotate);
            field.addShip(ship);
        }

    }
}
