package jp.ryans.minesweeper;

import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

/**
 * マインスイーパ　
 */
public class Minesweeper extends Thread implements BombsPanel.BombsPanelAction {

	private JFrame frame;

	private final Action action = new SwingAction();

	private BombsPanel bpanel;

	private DialogWindow rankFrame;

	private JButton btnStartButton;
	private JSpinner textColumn;
	private JSpinner textRow;
	private JSpinner textBombs;
	private JLabel label_loss;
	private JLabel label_win;
	private JLabel label_3;
	private JLabel timer_label;
	private JCheckBox checkBox;
	private Timer t;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuRank;
	private JMenu levelMenu;
	private JMenuItem lowMenuItem;
	private JMenuItem middleMenuItem;
	private JMenuItem highMenuItem;

	public static boolean isMac() {
		return (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// MacOSXでのJava実行環境用のシステムプロパティの設定.
		if (isMac()) {
			// JFrameにメニューをつけるのではなく、一般的なOSXアプリ同様に画面上端のスクリーンメニューにする.
			System.setProperty("apple.laf.useScreenMenuBar", "true");

		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Minesweeper window = new Minesweeper();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Minesweeper() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("マインスイーパー");
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frame.setResizable(false);
		frame.setBounds(100, 100, 350, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		bpanel = new BombsPanel();
		bpanel.setBounds(12, 113, 36, 36);
		bpanel.setBombs(1);
		bpanel.setRow(1);
		bpanel.setColumn(1);
		bpanel.setVisible(false);
		frame.getContentPane().setLayout(null);
		bpanel.setBackground(Color.WHITE);
		bpanel.setBombsPanelAction(this);
		frame.getContentPane().add(bpanel);

		btnStartButton = new JButton("ゲーム開始");
		btnStartButton.setBounds(12, 67, 127, 32);
		btnStartButton.setFont(new Font("メイリオ", Font.PLAIN, 10));
		btnStartButton.setEnabled(false);
		btnStartButton.setAction(action);
		frame.getContentPane().add(btnStartButton);

		textColumn = new JSpinner();
		textColumn.setBounds(43, 25, 37, 32);
		textColumn.setValue(10);
		frame.getContentPane().add(textColumn);

		textRow = new JSpinner();
		textRow.setBounds(123, 25, 37, 32);
		textRow.setValue(10);
		frame.getContentPane().add(textRow);

		textBombs = new JSpinner();
		textBombs.setBounds(196, 24, 44, 33);
		textBombs.setValue(10);
		frame.getContentPane().add(textBombs);

		JLabel label_1 = new JLabel("行");
		label_1.setBounds(92, 23, 19, 35);
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		frame.getContentPane().add(label_1);

		JLabel label = new JLabel("列");
		label.setBounds(12, 23, 19, 35);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		frame.getContentPane().add(label);

		JLabel label_2 = new JLabel("爆弾");
		label_2.setBounds(157, 22, 32, 35);
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		frame.getContentPane().add(label_2);

		label_loss = new JLabel("");
		label_loss.setBounds(248, 25, 32, 32);
		label_loss.setVisible(false);
		label_loss.setIcon(new ImageIcon(Minesweeper.class.getResource("/resource/loss.png")));
		frame.getContentPane().add(label_loss);

		label_win = new JLabel("");
		label_win.setBounds(292, 25, 32, 32);
		label_win.setVisible(false);
		label_win.setIcon(new ImageIcon(Minesweeper.class.getResource("/resource/win.png")));
		frame.getContentPane().add(label_win);

		checkBox = new JCheckBox(new ImageIcon(Minesweeper.class.getResource("/resource/bom.png ")));
		checkBox.setBounds(180, 64, 34, 34);
		checkBox.setAction(action);
		frame.getContentPane().add(checkBox);

		label_3 = new JLabel("残り");
		label_3.setBounds(222, 75, 44, 13);
		frame.getContentPane().add(label_3);
		bpanel.setRemaining(label_3);

		timer_label = new JLabel("000");
		timer_label.setBounds(151, 71, 24, 21);
		frame.getContentPane().add(timer_label);
		t.setLabel(timer_label);

		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 350, 21);
		frame.getContentPane().add(menuBar);

		menu = new JMenu("記録");
		menuBar.add(menu);

		menuRank = new JMenuItem("ランキング");
		menuRank.setEnabled(false);
		menuRank.setAction(action);
		menuRank.setText("ランキング");
		menu.add(menuRank);

		levelMenu = new JMenu("難易度");
		menuBar.add(levelMenu);

		lowMenuItem = new JMenuItem("");
		lowMenuItem.setEnabled(false);
		lowMenuItem.setAction(action);
		lowMenuItem.setText("初級（10×10)");
		levelMenu.add(lowMenuItem);

		middleMenuItem = new JMenuItem("");
		middleMenuItem.setEnabled(false);
		middleMenuItem.setAction(action);
		middleMenuItem.setText("中級（16×16)");
		levelMenu.add(middleMenuItem);

		highMenuItem = new JMenuItem("");
		highMenuItem.setEnabled(false);
		highMenuItem.setAction(action);
		highMenuItem.setText("上級（22×22)");
		levelMenu.add(highMenuItem);


		//タイマーのスタート
		t.start();

	}
	/**
	 * スイングアクション
	 * @author t-agarie
	 *
	 */

	/**
	 * アクション
	 * @author ryan
	 *
	 */
	private class SwingAction extends AbstractAction {




		public SwingAction(){
			putValue(NAME,"ゲーム開始");
			t = new Timer();
		}
		/**
		 * 警告ダイアログの表示
		 * @param msg
		 */
		public void dialog(String msg){
			JOptionPane.showMessageDialog(frame, msg);
			btnStartButton.setEnabled(true);
		}

		private int getJSpinnerTointeger(JSpinner spin){
			Object result = spin.getValue();
			if(result instanceof Integer){
				return (int) result;
			}else{
				return 0;
			}
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			if (obj == btnStartButton) {
				JButton bt = (JButton) obj;
				bt.setEnabled(false);
				try{
					int c = getJSpinnerTointeger(textColumn);
					int r = getJSpinnerTointeger(textRow);
					int b = getJSpinnerTointeger(textBombs);
					//入力された数値が0以下の場合
					if( c <= 0 || r <= 0 ){
						dialog( "列と行には１以上の数値を入力してください");
						return;
					}
					//爆弾の数がマスより多い場合
					if( b > c * r){
						dialog("爆弾の数が多すぎます");
						return;
					}
					bpanel.setColumn(c);
					bpanel.setRow(r);
					bpanel.setBombs(b);
				}catch(NullPointerException npe){
					//数値以外が入力された場合
						dialog( "実数値を入力してください");
						return;
					}
				t.startTime();
				textColumn.setEnabled(false);
				textRow.setEnabled(false);
				textBombs.setEnabled(false);
				label_3.setText("");
				bpanel.reStart();
				Rectangle x = bpanel.getBounds();
				if ( x.width < 320){
					x.width = 320;
				}
				frame.setBounds(0,0,x.width + 30,x.height + 150);
				frame.setLocationRelativeTo(null);
				frame.repaint();
				bpanel.setVisible(true);
				bpanel.setFlag(false);
				checkBox.setSelected(false);
				checkBox.setIcon(new ImageIcon(Minesweeper.class.getResource("/resource/bom.png")));
			}
			if(obj instanceof JCheckBox){
				JCheckBox ch = (JCheckBox) obj;
				if( ch.isSelected()){
					ch.setIcon(new ImageIcon(Minesweeper.class.getResource("/resource/flag.png")));
					bpanel.setFlag(true);
				}else{
					ch.setIcon(new ImageIcon(Minesweeper.class.getResource("/resource/bom.png")));
					bpanel.setFlag(false);
				}
			}
			if(obj == menuRank){
				rankFrame = new DialogWindow();
				rankFrame.setVisible(true);
				rankFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			}
			if(obj == lowMenuItem){
				setLevel(10,10,10);
			}
			if(obj == middleMenuItem){
				setLevel(16,16,40);
			}
			if(obj == highMenuItem){
				setLevel(22,22,100);
			}
		}
	}

	/**
	 * 難易度調整
	 * @param columnObject 行数
	 * @param rowObject 列数
	 * @param bombsObject 爆弾数
	 */
	public void setLevel(Object columnObject,Object rowObject,Object bombsObject){
		if(textColumn.isEnabled() != false){
			textColumn.setValue(columnObject);
			textRow.setValue(rowObject);
			textBombs.setValue(bombsObject);
		}
	}
	/**
	 * ファイルへ記録する
	 * @throws IOException
	 */
	public void fileWrite() throws IOException {
		File f = new File("クリアタイム.txt");
		/*
		FileOutputStream fs = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(fs);
		BufferedWriter bw = new BufferedWriter(osw);


		Date now = new Date();
		SimpleDateFormat sdt = new SimpleDateFormat("  yyyy/MM/dd HH:mm:ss");
		String s = sdt.format(now);
		*/

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("  yyyy/MM/dd HH:mm:ss");
		String s = now.format(dtf);


		FileOutputStream fos = new FileOutputStream(f,true);
		OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		String str = String.format("%3d",t.getTime());		//時間
		String c = Integer.toString(bpanel.getColumn());	//行
		String r = Integer.toString(bpanel.getRow());		//列
		String b = Integer.toString(bpanel.getBombs());	//爆弾

		bw.write(str + "秒" + s);
		bw.write("  縦" + c +"マス" + "横" + r +"マス" + "爆弾" + b);
		bw.newLine();
		bw.flush();
		bw.close();
	}

	@Override
	public void onPreviewGameStart(BombsPanel panel) {
		label_win.setVisible(false);
		label_loss.setVisible(false);
	}

	@Override
	public void onGameEnd(BombsPanel panel,boolean result)  {
		btnStartButton.setEnabled(true);
		t.stopTime();
		if( result ) {
			label_win.setVisible(true);
			textColumn.setEnabled(true);
			textRow.setEnabled(true);
			textBombs.setEnabled(true);
			try {
				fileWrite();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			label_loss.setVisible(true);
			textColumn.setEnabled(true);
			textRow.setEnabled(true);
			textBombs.setEnabled(true);
		}
	}
}
