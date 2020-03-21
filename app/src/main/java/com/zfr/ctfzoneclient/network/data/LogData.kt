package com.zfr.ctfzoneclient.network.data

enum class LogType(log_type: String) {
    verbose("verbose"),
    info("info"),
    debug("debug"),
    warning("warning"),
    error("error"),
    assert("assert")
}


data class LogNetworkEntity (
    val log_type: LogType = LogType.info,
    val log_message: String
)