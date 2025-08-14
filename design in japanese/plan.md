## 📌 現在の進捗状況

### 実装済み

* **Bento API**

    * `GET /bentos`（全件取得）
    * `GET /bentos/{id}`（1件取得）
    * `POST /bentos`（作成）
* **Recommendation API**

    * `GET /bentos/recommendation?userId={id}`
      ユーザーの`likedTags`と`dislikedIngredients`を考慮して弁当を推薦
* **User API**

    * `GET /users/{id}`（嗜好タグ・嫌いな食材付きで取得）
* **例外処理**

    * `@ControllerAdvice` + `@ExceptionHandler`
    * `400 Bad Request` / `404 Not Found` / `500 Internal Server Error`
* **DB連携**

    * `Bento` / `Ingredient` / `Tag` / `User` のエンティティ作成済み
    * MapStructによるDTO-Entityマッピング
* **推奨アルゴリズム（簡易版）**

    * likedTagsをすべて含むBentoを推薦
    * dislikedIngredientsを含まないBentoのみ推薦

---

## 🚀 次の開発ステップ（優先度順）

1. **Bento CRUD完成**

    * `PUT /bentos/{id}`（更新API）
    * `DELETE /bentos/{id}`（削除API）

2. **Ingredient & Tag API実装**

    * `GET /ingredients` / `POST /ingredients`
    * `GET /tags` / `POST /tags`
    * 他のCRUDメソッドは必要に応じて追加

3. **User嗜好設定API**

    * `POST /users/{id}/preferences`
      好きなタグ（likedTags）・嫌いな食材（dislikedIngredients）を登録

4. **Recommendation専用コントローラー**

    * `/recommendations?userId={id}` を**別コントローラー**に切り出し
      ビジネスロジックを`RecommendationService`として分離

5. **推薦アルゴリズム強化**

    * タグの一致数に基づくスコアリング
    * `Predicate`を使った動的フィルタリング

6. **データ初期化（Dummy Data）**

    * 日本の弁当・食材・タグをセットした`DataInitializer`
    * 起動時に自動投入（開発用のみ）

7. **テスト環境の充実**

    * Postmanコレクション作成
    * `@SpringBootTest`で統合テスト
    * MockMvcでAPI単体テスト
