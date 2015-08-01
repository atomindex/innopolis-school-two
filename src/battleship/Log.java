package battleship;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Atom on 7/30/2015.
 */

//Лог
public class Log implements Serializable {
    static final long serialVersionUID = 1;

    //Список логов
    private ArrayList<LogItem> list;



    public Log() {
        list = new ArrayList<LogItem>();
    }



    //Добавляет лог
    public void addLog(String playerName, String coords, char answer) {
        list.add(new LogItem(playerName, coords, answer));
    }

    //Выводит список логов
    public void printLog() {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        System.out.print("\n");
    }

    //Печатает последний лог
    public void printLast() {
        if (list.size() > 0)
            System.out.println(list.get(list.size() - 1));
    }

}
