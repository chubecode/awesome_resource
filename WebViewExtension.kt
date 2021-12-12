
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
