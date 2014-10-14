package com.andreldm.rcontrol.server;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
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
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

public class Window extends JFrame implements IMessageReceiver {
	private static final long serialVersionUID = 1265321415204048431L;

	private JLabel lblStatus;
	private JButton btnStartStop;
	private JScrollPane scrollPane;
	private JCheckBox chkStartServer;
	private DefaultListModel<String> listModel;
	private SystemTray tray;
	private TrayIcon trayIcon;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private boolean isServerRunning = false;
	private final int MSG_LIMIT = 100;

	private Server server = null;
	private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

	public Window() {
		initUI();
		MessageDispatcher.getInstance().registerReceiver(this);
		
		boolean autostart = prefs.getBoolean("auto-start-server", false);
		chkStartServer.setSelected(autostart);
		if(autostart) {
			startServer();
		}
	}

	public final void initUI() {
		MigLayout layout = new MigLayout("", "[grow][]", "[][][grow]");
		Container container = getContentPane(); 
		container.setLayout(layout);

        lblStatus = new JLabel();
        lblStatus.setText("Server is NOT running");
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

        listModel = new DefaultListModel<>();
        
        JList<String> listMsg = new JList<>();
        listMsg.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMsg.setModel(listModel);

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(listMsg);

        container.add(lblStatus, "cell 0 0, grow");
        container.add(btnStartStop, "cell 1 0, grow");
        container.add(chkStartServer, "cell 0 1");
        container.add(scrollPane, "cell 0 2, grow, span 2");

        setIconImage(new ImageIcon(Window.class.getResource("icon.png")).getImage());
        setTitle("Message Boxes");
        setSize(450, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setupTrayIcon();
	}
	
	private void setupTrayIcon() {
		if (!SystemTray.isSupported()) {
			System.err.println("system tray NOT supported");
			return;
		}

		tray = SystemTray.getSystemTray();

		trayIcon = new TrayIcon(new ImageIcon(Window.class.getResource("icon.xpm")).getImage(), "RControl");
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
		trayIcon.setImageAutoSize(true);
		
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				try {
					switch (e.getNewState()) {
					case ICONIFIED:
					case 7:
						tray.add(trayIcon);
						setVisible(false);
						break;
						
					case NORMAL:
					case MAXIMIZED_BOTH:
						tray.remove(trayIcon);
						setVisible(true);
						break;
						
					default:
						break;
					}
				} catch (AWTException ex) {
					// ignore
				}
			}
		});
	}

	private void toggleServer () {
		if(isServerRunning) {
			stopServer();
		} else {
			startServer();
		}
	}

	private void startServer() {
		server = new Server();
	    new Thread(server).start();

		btnStartStop.setText("Stop");
		MessageDispatcher.getInstance().dispatchMessage("Starting server");
        lblStatus.setText("Server is running");
		lblStatus.setForeground(Color.GREEN);
		
		isServerRunning = true;
	}
	
	private void stopServer() {
		if(server != null)
			server.shutdown();

		MessageDispatcher.getInstance().dispatchMessage("Stop server");
		btnStartStop.setText("Start");
        lblStatus.setText("Server is NOT running");
		lblStatus.setForeground(Color.RED);

		isServerRunning = false;
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

	public static void main(String[] args) {
		try {
			Util.loadLib();

			switch(Util.whichOS()) {
			case Constants.OS_LINUX:
			    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			    break;
			case Constants.OS_WINDOWS:
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			    break;
			default:
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    break;
			}

	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                Window w = new Window();
	                w.setVisible(true);
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
