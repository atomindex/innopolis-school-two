package battleInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.Rectangle2D;
import java.security.PublicKey;

/**
 * Created by Atom on 7/22/2015.
 */

public class TheButton extends JButton {
    boolean hover;
    String text;

    private Color backgroundColor;
    private Color hoverColor;

    public TheButton(String text) {
        this.text = text;
        hover = false;
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(150, 30));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hover = true;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                hover = false;
                repaint();
            }
        });

        hoverColor = Color.decode("#4DB6AC");
        backgroundColor = Color.decode("#26A69A");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(hover ?  hoverColor : backgroundColor);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 1, 1);

        g2d.setColor(Color.WHITE);
        Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(getText(), g2d);
        g2d.drawString(text, (getWidth() - (int) textBounds.getWidth()) / 2, getHeight() / 2 + (int) textBounds.getHeight() / 2 - 1);
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }

    public void setColors(Color bkColor, Color hvColor) {
        backgroundColor = bkColor;
        hoverColor = hvColor;
    }
}