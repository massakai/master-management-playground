# 商品カテゴリマスター管理システム MVP 基本設計

## 1. 目的

本ドキュメントは、`docs/requirements.md` をもとに、商品カテゴリマスター管理システム MVP の基本設計を定義する。

MVP では、単一の「商品カテゴリマスター」を対象に、一覧表示、CSV ダウンロード、CSV アップロードによる全件入れ替え更新を実現する。

## 2. 対象範囲

### 2.1 スコープ内

- 商品カテゴリ一覧画面
- CSV ダウンロード
- CSV アップロード
- CSV バリデーション
- 正常時の全件入れ替え更新
- エラー表示

### 2.2 スコープ外

- 複数マスター対応
- 個別登録、個別更新、個別削除
- 認可、承認ワークフロー
- 監査ログの本格導入
- 外部システム連携

## 3. システム構成

### 3.1 論理構成

- フロントエンド: `Vue.js 3`
- バックエンド: `Spring Boot`
- データアクセス: `MyBatis`
- CSV 入出力: `Apache Commons CSV`
- データベース: `H2`

### 3.2 構成方針

- フロントエンドは単一画面で一覧表示と CSV 操作を提供する
- バックエンドは REST API を提供する
- バックエンドは DDD を意識し、プレゼンテーション層、アプリケーション層、ドメイン層、インフラストラクチャ層に責務分離する
- CSV アップロード時のバリデーションと全件更新はバックエンドで実施する
- DB 更新はトランザクション制御し、失敗時は全件ロールバックする
- 将来の DB 切り替えを意識し、H2 固有 SQL への依存は避ける

## 4. 画面設計

### 4.1 画面一覧

- 商品カテゴリ一覧画面

MVP では 1 画面構成とし、以下の 3 領域で構成する。

- ヘッダー領域
- CSV 操作領域
- 一覧表示領域

### 4.2 画面レイアウト

#### ヘッダー領域

- 画面タイトル: 「商品カテゴリマスター管理」
- 補足文言: CSV ダウンロードと CSV アップロードで一括更新できることを説明

#### CSV 操作領域

- CSV ダウンロードボタン
- CSV ファイル選択
- CSV アップロード実行ボタン
- 処理結果メッセージ表示エリア

#### 一覧表示領域

- 商品カテゴリ一覧テーブル
- 表示列:
  - `category_code`
  - `category_name`
  - `display_order`
  - `is_active`
  - `description`

### 4.3 画面状態

- 初期表示:
  - 一覧を取得してテーブル表示する
  - CSV 未選択状態でアップロードボタンは非活性とする
- アップロード中:
  - 二重送信防止のためアップロードボタンを非活性とする
  - 任意でローディング表示を出す
- 更新成功時:
  - 完了メッセージを表示する
  - 一覧を再取得し、最新状態を反映する
- 更新失敗時:
  - エラー件数を表示する
  - 行番号、項目名、内容を一覧表示する

### 4.4 画面入出力項目

| 項目 | 種別 | 必須 | 説明 |
| --- | --- | --- | --- |
| CSV ファイル | ファイル入力 | ○ | UTF-8 の CSV ファイル |
| アップロード実行 | ボタン | - | CSV をサーバへ送信 |
| ダウンロード実行 | ボタン | - | 現在のマスターを CSV 取得 |

## 5. データ設計

### 5.1 管理対象エンティティ

商品カテゴリマスターを 1 エンティティとして扱う。

### 5.2 テーブル設計

テーブル名は `product_category` とする。

| 論理名 | 物理名 | 型 | NOT NULL | 主キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| カテゴリコード | `category_code` | `VARCHAR(50)` | ○ | ○ | 一意なカテゴリ識別子 |
| カテゴリ名 | `category_name` | `VARCHAR(100)` | ○ | - | 表示用カテゴリ名 |
| 表示順 | `display_order` | `INTEGER` | ○ | - | 一覧の表示順 |
| 有効フラグ | `is_active` | `BOOLEAN` | ○ | - | 有効状態 |
| 説明 | `description` | `VARCHAR(255)` | - | - | 補足説明 |
| 作成日時 | `created_at` | `TIMESTAMP` | ○ | - | 初回登録日時 |
| 更新日時 | `updated_at` | `TIMESTAMP` | ○ | - | 最終更新日時 |

### 5.3 インデックス方針

- 主キー: `category_code`
- 一覧取得の並び順用に `display_order`, `category_code` の複合インデックスを将来追加候補とする

### 5.4 DB 制約方針

- `category_code` は主キー制約で一意担保する
- `category_name`, `display_order`, `is_active`, `created_at`, `updated_at` は NOT NULL とする
- `description` は NULL 許容とする

## 6. CSV 設計

### 6.1 文字コードと形式

- 文字コード: `UTF-8`
- 区切り文字: `,`
- ヘッダー行: 必須
- 改行コード: `LF` を基準とするが、ライブラリにより `CRLF` も受け入れる
- Java での CSV 読み書きには `Apache Commons CSV` を利用する

### 6.2 列順

CSV の列順は固定とする。

1. `category_code`
2. `category_name`
3. `display_order`
4. `is_active`
5. `description`

### 6.3 サンプル

```csv
category_code,category_name,display_order,is_active,description
FOOD,食品,10,true,食品カテゴリ
BOOK,書籍,20,true,書籍カテゴリ
```

### 6.4 バリデーション仕様

| 項目 | チェック内容 | エラー条件 |
| --- | --- | --- |
| ファイル存在 | ファイルが選択されていること | ファイルなし |
| 文字コード | UTF-8 として読み込めること | 読み込み不能 |
| ヘッダー | 固定ヘッダーと完全一致すること | 列名不一致、列数不一致 |
| データ行数 | 1 行以上あること | ヘッダーのみ |
| `category_code` | 必須、一意、許可文字のみ | 空、重複、不正文字 |
| `category_name` | 必須、空文字不可 | 空 |
| `display_order` | 必須、整数 | 空、整数変換不可 |
| `is_active` | 必須、`true/false` | 空、不正値 |
| `description` | 任意 | なし |

### 6.5 `category_code` の入力制約

- 許可文字: 半角英数字、`_`、`-`
- 正規表現: `^[A-Za-z0-9_-]+$`

## 7. API 設計

API の詳細契約は [openapi.yaml](/Users/massakai/github/master-management-playground/docs/openapi.yaml) を正本とし、本章は補足方針を記載する。

### 7.1 API 一覧

| API 名 | メソッド | パス | 説明 |
| --- | --- | --- | --- |
| 商品カテゴリ一覧取得 | `GET` | `/api/categories` | 商品カテゴリ一覧を返す |
| 商品カテゴリ CSV ダウンロード | `GET` | `/api/categories/csv` | 商品カテゴリ CSV を返す |
| 商品カテゴリ CSV アップロード | `POST` | `/api/categories/csv` | CSV を検証し、正常時に全件更新する |

### 7.2 一覧取得 API

#### リクエスト

- クエリパラメータなし

#### レスポンス

```json
{
  "categories": [
    {
      "categoryCode": "FOOD",
      "categoryName": "食品",
      "displayOrder": 10,
      "isActive": true,
      "description": "食品カテゴリ"
    }
  ]
}
```

#### 並び順

- `display_order` 昇順
- 同順位は `category_code` 昇順

### 7.3 CSV ダウンロード API

#### レスポンス仕様

- `Content-Type`: `text/csv; charset=UTF-8`
- `Content-Disposition`: 添付ファイル
- ファイル名例: `product-categories.csv`

### 7.4 CSV アップロード API

#### リクエスト

- `multipart/form-data`
- パラメータ名: `file`

#### 正常レスポンス

```json
{
  "success": true,
  "message": "商品カテゴリを更新しました。",
  "updatedCount": 2
}
```

#### バリデーションエラーレスポンス

```json
{
  "success": false,
  "message": "CSV にエラーがあります。",
  "errorCount": 2,
  "errors": [
    {
      "rowNumber": 3,
      "field": "display_order",
      "code": "INVALID_INTEGER",
      "message": "整数で入力してください。"
    },
    {
      "rowNumber": 4,
      "field": "is_active",
      "code": "INVALID_BOOLEAN",
      "message": "true または false を入力してください。"
    }
  ]
}
```

#### ステータスコード方針

- `200 OK`: 一覧取得、CSV ダウンロード、CSV 更新成功
- `400 Bad Request`: 入力エラー、CSV バリデーションエラー
- `500 Internal Server Error`: 想定外例外

## 8. 処理設計

### 8.1 一覧取得処理

1. フロントエンドが一覧取得 API を呼ぶ
2. バックエンドが DB から商品カテゴリ一覧を取得する
3. `display_order`, `category_code` でソートした結果を返す

### 8.2 CSV ダウンロード処理

1. フロントエンドが CSV ダウンロード API を呼ぶ
2. バックエンドが DB から一覧を取得する
3. `Apache Commons CSV` で固定ヘッダー順の CSV を生成する
4. UTF-8 でレスポンス返却する

### 8.3 CSV アップロード処理

1. フロントエンドが CSV ファイルをアップロードする
2. バックエンドがファイル存在と拡張子を確認する
3. `Apache Commons CSV` で CSV を UTF-8 で読み込み、ヘッダーとデータ行を検証する
4. 行単位、項目単位のバリデーションを実施する
5. エラーが 1 件でもあれば更新せずにエラー一覧を返す
6. エラーがなければトランザクションを開始する
7. 既存データを全件削除する
8. CSV の全行を新規登録する
9. コミット後、更新件数を返す
10. 失敗時はロールバックし、システムエラーを返す

### 8.4 更新方式の補足

- 全件削除後に全件挿入する単純なリプレース方式とする
- 更新中は中途半端な状態を残さないよう、削除と登録を同一トランザクションで実行する
- 件数が少ない前提のため、MVP では差分更新は採用しない

## 9. バックエンド設計

### 9.1 レイヤ構成

- Presentation
  - Controller と API DTO を配置し、HTTP リクエスト、レスポンスを扱う
- Application
  - UseCase と Application Service を配置し、ユースケース実行とトランザクション制御を担う
- Domain
  - Entity、Value Object、Domain Service、Repository interface を配置し、商品カテゴリの業務ルールを表現する
- Infrastructure
  - MyBatis Mapper、Repository 実装、CSV パーサなど外部技術への接続を担う

### 9.2 主な責務

| レイヤ | 主な責務 |
| --- | --- |
| Presentation | リクエスト受理、DTO 変換、HTTP ステータス制御 |
| Application | 一覧取得、CSV 出力、CSV 更新ユースケース実行、トランザクション制御 |
| Domain | 商品カテゴリの整合性、CSV 行の業務ルール、全件入れ替え時のドメイン判断 |
| Infrastructure | MyBatis による永続化、Apache Commons CSV を使った CSV 入出力、時刻取得など技術実装 |

### 9.3 想定クラス

| 区分 | クラス名候補 | 役割 |
| --- | --- | --- |
| Presentation | `CategoryController` | 一覧 API、CSV API を提供 |
| Presentation | `CategoryResponse` | 一覧レスポンス DTO |
| Presentation | `CategoryCsvUploadResponse` | CSV 更新結果レスポンス DTO |
| Presentation | `CsvValidationErrorResponse` | エラー明細レスポンス DTO |
| Application | `ListCategoriesUseCase` | 商品カテゴリ一覧取得ユースケース |
| Application | `DownloadCategoriesCsvUseCase` | CSV ダウンロードユースケース |
| Application | `UploadCategoriesCsvUseCase` | CSV 検証と全件更新ユースケース |
| Application | `UploadCategoriesCsvCommand` | CSV アップロード入力モデル |
| Domain | `ProductCategory` | 商品カテゴリ集約ルート |
| Domain | `CategoryCode` | カテゴリコード値オブジェクト |
| Domain | `DisplayOrder` | 表示順値オブジェクト |
| Domain | `ProductCategoryRepository` | 永続化の抽象インターフェース |
| Domain | `CategoryCsvValidationService` | CSV のドメインバリデーション |
| Infrastructure | `MyBatisProductCategoryRepository` | Repository の MyBatis 実装 |
| Infrastructure | `ProductCategoryMapper` | 商品カテゴリテーブル操作 |
| Infrastructure | `CsvProductCategoryReader` | Apache Commons CSV で CSV からドメインオブジェクトへ変換 |
| Infrastructure | `CsvProductCategoryWriter` | Apache Commons CSV で商品カテゴリ一覧を CSV 出力 |

### 9.4 レイヤ依存方針

- Presentation は Application にのみ依存する
- Application は Domain に依存し、Infrastructure の具象実装には直接依存しない
- Domain は他レイヤへ依存しない
- Infrastructure は Domain の Repository interface を実装する
- MyBatis の Mapper や DB レコード表現は Domain に持ち込まない

### 9.5 パッケージ構成例

```text
com.example.mastermanagement
  ├─ presentation
  │   └─ category
  ├─ application
  │   └─ category
  ├─ domain
  │   └─ category
  └─ infrastructure
      ├─ persistence
      │   └─ category
      └─ csv
```

### 9.6 ドメイン設計方針

- 商品カテゴリは `ProductCategory` 集約として扱う
- `category_code` は単なる文字列ではなく `CategoryCode` 値オブジェクトで表現する
- `display_order` も整数のまま使わず `DisplayOrder` 値オブジェクト化を検討する
- CSV 構造チェックのうち技術的な部分は Infrastructure または Application で扱い、項目の業務妥当性は Domain で判定する
- 全件入れ替え更新は `UploadCategoriesCsvUseCase` から `ProductCategoryRepository` を通じて実行する

## 10. フロントエンド設計

### 10.1 コンポーネント方針

- 画面全体コンポーネント
- CSV 操作コンポーネント
- 一覧テーブルコンポーネント
- エラー一覧コンポーネント

### 10.2 状態管理

MVP ではローカル state を基本とし、グローバルな状態管理ライブラリは必須としない。

保持する主な状態:

- 商品カテゴリ一覧
- CSV 選択ファイル
- アップロード中フラグ
- 成功メッセージ
- エラーメッセージ
- バリデーションエラー一覧

### 10.3 UI 動作

- 初期表示時に一覧 API を呼ぶ
- ダウンロードボタン押下で CSV API を呼ぶ
- ファイル選択時に選択ファイルを保持する
- アップロード成功後は一覧を再読込する
- アップロード失敗時はエラー表を表示する

## 11. エラー設計

### 11.1 業務エラー

| エラーコード | 内容 | 返却内容 |
| --- | --- | --- |
| `FILE_REQUIRED` | ファイル未選択 | メッセージのみ |
| `INVALID_ENCODING` | UTF-8 読み込み不可 | メッセージのみ |
| `INVALID_HEADER` | ヘッダー不正 | エラー明細あり |
| `EMPTY_DATA` | データ行なし | メッセージのみ |
| `REQUIRED` | 必須項目未入力 | 行番号、項目名、内容 |
| `DUPLICATE_CATEGORY_CODE` | `category_code` 重複 | 行番号、項目名、内容 |
| `INVALID_FORMAT` | 不正文字形式 | 行番号、項目名、内容 |
| `INVALID_INTEGER` | 整数不正 | 行番号、項目名、内容 |
| `INVALID_BOOLEAN` | 真偽値不正 | 行番号、項目名、内容 |

### 11.2 システムエラー

- DB 接続失敗
- トランザクション失敗
- 想定外例外

システムエラー時は利用者向けに汎用メッセージを返し、詳細はサーバログで確認する。

## 12. 非機能設計

### 12.1 性能

- MVP では数百件程度のカテゴリデータを想定する
- 一覧取得は全件表示で許容する
- CSV アップロードも同程度の件数を想定し、同期処理で対応する

### 12.2 可用性

- ローカル利用前提のため高可用構成は対象外とする

### 12.3 運用保守

- アプリケーションログに、CSV アップロード開始、成功、失敗を出力する
- 失敗ログには例外情報を含める
- CSV 内容そのものは秘匿情報ではないが、必要以上に全量ログ出力しない

### 12.4 セキュリティ

- MVP では認証認可は対象外
- ファイルアップロードは CSV のみを想定し、サイズ上限はアプリケーション設定で制限可能にする
- SQL は MyBatis のバインド変数を利用し、文字列連結を避ける

## 13. テスト観点

### 13.1 バックエンド

- 一覧取得が指定順で返ること
- CSV ダウンロードのヘッダー、列順、UTF-8 を満たすこと
- 正常 CSV で全件入れ替え更新できること
- バリデーションエラー時に更新しないこと
- トランザクション失敗時にロールバックされること

### 13.2 フロントエンド

- 初期表示で一覧が表示されること
- CSV ダウンロード操作ができること
- CSV アップロード成功時に完了メッセージと最新一覧が表示されること
- CSV アップロード失敗時にエラー一覧が表示されること

## 14. 今後の拡張余地

- 検索条件の追加
- ページングの追加
- 差分更新方式への変更
- 個別編集画面の追加
- 複数マスターへの一般化
- 認証認可、監査ログの追加

## 15. 未確定事項

- CSV ダウンロード時に UTF-8 BOM を付与するか
- `description` の最大文字数を業務要件としてどこまで許容するか
- 将来 MySQL へ切り替える際の DDL 管理方式を Flyway などで統一するか
