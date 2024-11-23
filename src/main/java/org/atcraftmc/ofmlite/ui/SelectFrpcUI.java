package org.atcraftmc.ofmlite.ui;

import org.atcraftmc.ofmlite.SharedContext;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Objects;

public class SelectFrpcUI extends JDialog {
    public static final String WIN_64 = "Windows(x64)";
    public static final String WIN_32 = "Windows(x86)";
    public static final String WIN_ARM_64 = "Windows(ARM-64)";
    public static final String LINUX_64 = "Linux(x64)";
    public static final String LINUX_32 = "Linux(x86)";
    public static final String MACOS = "Mac OS X";
    private static final String MACOS_ARM = "Mac OS X (Arm)";

    private final Refreshable parent;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField localFrpcLocation;
    private JButton chooseLocalFrpcButton;
    private JComboBox<String> frpcImageList;
    private JRadioButton useLocalFrpc;
    private JRadioButton downloadFrpc;
    private ButtonGroup frpcSourceSelector;

    public SelectFrpcUI(Refreshable parent) {
        this.parent = parent;
        setContentPane(this.contentPane);
        setModal(true);
        getRootPane().setDefaultButton(this.buttonOK);

        this.buttonOK.addActionListener(e -> onOK());
        this.buttonCancel.addActionListener(e -> onCancel());
        this.contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        this.useLocalFrpc.setSelected(true);
        this.downloadFrpc.setSelected(false);
        this.frpcImageList.setEnabled(false);
        this.localFrpcLocation.setEnabled(true);
        this.chooseLocalFrpcButton.setEnabled(true);

        this.useLocalFrpc.addActionListener(e -> {
            this.frpcImageList.setEnabled(false);
            this.localFrpcLocation.setEnabled(true);
            this.chooseLocalFrpcButton.setEnabled(true);
        });
        this.downloadFrpc.addActionListener(e -> {
            this.frpcImageList.setEnabled(true);
            this.localFrpcLocation.setEnabled(false);
            this.chooseLocalFrpcButton.setEnabled(false);
        });
        this.chooseLocalFrpcButton.addActionListener(e -> {
            var frame = new JFrame();
            var chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setDialogTitle("选择Frpc可执行文件位置");
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setCurrentDirectory(new File(SharedContext.RUNTIME_PATH));

            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.canRead();
                }

                @Override
                public String getDescription() {
                    return "适用于当前平台的可执行程序(.exe等)";
                }
            });

            chooser.addPropertyChangeListener(event -> {
                if (Objects.equals(event.getPropertyName(), JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    var f = (File) event.getNewValue();
                    if (f == null) {
                        return;
                    }

                    this.localFrpcLocation.setText(f.getAbsolutePath());
                }
            });

            chooser.setVisible(true);
            chooser.showOpenDialog(frame);
        });

        this.frpcImageList.addItem(WIN_64);
        this.frpcImageList.addItem(WIN_32);
        this.frpcImageList.addItem(WIN_ARM_64);
        this.frpcImageList.addItem(LINUX_64);
        this.frpcImageList.addItem(LINUX_32);
        this.frpcImageList.addItem(MACOS);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    public static void open(Refreshable parent) {
        var dialog = new SelectFrpcUI(parent);
        dialog.setResizable(false);
        dialog.setTitle("选择Frpc - Frpc选择向导");
        dialog.setSize(500, 350);
        dialog.setVisible(true);
    }

    private static String getDownloadLink(String platform) {
        return switch (platform) {
            case WIN_32 -> "https://o.of.cd/client/OpenFRP_0.60.1_c6b0deb1_20240914/frpc_windows_386.zip";
            case WIN_64 -> "https://o.of.cd/client/OpenFRP_0.60.1_c6b0deb1_20240914/frpc_windows_amd64.zip";
            case WIN_ARM_64 -> "https://o.of.cd/client/OpenFRP_0.60.1_c6b0deb1_20240914/frpc_windows_arm64.zip";
            case LINUX_64 -> "https://o.of.cd/client/OpenFRP_0.60.1_c6b0deb1_20240914/frpc_linux_amd64.tar.gz";
            case LINUX_32 -> "https://o.of.cd/client/OpenFRP_0.60.1_c6b0deb1_20240914/frpc_linux_386.tar.gz";
            case MACOS -> "https://o.of.cd/client/OpenFRP_0.60.1_c6b0deb1_20240914/frpc_darwin_amd64.tar.gz";
            case MACOS_ARM -> "https://o.of.cd/client/OpenFRP_0.60.1_c6b0deb1_20240914/frpc_darwin_arm64.tar.gz";
            default -> throw new IllegalStateException("Unexpected value: " + platform);
        };
    }

    private static File getDistFile(String platform) {
        var fileName = switch (platform) {
            case WIN_32 -> "frpc_windows_i386-ofmlite-dist.exe";
            case WIN_64 -> "frpc_windows_amd64-ofmlite-dist.exe";
            case WIN_ARM_64 -> "frpc_windows_arm64-ofmlite-dist.exe";
            case LINUX_64 -> "frpc_linux_64-ofmlite-dist";
            case LINUX_32 -> "frpc_linux_32-ofmlite-dist";
            case MACOS -> "frpc_darwin_amd64.tar.gz";
            case MACOS_ARM -> "frpc_arm64.tar.gz";
            default -> throw new IllegalStateException("Unexpected value: " + platform);
        };

        return new File(SharedContext.RUNTIME_PATH + "/dists/" + fileName);
    }

    private void onOK() {
        if (this.useLocalFrpc.isSelected() && localFrpcLocation.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this.rootPane, "请选择一个有效的Frpc文件!", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (this.useLocalFrpc.isSelected()) {
            SharedContext.CONFIG.setProperty("frpc", this.localFrpcLocation.getText());
        } else {
            var platform = this.frpcImageList.getSelectedItem().toString();

            var url = getDownloadLink(platform);
            var file = getDistFile(platform);

            SharedContext.CONFIG.setProperty("frpc", file.getAbsolutePath());

            DownloadUI.open(file, url);
        }

        SharedContext.saveConfig();

        if (this.parent != null) {
            this.parent.reloadUI();
        }

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
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        buttonOK = new JButton();
        buttonOK.setText("确认");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel2.add(buttonOK, gbc);
        buttonCancel = new JButton();
        buttonCancel.setText("取消");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonCancel, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel3, gbc);
        localFrpcLocation = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel3.add(localFrpcLocation, gbc);
        chooseLocalFrpcButton = new JButton();
        chooseLocalFrpcButton.setText("选择文件");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel3.add(chooseLocalFrpcButton, gbc);
        useLocalFrpc = new JRadioButton();
        useLocalFrpc.setText("使用本地Frpc");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(useLocalFrpc, gbc);
        downloadFrpc = new JRadioButton();
        downloadFrpc.setText("下载Frpc");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(downloadFrpc, gbc);
        frpcImageList = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 0);
        panel3.add(frpcImageList, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer3, gbc);
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(4);
        label1.setHorizontalTextPosition(4);
        label1.setText("注: 云端下载的Frpc固定保存到<程序路径>/dists/<文件>");
        label1.setVerticalAlignment(3);
        label1.setVerticalTextPosition(3);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel3.add(label1, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(spacer6, gbc);
        frpcSourceSelector = new ButtonGroup();
        frpcSourceSelector.add(useLocalFrpc);
        frpcSourceSelector.add(downloadFrpc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
