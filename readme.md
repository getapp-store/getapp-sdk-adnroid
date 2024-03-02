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

## Как SDK доступны

Сейчас дотсупны только три SDK

- Медиация
- Подписки через boosty
- Биллинг с помощью юмани


### Медиация

Подключаем медиацию в проект

```
implementation 'ru.kovardin:mediation:0.1.0'
```

Инициализируем на старте приложения

```kt
Mediation.init(
    applicationContext,
    "1",
    adapters = mapOf(
        "yandex" to YandexAdsAdapter(),
        "mytarget" to MyTargetAdapter(),
        "cpa" to CpaAdapter(),
        "bigo" to BigoAdapter(),
    ),
    callbacks = object : MediationCallbacks {
        override fun onFinish() {
            Log.d(tag, "onFinish")
        }

        override fun onInitialized(network: String) {
            Log.d(tag, "onInitialized")
        }
    }
)
```

Поддерживаемые рекламные сетки:
- Yandex
- MyTarget
- Bigo



### Подписки через boosty

### Биллинг с помощью юмани