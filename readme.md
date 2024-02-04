# Getapp SDK Android

Набор библиотек для работы с сервисом [getapp](https://gitflic.ru/project/getapp/getapp-service).
Сейчас в разработке библиотека для реализации подписок через boosty и оплат через yoomoney

## Как использовать SDK

Указываем репозиторий

```kt
repositories {
    maven {
        url = uri("https://repo.getapp.store")
    }
}
```

Указываем зависимости

```kt
implementation 'ru.kovardin:mediation:0.1.0'
```