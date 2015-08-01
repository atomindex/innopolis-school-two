package battleship;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Atom on 7/29/2015.
 */

/*
    Делит поле на 9 частей, выбирает рандомно часть и стреляет рандомно по всей диагонали в этой части.
    После чего стреляет по диагоналям смещенных на 2 клетки вправо, затем по диагоналям смещенных на 1 и 3 клетки вправо.
    При попадании в корабль определяет ячейки в которых может находится остальная часть корабля и стреляет по ним.
*/

//Умный бот
public class CleverBot extends Player {
    private ArrayList<ArrayList<int[]>> plan;

    //Индекс текущего листа с точками
    private int arrIndex;
    //Лимит для списка листов с точками (позволяет разбить листы на группы по 9 штук)
    private int currentStageLimit;
    //Координаты последнего попадания
    private int[] lastInjure;



    public CleverBot(String name) {
        super(name);
        playerType = PlayerType.bot;
        currentStageLimit = 9;
        plan = makePlan();
    }


    //Отмечает на карте ответ другого игрока
    @Override
    public void mark(String coords, char value) {
        super.mark(coords, value);

        //Запоминаем или сбрасываем координаты последнего попадания
        switch (value) {
            case 'R':
                lastInjure = Field.strToCoords(coords);
                break;

            case 'K':
            case 'L':
                lastInjure = null;
                break;
        }
    }



    //Добавляет координаты, если они не выходят за пределы и ячейка свободна
    private void addCoords(int x, int y, ArrayList<int[]> list) {
        Cell[][] enemyCells = enemyField.getCells();
        if (x < 0 || x >= enemyCells.length) return;
        if (y < 0 || y >= enemyCells.length) return;
        if (enemyCells[y][x].getState() != CellState.unknown) return;

        list.add(new int[] { x, y });
    }

    //Возвращает список координат, в которых может находится палуба
    private ArrayList<int[]> getSuspectCoords(int x, int y) {
        ArrayList<int[]> result = new ArrayList<int[]>();

        Cell[][] enemyCells = enemyField.getCells();

        //Смещаемся влево, для поиска смежных палуб
        int lx = x - 1;
        while (lx >= 0 && enemyCells[y][lx].getState() == CellState.hasDeck)
            lx--;

        //Смещаемся вправо, для поиска смежных палуб
        int rx = x + 1;
        while (rx < enemyCells.length && enemyCells[y][rx].getState() == CellState.hasDeck)
            rx++;

        //Смещаемся вверх, для поиска смежных палуб
        int ty = y - 1;
        while (ty >= 0 && enemyCells[ty][x].getState() == CellState.hasDeck)
            ty--;

        //Смещаемся вниз, для поиска смежных палуб
        int by = y + 1;
        while (by < enemyCells.length && enemyCells[by][x].getState() == CellState.hasDeck)
            by++;

        //Определяем в какой стороне были найдены палубы и добавляем координаты в список
        if (x - lx > 1 || rx - x > 1) {
            addCoords(lx, y, result);
            addCoords(rx, y, result);
        } else if (y - ty > 1 || by - y > 1) {
            addCoords(x, ty, result);
            addCoords(x, by, result);
        } else {
            addCoords(lx, y, result);
            addCoords(rx, y, result);
            addCoords(x, ty, result);
            addCoords(x, by, result);
        }

        return result;
    }

    //Создает массив с точками, обозначающих план стрельбы
    private ArrayList<ArrayList<int[]>> makePlan() {
        ArrayList<ArrayList<int[]>> plan = new ArrayList<ArrayList<int[]>>();

        for (int i = 0; i < 9 * 3; i++)
            plan.add(new ArrayList<int[]>());

        for (int y = 0; y < 10; y++) {
            //1-е диагональные линии
            for (int x = y % 4; x < 10; x += 4) {
                int arrIndex = 3 * (y / 4) + x / 4;
                plan.get(arrIndex).add(new int[] { x, y });
            }

            //2-е диагональные линии
            for (int x = (y + 2) % 4; x < 10; x += 4) {
                int arrIndex = 3 * (y / 4) + x / 4 + 9;
                plan.get(arrIndex).add(new int[] { x, y });
            }

            //3-е диагональные линии
            for (int x = (y + 1) % 4; x < 10; x += 4) {
                int arrIndex = 3 * (y / 4) + x / 4 + 18;
                plan.get(arrIndex).add(new int[] { x, y });
            }

            //4-е диагональные линии
            for (int x = (y + 3) % 4; x < 10; x += 4) {
                int arrIndex = 3 * (y / 4) + x / 4 + 18;
                plan.get(arrIndex).add(new int[] { x, y });
            }
        }

        return plan;
    }

    //Возвращает координаты, куда бот решил бить
    public String getDecision() {
        if (winner) return null;

        Random r = new Random();
        int elIndex = 0;

        //Если было попадание выбираем координату, чтобы добить корабль
        if (lastInjure != null) {
            ArrayList<int[]> suspectCoords = getSuspectCoords(lastInjure[0], lastInjure[1]);
            if (suspectCoords.size() > 0) {
                int[] coords = suspectCoords.get(r.nextInt(suspectCoords.size()));
                return Field.coordsToStr(coords[0], coords[1]);
            } else lastInjure = null;
        }

        Cell[][] enemyCells = enemyField.getCells();
        int[] coords = null;

        mainloop: while (plan.size() > 0) {
            //Выбираем случайную группу с листом координат
            arrIndex = r.nextInt(Math.min(plan.size(), currentStageLimit));

            //Удаляем элемент списка, если в нем нет листов координат
            if (plan.get(arrIndex).size() == 0) {
                plan.remove(arrIndex);
                currentStageLimit--;
                if (currentStageLimit == 0) currentStageLimit = 9;
                continue;
            }

            //Выбираем рандомно координату, пока не выбирется свободная
            while (plan.get(arrIndex).size() > 0) {
                elIndex = r.nextInt(plan.get(arrIndex).size());
                int[] c = plan.get(arrIndex).get(elIndex);
                plan.get(arrIndex).remove(elIndex);
                if (enemyCells[ c[1] ][ c[0] ].getState() == CellState.unknown) {
                    coords = c;
                    break mainloop;
                }
            }
        }

        if (coords == null) return null;

        return Field.coordsToStr(coords[0], coords[1]);
    }

}
