# PC-Com Bot

パソコミ技術部による新たなパソコミ専属 Bot

## token

app/src/main/resources

```
cp example.application-secret.yaml application-secret.yaml
```

token に Discord の Bot Token を入れる
bestanswer にベストアンサーのロール ID を入れる
question に質問フォーラムチャンネルのチャンネル ID を入れる

## 起動

```
./gradlew bootRun --args="--spring.profiles.active=develop"
```
