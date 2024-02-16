package ru.kovardin.mediation

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.services.NetworkHandler
import ru.kovardin.mediation.services.NetworkResponse
import ru.kovardin.mediation.services.NetworksService

class Mediation {
    private val tag = "Mediation"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val service = NetworksService()

    var adapters = mapOf<String, MediationAdapter>()

    // TODO нужно добавить callback на инициализацию всех SDK
    private fun init(context: Context, app: String, adapters: Map<String, MediationAdapter>) {
        this.adapters = adapters

        scope.launch {
            // загружаем список подключенных медиаций и их ключи для инициализации
            service.fetch(app, object : NetworkHandler {
                override fun onFailure(e: Throwable) {
                    Log.e(tag, e.message.toString())
                }

                override fun onSuccess(resp: NetworkResponse) {
                    // инициализируем только те sdk, которые добавлены в приложение и указаны на стороне сервера
                    launch(Dispatchers.Main) {
                        // большинство адаптеров нужно инитить на главном треде
                        // возможно, нужно будет перенести внутрь адаптеров
                        for (network in resp.networks) {
                            if (this@Mediation.adapters.containsKey(network.name)) {
                                val adapter = this@Mediation.adapters[network.name]
                                adapter?.init(context, network.key)
                            }
                        }
                    }
                }
            })
        }
    }

    companion object {
        lateinit var instance: Mediation

        fun init(context: Context, app: String, adapters: Map<String, MediationAdapter>) {
            instance = Mediation()
            instance.init(context = context, app = app, adapters = adapters)
        }
    }
}