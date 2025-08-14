## 📌 開発進捗まとめ

### ✅ 実装済み機能

* **Bento API**

    * `GET /bentos`：全件取得
    * `GET /bentos/{id}`：ID で取得
    * `POST /bentos`：新規作成
    * `PUT /bentos/{id}`：更新
    * `DELETE /bentos/{id}`：削除
* **ユーザー嗜好設定 API**

    * `POST /users/{id}/preferences`：ユーザーの好み（タグ・食材）を登録
* **推薦 API**

    * `GET /recommendations?userId=xxx`：ユーザーの嗜好に基づく Bento 推薦
    * 推薦処理を `BentoController` から `RecommendationController` に分離
* **データ初期化**

    * Dummy Data 自動投入（開発・テスト用）
* **API テスト**

    * Postman コレクション作成
    * `MockMvc` を利用した API テスト導入

---

## 📌 今後の開発計画

### 1. 推薦アルゴリズム強化（推薦アルゴリズム強化）

* **現状課題**

    * 単純なタグ一致 & 食材除外のフィルタのみ
    * 並び順やスコアリングが未実装
* **改善方針**

    1. **スコアリング導入**

        * タグ一致度（加点方式）
        * 嫌いな食材含有（減点方式）
    2. **ランキング化**

        * スコア順にソートし、上位 N 件を返却
        * デフォルト N=10、`limit` クエリパラメータで変更可能
    3. **柔軟なフィルタ**

        * カロリー範囲指定（例：`minCalorie` / `maxCalorie`）
        * 特定タグの必須条件（例：`requiredTag`）
    4. **将来的な拡張**

        * ユーザーの注文履歴ベースの推薦（協調フィルタリング）
        * 人気ランキングと組み合わせたハイブリッド方式

---

### 2. API 拡張

* **Ingredient API**

    * `GET /ingredients`：全件取得
    * `POST /ingredients`：新規登録
* **Tag API**

    * `GET /tags`：全件取得
    * `POST /tags`：新規登録
* **User API**

    * `GET /users/{id}`：ユーザー詳細（嗜好データ含む）

---

### 3. テスト環境整備

* `application-test.yml` を使用したテスト DB 分離
* 全 API の正常系・異常系テスト
* 推薦 API のロジックテスト（スコア計算確認）

---

### 4. 将来的な拡張

* **Spring Security 導入**

    * JWT 認証
    * ユーザー別データアクセス制御
* **OpenAPI (Swagger) 導入**

    * API ドキュメント自動生成
* **Docker Compose 改善**

    * `init.sql` による DB 初期化
    * 本番/開発/テスト環境の切り替え
