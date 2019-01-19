package com.hit.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class CacheUnitView extends Observable implements View {
	private String stat = "";
	private String response;
	private JPanel contentPane;
	private JFrame main;
	private JLabel statLabel;

	private static final Logger log = Logger.getLogger("req-res log");
	FileHandler fh;
	SimpleFormatter sf;

	@Override
	public <T> void updateUIData(T t) {
		UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL", Font.PLAIN, 35)));
		response = (String) t;
		// if (response.contains("Total number of DataModels (GET/DELETE/UPDATE
		// requests):"))
		if (response.split("-")[0].equals("statistics") && response.split("-")[1].equals("true"))
			stat = response.split("-")[2];
		statLabel.setText("<html>" + stat.replaceAll("\n", "<br/>") + "</html>");
		Toast toast = new Toast(
				response.split("-")[0].toUpperCase() + " "
						+ (response.split("-")[1].equals("true") ? "request succeed" : "request unsucceed"),
				1980, 1500);
		toast.showtoast();
		log.log(customLogLevel.RESPONSE, (String) t);
	}

	@Override
	public void start() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate localDate = LocalDate.now();
		String path = "src/main/resources/ReqResLog" + dtf.format(localDate).replaceAll("/", "") + ".log";
		try {
			File yourFile = new File(path);
			yourFile.createNewFile(); // if file already exists will do nothing
			FileOutputStream oFile = new FileOutputStream(yourFile, true);
			oFile.close();
			fh = new FileHandler(path, true);
		} catch (SecurityException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // true forces append mode
		sf = new SimpleFormatter();
		fh.setFormatter(sf);
		log.addHandler(fh);
		main = new JFrame("Cache unit UI");
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setBounds(1200, 300, 2176, 1378);
		main.setIconImage(new ImageIcon("src/main/resources/images/icon.png").getImage());
		main.setVisible(true);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setToolTipText("");
		main.setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 2176, 1378);
		contentPane.add(panel);

		JButton showStatBtn = new JButton("Show statistics");
		showStatBtn.setFont(new Font("Tahoma", Font.PLAIN, 43));
		showStatBtn.setBounds(1507, 170, 500, 150);
		showStatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setChanged(); // for notifyObserver
				notifyObservers("{ headers : {action: statistics}, body: []}");// update all the observers
			}
		});
		panel.setLayout(null);

		JButton randomReqBtn = new JButton("Random 5 requests");
		randomReqBtn.setFont(new Font("Tahoma", Font.PLAIN, 43));
		randomReqBtn.setBounds(838, 170, 500, 150);
		panel.add(randomReqBtn);
		randomReqBtn.setIcon(new ImageIcon("src/main/resources/images/loadBtn.png"));

		randomReqBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (int i = 0; i < 5; i++) {
					String str = getRandomString();
					log.log(customLogLevel.REQUEST, str);
					setChanged(); // for notifyObserver
					notifyObservers(str);// update all the observers
				}
			}
		});

		JButton loadReqBtn = new JButton("Load a Request");
		loadReqBtn.setFont(new Font("Tahoma", Font.PLAIN, 43));
		loadReqBtn.setBounds(169, 170, 500, 150);
		panel.add(loadReqBtn);
		loadReqBtn.setIcon(new ImageIcon("src/main/resources/images/loadBtn.png"));

		loadReqBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setPreferredSize(new Dimension(1000, 800));
				fileChooser.setDialogTitle("Load a json request");
				setFileChooserFont(fileChooser.getComponents());
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try { // read the request as string
						FileInputStream fis = new FileInputStream(file);
						byte[] data = new byte[(int) file.length()];
						fis.read(data);
						fis.close();
						String jsonReqeust = new String(data);
						log.log(customLogLevel.REQUEST, jsonReqeust);
						setChanged(); // for notifyObserver
						notifyObservers(jsonReqeust);// update all the observers
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, "problem accessing file" + file.getAbsolutePath());
					}
				}
			}
		});

		showStatBtn.setIcon(new ImageIcon("src/main/resources/images/statBtn.png"));
		panel.add(showStatBtn);

		statLabel = new JLabel();
		statLabel.setBounds(50, 720, 2000, 400);
		panel.add(statLabel);
		statLabel.setFont(new Font("Courier New", Font.PLAIN, 48));
		statLabel.setVisible(true);
		statLabel.setForeground(Color.WHITE);

		JLabel rightLabel = new JLabel();
		rightLabel.setBounds(630, 1100, 1000, 300);
		panel.add(rightLabel);
		rightLabel.setFont(new Font("Courier New", Font.PLAIN, 30));
		rightLabel.setVisible(true);
		rightLabel.setForeground(Color.WHITE);
		rightLabel.setText("© All rights resserved Tomer Shats & Omri Eitan 2018");
		JLabel imageLabel = new JLabel(new ImageIcon("src/main/resources/images/back.jpg"));

		imageLabel.setLabelFor(panel);
		imageLabel.setBounds(0, 0, 2175, 1377);

		panel.add(imageLabel);
		main.add(panel);

		// panel.setVisible(true);
		// statLabel.setVisible(true);
		// rightLabel.setVisible(true);
		// main.setVisible(true);
	}

	private String getRandomString() {

		Random rand = new Random();
		String jsonReq = "{\r\n" + "    \"headers\": {\r\n" + "      \"action\": \"";
		int action = rand.nextInt(3);
		switch (action) {
		case 0:
			jsonReq = jsonReq.concat("UPDATE");
			break;
		case 1:
			jsonReq = jsonReq.concat("DELETE");
			break;
		case 2:
			jsonReq = jsonReq.concat("GET");
			break;
		}
		jsonReq = jsonReq.concat("\"\r\n" + "      },\r\n" + "      \"body\": [");
		int i = 0;
		int reqLenght = rand.nextInt(999) + 1;
		while (i < reqLenght) {
			jsonReq = jsonReq.concat("\r\n" + "          {\"dataModelId\":" + (i + 1) + ", \"content\":");
			if (action != 0)
				jsonReq = jsonReq.concat("null},");
			else
				jsonReq = jsonReq.concat("\""
						+ new RandomString(rand.nextInt(30) + 1, ThreadLocalRandom.current()).nextString() + "\"},");
			i += rand.nextInt(30) + 1;
		}
		jsonReq = jsonReq.concat("\r\n" + "          {\"dataModelId\":" + (i + 1) + ", \"content\":");
		if (action != 0)
			jsonReq = jsonReq.concat("null}\r\n" + "      ] \r\n" + " }");
		else
			jsonReq = jsonReq
					.concat("\"" + new RandomString(rand.nextInt(30) + 1, ThreadLocalRandom.current()).nextString()
							+ "\"}\r\n" + "      ] \r\n" + " }");
		return jsonReq;
	}

	private static void setFileChooserFont(Component[] comp) { // increase file name in file chooser dialog
		for (int x = 0; x < comp.length; x++) {
			if (comp[x] instanceof Container)
				setFileChooserFont(((Container) comp[x]).getComponents());

			try {
				if (comp[x] instanceof JList || comp[x] instanceof JTable)
					comp[x].setFont(comp[x].getFont().deriveFont(comp[x].getFont().getSize() * 3f));
			} catch (Exception e) {
			}
		}
	}

}

class RandomString {

	public String nextString() {
		for (int idx = 0; idx < buf.length; ++idx)
			buf[idx] = symbols[random.nextInt(symbols.length)];
		return new String(buf);
	}

	public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";

	public static final String lower = upper.toLowerCase(Locale.ROOT);

	public static final String digits = "0123456789";

	public static final String alphanum = upper + lower + digits;

	private final Random random;

	private final char[] symbols;

	private final char[] buf;

	public RandomString(int length, Random random, String symbols) {
		if (length < 1)
			throw new IllegalArgumentException();
		if (symbols.length() < 2)
			throw new IllegalArgumentException();
		this.random = Objects.requireNonNull(random);
		this.symbols = symbols.toCharArray();
		this.buf = new char[length];
	}

	public RandomString(int length, Random random) {
		this(length, random, alphanum);
	}
}

@SuppressWarnings("serial")
class Toast extends JFrame {

	// String of toast
	String s;

	// JWindow
	JWindow w;

	Toast(String s, int x, int y) {
		w = new JWindow();

		// make the background transparent
		w.setBackground(new Color(0, 0, 0, 0));

		// create a panel
		JPanel p = new JPanel() {
			public void paintComponent(Graphics g) {
				g.setFont(new Font("Tahoma", Font.CENTER_BASELINE, 39));
				int wid = g.getFontMetrics().stringWidth(s);
				int hei = g.getFontMetrics().getHeight();

				// draw the boundary of the toast and fill it
				g.setColor(new Color(0, 0, 0, 90));
				g.fillRect(10, 10, wid + 30, hei + 10);
				g.setColor(new Color(0, 0, 0, 90));
				g.drawRect(10, 10, wid + 30, hei + 10);

				// set the color of text
				if (s.contains("request succeed"))
					g.setColor(new Color(75, 181, 67, 240)); // green
				else
					g.setColor(new Color(178, 34, 34, 240)); // red
				g.drawString(s, 30, 50);
				int t = 250;

				// draw the shadow of the toast
				for (int i = 0; i < 4; i++) {
					t -= 60;
					g.setColor(new Color(0, 0, 0, t));
					g.drawRect(10 - i, 10 - i, wid + 30 + i * 2, hei + 10 + i * 2);
				}
			}
		};

		w.add(p);
		w.setLocation(x, y);
		w.setSize(900, 400);

	}

	// function to pop up the toast
	void showtoast() {
		try {

			w.setOpacity(1);
			w.setVisible(true);

			// wait for some time
			Thread.sleep(2000);

			// make the message disappear slowly
			for (double d = 1.0; d > 0.2; d -= 0.1) {
				Thread.sleep(100);
				w.setOpacity((float) d);
			}

			// set the visibility to false
			w.setVisible(false);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

@SuppressWarnings("serial")
class customLogLevel extends Level {
	public static final Level REQUEST = new customLogLevel("REQUEST", Level.SEVERE.intValue() + 1);
	public static final Level RESPONSE = new customLogLevel("RESPONSE", Level.SEVERE.intValue() + 1);

	public customLogLevel(String name, int value) {
		super(name, value);
	}
}
