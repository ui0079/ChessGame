import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameMode extends JPanel {
	boolean Error_Flag;
	Random rnd = new Random();
	static final int YMAX = 8, XMAX = 8;
	ArrayList<Figure> figs = new ArrayList<Figure>();
	boolean turn = true;

	int winner = 0;
	int c = 0, count = 1;
	int select;
	int trap_select;/*
									 * 選択したキャラの番号：0=何もいない
									 * 1=王（1P）2=クイーン（1P）3=歩兵1（1P）4=歩兵2（1P）5=王（2P）6=クイーン（2P）7=歩兵1（2P）8=歩兵2（2P）
									 */
	int pre_x, pre_y;
	int x, y;
	int[] rest = { 0, 0 };
	int player = 0;
	int pos[] = { -1, -1 };

	int board_x = 100, board_y = 120;

	String image_dir_name = "pic/";
	String[] image_file_name = { "king01.png", "queen01.png", "hohei1_01.png", "hohei2_01.png", "king02.png",
			"queen02.png", "hohei1_02.png", "hohei2_02.png", "trap.png" };
	String[] character_name = { "キング", "クイーン", "歩兵1", "歩兵2" };;

	int[][] board = new int[XMAX][YMAX];
	int[][] grid_color = new int[XMAX][YMAX];

	int[][] trap = new int[XMAX][YMAX];// 1=即死,2=キャラ変更
	Text ttr = new Text(50, 40, "1Pは青色の場所のどこかにキングを置いてください ", new Font("SansSerif", Font.PLAIN, 22));

	int[][] move_area = { { 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 6, 1, 6, 6, 1, 6, 1 }, { 0, 1, 0, 1, 1, 0, 1, 0 },
			{ 2, 0, 2, 0, 0, 2, 0, 2 } };

	public int Check_Winner() {
		boolean flag_1P = false;
		boolean flag_2P = false;
		for (int i = 0; i < XMAX; i++) {
			for (int j = 0; j < YMAX; j++) {
				if (board[i][j] == 1) {
					flag_1P = true;
				}
				if (board[i][j] == 5) {
					flag_2P = true;
					System.out.printf("detected : (%d, %d)\n", i, j);
				}
			}
		}

		if (flag_1P && !flag_2P) {
			return 1;
		} else if (!flag_1P && flag_2P) {
			return 2;
		}

		return 0;
	}

	public boolean Check_MoveArea(int x, int y, int next_x, int next_y, int character, int player) {
		if ((player == 0 && grid_color[next_x][next_y] == 3) || (player == 1 && grid_color[next_x][next_y] == 4))
			return true;

		return false;
	}

	public void Draw_MoveArea(int x, int y, int character, int player) {
		int direction = 0;

		character = character % 4;
		if (character == 0) {
			character = 4;
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i != 0 || j != 0) {
					for (int k = 1; k <= move_area[character - 1][direction]; k++) {
						if (x + k * i >= 0 && x + k * i <= 7 && y + k * j >= 0 && y + k * j <= 7
								&& (x + k * i != x || y + k * j != y)) {
							if (player == 0) {
								if ((player == 0 && board[x + k * i][y + k * j] > 4 || board[x + k * i][y + k * j] == 0)
										|| (player == 1 && (board[x + k * i][y + k * j] <= 4 || board[x + k * i][y + k * j] == 9))) {
									figs.add(new Rect(Color.BLUE, board_x + (x + k * i) * 90, board_y + (y + k * j) * 90, 90, 90));
									grid_color[x + k * i][y + k * j] = 3;
								}
							} else {
								if ((player == 0 && board[x + k * i][y + k * j] > 4 || board[x + k * i][y + k * j] == 0)
										|| (player == 1 && (board[x + k * i][y + k * j] <= 4 || board[x + k * i][y + k * j] == 9))) {
									figs.add(new Rect(Color.RED, board_x + (x + k * i) * 90, board_y + (y + k * j) * 90, 90, 90));
									grid_color[x + k * i][y + k * j] = 4;
								}
							}
							SetImage2Grid(x + k * i, y + k * j);

							if (board[x + k * i][y + k * j] != 0) {
								break;
							}
						}
					}
					direction++;
				}
			}
		}
	}

	public void Clear_MoveArea() {
		for (int x = 0; x < XMAX; x++) {
			for (int y = 0; y < YMAX; y++) {
				if (grid_color[x][y] == 3 || grid_color[x][y] == 4) {
					if ((x + y) % 2 == 0) {
						figs.add(new Rect(Color.WHITE, board_x + x * 90, board_y + y * 90, 90, 90));
						grid_color[x][y] = 0;
						SetImage2Grid(x, y);
					} else {
						figs.add(new Rect(Color.BLACK, board_x + x * 90, board_y + y * 90, 90, 90));
						grid_color[x][y] = 1;
						SetImage2Grid(x, y);
					}
					System.out.printf("%d,%d,%d\n", board[x][y], x, y);
				}
			}
		}
	}

	public void SetImage2Grid(int x, int y) {
		if (board[x][y] >= 1) {
			figs.add(new Picture(image_dir_name + image_file_name[board[x][y] - 1], board_x + x * 90, board_y + y * 90, 56, 56));
		}
	}

	public void PaintGrid(int x, int y) {
		if (grid_color[x][y] == 3) {
			figs.add(new Rect(Color.BLUE, board_x + x * 90, board_y + y * 90, 90, 90));
		} else if (grid_color[x][y] == 4) {
			figs.add(new Rect(Color.RED, board_x + x * 90, board_y + y * 90, 90, 90));
		} else if (grid_color[x][y] == 5) {
			figs.add(new Rect(Color.CYAN, board_x + x * 90, board_y + y * 90, 90, 90));
		} else {
			if ((x + y) % 2 == 0) {
				figs.add(new Rect(Color.WHITE, board_x + x * 90, board_y + y * 90, 90, 90));
				grid_color[x][y] = 0;
			} else {
				figs.add(new Rect(Color.BLACK, board_x + x * 90, board_y + y * 90, 90, 90));
				grid_color[x][y] = 1;
			}
		}
	}

	public void Create_Stage() {
		for (int i = 0; i < XMAX; i++) {
			for (int j = 0; j < YMAX; j++) {
				PaintGrid(i, j);
			}
		}
		return;
	}

	/*
	 * public void HideTrap(){
	 * for (int i = 0; i < XMAX; i++) {
	 * for (int j = 0; j < YMAX; j++) {
	 * if(board[i][j] == 9){
	 * PaintGrid(i, j);
	 * }
	 * c++;
	 * }
	 * 
	 * c++;
	 * }
	 * return;
	 * }
	 */

	public int[] GetGrid(MouseEvent evt) {
		Rect r = pick(evt.getX(), evt.getY());
		int xt, yt;
		if (r == null || winner != 0) {
			xt = -1;
			yt = -1;
		} else {
			xt = (r.getX() - 90) / 90;
			yt = (r.getY() - 90) / 90;
		}

		int[] value = { xt, yt };
		return value;
	}

	public int[] PointGrid(int pre_x, int pre_y, MouseEvent evt) {
		int[] coodinate = GetGrid(evt);
		int xt = coodinate[0];
		int yt = coodinate[1];

		if (pre_x != -1 && pre_y != -1 && (pre_x != xt || pre_y != yt)) {
			PaintGrid(pre_x, pre_y);
			SetImage2Grid(pre_x, pre_y);
			SetText();
		}

		if (xt == -1 || yt == -1) {
			return coodinate;
		}

		figs.add(new Rect(Color.YELLOW, board_x + xt * 90, board_y + yt * 90, 90, 90));
		SetImage2Grid(xt, yt);

		return coodinate;
	}

	public void SetCharacter(int character, int player, MouseEvent evt) {
		int[] coodinate = GetGrid(evt);
		int x = coodinate[0];
		int y = coodinate[1];

		if (x == -1 || y == -1) {
			return;
		}

		if (board[x][y] != 0 || (player == 0 && y != 7) || (player == 1 && y != 0)) {
			ttr.setText("そこには設置できません");
			Error_Flag = false;
			return;
		}

		board[x][y] = character; /* 赤=王 黄色=クイーン 青=歩兵1 緑=歩兵2 */
		grid_color[x][y] = 0;
		PaintGrid(x, y);
		SetImage2Grid(x, y);
		count++;
		repaint();
	}

	public void SetTrap(MouseEvent evt) {
		int[] coodinate = GetGrid(evt);
		int xt = coodinate[0];
		int yt = coodinate[1];

		if (xt == -1 || yt == -1) {
			return;
		}

		if (board[xt][yt] != 0 || yt < 2 || yt > 5) {
			ttr.setText("そこには設置できません");
			Error_Flag = false;
			return;
		}

		board[xt][yt] = 9;
		int ran = rnd.nextInt(3);
		trap[xt][yt] = ran + 1;
		figs.add(new Picture(image_dir_name + image_file_name[8], board_x + xt * 90, board_y + yt * 90, 56, 56));
		count++;
		repaint();
	}

	public void CheckTrap(int x, int y, int character, int player) {
		if (trap[x][y] != 0 && (character == 1 || character == 5)) {
			ttr.setText("キングには影響がないようだ…");
			Error_Flag = false;
			PaintGrid(x, y);
			SetImage2Grid(x, y);
			board[x][y] = character;
		} else if (trap[x][y] == 1) { // キャラ死亡
			ttr.setText("キャラが消えた");
			Error_Flag = false;
			PaintGrid(x, y);
			board[x][y] = 0;
			trap[x][y] = 0;
		} else if (trap[x][y] == 2) {// キャラ変更
			int rand_character = player * 3 + rnd.nextInt(3) + 2;
			while(rand_character == character){
				rand_character = player * 3 + rnd.nextInt(3) + 2;
			}
			board[x][y] = rand_character;
			trap[x][y] = 0;
			ttr.setText(character_name[rand_character - 1] + "に変身した");
			Error_Flag = false;
			PaintGrid(x, y);
			SetImage2Grid(x, y);
		} else if (trap[x][y] == 3) {
			ttr.setText("もう1ターン動かせるぞ");
			Error_Flag = false;
			trap[x][y] = 0;
			board[x][y] = character;
			PaintGrid(x, y);
			SetImage2Grid(x, y);
			if (player == 0) {
				count = 20;
			} else {
				count = 22;
			}
		} else {
			board[x][y] = character;
			PaintGrid(x, y);
			SetImage2Grid(x, y);
		}

		System.out.printf("%d,%d,%d\n", board[x][y], x, y);
		board[pre_x][pre_y] = 0;

		PaintGrid(pre_x, pre_y);
	}

	public void View_MoveArea(int character) {
		return;
	}

	public void CheckSelect(int player, MouseEvent evt) {
		int[] coodinate = GetGrid(evt);
		x = coodinate[0];
		y = coodinate[1];

		if (x == -1 || y == -1) {
			return;
		}

		pre_x = x;
		pre_y = y;
		select = board[x][y];

		System.out.printf("%d,%d,%d\n", board[x][y], x, y);

		if (player == 0) {
			if (select == 0 || select >= 5) {
				ttr.setText("キャラがいません");
				Error_Flag = false;
				count = 21;
				return;
			}
		} else {
			if (select <= 4) {
				ttr.setText("キャラがいません");
				count = 23;
				Error_Flag = false;
				return;
			}
		}

		Draw_MoveArea(x, y, select, player);
		ttr.setText("移動先を選択してください");

		repaint();
		count++;
		return;
	}

	public void CheckGrid(int select, int player, MouseEvent evt) {
		int[] coodinate = GetGrid(evt);
		x = coodinate[0];
		y = coodinate[1];

		if (x == -1 || y == -1) {
			return;
		}

		select = select % 4;

		if (select == 0) {
			select = 4;
		}

		if (!Check_MoveArea(pre_x, pre_y, x, y, select, player)) {
			Clear_MoveArea();
			ttr.setText("そこのマスには進めません");
			Error_Flag = false;
			if (player == 0)
				count = 21;
			else
				count = 23;
			return;
		}

		Clear_MoveArea();
		System.out.printf("%d,%d,%d\n", board[x][y], x, y);

		if (player == 0)
			ttr.setText("2Pのターンです");
		else
			ttr.setText("1Pのターンです");

		CheckTrap(x, y, 4 * player + select, player);

		repaint();

		count++;
	}

	public void SetText() {
		if (count == 2) {
			ttr.setText("1Pは青色の場所のどこかにクイーンを置いてください");
		} else if (count >= 3 && count <= 5) {
			ttr.setText("1Pは青色の場所のどこかに兵士1を置いてください");
		} else if (count >= 6 && count <= 8) {
			ttr.setText("1Pは青色の場所のどこかに兵士2を置いてください");
		} else if (count == 9) {
			ttr.setText("2Pは赤色の場所のどこかにキングを置いてください");
		} else if (count == 10) {
			ttr.setText("2Pは赤色の場所のどこかにクイーンを置いてください");
		} else if (count >= 11 && count <= 13) {
			ttr.setText("2Pは赤色の場所のどこかに兵士1を置いてください");
		} else if (count >= 14 && count <= 16) {
			ttr.setText("2Pは赤色の場所のどこかに兵士2を置いてください");
		} else if (count == 17 || count == 19) {
			ttr.setText("1Pは水色の場所のどこかにアイテムを置いてください");
		} else if (count == 18 || count == 20) {
			ttr.setText("2Pは水色の場所のどこかにアイテムを置いてください");
		} else if (count >= 21){
			if(count%4 == 1){
				ttr.setText("1Pのターンです");
			} else if(count % 4 == 3){
				ttr.setText("2Pのターンです");
			} else{
				ttr.setText("移動先を選択してください");
			}
		}
	}

	public GameMode() {

		for(int i=0; i<XMAX; i++){
			grid_color[i][YMAX-1] = 3;
		}

		Create_Stage();
		figs.add(ttr);

		setOpaque(false);

		addMouseMotionListener(new MouseAdapter() {
			// マウスカーソルの位置を記憶
			public void mouseMoved(MouseEvent evt) {
				pos = PointGrid(pos[0], pos[1], evt);
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Error_Flag = true;
				System.out.println(count);
				if (count == 1) {
					SetCharacter(1, 0, evt);
				} else if (count == 2) {
					SetCharacter(2, 0, evt);
				} else if (count >= 3 && count <= 5) {
					SetCharacter(3, 0, evt);
				} else if (count >= 6 && count <= 8) {
					SetCharacter(4, 0, evt);
					if (count == 9) {
						for (int i = 0; i < XMAX; i++) {
							grid_color[i][0] = 4;
							PaintGrid(i, 0);
						}
					}
				} else if (count == 9) {
					SetCharacter(5, 1, evt);
				} else if (count == 10) {
					SetCharacter(6, 1, evt);
				} else if (count >= 11 && count <= 13) {
					SetCharacter(7, 1, evt);
				} else if (count >= 14 && count <= 16) {
					SetCharacter(8, 1, evt);
					if (count == 17) {
						for (int i = 0; i < XMAX; i++) {
							for (int j = 2; j < 6; j++) {
								grid_color[i][j] = 5;
								PaintGrid(i, j);
							}
						}
					}
				} else if (count >= 17 && count <= 20) {

					SetTrap(evt);

					if (count == 21) {
						for (int i = 0; i < XMAX; i++) {
							for (int j = 2; j < 6; j++) {
								grid_color[i][j] = 0;
								PaintGrid(i, j);
								SetImage2Grid(i, j);
							}
						}
						ttr.setText("Game Start!!");
					}

				} else if (count % 4 == 1) {
					CheckSelect(0, evt);
				} else if (count % 4 == 2) {
					CheckGrid(select, 0, evt);
				} else if (count % 4 == 3) {
					CheckSelect(1, evt);
				} else if (count % 4 == 0) {
					CheckGrid(select, 1, evt);
				}

				if (count >= 21) {
					winner = Check_Winner();
					if (winner == 1) {
						ttr.setText("1Pの勝利");
						return;
					} else if (winner == 2) {
						ttr.setText("2Pの勝利");
						return;
					}
				}
				if(Error_Flag){
					SetText();
				}
				
				repaint();
			}
		});

	}

	private int ck(int x, int y, int dx, int dy) {
		int s = board[y][x], count = 1;
		for (int i = 1; ck1(x + dx * i, y + dy * i, s); i++) {
			count++;
		}
		for (int i = 1; ck1(x - dx * i, y - dy * i, s); i++) {
			count++;
		}

		return count;
	}

	private boolean ck1(int x, int y, int s) {
		return 0 <= x && x < XMAX && 0 <= y && y < YMAX && board[y][x] == s;
	}

	public Rect pick(int x, int y) {
		Rect r = null;
		for (Figure f : figs) {
			if (f instanceof Rect && ((Rect) f).hit(x, y)) {
				r = (Rect) f;
			}
		}
		return r;
	}

	public void paintComponent(Graphics g) {
		for (Figure f : figs) {
			f.draw(g);
		}
	}

	public static void main(String[] args) {
		JFrame app = new JFrame();
		app.add(new GameMode());
		app.setSize(900, 1000);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
	}

	interface Figure {
		public void draw(Graphics g);
	}

	static abstract class SimpleFigure implements Figure {
		int xpos, ypos, width, height;
		BufferedImage img;

		public SimpleFigure(int x, int y, int w, int h) {
			xpos = x;
			ypos = y;
			width = w;
			height = h;
		}

		public void draw(Graphics g) {
			int x = xpos - width / 2, y = ypos - height / 2;
			g.drawImage(img, x, y, width, height, null);
		}
	}

	static class Rect extends SimpleFigure {
		Color col;

		public Rect(Color c, int x, int y, int w, int h) {
			super(x, y, w, h);
			col = c;
		}

		public boolean hit(int x, int y) {
			return xpos - width / 2 <= x && x <= xpos + width / 2 && ypos - height / 2 <= y && y <= ypos + height / 2;
		}

		public int getX() {
			return xpos;
		}

		public int getY() {
			return ypos;
		}

		public void draw(Graphics g) {
			g.setColor(col);
			g.fillRect(xpos - width / 2, ypos - height / 2, width, height);
		}

	}

	static class Picture extends SimpleFigure {
		public Picture(String fname, int x, int y, int w, int h) {
			super(x, y, w, h);
			try {
				img = ImageIO.read(new File(fname));
			} catch (Exception ex) {
			}

		}

		public boolean hit(int x, int y) {
			return xpos - width / 2 <= x && x <= xpos + width / 2 && ypos - height / 2 <= y && x <= xpos + width / 2;
		}
	}

	static class Text implements Figure {
		int xpos, ypos;
		String txt;
		Font fn;

		public Text(int x, int y, String t, Font f) {
			xpos = x;
			ypos = y;
			txt = t;
			fn = f;
		}

		public void setText(String t) {
			txt = t;
		}

		public void draw(Graphics g) {
			g.setColor(Color.BLACK);
			g.setFont(fn);
			g.drawString(txt, xpos, ypos);
		}
	}

}