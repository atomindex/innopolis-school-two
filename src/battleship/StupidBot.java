package battleship;

import java.util.Random;

/**
 * Created by Atom on 7/28/2015.
 */

//Глупый бот
public class StupidBot extends Player {

    public StupidBot(String name) {
        super(name);
        playerType = PlayerType.bot;
    }



    //Возвращает координаты, куда бот решил бить
    public String getDecision() {
        if (winner) return null;

        Random r = new Random();

        while (true) {
            int x = r.nextInt(field.size);
            int y = r.nextInt(field.size);

            Cell cell = enemyField.getCell(x,y);
            if (cell.getState() == CellState.unknown)
                return Field.coordsToStr(x, y);
        }

    }

}
