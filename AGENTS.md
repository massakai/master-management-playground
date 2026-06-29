# master-management-playground Agent Instructions

## Project Context

- このリポジトリは、商品カテゴリマスター管理システムの MVP を検討、設計、実装するための playground です。
- 現在の要件の基準文書は `docs/requirements.md` です。
- 初期スコープは汎用マスター管理基盤ではなく、単一の「商品カテゴリマスター」に限定します。

## Current MVP Scope

- 商品カテゴリ一覧の表示
- CSV ダウンロード
- CSV アップロードによる一括更新
- CSV バリデーション
- エラー表示

## Current Technical Assumptions

- フロントエンドは `Vue.js 3`
- バックエンドは `Spring Boot`
- データアクセスは `MyBatis`
- CSV の読み書きは `Apache Commons CSV`
- ローカル開発 DB は `H2`
- 将来の本番 DB 切り替えを意識し、H2 固有機能への依存は最小化する

## Recommended Directory Structure

- `docs/`: 要件定義、基本設計、OpenAPI などの設計ドキュメント
- `scripts/`: OpenAPI 検証やプレビュー生成などの補助スクリプト
- `templates/`: 生成物用テンプレート
- `db/`: DDL、初期データ、マイグレーション関連ファイル
- `frontend/`: Vue.js 3 の UI 実装
- `backend/`: Spring Boot アプリケーション

バックエンドは DDD を意識して次のように構成する。

- `backend/src/main/java/.../presentation`: Controller、API DTO
- `backend/src/main/java/.../application`: UseCase、Application Service
- `backend/src/main/java/.../domain`: Entity、Value Object、Repository interface、Domain Service
- `backend/src/main/java/.../infrastructure`: MyBatis Mapper、Repository 実装、CSV 入出力、設定

補足:

- 実装前であっても、新規ファイルはできるだけこの配置方針に沿って追加する
- 設計ドキュメントは原則 `docs/` 配下に置く
- 自動生成物とその入力テンプレート、生成スクリプトは分離する

## Documentation Rules

- 要件変更時は、関連実装より先に `docs/requirements.md` を更新する
- 追加ドキュメントを作る場合は、まず `docs/` 配下に置く
- ドキュメント名は簡潔にし、リポジトリ名と重複する接頭辞は避けてよい
- `git commit` のメッセージは日本語で書く
- OpenAPI の正本は `docs/openapi.yaml` とする
- `docs/openapi-preview.html` は手編集せず、生成スクリプトで更新する

## Implementation Notes

- MVP では CSV が主役なので、個別編集画面より CSV 入出力の仕様を優先して固める
- CSV 仕様は `UTF-8`、ヘッダー必須、`true/false` の有効フラグを前提とする
- Java 側の CSV 入出力実装は `Apache Commons CSV` を優先する
- 更新方式は、正常時の全件入れ替えを基本とする
- バリデーションエラーが 1 件でもある場合は更新しない前提で進める
- Python の補助スクリプト実行は `uv` を前提とする
- 初回セットアップや依存更新時は `uv sync --group dev` を実行する
- `docs/openapi.yaml` または `templates/openapi_preview.html.j2` を変更したら、`uv run python3 scripts/validate_openapi.py` で検証し、`uv run python3 scripts/render_openapi_preview.py` で `docs/openapi-preview.html` を再生成する
