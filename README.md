# suijaku
大貧民（追加ルールなし＋NPC機械学習）
<BR><BR>
  <h2>usage</h2>
・まず「NPCを鍛える」でNPCを鍛えてください。

<h3>キャラ</h3>
・雑魚…出せるカードを手当たり次第に出す<BR>
・強い…強いカードを出し惜しみして、最後の方（ほかのプレイヤーが上がりそうになったら）に出す<BR>
・NeuralNetwork…選択肢がカード単位（output関数：sigmoid、神経多いバージョン/ReLu）、出せるカードBrain

<h2>＜今後やること（上から順）＞</h2>
・Status activityにPCを育てるの失敗したとき（paramがinfになったしまった場合）に備えてDELETEボタン実装<BR>
・遺伝アルゴリズムはtrain_activityでrobot_select選択かつ世代数を入力した場合に実装とする。（他の題材で遺伝アルゴリズムの実装の経験を積んでから作る。）

