package battleship;

import battleInterface.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;

/**
 * Created by Atom on 7/29/2015.
 */

//Игра
public class Game implements Serializable {
    static final long serialVersionUID = 1;

    //Логи
    private Log log;

    //Задержка бота
    private int botDelay;
    //Показ двух панелей двух игроков одновременно
    private boolean showBoth;
    //Показ затеменения заблокированных панелей
    private boolean showLockView;

    //Главная панель
    transient JPanel mainPanel;

    //Игрок 1
    private Player player1;
    private transient JPanel player1Panel;
    private transient View player1View;
    private transient View player1EnemyView;

    //Игрок 2
    private Player player2;
    private transient JPanel player2Panel;
    private transient View player2View;
    private transient View player2EnemyView;

    //Текущий игрок
    private Player currentPlayer;
    private transient JPanel currentPlayerPanel;
    private transient View currentView;

    //Текущий противник
    private Player currentEnemy;
    private transient JPanel currentEnemyPanel;
    private transient View currentEnemyView;



    public Game() {
        log = new Log();
        mainPanel = new JPanel();
        botDelay = 100;
        showLockView = false;
    }



    //Создает панель из 2х view и подписью
    public static JPanel createView(View myView, View enemyView, Player player) {
        //Панель
        ThePanel panel = new ThePanel();
        panel.setLayout(null);
        panel.setBorder(new EmptyBorder(5, 5, 15, 5));

        //Подпись с именем
        JLabel label = new JLabel(player.getName());
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 18));
        label.setBounds(15, 15, 500, 30);
        panel.add(label);

        //Поле игрока
        myView.setMap(player.getField().getCells());
        myView.setLocation(15, 50);
        myView.lock();
        myView.setShowEmptyCell(false);
        panel.add(myView);

        //Поле для отметок
        enemyView.setMap(player.getEnemyField().getCells());
        enemyView.setLocation(myView.getX() + myView.getWidth() + 40, myView.getY());
        enemyView.setPlayer(player);
        if (player.getPlayerType() == PlayerType.bot)
            enemyView.lock();
        panel.add(enemyView);

        //Устанавливаем размер панели
        panel.setBounds(0, 0, enemyView.getX() + enemyView.getWidth() + 35, enemyView.getY() + enemyView.getHeight() + 45);

        return panel;
    }

    //Инициализация интерфейсов (панелей и полей)
    private void init() {
        if (mainPanel == null)
            mainPanel = new JPanel();

        //Интерфейс игрока 1
        player1View = new View();
        player1EnemyView = new View();
        player1Panel = createView(player1View, player1EnemyView, player1);
        player1Panel.setLocation(0, 0);

        //Интерфейс игрока 2
        player2View = new View();
        player2View.setShowEmptyCell(false);
        player2EnemyView = new View();
        player2Panel = createView(player2View, player2EnemyView, player2);
        player2Panel.setBounds(
                player1Panel.getX(),
                player1Panel.getY() + player1Panel.getHeight(),
                player2Panel.getWidth(),
                player2Panel.getHeight()
        );

        //Устанавливаем размер, позиции и добавляем панели на главную панель игры
        mainPanel.setSize(player1Panel.getX() * 2 + player1Panel.getWidth(), player2Panel.getY() + player2Panel.getX() + player2Panel.getHeight());
        mainPanel.setLayout(null);
        mainPanel.add(player1Panel);
        mainPanel.add(player2Panel);
    }

    //Обновляет доступ к панелям и вьюхам (разблокирует текущего пользователя и блокирует противника)
    private void updateViewsAccess() {
        if (currentPlayer.getPlayerType() != PlayerType.bot)
            currentView.unlock();
        currentEnemyView.lock();

        if (showLockView) {
            currentView.lockView(false);
            currentEnemyView.lockView(true);
        }

        if (!showBoth) {
            currentPlayerPanel.setVisible(true);
            currentEnemyPanel.setVisible(false);
        }
    }



    //Запуск игры
    public void start(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1.setGame(this);
        player2.setGame(this);

        init();

        //Устанавливаем первого пользователя, как текущего
        currentPlayer = player1;
        currentPlayerPanel = player1Panel;
        currentView = player1EnemyView;

        currentEnemy = player2;
        currentEnemyPanel = player2Panel;
        currentEnemyView = player2EnemyView;

        updateViewsAccess();

        if (currentPlayer.getPlayerType() == PlayerType.bot){
            String coords = currentPlayer.getDecision();
            step(coords);
        }
    }

    //Пытается возобновить игру
    public void tryResume() {
        if (currentPlayer.winner == true || currentEnemy.winner == true) {
            end();
        } else if (currentPlayer.getPlayerType() == PlayerType.bot) {
            step(currentPlayer.getDecision());
        }
    }

    //Следующий шаг игры
    public void step(String coords) {
        char answer = currentEnemy.check(coords);
        currentPlayer.mark(coords, answer);

        log.addLog(currentPlayer.getName(), coords, answer);
        log.printLast();

        if (answer == 'L') {
            end();
            return;
        } else if (answer != 'R' && answer != 'K') {
            Player temp = currentPlayer;
            currentPlayer = currentEnemy;
            currentEnemy = temp;

            JPanel tempPanel = currentPlayerPanel;
            currentPlayerPanel = currentEnemyPanel;
            currentEnemyPanel = tempPanel;

            View tempView = currentView;
            currentView = currentEnemyView;
            currentEnemyView = tempView;

            updateViewsAccess();
        }

        if (currentPlayer.getPlayerType() == PlayerType.bot){
            try {
                mainPanel.repaint();
                Thread.sleep(botDelay);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            coords = currentPlayer.getDecision();
            step(coords);
        } else mainPanel.repaint();
    }

    //Остановка игры
    private void end() {
        //Блокируем поля отметок
        player1EnemyView.lock();
        player2EnemyView.lock();

        //Показываем сообщение
        ThePanel message = getMessagePanel("Победил " + (player1.winner ? player1.getName() : player2.getName()));
        message.setLocation(
                (currentPlayerPanel.getWidth() - message.getWidth()) / 2,
                (currentPlayerPanel.getHeight() - message.getHeight()) / 2
        );
        currentPlayerPanel.add(message, 0);

        //Перерисовываем панель
        mainPanel.repaint();
    }



    //Сохранение игры
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream("C:\\save.bs");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        }  catch (Exception e) {
            System.out.println("#Ошибка создания файла");
        }
    }

    //Загрузка игры
    public static Game loadGame() {
        Game game = null;

        //Грузим игру из файла
        try {
            FileInputStream fis = new FileInputStream("C:\\save.bs");
            ObjectInputStream ois = new ObjectInputStream(fis);
            game = (Game)ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            System.out.println("#Ошибка чтения файла");
            System.out.println(e.getMessage());
            return null;
        }

        //Инициализируем панели и поля
        game.init();

        //Устанавливаем текущего игрока
        if (game.currentPlayer == game.player1) {
            game.currentView = game.player1EnemyView;
            game.currentEnemyView = game.player2EnemyView;

            game.currentPlayerPanel = game.player1Panel;
            game.currentEnemyPanel = game.player2Panel;
        } else if (game.currentPlayer == game.player2) {
            game.currentView = game.player2View;
            game.currentEnemyView = game.player1EnemyView;

            game.currentPlayerPanel = game.player2Panel;
            game.currentEnemyPanel = game.player1Panel;
        }

        //Обновляем доступ
        game.updateViewsAccess();

        return game;
    }



    //Возвращает панель с сообщением
    private ThePanel getMessagePanel(String text) {
        ThePanel panel = new ThePanel();

        panel.setBackground(new Color(1, 1, 1, 1));
        panel.setSize(300, 110);
        panel.setLayout(null);
        panel.setBorder(new EmptyBorder(5, 5, 5, 20));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(5, 15, panel.getWidth() - 10, 30);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 18));

        panel.add(label);

        return panel;
    }

    //Возвращает логи
    public Log getLog() {
        return log;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    //Устанавка задержки ботов
    public void setBotDelay(int value) {
        botDelay = value;
    }

    //Установка показа двух панелей одновременно
    public void setBoth(boolean value) {
        showBoth = value;
    }

    //Установка показа затемнения, для блокированных полей
    public void setShowLockView(boolean val) {
        showLockView = val;
        if (player1EnemyView == null) return;

        if (val == false) {
            player1EnemyView.lockView(false);
            player2EnemyView.lockView(false);
        } else {
            player1EnemyView.lockView(false);
            player2EnemyView.lockView(false);
            currentEnemyView.lockView(true);
        }

    }
}
