# Backend

商品カテゴリマスター管理 API の Spring Boot バックエンドです。

## Requirements

- Java 17

## Start

```sh
cd backend
./gradlew bootRun
```

起動後、API は次の URL で利用できます。

- 一覧取得: `http://localhost:8080/api/categories`
- CSV ダウンロード: `http://localhost:8080/api/categories/csv`
- CSV アップロード: `http://localhost:8080/api/categories/csv`
- H2 Console: `http://localhost:8080/h2-console`

H2 Console の接続情報は次の通りです。

```text
JDBC URL: jdbc:h2:mem:master_management
User Name: sa
Password:
```

## Test

```sh
cd backend
./gradlew test
```

## Notes

- 設定ファイルは `src/main/resources/application.yaml` です。
- 起動時に `schema.sql` と `data.sql` で H2 のテーブルと初期データを作成します。
- CSV ファイル名は `app.csv.filename` で変更できます。
