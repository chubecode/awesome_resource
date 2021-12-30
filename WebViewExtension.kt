
fun WebView.injectJs(s: String, callback: ((String) -> Unit)? = null) {
    onUiThread {
        if (callback == null) {
            this.evaluateJavascript("(function() {$s})();", null)
        } else {
            this.evaluateJavascript("(function() {$s})();") {
                if (it.startsWith("\"") && it.endsWith("\"")) {
                    callback(it.replace("\\n", "\n").dropLast(1).drop(1))
                } else {
                    callback(it.replace("\\n", "\n"))
                }
            }
        }
    }
}

fun onUiThread(callback: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        callback()
    }
}
fun WebView.playPauseVideo() {
    onUiThread {
        this.evaluateJavascript(
            """document.getElementsByClassName('player-control-play-pause-icon')[0].click()""",
            null
        )
    }
}

fun WebView.previousVideo() {
    onUiThread {
        this.evaluateJavascript(
            """document.getElementsByClassName('player-controls-middle center')[0].children[0].click()""",
            null
        )
    }
}

fun WebView.nextVideo() {
    onUiThread {
        this.evaluateJavascript(
            """document.getElementsByClassName('player-controls-middle center')[0].children[4].click()""",
            null
        )
    }
}

fun WebView.isNextDisable(listener: ValueCallback<Boolean>) {
    onUiThread {
        this.evaluateJavascript(
            """(function() { var element = document.getElementsByClassName('player-controls-middle center')[0].children[0]; return element.className.includes('icon-disable'); })();""",
        ){ isDisable ->
            listener.onReceiveValue(isDisable != "null" && isDisable == "true")
        }
    }
}

fun WebView.isPreviousDisable(listener: ValueCallback<Boolean>) {
    onUiThread {
        this.evaluateJavascript(
            """(function() { var element = document.getElementsByClassName('player-controls-middle center')[0].children[4]; return element.className.includes('icon-disable'); })();""",
        ){ isDisable ->
            listener.onReceiveValue(isDisable != "null" && isDisable == "true")
        }
    }
}

fun WebView.isHome(listener: ValueCallback<Boolean>) {
    //getElementsByClassName better than querySelector(sometime not work)
    onUiThread {
        this.evaluateJavascript(
            """(function() {var element = document.getElementsByClassName('pivot-bar-item-tab').length; return element > 0})();"""
        ) { isHome ->
            listener.onReceiveValue(isHome.toBoolean())
        }
    }
}
