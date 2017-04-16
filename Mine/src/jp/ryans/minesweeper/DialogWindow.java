package jp.ryans.minesweeper;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
/**
 * ダイアログウィンドウ
 * @author t-agarie
 *
 */
public class DialogWindow extends JDialog{

	private final JPanel contentPanel = new JPanel();

	private final DialogAction dialogAction = new DialogAction();

	private Vector<String> model;

	private JList<String> list;

	private JButton buttonTime;

	private JButton cancelButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogWindow dialog = new DialogWindow();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);//
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 記録ダイアログのポップアップ
	 */
	public DialogWindow() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 434, 261);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);

		model = new Vector<String>();				//モデルを作成
		list = new JList<String>(model);			//モデルをリストに格納

		list.setBounds(12, 10, 397, 199);
		contentPanel.add(list);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 220, 434, 39);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(null);
			{
				JButton okButton = new JButton("OK");
				okButton.setBounds(285, 5, 49, 21);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton  = new JButton("Cancel");
				cancelButton.setBounds(355, 5, 67, 21);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
			{
				buttonTime = new JButton("");
				buttonTime.setBounds(8, 5, 49, 20);
				buttonTime.setAction(dialogAction);
				buttonTime.setText("秒");
				buttonPane.add(buttonTime);
			}
		}
			try {
				ranking();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
	}

	/**
	 * クリアタイム.txtを読み込んで表示させる
	 * @throws IOException
	 */
	public void ranking() throws IOException{
		File fw = new File("クリアタイム.txt");
		FileInputStream fis = new FileInputStream(fw);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String str = "";
		while(null != (str = br.readLine())){
				model.addElement(str);			//読み込んだ1行の文章をモデルに格納する
		}
		br.close();
	}

	/**
	 * クリアタイム.txtを秒の早い順に並べ替えて表示する
	 */
	public void sortListSecond(){
			contentPanel.remove(list);		//リストを消去する

			model = new Vector<String>();		//もう一度modelを作る
			list = new JList<String>(model);	//もう一度JListを作る（JListにはデータが格納されていないので）
			list.setBounds(12, 10, 397, 199);
			contentPanel.add(list);
			try {
				ArrayList<SortTime> list = buildTimeList();
				Collections.sort(list);
				for(int i = 0; i < list.size(); i++){
					model.addElement(list.get(i).other);
				}
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			this.repaint();
			//contentPanel.repaint();
	}

	/**
	 * クリアタイム.txtを秒数とその他に分けてsortListTimeに格納する
	 * @throws IOException
	 */
	public ArrayList<SortTime> buildTimeList() throws IOException{
		File fw = new File("クリアタイム.txt");
		FileInputStream fis = new FileInputStream(fw);
		InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		BufferedReader br = new BufferedReader(isr);
		ArrayList<SortTime> sortListTime = new ArrayList<SortTime>();
		String str = "";
		while(null != (str = br.readLine())){
			int inttime = Integer.parseInt(str.trim().split("秒")[0]);
			sortListTime.add(new SortTime(inttime,str));
		}
		br.close();
		return sortListTime;
	}



	/**
	 * 秒数を判定して並べ替えるインナークラス
	 * @author t-agarie
	 *
	 */
	private class SortTime implements Comparable<SortTime>{

		public int time;
		public String other;

		public SortTime( int st, String so ){
			time = st;
			other  = so;
		}

		@Override
		public int compareTo(SortTime o) {
			return this.time - o.time;
		}
	}


	private class DialogAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			if(obj == buttonTime ){
				sortListSecond();
			}
			if(obj == cancelButton){
				getDefaultCloseOperation();
			}
		}
	}
}
