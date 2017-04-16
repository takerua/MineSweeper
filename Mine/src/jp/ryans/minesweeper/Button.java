/**
 *
 */
package jp.ryans.minesweeper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import jp.ryans.minesweeper.BombsPanel.ClickButtonAction;

/**
 * 爆弾ボタン
 * <br>爆弾パネルに依存するので単独での使用はできない
 * @author Ryan's Factory
 *
 */
public final class Button extends JComponent  {

	private final class ButtonAction extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			if (obj instanceof JButton) {
				JButton bt = (JButton) obj;
				Button b = (Button) bt.getParent();
				BombsPanel p = (BombsPanel) b.getParent();
				//int m = e.getModifiers();
				if ( p.getFlag()) {
					if(null == bt.getIcon()){
						bt.setIcon(new ImageIcon(Minesweeper.class.getResource("/resource/flag.png")));
					}else{
						bt.setIcon(null);
					}
				} else {
					if(null == bt.getIcon()){
					bt.setEnabled(false);
					bt.setVisible(false);
						if (null != callback) {
							callback.onClickCallBack((Button) bt.getParent());
						}
					}
				}
				int sam = 0;
				for  (Component v : p.getComponents()) {
					((Button) v).isFrag();
					if(((Button) v).isFrag()){
						sam++;
					}
				}
				p.getRemaining().setText("" + sam);
			}
		}
	}

	public static int MIN_WIDTH = 34;

	public static int MIN_HEIGHT = 34;

	private static int BORDER_HEIGHT = 1;

	private static final int BUTTON = 0;

	private static final int LABEL = 1;

	public static final int BOMBS = -1;

	public static final int EMPTY = 0;

	private ImageIcon bomIcon = null;

	private final Action action;

	private int index = 0;

	private int status = EMPTY;

	private ClickButtonAction callback;

	private Labellistener labelaction;


	/**
	 *
	 */
	public Button() {
		super();
		action = new ButtonAction();
		JButton bt = new JButton("");
		bt.setAction(action);
		this.add(bt, BUTTON);
		JLabel jl = new JLabel(" ", bomIcon, JLabel.CENTER);
		labelaction = new Labellistener();
		jl.addMouseListener(labelaction);
		add(jl,LABEL);
		((JLabel) this.getComponent(LABEL)).setHorizontalTextPosition(JLabel.CENTER);
		((JLabel) this.getComponent(LABEL)).setOpaque(true);
		((JLabel) this.getComponent(LABEL)).setBackground(Color.WHITE);
		setBackground(Color.WHITE);
		setBorder(new LineBorder(new Color(200, 200, 200), BORDER_HEIGHT));
	}

	public void closePanel() {
		this.getComponent(BUTTON).setEnabled(true);
		this.getComponent(BUTTON).setVisible(true);
	}

	public int getBombs() {
		return this.status;
	}

	public int getIndex() {
		return this.index;
	}

	public int incrementStatus() {
		if (!isBombs()) {
			status++;
			((JLabel) this.getComponent(LABEL)).setText("" + status);
		}
		return status;
	}

	public boolean isBombs() {
		if (BOMBS == this.status) {
			return true;
		}
		return false;
	}

	public boolean isEmpty() {
		if (EMPTY == this.status) {
			return true;
		}
		return false;
	}

	public boolean isFrag(){
		JButton bt = (JButton) this.getComponent(BUTTON);
		if (null == bt.getIcon()){
			return false;
		}
		return true;
	}

	public boolean isOpenPanel() {
		if (((JButton) this.getComponent(BUTTON)).isVisible()) {
			return false;
		}
		return true;
	}

	public void openPanel() {
		this.getComponent(BUTTON).setEnabled(false);
		this.getComponent(BUTTON).setVisible(false);
	}
/*
	public void clickPanel(){
		((AbstractButton) this.getComponent(BUTTON)).doClick();
	}
	*/

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		this.getComponent(LABEL).setBackground(bg);
	}

	private void setBombs(boolean flg) {
		JLabel label = (JLabel) this.getComponent(LABEL);
		String text = " ";
		if (EMPTY < this.status) {
			text = "" + this.status;
		}
		label.setText(text);
		if (flg) {
			label.setIcon(bomIcon);
		} else {
			label.setIcon(null);
		}
	}

	public void setBombs(int bombs) {
		this.status = bombs;
		if (BOMBS == this.status) {
			setBombs(true);
		} else {
			setBombs(false);
		}
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		int w = width - (BORDER_HEIGHT * 2);
		int h = height - (BORDER_HEIGHT * 2);
		this.getComponent(BUTTON).setBounds(BORDER_HEIGHT, BORDER_HEIGHT, w, h);
		this.getComponent(LABEL).setBounds(BORDER_HEIGHT, BORDER_HEIGHT, w, h);
	}

	@Override
	public void setBounds(Rectangle r) {
		this.setBounds(r.x, r.y, r.width, r.height);
	}

	public void setCallback(ClickButtonAction callback) {
		this.callback = callback;
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		this.getComponent(BUTTON).setFont(font);
		this.getComponent(LABEL).setFont(font);
	}

	public void setIcon(ImageIcon icon) {
		this.bomIcon = icon;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
