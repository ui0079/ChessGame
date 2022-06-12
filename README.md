# 戦略ゲーム
![イメージ画像](https://user-images.githubusercontent.com/91183043/173242202-6eac4ef8-3a40-469b-8f68-7e70397d187a.png)
## 使用言語
Java

## 構成
```
│ 
|  .classpath
│  .project
│  README.md
│
├─.settings
│      org.eclipse.jdt.core.prefs
│
├─bin
│      GameMode$1.class
│      GameMode$2.class
│      GameMode$Figure.class
│      GameMode$Picture.class
│      GameMode$Rect.class
│      GameMode$SimpleFigure.class
│      GameMode$Text.class
│      GameMode.class
│
├─pic
│      hohei1_01.png
│      hohei1_02.png
│      hohei2_01.png
│      hohei2_02.png
│      king01.png
│      king02.png
│      queen01.png
│      queen02.png
│      trap.png
│
└─src
        GameMode.java
```

## ルール
8つのコマを操作して、相手のキングを倒すゲーム

### ゲームの流れ
1. 自分のコマの位置を決める
2. アイテムの位置を決める
3. ゲームスタート(自分のコマを交互に移動させる)

### キャラクター
* キング 
* クイーン
* 兵士1
* 兵士2

### アイテム
* キャラ変更 ※ただし、キングは変更されない
* キャラが消える
* もう一回自分のターンになる