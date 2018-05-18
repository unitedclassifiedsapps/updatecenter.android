package sk.azet.updatecenter

interface OnVersionCheckedListener {

    fun notLatestVersion(currentVersion: String, latestVersion: String)

    fun shouldUpdate(currentVersion: String, latestVersion: String)

    fun mustUpdate(currentVersion: String, latestVersion: String)

    fun onError(currentVersion: String, exception: Throwable?)
}