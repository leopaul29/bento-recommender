# 🍱 API設計書 - Bento Recommender

### API Vocabulary

* **概要 (概要説明)** : Overview (Summary)
* **処理詳細 (処理の流れ)** : Processing Details (Process Flow)
* **入力 (リクエスト仕様)** : Input (Request Specifications)
* **出力 (レスポンス仕様)** : Output (Response Specifications)
* **考慮事項 (バリデーションや制約)** : Considerations (Validation and Constraints)

---

## ベースURL

[http://localhost:8080/api](http://localhost:8080/api)

---

## 1. Bento関連 (Bento Management)

### 1.1 全Bento取得 (Get All Bentos)
**GET** `/bentos`

#### 概要
登録済みの全Bento情報を取得するAPI。

#### 処理詳細
1. DBの`bentos`テーブルから全件取得。
2. 関連する食材(`ingredients`)、タグ(`tags`)も同時に取得 (Eager Fetch推奨)。
3. DTOに変換して返却。

#### 入力
なし

#### 出力
- **200 OK**
```json
[
  {
    "id": 1,
    "name": "Tofu Delight",
    "description": "Light tofu bento with rice",
    "calorie": 350,
    "ingredients": [
      { "id": 1, "name": "tofu" },
      { "id": 2, "name": "rice" }
    ],
    "tags": [
      { "id": 1, "name": "vegan" }
    ]
  }
]
````

---

### 1.2 Bento登録 (Create Bento)

**POST** `/bentos`

#### 概要

新しいBentoをDBに登録するAPI。

#### 処理詳細

1. 受信したDTOからBentoエンティティを生成。
2. 食材・タグは既存データを再利用、存在しない場合は新規作成。
3. DBに保存後、登録内容を返却。

#### 入力

```json
{
  "name": "Salmon Special",
  "description": "Grilled salmon with vegetables",
  "calorie": 500,
  "ingredients": [
    { "name": "salmon" },
    { "name": "rice" }
  ],
  "tags": [
    { "name": "fish" }
  ]
}
```

#### 出力

* **201 Created**

```json
{
  "id": 2,
  "name": "Salmon Special",
  "description": "Grilled salmon with vegetables",
  "calorie": 500,
  "ingredients": [
    { "id": 3, "name": "salmon" },
    { "id": 2, "name": "rice" }
  ],
  "tags": [
    { "id": 2, "name": "fish" }
  ]
}
```

#### 考慮事項

* `name`は必須（重複不可）
* `calorie`は0以上
* 食材名・タグ名は小文字で正規化

---

### 1.3 特定のBento取得 (Get Bento by ID)

**GET** `/bentos/{id}`

#### 概要

指定IDのBento詳細を取得するAPI。

#### 処理詳細

1. `id`でBento検索。
2. 存在しなければ404エラーを返却。

---

### 1.4 Bento更新 (Update Bento)

**PUT** `/bentos/{id}`

#### 概要

既存Bentoを更新するAPI。

#### 処理詳細

1. `id`で既存Bentoを取得。
2. 入力データで上書き保存。
3. 食材・タグは存在チェック後、必要に応じて追加。

---

### 1.5 Bento削除 (Delete Bento)

**DELETE** `/bentos/{id}`

#### 概要

指定Bentoを削除するAPI。

---

## 2. ユーザー関連 (User Management)

### 2.1 ユーザー作成 (Create User)

**POST** `/users`

#### 概要

新しいユーザーを登録するAPI。

---

### 2.2 ユーザー嗜好設定 (Set User Preferences)

**POST** `/users/{id}/preferences`

#### 概要

ユーザーが好き/嫌いなタグ・食材を設定。

#### 処理詳細

1. ユーザーIDでUser取得。
2. LikedTags, DislikedIngredientsを更新。
3. DB保存。

---

## 3. 推薦API (Recommendation API)

### 3.1 推薦取得 (Get Recommendation)

**GET** `/recommendation?userId={id}`

#### 概要

ユーザー嗜好に基づき、最適なBentoを推薦。

#### 処理詳細

1. ユーザーの嗜好データ取得。
2. 全Bentoからフィルタリング：

    * 好きなタグ全てを含む
    * 嫌いな食材を含まない
3. DTOに変換して返却。

---

## 4. 食材 / タグ管理 (Ingredient / Tag Management)

### 4.1 食材一覧取得

**GET** `/ingredients`

### 4.2 タグ一覧取得

**GET** `/tags`

---

## Vocabulary

| 日本語（Expression）         | ひらがな                             | Traduction (FR)                                              |
|-------------------------|----------------------------------|--------------------------------------------------------------|
| 関連                      | かんれん                             | relation / lien                                              |
| 取得する                    | しゅとくする                           | obtenir / récupérer                                          |
| 登録済み                    | とうろくずみ                           | déjà enregistré                                              |
| 推奨                      | すいしょう                            | recommandation                                               |
| 変換する                    | へんかんする                           | convertir                                                    |
| 返却                      | へんきゃく                            | renvoyer (une valeur, une réponse)                           |
| 登録する                    | とうろくする                           | enregistrer / inscrire                                       |
| 受信したDTO                 | じゅしんした DTO                       | DTO reçu                                                     |
| エンティティを生成               | エンティティをせいせい                      | générer une entité                                           |
| タグは既存                   | タグはきぞん                           | le tag existe déjà                                           |
| データを再利用                 | データをさいりよう                        | réutiliser les données                                       |
| 存在しない場合は新規作成。           | そんざいしないばあいはしんきさくせい。              | créer si inexistant                                          |
| DBに保存後、登録内容を返却。         | DBにほぞんご、とうろくないようをへんきゃく。          | après enregistrement en DB, renvoyer le contenu              |
| `name`は必須（重複不可）         | `name`はひっす（ちょうふくふか）              | `name` est requis (pas de doublon)                           |
| `calorie`は0以上           | `calorie`はぜろいじょう                 | `calorie` doit être ≥ 0                                      |
| 食材名・タグ名は小文字で正規化         | しょくざいめい・タグめいはこもじでせいきか            | normaliser noms d’ingrédients et de tags en minuscules       |
| 指定IDのBento詳細を取得する       | していIDのBentoしょうさいをしゅとくする          | obtenir détails d’un bento par ID                            |
| `id`でBento検索。           | `id`でBentoけんさく。                  | rechercher un bento par ID                                   |
| 存在しなければ404エラーを返却。       | そんざいしなければ404エラーをへんきゃく。           | renvoyer une erreur 404 si inexistant                        |
| 既存Bentoを更新する            | きぞんBentoをこうしんする                  | mettre à jour un bento existant                              |
| `id`で既存Bentoを取得。        | `id`できぞんBentoをしゅとく。              | récupérer un bento existant par ID                           |
| 入力データで上書き保存。            | にゅうりょくデータでうわがきほぞん。               | écraser avec les nouvelles données                           |
| 食材・タグは存在チェック後、必要に応じて追加。 | しょくざい・タグはそんざいチェックご、ひつようにおうじてついか。 | vérifier existence des ingrédients/tags et ajouter si besoin |
| 指定Bentoを削除する            | していBentoをさくじょする                  | supprimer un bento donné                                     |
| ユーザー嗜好設定                | ユーザーしこうせってい                      | définir préférences utilisateur                              |
| 〇〇データを設定。               | 〇〇データをせってい。                      | définir données de 〇〇                                        |
| ユーザーIDでUser取得。          | ユーザーIDでUserしゅとく。                 | obtenir user par ID                                          |
| 〇〇を更新。                  | 〇〇をこうしん。                         | mettre à jour 〇〇                                             |
| DB保存                    | DBほぞん                            | sauvegarder en DB                                            |
| 最適なBento                | さいてきなBento                       | bento optimal                                                |
| 推薦                      | すいせん                             | recommandation                                               |
| 嗜好データ                   | しこうデータ                           | données de préférences                                       |
| フィルタリング                 | フィルタリング                          | filtrage                                                     |
| 〇〇を含む                   | 〇〇をふくむ                           | inclure 〇〇                                                   |
| 〇〇を含まない                 | 〇〇をふくまない                         | ne pas inclure 〇〇                                            |
| 管理                      | かんり                              | gestion                                                      |
| タグ一覧                    | タグいちらん                           | liste des tags                                               |

