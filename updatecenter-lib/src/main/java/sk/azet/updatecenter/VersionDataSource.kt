package sk.azet.updatecenter

interface VersionDataSource {

    val currentLocalVersion: SemanticVersion

    val latestVersion: SemanticVersion

    val shouldUpdateValue: Boolean

    val mustUpdateValue: Boolean

    /**
     * Signals the VersionDataSource implementation to retrieve current state.
     * @param onSuccess lambda invoked when state retrieval is successful, passing in mustUpdateValue, shouldUpdateValue, currentLocalVersion, latestVersion values
     * @param onError lambda invoked when state retrieval failed, passing in currentLocalVersion value and Throwable
     */
    fun getUpdateValues(onSuccess: (Boolean, Boolean, SemanticVersion, SemanticVersion) -> Unit, onError: (SemanticVersion, Throwable?) -> Unit)
}

