package org.atcraftmc.ofmlite.ui;

import org.atcraftmc.ofmlite.SharedContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public final class OptionsUI extends JDialog implements Refreshable {
    private JPanel contentPane;
    private JButton confirmButton;
    private JButton cancelButton;
    private JTextField tf_frpcFile;
    private JTextField tf_startupCommand;
    private JComboBox<String> themeSelector;
    private JButton reloadButton;
    private JTextField tf_globalToken;
    private JSpinner op_tunnelLogCount;
    private JButton frpcGuideButton;

    public OptionsUI() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(confirmButton);

        this.themeSelector.setModel(new DefaultComboBoxModel<>(new String[]{"Dark", "Light", "Darcula"}));

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        reloadUI();

        this.reloadButton.addActionListener(e -> reloadUI());
        this.confirmButton.addActionListener(e -> onOK());
        this.cancelButton.addActionListener(e -> onCancel());
        this.frpcGuideButton.addActionListener(e -> SelectFrpcUI.open(this));
    }

    @Override
    public void reloadUI() {
        SharedContext.loadConfig();
        this.themeSelector.setSelectedItem(SharedContext.CONFIG.getProperty("theme"));
        this.tf_frpcFile.setText(SharedContext.CONFIG.getProperty("frpc"));
        this.tf_startupCommand.setText(SharedContext.CONFIG.getProperty("command"));
        this.tf_globalToken.setText(SharedContext.CONFIG.getProperty("global_token"));
        this.op_tunnelLogCount.setValue(Integer.parseInt(SharedContext.CONFIG.getProperty("tunnel_log_count")));
    }

    private void onOK() {
        SharedContext.CONFIG.setProperty("theme", Objects.requireNonNull(this.themeSelector.getSelectedItem()).toString());
        SharedContext.CONFIG.setProperty("frpc", Objects.requireNonNull(this.tf_frpcFile.getText()));
        SharedContext.CONFIG.setProperty("command", Objects.requireNonNull(this.tf_startupCommand.getText()));
        SharedContext.CONFIG.setProperty("global_token", Objects.requireNonNull(this.tf_globalToken.getText()));
        SharedContext.CONFIG.setProperty("tunnel_log_count", Objects.requireNonNull(op_tunnelLogCount.getValue()).toString());
        SharedContext.saveConfig();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        confirmButton = new JButton();
        confirmButton.setText("完成");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(confirmButton, gbc);
        cancelButton = new JButton();
        cancelButton.setText("取消");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel2.add(cancelButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer1, gbc);
        reloadButton = new JButton();
        reloadButton.setText("重载设定");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel2.add(reloadButton, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer2, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel3, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Frpc可执行文件位置");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label1, gbc);
        tf_frpcFile = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel3.add(tf_frpcFile, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("启动命令(不要加程序文件)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel3.add(label2, gbc);
        tf_startupCommand = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel3.add(tf_startupCommand, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("UI风格");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label3, gbc);
        themeSelector = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel3.add(themeSelector, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("OFManagerLite - 1.0 作者:GrassBlock2022");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel3.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("©ATCraft Network 2024, ARR");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel3.add(label5, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("全局Token");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label6, gbc);
        tf_globalToken = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel3.add(tf_globalToken, gbc);
        op_tunnelLogCount = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel3.add(op_tunnelLogCount, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("单条隧道保存的日志长度");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label7, gbc);
        frpcGuideButton = new JButton();
        frpcGuideButton.setText("向导");
        frpcGuideButton.setToolTipText("选择一个适合您的Frpc");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel3.add(frpcGuideButton, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add(spacer6, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
