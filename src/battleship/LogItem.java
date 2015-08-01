package battleship;

import java.io.Serializable;

/**
 * Created by Atom on 7/30/2015.
 */
public class LogItem implements Serializable {
    static final long serialVersionUID = 1;

    //Имя игрока
    private String playerName;
    //Коориданты
    private String coords;
    //Ответ на данные координаты
    private char answer;



    public LogItem(String playerName, String coords, char answer) {
        this.playerName = playerName;
        this.coords = coords;
        this.answer = answer;
    }



    //Возвращает лог в виде строки
    public String toString() {
        String type;

        switch (answer) {
            case 'M': type = "Мимо";     break;
            case 'R': type = "Ранен";    break;
            case 'K': type = "Убит";     break;
            case 'L': type = "Проиграл"; break;
            default: type = "";
        }

        return String.format("%s: %s %s", playerName, coords, type);
    }
}
