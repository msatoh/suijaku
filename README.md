# suijaku
大貧民（追加ルールなし＋NPC機械学習）
<BR><BR>
  <b>●追加予定</b><BR>
  <h2>＜本体（大富豪機能）＞</h2>
完成<BR>
・階段<BR>
・8切り追加<BR>
（後は余計なものは入れない）<BR>
  
<h2>＜キャラクター選択画面＞</h2>
・<s>雑魚、強い、シグモイドでフル学習、ReLuでフル学習、選択肢から学習、</s>選択肢から学習＋残ったカードリスト、残ったカードリスト＋遺伝アルゴリズムの7人<BR>
<BR><BR>
  
<h2>＜今後やること（上から順）＞</h2>
<li>残ったカードリストから選択Brain作成
Train Activity作成…
<ul>キャラクター選択画面実装（フル学習・選択肢から学習・遺伝アルゴリズム）SelectActivity2かな
フル学習・選択肢から学習BrainのTrain実装…<BR>
<ul>ゲーム画面と同じにし、<BR>
ストップボタン…捨てたカードを反映させないで何度も実行する。<BR>
  を実装する。（これにより何度も同じ学習をさせることで本当に学習できているのか検証する）<BR></ul>
  Output値も表示させたい<BR></ul>
</li>
・遺伝アルゴリズムのBrain作成…candidate_cards_listsの中から選択させるようにし、出せる場合は必ず出せるようにする。（フル学習でない）<BR>
・遺伝アルゴリズムBrainのTrain実装<BR>
  ・5人のニューロン係数の初期値は乱数とする。<BR>
　・ゲーム画面と同じにし、全員遺伝アルゴリズムにする（自分もCPU）。<BR>
  ・順位が決まったら、次世代は<BR>
  　・1位<BR>
  　・2位<BR>
  　・1位遺伝子７５％、2位遺伝子２５％<BR>
  　・1位遺伝子と2位遺伝子の半々<BR>
  　・乱数<BR>
  　の5人とする。<BR>
・キャラクター選択を反映させる
