# master-management-playground

商品カテゴリマスター管理システムの MVP を題材に、AI エージェントで要件定義から実装まで進めるためのリポジトリです。

現時点の前提は次の通りです。

- 対象は単一の「商品カテゴリマスター」
- 主機能は一覧表示、CSV ダウンロード、CSV アップロードによる一括更新
- ローカル開発 DB は `MySQL` ではなく `H2` を優先
- 技術構成は `Vue.js 3`、`Spring Boot`、`MyBatis` を想定

## Documents

- 要件定義: [docs/requirements.md](/Users/massakai/github/master-management-playground/docs/requirements.md)
- 基本設計: [docs/basic-design.md](/Users/massakai/github/master-management-playground/docs/basic-design.md)
- バックエンド詳細設計: [docs/backend-design.md](/Users/massakai/github/master-management-playground/docs/backend-design.md)
- フロントエンド詳細設計: [docs/frontend-design.md](/Users/massakai/github/master-management-playground/docs/frontend-design.md)
- OpenAPI: [docs/openapi.yaml](/Users/massakai/github/master-management-playground/docs/openapi.yaml)
- OpenAPI Preview: [docs/openapi-preview.html](/Users/massakai/github/master-management-playground/docs/openapi-preview.html)

## OpenAPI Checks

- 依存定義: [pyproject.toml](/Users/massakai/github/master-management-playground/pyproject.toml)
- 初回セットアップ: `uv sync --group dev`
- 検証: `uv run python3 scripts/validate_openapi.py`
- プレビュー生成: `uv run python3 scripts/render_openapi_preview.py`
- テンプレート: [templates/openapi_preview.html.j2](/Users/massakai/github/master-management-playground/templates/openapi_preview.html.j2)

## Python Setup

- Python 関連の補助スクリプトは `uv` ベースで実行する
- 初回は `uv sync --group dev` で `.venv` と開発用依存を作成する
- 依存追加や更新をした場合は `uv sync --group dev` を再実行する
- 依存の再現には [uv.lock](/Users/massakai/github/master-management-playground/uv.lock) を利用する

## OpenAPI Preview Workflow

- `docs/openapi-preview.html` は [docs/openapi.yaml](/Users/massakai/github/master-management-playground/docs/openapi.yaml) から生成する成果物
- 生成スクリプトは [scripts/render_openapi_preview.py](/Users/massakai/github/master-management-playground/scripts/render_openapi_preview.py)
- レイアウト変更は [templates/openapi_preview.html.j2](/Users/massakai/github/master-management-playground/templates/openapi_preview.html.j2) で行う
- OpenAPI を変更したら、`uv run python3 scripts/validate_openapi.py` で検証し、`uv run python3 scripts/render_openapi_preview.py` で HTML を再生成する

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
- `docs/screens.md`
