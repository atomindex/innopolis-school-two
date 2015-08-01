package battleInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Atom on 7/22/2015.
 */

public class ThePanel extends JPanel {
    private float shadowOppacity;

    public ThePanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

            }
        });
        setBackground(new Color(1, 1, 1, 1));
        shadowOppacity = 0.6f;
    }

    public void setShadowOpacity(float opacity) {
        shadowOppacity = opacity;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Insets b = this.getBorder().getBorderInsets(this);
        int shHeight = 15;
        int halfShHeight = shHeight / 2;
        int contentWidth = getWidth() - b.right - b.left;
        int contentHeight = getHeight() - b.top - b.bottom;
        int startShadow = getHeight() - b.bottom - halfShHeight - 1;
        int endShadow = getHeight() - b.bottom + halfShHeight;

        Graphics2D g2d = (Graphics2D)g;
        g2d.setPaint(new GradientPaint(b.left, startShadow, new Color(0,0,0, shadowOppacity), b.left, endShadow, new Color(0, 0, 0, 0)));
        g2d.fillRoundRect(b.left, startShadow, contentWidth, shHeight, halfShHeight, halfShHeight);

        g.setColor(Color.white);
        g.fillRoundRect(b.top, b.left, contentWidth, contentHeight, 1, 1);

        g.setColor(new Color(0, 0, 0, 0.1f));
        g.drawRoundRect(b.top - 1, b.left - 1, contentWidth + 1, contentHeight + 1, 1, 1);

    }
}