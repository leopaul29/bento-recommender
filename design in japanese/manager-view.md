# 📋 開発タスク管理表

| ステータス  | タスク                                      | 詳細                                  | 優先度 |
|--------|------------------------------------------|-------------------------------------|-----|
| ✅ 完了   | `GET /bentos`                            | 全Bento一覧取得                          | 高   |
| ✅ 完了   | `GET /bentos/{id}`                       | ID指定でBento取得                        | 高   |
| ✅ 完了   | `POST /bentos`                           | Bento作成                             | 高   |
| ✅ 完了   | `GET /users/{id}`                        | ユーザーと嗜好情報取得                         | 中   |
| ✅ 完了   | `GET /bentos/recommendation?userId={id}` | ユーザーの嗜好に基づく推薦                       | 中   |
| ✅ 完了   | `PUT /bentos/{id}`                       | Bento更新                             | 高   |
| ✅ 完了   | `DELETE /bentos/{id}`                    | Bento削除                             | 高   |
| 🚧 未着手 | Ingredient API                           | CRUD実装                              | 中   |
| 🚧 未着手 | Tag API                                  | CRUD実装                              | 中   |
| ✅ 完了   | `POST /users/{id}/preferences`           | ユーザー嗜好設定                            | 高   |
| ✅ 完了   | Recommendation Controller分離              | RecommendationService作成             | 中   |
| 🚧 未着手 | 推薦アルゴリズム強化                               | タグ一致率・スコアリング                        | 低   |
| ✅ 完了   | Dummy Data初期化                            | DataInitializer作成                   | 中   |
| ✅ 完了   | APIテスト環境整備                               | Postman / MockMvc / @SpringBootTest | 中   |

---

## 🛠 開発の流れ（推奨順序）

1. **Bento CRUD 完成**✅

    * `PUT /bentos/{id}`
    * `DELETE /bentos/{id}`

2. **Ingredient / Tag CRUD追加**

    * 別コントローラーで管理

3. **ユーザー嗜好設定API追加**✅

    * `POST /users/{id}/preferences`

4. **推薦機能の分離**✅

    * `RecommendationController` + `RecommendationService`

5. **DataInitializer作成**✅

    * Dummy Data投入

6. **テスト環境整備**✅

    * Postmanコレクション✅
    * MockMvc / SpringBootTest✅

