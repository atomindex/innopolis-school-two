package battleInterface;

import battleship.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Atom on 7/31/2015.
 */


public class BattleForm extends JFrame {
    private Game game;

    private ThePanel menu;

    private int player1Type;
    private int player2Type;

    private String[] playersNames;

    private JLabel playersLabel;
    private JLabel saveLoadLabel;

    public BattleForm() {
        int bwidth = 75;
        int bheight = 33;

        playersNames = new String[] { "Бот", "Умный бот", "Человек" };

        setBounds(10, 10, 800, 720);
        setLayout(null);

        menu = new ThePanel();
        menu.setSize(200, 600);
        menu.setLayout(null);
        menu.setBorder(new EmptyBorder(5, 5, 15, 5));
        add(menu);

        TheButton button1 = new TheButton("Бот");
        menu.add(button1);
        button1.setBounds(20, 20, bwidth, bheight);
        button1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                player1Type = 0;
                updatePlayersLabel();
            }
        });

        TheButton button2 = new TheButton("Умный бот");
        menu.add(button2);
        button2.setBounds(20, button1.getY() + button1.getHeight() + 10, bwidth, bheight);
        button2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                player1Type = 1;
                updatePlayersLabel();
            }
        });

        TheButton button3 = new TheButton("Человек");
        menu.add(button3);
        button3.setBounds(20, button2.getY() + button2.getHeight() + 10, bwidth, bheight);
        button3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                player1Type = 2;
                updatePlayersLabel();
            }
        });


        TheButton button4 = new TheButton("Бот");
        menu.add(button4);
        button4.setBounds(button1.getX() + button1.getWidth() + 10, button1.getY(), bwidth, bheight);
        button4.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                player2Type = 0;
                updatePlayersLabel();
            }
        });

        TheButton button5 = new TheButton("Умный бот");
        menu.add(button5);
        button5.setBounds(button2.getX() + button2.getWidth() + 10, button2.getY(), bwidth, bheight);
        button5.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                player2Type = 1;
                updatePlayersLabel();
            }
        });

        TheButton button6 = new TheButton("Человек");
        menu.add(button6);
        button6.setBounds(button3.getX() + button3.getWidth() + 10, button3.getY(), bwidth, bheight);
        button6.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                player2Type = 2;
                updatePlayersLabel();
            }
        });

        playersLabel = new JLabel("", SwingConstants.CENTER);
        playersLabel.setBounds(20, button6.getY() + button6.getHeight() + 5, menu.getWidth() - 40, 20);
        menu.add(playersLabel);
        updatePlayersLabel();

        TheButton newGame = new TheButton("Начать игру");
        newGame.setColors(Color.decode("#E53935"), Color.decode("#F44336"));
        menu.add(newGame);
        newGame.setBounds(playersLabel.getX(), playersLabel.getY() + playersLabel.getHeight() + 9, playersLabel.getWidth(), bheight);
        newGame.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (game != null) {
                    remove(game.getPanel());
                }

                Player player1 = newPlayer(player1Type, 1);
                player1.autoArrangeShips();
                Player player2 = newPlayer(player2Type, 2);
                player2.autoArrangeShips();

                game = new Game();

                game.setBotDelay(100);
                game.setBoth(true);
                game.setShowLockView(false);

                add(game.getPanel());
                game.getPanel().setLocation(menu.getWidth(), 0);
                game.getPanel().repaint();

                Thread thread = new Thread(new StartGame(game, player1, player2, false));
                thread.start();
            }
        });

        TheButton saveButton = new TheButton("Сохранить игру");
        menu.add(saveButton);
        saveButton.setBounds(
                newGame.getX(),
                newGame.getY() + newGame.getHeight() + 10,
                newGame.getWidth(),
                newGame.getHeight()
        );
        saveButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                game.save();
                saveLoadLabel.setText("Игра сохранена");
            }
        });

        TheButton loadButton = new TheButton("Загрузить игру");
        menu.add(loadButton);
        loadButton.setBounds(
                saveButton.getX(),
                saveButton.getY() + saveButton.getHeight() + 10,
                saveButton.getWidth(), saveButton.getHeight()
        );
        loadButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (game != null) {
                    remove(game.getPanel());
                }

                game = Game.loadGame();
                if (game != null) {

                    System.out.println("### LOG ###");
                    game.getLog().printLog();

                    add(game.getPanel());
                    game.getPanel().setLocation(menu.getWidth(), 0);

                    game.getPanel().repaint();

                    Thread thread = new Thread(new StartGame(game, null, null, true));
                    thread.start();

                    saveLoadLabel.setText("Игра загружена");
                } else
                    saveLoadLabel.setText("Ошибка загрузки");
            }
        });

        saveLoadLabel = new JLabel("", SwingConstants.CENTER);
        menu.add(saveLoadLabel);
        saveLoadLabel.setBounds(loadButton.getX(), loadButton.getY() + loadButton.getHeight(), loadButton.getWidth(), 20);

        menu.setSize(menu.getWidth(), saveLoadLabel.getY() + saveLoadLabel.getHeight() + 25);
    }

    private Player newPlayer(int type, int num) {
        switch (type) {
            case 0: return new StupidBot("Бот " + num);
            case 1: return new CleverBot("Умный бот " + num);
            case 2: return new HumanPlayer("Игрок " + num);
        }
        return null;
    }

    private void updatePlayersLabel() {
        playersLabel.setText(playersNames[player1Type] + " vs " + playersNames[player2Type]);
    }
}


class StartGame implements Runnable {
    private Game game;
    private Player player1;
    private Player player2;
    private boolean resume;

    public StartGame(Game game, Player player1, Player player2, boolean resume) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.resume = resume;
    }

    public void run() {
        if (resume)
            game.tryResume();
        else
            game.start(player1, player2);
    }

}