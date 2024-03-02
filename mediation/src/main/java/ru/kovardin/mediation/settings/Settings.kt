package ru.kovardin.mediation.settings

import org.json.JSONObject

data object Settings {
    val base: String = "https://service.getapp.store/v1"
//    val base: String = "http://10.0.2.2:3333/v1"

    val mediationParam = JSONObject().let {
        it.putOpt("mediationName", "getapp"); // admob、meta etc.
        it.putOpt("mediationVersion", "1.0.0"); // 12.1.0
        it.putOpt("adapterVersion", "1.0.0"); // 12.1.0.0
    }
}