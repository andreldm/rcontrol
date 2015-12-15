package com.andreldm.rcontrol.server;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class Window extends JFrame implements IMessageReceiver {
	private static final long serialVersionUID = 1265321415204048431L;

	private JLabel lblStatus;
	private JButton btnStartStop;
	private JScrollPane scrollPane;
	private JCheckBox chkStartServer;
	private JCheckBox chkStartMinimized;
	private DefaultListModel<String> listModel;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private boolean isServerRunning = false;
	private final int MSG_LIMIT = 100;

	private Server server = null;
	private Service service = null;
	private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

	public Window() {
		boolean autostart = prefs.getBoolean("auto-start-server", false);
		boolean startminimized = prefs.getBoolean("start-minimized", false);
		
		server = new Server();
		service = new Service();

		initUI();
        setupTrayIcon();
		MessageDispatcher.getInstance().registerReceiver(this);

		if(autostart) {
			chkStartServer.setSelected(true);
			startServer();
		}

		if(startminimized) {
			chkStartMinimized.setSelected(true);
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	    			setState(JFrame.ICONIFIED);
	            }
	        });
		}
	}

	public final void initUI() {
		MigLayout layout = new MigLayout("", "[grow][]", "[][][grow]");
		Container container = getContentPane(); 
		container.setLayout(layout);

        lblStatus = new JLabel("Server is NOT running");
        lblStatus.setForeground(Color.RED);

        btnStartStop = new JButton("Start");
        btnStartStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleServer();
			}
		});

        chkStartServer = new JCheckBox("Auto-start server");
        chkStartServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("auto-start-server", chkStartServer.isSelected());
			}
		});

        chkStartMinimized = new JCheckBox("Start minimized");
        chkStartMinimized.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("start-minimized", chkStartMinimized.isSelected());
			}
		});

        listModel = new DefaultListModel<>();

        JList<String> listMsg = new JList<>();
        listMsg.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMsg.setModel(listModel);

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(listMsg);

        container.add(lblStatus, "cell 0 0, grow");
        container.add(btnStartStop, "cell 1 0, grow");
        container.add(chkStartServer, "cell 0 1, split 2");
        container.add(chkStartMinimized, "cell 0 1");
        container.add(scrollPane, "cell 0 2, grow, span 2");

        setIconImage(new ImageIcon(Window.class.getResource("icon.png")).getImage());
        setTitle("RControl");
        setSize(450, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void setupTrayIcon() {
		if (!SystemTray.isSupported()) {
			System.err.println("system tray NOT supported");
			return;
		}

		final SystemTray tray = SystemTray.getSystemTray();

		final TrayIcon trayIcon = new TrayIcon(getIconImage(), getTitle());
		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(true);
				setExtendedState(JFrame.NORMAL);
			}
		});

		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				try {
					int state = e.getNewState();

					if(state == ICONIFIED || state == 7) {
						tray.add(trayIcon);
						setVisible(false);
						return;
					}

					if(state == NORMAL || state == MAXIMIZED_BOTH) {
						tray.remove(trayIcon);
						setVisible(true);
						return;
					}
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private void toggleServer() {
		if (isServerRunning) {
			stopServer();
		} else {
			startServer();
		}
	}

	private void startServer() {
		btnStartStop.setText("Starting...");
		btnStartStop.setEnabled(false);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				int port = Util.findFreePort();
				server.start(port);
				service.start(port);

				btnStartStop.setText("Stop");
				btnStartStop.setEnabled(true);

				MessageDispatcher.getInstance().dispatch("Starting server at " + port);
				lblStatus.setText("Server is running");
				lblStatus.setForeground(Color.GREEN);

				isServerRunning = true;
			}
		}).start();
	}

	private void stopServer() {
		btnStartStop.setText("Stopping...");
		btnStartStop.setEnabled(false);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (server != null) server.stop();
				if (service != null) service.stop();

				btnStartStop.setText("Start");
				btnStartStop.setEnabled(true);

				MessageDispatcher.getInstance().dispatch("Stop server");
		        lblStatus.setText("Server is NOT running");
				lblStatus.setForeground(Color.RED);

				isServerRunning = false;
			}
		}).start();
	}

	@Override
	public void receiveMessage(String msg) {
		if (listModel.getSize() >= MSG_LIMIT) {
			listModel.remove(0);
		}

		String time = dateFormat.format(new Date());
		listModel.addElement(time + " - " + msg);

        SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	JScrollBar vbar = scrollPane.getVerticalScrollBar();
	    		vbar.setValue(vbar.getMaximum());
	        }
        });
	}
}
