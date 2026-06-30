# Frontend

商品カテゴリマスター管理 UI の Vue.js 3 フロントエンドです。

## Requirements

- Node.js 24 LTS
- npm

リポジトリルートの `.node-version` で Node.js 24 を指定しています。

## Setup

```sh
cd frontend
npm install
```

## Start

先にバックエンドを `localhost:8080` で起動します。

```sh
cd backend
./gradlew bootRun
```

別のターミナルでフロントエンドを起動します。

```sh
cd frontend
npm run dev
```

起動後、画面は次の URL で確認できます。

- `http://localhost:5173/`

開発サーバーでは `/api` へのリクエストを `http://localhost:8080` に proxy します。

## Build

```sh
cd frontend
npm run build
```

`vue-tsc --noEmit` による型チェック後、`dist/` にビルド成果物を生成します。

## Test

画面テストを実行する場合は次を実行します。

```sh
cd frontend
npm test
```

Vitest と Vue Test Utils を使い、jsdom 上で Vue コンポーネントの画面表示と操作を検証します。

## Preview

ビルド済みの成果物をローカルで確認する場合は次を実行します。

```sh
cd frontend
npm run preview
```

## Scripts

- `npm run dev`: Vite 開発サーバーを起動する
- `npm run build`: 型チェックと本番ビルドを実行する
- `npm test`: 画面テストを実行する
- `npm run preview`: ビルド済み成果物を preview する
- `npm run typecheck`: TypeScript / Vue の型チェックだけを実行する

## API

フロントエンドは次の API を利用します。

- `GET /api/categories`: 商品カテゴリ一覧取得
- `GET /api/categories/csv`: CSV ダウンロード
- `POST /api/categories/csv`: CSV アップロード

API 仕様の正本は `docs/openapi.yaml` です。

## Notes

- CSV アップロードは `multipart/form-data` の `file` フィールドで送信します。
- CSV バリデーションエラーは画面上のエラー表に表示します。
- バックエンドが起動していない場合、一覧取得や CSV 操作は通信エラーになります。
