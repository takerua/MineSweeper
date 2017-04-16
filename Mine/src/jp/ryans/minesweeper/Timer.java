package jp.ryans.minesweeper;

import javax.swing.JLabel;

/**
 * 			タイマー制御クラス
 * @author t-agarie
 *
 */
public class Timer extends Thread{
	/**
	 * タイマーのカウント
	 */
	private int num = 0;
	/**
	 *
	 */
	private int statu = 1;
	private JLabel timer_label;

	/**
	 * タイマーの実行
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(1000);
				if(statu == 0){
					this.num++;
				}
				String str = String.format("%03d", this.num);
				timer_label.setText(str);
				if(this.num == 999){

				}
			}
			catch(InterruptedException e){

			}
		}
	}

	/**
	 * タイマーのストップ
	 */
	public void stopTime(){
		this.statu =1;
	}

	/**
	 * タイマーのスタート
	 */
	public void startTime(){
		this.statu = 0;
		this.num = 0;
	}
	/**
	 * タイマーのリセット
	 */
	public void resetTime(){
		this.statu = 1;
		this.num = 0;
	}
	/**
	 * タイムを取得
	 * @return int
	 */
	public int getTime(){
		return this.num;
	}

	public void setLabel(JLabel timer_label){
		this.timer_label = timer_label;
	}
}
