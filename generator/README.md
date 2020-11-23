generator
===

app にプログラムを生成するアプリケーション

生成ルールは以下の通り

- `resources/domains.yaml` にあるクラスを生成する
- リストされたドメイン名1つに付き、以下のクラスを1つずつ作成する
    - `{Name}` クラス(内部は次の Id クラスをフィールドに持つ)
    - `{Name}Id` クラス(内部は `long` の `value` というフィールドを持つ)
    - `{Name}Service` インターフェース(`{Name} findById({Name}Id)`　と `{Name}Id createNew()` というメソッドを持つ)
    - `Map{Name}Service` クラス - `{Name}Service` の実装クラスで内部に `Map<{Name}Id, {Name}>` を持つ
    - `{Name}Controller` クラス - `Response<{Name}> get(long)`/`Response<Void> create()` メソッドがあり、フィールドの `{Name}Service` を利用するクラス
- `Map{Name}Service` のインスタンスを生成するだけのクラスを生成する
- 生成されたクラスファイルは `app` プロジェクトの `src/main/generated` ディレクトリーに格納される
- 生成されるクラスのパッケージは `com.example.generated`

