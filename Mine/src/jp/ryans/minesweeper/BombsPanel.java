/**
 *
 */
package jp.ryans.minesweeper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/**
 * 爆弾パネル マインスィーパのゲーム盤
 *
 * @author Ryan's Factory
 *
 */
public class BombsPanel extends JComponent {
	/**
	 * ゲームのアクション
	 * @author Ryan's Factory
	 */
	public interface BombsPanelAction {
		/**
		 * ゲームが終了した
		 * @param panel 爆弾パネル
		 * @param result ゲームの結果。trueの場合は勝利、falseの場合は負け
		 */
		public void onGameEnd(BombsPanel panel, boolean result);

		/**
		 * ゲームが開始される
		 * @param panel
		 */
		public void onPreviewGameStart(BombsPanel panel);
	}

	/**
	 * 爆弾ボタンがクリックされたときのコールバック
	 * @author Ryan's Factory
	 */
	private final class BombsPanelCallBack implements ClickButtonAction, BombsPanelAction {
		@Override
		public void onClickCallBack(Button button) {
			BombsPanel panel = (BombsPanel) button.getParent();
			if(isAllClosePanel()){
				panel.randomBombs(button.getIndex());
				panel.calculationButtonStatus();
				panel.repaint();
			}
			// クリックされたボタンが爆弾か？
			if (button.isBombs()) {
				// ボタンをすべて開く
				panel.openAllButton();
				// ゲームの終了
				onGameEnd(panel, LOSS);
				return;
			}
			// クリックされたボタンが空か？
			if (button.isEmpty()) {
				// 空のボタンと隣接するボタンをすべて開く
				panel.openEmptyButton(button.getIndex());
			}
			// 残りのボタンはすべて爆弾が配置されているか？
			if (panel.isAllBombs()) {
				// ボタンをすべて開く
				panel.openAllButton();
				// ゲームの終了
				onGameEnd(panel, WIN);
				return;
			}
		}

		@Override
		public void onGameEnd(BombsPanel panel, boolean result) {
			if (null != panel.action) {
				panel.action.onGameEnd(panel, result);
			}
		}

		@Override
		public void onPreviewGameStart(BombsPanel panel) {
			if (null != panel.action) {
				panel.action.onPreviewGameStart(panel);
			}
		}
	}

	/**
	 * ボタンクリックのイベントのインターフェイス
	 * @author Ryan's Factory
	 */
	public interface ClickButtonAction {
		/**
		 * 爆弾ボタンがクリックされた
		 * @param button クリックされたボタン
		 */
		public void onClickCallBack(Button button);
	}

	/**
	 * ゲームの負け
	 */
	public static final boolean LOSS = false;
	/**
	 * ゲームの勝ち
	 */
	public static final boolean WIN = true;
	/**
	 * 状態を探査するベクトル値
	 */
	private static final int[][] VECTOR = { { -1, -1 }, { 0, -1 }, { 1, -1 }, { -1, 0 }, { 1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };
	/**
	 * ボーダーの高さ
	 */
	private int BORDER_HEIGHT = 1;
	/**
	 * 爆弾のアイコン
	 */
	private final ImageIcon bomIcon = new ImageIcon(Minesweeper.class.getResource("/resource/bom.png"));
	/**
	 * フォント
	 */
	private Font font = new Font("メイリオ", Font.BOLD, 24);
	/**
	 * 爆弾ボタンを配置する列
	 */
	private int column;

	/**
	 * 爆弾ボタンを配置する行
	 */
	private int row;

	/**
	 * 爆弾ボタンを配置する数
	 */
	private int value;

	/**
	 * 爆弾を配置する数
	 */
	private int bombs = 0;
	/**
	 * 爆弾ボタンの横幅
	 */
	private int innerWidth = Button.MIN_WIDTH;

	/**
	 * 爆弾ボタンの高さ
	 */
	private int innerHeight = Button.MIN_HEIGHT;

	/**
	 * 爆弾ボタンのクリック処理
	 */
	private BombsPanelCallBack callback;

	/**
	 * ゲームのアクション
	 */
	private BombsPanelAction action = null;
	/**
	 * フラッグ機能のオンオフ trueなら旗を表示、falseなら爆弾を表示
	 */
	private boolean flag = false;
	/**
	 * 爆弾の残数
	 */
	private JLabel remaining;

	/**
	 * コンストラクタ
	 */
	public BombsPanel() {
		super();
		// クラス属性の設定
		this.column = 2;
		this.row = 2;
		this.bombs = 2;
		this.value = this.column * this.row;
		// 爆弾ボタンの設置と初期化
		this.init();
	}

	/**
	 * コンストラクタ
	 *
	 * @param column
	 *            爆弾ボタンを配置する列
	 * @param row
	 *            爆弾ボタンを配置する行
	 * @param bombs
	 *            爆弾を配置する数
	 */
	public BombsPanel(int column, int row, int bombs) {
		super();
		// クラス属性の設定
		this.column = column;
		this.row = row;
		this.bombs = bombs;
		this.value = column * row;
		// 爆弾ボタンの設置と初期化
		this.init();
	}

	private void calculationButtonStatus() {
		for (int index = 0; index < this.getComponentCount(); index++) {
			// 爆弾ボタンを取得する
			Button bt = (Button) this.getComponent(index);
			// 爆弾が配置されているか？
			if (bt.isBombs()) {
				// 座標を求める
				Dimension d = new Dimension(index % this.column, index / this.column);
				// 探査するベクトルだけ繰り返す
				for (int[] v : VECTOR) {
					//  爆弾ボタンを取得
					Button t = this.gettCalculatioComponent(d.width + v[0], d.height + v[1]);
					if (null != t) {
						// 爆弾が隣接するので数を増やす
						t.incrementStatus();
					}
				}
			}
		}
	}

	/**
	 * 爆弾をすべて取り除き、表示を消す
	 */
	private void emptyBombs() {
		for (Component v : this.getComponents()) {
			// 表示しない
			((Button) v).closePanel();
			// 爆弾を配置しない
			((Button) v).setBombs(Button.EMPTY);
		}
	}

	/**
	 * 爆弾を配置する数を取得
	 * @return int
	 */
	public int getBombs() {
		return this.bombs;
	}

	/**
	 * 爆弾が配置されていない爆弾ボタンを取得
	 * @return List<Button>
	 */
	private List<Button> getBombsNoLocated() {
		List<Button> result = new ArrayList<Button>();
		for (Component v : this.getComponents()) {
			if (!((Button) v).isBombs()) {
				result.add((Button) v);
			}
		}
		return result;
	}

	/**
	 * 爆弾ボタンを配置する列を取得
	 * @return int
	 */
	public int getColumn() {
		return this.column;
	}

	/**
	 * 爆弾ボタンを配置する行を取得
	 * @return int
	 */
	public int getRow() {
		return this.row;
	}

	/**
	 * 指定の座標にあるボタンを取得する
	 * @param x 列
	 * @param y 行
	 * @return 爆弾ボタンを返却、存在しない場合はNULLを返却。
	 */
	private Button gettCalculatioComponent(int x, int y) {
		// 存在しない座標はNULLで戻る
		if (0 > x || 0 > y || column <= x || row <= y) {
			return null;
		}
		return (Button) this.getComponent(x + (column * y));
	}

	/**
	 * クラスの初期化
	 */
	private void init() {
		// デフォルト設定
		this.setBackground(Color.WHITE);
		this.setBorder(new LineBorder(Color.BLACK, BORDER_HEIGHT));
		// コールバック生成
		this.callback = new BombsPanelCallBack();
		// 爆弾ボタンの設置
		this.settingBombsButtons();
		// 爆弾の設置
		this.setBombs(this.bombs);
	}

	/**
	 * 配置された爆弾と開かれていないボタンが等しいか検査
	 * @return trueの場合は等しい、falseの場合は等しくない
	 */
	public boolean isAllBombs() {
		int b = 0;
		for (Component v : this.getComponents()) {
			Button bt = (Button) v;
			if (!bt.isOpenPanel()) {
				b++;
			}
		}
		if (this.bombs == b) {
			return true;
		}
		return false;
	}

	public boolean isAllClosePanel(){
		int sam = 0;
		for(Component v : this.getComponents()) {
			if(((Button) v).isOpenPanel()){
				sam++;
			}
		}
		if(sam == 1){
			return true;
		}
		return false;
	}

	/**
	 * 爆弾ボタンをすべて表示する
	 */
	private void openAllButton() {
		for (Component v : this.getComponents()) {
			((Button) v).openPanel();
		}
	}

	/**
	 * 周囲８マスのボタンを表示する
	 */
	public void openAroundPanel(int index){
		if( this.flag == true){
			Dimension d = new Dimension(index % this.column, index / this.column);
			for (int[] v : VECTOR) {
				Button t = this.gettCalculatioComponent(d.width + v[0], d.height + v[1]);
				if(null == t){
					continue;
				}
				if(!t.isFrag()){
					t.openPanel();
					if(t.isBombs()){
						// ボタンをすべて開く
						this.openAllButton();
						// ゲームの終了
						action.onGameEnd(this, LOSS);
						return;
					}
					if (isAllBombs()) {
						// ボタンをすべて開く
						this.openAllButton();
						// ゲームの終了
						action.onGameEnd(this, WIN);
						return;
					}
				}
			}
		}
	}

	/**
	 * 爆弾ボタンの空白とその周囲にある数字をすべて表示する
	 * @param index
	 *            空白爆弾ボタンのインデックス
	 */
	public void openEmptyButton(int index) {
		// 座標軸を求める
		Dimension d = new Dimension(index % this.column, index / this.column);
		// 探査するベクトルだけ繰り返す
		for (int[] v : VECTOR) {
			// 爆弾ボタンを取得
			Button t = this.gettCalculatioComponent(d.width + v[0], d.height + v[1]);   // v0 のv[0]番地とv[1]番地を呼び出す
			// ボタンが存在するか?
			if (null != t) {
				// 空白ボタンか?
				if (t.isEmpty()) {
					// 既に表示されているか？
					if (!t.isOpenPanel()) {
						// 表示する
						t.openPanel();
						// この位置から再帰的に次の空白爆弾ボタンを処理する
						this.openEmptyButton(t.getIndex());
					}
				} else {
					// 表示する
					t.openPanel();
				}
			}
		}
	}

	public void openStatusButton(int index){
		Dimension d = new Dimension(index % this.column, index / this.column);
		for (int[] v : VECTOR) {
			Button t = this.gettCalculatioComponent(d.width + v[0], d.height + v[1]);
			if (null != t) {
				if (t.isEmpty()) {
					if(!t.isOpenPanel()){
						t.openPanel();
						this.openEmptyButton(t.getIndex());
					}else{
						this.openEmptyButton(t.getIndex());
					}
				}
			}
		}
	}

	/**
	 * ランダムに爆弾を配置する
	 */
	private void randomBombs() {
		int b = this.bombs;
		Random rand = new Random();
		// 爆弾が配置されていないボタンを取得する
		List<Button> list = this.getBombsNoLocated();
		// 全て配置したか配置するところがなければループを抜ける
		while ((0 < b) && (!list.isEmpty())) {
			// 爆弾ボタンをランダムに取得する
			Button bt = list.get(rand.nextInt(list.size()));
			// 爆弾を配置
			bt.setBombs(Button.BOMBS);
			// 爆弾が配置されていないボタンを取得する
			list = this.getBombsNoLocated();
			b--;
		}
	}

	private void randomBombs(int index){
		int b = this.bombs;
		Random rand = new Random();
		// 爆弾が配置されていないボタンを取得する
		List<Button> list = this.getBombsNoLocated();
		// 全て配置したか配置するところがなければループを抜ける
		while ((0 < b) && (!list.isEmpty())) {
			// 爆弾ボタンをランダムに取得する
			Button bt = list.get(rand.nextInt(list.size()));
			if(index != bt.getIndex()){
				// 爆弾を配置
				bt.setBombs(Button.BOMBS);
				// 爆弾が配置されていないボタンを取得する
				list = this.getBombsNoLocated();
				b--;
			}
		}
	}

	/**
	 * 爆弾を再配置してゲームを再開できるようにする
	 */
	public void reStart() {
		if (null != this.action) {
			// ゲームを再開始する前に呼び出す
			this.action.onPreviewGameStart(this);
		}
		// 現在の配置をリセットする
		this.emptyBombs();
		// 爆弾をランダムに配置する
		//this.randomBombs();
		// 爆弾の配置状態を設定
		//this.calculationButtonStatus();
		// 再描画
		this.repaint();
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		for (Component v : this.getComponents()) {
			v.setBackground(bg);
		}
	}

	/**
	 * 爆弾を配置する数を設定
	 * @param b
	 *            爆弾を配置する数
	 */
	public void setBombs(int b) {
		// 配置する爆弾の数がボタンを超えてはならない
		if (value < b) {
			// 数は変更せずに戻る
			return;
		}
		this.bombs = b;
		// 現在の配置をリセットする
		this.emptyBombs();
		// 爆弾をランダムに配置する
		this.randomBombs();
		// 爆弾の配置状態を設定
		this.calculationButtonStatus();
	}

	/**
	 * ゲームのアクションを設定
	 * @param action
	 */
	public void setBombsPanelAction(BombsPanelAction action) {
		this.action = action;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		width = innerWidth * column + (BORDER_HEIGHT * 2);
		height = innerHeight * row + (BORDER_HEIGHT * 2);
		super.setBounds(x, y, width, height);
	}

	@Override
	public void setBounds(Rectangle r) {
		this.setBounds(r.x, r.y, r.width, r.height);
	}

	/**
	 * 爆弾ボタンを配置する列を設定
	 * @param column
	 */
	public void setColumn(int column) {
		// 現在の爆弾ボタンを削除する
		this.removeAll();
		// 爆弾ボタンを配置する列を設定
		this.column = column;
		// 爆弾ボタンの数を設定
		this.value = this.column * this.row;
		// 爆弾ボタンの設置
		this.settingBombsButtons();
		// 爆弾の設置
		//this.setBombs(this.bombs);
		// パネルのサイズを変更
		this.setSize(this.innerWidth * this.column + (BORDER_HEIGHT * 2), this.innerHeight * this.row + (BORDER_HEIGHT * 2));
	}

	@Override
	public void setFont(Font font) {
		this.font = font;
		super.setFont(font);
		for (Component v : this.getComponents()) {
			v.setFont(font);
		}
	}

	/**
	 * 爆弾ボタンを配置する行を設定
	 * @param row
	 */
	public void setRow(int row) {
		// 現在の爆弾ボタンを削除する
		this.removeAll();
		// 爆弾ボタンを配置する行を設定
		this.row = row;
		// 爆弾ボタンの数を設定
		this.value = this.column * this.row;
		// 爆弾ボタンの設置
		this.settingBombsButtons();
		// 爆弾の設置
		//this.setBombs(this.bombs);
		// パネルのサイズを変更
		this.setSize(this.innerWidth * this.column + (BORDER_HEIGHT * 2), this.innerHeight * this.row + (BORDER_HEIGHT * 2));
	}

	/**
	 * 爆弾ボタンの設置
	 */
	private void settingBombsButtons() {
		for (int index = 0; index < this.value; index++) {
			// 爆弾ボタンの生成
			Button b = new Button();
			// コールバック設定
			b.setCallback(this.callback);
			// 座標を求める
			Dimension d = new Dimension(index % this.column, index / this.column);
			// 爆弾ボタンを配置する座標を求める。
			int x = (int) ((BORDER_HEIGHT) + (d.getWidth() * this.innerWidth));
			int y = (int) ((BORDER_HEIGHT) + (d.getHeight() * this.innerHeight));
			// 爆弾ボタンを配置する
			b.setBounds(x, y, this.innerWidth, this.innerHeight);
			// インデックスを設定
			b.setIndex(index);
			// アイコンの設定
			b.setIcon(bomIcon);
			// フォントの設定
			b.setFont(font);
			// コンポーネントに追加する
			this.add(b);
		}
		// パネルのサイズを変更
		this.setSize(this.innerWidth * this.column + (BORDER_HEIGHT * 2), this.innerHeight * this.row + (BORDER_HEIGHT * 2));
	}

	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
	}
	/**
	 * 旗の状態を変更する
	 * @param b 旗か爆弾の選択 trueなら旗を表示、falseなら爆弾を表示
	 */
	public void setFlag(boolean b) {
		this.flag = b;
	}
	/**
	 * 旗の状態を取得する
	 * @return trueなら旗を表示、falseなら爆弾を表示
	 */
	public boolean getFlag(){
		return this.flag;
	}
	/**
	 * 旗を立てた数を変更する
	 * @param label_3
	 */
	public void setRemaining(JLabel label_3) {
		this.remaining = label_3;
	}
	/**
	 * 旗を立てた数を取得する
	 * @return JLabel
	 */
	public JLabel getRemaining(){
		return this.remaining;
	}
}