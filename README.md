# master-management-playground

商品カテゴリマスター管理システムの MVP を題材に、AI エージェントで要件定義から実装まで進めるためのリポジトリです。

現時点の前提は次の通りです。

- 対象は単一の「商品カテゴリマスター」
- 主機能は一覧表示、CSV ダウンロード、CSV アップロードによる一括更新
- ローカル開発 DB は `MySQL` ではなく `H2` を優先
- 技術構成は `Vue.js 3`、`Spring Boot`、`MyBatis` を想定

## Documents

- 要件定義: [docs/requirements.md](/Users/massakai/github/master-management-playground/docs/requirements.md)

## MVP Scope

- 商品カテゴリ一覧の表示
- CSV ダウンロード
- CSV アップロード
- CSV バリデーション
- 正常時の全件入れ替え更新
- エラー内容の表示

## Out Of Scope

- 複数マスタ対応
- 画面での個別編集
- 本番デプロイ
- 外部システム連携
- 本格的な認可、監査ログ設計

## Next Docs

次に追加しやすいドキュメント候補:

- `docs/schema.md`
- `docs/api.md`
- `docs/screens.md`
