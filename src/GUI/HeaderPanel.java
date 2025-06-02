package GUI;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    public HeaderPanel() {
        setBackground(new Color(33, 37, 41));
        setPreferredSize(new Dimension(0, 60));
        setLayout(new BorderLayout());

        // Logo và Tiêu đề
        JLabel lblTitle = new JLabel("Quản Lý Bán Sách");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        add(lblTitle, BorderLayout.WEST);

        // Menu tài khoản (Dropdown)
        JPopupMenu accountMenu = new JPopupMenu();
        JMenuItem menuAccount = new JMenuItem("Tài khoản");
        JMenuItem menuSettings = new JMenuItem("Cài đặt");
        JMenuItem menuLogout = new JMenuItem("Đăng xuất");
        accountMenu.add(menuAccount);
        accountMenu.add(menuSettings);
        accountMenu.addSeparator();
        accountMenu.add(menuLogout);

        JButton btnAccount = new JButton("Admin");
        btnAccount.setForeground(Color.WHITE);
        btnAccount.setBackground(new Color(33, 37, 41));
        btnAccount.setFocusPainted(false);
        btnAccount.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        btnAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                accountMenu.show(btnAccount, btnAccount.getWidth() - accountMenu.getPreferredSize().width, btnAccount.getHeight());
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAccount.setBackground(new Color(52, 58, 64));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAccount.setBackground(new Color(33, 37, 41));
            }
        });
        add(btnAccount, BorderLayout.EAST);
    }
}