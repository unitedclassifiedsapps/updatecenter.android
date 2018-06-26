package sk.azet.updatecenter

/**
 * Created by droppa on 4/30/2018.
 */
class UpdateCenter(val versionDataSource: VersionDataSource, val onVersionCheckedListener: OnVersionCheckedListener) {

    /**
     * This method is used to check update values provided by [VersionDataSource] implementation.
     * Based on current VersionDataSource state, one of the [OnVersionCheckedListener] callbacks is invoked.
     */
    fun check() {
        versionDataSource.getUpdateValues(
            onSuccess = { mustUpdate: Boolean, shouldUpdate: Boolean, currentVersion: SemanticVersion, latestVersion: SemanticVersion ->
                if (versionDataSource.currentLocalVersion < latestVersion) {
                    when {
                        mustUpdate -> onVersionCheckedListener.mustUpdate(currentVersion.toString(), latestVersion.toString())
                        shouldUpdate -> onVersionCheckedListener.shouldUpdate(currentVersion.toString(), latestVersion.toString())
                        else -> onVersionCheckedListener.notLatestVersion(currentVersion.toString(), latestVersion.toString())
                    }
                }
            },
            onError = { currentVersion: SemanticVersion, exception ->
                onVersionCheckedListener.onError(currentVersion.toString(), exception)
            }
        )
    }
}

