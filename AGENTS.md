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
- ローカル開発 DB は `H2`
- 将来の本番 DB 切り替えを意識し、H2 固有機能への依存は最小化する

## Documentation Rules

- 要件変更時は、関連実装より先に `docs/requirements.md` を更新する
- 追加ドキュメントを作る場合は、まず `docs/` 配下に置く
- ドキュメント名は簡潔にし、リポジトリ名と重複する接頭辞は避けてよい

## Implementation Notes

- MVP では CSV が主役なので、個別編集画面より CSV 入出力の仕様を優先して固める
- CSV 仕様は `UTF-8`、ヘッダー必須、`true/false` の有効フラグを前提とする
- 更新方式は、正常時の全件入れ替えを基本とする
- バリデーションエラーが 1 件でもある場合は更新しない前提で進める
