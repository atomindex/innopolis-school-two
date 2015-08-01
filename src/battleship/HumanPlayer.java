package battleship;

import java.util.Scanner;

/**
 * Created by Atom on 7/28/2015.
 */

//Игрок-человек
public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
        playerType = PlayerType.human;
    }

    //Запрос координат
    public String getDecision() {
        Scanner in = new Scanner(System.in);
        String answer = in.nextLine();
        return answer;
    }

}
