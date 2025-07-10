package inTempo;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.Timer;
import javazoom.jl.player.Player;
import netscape.javascript.JSObject;

import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.text.SimpleDateFormat;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import javax.swing.border.AbstractBorder;


import java.net.URI;
import java.net.URL;
import inTempo.RemoteServer;

// AGGIUNTA: import per mp3agic
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
// AGGIUNTA: import per BasicPlayer
// import javazoom.jlgui.basicplayer.BasicPlayer;
// import javazoom.jlgui.basicplayer.BasicPlayerException;
import java.util.*;
import java.util.List;
// AGGIUNTA: import per Jsoup
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class TimerApp {

    // Da aggiungere tra i campi della classe
private String testoPersonalizzatoClock = null;

// Metodo per mostrare testo personalizzato
public void mostraTestoSuClockLabel(String testo) {
    testoPersonalizzatoClock = testo;
    String htmlText = "<html><div style='text-align:center;word-break:break-word;'>" + escapeHtml(testo) + "</div></html>";
    // Mostra su clockLabel (preview)
    clockLabel.setText(htmlText);
    clockLabel.setForeground(Color.WHITE);
    adaptFontToLabel(clockLabel);
    // Mostra su timerLabel (timer attivo)
    timerLabel.setText(htmlText);
    timerLabel.setForeground(Color.WHITE);
    adaptFontToLabel(timerLabel);
    // Mostra anche sulla label esterna
    if (externalClockLabel != null) {
        externalClockLabel.setText(htmlText);
        externalClockLabel.setForeground(Color.WHITE);
        updateExternalClockLabelFont();
    }
    if (clockTimer != null) clockTimer.stop();
}

// Utility per escape HTML
private String escapeHtml(String s) {
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
}

// Metodo per ripristinare l'orologio
public void ripristinaClockLabel() {
    testoPersonalizzatoClock = null;
    if (clockTimer != null) clockTimer.start();
    updateClockLabel();
    // Se il timer era attivo, riavvia il countdown
    if (timerLabel.isVisible() && swingTimer != null && !swingTimer.isRunning()) {
        swingTimer.start();
    }
    // Aggiorna anche la timerLabel
    updateTimerLabel();
}

    private String getLocalIpAddress() throws Exception {
        for (java.net.NetworkInterface ni : java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
            for (java.net.InetAddress addr : java.util.Collections.list(ni.getInetAddresses())) {
                if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                    String ip = addr.getHostAddress();
                    if (!ip.startsWith("169.254.")) { // escludi APIPA
                        return ip;
                    }
                }
            }
        }
        return java.net.InetAddress.getLocalHost().getHostAddress(); // Fallback
    }

private List<MeetingSchedule> meetingSchedules = new ArrayList<>();
private static final String MEETINGS_CONFIG = "meetings.txt";
    
private JFrame frame;
private DefaultListModel<Parte> listModel;
private JList<Parte> listaParti;
private JLabel timerLabel;
private Timer swingTimer;
private int timeLeft;
private int currentParteIndex = 0;
private boolean isPaused = false;
private boolean inizioAutomatico = true;
private LocalTime orarioInizioAdunanza = LocalTime.of(19, 0);
private AnalogClockPanel analogClockPanel;
private JLabel statusLabel;
private JPanel centerPanel;
private CardLayout cardLayout;
private JLabel clockLabel;
private Timer clockTimer;
// Player MP3
private JPanel musicPanel;
private JButton playButton, stopButton, nextSongButton, prevSongButton;
private JLabel songLabel;
private File[] mp3Files;
private File currentFolder;
private Player mp3Player;
private Thread playerThread;
private int currentSongIndex = 0;
private volatile boolean stopRequested = false;
// Pannello superiore per pulsanti timer e timer/orologio
private JPanel topPanel;
private static final String MUSIC_FOLDER_CONFIG = "music_folder.txt";
private JLabel topClockLabel;
private JLabel topDateLabel;
private Timer topClockTimer;
// --- AGGIUNTA: pannello freccia e scritta ---
private JPanel arrowPanel;
private JPanel comboAndArrowPanel;
private JPanel customMsgPanel; // Box messaggi oratore
private JButton addParteButton;
private JButton startButton;
private JButton pauseButton;
private JButton prevButton;
private JButton nextButton;
private Font bigButtonFont;
private JScrollPane listaScroll;
private JFrame externalClockFrame; // Finestra su secondo monitor
private JLabel externalClockLabel; // label del secondo monitor
private boolean externalIsTimer = false; // true se la label esterna mostra il timer
private Timer externalClockSwingTimer;
private Timer blinkTimer;
private boolean blinkState = false;
// All'inizio della classe TimerApp
private Font customTitleFont;
private static final String SETTINGS_CONFIG = "settings.txt";
private boolean proiezioneAbilitata = true;
private int monitorSelezionato = 1;
private boolean fuoriTempoLampeggiante = true;
private boolean disabilitaMessaggiOratore = false;
// Aggiungi campo per tracciare il programma selezionato
private int currentProgramIndex = 0;
private boolean ignoreProgramComboEvent = false;
private boolean mostraOrariParti = true;
private boolean isMuted = false;
private boolean isPlaying = false;
private Timer playerBlinkTimer;
private boolean playerBlinkState = false;
private Float previousVolume = null;
// Durata fissa adunanza in secondi (1 ora e 46 minuti = 6360 secondi)
// private static final int DURATA_FISSA_ADUNANZA_SEC = 106 * 60;
// Orario di fine programmato fisso
private LocalTime orarioFineProgrammatoFisso = null;
private long ritardoUltimoCambioParte = 0; // in secondi
private JLabel fineOrariLabel = null;
private boolean assorbiRitardoAutomatico = false;
// Label per mostrare il tempo effettivo EFFICACI NEL MINISTERO
private JLabel efficaciTimeLabel;
private long tempoEffettivoUltimaEfficaci = -1;
private long inizioEfficaciMillis = -1;
private boolean mostraTempoEfficaciMinistero = false;
private JLabel smallTimerLabel; // Timer più piccolo sulla sinistra
private long inizioParteMillis = -1; // Tempo di inizio della parte corrente
private long tempoEffettivoUltimaParte = -1; // Tempo effettivo dell'ultima parte completata
private JPanel timerWithEffPanel;
private boolean programmaImportato = false;
// --- AGGIUNTA: variabile per animazione parte attiva ---
private int parteAnimata = -1;
private Timer animTimer = null;
private int animStep = 0;
// --- AGGIUNTA: timer per repaint lista in tempo reale ---
private Timer repaintListaTimer = null;
// --- AGGIUNTA: flag per evitare doppia apertura impostazioni ---
private boolean impostazioniAperte = false;
// --- AGGIUNTA: controllo aggiornamenti automatico all'avvio ---
private static final String VERSION = "1.2.1"; // aggiorna qui la versione corrente
private static final String GITHUB_API = "https://api.github.com/repos/diegoisme-dev/KH-Timer/releases/latest";
private JLabel checkLabel;
// Variabile per la visibilità del tasto Importa WOL
private boolean mostraImportaWOL = true;
private JPanel importaWOLPanel; // Pannello per il pulsante Importa WOL
private int animFrame = 0;
private int animTotalFrames = 20; // Più alto = animazione più lenta e fluida
private int parteAnimataDa = -1; // Indice di partenza animazione
private int parteAnimataA = -1;  // Indice di arrivo animazione
private Timer animFluidaTimer = null;
private JButton cambiaGiornoButton; // Pulsante per cambiare giorno congresso
//private String AppID = "72309BDF-9F0E-45AF-8DF9-0A7E1AE8FD06";

// --- VARIABILI PER COLLEGAMENTO REMOTO ---
private RemoteServer remoteServer = null;
private boolean remoteActive = false;
private String remoteLastIp = null;
private ImageIcon remoteQrIcon = null;
private String remotePassword = null; // Password per accesso remoto
private String remotePasswordInput = null; // Password inserita dall'utente remoto

// --- AGGIUNTA: pulsante timer manuale ---
private JButton timerManualeButton;

// --- METODI PER IL SERVER REMOTO ---
public String getTimerLabelText() {
    if (timerLabel == null) return "--:--";
    String raw = timerLabel.getText();
    // Rimuovi eventuali tag HTML
    return raw.replaceAll("<[^>]*>", "");
}

// --- MeetingSchedule class ---
private static class MeetingSchedule {
    private DayOfWeek dayOfWeek;
    private LocalTime time;

    public MeetingSchedule(DayOfWeek dayOfWeek, LocalTime time) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getTime() {
        return time;
    }

    public LocalDateTime getNextOccurrence() {
        LocalDate now = LocalDate.now();
        int todayValue = now.getDayOfWeek().getValue();
        int targetValue = dayOfWeek.getValue();
        int daysUntil = (targetValue - todayValue + 7) % 7;
        if (daysUntil == 0 && LocalTime.now().isAfter(time)) {
            daysUntil = 7;
        }
        return now.plusDays(daysUntil).atTime(time);
    }

    @Override
    public String toString() {
        String[] giorni = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        String giorno = giorni[dayOfWeek.getValue() - 1];
        return giorno + " - " + time.toString();
    }
}

private Set<String> warningHistory = new HashSet<>();
private String lastWarningShown = "";

private void checkForWarning() {
    String url = "https://raw.githubusercontent.com/diegoisme-dev/KH-Timer/refs/heads/main/WARNING.txt";
    new Thread(() -> {
        try {
            java.net.URL u = java.net.URI.create(url).toURL();
            java.util.Scanner s = new java.util.Scanner(u.openStream(), "UTF-8").useDelimiter("\\A");
            String msg = s.hasNext() ? s.next().trim() : "";
            s.close();
            if (!msg.isEmpty() && !msg.equals(lastWarningShown)) {
                lastWarningShown = msg; // aggiorna solo se diverso
                // Mostra il dialog solo se NON è mai stato mostrato in questa sessione
                if (!warningHistory.contains(msg)) {
                    warningHistory.add(msg);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, lastWarningShown, "⚠️ Avviso Importante", JOptionPane.WARNING_MESSAGE);
                    });
                }
            }
        } catch (Exception e) {
            // Silenzia errori di rete
        }
    }).start();
}


// --- Save meeting schedules ---
private void saveMeetingSchedules() {
    try (PrintWriter pw = new PrintWriter(MEETINGS_CONFIG)) {
        for (MeetingSchedule ms : meetingSchedules) {
            pw.println(ms.getDayOfWeek().getValue() + ";" +
                      ms.getTime().getHour() + ";" +
                      ms.getTime().getMinute());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// --- Load meeting schedules ---
private void loadMeetingSchedules() {
    meetingSchedules.clear();
    File file = new File(MEETINGS_CONFIG);
    if (!file.exists()) return;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length == 3) {
                DayOfWeek day = DayOfWeek.of(Integer.parseInt(parts[0]));
                LocalTime time = LocalTime.of(
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
                );
                meetingSchedules.add(new MeetingSchedule(day, time));
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public void remotePrev() { cambiaParte(-1); }
public void remoteNext() { cambiaParte(1); }
public void remotePause() { togglePause(); }
public void remoteStart() { startTimer(); }

// AGGIUNTA VARIABILE PER MEMORIZZARE IL TEMPO ALLA PAUSA
private int timeLeftWhenPaused = -1;

public TimerApp() {
  FlatDarkLaf.setup();
  // Carica il font custom SUBITO
  try {
      InputStream fontStream = getClass().getResourceAsStream("/inTempo/res/fonts/RobotoCondensed-Medium.ttf");
      if (fontStream != null) {
          customTitleFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, 28f);
          fontStream.close();
      } else {
          System.err.println("Font non trovato!");
          customTitleFont = new Font("SansSerif", Font.BOLD, 28);
      }
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(customTitleFont);
      System.out.println("Font custom caricato correttamente: " + customTitleFont.getFontName());
  } catch (Exception e) {
      System.err.println("Errore caricamento font custom: " + e.getMessage());
      customTitleFont = new Font("SansSerif", Font.BOLD, 28); // fallback
  }
  UIManager.put("Component.arc", 10);
  Font bigButtonFont = new Font("SansSerif", Font.BOLD, 22);
  addParteButton = new JButton("Aggiungi parte");
  addParteButton.setFont(bigButtonFont);
  addParteButton.setFocusPainted(false);
  addParteButton.setBackground(new Color(30, 60, 30));
  addParteButton.setForeground(Color.WHITE);
  addParteButton.setVisible(false);
  addParteButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
          addParteButton.setBackground(new Color(0, 180, 180));
          addParteButton.setForeground(Color.BLACK);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
          addParteButton.setBackground(new Color(30, 60, 30));
          addParteButton.setForeground(Color.WHITE);
      }
  });



   frame = new JFrame("KH Timer - Kingdom Hall Timer");
   checkForWarning();
   new javax.swing.Timer(15 * 60 * 1000, e -> checkForWarning()).start();
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frame.setSize(1000, 600);
  frame.setLayout(new BorderLayout());
  frame.getContentPane().setBackground(Color.BLACK);


  
  // Inizializzo listModel e listaParti PRIMA di ogni utilizzo
  listModel = new DefaultListModel<>();
  listaParti = new JList<>(listModel);
  // Miglioramenti UI dark e responsive
  UIManager.put("Button.background", new Color(30, 30, 30));
  UIManager.put("Button.foreground", Color.WHITE);
  UIManager.put("Button.select", new Color(0, 180, 180));
  UIManager.put("Button.focus", new Color(0, 180, 180));
  UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true));
  UIManager.put("Panel.background", new Color(18, 18, 18));
  UIManager.put("List.background", new Color(18, 18, 18));
  UIManager.put("List.foreground", Color.WHITE);
  UIManager.put("List.selectionBackground", new Color(0, 180, 180));
  UIManager.put("List.selectionForeground", Color.BLACK);
  UIManager.put("ScrollBar.thumb", new Color(40, 40, 40));
  UIManager.put("ScrollBar.track", new Color(18, 18, 18));
  UIManager.put("ComboBox.background", new Color(30, 30, 30));
  UIManager.put("ComboBox.foreground", Color.WHITE);
  UIManager.put("ComboBox.selectionBackground", new Color(0, 180, 180));
  UIManager.put("ComboBox.selectionForeground", Color.BLACK);
  UIManager.put("TextField.background", new Color(30, 30, 30));
  UIManager.put("TextField.foreground", Color.WHITE);
  UIManager.put("TextField.caretForeground", Color.CYAN);
  UIManager.put("TextField.border", BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true));
  UIManager.put("Label.foreground", Color.WHITE);
  UIManager.put("OptionPane.background", new Color(30, 30, 30));
  UIManager.put("Panel.background", new Color(18, 18, 18));
  UIManager.put("OptionPane.messageForeground", Color.WHITE);
  UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.BOLD, 16));
  UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 16));
  // Family font globale
  UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 16));
  UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 18));
  UIManager.put("ComboBox.font", new Font("SansSerif", Font.BOLD, 18));
  UIManager.put("List.font", new Font("SansSerif", Font.PLAIN, 16));
  // Responsive font e padding
  frame.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
          int w = frame.getWidth();
          int h = frame.getHeight();
          int base = Math.min(w, h);
          int fontBig = Math.max(18, base / 32);
          int fontMed = Math.max(14, base / 48);
          int fontSmall = Math.max(12, base / 64);
          timerLabel.setFont(new Font("SansSerif", Font.BOLD, fontBig * 6));
          clockLabel.setFont(new Font("SansSerif", Font.BOLD, fontBig * 6));
          topClockLabel.setFont(new Font("SansSerif", Font.BOLD, fontBig));
          topDateLabel.setFont(new Font("SansSerif", Font.PLAIN, fontMed));
          statusLabel.setFont(new Font("SansSerif", Font.BOLD, fontMed));
          listaParti.setFont(new Font("SansSerif", Font.PLAIN, fontMed));
            
          addParteButton.setFont(new Font("SansSerif", Font.BOLD, fontBig));
      }
  });
  // Effetto hover sui pulsanti
  addParteButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
          addParteButton.setBackground(new Color(0, 180, 180));
          addParteButton.setForeground(Color.BLACK);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
          addParteButton.setBackground(new Color(30, 60, 30));
          addParteButton.setForeground(Color.WHITE);
      }
  });
  startButton = new JButton("▶");
  pauseButton = new JButton("||");
  prevButton = new JButton("⏪");
  nextButton = new JButton("⏩");
  JButton[] timerButtons = {startButton, pauseButton, prevButton, nextButton};
  for (JButton btn : timerButtons) {
      btn.setFont(new Font("SansSerif", Font.BOLD, 32));
      btn.setFocusPainted(false);
      btn.setBackground(new Color(30, 30, 30));
      btn.setForeground(Color.WHITE);
      btn.setPreferredSize(new Dimension(64, 64));
      btn.setMinimumSize(new Dimension(64, 64));
      btn.setMaximumSize(new Dimension(64, 64));
      btn.setContentAreaFilled(false);
      btn.setOpaque(false);
      btn.setBorder(BorderFactory.createEmptyBorder());
      btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      btn.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent evt) {
              btn.setBackground(new Color(0, 180, 180));
              btn.setForeground(Color.BLACK);
              btn.setOpaque(true);
          }
          public void mouseExited(java.awt.event.MouseEvent evt) {
              btn.setBackground(new Color(30, 30, 30));
              btn.setForeground(Color.WHITE);
              btn.setOpaque(false);
          }
      });
      btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
          @Override
          public void paint(Graphics g, JComponent c) {
              Graphics2D g2 = (Graphics2D) g.create();
              g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
              int w = c.getWidth();
              int h = c.getHeight();
              int d = Math.min(w, h);
              int x = (w - d) / 2;
              int y = (h - d) / 2;
              if (btn.getModel().isRollover()) {
                  g2.setColor(new Color(0, 180, 180));
              } else {
                  g2.setColor(btn.getBackground());
              }
              g2.fillOval(x, y, d, d);
              g2.setColor(new Color(0, 180, 180));
              g2.setStroke(new BasicStroke(3f));
              g2.drawOval(x + 1, y + 1, d - 3, d - 3);
              g2.dispose();
              super.paint(g, c);
          }
      });
      // Hit area circolare
      btn.setModel(new javax.swing.DefaultButtonModel() {
          @Override
          public boolean isPressed() {
              return super.isPressed();
          }
      });
      btn.setFocusable(false);
      btn.setMargin(new Insets(0,0,0,0));
      btn.setAlignmentX(Component.CENTER_ALIGNMENT);
      btn.setAlignmentY(Component.CENTER_ALIGNMENT);
      btn.setSize(new Dimension(64, 64));
      btn.setMinimumSize(new Dimension(64, 64));
      btn.setMaximumSize(new Dimension(64, 64));
      btn.setPreferredSize(new Dimension(64, 64));
      btn.setBounds(0, 0, 64, 64);
      btn.setFocusPainted(false);
      btn.setRolloverEnabled(true);
      btn.setContentAreaFilled(false);
      btn.setOpaque(false);
      btn.setBorderPainted(false);
      btn.setBorder(BorderFactory.createEmptyBorder());
      btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
          @Override
          public void paint(Graphics g, JComponent c) {
              Graphics2D g2 = (Graphics2D) g.create();
              g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
              int w = c.getWidth();
              int h = c.getHeight();
              int d = Math.min(w, h);
              int x = (w - d) / 2;
              int y = (h - d) / 2;
              if (btn.getModel().isRollover()) {
                  g2.setColor(new Color(0, 180, 180));
              } else {
                  g2.setColor(btn.getBackground());
              }
              g2.fillOval(x, y, d, d);
              g2.setColor(new Color(0, 180, 180));
              g2.setStroke(new BasicStroke(3f));
              g2.drawOval(x + 1, y + 1, d - 3, d - 3);
              g2.dispose();
              super.paint(g, c);
          }
      });
      btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
          @Override
          public boolean contains(JComponent c, int x, int y) {
              int w = c.getWidth();
              int h = c.getHeight();
              int d = Math.min(w, h);
              int cx = w / 2;
              int cy = h / 2;
              int r = d / 2;
              int dx = x - cx;
              int dy = y - cy;
              return dx * dx + dy * dy <= r * r;
          }
      });
  }
  // Miglioro la lista delle parti: effetto card e linee più visibili
  listaParti.setCellRenderer(null);
  listaParti.setCellRenderer(new ListCellRenderer<Parte>() {
      private final Color verdeCantico = new Color(0, 220, 0);
      private final Color selezione = Color.DARK_GRAY;
      private final Color testoSelezione = Color.CYAN;
      private final Color introColor = Color.ORANGE;
      private final Color bgNormale = Color.BLACK;
      @Override
      public Component getListCellRendererComponent(JList<? extends Parte> list, Parte value, int index, boolean isSelected, boolean cellHasFocus) {
          JPanel panel = new JPanel(new BorderLayout()) {
              @Override
              protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  if (index < list.getModel().getSize() - 1) {
                      g.setColor(new Color(60, 60, 60));
                      g.fillRect(0, getHeight() - 2, getWidth(), 2);
                  }
              }
          };
          // Imposta l'altezza della pillola/cella
          panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 36));
          panel.setOpaque(true);
          JLabel label = new JLabel(value.toString(), SwingConstants.LEFT);
          if (value.nome.equalsIgnoreCase("INIZIO")) {
              label.setText("INIZIO");
              label.setFont(list.getFont().deriveFont(Font.BOLD));
              label.setForeground(new Color(0, 255, 255)); // ciano
              panel.setBackground(bgNormale);
              if (isSelected) {
                  panel.setBackground(selezione);
                  label.setForeground(testoSelezione);
              }
              // Orario solo a destra
              String orarioInizioStr = orarioInizioAdunanza.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              JLabel orariLabel = new JLabel(orarioInizioStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
              orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
              orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
              panel.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(18, 56, 18, 32),
                  panel.getBorder()
              ));
              panel.add(label, BorderLayout.CENTER);
              panel.add(orariLabel, BorderLayout.EAST);
              return panel;
          }
          if (value.nome.equalsIgnoreCase("FINE") && orarioFineProgrammatoFisso != null) {
              label.setText("FINE");
              label.setFont(list.getFont().deriveFont(Font.BOLD));
              label.setForeground(new Color(0, 255, 255)); // ciano
              panel.setBackground(bgNormale);
              if (isSelected) {
                  panel.setBackground(selezione);
                  label.setForeground(testoSelezione);
              }
              // Orario solo a destra (mostra orario previsto, non reale, nella lista)
              String orarioFineStr = orarioFineProgrammatoFisso.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              JLabel orariLabel = new JLabel(orarioFineStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
              orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
              orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
              panel.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(18, 56, 18, 32),
                  panel.getBorder()
              ));
              panel.add(label, BorderLayout.CENTER);
              panel.add(orariLabel, BorderLayout.EAST);
              return panel;
          }
          // --- Stile normale per tutte le altre parti ---
          label.setFont(list.getFont().deriveFont(Font.BOLD)); // grassetto
          label.setOpaque(false);
          label.setForeground(Color.WHITE);
          panel.setBackground(bgNormale);
          if (value.nome.toUpperCase().contains("CANTICO")) {
              label.setForeground(verdeCantico);
          }
          if (value.isIntro) {
              label.setForeground(introColor);
          }
          if (index == currentParteIndex) {
              panel.setBackground(selezione);
              label.setForeground(testoSelezione);
          }
          // --- ORARI O TEMPO EFFETTIVO ---
          JLabel orariLabel;
          String nomeUpper = value.nome.toUpperCase();
          boolean inEfficaci = false;
          for (int i = 0; i <= index; i++) {
                Parte p = listModel.get(i);
                if (p.nome.toUpperCase().contains("EFFICACI NEL MINISTERO")) inEfficaci = true;
                if (p.nome.toUpperCase().contains("VITA CRISTIANA")) inEfficaci = false;
          }
          boolean mostraEffettivo = (
                value.tempoEffettivo != null && (
                    nomeUpper.contains("LETTURA BIBLICA") ||
                    nomeUpper.contains("EFFICACI NEL MINISTERO") ||
                    nomeUpper.contains("DISCORSO PUBBLICO")
                )
            ) || (
                inEfficaci && !value.isIntro && value.durataSecondi > 0 && !nomeUpper.contains("CONSIGLI")
          );
          if (mostraEffettivo) {
            String testoPillola;
            if (value.tempoEffettivo != null) {
                testoPillola = Parte.formatTime(value.tempoEffettivo.intValue());
            } else {
                testoPillola = "--:--";
            }
            orariLabel = new JLabel(testoPillola + "\u00A0\u00A0\u00A0");
            orariLabel.setFont(list.getFont().deriveFont(Font.BOLD, 16f));
            orariLabel.setForeground(new Color(0, 180, 220));
            orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
            panel.add(label, BorderLayout.CENTER);
            panel.add(orariLabel, BorderLayout.EAST);
                return panel; // <-- AGGIUNGI QUESTO RETURN!
        } else if (value.nome.equalsIgnoreCase("INIZIO")) {
              String orarioInizioStr = orarioInizioAdunanza.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioInizioStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          } else if (value.nome.equalsIgnoreCase("FINE") && orarioFineProgrammatoFisso != null) {
              String orarioFineStr = orarioFineProgrammatoFisso.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioFineStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          } else {
              // Calcola orario inizio e fine
              LocalTime inizio = orarioInizioAdunanza;
              int secondiTrascorsi = 0;
              for (int i = 0; i < index; i++) {
                  secondiTrascorsi += listModel.get(i).durataSecondi;
              }
              inizio = inizio.plusSeconds(secondiTrascorsi);
              LocalTime fine = inizio.plusSeconds(value.durataSecondi);
              String orarioInizio = inizio.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              String orarioFine = fine.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioInizio + " - " + orarioFine);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          }
          orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
          orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
          panel.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createEmptyBorder(18, 56, 18, 32),
              panel.getBorder()
          ));
          panel.add(label, BorderLayout.CENTER);
          panel.add(orariLabel, BorderLayout.EAST);
          return panel;
      }
  });
  // 1. Bottoni
  // 2. Label timer e orologio
  timerLabel = new JLabel("00:00", SwingConstants.CENTER) {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(426, 240);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(426, 240);
    }
  };
  timerLabel.setFont(new Font("SansSerif", Font.BOLD, 180));
  timerLabel.setForeground(Color.GREEN);
  timerLabel.setOpaque(true);
  timerLabel.setBackground(Color.BLACK);
  timerLabel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(0, 0, 0, 0),
      BorderFactory.createLineBorder(Color.DARK_GRAY, 2)));
  clockLabel = new JLabel("00:00:00", SwingConstants.CENTER);
  clockLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
  clockLabel.setForeground(Color.WHITE);
  clockLabel.setOpaque(true);
  clockLabel.setBackground(Color.BLACK);
  clockLabel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(0, 0, 0, 0),
      BorderFactory.createLineBorder(Color.DARK_GRAY, 2)));
  // Timer per aggiornare l'orologio digitale
  clockTimer = new Timer(1000, e -> updateClockLabel());
  clockTimer.start();
  JPanel leftPanel = new JPanel(new BorderLayout());
  leftPanel.setBackground(Color.BLACK);
  leftPanel.setPreferredSize(new Dimension(500, 600));
  JComboBox<String> programmaCombo = new JComboBox<>(new String[] {
          "Vita Cristiana e Ministero",
          "VISITA SORVEGLIANTE - Vita Cristiana e Ministero",
          "Adunanza Pubblica",
          "VISITA SORVEGLIANTE - Adunanza Pubblica",
          //"Adunanza PROVA",
          "CONGRESSO DI ZONA 2025 \"Pura Adorazione\""
      });
  // Rendo il menu a tendina più basso quando è chiuso
  programmaCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
  programmaCombo.setPreferredSize(new Dimension(10, 28)); // altezza più bassa
  ((JLabel)programmaCombo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
  // --- Pannello freccia e scritta ---
  arrowPanel = new JPanel();
  arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.Y_AXIS));
  arrowPanel.setBackground(Color.BLACK);
  JLabel arrowLabel = new JLabel("\u2191", SwingConstants.CENTER); // Freccia verso l'alto
  arrowLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
  arrowLabel.setForeground(Color.ORANGE);
  arrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
  JLabel textLabel = new JLabel("Scegli il programma", SwingConstants.CENTER);
  textLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
  textLabel.setForeground(Color.ORANGE);
  textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
  arrowPanel.add(Box.createVerticalStrut(4));
  arrowPanel.add(arrowLabel);
  arrowPanel.add(textLabel);
  arrowPanel.add(Box.createVerticalStrut(8));
  programmaCombo.addActionListener(e -> {
       if (ignoreProgramComboEvent) return;
       boolean timerAttivo = swingTimer != null && swingTimer.isRunning() && !isPaused;
       if (timerAttivo) {
           int res = JOptionPane.showConfirmDialog(frame, "Se cambi programma il timer si fermerà di funzionare. Vuoi continuare?", "Attenzione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
           if (res != JOptionPane.YES_OPTION) {
               // Ripristina la selezione precedente senza triggerare l'evento
               ignoreProgramComboEvent = true;
               programmaCombo.setSelectedIndex(currentProgramIndex);
               ignoreProgramComboEvent = false;
               return;
           }
           // Ferma il timer e torna all'orologio
           if (swingTimer != null) swingTimer.stop();
           isPaused = false;
           showClock();
       }
       if (arrowPanel.isVisible()) {
           arrowPanel.setVisible(false);
           comboAndArrowPanel.revalidate();
           comboAndArrowPanel.repaint();
       }
       // Mostra il tasto aggiungi parte solo dopo la scelta
       addParteButton.setVisible(true);
       currentProgramIndex = programmaCombo.getSelectedIndex();
       // Reset timer e indice parti
       currentParteIndex = 0;
       timeLeft = 0;
       if (!meetingSchedules.isEmpty()) {
           MeetingSchedule nextMeeting = meetingSchedules.stream()
               .min(Comparator.comparing(MeetingSchedule::getNextOccurrence))
               .get();
           orarioInizioAdunanza = nextMeeting.getTime();
           } else {
               orarioInizioAdunanza = LocalTime.of(19, 0);
      orarioInizioAdunanza = LocalTime.of(19, 0);
           }
       switch (programmaCombo.getSelectedIndex()) {
           case 0: caricaAdunanzaVitaCristianaEMinistero();  chiediNumeriCantici(); break;
           case 1: caricaAdunanzaVisitaSorvegliante(); chiediNumeriCantici(); break;
           case 2: caricaAdunanzaPubblica(); chiediNumeriCanticiPubblica(); break;
           case 3: caricaAdunanzaPubblicaSorvegliante(); chiediNumeriCanticiPubblicaSorvegliante(); break;
           //case 4: caricaAdunanzaStrana(); break;
           case 4: caricaAdunanzaCongressoZona2025(); break;
           //case 5: caricaTempoManuale(); break;
       }
       currentParteIndex = 0;
       timeLeft = 0;
       showClock();
       cambiaGiornoButton.setVisible(programmaCombo.getSelectedIndex() == 5);
   });
  listaParti = new JList<>(listModel);
  listaParti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  listaParti.setFont(new Font("SansSerif", Font.PLAIN, 14));
  listaParti.setBackground(Color.BLACK);
  listaParti.setForeground(Color.WHITE);
  listaParti.setCellRenderer(new ListCellRenderer<Parte>() {
      private final Color verdeCantico = new Color(0, 220, 0);
      private final Color selezione = Color.DARK_GRAY;
      private final Color testoSelezione = Color.CYAN;
      private final Color introColor = Color.ORANGE;
      private final Color bgNormale = Color.BLACK;
      @Override
      public Component getListCellRendererComponent(JList<? extends Parte> list, Parte value, int index, boolean isSelected, boolean cellHasFocus) {
          JPanel panel = new JPanel(new BorderLayout()) {
              @Override
              protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  // Effetto ombra sotto la pillola
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  int arc = 40;
                  int margin = 8;
                  int w = getWidth() - 2 * margin;
                  int h = getHeight() - 8;
                  int x = margin;
                  int y = 4;
                  // Ombra
                  g2.setColor(new Color(0,0,0,40));
                  g2.fillRoundRect(x+2, y+4, w, h, arc, arc);
                  // Sfondo pillola
                  Color pillColor;
                  if (index == parteAnimataA && animFrame > 0 && parteAnimataDa != -1) {
                      // Interpolazione colore tra selezione e normale
                      float progress = animFrame / (float) animTotalFrames;
                      pillColor = blendColor(new Color(35, 35, 35), selezione, progress);
                  } else if (index == parteAnimataDa && animFrame > 0 && parteAnimataA != -1) {
                      float progress = 1f - (animFrame / (float) animTotalFrames);
                      pillColor = blendColor(new Color(35, 35, 35), selezione, progress);
                  } else if (index == currentParteIndex) {
                      pillColor = selezione;
                  } else {
                      pillColor = new Color(35, 35, 35);
                  }
                  if (index == parteAnimata && animStep > 0) {
                      // Animazione: illumina la pillola
                      int alpha = Math.min(180, 30 * animStep);
                      pillColor = new Color(255, 255, 0, alpha).brighter();
                  }
                  g2.setColor(pillColor);
                  g2.fillRoundRect(x, y, w, h, arc, arc);
                  g2.dispose();
              }
          };
          panel.setOpaque(false);
          panel.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
          JLabel label = new JLabel(value.toString(), SwingConstants.LEFT);
          label.setFont(list.getFont().deriveFont(Font.BOLD));
          label.setOpaque(false);
          label.setForeground(Color.WHITE);
          if (value.nome.toUpperCase().contains("CANTICO")) label.setForeground(verdeCantico);
          if (value.isIntro) label.setForeground(introColor);
          if (index == currentParteIndex) label.setForeground(testoSelezione);
          // --- ORARI O TEMPO EFFETTIVO ---
          JLabel orariLabel;
          String nomeUpper = value.nome.toUpperCase();// Patch: mostra la pillola del tempo effettivo per tutte le parti tra EFFICACI NEL MINISTERO e VITA CRISTIANA (esclusi i titoli e le intro)
          boolean inEfficaci = false;
          boolean mostraEffettivo = value.tempoEffettivo != null && (
              nomeUpper.contains("LETTURA BIBLICA") ||
              (inEfficaci && !value.isIntro && value.durataSecondi > 0) ||
              nomeUpper.contains("DISCORSO PUBBLICO")
          );
          if (mostraEffettivo) {
              orariLabel = new JLabel(Parte.formatTime(value.tempoEffettivo.intValue()) + "\u00A0\u00A0\u00A0");
              orariLabel.setFont(list.getFont().deriveFont(Font.BOLD, 16f));
              orariLabel.setForeground(new Color(0, 180, 220));
          } else if (value.nome.equalsIgnoreCase("INIZIO")) {
              String orarioInizioStr = orarioInizioAdunanza.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioInizioStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          } else if (value.nome.equalsIgnoreCase("FINE") && orarioFineProgrammatoFisso != null) {
              String orarioFineStr = orarioFineProgrammatoFisso.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioFineStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          } else {
              LocalTime inizio = orarioInizioAdunanza;
              int secondiTrascorsi = 0;
              for (int i = 0; i < index; i++) {
                  secondiTrascorsi += listModel.get(i).durataSecondi;
              }
              inizio = inizio.plusSeconds(secondiTrascorsi);
              LocalTime fine = inizio.plusSeconds(value.durataSecondi);
              String orarioInizio = inizio.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              String orarioFine = fine.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioInizio + " - " + orarioFine);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          }
          orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
          orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
          panel.add(label, BorderLayout.CENTER);
          panel.add(orariLabel, BorderLayout.EAST);
          return panel;
      }
  });
  // Ripristino doppio click per modifica parti/cantici/INIZIO
  listaParti.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
              int index = listaParti.locationToIndex(e.getPoint());
              if (index >= 0) {
                  Parte parte = listModel.getElementAt(index);
                  if (parte.nome.equalsIgnoreCase("INIZIO")) {
                      chiediOrarioInizio();
                  } else if (parte.nome.equalsIgnoreCase("FINE")) {
                      // Mostra finestra info fine
                      LocalTime inizio = orarioInizioAdunanza;
                      int secondiTrascorsi = 0;
                      for (int i = 0; i < index; i++) {
                          secondiTrascorsi += listModel.get(i).durataSecondi;
                      }
                      LocalTime orarioPrevistoFine = inizio.plusSeconds(secondiTrascorsi);
                      LocalTime oraAttuale = LocalTime.now();
                      Duration diff = Duration.between(oraAttuale, orarioPrevistoFine);
                      long secRimasti = diff.getSeconds();
                      String orarioPrevisto = orarioPrevistoFine.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                      String tempoRimasto;
                      if (secRimasti >= 0) {
                          tempoRimasto = String.format("%d:%02d", secRimasti / 60, secRimasti % 60);
                      } else {
                          tempoRimasto = "Già terminato";
                      }
                      JOptionPane.showMessageDialog(frame,
                          "Orario previsto per la fine: " + orarioPrevisto + "\n" +
                          "Tempo rimasto: " + tempoRimasto,
                          "Info Fine Programma",
                          JOptionPane.INFORMATION_MESSAGE);
                  } else if (parte.nome.toUpperCase().contains("CANTICO")) {
                      modificaSoloNumeroCantico(parte, index);
                  } else {
                      mostraFinestraModifica(parte, index);
                  }
              }
          }
      }
  });
  JPanel topLeftPanel = new JPanel(new BorderLayout());
  topLeftPanel.setBackground(Color.BLACK);
  // Inserisco un pannello per menu + freccia/scritta
  comboAndArrowPanel = new JPanel();
  comboAndArrowPanel.setLayout(new BoxLayout(comboAndArrowPanel, BoxLayout.Y_AXIS));
  comboAndArrowPanel.setBackground(Color.BLACK);
  comboAndArrowPanel.add(Box.createVerticalStrut(8));
  comboAndArrowPanel.add(programmaCombo);
  comboAndArrowPanel.add(arrowPanel);
  // --- PULSANTE AGGIUNGI PARTE ---
  addParteButton = new JButton("Aggiungi parte") {
      @Override
      protected void paintComponent(Graphics g) {
          Graphics2D g2 = (Graphics2D) g.create();
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          int w = getWidth();
          int h = getHeight();
          // Gradiente
          Color c1 = getModel().isRollover() ? new Color(0, 220, 180) : new Color(0, 180, 140);
          Color c2 = getModel().isRollover() ? new Color(0, 180, 220) : new Color(0, 120, 180);
          GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
          g2.setPaint(gp);
          g2.fillRoundRect(0, 0, w, h, 20, 20);
          // Glow/ombra
          if (getModel().isRollover()) {
              g2.setColor(new Color(0,255,255,80));
              g2.setStroke(new BasicStroke(4f));
              g2.drawRoundRect(2, 2, w-5, h-5, 16, 16);
          }
          // Bordo
          g2.setColor(new Color(0, 80, 120));
          g2.setStroke(new BasicStroke(2f));
          g2.drawRoundRect(1, 1, w-3, h-3, 20, 20);
          g2.dispose();
          super.paintComponent(g);
      }
  };
  addParteButton.setFont(customTitleFont.deriveFont(Font.BOLD, 18f));
  addParteButton.setForeground(Color.WHITE);
  addParteButton.setBackground(new Color(0, 180, 140));
  addParteButton.setFocusPainted(false);
  addParteButton.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
  addParteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  addParteButton.setVisible(false); // Inizialmente nascosto
  // Crea l'icona "+" dinamicamente (più piccola)
  BufferedImage plusIcon = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
  Graphics2D g2 = plusIcon.createGraphics();
  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  g2.setColor(Color.WHITE);
  g2.setStroke(new BasicStroke(3f));
  g2.drawLine(9, 3, 9, 15);
  g2.drawLine(3, 9, 15, 9);
  g2.dispose();
  addParteButton.setIcon(new ImageIcon(plusIcon));
  addParteButton.setHorizontalAlignment(SwingConstants.LEFT);
  addParteButton.setIconTextGap(8);
  addParteButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
          addParteButton.setForeground(Color.BLACK);
          addParteButton.setBackground(new Color(0, 255, 220));
          addParteButton.repaint();
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
          addParteButton.setForeground(Color.WHITE);
          addParteButton.setBackground(new Color(0, 180, 140));
          addParteButton.repaint();
      }
  });
  addParteButton.addActionListener(e -> {
      JTextField nomeField = new JTextField("EFFICACI NEL MINISTERO");
      JTextField minutiField = new JTextField("04");
      JTextField secondiField = new JTextField("00");
      JCheckBox introCheck = new JCheckBox("Parte di introduzione (colore arancione)");
      JCheckBox consigliCheck = new JCheckBox("Aggiungi CONSIGLI (1MIN)");
      // Blocca Minuti/Secondi centrato
      JPanel timePanel = new JPanel();
      timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
      timePanel.setOpaque(false);
      JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
      JLabel minLabel = new JLabel("Minuti");
      JLabel secLabel = new JLabel("Secondi");
      labelRow.add(minLabel);
      labelRow.add(secLabel);
      JPanel fieldRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
      fieldRow.add(minutiField);
      fieldRow.add(secondiField);
      timePanel.add(labelRow);
      timePanel.add(fieldRow);
      JPanel panel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(6, 8, 6, 8);
      gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
      panel.add(new JLabel("Nome:"), gbc);
      gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
      panel.add(nomeField, gbc);
      gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
      panel.add(timePanel, gbc);
      panel.add(new JLabel(" "));
      panel.add(introCheck);
      panel.add(new JLabel(" "));
      panel.add(consigliCheck);
      int result = JOptionPane.showConfirmDialog(frame, panel, "Aggiungi Parte", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if (result == JOptionPane.OK_OPTION) {
          String nome = nomeField.getText().trim();
          try {
              int minuti = Integer.parseInt(minutiField.getText().trim());
              int secondi = Integer.parseInt(secondiField.getText().trim());
              boolean isIntro = introCheck.isSelected();
              boolean aggiungiConsigli = consigliCheck.isSelected();
              if (!nome.isEmpty() && secondi >= 0 && secondi < 60) {
                  listModel.addElement(new Parte(nome, minuti * 60 + secondi, isIntro));
                  if (aggiungiConsigli) {
                      listModel.addElement(new Parte("CONSIGLI", 60));
                  }
              }
          } catch (NumberFormatException ex) {
              JOptionPane.showMessageDialog(frame, "Inserisci valori validi per minuti e secondi.", "Errore", JOptionPane.ERROR_MESSAGE);
          }
      }
  });
  // Pannello lista centrale
  listaScroll = new JScrollPane(listaParti);
  comboAndArrowPanel.add(Box.createVerticalStrut(8));
  // Pannello per il tasto in basso a sinistra, dentro il riquadro nero
  JPanel panelPiu = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
  panelPiu.setBackground(Color.BLACK);
  panelPiu.add(addParteButton);
  // --- AGGIUNTA: pulsante Cambia giorno per il congresso ---
  cambiaGiornoButton = new JButton("Cambia giorno");
  cambiaGiornoButton.setFont(new Font("SansSerif", Font.BOLD, 16));
  cambiaGiornoButton.setBackground(new Color(0, 120, 220));
  cambiaGiornoButton.setForeground(Color.WHITE);
  cambiaGiornoButton.setFocusPainted(false);
  cambiaGiornoButton.setVisible(false);
  cambiaGiornoButton.addActionListener(e -> caricaAdunanzaCongressoZona2025());
  panelPiu.add(cambiaGiornoButton);

  // --- AGGIUNTA: pulsante Timer Manuale ---
  timerManualeButton = new JButton("Timer Manuale") {
    // Pittura personalizzata del pulsante per bordi arrotondati
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Colore di sfondo personalizzato (sfumatura opzionale)
        Color baseColor = getModel().isRollover() ? new Color(0, 200, 80) : new Color(0, 180, 60);
        g2.setColor(baseColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Nessun bordo visibile
    }
};

// Miglioramento stile e responsività del pulsante Timer Manuale
timerManualeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
timerManualeButton.setForeground(Color.WHITE);
timerManualeButton.setPreferredSize(new Dimension(170, 44));
timerManualeButton.setMinimumSize(new Dimension(120, 36));
timerManualeButton.setMaximumSize(new Dimension(240, 60));
timerManualeButton.setFocusPainted(false);
timerManualeButton.setContentAreaFilled(false);
timerManualeButton.setBorderPainted(false);
timerManualeButton.setOpaque(false);
timerManualeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
timerManualeButton.setMargin(new Insets(8, 24, 8, 24));
// Adatta il font quando il pulsante viene ridimensionato
addResizeFontListener(timerManualeButton);


  panelPiu.add(timerManualeButton);
  timerManualeButton.addActionListener(e -> {
      JDialog dialog = new JDialog(frame, "Timer Manuale", true);
      JPanel panel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(8, 8, 8, 8);
      gbc.gridx = 0; gbc.gridy = 0;
      panel.add(new JLabel("Minuti:"), gbc);
      gbc.gridx = 1;
      JTextField minutiField = new JTextField("0", 3);
      minutiField.setFont(new Font("SansSerif", Font.BOLD, 18));
      panel.add(minutiField, gbc);
      gbc.gridx = 0; gbc.gridy = 1;
      panel.add(new JLabel("Secondi:"), gbc);
      gbc.gridx = 1;
      JTextField secondiField = new JTextField("0", 3);
      secondiField.setFont(new Font("SansSerif", Font.BOLD, 18));
      panel.add(secondiField, gbc);
      gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
      JButton avviaButton = new JButton("Avvia Timer");
      avviaButton.setFont(new Font("SansSerif", Font.BOLD, 16));
      avviaButton.setBackground(new Color(0, 180, 60));
      avviaButton.setForeground(Color.WHITE);
      avviaButton.setFocusPainted(false);
      panel.add(avviaButton, gbc);
      avviaButton.addActionListener(ev -> {
          try {
              int min = Integer.parseInt(minutiField.getText().trim());
              int sec = Integer.parseInt(secondiField.getText().trim());
              if (min < 0) min = 0;
              if (sec < 0) sec = 0;
              int totalSec = min * 60 + sec;
              if (totalSec <= 0) {
                  JOptionPane.showMessageDialog(dialog, "Inserisci un tempo maggiore di zero.", "Errore", JOptionPane.ERROR_MESSAGE);
                  return;
              }
              // Rimuovi eventuali timer manuali precedenti
              listModel.clear();
              // Aggiungi la parte temporanea
              Parte parteManuale = new Parte("TIMER MANUALE", totalSec);
              listModel.addElement(parteManuale);
              currentParteIndex = listModel.size() - 1;
              listaParti.setSelectedIndex(currentParteIndex);
              inizioParteMillis = -1;
              startTimer();
              dialog.dispose();
          } catch (NumberFormatException ex) {
              JOptionPane.showMessageDialog(dialog, "Inserisci valori validi per minuti e secondi.", "Errore", JOptionPane.ERROR_MESSAGE);
          }
      });
      dialog.setContentPane(panel);
      dialog.pack();
      dialog.setLocationRelativeTo(frame);
      dialog.setVisible(true);
  });

  
  // Mostra/nascondi il pulsante in base al programma selezionato
  programmaCombo.addActionListener(e -> {
      // ... existing code ...
      cambiaGiornoButton.setVisible(programmaCombo.getSelectedIndex() == 5);
      // ... existing code ...
  });
  cambiaGiornoButton.setVisible(programmaCombo.getSelectedIndex() == 5);
  // Crea il timer piccolo sulla sinistra
  smallTimerLabel = new JLabel("00:00", SwingConstants.CENTER);
  smallTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
  smallTimerLabel.setForeground(Color.GREEN);
  smallTimerLabel.setBackground(Color.BLACK);
  smallTimerLabel.setOpaque(true);
  smallTimerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    // Aggiungi il timer piccolo al leftPanel
  JPanel smallTimerPanel = new JPanel(new BorderLayout());
  smallTimerPanel.setBackground(Color.BLACK);
  smallTimerPanel.add(smallTimerLabel, BorderLayout.CENTER);
  smallTimerPanel.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40), 1));
    // Layout verticale: comboAndArrowPanel in alto, timer piccolo, lista al centro, pulsante in basso
  leftPanel.setLayout(new BorderLayout());
  leftPanel.add(comboAndArrowPanel, BorderLayout.NORTH);
    // Pannello centrale che contiene smallTimerPanel e listaScroll
  JPanel centerLeftPanel = new JPanel(new BorderLayout());
  centerLeftPanel.setBackground(Color.WHITE);
  centerLeftPanel.add(listaScroll, BorderLayout.CENTER);
    leftPanel.add(centerLeftPanel, BorderLayout.CENTER);
  leftPanel.add(panelPiu, BorderLayout.SOUTH);
  // 3. CenterPanel
  cardLayout = new CardLayout();
  centerPanel = new JPanel(cardLayout);
  centerPanel.add(clockLabel, "clock");
  centerPanel.add(timerLabel, "timer");
  // 4. StatusLabel
  statusLabel = new JLabel(" ", SwingConstants.CENTER);
  statusLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
  statusLabel.setForeground(Color.WHITE);
  statusLabel.setOpaque(true);
  statusLabel.setBackground(Color.BLACK);
  // --- MUSIC PANEL ---
  musicPanel = new JPanel(new BorderLayout());
  musicPanel.setBackground(Color.BLACK);
  Dimension playerBtnSize = new Dimension(64, 38);
  playButton = new JButton("Play");
  stopButton = new JButton("Stop");
  nextSongButton = new JButton("▶");
  prevSongButton = new JButton("◀");
  // --- AGGIUNTA: campo per numero cantico ---
  JTextField numeroCanticoField = new JTextField(3);
  numeroCanticoField.setMaximumSize(new Dimension(40, 32));
  numeroCanticoField.setFont(new Font("SansSerif", Font.BOLD, 16));
  numeroCanticoField.setHorizontalAlignment(JTextField.CENTER);
  numeroCanticoField.setToolTipText("Numero cantico");
  numeroCanticoField.addActionListener(e -> {
      String num = numeroCanticoField.getText().trim();
      if (!num.matches("\\d+")) return;
      int trovato = -1;
      if (mp3Files != null) {
          for (int i = 0; i < mp3Files.length; i++) {
              String nome = mp3Files[i].getName();
              if (nome.matches(".*_0*"+num+"(\\D|$).*") || nome.matches(".*_"+num+"(\\D|$).*") || nome.contains("_"+num+"_")) {
                  trovato = i;
                  break;
              }
          }
      }
      if (trovato != -1) {
          playSong(trovato);
          isPlaying = true;
          startPlayerBlinkTimer();
      } else {
          JOptionPane.showMessageDialog(frame, "Cantico numero " + num + " non trovato.", "Errore", JOptionPane.ERROR_MESSAGE);
      }
  });
  JButton[] playerBtns = {playButton, stopButton, nextSongButton, prevSongButton};
// Definizione dei colori usati
Color defaultBg = new Color(30, 30, 30);
Color hoverBg = Color.WHITE;
Color defaultFg = Color.WHITE;
Color hoverFg = Color.BLACK;

// Applicazione dello stile a ciascun pulsante
for (JButton btn : playerBtns) {
    // Stile base
    btn.setBackground(defaultBg);
    btn.setForeground(defaultFg);
    btn.setFocusPainted(false);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(playerBtnSize);
    btn.setMinimumSize(playerBtnSize);
    btn.setMaximumSize(playerBtnSize);
    btn.setBorder(BorderFactory.createLineBorder(defaultBg, 0, true));


    // Effetto hover
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            btn.setBackground(hoverBg);
            btn.setForeground(hoverFg);
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            btn.setBackground(defaultBg);
            btn.setForeground(defaultFg);
        }
    });
}

  songLabel = new JLabel("", SwingConstants.CENTER);
  songLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
  songLabel.setForeground(new Color(0, 180, 220));
  songLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
  
  // Riquadro unico per titolo + pulsanti, compatto e in basso
 // Pannello principale del player (con sfondo nero e layout verticale)

JPanel playerBox = new JPanel();
playerBox.setLayout(new BoxLayout(playerBox, BoxLayout.Y_AXIS));
playerBox.setBackground(Color.BLACK);

// Etichetta del canto (centrata, con font leggibile)
songLabel.setHorizontalAlignment(SwingConstants.CENTER);
songLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
songLabel.setForeground(new Color(0, 180, 220));
songLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
playerBox.add(songLabel);

// Spazio verticale tra etichetta e pulsanti
playerBox.add(Box.createVerticalStrut(6));

// Pannello pulsanti musica (trasparente, layout orizzontale)
JPanel musicButtonPanel = new JPanel();
musicButtonPanel.setLayout(new BoxLayout(musicButtonPanel, BoxLayout.X_AXIS));
musicButtonPanel.setOpaque(false);

// Aggiunta pulsanti al pannello
musicButtonPanel.add(Box.createHorizontalStrut(8)); // padding sinistro
musicButtonPanel.add(prevSongButton);
musicButtonPanel.add(Box.createHorizontalStrut(4));
musicButtonPanel.add(playButton);
musicButtonPanel.add(Box.createHorizontalStrut(4));
musicButtonPanel.add(stopButton);
musicButtonPanel.add(Box.createHorizontalStrut(4));
musicButtonPanel.add(nextSongButton);
musicButtonPanel.add(Box.createHorizontalStrut(12));
musicButtonPanel.add(numeroCanticoField);
musicButtonPanel.add(Box.createHorizontalStrut(8)); // padding destro

// Allineamento orizzontale al centro
musicButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

// Aggiunta del pannello pulsanti al box principale
playerBox.add(musicButtonPanel);

JPanel southWrapper = new JPanel(new BorderLayout());
southWrapper.setOpaque(false); // se vuoi fondo trasparente
southWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0)); // top padding
southWrapper.add(playerBox, BorderLayout.CENTER);

// Aggiunta del playerBox e statusLabel al pannello principale (musicPanel)
musicPanel.add(statusLabel, BorderLayout.CENTER);
musicPanel.add(southWrapper, BorderLayout.SOUTH);

  // --- LAYOUT PERSONALIZZATO ---
  // Pannello principale con GridBagLayout per proporzioni 3/5 e 2/5
  JPanel mainPanel = new JPanel(new GridBagLayout());
  GridBagConstraints gbc = new GridBagConstraints();
  gbc.gridy = 0;
  gbc.fill = GridBagConstraints.BOTH;
  gbc.weighty = 1.0;
  // Pannello sinistro (programma) - 3/5
  gbc.gridx = 0;
  gbc.weightx = 3.0;
  mainPanel.add(leftPanel, gbc);
  // Pannello destro (timer + player) - 2/5
  gbc.gridx = 1;
  gbc.weightx = 2.0;
  JPanel rightPanel = new JPanel();
  rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
  rightPanel.setBackground(Color.BLACK);
  // 1. Crea il pannello timerPanel16_9
  JPanel timerPanel16_9 = new JPanel(new BorderLayout()) {
    @Override
    public Dimension getPreferredSize() {
        Container parent = getParent();
        int marginW = 0, marginH = 0;
        int parentWidth = 800, parentHeight = 600;
        if (parent != null) {
            parentWidth = parent.getWidth();
            parentHeight = parent.getHeight();
            marginW = (int)(parentWidth * 0.05);
            marginH = (int)(parentHeight * 0.05);
        }
        int maxW = parentWidth - 2 * marginW;
        int maxH = parentHeight - 2 * marginH;
        int width = maxW;
        int height = (int)(width / 16.0 * 9.0);
        if (height > maxH) {
            height = maxH;
            width = (int)(height * 16.0 / 9.0);
        }
        return new Dimension(width, height);
    }
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(160, 90);
    }
};
  timerPanel16_9.setBackground(Color.BLACK);
  
  
  // 2. Crea e aggiungi il centerPanel
  // Creo un pannello con layout BorderLayout per il timer e la label sotto
   timerWithEffPanel = new JPanel(new BorderLayout()) {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(426, 240);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(426, 240);
    }
  };
  timerWithEffPanel.setOpaque(false);
  
  timerLabel = new JLabel("00:00", SwingConstants.CENTER) {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(426, 240);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(426, 240);
    }
  };
  timerLabel.setFont(new Font("SansSerif", Font.BOLD, 180));
  timerLabel.setForeground(Color.GREEN);
  timerLabel.setOpaque(true);
  timerLabel.setBackground(Color.BLACK);
  timerLabel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(0, 0, 0, 0),
      BorderFactory.createLineBorder(Color.DARK_GRAY, 2)));
      
  timerWithEffPanel.add(timerLabel, BorderLayout.CENTER);
  efficaciTimeLabel = new JLabel("");
  efficaciTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
  efficaciTimeLabel.setForeground(new Color(0, 180, 220));
  efficaciTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
  efficaciTimeLabel.setPreferredSize(new Dimension(10, 0)); // spazio fisso
  timerWithEffPanel.add(efficaciTimeLabel, BorderLayout.SOUTH);
  
  centerPanel = new JPanel(cardLayout) {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(426, 240);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(426, 240);
    }
  };
  centerPanel.add(clockLabel, "clock");
  centerPanel.add(timerWithEffPanel, "timer");
  timerPanel16_9.add(centerPanel, BorderLayout.CENTER);
  
  // 2. Crea e aggiungi il pannello pulsanti
  JPanel timerButtonPanel = new JPanel(new GridLayout(1, 4, 8, 0));
  timerButtonPanel.setBackground(Color.BLACK);
  timerButtonPanel.add(prevButton);
  timerButtonPanel.add(startButton);
  timerButtonPanel.add(pauseButton);
  timerButtonPanel.add(nextButton);
  
  // Pannello player sotto
  JPanel playerPanel = new JPanel(new BorderLayout());
  playerPanel.add(musicPanel, BorderLayout.CENTER);
  playerPanel.add(statusLabel, BorderLayout.SOUTH);
  
  // --- CAMPO DI INPUT E PULSANTI SEMPLICI PER TESTO PREVIEW ---
  customMsgPanel = new JPanel();
  customMsgPanel.setLayout(new BoxLayout(customMsgPanel, BoxLayout.Y_AXIS));
  customMsgPanel.setBackground(Color.BLACK); // Sfondo nero
  customMsgPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
  customMsgPanel.setMaximumSize(new Dimension(340, Integer.MAX_VALUE));
  customMsgPanel.setPreferredSize(new Dimension(320, customMsgPanel.getPreferredSize().height));
  customMsgPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
  JLabel customMsgLabel = new JLabel("Invia messaggio");
  customMsgLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
  customMsgLabel.setForeground(new Color(0,180,180));
  customMsgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
  customMsgLabel.setHorizontalAlignment(SwingConstants.CENTER);
  JPanel inputPanel = new JPanel();
  inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
  inputPanel.setOpaque(false);
  inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
  JTextField customMsgField = new JTextField();
  customMsgField.setFont(new Font("SansSerif", Font.PLAIN, 16));
  customMsgField.setMaximumSize(new Dimension(300, 32));
  customMsgField.setPreferredSize(new Dimension(200, 32));
  customMsgField.setAlignmentX(Component.CENTER_ALIGNMENT);
  // Limite massimo caratteri input
  customMsgField.setDocument(new javax.swing.text.PlainDocument() {
      @Override
      public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
          if (str == null) return;
          if ((getLength() + str.length()) <= 120) {
              super.insertString(offs, str, a);
          }
      }
  });
  // Pulsante invio (freccia)
  JButton sendBtn = new JButton();
  sendBtn.setText("\u27A4"); // Freccia chiara
  sendBtn.setFont(new Font("SansSerif", Font.BOLD, 22));
  sendBtn.setBackground(new Color(0,180,180));
  sendBtn.setForeground(Color.BLACK);
  sendBtn.setFocusPainted(false);
  sendBtn.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
  sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  sendBtn.setToolTipText("Mostra messaggio");
  sendBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
  sendBtn.addActionListener(e -> {
      String testo = customMsgField.getText();
      if (testo == null || testo.trim().isEmpty()) return;
      mostraTestoSuClockLabel(testo);
      // Imposta il colore del testo a bianco
      timerLabel.setForeground(Color.WHITE);
      clockLabel.setForeground(Color.WHITE);
      if (externalClockLabel != null) externalClockLabel.setForeground(Color.WHITE);
  });
  // Pulsante X rossa (nascondi)
  JButton hideBtn = new JButton();
  hideBtn.setText("\u2716"); // Unicode X
  hideBtn.setFont(new Font("SansSerif", Font.BOLD, 22));
  hideBtn.setBackground(new Color(180, 60, 60));
  hideBtn.setForeground(Color.WHITE);
  hideBtn.setFocusPainted(false);
  hideBtn.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
  hideBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  hideBtn.setToolTipText("Nascondi messaggio");
  hideBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
  hideBtn.addActionListener(e -> {
      ripristinaClockLabel();
      customMsgField.setText(""); // Svuota l'input
  });
  inputPanel.add(customMsgField);
  inputPanel.add(Box.createHorizontalStrut(8));
  inputPanel.add(sendBtn);
  inputPanel.add(Box.createHorizontalStrut(4));
  inputPanel.add(hideBtn);
  customMsgPanel.add(Box.createVerticalStrut(10));
  customMsgLabel.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));
  customMsgPanel.add(customMsgLabel);
  customMsgPanel.add(Box.createVerticalStrut(4));
  customMsgPanel.add(inputPanel);
  customMsgPanel.add(Box.createVerticalStrut(6));
  // Nascondi il pannello messaggi se richiesto dalle impostazioni
  if (disabilitaMessaggiOratore) {
      customMsgPanel.setVisible(false);
  }
  
  // Aggiungi timer e player al pannello destro
  rightPanel.add(timerButtonPanel);
  rightPanel.add(Box.createVerticalStrut(10));
  rightPanel.add(timerPanel16_9);
  rightPanel.add(Box.createVerticalStrut(10));
  rightPanel.add(customMsgPanel);
  rightPanel.add(Box.createVerticalStrut(10));
  rightPanel.add(playerPanel);
  mainPanel.add(rightPanel, gbc);
  frame.setContentPane(mainPanel);
  frame.pack();
  frame.setResizable(true);
  frame.setVisible(true);
  cardLayout.show(centerPanel, "clock");
  timerLabel.setVisible(false);
  clockLabel.setVisible(true);
  startButton.addActionListener(e -> {
    // Se il timer è già in esecuzione e non è in pausa, non fare nulla
    if (!isPaused && swingTimer != null && swingTimer.isRunning() && timeLeft > 0) {
        return;
    }
    if (isPaused) {
        togglePause(); // Riprendi dal punto giusto se era in pausa
        return;
    }
    LocalTime now = LocalTime.now();
    if (now.isBefore(orarioInizioAdunanza)) {
        timeLeft = (int) Duration.between(now, orarioInizioAdunanza).getSeconds();
        inizioAutomatico = false;
        startContoAllaRovesciaIniziale();
    } else {
        inizioAutomatico = false;
        startTimer();
    }
});
  pauseButton.addActionListener(e -> togglePause());
  prevButton.addActionListener(e -> cambiaParte(-1));
  nextButton.addActionListener(e -> cambiaParte(1));
  nextSongButton.addActionListener(e -> {
      playNextSong();
      if (isPlaying) {
          playSelectedSong();
      }
  });
  prevSongButton.addActionListener(e -> {
      playPrevSong();
      if (isPlaying) {
          playSelectedSong();
      }
  });
  // --- MUSIC PLAYER LOGIC ---
  playButton.addActionListener(e -> {
      playSelectedSong();
      updateSongLabel();
      isPlaying = true;
      startPlayerBlinkTimer();
  });
  stopButton.addActionListener(e -> {
      stopSong();
      isPlaying = false;
      startPlayerBlinkTimer();
  });
  addResizeFontListener(timerLabel);
  addResizeFontListener(clockLabel);
  // Carica cartella musica se presente
  loadMusicFolderFromConfig();
  // --- DRAG & DROP PER RIORDINARE ---
  listaParti.setDragEnabled(true);
  listaParti.setDropMode(DropMode.INSERT);
  listaParti.setTransferHandler(new TransferHandler() {
      private int fromIndex = -1;
      @Override
      public int getSourceActions(JComponent c) {
          return MOVE;
      }
      @Override
      protected Transferable createTransferable(JComponent c) {
          fromIndex = listaParti.getSelectedIndex();
          return new StringSelection(""); // Dati non usati
      }
      @Override
      public boolean canImport(TransferSupport info) {
          return info.isDrop();
      }
      @Override
      public boolean importData(TransferSupport info) {
          if (!info.isDrop()) return false;
          JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
          int toIndex = dl.getIndex();
          if (fromIndex < 0 || fromIndex == toIndex) return false;
          Parte parte = listModel.getElementAt(fromIndex);
          listModel.remove(fromIndex);
          if (toIndex > fromIndex) toIndex--;
          listModel.add(toIndex, parte);
          listaParti.setSelectedIndex(toIndex);
          return true;
      }
  });
  // --- BARRA SUPERIORE ---
  JPanel topBar = new JPanel(new BorderLayout(10, 0)) {
      @Override
      public Dimension getPreferredSize() {
          // Altezza calcolata in base al font delle label verticali
          int h = 0;
          h += new JLabel("00:00:00").getFontMetrics(new Font("SansSerif", Font.BOLD, 16)).getHeight();
          h += new JLabel("00/00/0000").getFontMetrics(new Font("SansSerif", Font.PLAIN, 14)).getHeight();
          h += 8; // piccolo margine
          return new Dimension(super.getPreferredSize().width, h);
      }
      @Override
      public Dimension getMinimumSize() {
          return getPreferredSize();
      }
      @Override
      public Dimension getMaximumSize() {
          return getPreferredSize();
      }
  };


  JLabel iconLabel = new JLabel();
try {
    ImageIcon icon = new ImageIcon(getClass().getResource("/inTempo/res/icon.png"));
    Image img = icon.getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
    iconLabel.setIcon(new ImageIcon(img));
} catch (Exception e) {
    // Se non trova l'icona, lascia la label vuota
}


  topBar.setBackground(new Color(30, 30, 30));
  topBar.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8)); // padding quasi nullo
  // Nome app a sinistra
  JLabel appNameLabel = new JLabel("KH Timer");
  appNameLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
  appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
  
  Timer colorPulse = new Timer(50, null);
  colorPulse.addActionListener(new ActionListener() {
      private int hue = 0;
      private boolean reverse = false;
  
      @Override
      public void actionPerformed(ActionEvent e) {
          // Variazione dell'hue tra 0 e 360 per un effetto arcobaleno leggero
          float h = (hue % 360) / 360f;
          Color c = Color.getHSBColor(h, 0.6f, 1.0f); // Saturazione e luminosità costanti
          appNameLabel.setForeground(c);
          
          hue += reverse ? -2 : 2;
          if (hue >= 360) reverse = true;
          if (hue <= 0) reverse = false;
      }
  });
  colorPulse.start();
  
  JPanel leftPanelTop = new JPanel();
leftPanelTop.setOpaque(false);
leftPanelTop.setLayout(new BoxLayout(leftPanelTop, BoxLayout.X_AXIS));
leftPanelTop.add(iconLabel);
leftPanelTop.add(Box.createHorizontalStrut(6));
leftPanelTop.add(appNameLabel);

topBar.add(leftPanelTop, BorderLayout.WEST);  // --- NAVBAR ORDINATA: nome app a sinistra, a destra check, orario/data, impostazioni ---
  // Nome app a sinistra (già presente)
  // Creo il pannello destro con BoxLayout.X_AXIS
  JPanel rightPanelTop = new JPanel();
  rightPanelTop.setOpaque(false);
  rightPanelTop.setLayout(new BoxLayout(rightPanelTop, BoxLayout.X_AXIS));
  checkLabel = new JLabel("\u2714 App Aggiornata");
  checkLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
  checkLabel.setForeground(Color.GREEN);
  // Rendo la label cliccabile SOLO se mostra la X rossa
  checkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  checkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {
          if ("\u274C aggiornamento disponibile".equals(checkLabel.getText())) {
              try {
                  java.awt.Desktop.getDesktop().browse(new java.net.URI("https://github.com/diegoisme-dev/KH-Timer/releases"));
              } catch (Exception ex) {
                  // Ignora errori
              }
          }
      }
      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt) {
          if ("\u274C aggiornamento disponibile".equals(checkLabel.getText())) {
              checkLabel.setForeground(new Color(255, 80, 80));
              checkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          }
      }
      @Override
      public void mouseExited(java.awt.event.MouseEvent evt) {
          if ("\u274C aggiornamento disponibile".equals(checkLabel.getText())) {
              checkLabel.setForeground(Color.RED);
              checkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          }
      }
  });
  rightPanelTop.add(checkLabel);
  rightPanelTop.add(Box.createHorizontalStrut(8));
  JPanel centerPanelTop = new JPanel();
  centerPanelTop.setOpaque(false);
  centerPanelTop.setLayout(new BoxLayout(centerPanelTop, BoxLayout.Y_AXIS));
  centerPanelTop.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
  topClockLabel = new JLabel("00:00:00");
  topClockLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
  topClockLabel.setForeground(Color.CYAN);
  topClockLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  topDateLabel = new JLabel();
  topDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
  topDateLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  centerPanelTop.add(topClockLabel);
  centerPanelTop.add(topDateLabel);
  rightPanelTop.add(centerPanelTop);
  rightPanelTop.add(Box.createHorizontalStrut(8));

  // --- PULSANTE SCHERMO INTERO ---
  JButton fullscreenButton = new JButton(proiezioneAbilitata ? "🗗" : "⛶");
  fullscreenButton.setToolTipText("Attiva/disattiva schermo intero secondo monitor");
  fullscreenButton.setFocusPainted(false);
  fullscreenButton.setBorder(BorderFactory.createEmptyBorder());
  fullscreenButton.setContentAreaFilled(false);
  fullscreenButton.setPreferredSize(new Dimension(35, 35));
  fullscreenButton.setMaximumSize(new Dimension(35, 35));
  fullscreenButton.setMinimumSize(new Dimension(16, 16));
  rightPanelTop.add(fullscreenButton);


  
  fullscreenButton.addActionListener(e -> {
      proiezioneAbilitata = !proiezioneAbilitata;
      salvaImpostazioni();
      if (proiezioneAbilitata) {
          mostraOrologioSecondoMonitor();
          fullscreenButton.setText("🗗");
      } else {
          if (externalClockFrame != null) externalClockFrame.setVisible(false);
          fullscreenButton.setText("⛶");
      }
  });

  JButton settingsButton = new JButton();
  java.net.URL settingIconUrl = getClass().getResource("/inTempo/res/setting.png");
  if (settingIconUrl != null) {
      settingsButton.setIcon(new ImageIcon(settingIconUrl));
  } else {
      System.err.println("Immagine /inTempo/res/setting.png non trovata!");
  }
  settingsButton.setFocusPainted(false);
  settingsButton.setBorder(BorderFactory.createEmptyBorder());
  settingsButton.setContentAreaFilled(false);
  settingsButton.setPreferredSize(new Dimension(35, 35));
  settingsButton.setMaximumSize(new Dimension(35, 35));
  settingsButton.setMinimumSize(new Dimension(16, 16));
  rightPanelTop.add(settingsButton);
  topBar.add(rightPanelTop, BorderLayout.EAST);

  // Timer per aggiornare orologio e data
  topClockTimer = new Timer(1000, e -> updateTopClockAndDate());
  topClockTimer.start();
  updateTopClockAndDate();
  // --- LAYOUT CENTRALE 3/5 e 2/5 ---
  JPanel mainCenterPanel = new JPanel(null) {
      @Override
      public void doLayout() {
          int w = getWidth();
          int h = getHeight();
          int leftW = (int)(w * 0.6);
          int rightW = w - leftW;
          leftPanel.setBounds(0, 0, leftW, h);
          rightPanel.setBounds(leftW, 0, rightW, h);
      }
  };
  mainCenterPanel.add(leftPanel);
  mainCenterPanel.add(rightPanel);

  // --- ASSEMBLA FRAME ---
  frame.setLayout(new BorderLayout());
  frame.add(topBar, BorderLayout.NORTH);
  // --- FINE: ora checkLabel è inizializzata ---
  frame.add(mainCenterPanel, BorderLayout.CENTER);
  frame.pack();
  frame.setSize(1200, 700);
  frame.setResizable(true);
  frame.setVisible(true);
// --- FINE setup listaParti ---
// --- NEL COSTRUTTORE ---

  // Mostra orologio/timer su secondo monitor all'avvio
  mostraOrologioSecondoMonitor();
  // Listener per ingrandire le celle e riempire il container
  listaScroll.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
          aggiornaAltezzaCelleLista();
      }
  });
  listModel.addListDataListener(new javax.swing.event.ListDataListener() {
      public void intervalAdded(javax.swing.event.ListDataEvent e) { aggiornaAltezzaCelleLista(); }
      public void intervalRemoved(javax.swing.event.ListDataEvent e) { aggiornaAltezzaCelleLista(); }
      public void contentsChanged(javax.swing.event.ListDataEvent e) { aggiornaAltezzaCelleLista(); }
  });
  // Timer per lampeggiare i pulsanti
  blinkTimer = new Timer(500, e -> aggiornaLampeggioPulsanti());
  blinkTimer.start();
  caricaImpostazioni();
  loadMeetingSchedules();

  // Imposta l'orario iniziale basato sul prossimo meeting programmato
  if (!meetingSchedules.isEmpty()) {
      MeetingSchedule nextMeeting = meetingSchedules.stream()
          .min(Comparator.comparing(MeetingSchedule::getNextOccurrence))
          .get();
      orarioInizioAdunanza = nextMeeting.getTime();
  }

  if (proiezioneAbilitata) {
      mostraOrologioSecondoMonitor();
  } else {
      if (externalClockFrame != null) externalClockFrame.setVisible(false);
  }
  // Alla fine del costruttore, dopo la creazione di listaParti, forza il setCellRenderer con la logica aggiornata:
  listaParti.setCellRenderer(null);
  listaParti.setCellRenderer(new ListCellRenderer<Parte>() {
      private final Color verdeCantico = new Color(0, 220, 0);
      private final Color selezione = Color.DARK_GRAY;
      private final Color testoSelezione = Color.CYAN;
      private final Color introColor = Color.ORANGE;
      private final Color bgNormale = Color.BLACK;
      @Override
      public Component getListCellRendererComponent(JList<? extends Parte> list, Parte value, int index, boolean isSelected, boolean cellHasFocus) {
        
          JPanel panel = new JPanel(new BorderLayout()) {
              @Override
              protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  if (index < list.getModel().getSize() - 1) {
                      g.setColor(new Color(60, 60, 60));
                      g.fillRect(0, getHeight() - 2, getWidth(), 2);
                  }
              }
          };
          // Imposta l'altezza della pillola/cella
          panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 36));
          panel.setOpaque(true);
          JLabel label = new JLabel(value.toString(), SwingConstants.LEFT);
          if (value.nome.equalsIgnoreCase("INIZIO")) {
              label.setText("INIZIO");
              adaptFontToLabel(label);
              label.setFont(list.getFont().deriveFont(Font.BOLD));
              label.setForeground(new Color(0, 255, 255)); // ciano
              panel.setBackground(bgNormale);
              if (isSelected) {
                  panel.setBackground(selezione);
                  label.setForeground(testoSelezione);
              }
              // Orario solo a destra
              String orarioInizioStr = orarioInizioAdunanza.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              JLabel orariLabel = new JLabel(orarioInizioStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
              orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
              orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
              panel.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(18, 56, 18, 32),
                  panel.getBorder()
              ));
              panel.add(label, BorderLayout.CENTER);
              panel.add(orariLabel, BorderLayout.EAST);
              return panel;
          }
          if (value.nome.equalsIgnoreCase("FINE") && orarioFineProgrammatoFisso != null) {
              label.setText("FINE");
              label.setFont(list.getFont().deriveFont(Font.BOLD));
              label.setForeground(new Color(0, 255, 255)); // ciano
              panel.setBackground(bgNormale);
              if (isSelected) {
                  panel.setBackground(selezione);
                  label.setForeground(testoSelezione);
              }
              // Orario solo a destra (mostra orario previsto, non reale, nella lista)
              String orarioFineStr = orarioFineProgrammatoFisso.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              JLabel orariLabel = new JLabel(orarioFineStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
              orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
              orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
              panel.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(18, 56, 18, 32),
                  panel.getBorder()
              ));
              panel.add(label, BorderLayout.CENTER);
              panel.add(orariLabel, BorderLayout.EAST);
              return panel;
          }
          // --- Stile normale per tutte le altre parti ---
          label.setFont(list.getFont().deriveFont(Font.BOLD)); // grassetto
          label.setOpaque(false);
          label.setForeground(Color.WHITE);
          panel.setBackground(bgNormale);
          if (value.nome.toUpperCase().contains("CANTICO")) {
              label.setForeground(verdeCantico);
          }
          if (value.isIntro) {
              label.setForeground(introColor);
          }
          if (index == currentParteIndex) {
              panel.setBackground(selezione);
              label.setForeground(testoSelezione);
          }
          // --- ORARI O TEMPO EFFETTIVO ---
          JLabel orariLabel;
          String nomeUpper = value.nome.toUpperCase();
          boolean inEfficaci = false;
            for (int i = 0; i <= index; i++) {
                Parte p = listModel.get(i);
                if (p.nome.toUpperCase().contains("EFFICACI NEL MINISTERO")) inEfficaci = true;
                if (p.nome.toUpperCase().contains("VITA CRISTIANA")) inEfficaci = false;
            }
            boolean mostraEffettivo = (
                value.tempoEffettivo != null && (
                    nomeUpper.contains("LETTURA BIBLICA") ||
                    nomeUpper.contains("EFFICACI NEL MINISTERO") ||
                    nomeUpper.contains("DISCORSO PUBBLICO")
                )
            ) || (
                inEfficaci && !value.isIntro && value.durataSecondi > 0 && !nomeUpper.contains("CONSIGLI")
            );
          if (mostraEffettivo) {
              orariLabel = new JLabel(Parte.formatTime(value.tempoEffettivo.intValue()) + "\u00A0\u00A0\u00A0");
              orariLabel.setFont(list.getFont().deriveFont(Font.BOLD, 16f));
              orariLabel.setForeground(new Color(0, 180, 220));
          } else if (value.nome.equalsIgnoreCase("INIZIO")) {
                String orarioInizioStr = orarioInizioAdunanza.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                orariLabel = new JLabel(orarioInizioStr);
                orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
                orariLabel.setForeground(new Color(180,180,180));
          } else if (value.nome.equalsIgnoreCase("FINE") && orarioFineProgrammatoFisso != null) {
                String orarioFineStr = orarioFineProgrammatoFisso.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                orariLabel = new JLabel(orarioFineStr);
                orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
                orariLabel.setForeground(new Color(180,180,180));
          } else {
                // Calcola orario inizio e fine
                LocalTime inizio = orarioInizioAdunanza;
                int secondiTrascorsi = 0;
                for (int i = 0; i < index; i++) {
                    secondiTrascorsi += listModel.get(i).durataSecondi;
                }
                inizio = inizio.plusSeconds(secondiTrascorsi);
                LocalTime fine = inizio.plusSeconds(value.durataSecondi);
                String orarioInizio = inizio.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                String orarioFine = fine.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                orariLabel = new JLabel(orarioInizio + " - " + orarioFine);
                orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
                orariLabel.setForeground(new Color(180,180,180));
          }
          orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
          orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
          panel.add(label, BorderLayout.CENTER);
          panel.add(orariLabel, BorderLayout.EAST);
          return panel;
      }
  });
  // --- AGGIUNTA: funzione per applicare renderer pillola con animazione ---
  applicaRendererPillola();
  // --- AGGIORNAMENTO: carica icona app e icona impostazioni dal jar ---
  try {
      java.net.URL iconUrl = getClass().getResource("/inTempo/res/icon.png");
      if (iconUrl != null) {
          frame.setIconImage(javax.imageio.ImageIO.read(iconUrl));
      } else {
          System.err.println("Icona non trovata!");
      }
  } catch (Exception e) {
      System.err.println("Errore caricamento icona: " + e.getMessage());
  }
  // --- ANIMAZIONE SUL PULSANTE IMPOSTAZIONI ---
  settingsButton.addActionListener(e -> {
      if (impostazioniAperte) return; // evita doppia apertura
      impostazioniAperte = true;
      Icon originalIcon = settingsButton.getIcon();
      if (originalIcon instanceof ImageIcon) {
          int steps = 16;
          int[] currentStep = {0};
          Timer animTimer = new Timer(15, null);
          animTimer.addActionListener(ev -> {
              double angle = 2 * Math.PI * currentStep[0] / steps;
              Image img = ((ImageIcon) originalIcon).getImage();
              int w = img.getWidth(null);
              int h = img.getHeight(null);
              BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
              Graphics2D g2d = rotated.createGraphics();
              g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
              g2d.translate(w/2, h/2);
              g2d.rotate(angle);
              g2d.translate(-w/2, -h/2);
              g2d.drawImage(img, 0, 0, null);
              g2d.dispose();
              settingsButton.setIcon(new ImageIcon(rotated));
              currentStep[0]++;
              if (currentStep[0] > steps) {
            animTimer.stop();
                  settingsButton.setIcon(originalIcon);
                  mostraFinestraImpostazioni();
              }
          });
          animTimer.start();
      } else {
          mostraFinestraImpostazioni();
      }
  });
  // --- EFFETTO HOVER SULL'ICONA IMPOSTAZIONI ---
  settingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt) {
          Icon icon = settingsButton.getIcon();
          if (icon instanceof ImageIcon) {
    Image img = ((ImageIcon) icon).getImage();
    int w = img.getWidth(null);
    int h = img.getHeight(null);
              BufferedImage opac = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
              Graphics2D g2d = opac.createGraphics();
              g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Opacità più leggera
              g2d.drawImage(img, 0, 0, null);
              g2d.dispose();
              settingsButton.setIcon(new ImageIcon(opac));
          }
      }
      @Override
      public void mouseExited(java.awt.event.MouseEvent evt) {
          // Ripristina l'icona originale
          java.net.URL iconUrl = getClass().getResource("/inTempo/res/setting.png");
          if (iconUrl != null) {
              settingsButton.setIcon(new ImageIcon(iconUrl));
          }
      }
  });
  System.out.println("Test icon: " + getClass().getResource("/inTempo/res/icon.png"));
  System.out.println("Test setting: " + getClass().getResource("/inTempo/res/setting.png"));
  System.out.println("Test font: " + getClass().getResource("/inTempo/res/fonts/RobotoCondensed-Medium.ttf"));
  // Ora che checkLabel è inizializzata e visibile, posso controllare gli aggiornamenti
  checkForUpdates(); // Controllo aggiornamenti all'avvio
  // AGGIUNTA: pulsante temporaneo per testare l'import
  JButton importaWOLBtn = new JButton("Importa WOL") {
      @Override
      protected void paintComponent(Graphics g) {
          Graphics2D g2 = (Graphics2D) g.create();
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          int w = getWidth();
          int h = getHeight();
          // Gradiente blu-azzurro
          Color c1 = getModel().isRollover() ? new Color(0, 200, 255) : new Color(0, 140, 220);
          Color c2 = getModel().isRollover() ? new Color(0, 120, 200) : new Color(0, 80, 160);
          GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
          g2.setPaint(gp);
          g2.fillRoundRect(0, 0, w, h, 24, 24);
          // Bordo
          g2.setColor(new Color(0, 80, 120));
          g2.setStroke(new BasicStroke(2f));
          g2.drawRoundRect(1, 1, w-3, h-3, 24, 24);
    g2.dispose();
          super.paintComponent(g);
      }
  };
  importaWOLBtn.setFont(customTitleFont.deriveFont(Font.BOLD, 18f));
  importaWOLBtn.setForeground(Color.WHITE);
  importaWOLBtn.setBackground(new Color(0, 140, 220));
  importaWOLBtn.setFocusPainted(false);
  importaWOLBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
  importaWOLBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  // Crea icona download dinamica
  BufferedImage downloadIcon = new BufferedImage(22, 22, BufferedImage.TYPE_INT_ARGB);
  Graphics2D g2d = downloadIcon.createGraphics();
  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  g2d.setColor(Color.WHITE);
  g2d.setStroke(new BasicStroke(3f));
  // Freccia verso il basso
  g2d.drawLine(11, 4, 11, 16);
  g2d.drawLine(7, 12, 11, 16);
  g2d.drawLine(15, 12, 11, 16);
  // Rettangolino base
  g2d.drawLine(7, 18, 15, 18);
  g2d.dispose();
  importaWOLBtn.setIcon(new ImageIcon(downloadIcon));
  importaWOLBtn.setHorizontalAlignment(SwingConstants.LEFT);
  importaWOLBtn.setIconTextGap(10);
  // --- ANIMAZIONE LOADING PER IMPORTA WOL ---
final Timer[] loadingTimer = new Timer[1];
final BufferedImage[] loadingIcons = new BufferedImage[12];
final int[] loadingFrame = {0};
for (int i = 0; i < 12; i++) {
    BufferedImage img = new BufferedImage(22, 22, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2load = img.createGraphics();
    g2load.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2load.setColor(new Color(255,255,255,80));
    for (int j = 0; j < 12; j++) {
        int len = (j == i) ? 10 : 7;
        int alpha = (j == i) ? 255 : 80;
        g2load.setColor(new Color(0,220,255,alpha));
        double angle = Math.PI * 2 * j / 12.0;
        int x1 = 11 + (int)(Math.sin(angle) * 5);
        int y1 = 11 - (int)(Math.cos(angle) * 5);
        int x2 = 11 + (int)(Math.sin(angle) * len);
        int y2 = 11 - (int)(Math.cos(angle) * len);
        g2load.setStroke(new BasicStroke(3f));
        g2load.drawLine(x1, y1, x2, y2);
    }
    g2load.dispose();
    loadingIcons[i] = img;
}
  importaWOLBtn.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
          importaWOLBtn.setForeground(Color.BLACK);
          importaWOLBtn.setBackground(new Color(0, 220, 255));
          importaWOLBtn.repaint();
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
          importaWOLBtn.setForeground(Color.WHITE);
          importaWOLBtn.setBackground(new Color(0, 140, 220));
          importaWOLBtn.repaint();
      }
  });
  importaWOLBtn.addActionListener(e -> {
    boolean timerAttivo = swingTimer != null && swingTimer.isRunning() && !isPaused;
    if (timerAttivo) {
        int res = JOptionPane.showConfirmDialog(frame, "Se importi il programma WOL il timer si fermerà di funzionare. Vuoi continuare?", "Attenzione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.YES_OPTION) {
            return;
        }
        if (swingTimer != null) swingTimer.stop();
        isPaused = false;
        showClock();
    }
      importaWOLBtn.setEnabled(false);
      importaWOLBtn.setText("Caricamento in corso...");
      loadingFrame[0] = 0;
      importaWOLBtn.setIcon(new ImageIcon(loadingIcons[0]));
      loadingTimer[0] = new Timer(80, ev -> {
          loadingFrame[0] = (loadingFrame[0] + 1) % 12;
          importaWOLBtn.setIcon(new ImageIcon(loadingIcons[loadingFrame[0]]));
      });
      loadingTimer[0].start();
      // Esegui l'importazione in un thread separato per non bloccare la UI
      new Thread(() -> {
          try {
            if (!meetingSchedules.isEmpty()) {
                MeetingSchedule nextMeeting = meetingSchedules.stream()
                    .min(Comparator.comparing(MeetingSchedule::getNextOccurrence))
                    .get();
                orarioInizioAdunanza = nextMeeting.getTime();
            } else {
                orarioInizioAdunanza = LocalTime.of(19, 0);
            }
            importaPartiDaWOLSmart();
          } finally {
              SwingUtilities.invokeLater(() -> {
                currentParteIndex = 0;
                if (listaParti != null && listModel != null && listModel.size() > 0) {
                    listaParti.setSelectedIndex(0);
                    listaParti.ensureIndexIsVisible(0);
                }
                timeLeft = listModel.get(0).durataSecondi;
                updateTimerLabel();
                  if (loadingTimer[0] != null) loadingTimer[0].stop();
                  importaWOLBtn.setText("Importa WOL");
                  importaWOLBtn.setIcon(new ImageIcon(downloadIcon));
                  importaWOLBtn.setEnabled(true);
              });
          }
      }).start();
  });
  // Aggiungi il pulsante in alto a sinistra (vicino comboAndArrowPanel)
  comboAndArrowPanel.add(Box.createVerticalStrut(8));
  comboAndArrowPanel.add(importaWOLBtn);
  // Aggiungi il pannello importaWOLPanel
  importaWOLPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
  importaWOLPanel.setOpaque(false);
  importaWOLPanel.add(importaWOLBtn);
  if (mostraImportaWOL) {
      comboAndArrowPanel.add(importaWOLPanel);
  }
  // Dopo la creazione di comboAndArrowPanel e importaWOLPanel nel costruttore:
  if (comboAndArrowPanel != null && importaWOLPanel != null) {
      if (mostraImportaWOL) {
          if (importaWOLPanel.getParent() != comboAndArrowPanel) {
              comboAndArrowPanel.add(importaWOLPanel);
          }
      } else {
          comboAndArrowPanel.remove(importaWOLPanel);
      }
      comboAndArrowPanel.revalidate();
      comboAndArrowPanel.repaint();
  }

  // Aggiungi il pulsante di testo personalizzato sotto la preview
  JTextField testoInput = new JTextField(20);
  JButton testMsgBtn1 = new JButton("Mostra testo") {
      @Override
      protected void paintComponent(Graphics g) {
          Graphics2D g2 = (Graphics2D) g.create();
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          int w = getWidth();
          int h = getHeight();
          // Gradiente blu-azzurro
          Color c1 = getModel().isRollover() ? new Color(0, 200, 255) : new Color(0, 140, 220);
          Color c2 = getModel().isRollover() ? new Color(0, 120, 200) : new Color(0, 80, 160);
          GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
          g2.setPaint(gp);
          g2.fillRoundRect(0, 0, w, h, 24, 24);
          // Bordo
          g2.setColor(new Color(0, 80, 120));
          g2.setStroke(new BasicStroke(2f));
          g2.drawRoundRect(1, 1, w-3, h-3, 24, 24);
          g2.dispose();
          super.paintComponent(g);
      }
  };
  testMsgBtn1.setFont(customTitleFont.deriveFont(Font.BOLD, 18f));
  testMsgBtn1.setForeground(Color.WHITE);
  testMsgBtn1.setBackground(new Color(0, 140, 220));
  testMsgBtn1.setFocusPainted(false);
  testMsgBtn1.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
  testMsgBtn1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  testMsgBtn1.addActionListener(e -> {
      String testo = testoInput.getText();
      if (!testo.isEmpty()) {
          mostraTestoSuClockLabel(testo);
      }
  });

  JButton nascondiBtn = new JButton("Nascondi testo");
  nascondiBtn.setFont(customTitleFont.deriveFont(Font.BOLD, 16f));
  nascondiBtn.setForeground(Color.WHITE);
  nascondiBtn.setBackground(new Color(180, 60, 60));
  nascondiBtn.setFocusPainted(false);
  nascondiBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
  nascondiBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  nascondiBtn.addActionListener(e -> ripristinaClockLabel());

  JPanel testPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
  testPanel.setOpaque(false);
  testPanel.add(testoInput);
  testPanel.add(testMsgBtn1);
  testPanel.add(Box.createHorizontalStrut(8));
  testPanel.add(nascondiBtn);
  // Aggiungi il pannello di testo sotto la preview
  centerPanel.add(testPanel, BorderLayout.SOUTH);

}
private void startTimer() {
    // Se sto riprendendo da una pausa, NON resettare timeLeft
    if (timeLeftWhenPaused > 0) {
        timeLeft = timeLeftWhenPaused;
        timeLeftWhenPaused = -1;
    } else {
        timeLeft = listModel.get(currentParteIndex).durataSecondi;
    }
    System.out.println("DEBUG: Avvio timer per parte: " + currentParteIndex + " | durata: " + listModel.get(currentParteIndex).durataSecondi + " | timeLeft: " + timeLeft + " | isPaused: " + isPaused);
  cardLayout.show(centerPanel, "timer");
  timerLabel.setVisible(true);
  clockLabel.setVisible(false);
  if (currentParteIndex >= listModel.size()) return;
  Parte parte = listModel.get(currentParteIndex);
  // Inizializza il tempo di inizio della parte
  if (inizioParteMillis <= 0) {
      inizioParteMillis = System.currentTimeMillis();
  }
  // Se parte CONSIGLI, riduci il font del timer
  if (parte.nome.toUpperCase().contains("CONSIGLI")) {
      timerLabel.setFont(new Font("SansSerif", Font.BOLD, 60));
  } else {
      timerLabel.setFont(new Font("SansSerif", Font.BOLD, 180));
  }
  // Se attivo, parte CONSIGLI e tempo effettivo disponibile, mostra solo il tempo mm:ss
  String testoEff = "";
  if (mostraTempoEfficaciMinistero && parte.nome.toUpperCase().contains("CONSIGLI") && tempoEffettivoUltimaEfficaci > 0) {
      testoEff = Parte.formatTime((int)tempoEffettivoUltimaEfficaci);
  }
  if (testoEff != null && !testoEff.isEmpty()) {
      if (timerWithEffPanel.getComponentCount() < 2)
          timerWithEffPanel.add(efficaciTimeLabel, BorderLayout.SOUTH);
      efficaciTimeLabel.setText(testoEff);
      efficaciTimeLabel.setVisible(true);
  } else {
      efficaciTimeLabel.setText("");
      efficaciTimeLabel.setVisible(false);
      if (timerWithEffPanel.getComponentCount() > 1)
          timerWithEffPanel.remove(efficaciTimeLabel);
  }
  timerWithEffPanel.revalidate();
  timerWithEffPanel.repaint();
  efficaciTimeLabel.repaint();
  if (parte.nome.equalsIgnoreCase("FINE") && orarioFineProgrammatoFisso != null) {
      // Mostra "FINE" a schermo
      String orarioAttuale = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
      LocalTime oraAttuale = LocalTime.now();
      Duration diff = Duration.between(orarioFineProgrammatoFisso, oraAttuale);
      long secDiff = diff.getSeconds();
      String diffStr;
      String colorDiff;
      if (secDiff < 0) {
          diffStr = String.format("+%d:%02d", Math.abs(secDiff) / 60, Math.abs(secDiff) % 60);
          colorDiff = "#00ff00"; // verde
      } else if (secDiff > 0) {
          diffStr = String.format("-%d:%02d", secDiff / 60, secDiff % 60);
          colorDiff = "#ff0000"; // rosso
      } else {
          diffStr = "+0:00";
          colorDiff = "#00ff00";
      }
      // Font moderno e dimensioni adattive per 16:9
      String fontStack = "'Segoe UI', 'Roboto', 'Arial', 'sans-serif'";
      int baseFont = Math.max(24, timerLabel.getHeight() / 4); // font adattivo
      int orarioFont = Math.max(18, timerLabel.getHeight() / 11);
      int diffFont = Math.max(14, timerLabel.getHeight() / 14);
      // Scritta FINE gialla lampeggiante, orario bianco, anticipo verde, ritardo rosso
      String fineColor = blinkState ? "#ffff00" : "#222200";
      timerLabel.setText("<html><div style='font-family:" + fontStack + ";text-align:center;'>"
          + "<div id='fineLamp' style='font-size:" + baseFont + "px;font-weight:bold;'>FINE</div>"
          + "<div style='font-size:" + orarioFont + "px;color:#ffffff;font-weight:normal;'>" + orarioAttuale + "</div>"
          + "<div style='font-size:" + diffFont + "px;font-weight:bold;color:" + colorDiff + ";'>" + diffStr + "</div>"
          + "</div></html>");
      timerLabel.setForeground(Color.YELLOW); // per sicurezza
      adaptFontToLabel(timerLabel);
      // Mostra "FINE" anche sul monitor esterno
      if (externalClockLabel != null) {
          int extBaseFont = Math.max(40, externalClockLabel.getHeight() / 7);
          int extOrarioFont = Math.max(28, externalClockLabel.getHeight() / 11);
          int extDiffFont = Math.max(20, externalClockLabel.getHeight() / 14);
          String fineColorExt = blinkState ? "#ffff00" : "#222200";
          externalClockLabel.setText("<span id='fineLampExt' style='font-family:" + fontStack + ";font-size:"+extBaseFont+"px;font-weight:bold;'>FINE</span><br>" +
              "<span style='font-family:" + fontStack + ";font-size:"+extOrarioFont+"px;color:#ffffff;font-weight:normal;'>" + orarioAttuale + "</span><br>" +
              "<span style='font-family:" + fontStack + ";font-size:"+extDiffFont+"px;font-weight:bold;color:" + colorDiff + ";'>" + diffStr + "</span>");
          externalClockLabel.setForeground(Color.YELLOW);
          externalIsTimer = true;
      }
      ritardoUltimoCambioParte = secDiff;
      if (swingTimer != null && swingTimer.isRunning()) {
          swingTimer.stop();
      }
      updateStatusLabel();
      return;
  }
  if (!isPaused || timeLeft <= 0) {
      timeLeft = parte.durataSecondi;
  }
  isPaused = false;
  listaParti.repaint();
  if (swingTimer != null && swingTimer.isRunning()) swingTimer.stop();
  swingTimer = new Timer(1000, e -> {
      if (!isPaused) {
          timeLeft--;

          updateTimerLabel();
          listaParti.repaint();
          // updateStatusLabel(); // NON aggiornare qui!
          // Se il timer è scaduto, assorbi il ritardo prima di passare alla prossima parte
          if (timeLeft <= 0) {
              // assorbiRitardo(); // RIMOSSO
              // --- FERMA IL TIMER DI REPAINT LISTA ---
              if (repaintListaTimer != null) repaintListaTimer.stop();
          }
      }
  });
  swingTimer.start();
  // --- AVVIA IL TIMER DI REPAINT LISTA ---
  if (repaintListaTimer != null) repaintListaTimer.stop();
  repaintListaTimer = new Timer(250, e -> {
      if (!isPaused) listaParti.repaint();
  });
  repaintListaTimer.start();
  // Aggiorna subito la label esterna e segnala che ora mostra il timer
  if (externalClockLabel != null) {
      externalIsTimer = true;
      updateTimerLabel(); // così la logica della checkbox viene rispettata sempre
  }
  if (externalClockSwingTimer != null) externalClockSwingTimer.stop();
  aggiornaLampeggioPulsanti();
  // Aggiorna la statusLabel SOLO se non siamo su FINE
  if (!parte.nome.equalsIgnoreCase("FINE")) {
      ritardoUltimoCambioParte = Long.MIN_VALUE;
      updateStatusLabel();
  }
}

private void startContoAllaRovesciaIniziale() {
  cardLayout.show(centerPanel, "timer");
  timerLabel.setVisible(true);
  clockLabel.setVisible(false);
  isPaused = false;
  if (swingTimer != null && swingTimer.isRunning()) swingTimer.stop();
  swingTimer = new Timer(1000, e -> {
      if (!isPaused) {
          timeLeft--;
          updateTimerLabel();
          // updateStatusLabel(); // RIMOSSO: la label si aggiorna solo al cambio parte
          if (timeLeft <= 0) {
              swingTimer.stop();
              // Dopo il conto alla rovescia, controlla la parte INIZIO
              if (listModel.getSize() > 0 && listModel.getElementAt(0).nome.equalsIgnoreCase("INIZIO")) {
                  Parte inizio = listModel.getElementAt(0);
                  if (inizio.durataSecondi > 0) {
                      currentParteIndex = 0;
                  } else {
                      currentParteIndex = 1;
                  }
              } else {
                  currentParteIndex = 0;
              }
              startTimer();
          }
      }
  });
  swingTimer.start();
  // Aggiorna subito la label esterna e segnala che ora mostra il timer
  if (externalClockLabel != null) {
      externalIsTimer = true;
      updateTimerLabel();
  }
  if (externalClockSwingTimer != null) externalClockSwingTimer.stop();
  aggiornaLampeggioPulsanti();
}
private int calcolaSecondiMancantiAlle19() {
  LocalTime now = LocalTime.now();
  Duration duration = Duration.between(now, orarioInizioAdunanza);
  return (int) duration.getSeconds();
}

private void togglePause() {
    isPaused = !isPaused;
    if (isPaused) {
        // Salva il tempo rimasto quando si mette in pausa
        timeLeftWhenPaused = timeLeft;
    } else {
        // Se si riprende dalla pausa, ripristina il tempo rimasto
        if (timeLeftWhenPaused > 0) {
            timeLeft = timeLeftWhenPaused;
            timeLeftWhenPaused = -1;
        }
        
    }
    aggiornaLampeggioPulsanti();
    // --- FERMA IL TIMER DI REPAINT LISTA SE IN PAUSA ---
    if (repaintListaTimer != null) repaintListaTimer.stop();
}

private void cambiaParte(int delta) {
  int newIndex = currentParteIndex + delta;
  // Salta titoli sezione (isIntro==true e durata==0)
  while (newIndex >= 0 && newIndex < listModel.size() && listModel.get(newIndex).isIntro && listModel.get(newIndex).durataSecondi == 0) {
      newIndex += (delta > 0 ? 1 : -1);
  }
  if (newIndex >= 0 && newIndex < listModel.size()) {
      // Registra il tempo impiegato per la parte che sta finendo (sempre, anche se CONSIGLI ha timeLeft > 0)
      long now = System.currentTimeMillis();
      if (inizioParteMillis > 0 && currentParteIndex >= 0 && currentParteIndex < listModel.size()) {
          tempoEffettivoUltimaParte = (now - inizioParteMillis) / 1000;
          // Salva il tempo effettivo nella parte appena conclusa
          Parte parteConclusa = listModel.get(currentParteIndex);
          parteConclusa.tempoEffettivo = tempoEffettivoUltimaParte;
          listModel.setElementAt(parteConclusa, currentParteIndex);
      }
      inizioParteMillis = now;
      // --- LOGICA PER ADUNANZA PUBBLICA ---
      if (currentProgramIndex == 2) {
          if (currentParteIndex >= 0 && currentParteIndex < listModel.size()) {
              Parte parteCorrente = listModel.get(currentParteIndex);
              if (parteCorrente.nome.equalsIgnoreCase("DISCORSO PUBBLICO")) {
                  int idxTorre = -1;
                  for (int i = 0; i < listModel.size(); i++) {
                      if (listModel.get(i).nome.equalsIgnoreCase("STUDIO TORRE DI GUARDIA")) {
                          idxTorre = i;
                          break;
                      }
                  }
                  if (idxTorre != -1 && assorbiRitardoAutomatico) {
                      int secondiPrevisti = 0;
                      for (int i = 0; i <= currentParteIndex; i++) {
                          secondiPrevisti += listModel.get(i).durataSecondi;
                      }
                      LocalTime orarioPrevistoFineDiscorso = orarioInizioAdunanza.plusSeconds(secondiPrevisti);
                      LocalTime oraAttuale = LocalTime.now();
                      long ritardo = Duration.between(orarioPrevistoFineDiscorso, oraAttuale).getSeconds();
                      if (ritardo > 0) {
                          Parte torre = listModel.get(idxTorre);
                          torre.durataSecondi = (int)Math.max(60, torre.durataSecondi - ritardo);
                          listModel.setElementAt(torre, idxTorre);
                      }
                  }
              }
          }
      }
      // --- LOGICA PER VITA CRISTIANA E MINISTERO ---
      if (currentProgramIndex == 0 || programmaImportato) {
          // SOLO quando si entra nello studio biblico di congregazione
          Parte parteNuova = listModel.get(newIndex);
          if (parteNuova.nome.toUpperCase().contains("STUDIO BIBLICO DI CONGREGAZIONE") && assorbiRitardoAutomatico) {
              int secondiPrevisti = 0;
              for (int i = 0; i < newIndex; i++) {
                  secondiPrevisti += listModel.get(i).durataSecondi;
              }
              LocalTime orarioPrevistoInizioStudio = orarioInizioAdunanza.plusSeconds(secondiPrevisti);
              LocalTime oraAttuale = LocalTime.now();
              long ritardo = Duration.between(orarioPrevistoInizioStudio, oraAttuale).getSeconds();
              if (ritardo > 0) {
                  Parte studio = listModel.get(newIndex);
                  studio.durataSecondi = (int)Math.max(60, studio.durataSecondi - ritardo);
                  listModel.setElementAt(studio, newIndex);
              }
          }
      }
      // --- LOGICA PER ADUNANZA PUBBLICA SORVEGLIANTE ---
      if (currentProgramIndex == 3) {
          if (currentParteIndex >= 0 && currentParteIndex < listModel.size()) {
              Parte parteCorrente = listModel.get(currentParteIndex);
              if (parteCorrente.nome.equalsIgnoreCase("DISCORSO PUBBLICO SORVEGLIANTE")) {
                  int idxTorre = -1;
                  for (int i = 0; i < listModel.size(); i++) {
                      if (listModel.get(i).nome.equalsIgnoreCase("STUDIO TORRE DI GUARDIA")) {
                          idxTorre = i;
                          break;
                      }
                  }
                  if (idxTorre != -1 && assorbiRitardoAutomatico) {
                      int secondiPrevisti = 0;
                      for (int i = 0; i <= currentParteIndex; i++) {
                          secondiPrevisti += listModel.get(i).durataSecondi;
                      }
                      LocalTime orarioPrevistoFineDiscorso = orarioInizioAdunanza.plusSeconds(secondiPrevisti);
                      LocalTime oraAttuale = LocalTime.now();
                      long ritardo = Duration.between(orarioPrevistoFineDiscorso, oraAttuale).getSeconds();
                      if (ritardo > 0) {
                          Parte torre = listModel.get(idxTorre);
                          torre.durataSecondi = (int)Math.max(60, torre.durataSecondi - ritardo);
                          listModel.setElementAt(torre, idxTorre);
                      }
                  }
              }
          }
      }
      // --- LOGICA PER ADUNANZA STRANA ---
      if (currentProgramIndex == 4) {
          if (currentParteIndex >= 0 && currentParteIndex < listModel.size()) {
              Parte parteCorrente = listModel.get(currentParteIndex);
              if (parteCorrente.nome.equalsIgnoreCase("DISCORSO PUBBLICO")) {
                  int idxTorre = -1;
                  for (int i = 0; i < listModel.size(); i++) {
                      if (listModel.get(i).nome.equalsIgnoreCase("STUDIO TORRE DI GUARDIA")) {
                          idxTorre = i;
                          break;
                      }
                  }
                  if (idxTorre != -1 && assorbiRitardoAutomatico) {
                      int secondiPrevisti = 0;
                      for (int i = 0; i <= currentParteIndex; i++) {
                          secondiPrevisti += listModel.get(i).durataSecondi;
                      }
                      LocalTime orarioPrevistoFineDiscorso = orarioInizioAdunanza.plusSeconds(secondiPrevisti);
                      LocalTime oraAttuale = LocalTime.now();
                      long ritardo = Duration.between(orarioPrevistoFineDiscorso, oraAttuale).getSeconds();
                      if (ritardo > 0) {
                          Parte torre = listModel.get(idxTorre);
                          torre.durataSecondi = (int)Math.max(60, torre.durataSecondi - ritardo);
                          listModel.setElementAt(torre, idxTorre);
                      }
                  }
              }
          }
      }
      currentParteIndex = newIndex;
      Parte parte = listModel.get(currentParteIndex);
      timeLeft = parte.durataSecondi;
      // --- ANIMAZIONE: illumina la parte attiva ---
      parteAnimata = currentParteIndex;
      animStep = 1;
      if (animTimer != null && animTimer.isRunning()) animTimer.stop();
      animTimer = new Timer(30, e -> {
          animStep++;
          if (animStep > 15) { // durata animazione aumentata (~450ms)
              parteAnimata = -1;
              animStep = 0;
                animTimer.stop();
          }
          listaParti.repaint();
      });
      animTimer.start();
      listaParti.repaint();
      updateTimerLabel();
      updateStatusLabel();
      startTimer();
      if (parte.nome.equalsIgnoreCase("FINE")) {
        mostraReportFinale();
    }
  }
}
private void updateTimerLabel() {
    // --- PRIORITÀ: testo personalizzato in primo piano ---
    if (testoPersonalizzatoClock != null) {
        timerLabel.setText(testoPersonalizzatoClock);
        adaptFontToLabel(timerLabel);
        if (externalClockLabel != null) {
            externalClockLabel.setText(testoPersonalizzatoClock);
            externalClockLabel.setForeground(timerLabel.getForeground());
            updateExternalClockLabelFont();
        }
        return; // NON eseguire nessun'altra logica!
    }
 boolean showEffTime = false;
 if (currentParteIndex >= 0 && currentParteIndex < listModel.size()) {
     Parte parte = listModel.get(currentParteIndex);
     // LOGICA SPECIALE PER INIZIO
if (parte.nome.equalsIgnoreCase("INIZIO")) {
    LocalTime oraAttuale = LocalTime.now();
    long diff = Duration.between(oraAttuale, orarioInizioAdunanza).getSeconds();
    if (diff > 0) {
        // Orario di inizio nel futuro: countdown
        timeLeft = (int) diff;
    } else {
        // Orario di inizio già passato: timer negativo
        timeLeft = (int) diff;
    }
}
     if (parte.nome.toUpperCase().contains("CANTICO")) {
         // Estrai il numero del cantico (es: N. 134 oppure solo 134)
         
         String num = "";
        String nomeUpper = parte.nome.toUpperCase();
        int idx = nomeUpper.indexOf("CANTICO");
            if (idx != -1) {
                String after = parte.nome.substring(idx + "CANTICO".length());
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(after);
                if (m.find()) num = m.group(1);
            }
         int labelW = timerLabel.getWidth();
         int labelH = timerLabel.getHeight();
         // Limiti min/max per i font
         int fontCantico = Math.max(18, Math.min((int)(labelH * 0.22), 120));
         int fontNum = Math.max(40, Math.min((int)(labelH * 0.85), (int)(labelW * 0.95)));
         // Riduci se troppo largo
         Font baseFont = new Font("SansSerif", Font.BOLD, fontNum);
         FontMetrics fm = timerLabel.getFontMetrics(baseFont);
         int textWidth = fm.stringWidth(num);
         while ((textWidth > labelW * 0.98 || fm.getHeight() > labelH * 0.8) && fontNum > 40) {
             fontNum--;
             baseFont = baseFont.deriveFont((float)fontNum);
             fm = timerLabel.getFontMetrics(baseFont);
             textWidth = fm.stringWidth(num);
         }
         // --- NUOVA VISUALIZZAZIONE ---
         timerLabel.setText("");
         timerLabel.setLayout(new BorderLayout());
         timerLabel.removeAll();
         timerLabel.setOpaque(true);
         timerLabel.setBackground(Color.BLACK);
         timerLabel.add(creaPanelCantico(num, fontCantico, fontNum), BorderLayout.CENTER);
         timerLabel.revalidate();
         timerLabel.repaint();
         // --- LABEL ESTERNA ---
         if (externalClockLabel != null && timerLabel.isVisible() && externalIsTimer) {
             int extW = externalClockLabel.getWidth();
             int extH = externalClockLabel.getHeight();
             int extFontCantico = Math.max(18, Math.min((int)(extH * 0.13), 120));
             int extFontNum = Math.max(40, Math.min((int)(extH * 0.7), (int)(extW * 0.95)));
             Font extBaseFont = new Font("SansSerif", Font.BOLD, extFontNum);
             FontMetrics extFm = externalClockLabel.getFontMetrics(extBaseFont);
             int extTextWidth = extFm.stringWidth(num);
             while ((extTextWidth > extW * 0.98 || extFm.getHeight() > extH * 0.8) && extFontNum > 40) {
                 extFontNum--;
                 extBaseFont = extBaseFont.deriveFont((float)extFontNum);
                 extFm = externalClockLabel.getFontMetrics(extBaseFont);
                 extTextWidth = extFm.stringWidth(num);
             }
             externalClockLabel.setText("");
             externalClockLabel.setLayout(new BorderLayout());
             externalClockLabel.removeAll();
             externalClockLabel.setOpaque(true);
             externalClockLabel.setBackground(Color.BLACK);
             externalClockLabel.add(creaPanelCantico(num, extFontCantico, extFontNum), BorderLayout.CENTER);
             externalClockLabel.revalidate();
             externalClockLabel.repaint();
         }
                return;
     } else {
         // Se NON è un cantico, resetta la label esterna al timer normale
         if (externalClockLabel != null && timerLabel.isVisible() && externalIsTimer) {
             externalClockLabel.removeAll(); // Rimuove ogni componente grafico residuo
             externalClockLabel.setLayout(new BorderLayout()); // Reset layout
             int min = Math.abs(timeLeft) / 60;
             int sec = Math.abs(timeLeft) % 60;
             String sign = timeLeft < 0 ? "-" : "";
             String timerStr = sign + String.format("%02d:%02d", min, sec);
             int labelW = externalClockLabel.getWidth();
             int labelH = externalClockLabel.getHeight();
             int fontSize = Math.min((int)(labelH * 0.9), (int)(labelW * 0.9));
             String html = "<html><div style='text-align:center;'><span style=\"font-size:" + fontSize + "px;font-family:SansSerif;font-weight:bold;\">" + timerStr + "</span></div></html>";
             externalClockLabel.setText(html);
             externalClockLabel.setForeground(timeLeft < 0 ? Color.RED : Color.GREEN);
             externalClockLabel.revalidate();
             externalClockLabel.repaint();
         }
     }
     if (parte.nome.toUpperCase().contains("CONSIGLI")) {
         showEffTime = true;
     }
     if (currentProgramIndex == 2 && parte.nome.toUpperCase().contains("RINGRAZIAMENTI E INTRODUZIONE TDG")) {
         showEffTime = true;
     }
     // NON mostrare mai il tempo effettivo per le parti di tipo INTRO o COMMENTI INTRODUTTIVI
     if (parte.nome.toUpperCase().contains("INTRO") || parte.nome.toUpperCase().contains("COMMENTI INTRODUTTIVI")) {
         showEffTime = false;
     }
 }
  timerLabel.setVisible(true);
  int min = Math.abs(timeLeft) / 60;
  int sec = Math.abs(timeLeft) % 60;
  String sign = timeLeft < 0 ? "" : "";
  String html = "";
  String tempoPrecedente = tempoEffettivoUltimaParte >= 0 ? Parte.formatTime((int)tempoEffettivoUltimaParte) : "00:00";
  String colorTimer = (timeLeft < 0) ? "#ff0000" : "#ffff00";
  if (showEffTime && mostraTempoEfficaciMinistero) {
      timerLabel.removeAll();
      timerLabel.setLayout(new BorderLayout());
      // Responsive: calcola font in base a larghezza e altezza
      int labelW = timerLabel.getWidth();
      int labelH = timerLabel.getHeight();
      int fontEff = Math.min((int)(labelH / 2.8), (int)(labelW / 7)); // tempo effettivo sopra
      int fontMinuti = Math.min((int)(labelH / 3.5), (int)(labelW / 4.5)); // timer sotto
      int fontSecondi = Math.min((int)(labelH / 7), (int)(labelW / 12)); // secondi piccoli
      html = "<html><div style='text-align:center;'>"
          + "<span style=\"font-size:" + fontEff + "px;font-family:SansSerif;font-weight:bold;color:#00b4dc;letter-spacing:2px;line-height:1;\">" + tempoPrecedente + "</span><br>"
          + "<span style=\"font-size:" + fontMinuti + "px;font-family:SansSerif;font-weight:bold;line-height:1;letter-spacing:2px;color:" + colorTimer + ";\">"
          + (timeLeft < 0 ? "" : "") + String.format("%02d", Math.abs(timeLeft) / 60) + "</span>"
          + "<span style=\"font-size:" + fontSecondi + "px;font-family:sans-serif;font-weight:bold;vertical-align:bottom;letter-spacing:1px;color:" + colorTimer + ";\">:"
          + String.format("%02d", Math.abs(timeLeft) % 60) + "</span>"
          + "</div></html>";
      timerLabel.setText(html);
      adaptFontToLabel(timerLabel);
      if (smallTimerLabel != null) {
          smallTimerLabel.setText(timerLabel.getText().replaceAll("<[^>]*>", ""));
      }
      // --- AGGIUNTA: aggiorna externalClockLabel su due righe ---
      if (externalClockLabel != null && timerLabel.isVisible() && externalIsTimer) {
          if (mostraTempoEfficaciMinistero && showEffTime) {
              externalClockLabel.removeAll();
              externalClockLabel.setLayout(new BorderLayout());
              JPanel panel = new JPanel();
              panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
              panel.setBackground(Color.BLACK);
              panel.setOpaque(true);
              // JLabel per tempo effettivo
              JLabel effLabel = new JLabel(tempoPrecedente, SwingConstants.CENTER);
              effLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(60, externalClockLabel.getHeight()/4)));
              effLabel.setForeground(new Color(0, 180, 220));
              effLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
              // JLabel per timer
              String timerStr = (timeLeft < 0 ? "" : "") + String.format("%02d:%02d", Math.abs(timeLeft) / 60,  Math.abs(timeLeft) % 60);
              JLabel timerLabel2 = new JLabel(timerStr, SwingConstants.CENTER);
              timerLabel2.setFont(new Font("SansSerif", Font.BOLD, Math.max(80, externalClockLabel.getHeight()/2)));
              timerLabel2.setForeground(timeLeft < 0 ? Color.RED : Color.YELLOW);
              timerLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
              panel.add(effLabel);
              panel.add(Box.createVerticalStrut(10));
              panel.add(timerLabel2);
              externalClockLabel.add(panel, BorderLayout.CENTER);
              externalClockLabel.revalidate();
              externalClockLabel.repaint();
              return;
          } else {
              externalClockLabel.removeAll();
              externalClockLabel.setLayout(new BorderLayout());
              if (Math.abs(timeLeft) < 60) {
                  // SOLO SECONDI GRANDI
                  JLabel secLabel = new JLabel(sign + String.format("%02d", sec), SwingConstants.CENTER);
                  secLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(120, externalClockLabel.getHeight() / 1)));
                  secLabel.setForeground(timeLeft < 0 ? Color.RED : Color.YELLOW);
                  secLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                  externalClockLabel.add(secLabel, BorderLayout.CENTER);
              } else {
                  // MINUTI GRANDI, SECONDI PICCOLI IN BASSO A DESTRA
                  JPanel timerPanel = new JPanel(null);
                  timerPanel.setBackground(Color.BLACK);
                  timerPanel.setOpaque(true);
                  int w = externalClockLabel.getWidth();
                  int h = externalClockLabel.getHeight();
                  int minFont = Math.max(100, (int)(h * 0.7));
                  int secFont = Math.max(40, (int)(h * 0.25));
                  JLabel minLabel = new JLabel(sign + String.format("%02d", min));
                  minLabel.setFont(new Font("SansSerif", Font.BOLD, minFont));
                  minLabel.setForeground(timeLeft < 0 ? Color.RED : Color.YELLOW);
                  minLabel.setBounds((int)(w*0.15), (int)(h*0.15), (int)(w*0.7), minFont+10);
                  minLabel.setHorizontalAlignment(SwingConstants.LEFT);
                  JLabel secLabel = new JLabel(String.format(":%02d", sec));
                  secLabel.setFont(new Font("SansSerif", Font.BOLD, secFont));
                  secLabel.setForeground(timeLeft < 0 ? Color.RED : Color.YELLOW);
                  secLabel.setBounds((int)(w*0.65), (int)(h*0.15)+minFont-10, (int)(w*0.3), secFont+10);
                  secLabel.setHorizontalAlignment(SwingConstants.LEFT);
                  timerPanel.add(minLabel);
                  timerPanel.add(secLabel);
                  externalClockLabel.add(timerPanel, BorderLayout.CENTER);
              }
              externalClockLabel.revalidate();
              externalClockLabel.repaint();
              return;
          }
      } else {
          // Solo timer (una riga)
          int extW = externalClockLabel.getWidth();
          int extH = externalClockLabel.getHeight();
          int extFontMinuti = Math.min((int)(extH / 2.5), (int)(extW / 4.5));
          int extFontSecondi = Math.min((int)(extH / 7), (int)(extW / 12));
          String html1 = "<html><div style='text-align:center;'><span style=\"font-size:" + extFontMinuti + "px;font-family:SansSerif;font-weight:bold;line-height:1;\">" + (timeLeft < 0 ? "" : "") + String.format("%02d", Math.abs(timeLeft) / 60) + "</span>"
              + "<span style=\"font-size:" + extFontSecondi + "px;font-family:sans-serif;font-weight:bold;vertical-align:super;\">:" + String.format("%02d", Math.abs(timeLeft) % 60) + "</span></div></html>";
          externalClockLabel.setText(html1);
          externalClockLabel.setForeground(timeLeft < 0 ? Color.RED : Color.GREEN);
      }
      // --- FINE AGGIUNTA ---
      return;
  } else if (timeLeft < 60 && timeLeft >= 0) {
      timerLabel.removeAll();
      timerLabel.setLayout(new BorderLayout());
      int labelW = timerLabel.getWidth();
      int labelH = timerLabel.getHeight();
      int fontMin = 40, fontMax = 400;
      int fontSize = Math.min((int)(labelH * 0.8), (int)(labelW * 0.8));
      Font font = timerLabel.getFont();
      FontMetrics fm;
      String secStr = String.format("%02d", Math.abs(timeLeft) % 60);
      for (int fs = fontMax; fs >= fontMin; fs--) {
          font = font.deriveFont((float) fs);
          fm = timerLabel.getFontMetrics(font);
          int w = fm.stringWidth(secStr);
          int h = fm.getHeight();
          if (w < labelW * 0.9 && h < labelH * 0.9) {
              fontSize = fs;
              break;
          }
      }
      html = "<html><div style='text-align:center;'><span style=\"font-size:" + fontSize + "px;font-family:sans-serif;font-weight:bold;line-height:1;\">" + secStr + "</span></div></html>";
      timerLabel.setForeground(Color.YELLOW);
      timerLabel.setText(html);
      adaptFontToLabel(timerLabel);
      if (smallTimerLabel != null) {
          smallTimerLabel.setText(timerLabel.getText().replaceAll("<[^>]*>", ""));
      }
      // --- AGGIUNTA: aggiorna externalClockLabel su due righe ---
      if (externalClockLabel != null && timerLabel.isVisible() && externalIsTimer) {
          if (mostraTempoEfficaciMinistero) {
              int extW = externalClockLabel.getWidth();
              int extH = externalClockLabel.getHeight();
              int extFontEff = (int)(Math.min((int)(extH / 2.8), (int)(extW / 7)) * 1.15);
              int extFontMinuti = (int)(Math.min((int)(extH / 3.5), (int)(extW / 4.5)) * 1.15);
              int extFontMinuti1 = (int)(Math.min((int)(extH/1.5), (int)(extW/1.5)) * 1.15);
              int extFontSecondi = (int)(Math.min((int)(extH / 2), (int)(extW / 12)) * 1.15);
              String colorEff = "black";
              String timerStr = (timeLeft < 0 ? "" : "") + String.format(String.format("%02d", Math.abs(timeLeft) % 60));
              String html2 = "<html><div style='text-align:center;'>"
                  + "<span style=\"font-size:" + extFontMinuti1 + "px;font-family:SansSerif;font-weight:bold;line-height:1;letter-spacing:2px;color:" + colorTimer + ";\">" + timerStr + "</span>"
                  + "</div></html>";
              externalClockLabel.setText(html2);
              externalClockLabel.setForeground(new Color(0, 180, 220));
          } else {
              // Solo timer (una riga)
              int extW = externalClockLabel.getWidth();
              int extH = externalClockLabel.getHeight();
              int extFontMinuti = Math.min((int)(extH / 2.5), (int)(extW / 4.5));
              int extFontSecondi = Math.min((int)(extH / 1.2), (int)(extW));
              String html1 = "<html><div style='display:inline-flex;align-items:flex-end;justify-content:center;width:100%;height:100%;'>"
                  + "<span style=\"font-size:" + extFontSecondi + "px;font-family:sans-serif;font-weight:bold; line-height:1;\">" + String.format("%02d", Math.abs(timeLeft) % 60) + "</span></div></html>";
              externalClockLabel.setText(html1);
              externalClockLabel.setForeground(timeLeft < 0 ? Color.RED : new Color(0xffe066));
          }
      }
      // --- FINE AGGIUNTA ---
      return;
  } else {
        timerLabel.removeAll();
        timerLabel.setLayout(new BorderLayout());
        int labelW = timerLabel.getWidth();
        int labelH = timerLabel.getHeight();
        int minuti = Math.abs(timeLeft) / 60;
        int fontMinuti;
if (String.valueOf(minuti).length() >= 3) {
    // Se i minuti sono a 3 cifre o più, riduci il font
    fontMinuti = Math.min((int)(labelH * 0.55), (int)(labelW * 0.55));
} else {
    // Altrimenti usa il font normale
    fontMinuti = timeLeft >= 0 ? 
        Math.min((int)(labelH * 0.7), (int)(labelW * 0.7)) : 
        Math.min((int)(labelH * 0.5), (int)(labelW * 0.5));
}
      int fontSecondi;
if (String.valueOf(minuti).length() >= 3) {
    fontSecondi = Math.min((int)(labelH * 0.25), (int)(labelW * 0.25));
} else {
    fontSecondi = timeLeft >= 0 ? 
        Math.min((int)(labelH * 0.35), (int)(labelW * 0.35)) : 
        Math.min((int)(labelH * 0.45), (int)(labelW * 0.45));
}
      // Colore secondi: bianco lucido se timeLeft > 0, rosso se < 0
      String colorSecondi = (timeLeft < 0) ? "#ff0000" : "#ffffff";
      // AGGIUNTA: spazio non interrotto tra minuti e secondi
      html = "<html><div style='display:inline-flex;align-items:flex-end;justify-content:center;width:100%;height:100%;'>"
          + "<span style=\"font-size:" + fontMinuti + "px;font-family:sans-serif;font-weight:bold;line-height:1;\">" + (timeLeft < 0 ? "-" : "") + String.format("%02d", Math.abs(timeLeft) / 60) + "</span>"
          + "<span style=\"font-size:" + fontSecondi + "px;font-family:sans-serif;font-weight:bold;line-height:1;margin-left:12px;vertical-align:bottom;color:" + colorSecondi + ";\">:" + String.format("%02d", Math.abs(timeLeft) % 60) + "</span>"
          + "</div></html>";
      if (timeLeft < 0) {
          timerLabel.setForeground(Color.RED);
      } else {
          timerLabel.setForeground(Color.GREEN);
      }
      timerLabel.setText(html);
      adaptFontToLabel(timerLabel);
      if (smallTimerLabel != null) {
          smallTimerLabel.setText(timerLabel.getText().replaceAll("<[^>]*>", ""));
      }
    }
// ... existing code ...
  // LOGICA PER LABEL ESTERNA (secondo monitor) CON FONT ADATTIVO E PIU' GRANDE
  if (externalClockLabel != null && timerLabel.isVisible() && externalIsTimer) {
      Parte parte = null;
      if (currentParteIndex >= 0 && currentParteIndex < listModel.size()) {
          parte = listModel.get(currentParteIndex);
      }
      if (showEffTime && mostraTempoEfficaciMinistero) {
          // Due righe: sopra tempo effettivo (bianco), sotto timer (giallo/rosso)
          int labelW = externalClockLabel.getWidth();
          int labelH = externalClockLabel.getHeight();
          int fontEff = Math.min((int)(labelH / 3.5), (int)(labelW / 6.5));
          int fontMinuti = Math.min((int)(labelH / 4.5), (int)(labelW / 6.5));
          int fontSecondi = Math.min((int)(labelH / 10), (int)(labelW / 18));
          String tempoPrecedente1 = tempoEffettivoUltimaParte >= 0 ? Parte.formatTime((int)tempoEffettivoUltimaParte) : "00:00";
          String colorTimer1 = (timeLeft < 0) ? "#ff0000" : "#ffe066";
          String colorEff = "#00b4dc";
          String timerStr = (timeLeft < 0 ? "" : "") + String.format("%02d", Math.abs(timeLeft) / 60) + ":" + String.format("%02d", Math.abs(timeLeft) % 60);
          String html2 = "<html><div style='text-align:center;'>"
              + "<span style=\"font-size:" + fontMinuti + "px;font-family:SansSerif;font-weight:bold;line-height:1;letter-spacing:2px;color:" + colorTimer1 + ";\">" + timerStr + "</span>"
              + "</div></html>";
          externalClockLabel.setText(html2);
          externalClockLabel.setForeground(new Color(0, 180, 220));
      } else if (Math.abs(timeLeft) >= 60) {
          String minStr = String.format("%02d", Math.abs(timeLeft) / 60);
          String secStr = String.format("%02d", Math.abs(timeLeft) % 60);
          int labelW = externalClockLabel.getWidth();
          int labelH = externalClockLabel.getHeight();
          int fontMin = 60, fontMax = 600;
          int fontSizeMin = fontMax, fontSizeSec = fontMax/3;
          Font font = externalClockLabel.getFont();
          FontMetrics fm;
          for (int fs = fontMax; fs >= fontMin; fs--) {
              font = font.deriveFont((float) fs);
              fm = externalClockLabel.getFontMetrics(font);
              int w = fm.stringWidth(minStr);
              int h = fm.getHeight();
              if (w < labelW * 0.7 && h < labelH * 0.8) {
                  fontSizeMin = fs;
                  break;
              }
          }
          for (int fs = fontMax/2; fs >= fontMin/2; fs--) {
              font = font.deriveFont((float) fs);
              fm = externalClockLabel.getFontMetrics(font);
              int w = fm.stringWidth(secStr);
              int h = fm.getHeight();
              if (w < labelW * 0.3 && h < labelH * 0.5) {
                  fontSizeSec = fs;
                  break;
              }
          }
          // Secondi: bianchi se in orario, rossi se in ritardo
          String colorSec = (timeLeft < 0) ? "#ff0000" : "#ffffff";
          String html1 = "<span style=\"font-size:" + fontSizeMin + "px;font-family:SansSerif;font-weight:bold;line-height:1;\">" + (timeLeft < 0 ? "-" : "") + minStr + "</span>" +
                        "<span style=\"font-size:" + fontSizeSec + "px;font-family:SansSerif;font-weight:bold;vertical-align:bottom;color:" + colorSec + ";\">:" + secStr + "</span>";
          externalClockLabel.setText(html1);
      } else if (Math.abs(timeLeft) >= 0) {
          // Solo secondi a schermo intero, sempre bianchi
          String secStr = String.format("%02d", Math.abs(timeLeft) % 60);
          int labelW = externalClockLabel.getWidth();
          int labelH = externalClockLabel.getHeight();
          int fontMin = 60, fontMax = 600;
          int fontSize = fontMax;
          Font font = externalClockLabel.getFont();
          FontMetrics fm;
          for (int fs = fontMax; fs >= fontMin; fs--) {
              font = font.deriveFont((float) fs);
              fm = externalClockLabel.getFontMetrics(font);
              int w = fm.stringWidth(secStr);
              int h = fm.getHeight();
              if (w < labelW * 0.9 && h < labelH * 0.9) {
                  fontSize = fs;
                  break;
              }
          }
          String html1 = "<html><div style='text-align:center;'><span style=\"font-size:" + fontSize + "px;font-family:SansSerif;font-weight:bold;line-height:1;color:#FF0000;\">" + "-" +secStr + "</span></div></html>";
          externalClockLabel.setText(html1);
      }
      externalClockLabel.setForeground(timerLabel.getForeground());
  }
  // --- AGGIUNTA: aggiorna la lista per la progress bar ---
  if (listaParti != null) listaParti.repaint();
}
private void mostraFinestraModifica(Parte parte, int index) {
  JTextField nomeField = new JTextField(parte.nome);
  JTextField minutiField = new JTextField("" + (parte.durataSecondi / 60));
  JTextField secondiField = new JTextField("" + (parte.durataSecondi % 60));
  Font bigFont = new Font("SansSerif", Font.BOLD, 22);
  minutiField.setFont(bigFont);
  secondiField.setFont(bigFont);
  minutiField.setHorizontalAlignment(JTextField.CENTER);
  secondiField.setHorizontalAlignment(JTextField.CENTER);
  minutiField.setPreferredSize(new Dimension(60, 40));
  secondiField.setPreferredSize(new Dimension(60, 40));
  JPanel panel = new JPanel(new GridBagLayout());
  GridBagConstraints gbc = new GridBagConstraints();
  gbc.insets = new Insets(6, 8, 6, 8);
  gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
  panel.add(new JLabel("Nome:"), gbc);
  gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
  panel.add(nomeField, gbc);
  // Label sopra i due campi
  gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
  JLabel minLabel = new JLabel("Minuti");
  minLabel.setHorizontalAlignment(SwingConstants.CENTER);
  panel.add(minLabel, gbc);
  gbc.gridx = 1;
  JLabel secLabel = new JLabel("Secondi");
  secLabel.setHorizontalAlignment(SwingConstants.CENTER);
  panel.add(secLabel, gbc);
  // Campi grandi affiancati
  gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
  panel.add(minutiField, gbc);
  gbc.gridx = 1;
  panel.add(secondiField, gbc);
  // --- AGGIUNTA: pulsante Elimina ---
  JButton eliminaButton = new JButton("Elimina Parte");
  eliminaButton.setForeground(Color.RED);
  eliminaButton.setPreferredSize(new Dimension(120, 32));
  JButton okButton = new JButton("OK");
  JButton annullaButton = new JButton("Annulla");
  okButton.setPreferredSize(new Dimension(90, 32));
  annullaButton.setPreferredSize(new Dimension(90, 32));
  JPanel buttonPanel = new JPanel(new BorderLayout());
  JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
  rightPanel.add(okButton);
  rightPanel.add(annullaButton);
  buttonPanel.add(eliminaButton, BorderLayout.WEST);
  buttonPanel.add(rightPanel, BorderLayout.EAST);
  buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
  JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
  mainPanel.add(panel, BorderLayout.CENTER);
  mainPanel.add(buttonPanel, BorderLayout.SOUTH);
  JDialog dialog = new JDialog(frame, "Modifica Parte", true);
  dialog.setContentPane(mainPanel);
  dialog.setMinimumSize(new Dimension(420, 240));
  dialog.pack();
  dialog.setLocationRelativeTo(frame);
  eliminaButton.addActionListener(e -> {
      int res = JOptionPane.showConfirmDialog(dialog, "Sei sicuro di voler eliminare questa parte?", "Conferma eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (res == JOptionPane.YES_OPTION) {
          listModel.remove(index);
          dialog.dispose();
      }
  });
  okButton.addActionListener(e -> {
      String nuovoNome = nomeField.getText().trim();
      try {
          int minuti = Integer.parseInt(minutiField.getText().trim());
          int secondi = Integer.parseInt(secondiField.getText().trim());
          if (!nuovoNome.isEmpty() && secondi >= 0 && secondi < 60) {
              int nuovaDurata = minuti * 60 + secondi;
              int vecchiaDurata = parte.durataSecondi;
              parte.nome = nuovoNome;
              parte.durataSecondi = nuovaDurata;
              listModel.setElementAt(parte, index);
              // Se la parte modificata è quella in corso e il timer è attivo, aggiorna timeLeft
              if (index == currentParteIndex && swingTimer != null && swingTimer.isRunning()) {
                  int differenza = vecchiaDurata - nuovaDurata;
                  timeLeft = timeLeft - differenza;
                  // Non permettere che il timer vada oltre la nuova durata (se già scaduto resta negativo)
                  if (timeLeft > nuovaDurata) timeLeft = nuovaDurata;
                  updateTimerLabel();
              }
              listaParti.repaint();
              dialog.dispose();
          }
      } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(dialog, "Inserisci valori validi per minuti e secondi.", "Errore", JOptionPane.ERROR_MESSAGE);
      }
  });
  annullaButton.addActionListener(e -> dialog.dispose());
  dialog.setVisible(true);
}
private void chiediNumeriCantici() {
    String iniziale = chiediNumeroCanticoObbligatorio("Numero Cantico Iniziale:");
    String centrale = chiediNumeroCanticoObbligatorio("Numero Cantico Centrale:");
    String finale = chiediNumeroCanticoObbligatorio("Numero Cantico Finale:");
    if (iniziale.isEmpty()) iniziale = "0";
    if (centrale.isEmpty()) centrale = "0";
    if (finale.isEmpty()) finale = "0";
    if (listModel.size() > 1)
        listModel.setElementAt(new Parte("CANTICO INIZIALE N. " + iniziale, 5 * 60), 1);
    if (listModel.size() > 13)
        listModel.setElementAt(new Parte("CANTICO CENTRALE N. " + centrale, 5 * 60), 13);
    // Rimuovo eventuali duplicati di CANTICO FINALE (tranne quello all'indice 21)
    for (int i = listModel.size() - 1; i >= 0; i--) {
        if (i != 21 && listModel.get(i).nome.toUpperCase().contains("CANTICO FINALE")) {
            listModel.remove(i);
        }
    }
    if (listModel.size() > 21)
        listModel.setElementAt(new Parte("CANTICO FINALE N. " + finale, 5 * 60), 21);
}
private void chiediOrarioInizio() {
  boolean timerAttivo = swingTimer != null && swingTimer.isRunning() && !isPaused;
  if (timerAttivo) {
      int res = JOptionPane.showConfirmDialog(frame, "Se cambi l'orario di inizio il timer si fermerà e ripartirà da zero. Vuoi continuare?", "Attenzione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (res != JOptionPane.YES_OPTION) {
          return;
      }
      swingTimer.stop();
      isPaused = false;
      showClock();
  }
  String orario = JOptionPane.showInputDialog(frame, "Inserisci orario di inizio (HH:mm):", orarioInizioAdunanza.format(DateTimeFormatter.ofPattern("HH:mm")));
  if (orario != null && !orario.isEmpty()) {
      // Se l'utente inserisce solo due cifre (es. "13"), aggiungi ":00"
      if (orario.matches("^\\d{1,2}$")) {
          orario = orario + ":00";
      }
      try {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
          LocalTime nuovoOrario = LocalTime.parse(orario, formatter);
          orarioInizioAdunanza = nuovoOrario;
          // Reset timer e parte corrente
          currentParteIndex = 0;
          timeLeft = 0;
          // startContoAllaRovesciaIniziale(); // NON far partire subito il timer
      } catch (DateTimeParseException e) {
          JOptionPane.showMessageDialog(frame, "Formato orario non valido. Usa HH:mm (es. 19:00).", "Errore", JOptionPane.ERROR_MESSAGE);
      }
  }
  calcolaOrarioFineProgrammatoFisso();
}
private void caricaAdunanzaVitaCristianaEMinistero() {
    programmaImportato = false;

  listModel.clear();
  listModel.addElement(new Parte("INIZIO", 0));
  listModel.addElement(new Parte("CANTICO INIZIALE", 5 * 60));
  listModel.addElement(new Parte("COMMENTI INTRODUTTIVI", 60));
  listModel.addElement(new Parte("TESORI DELLA PAROLA DI DIO", 60 * 10));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("SCAVIAMO PER TROVARE GEMME SPIRITUALI", 60 * 10));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("LETTURA BIBLICA", 4 * 60));
  listModel.addElement(new Parte("CONSIGLI", 60));
  listModel.addElement(new Parte("EFFICACI NEL MINISTERO", 7 * 60));
  listModel.addElement(new Parte("CONSIGLI", 60));
  listModel.addElement(new Parte("EFFICACI NEL MINISTERO", 7 * 60));
  listModel.addElement(new Parte("CONSIGLI", 60));
  listModel.addElement(new Parte("CANTICO CENTRALE", 5 * 60));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("VITA CRISTIANA PARTE 1", 10  * 60));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("VITA CRISTIANA PARTE 2", 5 * 60));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("STUDIO BIBLICO DI CONGREGAZIONE", 30 * 60));
  listModel.addElement(new Parte("RIPASSO E PANORAMICA", 3 * 60));
  listModel.addElement(new Parte("CANTICO FINALE", 5 * 60));
  listModel.addElement(new Parte("FINE", 0));
  calcolaOrarioFineProgrammatoFisso();
}
private void calcolaOrarioFineProgrammatoFisso() {
  int secondiTotali = 0;
  for (int i = 0; i < listModel.size(); i++) {
      secondiTotali += listModel.get(i).durataSecondi;
  }
  if (orarioInizioAdunanza != null) {
    orarioFineProgrammatoFisso = orarioInizioAdunanza.plusSeconds(secondiTotali);
  } else {
    orarioFineProgrammatoFisso = null;
  }
}
private void caricaAdunanzaVisitaSorvegliante() {
    programmaImportato = false;

  listModel.clear();
  listModel.addElement(new Parte("INIZIO", 0));
  listModel.addElement(new Parte("CANTICO INIZIALE", 5 * 60));
  listModel.addElement(new Parte("COMMENTI INTRODUTTIVI", 1 * 60));
  listModel.addElement(new Parte("TESORI DELLA PAROLA DI DIO", 10 * 60));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("SCAVIAMO PER TROVARE GEMME SPIRITUALI", 10 * 60));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("LETTURA BIBLICA", 4 * 60));
  listModel.addElement(new Parte("CONSIGLI", 60));
  listModel.addElement(new Parte("EFFICACI NEL MINISTERO", 7 * 60));
  listModel.addElement(new Parte("CONSIGLI", 60));
  listModel.addElement(new Parte("EFFICACI NEL MINISTERO", 7 * 60));
  listModel.addElement(new Parte("CONSIGLI", 60));
  listModel.addElement(new Parte("CANTICO CENTRALE", 5 * 60));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("VITA CRISTIANA PARTE 1", 10 * 60));
  listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
  listModel.addElement(new Parte("VITA CRISTIANA PARTE 2", 5 * 60));
  listModel.addElement(new Parte("RIPASSO E PANORAMICA", 3 * 60));
  listModel.addElement(new Parte("DISCORSO SORVEGLIANTE", 30 * 60));
  listModel.addElement(new Parte("CANTICO FINALE", 5 * 60));
  listModel.addElement(new Parte("FINE", 0));
  calcolaOrarioFineProgrammatoFisso();
}
private void caricaAdunanzaPubblica() {
  listModel.clear();
  listModel.addElement(new Parte("INIZIO", 0));
  listModel.addElement(new Parte("CANTICO INIZIALE", 5 * 60));
  listModel.addElement(new Parte("INTRO", 1 * 60));
  listModel.addElement(new Parte("DISCORSO PUBBLICO", 30 * 60));
  listModel.addElement(new Parte("RINGRAZIAMENTI E INTRODUZIONE TDG", 30));
  listModel.addElement(new Parte("CANTICO TG", 5 * 60));
  listModel.addElement(new Parte("STUDIO TORRE DI GUARDIA", 60 * 60));
  listModel.addElement(new Parte("CANTICO FINALE", 5 * 60));
  listModel.addElement(new Parte("FINE", 0));
  calcolaOrarioFineProgrammatoFisso();
}
private void caricaAdunanzaStrana() {
  listModel.clear();
  listModel.addElement(new Parte("INIZIO", 0));
  listModel.addElement(new Parte("DISCORSO PUBBLICO", 1 * 01));
  listModel.addElement(new Parte("STUDIO TORRE DI GUARDIA", 60 * 60));
  listModel.addElement(new Parte("FINE", 0));
  calcolaOrarioFineProgrammatoFisso();
}



private void caricaAdunanzaPubblicaSorvegliante() {
	listModel.clear();
  listModel.addElement(new Parte("INIZIO", 0));
  listModel.addElement(new Parte("CANTICO INIZIALE", 5 * 60));
  listModel.addElement(new Parte("INTRO", 60));
  listModel.addElement(new Parte("DISCORSO PUBBLICO SORVEGLIANTE", 30 * 60));
  listModel.addElement(new Parte("RINGRAZIAMENTI E INTRODUZIONE TDG", 60));
  listModel.addElement(new Parte("CANTICO TG", 5 * 60));
  listModel.addElement(new Parte("STUDIO TORRE DI GUARDIA", 30 * 60));
  listModel.addElement(new Parte("DISCORSO 2 SORVEGLIANTE", 30 * 60));
  listModel.addElement(new Parte("CANTICO FINALE", 5 * 60));
  listModel.addElement(new Parte("FINE", 0));
  calcolaOrarioFineProgrammatoFisso();
}
private void modificaSoloNumeroCantico(Parte parte, int index) {
    // Estrae numero attuale DOPO la parola Cantico, se presente
    String nome = parte.nome;
    String nomeUpper = nome.toUpperCase();
    int idx = nomeUpper.indexOf("CANTICO");
    String numeroAttuale = "";
    if (idx != -1) {
        String after = nome.substring(idx + "CANTICO".length());
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(after);
        if (m.find()) numeroAttuale = m.group(1);
    }
    JTextField numeroField = new JTextField(numeroAttuale);
    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Numero Cantico:"));
    panel.add(numeroField);
    int result = JOptionPane.showConfirmDialog(frame, panel, "Modifica Cantico", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
        String nuovoNumero = numeroField.getText().trim();
        if (!nuovoNumero.isEmpty() && nuovoNumero.matches("\\d+")) {
            // Aggiorna SOLO il numero dopo la parola Cantico
            String nuovoNome = nome;
            if (idx != -1) {
                String before = nome.substring(0, idx + "CANTICO".length());
                String after = nome.substring(idx + "CANTICO".length());
                after = after.replaceFirst("\\d+", nuovoNumero); // Sostituisce solo la prima sequenza di cifre dopo Cantico
                nuovoNome = before + after;
            }
            Parte nuovaParte = new Parte(nuovoNome, parte.durataSecondi, parte.isIntro);
            listModel.setElementAt(nuovaParte, index);
            if (index == currentParteIndex) {
                updateTimerLabel();
            }
            listaParti.setSelectedIndex(index);
            listaParti.repaint();
        } else {
            JOptionPane.showMessageDialog(frame, "Inserisci un numero valido.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
private void chiediNumeriCanticiPubblica() {
  String iniziale = chiediNumeroCanticoObbligatorio("Numero Cantico Iniziale:");
  String tg = chiediNumeroCanticoObbligatorio("Numero Cantico TG:");
  String finale = chiediNumeroCanticoObbligatorio("Numero Cantico Finale:");
  if (iniziale.isEmpty()) iniziale = "0";
  if (tg.isEmpty()) tg = "0";
  if (finale.isEmpty()) finale = "0";
  if (!iniziale.isEmpty() && listModel.size() > 1)
      listModel.setElementAt(new Parte("CANTICO INIZIALE N. " + iniziale, 5 * 60), 1);
  if (!tg.isEmpty() && listModel.size() > 5)
      listModel.setElementAt(new Parte("CANTICO TG N. " + tg, 5 * 60), 5);
  if (!finale.isEmpty() && listModel.size() > 7)
      listModel.setElementAt(new Parte("CANTICO FINALE N. " + finale, 5 * 60), 7);
}
private void chiediNumeriCanticiPubblicaSorvegliante() {
  String iniziale = chiediNumeroCanticoObbligatorio("Numero Cantico Iniziale:");
  String tg = chiediNumeroCanticoObbligatorio("Numero Cantico TG:");
  String finale = chiediNumeroCanticoObbligatorio("Numero Cantico Finale:");
  if (iniziale.isEmpty()) iniziale = "0";
  if (tg.isEmpty()) tg = "0";
  if (finale.isEmpty()) finale = "0";
  if (!iniziale.isEmpty() && listModel.size() > 1)
      listModel.setElementAt(new Parte("CANTICO INIZIALE N. " + iniziale, 5 * 60), 1);
  if (!tg.isEmpty() && listModel.size() > 5)
      listModel.setElementAt(new Parte("CANTICO TG N. " + tg, 5 * 60), 5);
  if (!finale.isEmpty() && listModel.size() > 8)
      listModel.setElementAt(new Parte("CANTICO FINALE N. " + finale, 5 * 60), 8);
}

private void updateStatusLabel() {
  if (listModel.isEmpty() || currentParteIndex < 0 || currentParteIndex >= listModel.size()) {
      statusLabel.setText(" ");
        return;
    }
    // NON mostrare nulla se la parte corrente è un timer manuale
    Parte parte = listModel.get(currentParteIndex);
    if (parte.nome.equalsIgnoreCase("TIMER MANUALE")) {
        statusLabel.setText(" ");
        statusLabel.setBackground(Color.BLACK);
        return;
    }
  // Calcola il tempo previsto per la parte corrente
  int secondiPrevisti = 0;
  for (int i = 0; i < currentParteIndex; i++) {
      secondiPrevisti += listModel.get(i).durataSecondi;
  }
  LocalTime orarioPrevisto = orarioInizioAdunanza.plusSeconds(secondiPrevisti);
  LocalTime oraAttuale = LocalTime.now();
  Duration diff = Duration.between(orarioPrevisto, oraAttuale);
  long sec = diff.getSeconds();
  String testo;
  if (sec < 0) {
      testo = String.format("In anticipo di %d:%02d", Math.abs(sec) / 60, Math.abs(sec) % 60);
      statusLabel.setBackground(new Color(0, 100, 0)); // verde scuro
  } else if (sec > 0) {
      testo = String.format("In ritardo di %d:%02d", sec / 60, sec % 60);
      statusLabel.setBackground(new Color(120, 0, 0)); // rosso scuro
  } else {
      testo = "In orario";
      statusLabel.setBackground(new Color(30, 30, 30));
  }
  statusLabel.setText(testo);
}
private void updateClockLabel() {
    if (testoPersonalizzatoClock != null) {
        clockLabel.setText(testoPersonalizzatoClock);
        adaptFontToLabel(clockLabel);
        if (externalClockLabel != null && clockLabel.isVisible() && !externalIsTimer) {
            externalClockLabel.setText(testoPersonalizzatoClock);
            externalClockLabel.setForeground(clockLabel.getForeground());
            updateExternalClockLabelFont();
        }
        return;
    }
    java.time.LocalTime now = java.time.LocalTime.now();
    clockLabel.setText(now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
    adaptFontToLabel(clockLabel);
    // Aggiorna la label esterna solo se non siamo in modalità timer
    if (externalClockLabel != null && clockLabel.isVisible() && !externalIsTimer) {
        externalClockLabel.setText(clockLabel.getText());
        externalClockLabel.setForeground(clockLabel.getForeground());
        updateExternalClockLabelFont();
    }
}
// Metodo per adattare il font alla label
private void adaptFontToLabel(JLabel label) {
  String text = label.getText().replaceAll("<[^>]*>", ""); // Rimuove eventuali tag HTML
  if (text.isEmpty()) return;
  int labelWidth = label.getWidth();
  int labelHeight = label.getHeight();
  if (labelWidth <= 0 || labelHeight <= 0) return;
  int fontSize = 40; // Parto da un font più piccolo
  Font font = label.getFont();
  FontMetrics fm;
  // Margine di sicurezza del 10%
  int safeWidth = (int)(labelWidth * 0.9);
  int safeHeight = (int)(labelHeight * 0.9);
  do {
      font = font.deriveFont((float) fontSize);
      fm = label.getFontMetrics(font);
      int textWidth = fm.stringWidth(text);
      int textHeight = fm.getHeight();
      if (textWidth > safeWidth || textHeight > safeHeight) {
          fontSize--;
          break;
      }
      fontSize++;
  } while (fontSize < 120); // Limite massimo più basso
  if (fontSize < 10) fontSize = 10;
  label.setFont(label.getFont().deriveFont((float) fontSize));
}
// Aggiorna il font quando la label viene ridimensionata
private void addResizeFontListener(JLabel label) {
  label.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent e) {
          adaptFontToLabel(label);
      }
  });
}
// Quando vuoi tornare all'orologio digitale (ad esempio dopo la fine del programma)
private void showClock() {
  cardLayout.show(centerPanel, "clock");
  timerLabel.setVisible(false);
  clockLabel.setVisible(true);
  // Aggiorna anche la label esterna e segnala che ora mostra l'orologio
  if (externalClockLabel != null) {
      externalClockLabel.setText(clockLabel.getText());
      externalClockLabel.setForeground(clockLabel.getForeground());
      externalIsTimer = false;
  }
  if (externalClockSwingTimer != null) externalClockSwingTimer.start();
  aggiornaLampeggioPulsanti();
}
private static class Parte {
  String nome;
  int durataSecondi;
  boolean isIntro;
  Long tempoEffettivo = null; // tempo effettivo in secondi, null se non ancora conclusa
  public Parte(String nome, int durataSecondi) {
      this(nome, durataSecondi, false);
  }
  public Parte(String nome, int durataSecondi, boolean isIntro) {
      this.nome = nome;
      this.durataSecondi = durataSecondi;
      this.isIntro = isIntro;
  }
  @Override
  public String toString() {
      if (nome.toUpperCase().contains("CANTICO")) {
          // Nascondi il numero se è 00
          String num = "";
          java.util.regex.Matcher m = java.util.regex.Pattern.compile("N\\.\\s*(\\d+)").matcher(nome);
          if (m.find()) num = m.group(1);
          if ("00".equals(num)) {
              return nome.replaceAll("N\\.\\s*00", "").replaceAll("\s+", " ").trim();
          } else {
              return nome;
          }
      } else {
          return nome + " - " + formatTime(durataSecondi);
      }
  }
  public static String formatTime(int totalSeconds) {
      int minutes = Math.abs(totalSeconds) / 60;
      int seconds = Math.abs(totalSeconds) % 60;
      String time = String.format("%02d:%02d", minutes, seconds);
      return totalSeconds < 0 ? "-" + time : time;
  }
}
// Classe interna per l'orologio analogico
class AnalogClockPanel extends JPanel {
  public AnalogClockPanel() {
      Timer t = new Timer(1000, e -> repaint());
      t.start();
  }
  @Override
  protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      int w = getWidth(), h = getHeight();
      int size = Math.min(w, h) - 40;
      int cx = w / 2, cy = h / 2;
      g2.setColor(Color.BLACK);
      g2.fillOval(cx - size/2, cy - size/2, size, size);
      g2.setColor(Color.WHITE);
      g2.drawOval(cx - size/2, cy - size/2, size, size);
      LocalTime now = LocalTime.now();
      double sec = now.getSecond() * Math.PI / 30;
      double min = (now.getMinute() + now.getSecond()/60.0) * Math.PI / 30;
      double hour = (now.getHour()%12 + now.getMinute()/60.0) * Math.PI / 6;
      // Lancetta ore
      g2.setStroke(new BasicStroke(6));
      g2.setColor(Color.WHITE);
      g2.drawLine(cx, cy, (int)(cx + Math.sin(hour) * size/3), (int)(cy - Math.cos(hour) * size/3));
      // Lancetta minuti
      g2.setStroke(new BasicStroke(4));
      g2.setColor(Color.YELLOW);
      g2.drawLine(cx, cy, (int)(cx + Math.sin(min) * size/2.2), (int)(cy - Math.cos(min) * size/2.2));
      // Lancetta secondi
      g2.setStroke(new BasicStroke(2));
      g2.setColor(Color.RED);
      g2.drawLine(cx, cy, (int)(cx + Math.sin(sec) * size/2.1), (int)(cy - Math.cos(sec) * size/2.1));
  }
}
private void chooseMusicFolder() {
  JFileChooser chooser = new JFileChooser();
  chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
      currentFolder = chooser.getSelectedFile();
      saveMusicFolderToConfig(currentFolder);
      File[] files = currentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
      mp3Files = files != null ? files : new File[0];
      currentSongIndex = 0;
      updateSongLabel();
  }
}
private void saveMusicFolderToConfig(File folder) {
  try (FileWriter fw = new FileWriter(MUSIC_FOLDER_CONFIG)) {
      fw.write(folder.getAbsolutePath());
  } catch (IOException e) {
      // Ignora errori di scrittura
  }
}
private void loadMusicFolderFromConfig() {
  File config = new File(MUSIC_FOLDER_CONFIG);
  if (config.exists()) {
      try (BufferedReader br = new BufferedReader(new FileReader(config))) {
          String path = br.readLine();
          if (path != null && !path.isEmpty()) {
              File folder = new File(path);
              if (folder.exists() && folder.isDirectory()) {
                  currentFolder = folder;
                  File[] files = currentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
                  mp3Files = files != null ? files : new File[0];
                  currentSongIndex = 0;
                  updateSongLabel();
              }
          }
      } catch (IOException e) {
          // Ignora errori di lettura
      }
  }
}
private void playSelectedSong() {
  if (mp3Files == null || mp3Files.length == 0) return;
  playSong(currentSongIndex);
}
private void playSong(int idx) {
    stopSong();
    if (mp3Files == null || idx < 0 || idx >= mp3Files.length) return;
    currentSongIndex = idx;
    updateSongLabel();
    stopRequested = false;
    playerThread = new Thread(() -> {
        try (FileInputStream fis = new FileInputStream(mp3Files[idx])) {
            mp3Player = new Player(fis);
            while (!stopRequested && !Thread.currentThread().isInterrupted()) {
                if (!mp3Player.play(1)) break;
            }
            mp3Player.close();
            mp3Player = null;
            if (!stopRequested) {
                SwingUtilities.invokeLater(() -> {
                    playNextSong();
                    if (isPlaying) playSelectedSong();
                });
            }
        } catch (Exception ex) {
            if (!stopRequested) {
                JOptionPane.showMessageDialog(frame, "Errore nella riproduzione: " + ex.getMessage());
            }
        }
    });
    playerThread.start();
}
private void stopSong() {
    stopRequested = true;
    if (mp3Player != null) {
        mp3Player.close();
        mp3Player = null;
    }
    if (playerThread != null && playerThread.isAlive()) {
        try {
            playerThread.join(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        playerThread = null;
    }
}
private void playNextSong() {
  if (mp3Files == null || mp3Files.length == 0) return;
  int next = (currentSongIndex + 1) % mp3Files.length;
  currentSongIndex = next;
  updateSongLabel();
}
private void playPrevSong() {
  if (mp3Files == null || mp3Files.length == 0) return;
  int prev = (currentSongIndex - 1 + mp3Files.length) % mp3Files.length;
  currentSongIndex = prev;
  updateSongLabel();
}
public static void main(String[] args) {
    SwingUtilities.invokeLater(TimerApp::new);
}
// Aggiungo la versione per JButton del metodo addResizeFontListener
private void addResizeFontListener(JButton button) {
  button.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent e) {
          adaptFontToButton(button);
      }
  });
}
private void adaptFontToButton(JButton button) {
  String text = button.getText();
  if (text.isEmpty()) return;
  int btnWidth = button.getWidth();
  int btnHeight = button.getHeight();
  if (btnWidth <= 0 || btnHeight <= 0) return;
  int fontSize = 18; // Parto da un font più piccolo
  Font font = button.getFont();
  FontMetrics fm;
  int safeWidth = (int)(btnWidth * 0.85);
  int safeHeight = (int)(btnHeight * 0.7);
  do {
      font = font.deriveFont((float) fontSize);
      fm = button.getFontMetrics(font);
      int textWidth = fm.stringWidth(text);
      int textHeight = fm.getHeight();
      if (textWidth > safeWidth || textHeight > safeHeight) {
          fontSize--;
          break;
      }
      fontSize++;
  } while (fontSize < 60);
  if (fontSize < 10) fontSize = 10;
  button.setFont(button.getFont().deriveFont((float) fontSize));
}
private void updateTopClockAndDate() {
  topClockLabel.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
  topDateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
}
// Metodo per aggiornare l'altezza delle celle della lista in modo dinamico
private void aggiornaAltezzaCelleLista() {
  int n = listModel.getSize();
  if (n == 0) return;
  int altezzaDisponibile = listaScroll.getViewport().getHeight();
  int cellHeight = Math.max(45, Math.min(60, altezzaDisponibile / n));
  listaParti.setFixedCellHeight(cellHeight);
}



// Chiede un numero di cantico obbligatorio e solo numerico
private String chiediNumeroCanticoObbligatorio(String messaggio) {
  String input;
  do {
      input = JOptionPane.showInputDialog(frame, messaggio, "Inserisci Numero", JOptionPane.PLAIN_MESSAGE);
      if (input == null) {
          // Se annulla o chiude, restituisci stringa vuota
          return "";
      } else if (!input.matches("\\d+")) {
          JOptionPane.showMessageDialog(frame, "Inserisci solo numeri per il cantico.", "Errore", JOptionPane.ERROR_MESSAGE);
          input = null;
      }
  } while (input == null);
  return input;
}
// Metodo per mostrare la finestra delle impostazioni
private void mostraFinestraImpostazioni() {
  JDialog settingsDialog = new JDialog(frame, "Impostazioni", true);
  settingsDialog.setSize(460, 600);
  settingsDialog.setPreferredSize(new Dimension(900, 600)); // aumenta la larghezza
  settingsDialog.pack();
  settingsDialog.setLayout(new BorderLayout());
  JTabbedPane tabbedPane = new JTabbedPane();
  tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
  // --- SEZIONE GENERALI ---
  JPanel generaliPanel = new JPanel();
  generaliPanel.setLayout(new BoxLayout(generaliPanel, BoxLayout.Y_AXIS));
  generaliPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
  JCheckBox fuoriTempoCheck = new JCheckBox("Fuori tempo lampeggiante", fuoriTempoLampeggiante);
  fuoriTempoCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  generaliPanel.add(fuoriTempoCheck);
  generaliPanel.add(Box.createVerticalStrut(12));
  // Checkbox disabilitata per messaggi oratore
  JCheckBox disabilitaMessaggiCheck = new JCheckBox("Disabilita messaggi oratore", disabilitaMessaggiOratore);
  disabilitaMessaggiCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  generaliPanel.add(disabilitaMessaggiCheck);
  generaliPanel.add(Box.createVerticalStrut(12));
  // Nuova checkbox per assorbimento ritardo
  JCheckBox assorbiRitardoCheck = new JCheckBox("Assorbi ritardo automaticamente", assorbiRitardoAutomatico);
  assorbiRitardoCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  generaliPanel.add(assorbiRitardoCheck);
  generaliPanel.add(Box.createVerticalStrut(12));
  JCheckBox mostraOrariCheck = new JCheckBox("Mostra orari inizio/fine parti nella lista", mostraOrariParti);
  mostraOrariCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  generaliPanel.add(mostraOrariCheck);
  generaliPanel.add(Box.createVerticalStrut(12));
  JCheckBox mostraTempoEfficaciCheck = new JCheckBox("Mostra durata parti a tempo", mostraTempoEfficaciMinistero);
  mostraTempoEfficaciCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  generaliPanel.add(mostraTempoEfficaciCheck);
  generaliPanel.add(Box.createVerticalStrut(12));
  JCheckBox mostraImportaWOLCheck = new JCheckBox("Mostra tasto Importa WOL (importazione automatica programma infrasettimanale)", mostraImportaWOL);
  mostraImportaWOLCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  generaliPanel.add(mostraImportaWOLCheck);
  generaliPanel.add(Box.createVerticalStrut(12));
JButton applicaGeneraliBtn = new JButton("Applica");
  applicaGeneraliBtn.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  // Dopo la creazione del pulsante
applicaGeneraliBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
applicaGeneraliBtn.setBackground(new Color(39, 174, 96)); // verde elegante
applicaGeneraliBtn.setForeground(Color.WHITE);
applicaGeneraliBtn.setFocusPainted(false);
applicaGeneraliBtn.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(34, 153, 84), 2, true),
    BorderFactory.createEmptyBorder(8, 24, 8, 24)
));
applicaGeneraliBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

  generaliPanel.add(applicaGeneraliBtn);
  applicaGeneraliBtn.addActionListener(e -> {
      fuoriTempoLampeggiante = fuoriTempoCheck.isSelected();
      disabilitaMessaggiOratore = disabilitaMessaggiCheck.isSelected();
      if (customMsgPanel != null) {
          customMsgPanel.setVisible(!disabilitaMessaggiOratore);
      }
      mostraOrariParti = mostraOrariCheck.isSelected();
      assorbiRitardoAutomatico = assorbiRitardoCheck.isSelected();
      mostraTempoEfficaciMinistero = mostraTempoEfficaciCheck.isSelected();
      mostraImportaWOL = mostraImportaWOLCheck.isSelected();

      salvaImpostazioni();
      if (comboAndArrowPanel != null && importaWOLPanel != null) {
        if (mostraImportaWOL) {
            comboAndArrowPanel.add(importaWOLPanel);
        } else {
            comboAndArrowPanel.remove(importaWOLPanel);
        }
        comboAndArrowPanel.revalidate();
        comboAndArrowPanel.repaint();
    }
      // Richiama il renderer originale per ripristinare tutti gli stili
      applicaRendererPillola();
      JOptionPane.showMessageDialog(settingsDialog, "Impostazioni generali applicate.");
  });
  tabbedPane.addTab("Generali", generaliPanel);
  // --- SEZIONE MONITOR ---
  JPanel monitorPanel = new JPanel();
  monitorPanel.setLayout(new BoxLayout(monitorPanel, BoxLayout.Y_AXIS));
  monitorPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
  JCheckBox abilitaProiezioneCheck = new JCheckBox("Abilita proiezione su secondo schermo", proiezioneAbilitata);
  abilitaProiezioneCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  monitorPanel.add(abilitaProiezioneCheck);
  monitorPanel.add(Box.createVerticalStrut(12));
  GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
  GraphicsDevice[] screens = ge.getScreenDevices();
  String[] monitorNames = new String[screens.length];
  for (int i = 0; i < screens.length; i++) {
      String id = screens[i].getIDstring();
      monitorNames[i] = id != null ? id : ("Monitor " + (i + 1));
  }

  JComboBox<String> monitorCombo = new JComboBox<>(monitorNames);
// ... existing code ...
monitorCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
monitorCombo.setForeground(Color.WHITE);
monitorCombo.setBackground(new Color(28, 28, 36));
monitorCombo.setMinimumSize(new Dimension(200, 60));
monitorCombo.setPreferredSize(new Dimension(320, 60));
monitorCombo.setMaximumSize(monitorCombo.getPreferredSize());
monitorCombo.setBorder(new RoundedBorder(14, new Color(0,180,180)));
monitorCombo.setFocusable(false);
monitorCombo.setMaximumRowCount(6);
monitorCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
monitorCombo.setToolTipText("Seleziona il monitor su cui proiettare l'orologio/programma");
// Custom UI per freccia bianca e hover
monitorCombo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                int[] x = {w/2-6, w/2, w/2+6};
                int[] y = {h/2-2, h/2+6, h/2-2};
                g2.fillPolygon(x, y, 3);
                g2.dispose();
            }
        };
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }
});

monitorCombo.setRenderer(new DefaultListCellRenderer() {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setToolTipText(value != null ? value.toString() : "");
        return label;
    }
});

  monitorCombo.setSelectedIndex(Math.max(0, Math.min(monitorSelezionato, screens.length - 1)));
  monitorCombo.setAlignmentX(JComponent.LEFT_ALIGNMENT);

  
  monitorPanel.add(new JLabel("Scegli monitor:"));
  monitorPanel.add(monitorCombo);
  monitorPanel.add(Box.createVerticalStrut(12));
  JButton applicaMonitorBtn = new JButton("Applica");
  applicaMonitorBtn.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  // Dopo la creazione del pulsante
applicaMonitorBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
applicaMonitorBtn.setBackground(new Color(39, 174, 96)); // verde elegante
applicaMonitorBtn.setForeground(Color.WHITE);
applicaMonitorBtn.setFocusPainted(false);
applicaMonitorBtn.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(34, 153, 84), 2, true),
    BorderFactory.createEmptyBorder(8, 24, 8, 24)
));
applicaMonitorBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
// Se vuoi aggiungere un'icona (opzionale, serve una icona nella cartella res)
// applicaMonitorBtn.setIcon(new ImageIcon("res/icon.png"));
  monitorPanel.add(applicaMonitorBtn);
  applicaMonitorBtn.addActionListener(e -> {
      proiezioneAbilitata = abilitaProiezioneCheck.isSelected();
      monitorSelezionato = monitorCombo.getSelectedIndex();
      salvaImpostazioni();
      if (proiezioneAbilitata) {
          mostraOrologioSecondoMonitor();
      } else {
          if (externalClockFrame != null) externalClockFrame.setVisible(false);
      }
      JOptionPane.showMessageDialog(settingsDialog, "Impostazioni monitor applicate.");
  });


  tabbedPane.addTab("Monitor", monitorPanel);

  
  // --- SEZIONE INFO ---
JPanel infoPanel = new JPanel();
infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
infoPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

// Titolo (bold, grande)
JLabel appNameLabel = new JLabel("KH Timer");
appNameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
appNameLabel.setForeground(Color.WHITE);
appNameLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
infoPanel.add(appNameLabel);
infoPanel.add(Box.createVerticalStrut(12));

// Versione
JLabel versionLabel = new JLabel("Versione: " + VERSION);
versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
versionLabel.setForeground(Color.LIGHT_GRAY);
versionLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
infoPanel.add(versionLabel);
infoPanel.add(Box.createVerticalStrut(8));

// UUID
JLabel uuidLabel = new JLabel("UUID: f40a50bf-267d-4466-880b-e21c423caa75");
uuidLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
uuidLabel.setForeground(Color.LIGHT_GRAY);
uuidLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
infoPanel.add(uuidLabel);
infoPanel.add(Box.createVerticalStrut(8));

// Autore
JLabel authorLabel = new JLabel("Autore: Diego Santone");
authorLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
authorLabel.setForeground(Color.LIGHT_GRAY);
authorLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
infoPanel.add(authorLabel);
infoPanel.add(Box.createVerticalStrut(8));

// Email
JLabel authorEmailLabel = new JLabel("Contatti: diesse912@gmail.com");
authorEmailLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
authorEmailLabel.setForeground(Color.LIGHT_GRAY);
authorEmailLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
infoPanel.add(authorEmailLabel);
infoPanel.add(Box.createVerticalStrut(12));

// Link GitHub
JLabel githubLabel = new JLabel("<html><span style='color:#00BFFF;'>GitHub: KH-Timer</span></html>");
githubLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
githubLabel.setForeground(new Color(0, 191, 255)); // DeepSkyBlue
githubLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
githubLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
githubLabel.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/diegoisme-dev/KH-Timer"));
        } catch (Exception ignored) {}
    }
});
infoPanel.add(githubLabel);
infoPanel.add(Box.createVerticalStrut(8));

// Link Website
JLabel websiteLabel = new JLabel("<html><span style='color:#00BFFF;'>Sito web: KH Timer</span></html>");
websiteLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
websiteLabel.setForeground(new Color(0, 191, 255));
websiteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
websiteLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
websiteLabel.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new URI("https://diegoisme-dev.github.io/KH-Timer/"));
        } catch (Exception ignored) {}
    }
});
infoPanel.add(websiteLabel);


tabbedPane.addTab("Info", infoPanel);
  // --- SEZIONE PLAYER ---
  JPanel playerPanel = new JPanel();
  playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
  playerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

  JLabel folderLabel = new JLabel("Cartella attuale: " + (currentFolder != null ? currentFolder.getAbsolutePath() : "(nessuna)"));
  folderLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  folderLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
  folderLabel.setForeground(Color.LIGHT_GRAY);

  JButton scegliCartellaBtn = new JButton("Scegli cartella musica");
  scegliCartellaBtn.setAlignmentX(JComponent.LEFT_ALIGNMENT);
  scegliCartellaBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
  scegliCartellaBtn.setBackground(new Color(39, 174, 96)); // verde elegante
  scegliCartellaBtn.setForeground(Color.WHITE);
  scegliCartellaBtn.setFocusPainted(false);
  scegliCartellaBtn.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(34, 153, 84), 2, true),
      BorderFactory.createEmptyBorder(8, 24, 8, 24)
  ));
  scegliCartellaBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  // scegliCartellaBtn.setIcon(new ImageIcon("res/icon.png")); // opzionale

  scegliCartellaBtn.addActionListener(e -> {
      chooseMusicFolder();
      folderLabel.setText("Cartella attuale: " + (currentFolder != null ? currentFolder.getAbsolutePath() : "(nessuna)"));
  });

  playerPanel.add(folderLabel);
  playerPanel.add(Box.createVerticalStrut(12));
  playerPanel.add(scegliCartellaBtn);
  tabbedPane.addTab("Player", playerPanel);

  class RoundedButtonBorder extends javax.swing.border.AbstractBorder {
    private int radius;
    private Color color;
    public RoundedButtonBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(x+2, y+2, width-5, height-5, radius, radius);
        g2.dispose();
    }
}



// --- SEZIONE MEETING ---
JPanel meetingPanel = new JPanel();
meetingPanel.setLayout(new BoxLayout(meetingPanel, BoxLayout.Y_AXIS));
meetingPanel.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(new Color(0,180,180), 2, true),
    "Gestione orari adunanze",
    0, 2,
    new Font("SansSerif", Font.BOLD, 18),
    new Color(0,180,180)
));
meetingPanel.setBackground(new Color(24, 24, 24));
meetingPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

meetingPanel.add(Box.createVerticalStrut(10));
JLabel meetingLabel = new JLabel("Orari delle adunanze settimanali:");
meetingLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
meetingLabel.setForeground(new Color(0,180,180));
meetingLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
meetingPanel.add(meetingLabel);
meetingPanel.add(Box.createVerticalStrut(8));

DefaultListModel<MeetingSchedule> meetingListModel = new DefaultListModel<>();
for (MeetingSchedule ms : meetingSchedules) meetingListModel.addElement(ms);
JList<MeetingSchedule> meetingList = new JList<>(meetingListModel);
meetingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
meetingList.setFont(new Font("SansSerif", Font.BOLD, 18));
meetingList.setBackground(new Color(32, 32, 32));
meetingList.setForeground(new Color(0, 220, 0));

meetingList.setBorder(BorderFactory.createLineBorder(new Color(0,180,180), 1, true));
meetingList.setCellRenderer(new DefaultListCellRenderer() {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(isSelected ? Color.BLACK : new Color(0, 220, 0));
        label.setBackground(isSelected ? new Color(0, 220, 220) : new Color(32, 32, 32));
        label.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        return label;
    }
});
JScrollPane meetingScroll = new JScrollPane(meetingList);
meetingScroll.setPreferredSize(new Dimension(340, 120));
meetingScroll.setMaximumSize(new Dimension(340, 160));
meetingScroll.setAlignmentX(JComponent.CENTER_ALIGNMENT);
meetingScroll.setBorder(BorderFactory.createLineBorder(new Color(0,180,180), 1, true));
meetingPanel.add(meetingScroll);
meetingPanel.add(Box.createVerticalStrut(10));

JPanel btnPanel = new JPanel();

Dimension buttonSize = new Dimension(120, 44); // larghezza aumentata

JButton editMeetingBtn = new JButton("Modifica");
editMeetingBtn.setToolTipText("Modifica l'orario selezionato");
editMeetingBtn.setPreferredSize(buttonSize);
editMeetingBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
editMeetingBtn.setForeground(new Color(220, 180, 0));
editMeetingBtn.setBackground(Color.DARK_GRAY);
editMeetingBtn.setFocusPainted(false);
editMeetingBtn.setContentAreaFilled(false);
editMeetingBtn.setBorder(new RoundedBorder(18, new Color(220, 180, 0)));
editMeetingBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

JButton delMeetingBtn = new JButton("Elimina");
delMeetingBtn.setToolTipText("Elimina l'orario selezionato");
delMeetingBtn.setPreferredSize(buttonSize);
delMeetingBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
delMeetingBtn.setForeground(new Color(220, 0, 0));
delMeetingBtn.setBackground(Color.DARK_GRAY);
delMeetingBtn.setFocusPainted(false);
delMeetingBtn.setContentAreaFilled(false);
delMeetingBtn.setBorder(new RoundedBorder(18, new Color(220, 0, 0)));
delMeetingBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

JButton addMeetingBtn = new JButton("Aggiungi");
addMeetingBtn.setToolTipText("Aggiungi un nuovo orario adunanza");
addMeetingBtn.setPreferredSize(buttonSize);
addMeetingBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
addMeetingBtn.setForeground(new Color(0, 180, 0));
addMeetingBtn.setBackground(Color.DARK_GRAY);
addMeetingBtn.setFocusPainted(false);
addMeetingBtn.setContentAreaFilled(false);
addMeetingBtn.setBorder(new RoundedBorder(18, new Color(0, 180, 0)));
addMeetingBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));



btnPanel.add(addMeetingBtn);
btnPanel.add(editMeetingBtn);
btnPanel.add(delMeetingBtn);
btnPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
meetingPanel.add(btnPanel);
meetingPanel.add(Box.createVerticalStrut(10));

JSeparator sep = new JSeparator();
sep.setMaximumSize(new Dimension(340, 2));
meetingPanel.add(sep);
meetingPanel.add(Box.createVerticalStrut(10));

JButton applicaMeetingBtn = new JButton("Applica modifiche");
applicaMeetingBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
applicaMeetingBtn.setPreferredSize(new Dimension(160, 44)); // più largo per testo lungo
applicaMeetingBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
applicaMeetingBtn.setForeground(new Color(0, 180, 180)); // turchese per il testo
applicaMeetingBtn.setBackground(Color.DARK_GRAY); // sfondo neutro (non visibile)
applicaMeetingBtn.setFocusPainted(false);
applicaMeetingBtn.setContentAreaFilled(false); // no riempimento interno
applicaMeetingBtn.setBorder(new RoundedBorder(18, new Color(0, 180, 180))); // bordo turchese
applicaMeetingBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
applicaMeetingBtn.setToolTipText("Salva gli orari delle adunanze");

meetingPanel.add(applicaMeetingBtn);
meetingPanel.add(Box.createVerticalStrut(8));
JLabel notaLabel = new JLabel("Nota: riavvia l'app dopo aver inserito nuovi orari per applicare le modifiche.");
notaLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
notaLabel.setForeground(new Color(180, 180, 180));
notaLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
meetingPanel.add(notaLabel);
// Listener aggiungi
addMeetingBtn.addActionListener(e -> {
    JPanel inputPanel = new JPanel(new GridLayout(2,2,8,8));
    inputPanel.setBackground(new Color(32,32,32));
    inputPanel.add(new JLabel("Giorno della settimana:"));
    String[] giorni = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
    JComboBox<String> dayCombo = new JComboBox<>(giorni);
    inputPanel.add(dayCombo);
    inputPanel.add(new JLabel("Orario (HH:MM):"));
    JTextField timeField = new JTextField();
    inputPanel.add(timeField);
    int res = JOptionPane.showConfirmDialog(settingsDialog, inputPanel, "Nuovo orario adunanza", JOptionPane.OK_CANCEL_OPTION);
    if (res == JOptionPane.OK_OPTION) {
        try {
            int day = dayCombo.getSelectedIndex() + 1; // DayOfWeek.of(1)=Lunedì
            String timeText = timeField.getText().trim();
            int hour, min;
            if (timeText.contains(":")) {
                String[] hm = timeText.split(":");
                hour = Integer.parseInt(hm[0]);
                min = (hm.length > 1) ? Integer.parseInt(hm[1]) : 0;
        } else {
                hour = Integer.parseInt(timeText);
                min = 0;
            }
            MeetingSchedule ms = new MeetingSchedule(java.time.DayOfWeek.of(day), java.time.LocalTime.of(hour, min));
            meetingListModel.addElement(ms);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(settingsDialog, "Valori non validi.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
});

// Listener modifica
editMeetingBtn.addActionListener(e -> {
    int idx = meetingList.getSelectedIndex();
    if (idx < 0) return;
    MeetingSchedule ms = meetingListModel.get(idx);
    JPanel inputPanel = new JPanel(new GridLayout(2,2,8,8));
    inputPanel.setBackground(new Color(32,32,32));
    inputPanel.add(new JLabel("Giorno della settimana:"));
        String[] giorni = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
    JComboBox<String> dayCombo = new JComboBox<>(giorni);
    dayCombo.setSelectedIndex(ms.getDayOfWeek().getValue() - 1);
    inputPanel.add(dayCombo);
    inputPanel.add(new JLabel("Orario (HH:MM):"));
    JTextField timeField = new JTextField(String.format("%02d:%02d", ms.getTime().getHour(), ms.getTime().getMinute()));
    inputPanel.add(timeField);
    int res = JOptionPane.showConfirmDialog(settingsDialog, inputPanel, "Modifica orario adunanza", JOptionPane.OK_CANCEL_OPTION);
    if (res == JOptionPane.OK_OPTION) {
        try {
            int day = dayCombo.getSelectedIndex() + 1;
            String timeText = timeField.getText().trim();
            int hour, min;
            if (timeText.contains(":")) {
                String[] hm = timeText.split(":");
                hour = Integer.parseInt(hm[0]);
                min = (hm.length > 1) ? Integer.parseInt(hm[1]) : 0;
            } else {
                hour = Integer.parseInt(timeText);
                min = 0;
            }
            MeetingSchedule newMs = new MeetingSchedule(java.time.DayOfWeek.of(day), java.time.LocalTime.of(hour, min));
            meetingListModel.set(idx, newMs);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(settingsDialog, "Valori non validi.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
});
// Listener elimina
delMeetingBtn.addActionListener(e -> {
    int idx = meetingList.getSelectedIndex();
    if (idx >= 0) meetingListModel.remove(idx);
});
// Listener applica
applicaMeetingBtn.addActionListener(e -> {
  meetingSchedules.clear();
    for (int i = 0; i < meetingListModel.size(); i++) {
        meetingSchedules.add(meetingListModel.get(i));
    }
            saveMeetingSchedules();
    JOptionPane.showMessageDialog(settingsDialog, "Orari adunanze salvati.");
});
tabbedPane.addTab("Meeting", meetingPanel);

  // --- SEZIONE STILE (placeholder) ---
  JPanel stilePanel = new JPanel();
  stilePanel.setLayout(new BoxLayout(stilePanel, BoxLayout.Y_AXIS));
  stilePanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
  stilePanel.add(new JLabel("(Prossimamente opzioni di stile)", SwingConstants.CENTER));
  tabbedPane.addTab("Stile", stilePanel);
  settingsDialog.add(tabbedPane, BorderLayout.CENTER);
  JButton closeButton = new JButton("Chiudi finestra");
  
  closeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
  closeButton.setForeground(Color.WHITE);
  closeButton.setBackground(new Color(200, 40, 40));
  closeButton.setFocusPainted(false);
  closeButton.setContentAreaFilled(false);
  closeButton.setOpaque(true);
  closeButton.setBorder(new RoundedBorder(16, new Color(200, 40, 40)));
  closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  closeButton.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));


  closeButton.addActionListener(e -> {
      settingsDialog.dispose();
      impostazioniAperte = false;
  });

// --- SEZIONE COLLEGAMENTO REMOTO ---

JPanel remotePanel = new JPanel();
remotePanel.setLayout(new BoxLayout(remotePanel, BoxLayout.Y_AXIS));
remotePanel.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(0,180,180), 2, true),
    BorderFactory.createEmptyBorder(20, 24, 20, 24)
));
remotePanel.setBackground(new Color(24, 24, 32));

// Stato
JLabel statoLabel = new JLabel(remoteActive ? "🟢 Stato: ATTIVO" : "🔴 Stato: NON ATTIVO");
statoLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
statoLabel.setForeground(remoteActive ? new Color(0, 220, 0) : Color.RED);
statoLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
remotePanel.add(statoLabel);
remotePanel.add(Box.createVerticalStrut(18));

// Porta
JLabel portaLabel = new JLabel("Porta server (1100-65535):");
portaLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
portaLabel.setForeground(new Color(180, 180, 180));
portaLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
remotePanel.add(portaLabel);

JTextField portaField = new JTextField("8080", 8);
portaField.setMaximumSize(new Dimension(120, 32));
portaField.setFont(new Font("SansSerif", Font.PLAIN, 16));
portaField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
remotePanel.add(portaField);
remotePanel.add(Box.createVerticalStrut(18));

// Password
JLabel passwordLabel = new JLabel("Password (opzionale):");
passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
passwordLabel.setForeground(new Color(180, 180, 180));
passwordLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
remotePanel.add(passwordLabel);

JPasswordField passwordField = new JPasswordField(16);
passwordField.setMaximumSize(new Dimension(260, 32));
passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
passwordField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
remotePanel.add(passwordField);
remotePanel.add(Box.createVerticalStrut(18));

Runnable aggiornaVisibilitaCampiRemoti = () -> {
    boolean visibile = !remoteActive;
    portaLabel.setVisible(visibile);
    portaField.setVisible(visibile);
    passwordLabel.setVisible(visibile);
    passwordField.setVisible(visibile);
};
aggiornaVisibilitaCampiRemoti.run();

// Bottone avvia/ferma
JButton avviaBtn = new JButton(remoteActive ? "Ferma server remoto" : "Avvia server remoto");
avviaBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
avviaBtn.setPreferredSize(new Dimension(260, 44));
avviaBtn.setMaximumSize(avviaBtn.getPreferredSize());
avviaBtn.setForeground(Color.WHITE);
avviaBtn.setBackground(remoteActive ? new Color(180, 0, 0) : new Color(0, 180, 0));
avviaBtn.setFocusPainted(false);
avviaBtn.setContentAreaFilled(true);
avviaBtn.setContentAreaFilled(false);
avviaBtn.setMargin(new Insets(4, 8, 4, 8));
avviaBtn.setBorder(new RoundedBorder(18, remoteActive ? Color.RED : new Color(0, 180, 0)));
avviaBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
avviaBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
remotePanel.add(avviaBtn);
remotePanel.add(Box.createVerticalStrut(18));

JLabel ipLabel = new JLabel(remoteActive && remoteLastIp != null ? "🌐 Collegati a: http://" + remoteLastIp + ":8080" : "");
ipLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
ipLabel.setForeground(new Color(0, 180, 180));
ipLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
remotePanel.add(ipLabel);
remotePanel.add(Box.createVerticalStrut(18));

JLabel qrLabel = new JLabel();
qrLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
qrLabel.setIcon((remoteActive && remoteQrIcon != null) ? remoteQrIcon : null);
remotePanel.add(qrLabel);
remotePanel.add(Box.createVerticalStrut(8));

avviaBtn.addActionListener(e -> {
    if (!remoteActive) {
        try {
            int porta = 8080;
            try {
                porta = Integer.parseInt(portaField.getText().trim());
                if (porta < 1100 || porta > 65535) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Inserisci una porta valida tra 1100 e 65535", "Porta non valida", JOptionPane.ERROR_MESSAGE);
        return;
    }
            String ip = getLocalIpAddress();
            String pwd = new String(passwordField.getPassword());
            remotePassword = pwd.isEmpty() ? null : pwd;
            remoteServer = new RemoteServer(this, remotePassword, porta);
            remoteActive = true;
            remoteLastIp = ip;
            statoLabel.setText("Stato: ATTIVO");
            avviaBtn.setText("Ferma server remoto");
            ipLabel.setText("Collegati a: http://" + ip + ":" + porta);
            // Genera QR code
            try {
                com.google.zxing.Writer writer = new com.google.zxing.qrcode.QRCodeWriter();
                com.google.zxing.common.BitMatrix bitMatrix = writer.encode("http://" + ip + ":" + porta, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200);
                java.awt.image.BufferedImage qrImage = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < 200; x++) for (int y = 0; y < 200; y++) qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                remoteQrIcon = new ImageIcon(qrImage);
                qrLabel.setIcon(remoteQrIcon);
            } catch (Exception ex) { qrLabel.setText("Errore QR"); }
        } catch (Exception ex) {
            statoLabel.setText("Errore avvio server: " + ex.getMessage());
        }
    } else {
        if (remoteServer != null) remoteServer.stopServer();
        remoteServer = null;
        remoteActive = false;
        statoLabel.setText("Stato: NON ATTIVO");
        avviaBtn.setText("Avvia server remoto");
        ipLabel.setText("");
        qrLabel.setIcon(null);
    }
    aggiornaVisibilitaCampiRemoti.run();
});

  tabbedPane.addTab("Collegamento Remoto", remotePanel);
  settingsDialog.add(tabbedPane, BorderLayout.CENTER);
  JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
  closePanel.add(closeButton);
  settingsDialog.add(closePanel, BorderLayout.SOUTH);
  settingsDialog.setLocationRelativeTo(frame);
  settingsDialog.setVisible(true);
  impostazioniAperte = false; // sicurezza: se la finestra viene chiusa in altro modo
  // Nuova checkbox per mostrare/nascondere il tasto Importa WOL

  // --- AGGIUNGI SEZIONE COLLEGAMENTO REMOTO NELLE IMPOSTAZIONI ---
  remotePanel.setLayout(new BoxLayout(remotePanel, BoxLayout.Y_AXIS));
  remotePanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
  remotePanel.add(statoLabel);
  remotePanel.add(Box.createVerticalStrut(8));
  remotePanel.add(avviaBtn);
  remotePanel.add(Box.createVerticalStrut(8));
  remotePanel.add(ipLabel);
  remotePanel.add(Box.createVerticalStrut(8));
  remotePanel.add(qrLabel);

}

private void salvaImpostazioni() {
  try (FileWriter fw = new FileWriter(SETTINGS_CONFIG)) {
      fw.write("proiezioneAbilitata=" + proiezioneAbilitata + "\n");
      fw.write("monitorSelezionato=" + monitorSelezionato + "\n");
      fw.write("fuoriTempoLampeggiante=" + fuoriTempoLampeggiante + "\n");
      fw.write("disabilitaMessaggiOratore=" + disabilitaMessaggiOratore + "\n");
      fw.write("mostraOrariParti=" + mostraOrariParti + "\n");
      fw.write("mostraTempoEfficaciMinistero=" + mostraTempoEfficaciMinistero + "\n");
      fw.write("mostraImportaWOL=" + mostraImportaWOL + "\n");
      fw.write("assorbiRitardoAutomatico=" + assorbiRitardoAutomatico + "\n");
  } catch (IOException e) {
      // Ignora errori di scrittura
  }
}
private void caricaImpostazioni() {
  File config = new File(SETTINGS_CONFIG);
  if (config.exists()) {
      try (BufferedReader br = new BufferedReader(new FileReader(config))) {
          String line;
          while ((line = br.readLine()) != null) {
              if (line.startsWith("proiezioneAbilitata=")) {
                  proiezioneAbilitata = Boolean.parseBoolean(line.split("=", 2)[1]);
              } else if (line.startsWith("monitorSelezionato=")) {
                  try {
                      monitorSelezionato = Integer.parseInt(line.split("=", 2)[1]);
                  } catch (NumberFormatException ex) {
                      monitorSelezionato = 1;
                  }
              } else if (line.startsWith("fuoriTempoLampeggiante=")) {
                  fuoriTempoLampeggiante = Boolean.parseBoolean(line.split("=", 2)[1]);
              } else if (line.startsWith("disabilitaMessaggiOratore=")) {
                  disabilitaMessaggiOratore = Boolean.parseBoolean(line.split("=", 2)[1]);
              } else if (line.startsWith("mostraOrariParti=")) {
                  mostraOrariParti = Boolean.parseBoolean(line.split("=", 2)[1]);
              } else if (line.startsWith("mostraTempoEfficaciMinistero=")) {
                  mostraTempoEfficaciMinistero = Boolean.parseBoolean(line.split("=", 2)[1]);
              } else if (line.startsWith("mostraImportaWOL=")) {
                  mostraImportaWOL = Boolean.parseBoolean(line.split("=", 2)[1]);
              } else if (line.startsWith("assorbiRitardoAutomatico=")) {
                  assorbiRitardoAutomatico = Boolean.parseBoolean(line.split("=", 2)[1]);
              }
          }
      } catch (IOException e) {
          // Ignora errori di lettura
      }
  }
}
// Modifica mostraOrologioSecondoMonitor per usare monitorSelezionato
private void mostraOrologioSecondoMonitor() {
    if (!proiezioneAbilitata) {
  if (externalClockFrame != null) externalClockFrame.setVisible(false);
        return;
    }
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screens = ge.getScreenDevices();
    int idx = Math.max(0, Math.min(monitorSelezionato, screens.length - 1));
    GraphicsDevice targetScreen = screens[idx];
    if (externalClockFrame == null) {
        externalClockFrame = new JFrame();
  externalClockFrame.setUndecorated(true);
  externalClockFrame.setAlwaysOnTop(true);
        externalClockFrame.setBackground(Color.BLACK);
        externalClockLabel = new JLabel("00:00:00", SwingConstants.CENTER) {
      @Override
            public void setText(String text) {
                super.setText("<html><div style='text-align:center;word-break:break-all;'>" + text.replace("\n", "<br>") + "</div></html>");
                updateExternalClockLabelFont();
            }
        };
        externalClockLabel.setFont(new Font("SansSerif", Font.BOLD,290));
        externalClockLabel.setForeground(Color.CYAN);
        externalClockLabel.setBackground(Color.BLACK);
  externalClockLabel.setOpaque(true);
        externalClockFrame.getContentPane().add(externalClockLabel);
        externalClockSwingTimer = new Timer(1000, e -> {
            if (!externalIsTimer) {
                if (testoPersonalizzatoClock != null) return;
                externalClockLabel.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        });
  externalClockSwingTimer.start();
        externalClockFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateTimerLabel();
                updateExternalClockLabelFont();
            }
        });
    }
    // Usa gli usable bounds per evitare la taskbar
    java.awt.Rectangle usableBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    java.awt.Rectangle bounds = targetScreen.getDefaultConfiguration().getBounds();
    // Calcola il rapporto del monitor
    double ratio = (double) bounds.width / (double) bounds.height;
    // Se il monitor è circa 4:3 (1.33), adatta la finestra a 4:3 centrata
    if (Math.abs(ratio - 4.0/3.0) < 0.05) {
        int newW = bounds.width;
        int newH = (int) (bounds.width * 3.0 / 4.0);
        if (newH > bounds.height) {
            newH = bounds.height;
            newW = (int) (bounds.height * 4.0 / 3.0);
        }
        int x = bounds.x + (bounds.width - newW) / 2;
        int y = bounds.y + (bounds.height - newH) / 2;
        // Adatta a usable bounds
        if (usableBounds.contains(x, y, newW, newH)) {
            externalClockFrame.setBounds(x, y, newW, newH);
        } else {
            externalClockFrame.setBounds(usableBounds);
        }
    } else {
        // Altrimenti usa tutto lo schermo (16:9 o altro), ma solo la parte usabile
        if (usableBounds.contains(bounds)) {
            externalClockFrame.setBounds(usableBounds);
        } else {
            externalClockFrame.setBounds(bounds);
        }
    }
    externalClockFrame.setVisible(true);
    updateExternalClockLabelFont();
}
// Adatta il font della label esterna in base alla dimensione della finestra
private void adaptFontToLabelEsterno() {
  if (externalClockLabel == null) return;
  String text = externalClockLabel.getText().replaceAll("<[^>]*>", "");
      if (text.isEmpty()) return;
      int labelWidth = externalClockLabel.getWidth();
      int labelHeight = externalClockLabel.getHeight();
      if (labelWidth <= 0 || labelHeight <= 0) return;
  int fontSize = 40;
      Font font = externalClockLabel.getFont();
      FontMetrics fm;
  int safeWidth = (int)(labelWidth * 0.92); // margine di sicurezza
  int safeHeight = (int)(labelHeight * 0.85);
      do {
          font = font.deriveFont((float) fontSize);
          fm = externalClockLabel.getFontMetrics(font);
          int textWidth = fm.stringWidth(text);
          int textHeight = fm.getHeight();
          if (textWidth > safeWidth || textHeight > safeHeight) {
              fontSize--;
              break;
          }
          fontSize++;
  } while (fontSize < 600);
      if (fontSize < 10) fontSize = 10;
      externalClockLabel.setFont(externalClockLabel.getFont().deriveFont((float) fontSize));
  }
private void aggiornaLampeggioPulsanti() {
  blinkState = !blinkState;
  // Lampeggio timerLabel in caso di ritardo SOLO se NON siamo su FINE
  boolean isFine = timerLabel.getText().contains("id='fineLamp'");
  if (!isFine && fuoriTempoLampeggiante && timeLeft < 0 && timerLabel.isVisible()) {
      Color lampeggioColore = blinkState ? Color.RED : Color.BLACK;
      timerLabel.setForeground(lampeggioColore);
      // Aggiorna anche il colore HTML se serve
      String html = timerLabel.getText();
      if (html.startsWith("<html>")) {
          html = html.replaceAll("color:[^;']*;?", "");
          String coloreHex = blinkState ? String.format("#%02x%02x%02x;", 255, 0, 0) : String.format("#%02x%02x%02x;", 0, 0, 0);
          html = html.replaceFirst("<span ", "<span style='color:" + coloreHex + "' ");
            timerLabel.setText(html);
      }
      // Lampeggio anche su externalClockLabel
      if (externalClockLabel != null && externalIsTimer && externalClockLabel.isVisible()) {
          externalClockLabel.setForeground(lampeggioColore);
          String extHtml = externalClockLabel.getText();
          if (extHtml.startsWith("<html>")) {
              extHtml = extHtml.replaceAll("color:[^;']*;?", "");
              String coloreHex = blinkState ? String.format("#%02x%02x%02x;", 255, 0, 0) : String.format("#%02x%02x%02x;", 0, 0, 0);
              extHtml = extHtml.replaceFirst("<span ", "<span style='color:" + coloreHex + "' ");
              externalClockLabel.setText(extHtml);
          }
      }
  }
  // Lampeggio solo sulla parola FINE (giallo/trasparente) se siamo su FINE
  if (isFine) {
      String html = timerLabel.getText();
      String color = blinkState ? "#ffff00" : "#222200";
      html = html.replaceAll("(<span id='fineLamp'[^>]*style='[^\"]*)color:[^;']*;?", "$1");
      html = html.replaceAll("(<span id='fineLamp'[^>]*style='[^\"]*)'", "$1;color:"+color+";'");
      timerLabel.setText(html);
      if (externalClockLabel != null && externalClockLabel.getText().contains("id='fineLampExt'")) {
          String extHtml = externalClockLabel.getText();
          String colorExt = blinkState ? "#ffff00" : "#222200";
          extHtml = extHtml.replaceAll("(<span id='fineLampExt'[^>]*style='[^\"]*)color:[^;']*;?", "$1");
          extHtml = extHtml.replaceAll("(<span id='fineLampExt'[^>]*style='[^\"]*)'", "$1;color:"+colorExt+";'");
          externalClockLabel.setText(extHtml);
      }
  } // <--- questa graffa deve esserci!
  if (swingTimer != null && swingTimer.isRunning() && !isPaused) {
      // Timer in esecuzione: lampeggia startButton
      startButton.setBackground(blinkState ? new Color(0, 220, 0) : new Color(30, 30, 30));
      startButton.setForeground(blinkState ? Color.BLACK : Color.WHITE);
      pauseButton.setBackground(new Color(30, 30, 30));
      pauseButton.setForeground(Color.WHITE);
  } else if (isPaused && swingTimer != null && swingTimer.isRunning()) {
      // Timer in pausa: lampeggia pauseButton
      pauseButton.setBackground(blinkState ? new Color(255, 255, 0) : new Color(30, 30, 30));
      pauseButton.setForeground(blinkState ? Color.BLACK : Color.WHITE);
      startButton.setBackground(new Color(30, 30, 30));
      startButton.setForeground(Color.WHITE);
              } else {
      // Nessun lampeggio
      startButton.setBackground(new Color(30, 30, 30));
      startButton.setForeground(Color.WHITE);
      pauseButton.setBackground(new Color(30, 30, 30));
      pauseButton.setForeground(Color.WHITE);
  }
  if (fineOrariLabel != null && fineOrariLabel.isShowing()) {
      fineOrariLabel.setForeground(blinkState ? new Color(0,255,255) : new Color(0,0,0,0));
  }
}
// Aggiorna la label della canzione corrente
private void updateSongLabel() {
    if (mp3Files != null && mp3Files.length > 0 && currentSongIndex >= 0 && currentSongIndex < mp3Files.length) {
        String titolo = null;
        String numero = "";
        try {
            Mp3File mp3 = new Mp3File(mp3Files[currentSongIndex]);
            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                titolo = tag.getTitle();
            } else if (mp3.hasId3v1Tag()) {
                ID3v1 tag = mp3.getId3v1Tag();
                titolo = tag.getTitle();
            }
        } catch (Exception e) {
            // Se errore, fallback
        }
        if (titolo == null || titolo.isEmpty()) {
            titolo = mp3Files[currentSongIndex].getName();
        }
        // Estrai numero cantico dal nome file (es: _008_)
        String nome = mp3Files[currentSongIndex].getName();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("_0*(\\d+)").matcher(nome);
        if (m.find()) numero = m.group(1);
        songLabel.setText(titolo);
        smallTimerLabel.setText("CANTICO " + (numero.isEmpty() ? "?" : numero));
  } else {
        songLabel.setText("");
        smallTimerLabel.setText("");
    }
}
private void startPlayerBlinkTimer() {
  if (playerBlinkTimer != null) playerBlinkTimer.stop();
  playerBlinkTimer = new Timer(500, e -> {
      playerBlinkState = !playerBlinkState;
      if (isPlaying) {
          playButton.setBackground(playerBlinkState ? new Color(0, 220, 0) : new Color(30, 30, 30));
          playButton.setForeground(playerBlinkState ? Color.BLACK : Color.WHITE);
          stopButton.setBackground(new Color(30, 30, 30));
          stopButton.setForeground(Color.WHITE);
      } else {
          stopButton.setBackground(playerBlinkState ? new Color(255, 255, 0) : new Color(30, 30, 30));
          stopButton.setForeground(playerBlinkState ? Color.BLACK : Color.WHITE);
          playButton.setBackground(new Color(30, 30, 30));
          playButton.setForeground(Color.WHITE);
      }
  });
  playerBlinkTimer.start();
}
// --- AGGIUNTA: funzione per applicare renderer pillola con animazione ---
private void applicaRendererPillola() {
  listaParti.setCellRenderer(new ListCellRenderer<Parte>() {
      private final Color verdeCantico = new Color(0, 220, 0);
      private final Color selezione = Color.DARK_GRAY;
      private final Color testoSelezione = Color.CYAN;
      private final Color introColor = Color.ORANGE;
      private final Color bgNormale = Color.BLACK;
      @Override
      public Component getListCellRendererComponent(JList<? extends Parte> list, Parte value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.isIntro && value.durataSecondi == 0) {
            // Titolo sezione: colore diverso in base all'ordine
            int introCount = 0;
            for (int i = 0; i <= index; i++) {
                Parte p = listModel.get(i);
                if (p.isIntro && p.durataSecondi == 0) introCount++;
            }
            Color sectionColor;
            if (introCount == 1) sectionColor = new Color(0x3c, 0xf7, 0x8b); // #3cf78b
            else if (introCount == 2) sectionColor = new Color(0xff, 0xcd, 0x5c); // #ffcd5c
            else if (introCount == 3) sectionColor = new Color(0xbf, 0x2f, 0x13); // #bf2f13
            else sectionColor = new Color(255, 180, 40); // fallback arancione
            JLabel titoloLabel = new JLabel(value.nome, SwingConstants.CENTER);
            titoloLabel.setFont(list.getFont().deriveFont(Font.BOLD, 20f));
            titoloLabel.setForeground(sectionColor);
            titoloLabel.setOpaque(false);
            JPanel titoloPanel = new JPanel(new BorderLayout());
            titoloPanel.setOpaque(false);
            titoloPanel.add(titoloLabel, BorderLayout.CENTER);
            return titoloPanel;
        }
          JPanel panel = new JPanel(new BorderLayout()) {
              @Override
              protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  int arc = 40;
                  int margin = 8;
                  int w = getWidth() - 2 * margin;
                  int h = getHeight() - 8;
                  int x = margin;
                  int y = 4;
                  // Ombra
                  g2.setColor(new Color(0,0,0,40));
                  g2.fillRoundRect(x+2, y+4, w, h, arc, arc);
                  // Sfondo pillola
                  Color pillColor;
                  if (index == parteAnimataA && animFrame > 0 && parteAnimataDa != -1) {
                      // Interpolazione colore tra selezione e normale
                      float progress = animFrame / (float) animTotalFrames;
                      pillColor = blendColor(new Color(35, 35, 35), selezione, progress);
                  } else if (index == parteAnimataDa && animFrame > 0 && parteAnimataA != -1) {
                      float progress = 1f - (animFrame / (float) animTotalFrames);
                      pillColor = blendColor(new Color(35, 35, 35), selezione, progress);
                  } else if (index == currentParteIndex) {
                      pillColor = selezione;
          } else {
                      pillColor = new Color(35, 35, 35);
                  }
                  if (index == parteAnimata && animStep > 0) {
                      // Animazione: illumina la pillola
                      int alpha = Math.min(180, 30 * animStep);
                      pillColor = new Color(255, 255, 0, alpha).brighter();
                  }
                  g2.setColor(pillColor);
                  g2.fillRoundRect(x, y, w, h, arc, arc);
                  // --- PROGRESS BAR TIPO CLESSIDRA ---
                  if (index == currentParteIndex && value.durataSecondi > 0) {
                      double percent = 1.0;
                      int timeForBar = timeLeft;
                      if (timeForBar >= 0 && timeForBar <= value.durataSecondi) {
                          // Barra fluida, nessun arrotondamento
                          percent = (double) timeForBar / (double) value.durataSecondi;
                      } else if (timeForBar < 0) {
                          percent = 0.0;
                      }
                      int barW = (int) (w * percent);
                      Color barColor;
                      if (percent > 0.5) {
                          barColor = new Color(0, 200, 0, 180); // verde
                      } else if (percent > 0.2) {
                          barColor = new Color(255, 200, 0, 200); // giallo
              } else {
                          barColor = new Color(255, 60, 60, 200); // rosso
                      }
                      g2.setColor(barColor);
                      g2.fillRoundRect(x, y, barW, h, arc, arc);
                  }
                  g2.dispose();
              }
          };
          panel.setOpaque(false);
          panel.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
          JLabel label = new JLabel(value.toString(), SwingConstants.LEFT);
          label.setFont(list.getFont().deriveFont(Font.BOLD));
          label.setOpaque(false);
                  label.setForeground(Color.WHITE);
          if (value.nome.toUpperCase().contains("CANTICO")) label.setForeground(verdeCantico);
          if (value.isIntro) label.setForeground(introColor);
          if (index == currentParteIndex) label.setForeground(testoSelezione);
          JLabel orariLabel;
          String nomeUpper = value.nome.toUpperCase();
          // --- LOGICA PILLOLA EFFICACI ---
// Determina se siamo tra "EFFICACI NEL MINISTERO" e "VITA CRISTIANA"
boolean inEfficaci = false;
for (int i = 0; i <= index; i++) {
    String n = listModel.get(i).nome.toUpperCase();
    if (n.contains("EFFICACI NEL MINISTERO")) inEfficaci = true;
    if (n.contains("VITA CRISTIANA")) inEfficaci = false;
}
boolean mostraEffettivo = (
    (inEfficaci && !value.isIntro && value.durataSecondi > 0 && !nomeUpper.contains("CONSIGLI"))
    || nomeUpper.contains("LETTURA BIBLICA")
    || nomeUpper.contains("DISCORSO PUBBLICO")
);
if (mostraEffettivo && value.tempoEffettivo != null) {
    // Mostra la pillola SOLO se la parte è conclusa
    String testoPillola = Parte.formatTime(value.tempoEffettivo.intValue());
    orariLabel = new JLabel(testoPillola + "\u00A0\u00A0\u00A0");
    orariLabel.setFont(list.getFont().deriveFont(Font.BOLD, 16f));
    orariLabel.setForeground(new Color(0, 180, 220));
    orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
    panel.add(label, BorderLayout.CENTER);
    panel.add(orariLabel, BorderLayout.EAST);
    return panel;
} else if (mostraEffettivo) {
    // Mostra orario inizio-fine FINCHÉ la parte non è conclusa
    LocalTime inizio = orarioInizioAdunanza;
    int secondiTrascorsi = 0;
    for (int i = 0; i < index; i++) {
        secondiTrascorsi += listModel.get(i).durataSecondi;
    }
    inizio = inizio.plusSeconds(secondiTrascorsi);
    LocalTime fine = inizio.plusSeconds(value.durataSecondi);
    String orarioInizio = inizio.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    String orarioFine = fine.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    orariLabel = new JLabel(orarioInizio + " - " + orarioFine);
    orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
    orariLabel.setForeground(new Color(180,180,180));
    orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
    panel.add(label, BorderLayout.CENTER);
    panel.add(orariLabel, BorderLayout.EAST);
    return panel;
} else if (value.nome.equalsIgnoreCase("INIZIO")) {
              String orarioInizioStr = orarioInizioAdunanza.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioInizioStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          } else if (value.nome.equalsIgnoreCase("FINE") && orarioFineProgrammatoFisso != null) {
              String orarioFineStr = orarioFineProgrammatoFisso.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioFineStr);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          } else {
              LocalTime inizio = orarioInizioAdunanza;
              int secondiTrascorsi = 0;
              for (int i = 0; i < index; i++) {
                  secondiTrascorsi += listModel.get(i).durataSecondi;
              }
              inizio = inizio.plusSeconds(secondiTrascorsi);
              LocalTime fine = inizio.plusSeconds(value.durataSecondi);
              String orarioInizio = inizio.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              String orarioFine = fine.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
              orariLabel = new JLabel(orarioInizio + " - " + orarioFine);
              orariLabel.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
              orariLabel.setForeground(new Color(180,180,180));
          }
          orariLabel.setHorizontalAlignment(SwingConstants.RIGHT);
          orariLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
          panel.add(label, BorderLayout.CENTER);
          panel.add(orariLabel, BorderLayout.EAST);
          return panel;
      }
  });
}
// --- AGGIUNTA: metodo per adattare il font della label esterna del secondo monitor ---
private void updateExternalClockLabelFont() {
    if (externalClockLabel == null) return;
    String text = externalClockLabel.getText().replaceAll("<[^>]*>", "");
    if (text.isEmpty()) return;
    int labelWidth = externalClockLabel.getWidth();
    int labelHeight = externalClockLabel.getHeight();
    if (labelWidth <= 0 || labelHeight <= 0) return;
    int fontSize = 40;
    Font font = externalClockLabel.getFont();
    FontMetrics fm;
    int safeWidth = (int)(labelWidth * 0.92); // margine di sicurezza
    int safeHeight = (int)(labelHeight * 0.85);
    do {
        font = font.deriveFont((float) fontSize);
        fm = externalClockLabel.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        if (textWidth > safeWidth || textHeight > safeHeight) {
            fontSize--;
            break;
        }
        fontSize++;
    } while (fontSize < 600);
    if (fontSize < 10) fontSize = 10;
    externalClockLabel.setFont(externalClockLabel.getFont().deriveFont((float) fontSize));
}

private void checkForUpdates() {
    new Thread(() -> {
        try {
            // Usa la pagina HTML delle release invece dell'API
            String url = "https://github.com/diegoisme-dev/KH-Timer/releases/latest";
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .get();
            // Cerca il tag che contiene la versione (di solito <span class="css-truncate-target">)
            Element versionElem = doc.selectFirst(".css-truncate-target");
            String latestVersion = versionElem != null ? versionElem.text() : null;
            if (latestVersion != null && !latestVersion.equals(VERSION)) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    checkLabel.setText("\u274C aggiornamento disponibile");
                    checkLabel.setForeground(Color.RED);

                    // Mostra anche la finestra di dialogo
                    JDialog updateDialog = new JDialog(frame, "Aggiornamento disponibile", true);
                    updateDialog.setLayout(new BorderLayout(16, 16));
                    updateDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    updateDialog.setSize(400, 250);
                    updateDialog.setResizable(false);
                    updateDialog.setLocationRelativeTo(frame);

                    JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
                    iconLabel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 0));
                    updateDialog.add(iconLabel, BorderLayout.WEST);

                    JPanel textPanel = new JPanel();
                    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                    JLabel titleLabel = new JLabel("Nuova versione disponibile!");
                    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JLabel versionLabel = new JLabel("Versione attuale: " + VERSION);
                    versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
                    versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JLabel latestLabel = new JLabel("Ultima: " + latestVersion);
                    latestLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
                    latestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JLabel infoLabel = new JLabel("Scarica l'ultima versione dal sito ufficiale:");
                    infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JLabel linkLabel = new JLabel("<html><a href='https://github.com/diegoisme-dev/KH-Timer/releases'>https://github.com/diegoisme-dev/KH-Timer/releases</a></html>");
                    linkLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    linkLabel.setForeground(new Color(0, 102, 204));
                    linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent e) {
          try {
                                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://github.com/diegoisme-dev/KH-Timer/releases"));
          } catch (Exception ex) {
                                // Ignora
          }
      }
  });
                    textPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 16));
                    textPanel.add(titleLabel);
                    textPanel.add(Box.createVerticalStrut(8));
                    textPanel.add(versionLabel);
                    textPanel.add(Box.createVerticalStrut(8));
                    textPanel.add(latestLabel);
                    textPanel.add(Box.createVerticalStrut(8));
                    textPanel.add(infoLabel);
                    textPanel.add(linkLabel);
                    updateDialog.add(textPanel, BorderLayout.CENTER);

                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton updateBtn = new JButton("Aggiorna");
                    JButton closeBtn = new JButton("Chiudi");
                    updateBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
                    closeBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    updateBtn.addActionListener(ev -> {
                        try {
                            java.awt.Desktop.getDesktop().browse(new java.net.URI("https://github.com/diegoisme-dev/KH-Timer/releases"));
                        } catch (Exception ex) {
                            // Ignora
                        }
                    });
                    closeBtn.addActionListener(ev -> updateDialog.dispose());
                    buttonPanel.add(updateBtn);
                    buttonPanel.add(closeBtn);
                    updateDialog.add(buttonPanel, BorderLayout.SOUTH);
                    updateDialog.setVisible(true);
                });
  } else {
                checkLabel.setText("\u2714 App Aggiornata");
                checkLabel.setForeground(new Color(0, 200, 0));
            }
        } catch (Exception e) {
            System.err.println("Errore controllo aggiornamenti: " + e.getMessage());
        }
    }).start();
}
// --- AGGIUNTA: metodo per creare il pannello cantico ---
private JPanel creaPanelCantico(String num, int fontCantico, int fontNum) {
    JLabel labelCantico = new JLabel("CANTICO");
    labelCantico.setFont(new Font("SansSerif", Font.BOLD, fontCantico));
    labelCantico.setForeground(Color.WHITE);
    labelCantico.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel labelNumero = new JLabel(num);
    labelNumero.setFont(new Font("SansSerif", Font.BOLD, fontNum));
    labelNumero.setForeground(Color.WHITE);
    labelNumero.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.BLACK);
    panel.setOpaque(true);
    panel.add(labelCantico);
    if (!"00".equals(num)) {
        panel.add(Box.createRigidArea(new Dimension(0, -20))); // Spazio negativo per avvicinare
        panel.add(labelNumero);
    }
    return panel;
}

private int getWOLWeekNumber2025() {
    LocalDate start = LocalDate.of(2024, 12, 30);
    LocalDate today = LocalDate.now();
    long days = java.time.temporal.ChronoUnit.DAYS.between(start, today);
    int week = (int)(days / 7) + 1;
    if (week < 1) week = 1;
    if (week > 52) week = 52;
    return week;
}
// AGGIUNTA: funzione per importare le parti dal file HTML WOL
private void importaPartiDaHtmlWOL() {
    // URL fisso fornito dall'utente
    int weekNum = getWOLWeekNumber2025();
    String url = "https://wol.jw.org/it/wol/meetings/r6/lp-i/2025/" + weekNum;
    try {
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
            .header("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .timeout(20000)
            .get();
        // --- ESTRATTORE GENERICO: cerca le parti principali ---
        Elements titoli = doc.select("h3, h2, h4, .pub-meeting-item-header, .pub-meeting-item-title");
        listModel.clear();
        listModel.addElement(new Parte("INIZIO", 0));
        java.util.List<Parte> partiTemp = new java.util.ArrayList<>();
          boolean inEfficaci = false;
        boolean inVitaCristiana = false;
        for (Element titolo : titoli) {
            String nome = titolo.text().trim();
            String nomeNorm = nome.toUpperCase().replaceAll("\\s+", " ").trim();
            System.out.println("--- PARSING ---");
            System.out.println("Titolo: " + nome);
            if (nomeNorm.equals("TESORI DELLA PAROLA DI DIO")) {
                System.out.println("  -> Titolo sezione speciale, aggiunto come intro");
                partiTemp.add(new Parte(nome, 0, true));
                continue;
            }
            if (nomeNorm.equals("EFFICACI NEL MINISTERO")) {
                inEfficaci = true;
                System.out.println("  -> Titolo sezione speciale, aggiunto come intro");
                partiTemp.add(new Parte(nome, 0, true));
                continue;
            }
            if (nomeNorm.equals("VITA CRISTIANA")) {
                inVitaCristiana = true;
                inEfficaci = false;
                partiTemp.add(new Parte(nome, 0, true));
                continue;
            }
            
            // Cerca il <p> subito dopo il titolo (anche annidato)
            Element nextP = null;
            Element sibling = titolo.nextElementSibling();
            while (sibling != null) {
                nextP = sibling.selectFirst("p");
                if (nextP != null) break;
                sibling = sibling.nextElementSibling();
            }
            if (nextP != null) {
                String testo = nextP.text().trim();
                System.out.println("  <p> dopo titolo: " + testo);
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d+)\\s*min\\)").matcher(testo);
                if (m.find()) {
                    int durataSec = Integer.parseInt(m.group(1)) * 60;
                    String nomeParte;
                    if (inVitaCristiana) {
                        // Prendi solo il testo del <strong> dentro <h3> e togli il numerino
                        Element strong = titolo.selectFirst("strong");
                        if (strong != null) {
                            nomeParte = strong.text().trim().replaceFirst("^\\d+\\.\\s*", "");
          } else {
                            nomeParte = nome.replaceFirst("^\\d+\\.\\s*", "");
                        }

                        if (!nomeParte.toUpperCase().contains("CANTICO")) {
                            partiTemp.add(new Parte("INTRO", 15, true));
                        }
                    } else if (inEfficaci) {
                        Element strong = titolo.selectFirst("strong");
                        nomeParte = (strong != null) ? strong.text().trim() : nome;
      } else {
                        nomeParte = testo.substring(m.end()).trim();
                        if (nomeParte.isEmpty()) nomeParte = nome;
                    }
                    partiTemp.add(new Parte(nomeParte, durataSec));
                    System.out.println("  -> Estratto: nome='" + nomeParte + "', durata=" + durataSec/60 + " min");
                    continue;
                }
            } else {
                System.out.println("  <p> dopo titolo: (nessuno)");
            }
            // Se non trovato, usa la logica classica
            Element durataElem = titolo.parent().selectFirst(".pub-meeting-item-time, .pub-meeting-item-duration, .duration, .time");
            int durataSec = 0;
            if (durataElem != null) {
                String durataTxt = durataElem.text().replaceAll("[^0-9]", "");
                if (!durataTxt.isEmpty()) {
                    durataSec = Integer.parseInt(durataTxt) * 60;
                }
            }
            System.out.println("  -> Logica classica: nome='" + nome + "', durata=" + (durataSec > 0 ? (durataSec/60 + " min") : "0 min") );
            if (!nome.isEmpty()) {
                partiTemp.add(new Parte(nome, durataSec));
            }
        }
        for (int i = 0; i < partiTemp.size() - 1; i++) {
            Parte p = partiTemp.get(i);
            if (p.nome.toUpperCase().contains("GEMME SPIRITUALI")) {
                Parte dopo = partiTemp.get(i + 1);
                if (dopo != null && !dopo.nome.toUpperCase().contains("LETTURA BIBLICA")) {
                    dopo.nome = "3. Lettura biblica" + (dopo.nome.isEmpty() ? "" : ": " + dopo.nome);
                    partiTemp.set(i + 1, dopo);
                }
                break;
            }
        }
        for (int i = partiTemp.size() - 1; i >= 0; i--) {
            String nome = partiTemp.get(i).nome.toUpperCase();
            if (nome.contains("GEMME SPIRITUALI") || nome.contains("LETTURA BIBLICA")) {
                partiTemp.add(i, new Parte("INTRO", 15, true));
            }
        }
        listModel.addAll(listModel.size(), partiTemp);
        listModel.addElement(new Parte("FINE", 0));
        // --- RIMOZIONE VERSO/VERSETTO DOPO INIZIO ---
        if (listModel.size() > 2) {
            Parte dopoInizio = listModel.get(1);
            String n = dopoInizio.nome.toLowerCase();
            // Rimuovi se contiene 'versetto', 'tema', 'testo', o è breve (tipico del versetto)
            if (n.contains("versetto") || n.contains("testo") || n.contains("tema") || n.matches("[a-zA-Z0-9 ]{1,25}")) {
                listModel.remove(1);
            }
        }
        // --- RIMOZIONE 2 PARTI TRA CANTICO FINALE E FINE ---
        int idxCantico = -1, idxFine = -1;
        for (int i = 0; i < listModel.size(); i++) {
            String nome = listModel.get(i).nome.toUpperCase();
            if (idxCantico == -1 && nome.contains("CANTICO FINALE")) idxCantico = i;
            if (nome.equals("FINE")) { idxFine = i; break; }
        }
        if (idxCantico != -1 && idxFine != -1 && idxFine - idxCantico > 2) {
            // Rimuovi le due parti dopo il cantico finale
            listModel.remove(idxCantico + 2);
            listModel.remove(idxCantico + 1);
        }
        // --- IMPOSTA DURATA CANTICI E STUDIO BIBLICO ---
        int canticoCount = 0;
        int idxVitaCristiana = -1;
        for (int i = 0; i < listModel.size(); i++) {
            Parte parte = listModel.get(i);
            String nome = parte.nome.toUpperCase();
            if (nome.contains("CANTICO")) {
                canticoCount++;
                if (canticoCount == 1) parte.durataSecondi = 6 * 60;
                else if (canticoCount == 2) parte.durataSecondi = 5 * 60;
                else if (canticoCount == 3) parte.durataSecondi = 8 * 60;
                listModel.set(i, parte);
            }
            if (nome.contains("STUDIO BIBLICO DI CONGREGAZIONE")) {
                parte.durataSecondi = 30 * 60;
                listModel.set(i, parte);
            }
            if (nome.equals("LETTURA BIBLICA")) {
                parte.durataSecondi = 4 * 60;
                listModel.set(i, parte);
            }
            if (nome.equals("VITA CRISTIANA")) {
                idxVitaCristiana = i;
            }
        }
        // --- AGGIUNGI CONSIGLI DOPO LE PARTI DELLA SCUOLA (da LETTURA BIBLICA a prima di VITA CRISTIANA) ---
        // Trova l'indice di "Gemme spirituali" e del primo cantico centrale dopo di essa
int idxGemme = -1, idxCanticoCentrale = -1;
for (int i = 0; i < listModel.size(); i++) {
    String nome = listModel.get(i).nome.toLowerCase();
    if (idxGemme == -1 && nome.contains("gemme spirituali")) idxGemme = i;
    if (idxGemme != -1 && idxCanticoCentrale == -1 && nome.contains("cantico") && i > idxGemme) {
        idxCanticoCentrale = i;
        break;
    }
}

// Se trovati entrambi, aggiungi "CONSIGLI" dopo ogni parte tra questi due indici (escludendo cantici e consigli)
if (idxGemme != -1 && idxCanticoCentrale != -1) {
    java.util.List<Integer> insertAfter = new java.util.ArrayList<>();
    for (int i = idxGemme + 1; i < idxCanticoCentrale; i++) {
        Parte parte = listModel.get(i);
        String nome = parte.nome.toLowerCase();
        if (!nome.contains("cantico") && !nome.contains("consigli") && !parte.isIntro) {
            insertAfter.add(i);
        }
    }
    // Inserisci i consigli partendo dal fondo per non sballare gli indici
    for (int j = insertAfter.size() - 1; j >= 0; j--) {
        int idx = insertAfter.get(j);
        listModel.add(idx + 1, new Parte("CONSIGLI", 60));
    }
}
// --- RIMOZIONE PARTI DOPO CANTICO FINALE O COMMENTI CONCLUSIVI ---
int idxFine1 = -1;
for (int i = 0; i < listModel.size(); i++) {
    String nome = listModel.get(i).nome.toLowerCase();
    if (nome.contains("cantico finale") || nome.contains("commenti conclusivi")) {
        idxFine1 = i;
    }
}
if (idxFine1 != -1) {
    // Mantieni solo fino a CANTICO FINALE/COMMENTI CONCLUSIVI inclusi, poi FINE
    while (listModel.size() > idxFine1 + 2) {
        listModel.remove(idxFine1 + 1);
    }
    // Assicurati che l'ultima parte sia FINE
    if (!listModel.get(listModel.size() - 1).nome.equalsIgnoreCase("FINE")) {
        listModel.addElement(new Parte("FINE", 0));
    }
}



        calcolaOrarioFineProgrammatoFisso();
        listaParti.repaint();
        JOptionPane.showMessageDialog(frame, "Importazione completata da: " + url, "Info", JOptionPane.INFORMATION_MESSAGE);
        // Nascondi il pannello freccia e scritta se l'import è andato a buon fine
        if (arrowPanel != null) {
            arrowPanel.setVisible(false);
            comboAndArrowPanel.revalidate();
            comboAndArrowPanel.repaint();
        }
    
    } catch (Exception ex) {
        String msg = ex.getMessage();
        if (msg != null && msg.contains("Read timed out")) {
            JOptionPane.showMessageDialog(frame,
                "Errore di connessione: il sito jw.org a volte blocca le richieste automatiche che non provengono direttamente da un browser.\n"
                + "Riprova, oppure assicurati di essere connesso a Internet.\n"
                + "Se il problema persiste, prova a riavviare l'applicazione, aspettare qualche minuto o a cambiare rete.",
                "Errore durante l'importazione da WOL",
                JOptionPane.ERROR_MESSAGE);
  } else {
            JOptionPane.showMessageDialog(frame, "Errore durante l'importazione da WOL: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    // --- LOG DI DEBUG: stampa la lista delle parti dopo l'importazione ---
    System.out.println("--- PARTI IMPORTATE ---");
    for (int i = 0; i < listModel.size(); i++) {
        Parte p = listModel.get(i);
        System.out.println((i+1) + ". " + p.nome + " - " + Parte.formatTime(p.durataSecondi));
    }
    System.out.println("-----------------------");
    SwingUtilities.invokeLater(() -> {
        listaScroll.revalidate();
        listaScroll.repaint();
        aggiornaAltezzaCelleLista();
    });}
private static Color blendColor(Color c1, Color c2, float ratio) {
    int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
    int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
    int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
    int a = (int) (c1.getAlpha() * (1 - ratio) + c2.getAlpha() * ratio);
    return new Color(r, g, b, a);
}

private void mostraReportFinale() {
    String[] colonne = {"Parte", "Durata Prevista", "Durata Effettiva", "Anticipo/Ritardo"};
    Object[][] dati = new Object[listModel.size()][4];
    LocalTime orario = orarioInizioAdunanza;
    for (int i = 0; i < listModel.size(); i++) {
        Parte parte = listModel.get(i);
        String nome = parte.nome;
        String durataPrevista = Parte.formatTime(parte.durataSecondi);
        String durataEffettiva = parte.tempoEffettivo != null ? Parte.formatTime(parte.tempoEffettivo.intValue()) : "-";
        String diff = "-";
        if (parte.tempoEffettivo != null) {
            int diffSec = parte.tempoEffettivo.intValue() - parte.durataSecondi;
            if (diffSec > 0) diff = "+" + Parte.formatTime(diffSec);
            else if (diffSec < 0) diff = Parte.formatTime(diffSec);
            else diff = "0";
        }
        dati[i][0] = nome;
        dati[i][1] = durataPrevista;
        dati[i][2] = durataEffettiva;
        dati[i][3] = diff;
        orario = orario.plusSeconds(parte.durataSecondi);
    }
    JTable table = new JTable(dati, colonne);
    JScrollPane scroll = new JScrollPane(table);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.setFillsViewportHeight(true);

    JButton exportBtn = new JButton("Esporta CSV");
    exportBtn.addActionListener(e -> esportaReportCSV(dati, colonne));

    JPanel panel = new JPanel(new BorderLayout(8,8));
    panel.add(scroll, BorderLayout.CENTER);
    panel.add(exportBtn, BorderLayout.SOUTH);

    JDialog dialog = new JDialog(frame, "Report finale riunione", true);
    dialog.setContentPane(panel);
    dialog.setSize(700, 400);
    dialog.setLocationRelativeTo(frame);
    dialog.setVisible(true);
}

private void esportaReportCSV(Object[][] dati, String[] colonne) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Salva report come CSV");
    if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }
        try (PrintWriter pw = new PrintWriter(file, "UTF-8")) {
            // Intestazione
            for (int i = 0; i < colonne.length; i++) {
                pw.print(colonne[i]);
                if (i < colonne.length - 1) pw.print(";");
            }
            pw.println();
            // Dati
            for (Object[] row : dati) {
                for (int i = 0; i < row.length; i++) {
                    pw.print(row[i] != null ? row[i].toString() : "");
                    if (i < row.length - 1) pw.print(";");
                }
                pw.println();
            }
            JOptionPane.showMessageDialog(frame, "Report esportato con successo!", "Esporta CSV", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore durante l'esportazione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
// --- AGGIUNTA: programma Congresso di Zona 2025 ---
private void caricaAdunanzaCongressoZona2025() {
    String[] giorni = {"Venerdì", "Sabato", "Domenica"};
    String giorno = (String) JOptionPane.showInputDialog(
        frame,
        "Scegli il giorno del congresso:",
        "Seleziona Giorno",
        JOptionPane.QUESTION_MESSAGE,
        null,
        giorni,
        giorni[0]
    );
    if (giorno == null) return; // Utente ha annullato
    listModel.clear();
    orarioInizioAdunanza = LocalTime.of(9, 20); // Imposta inizio alle 09:20 per il congresso
    listModel.addElement(new Parte("INIZIO", 0));
    if (giorno.equals("Venerdì")) {
        // Mattina
        listModel.addElement(new Parte("Video musicale", 10 * 60)); // 9:20-9:30
        listModel.addElement(new Parte("Cantico 74 e preghiera", 10 * 60)); // 9:30-9:40
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("DISCORSO DEL PRESIDENTE: Che cos'è la pura adorazione?", 30 * 60)); // 9:40-10:10
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("VIDEORACCONTO: La buona notizia secondo Gesù (Episodio 2) - 'Questo è mio Figlio' (Parte 1)", 30 * 60)); // 10:10-10:40
        listModel.addElement(new Parte("Cantico 122 e annunci", 10 * 60)); // 10:40-10:50
        listModel.addElement(new Parte("INTRODUZIONE SIMPOSIO", 15, true));
        // Simposio: 10:50-11:45 (55 min, 3 parti)
        int durataSimposio1 = 55 * 60 / 3;
        listModel.addElement(new Parte("SIMPOSIO: Le profezie messianiche e il loro adempimento (Parte 1) - Riconosciuto da Dio come Figlio", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Le profezie messianiche e il loro adempimento (Parte 1) - Discendente del re Davide", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Le profezie messianiche e il loro adempimento (Parte 1) - Unto come Messia e Condottiero", durataSimposio1));
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("Chi governa veramente il mondo?", 30 * 60)); // 11:45-12:15
        listModel.addElement(new Parte("Cantico 22 e intervallo", 80 * 60)); // 12:15-13:35 (intervallo lungo)
        // Pomeriggio
        listModel.addElement(new Parte("Video musicale", 10 * 60)); // 13:35-13:45
        listModel.addElement(new Parte("Cantico 121", 5 * 60)); // 13:45-13:50
        listModel.addElement(new Parte("INTRODUZIONE SIMPOSIO", 15, true));
        // Simposio: 13:50-14:50 (60 min, 4 parti)
        int durataSimposio2 = 60 * 60 / 4;
        listModel.addElement(new Parte("SIMPOSIO: Davanti al Tentatore imitiamo Gesù - Traiamo forza dalla Parola di Dio", durataSimposio2));
        listModel.addElement(new Parte("SIMPOSIO: Davanti al Tentatore imitiamo Gesù - Non mettiamo Geova alla prova", durataSimposio2));
        listModel.addElement(new Parte("SIMPOSIO: Davanti al Tentatore imitiamo Gesù - Adoriamo solo Geova", durataSimposio2));
        listModel.addElement(new Parte("SIMPOSIO: Davanti al Tentatore imitiamo Gesù - Difendiamo la verità", durataSimposio2));
        listModel.addElement(new Parte("Cantico 97 e annunci", 10 * 60)); // 14:50-15:00
        listModel.addElement(new Parte("INTRODUZIONE  SIMPOSIO", 15, true));
        // Simposio: 15:00-16:10 (70 min, 7 parti)
        int durataSimposio3 = 70 * 60 / 7;
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal paese in cui visse Gesù - Il deserto della Giudea", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal paese in cui visse Gesù - La valle del Giordano", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal paese in cui visse Gesù - Gerusalemme", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal paese in cui visse Gesù - La Samaria", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal paese in cui visse Gesù - La Galilea", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal paese in cui visse Gesù - La Fenicia", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal paese in cui visse Gesù - La Siria", durataSimposio3));
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("Cosa vede Gesù in noi?", 35 * 60)); // 16:10-16:45
        listModel.addElement(new Parte("Cantico 34 e preghiera conclusiva", 15 * 60)); // 16:45-16:50
    } else if (giorno.equals("Sabato")) {
        // Mattina
        listModel.addElement(new Parte("Video musicale", 10 * 60)); // 9:20-9:30
        listModel.addElement(new Parte("Cantico 93 e preghiera", 10 * 60)); // 9:30-9:40
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("\"Cosa cercate?\" (Giovanni 1:38)", 10 * 60)); // 9:40-9:50
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("VIDEORACCONTO: La buona notizia secondo Gesù (Episodio 2) - 'Questo è mio Figlio' (Parte 2)", 30 * 60)); // 9:50-10:20
        listModel.addElement(new Parte("Cantico 54 e annunci", 10 * 60)); // 10:20-10:30
        listModel.addElement(new Parte("INTRODUZIONE SIMPOSIO", 15, true));
        // Simposio: 10:30-11:35 (65 min, 7 parti)
        int durataSimposio1 = 65 * 60 / 7;
        listModel.addElement(new Parte("SIMPOSIO: Imitiamo chi amava la pura adorazione - Giovanni Battista", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Imitiamo chi amava la pura adorazione - Andrea", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Imitiamo chi amava la pura adorazione - Pietro", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Imitiamo chi amava la pura adorazione - Giovanni", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Imitiamo chi amava la pura adorazione - Giacomo", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Imitiamo chi amava la pura adorazione - Filippo", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Imitiamo chi amava la pura adorazione - Natanaele", durataSimposio1));
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("BATTESIMO: Qual è il significato del battesimo?", 30 * 60)); // 11:35-12:05
        listModel.addElement(new Parte("Cantico 52 e intervallo", 90 * 60)); // 12:05-13:35
        // Pomeriggio
        listModel.addElement(new Parte("Video musicale", 10 * 60)); // 13:35-13:45
        listModel.addElement(new Parte("Cantico 36", 5 * 60)); // 13:45-13:50
        listModel.addElement(new Parte("INTRODUZIONE SIMPOSIO", 15, true));
        // Simposio: 13:50-14:20 (30 min, 3 parti)
        int durataSimposio2 = 30 * 60 / 3;
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal primo miracolo di Gesù - La compassione", durataSimposio2));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal primo miracolo di Gesù - L'umiltà", durataSimposio2));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni dal primo miracolo di Gesù - La generosità", durataSimposio2));
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("\"L'Agnello di Dio\" toglie il peccato. In che modo?", 25 * 60)); // 14:20-14:45
        listModel.addElement(new Parte("INTRODUZIONE SIMPOSIO", 15, true));
        // Simposio: 14:45-15:20 (35 min, 3 parti)
        int durataSimposio3 = 35 * 60 / 3;
        listModel.addElement(new Parte("SIMPOSIO: Le profezie messianiche e il loro adempimento (Parte 2) - Lo zelo per la casa di Geova lo consumò", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Le profezie messianiche e il loro adempimento (Parte 2) - Portò 'buone notizie ai mansueti'", durataSimposio3));
        listModel.addElement(new Parte("SIMPOSIO: Le profezie messianiche e il loro adempimento (Parte 2) - 'Una gran luce' rifulse in Galilea", durataSimposio3));
        listModel.addElement(new Parte("Cantico 117 e annunci", 10 * 60)); // 15:20-15:30
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("Portate via di qua queste cose! (Giovanni 2:13-16)", 30 * 60)); // 15:30-16:00
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("Lo rialzerò (Giovanni 2:18-22)", 35 * 60)); // 16:00-16:35
        listModel.addElement(new Parte("Cantico 75 e preghiera conclusiva", 15 * 60)); // 16:35-16:50
    } else if (giorno.equals("Domenica")) {
        // Mattina
        listModel.addElement(new Parte("Video musicale", 10 * 60)); // 9:20-9:30
        listModel.addElement(new Parte("Cantico 140 e preghiera", 10 * 60)); // 9:30-9:40
        listModel.addElement(new Parte("INTRODUZIONE SIMPOSIO", 15, true));
        // Simposio: 9:40-11:05 (85 min, 6 parti)
        int durataSimposio1 = 85 * 60 / 6;
        listModel.addElement(new Parte("SIMPOSIO: Lezioni da ciò che disse Gesù - 'A meno che non nasca d'acqua e di spirito' (Giovanni 3:3, 5)", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni da ciò che disse Gesù - 'Nessun uomo è asceso al cielo' (Giovanni 3:13)", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni da ciò che disse Gesù - 'Si espone alla luce' (Giovanni 3:19-21)", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni da ciò che disse Gesù - 'Sono io' (Giovanni 4:25, 26)", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni da ciò che disse Gesù - 'Il mio cibo' (Giovanni 4:34)", durataSimposio1));
        listModel.addElement(new Parte("SIMPOSIO: Lezioni da ciò che disse Gesù - I campi 'sono pronti per la mietitura' (Giovanni 4:35)", durataSimposio1));
        listModel.addElement(new Parte("Cantico 37 e annunci", 10 * 60)); // 11:05-11:15
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("DISCORSO PUBBLICO: Come facciamo a sapere se quello in cui crediamo è vero?", 30 * 60)); // 11:15-11:45
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("Studio Torre di Guardia", 30 * 60)); // 11:45-12:15
        listModel.addElement(new Parte("Cantico 84 e intervallo", 80 * 60)); // 12:15-13:35
        // Pomeriggio
        listModel.addElement(new Parte("Video musicale", 10 * 60)); // 13:35-13:45
        listModel.addElement(new Parte("Cantico 77", 5 * 60)); // 13:45-13:50
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("VIDEORACCONTO: La buona notizia secondo Gesù (Episodio 3) - 'Sono io'", 45 * 60)); // 13:50-14:35
        listModel.addElement(new Parte("Cantico 20 e annunci", 10 * 60)); // 14:35-14:45
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("Cosa abbiamo imparato?", 10 * 60)); // 14:45-14:55
        listModel.addElement(new Parte("INTRODUZIONE PROSSIMA PARTE", 15, true));
        listModel.addElement(new Parte("Rimaniamo nel grande tempio spirituale di Geova!", 50 * 60)); // 14:55-15:45
        listModel.addElement(new Parte("Nuova canzone e preghiera conclusiva", 15 * 60)); // 15:45-16:00
    }
    listModel.addElement(new Parte("FINE", 0));
    calcolaOrarioFineProgrammatoFisso();
}

private void importaPartiDaWOLSmart() {
    programmaImportato = true;
    int weekNum = getWOLWeekNumber2025();
    int year = LocalDate.now().getYear();
    File dir = new File("wol_schemi");
    File yearDir = new File(dir, String.valueOf(year));
    if (!yearDir.exists()) yearDir.mkdirs();
    if (!dir.exists()) dir.mkdirs();
    String fileName = "wol_schemi/" + year + "/" + year + "_" + weekNum + ".txt";
    File file = new File(fileName);
    boolean settimanaImportata = false;
    // 1. Importa SUBITO la settimana corrente se il file esiste e contiene almeno una parte
    if (file.exists() && file.length() > 0) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) found = true; break; }
            if (found) {
                caricaPartiDaFile(file);
                JOptionPane.showMessageDialog(frame, "Importazione completata", "Info", JOptionPane.INFORMATION_MESSAGE);
                settimanaImportata = true;
            }
        } catch (Exception ex) { /* ignora */ }
    }
    // 2. Se non importata, prova a scaricarla dal sito e importa solo se contiene parti
    if (!settimanaImportata) {
        try {
            String url = "https://wol.jw.org/it/wol/meetings/r6/lp-i/" + year + "/" + weekNum;
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .timeout(20000)
                .get();
            java.util.List<String> righe = new java.util.ArrayList<>();
            org.jsoup.select.Elements titoli = doc.select("h3, h2, h4, .pub-meeting-item-header, .pub-meeting-item-title");
            boolean inEfficaci = false;
            boolean inVitaCristiana = false;
            for (org.jsoup.nodes.Element titolo : titoli) {
                String nome = titolo.text().trim();
                String nomeNorm = nome.toUpperCase().replaceAll("\\s+", " ").trim();
                if (nomeNorm.equals("TESORI DELLA PAROLA DI DIO")) { righe.add(nome + ";0;true"); continue; }
                if (nomeNorm.equals("EFFICACI NEL MINISTERO")) { inEfficaci = true; righe.add(nome + ";0;true"); continue; }
                if (nomeNorm.equals("VITA CRISTIANA")) { inVitaCristiana = true; inEfficaci = false; righe.add(nome + ";0;true"); continue; }
                org.jsoup.nodes.Element nextP = null;
                org.jsoup.nodes.Element sibling = titolo.nextElementSibling();
                while (sibling != null) { nextP = sibling.selectFirst("p"); if (nextP != null) break; sibling = sibling.nextElementSibling(); }
                if (nextP != null) {
                    String testo = nextP.text().trim();
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d+)\\s*min\\)").matcher(testo);
                    if (m.find()) {
                        int durataSec = Integer.parseInt(m.group(1)) * 60;
                        String nomeParte;
                        if (inVitaCristiana) {
                            org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                            if (strong != null) {
                                nomeParte = strong.text().trim().replaceFirst("^\\d+\\.\\s*", "");
          } else {
                                nomeParte = nome.replaceFirst("^\\d+\\.\\s*", "");
                            }
                            if (!nomeParte.toUpperCase().contains("CANTICO")) {
                                righe.add("INTRO;15;true");
                            }
                        } else if (inEfficaci) {
                            org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                            nomeParte = (strong != null) ? strong.text().trim() : nome;
                        } else {
                            nomeParte = testo.substring(m.end()).trim();
                            if (nomeParte.isEmpty()) nomeParte = nome;
                        }
                        righe.add(nomeParte + ";" + durataSec + ";false");
                        continue;
                    }
                }
                org.jsoup.nodes.Element durataElem = titolo.parent().selectFirst(".pub-meeting-item-time, .pub-meeting-item-duration, .duration, .time");
                int durataSec = 0;
                if (durataElem != null) {
                    String durataTxt = durataElem.text().replaceAll("[^0-9]", "");
                    if (!durataTxt.isEmpty()) {
                        durataSec = Integer.parseInt(durataTxt) * 60;
                    }
                }
                if (!nome.isEmpty()) {
                    righe.add(nome + ";" + durataSec + ";false");
                }
            }
            if (!righe.isEmpty()) {
                try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
                    for (String riga : righe) { pw.println(riga); }
                }
                caricaPartiDaFile(file);
                JOptionPane.showMessageDialog(frame, "Importazione completata da WOL: " + fileName, "Info", JOptionPane.INFORMATION_MESSAGE);
                settimanaImportata = true;
  } else {
                JOptionPane.showMessageDialog(frame, "La settimana corrente non è ancora disponibile su WOL.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore: impossibile trovare o scaricare lo schema per questa settimana. Riprova!", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    // 3. In background scarica solo i file mancanti (ma solo se la pagina contiene almeno una parte)
    new Thread(() -> {
        java.util.List<Integer> mancanti = new java.util.ArrayList<>();
        for (int w = weekNum; w <= 52; w++) {
            String fn = "wol_schemi/" + year + "/" + year + "_" + w + ".txt";
            File f = new File(fn);
            if (!f.exists() || f.length() == 0) mancanti.add(w);
        }
        System.out.println("[WOL BG] Inizio download settimane mancanti: " + mancanti);
        for (int w : mancanti) {
            try {
                String url = "https://wol.jw.org/it/wol/meetings/r6/lp-i/" + year + "/" + w;
                System.out.println("[WOL BG] Scarico settimana " + year + "/" + w + " ...");
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .header("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .timeout(20000)
                    .get();
                java.util.List<String> righe = new java.util.ArrayList<>();
                org.jsoup.select.Elements titoli = doc.select("h3, h2, h4, .pub-meeting-item-header, .pub-meeting-item-title");
                boolean inEfficaci = false;
                boolean inVitaCristiana = false;
                for (org.jsoup.nodes.Element titolo : titoli) {
                    String nome = titolo.text().trim();
                    String nomeNorm = nome.toUpperCase().replaceAll("\\s+", " ").trim();
                    if (nomeNorm.equals("TESORI DELLA PAROLA DI DIO")) { righe.add(nome + ";0;true"); continue; }
                    if (nomeNorm.equals("EFFICACI NEL MINISTERO")) { inEfficaci = true; righe.add(nome + ";0;true"); continue; }
                    if (nomeNorm.equals("VITA CRISTIANA")) { inVitaCristiana = true; inEfficaci = false; righe.add(nome + ";0;true"); continue; }
                    org.jsoup.nodes.Element nextP = null;
                    org.jsoup.nodes.Element sibling = titolo.nextElementSibling();
                    while (sibling != null) { nextP = sibling.selectFirst("p"); if (nextP != null) break; sibling = sibling.nextElementSibling(); }
                    if (nextP != null) {
                        String testo = nextP.text().trim();
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d+)\\s*min\\)").matcher(testo);
                        if (m.find()) {
                            int durataSec = Integer.parseInt(m.group(1)) * 60;
                            String nomeParte;
                            if (inVitaCristiana) {
                                org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                                if (strong != null) {
                                    nomeParte = strong.text().trim().replaceFirst("^\\d+\\.\\s*", "");
  } else {
                                    nomeParte = nome.replaceFirst("^\\d+\\.\\s*", "");
                                }
                                if (!nomeParte.toUpperCase().contains("CANTICO")) {
                                    righe.add("INTRO;15;true");
                                }
                            } else if (inEfficaci) {
                                org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                                nomeParte = (strong != null) ? strong.text().trim() : nome;
                            } else {
                                nomeParte = testo.substring(m.end()).trim();
                                if (nomeParte.isEmpty()) nomeParte = nome;
                            }
                            righe.add(nomeParte + ";" + durataSec + ";false");
                            continue;
                        }
                    }
                    org.jsoup.nodes.Element durataElem = titolo.parent().selectFirst(".pub-meeting-item-time, .pub-meeting-item-duration, .duration, .time");
                    int durataSec = 0;
                    if (durataElem != null) {
                        String durataTxt = durataElem.text().replaceAll("[^0-9]", "");
                        if (!durataTxt.isEmpty()) {
                            durataSec = Integer.parseInt(durataTxt) * 60;
                        }
                    }
                    if (!nome.isEmpty()) {
                        righe.add(nome + ";" + durataSec + ";false");
                    }
                }
                if (!righe.isEmpty()) {
                    // FILTRO: non salvare se contiene solo voci "Articolo di studio" o "Letture supplementari"
                    boolean contieneParteVera = false;
                    for (String riga : righe) {
                        String nome = riga.split(";")[0].toLowerCase();
                        if (
                            !nome.contains("articolo di studio") &&
                            !nome.contains("letture supplementari") &&
                            !nome.matches("articolo di studio \\d+.*")
                        ) {
                            contieneParteVera = true;
                            break;
                        }
                    }
                    if (!righe.isEmpty() && contieneParteVera) {
                        try (java.io.PrintWriter pw = new java.io.PrintWriter(new File("wol_schemi/" + year + "/" + year + "_" + w + ".txt"))) {
                            for (String riga : righe) { pw.println(riga); }
                        }
                        System.out.println("[WOL BG] Scaricata settimana " + year + "/" + w + " con " + righe.size() + " parti.");
                    } else {
                        System.out.println("[WOL BG] Settimana " + year + "/" + w + " NON valida (solo articoli/letture), file NON salvato.");
                    }
                } else {
                    System.out.println("[WOL BG] Settimana " + year + "/" + w + " NON disponibile, nessuna parte trovata.");
                }
            } catch (Exception ex) {
                System.out.println("[WOL BG] Errore su settimana " + year + "/" + w + ": " + ex.getMessage());
                
            }
        }
        System.out.println("[WOL BG] Download settimane mancanti completato.");
    }).start();
}

private void caricaPartiDaFile(File file) {
    programmaImportato = true;

    listModel.clear();
    java.util.List<Parte> partiTemp = new java.util.ArrayList<>();
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
          String line;
          while ((line = br.readLine()) != null) {
            // Qui decidi il formato: es. "NOME;DURATA;INTRO"
              String[] parts = line.split(";");
            if (parts.length >= 2) {
                  String nome = parts[0];
                  int durata = Integer.parseInt(parts[1]);
                boolean isIntro = parts.length > 2 && Boolean.parseBoolean(parts[2]);
                partiTemp.add(new Parte(nome, durata, isIntro));
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, "Errore lettura file locale: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
    }
    // Rimuovi le ultime due righe se sono "Articolo di studio" o "Letture supplementari"
    for (int i = 0; i < 2; i++) {
        if (!partiTemp.isEmpty()) {
            Parte ultima = partiTemp.get(partiTemp.size() - 1);
            String nome = ultima.nome.toLowerCase();
            if (nome.contains("articolo di studio") || nome.contains("letture supplementari")) {
                partiTemp.remove(partiTemp.size() - 1);
            }
        }
    }

    

    // La prima parte deve essere sempre INIZIO
if (!partiTemp.isEmpty()) {
    partiTemp.set(0, new Parte("INIZIO", 0));
}
    
    for (Parte p : partiTemp) listModel.addElement(p);
    // Inserisci CONSIGLI dopo ogni parte tra EFFICACI NEL MINISTERO e VITA CRISTIANA
int idxEffMin = -1, idxVitaCrist = -1;
  for (int i = 0; i < listModel.size(); i++) {
    String nome = listModel.get(i).nome.toUpperCase();
    if (idxEffMin == -1 && nome.contains("EFFICACI NEL MINISTERO")) idxEffMin = i;
    if (idxEffMin != -1 && idxVitaCrist == -1 && nome.contains("VITA CRISTIANA")) {
        idxVitaCrist = i;
        break;
    }
}
if (idxEffMin != -1 && idxVitaCrist != -1) {
    java.util.List<Integer> insertAfter = new java.util.ArrayList<>();
    for (int i = idxEffMin + 1; i < idxVitaCrist; i++) {
              Parte parte = listModel.get(i);
        String nome = parte.nome.toUpperCase();
        if (
            !nome.contains("CANTICO") &&
            !nome.contains("CONSIGLI") &&
            !parte.isIntro
        ) {
            insertAfter.add(i);
        }
    }
    // Inserisci i consigli partendo dal fondo per non sballare gli indici
    for (int j = insertAfter.size() - 1; j >= 0; j--) {
        int idx = insertAfter.get(j);
        listModel.add(idx + 1, new Parte("CONSIGLI", 60));
    }
}

// 1. Imposta la durata dei cantici
int canticoCount = 0;
  for (int i = 0; i < listModel.size(); i++) {
      Parte parte = listModel.get(i);
    String nome = parte.nome.toUpperCase();
    if (nome.contains("CANTICO")) {
        canticoCount++;
        if (canticoCount == 1) parte.durataSecondi = 6 * 60;
        else if (canticoCount == 2) parte.durataSecondi = 5 * 60;
        else if (canticoCount == 3) parte.durataSecondi = 8 * 60;
        listModel.set(i, parte);
    }
}

// 2. Trova le parti di TESORI DELLA PAROLA DI DIO (escludendo la sezione stessa)
int idxTesori = -1;
java.util.List<Integer> idxPartiTesori = new java.util.ArrayList<>();
  for (int i = 0; i < listModel.size(); i++) {
    String nome = listModel.get(i).nome.toUpperCase();
    if (idxTesori == -1 && nome.contains("TESORI DELLA PAROLA DI DIO")) idxTesori = i;
    else if (idxTesori != -1 && idxPartiTesori.size() < 3 && !nome.contains("INTRO") && !nome.contains("TESORI") && !nome.contains("CANTICO")) {
        idxPartiTesori.add(i);
    }
}

// 3. Aggiungi INTRO dopo la seconda e la terza parte, e CONSIGLI dopo la terza
if (idxPartiTesori.size() >= 3) {
    // Lettura Biblica: rinomina la terza parte
    Parte terza = listModel.get(idxPartiTesori.get(2));
    terza.nome = "Lettura Biblica: " + terza.nome;
    listModel.set(idxPartiTesori.get(2), terza);

    // Inserisci INTRO dopo la seconda parte
    listModel.add(idxPartiTesori.get(0) + 1, new Parte("INTRO", 15, true));
    // Inserisci INTRO dopo la terza parte (l'indice si sposta di 1 per l'INTRO precedente)
    listModel.add(idxPartiTesori.get(1) + 2, new Parte("INTRO", 15, true));
    // Inserisci CONSIGLI dopo la terza parte (l'indice si sposta di 2 per i due INTRO)
    listModel.add(idxPartiTesori.get(2) + 3, new Parte("CONSIGLI", 60));
}
    listModel.addElement(new Parte("FINE", 0));
    calcolaOrarioFineProgrammatoFisso();
    listaParti.repaint();
    // Nascondi il pannello freccia e scritta se l'import è andato a buon fine
    if (arrowPanel != null) {
        arrowPanel.setVisible(false);
        comboAndArrowPanel.revalidate();
        comboAndArrowPanel.repaint();
    }

// ... existing code ...
System.out.println("currentProgramIndex: " + currentProgramIndex);
// Se il programma selezionato è VISITA SORVEGLIANTE - Vita Cristiana e Ministero, aggiorna struttura finale
if (currentProgramIndex == 1) {
    for (int i = 0; i < listModel.size(); i++) {
        Parte parte = listModel.get(i);
        String nomePulito = parte.nome.trim().toLowerCase();
        if (nomePulito.contains("studio biblico di congregazione")) {
            // 1. Rinomina la parte in DISCORSO SORVEGLIANTE
            Parte sostituita = new Parte("Discorso Sorvegliante", parte.durataSecondi, parte.isIntro);
            listModel.set(i, sostituita);
            // 2. Modifica la parte precedente se è INTRODUZIONE PROSSIMA PARTE
            if (i > 0) {
                Parte prev = listModel.get(i-1);
                if (prev.nome.trim().toLowerCase().contains("intro")) {
                    listModel.set(i-1, new Parte("Commenti Conclusivi e Introduzione Sorvegliante", prev.durataSecondi, prev.isIntro));
                }
            }
            // 3. Modifica la parte successiva in CANTICO FINALE N. (mantieni numero se già presente)
            if (i+1 < listModel.size()) {
                Parte dopo = listModel.get(i+1);
                String nomeCantico = dopo.nome;
                String num = "";
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?i)cantico[^\\d]*(\\d+)").matcher(nomeCantico);
                if (m.find()) num = m.group(1);
                String nuovoNome = "Cantico " + (num.isEmpty() ? "" : num);
                listModel.set(i+1, new Parte(nuovoNome, dopo.durataSecondi, dopo.isIntro));
            }
            break;
        }
    }
}
}

private boolean scaricaSettimanaWOL(int year, int week, File fileDest) {
    int tentativi = 0;
    while (tentativi < 3) {
        try {
            String url = "https://wol.jw.org/it/wol/meetings/r6/lp-i/" + year + "/" + week;
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .timeout(20000)
                .get();
            java.util.List<String> righe = new java.util.ArrayList<>();
            org.jsoup.select.Elements titoli = doc.select("h3, h2, h4, .pub-meeting-item-header, .pub-meeting-item-title");
            boolean inEfficaci = false;
            boolean inVitaCristiana = false;
            for (org.jsoup.nodes.Element titolo : titoli) {
                String nome = titolo.text().trim();
                String nomeNorm = nome.toUpperCase().replaceAll("\\s+", " ").trim();
                if (nomeNorm.equals("TESORI DELLA PAROLA DI DIO")) { righe.add(nome + ";0;true"); continue; }
                if (nomeNorm.equals("EFFICACI NEL MINISTERO")) { inEfficaci = true; righe.add(nome + ";0;true"); continue; }
                if (nomeNorm.equals("VITA CRISTIANA")) { inVitaCristiana = true; inEfficaci = false; righe.add(nome + ";0;true"); continue; }
                org.jsoup.nodes.Element nextP = null;
                org.jsoup.nodes.Element sibling = titolo.nextElementSibling();
                while (sibling != null) { nextP = sibling.selectFirst("p"); if (nextP != null) break; sibling = sibling.nextElementSibling(); }
                if (nextP != null) {
                    String testo = nextP.text().trim();
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d+)\\s*min\\)").matcher(testo);
                    if (m.find()) {
                        int durataSec = Integer.parseInt(m.group(1)) * 60;
                        String nomeParte;
                        if (inVitaCristiana) {
                            org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                            if (strong != null) {
                                nomeParte = strong.text().trim().replaceFirst("^\\d+\\.\\s*", "");
          } else {
                                nomeParte = nome.replaceFirst("^\\d+\\.\\s*", "");
                            }
                            if (!nomeParte.toUpperCase().contains("CANTICO")) {
                                righe.add("INTRO;15;true");
                            }
                        } else if (inEfficaci) {
                            org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                            nomeParte = (strong != null) ? strong.text().trim() : nome;
                        } else {
                            nomeParte = testo.substring(m.end()).trim();
                            if (nomeParte.isEmpty()) nomeParte = nome;
                        }
                        righe.add(nomeParte + ";" + durataSec + ";false");
                        continue;
                    }
                }
                org.jsoup.nodes.Element durataElem = titolo.parent().selectFirst(".pub-meeting-item-time, .pub-meeting-item-duration, .duration, .time");
                int durataSec = 0;
                if (durataElem != null) {
                    String durataTxt = durataElem.text().replaceAll("[^0-9]", "");
                    if (!durataTxt.isEmpty()) {
                        durataSec = Integer.parseInt(durataTxt) * 60;
                    }
                }
                if (!nome.isEmpty()) {
                    righe.add(nome + ";" + durataSec + ";false");
                }
            }
            // FILTRO: non salvare se contiene solo voci "Articolo di studio" o "Letture supplementari"
            boolean contieneParteVera = false;
            for (String riga : righe) {
                String nome = riga.split(";")[0].toLowerCase();
                if (!nome.contains("articolo di studio") && !nome.contains("letture supplementari") && !nome.matches("articolo di studio \\d+.*")) {
                    contieneParteVera = true;
                    break;
                }
            }
            if (!righe.isEmpty() && contieneParteVera) {
                try (java.io.PrintWriter pw = new java.io.PrintWriter(fileDest)) {
                    for (String riga : righe) { pw.println(riga); }
                }
                System.out.println("[WOL BG] Scaricata settimana " + year + "/" + week + " con " + righe.size() + " parti.");
                return true;
            } else {
                System.out.println("[WOL BG] Settimana " + year + "/" + week + " NON valida (solo articoli/letture), file NON salvato.");
                return false;
          }
      } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Read timed out")) {
                tentativi++;
                System.out.println("[WOL BG] Timeout su settimana " + year + "/" + week + ", ritento (tentativo " + tentativi + ")");
                try { Thread.sleep(2000); } catch (InterruptedException ie) { }
                continue;
            } else {
                System.out.println("[WOL BG] Errore su settimana " + year + "/" + week + ": " + ex.getMessage());
                return false;
            }
        }
    }
    return false;
}

private void importaPartiDaHtmlWOL_ESalvaTutteLeSettimane(int year) {
    File dir = new File("wol_schemi");
    if (!dir.exists()) dir.mkdirs();
    for (int week = 20; week <= 52; week++) { // Modificato: parte da settimana 20
        String fileName = "wol_schemi/" + year + "_" + week + ".txt";
        File file = new File(fileName);
        if (file.exists()) continue; // Salta se già presente
        try {
            String url = "https://wol.jw.org/it/wol/meetings/r6/lp-i/" + year + "/" + week;
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .timeout(20000)
                .get();
            java.util.List<String> righe = new java.util.ArrayList<>();
            org.jsoup.select.Elements titoli = doc.select("h3, h2, h4, .pub-meeting-item-header, .pub-meeting-item-title");
            boolean inEfficaci = false;
            boolean inVitaCristiana = false;
            for (org.jsoup.nodes.Element titolo : titoli) {
                String nome = titolo.text().trim();
                String nomeNorm = nome.toUpperCase().replaceAll("\\s+", " ").trim();
                if (nomeNorm.equals("TESORI DELLA PAROLA DI DIO")) {
                    righe.add(nome + ";0;true");
                    continue;
                }
                if (nomeNorm.equals("EFFICACI NEL MINISTERO")) {
                    inEfficaci = true;
                    righe.add(nome + ";0;true");
                    continue;
                }
                if (nomeNorm.equals("VITA CRISTIANA")) {
                    inVitaCristiana = true;
                    inEfficaci = false;
                    righe.add(nome + ";0;true");
                    continue;
                }
                org.jsoup.nodes.Element nextP = null;
                org.jsoup.nodes.Element sibling = titolo.nextElementSibling();
                while (sibling != null) {
                    nextP = sibling.selectFirst("p");
                    if (nextP != null) break;
                    sibling = sibling.nextElementSibling();
                }
                if (nextP != null) {
                    String testo = nextP.text().trim();
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d+)\\s*min\\)").matcher(testo);
                    if (m.find()) {
                        int durataSec = Integer.parseInt(m.group(1)) * 60;
                        String nomeParte;
                        boolean isIntro = false;
                        if (inVitaCristiana) {
                            org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                            if (strong != null) {
                                nomeParte = strong.text().trim().replaceFirst("^\\d+\\.\\s*", "");
                            } else {
                                nomeParte = nome.replaceFirst("^\\d+\\.\\s*", "");
                            }
                            if (!nomeParte.toUpperCase().contains("CANTICO")) {
                                righe.add("INTRO;15;true");
                            }
                        } else if (inEfficaci) {
                            org.jsoup.nodes.Element strong = titolo.selectFirst("strong");
                            nomeParte = (strong != null) ? strong.text().trim() : nome;
                        } else {
                            nomeParte = testo.substring(m.end()).trim();
                            if (nomeParte.isEmpty()) nomeParte = nome;
                        }
                        righe.add(nomeParte + ";" + durataSec + ";false");
                        continue;
                    }
                }
                org.jsoup.nodes.Element durataElem = titolo.parent().selectFirst(".pub-meeting-item-time, .pub-meeting-item-duration, .duration, .time");
                int durataSec = 0;
                if (durataElem != null) {
                    String durataTxt = durataElem.text().replaceAll("[^0-9]", "");
                    if (!durataTxt.isEmpty()) {
                        durataSec = Integer.parseInt(durataTxt) * 60;
                    }
                }
                if (!nome.isEmpty()) {
                    righe.add(nome + ";" + durataSec + ";false");
                }
            }
            // Salva su file
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
                for (String riga : righe) {
                    pw.println(riga);
                }
            }
        } catch (Exception ex) {
            // Ignora errori per settimane non disponibili
        }
    }
}

}

class RoundedBorder extends AbstractBorder {
private int radius;
private Color color;
public RoundedBorder(int radius, Color color) {
   this.radius = radius;
   this.color = color;
}

@Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

@Override
public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
   Graphics2D g2 = (Graphics2D) g.create();
   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   g2.setColor(color);
   g2.setStroke(new BasicStroke(3));
   g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
   g2.dispose();
}



}