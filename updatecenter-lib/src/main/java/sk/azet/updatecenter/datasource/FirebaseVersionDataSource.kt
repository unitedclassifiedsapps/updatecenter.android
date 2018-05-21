package sk.azet.updatecenter.datasource

import android.content.Context
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import sk.azet.updatecenter.BuildConfig
import sk.azet.updatecenter.R
import sk.azet.updatecenter.SemanticVersion
import sk.azet.updatecenter.VersionDataSource

/**
 * [VersionDataSource] implementation, which uses Firebase Remote Config to fetch current state.
 * @property context used to access shared preferences
 * @property remoteConfig instance of [FirebaseRemoteConfig] used to fetch remote config values
 * @property currentVersion to check against
 */
class FirebaseVersionDataSource(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig,
    private val currentVersion: String
) : VersionDataSource {

    override val currentLocalVersion: SemanticVersion
        get() = SemanticVersion.fromVersionString(currentVersion)

    override val mustUpdateValue: Boolean
        get() = remoteConfig.getBoolean(MUST_UPDATE_KEY)

    override val shouldUpdateValue: Boolean
        get() = remoteConfig.getBoolean(SHOULD_UPDATE_KEY)

    override val latestVersion: SemanticVersion
        get() = SemanticVersion.fromVersionString(remoteConfig.getString(LATEST_VERSION_KEY))

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(R.xml.remote_defaults)
    }

    /**
     * Retrieves current update values from Firebase Remote Config.
     * If the update values were successfully fetched, they will be cached for 24 hours.
     * New values are fetched only after 24h or if(currentLocalVersion > storedVersion)
     * @param onSuccess lambda invoked when state retrieval is successful, passing in mustUpdateValue, shouldUpdateValue, currentLocalVersion, latestVersion values
     * @param onError lambda invoked when state retrieval failed, passing in currentLocalVersion value and Throwable
     */
    override fun getUpdateValues(onSuccess: (Boolean, Boolean, SemanticVersion, SemanticVersion) -> Unit, onError: (SemanticVersion, Throwable?) -> Unit) {
        val fetchInterval = getFetchInterval(remoteConfig.info.configSettings.isDeveloperModeEnabled, getStoredVersion())
        remoteConfig.fetch(fetchInterval).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, "Fetch Succeeded")
                remoteConfig.activateFetched()
                Log.i(TAG, "Must update: \"$mustUpdateValue\" Should update: \"$shouldUpdateValue\" Latest Version: \"$latestVersion\"")
                onSuccess(
                    mustUpdateValue,
                    shouldUpdateValue,
                    currentLocalVersion,
                    latestVersion
                )
                storeCurrentVersion()
            } else {
                Log.i(TAG, "Fetch Failed")
                onError(currentLocalVersion, task.exception)
            }
        }
    }

    private fun getFetchInterval(isDeveloperModeEnabled: Boolean, storedVersion: SemanticVersion?): Long {
        if (isDeveloperModeEnabled ||
            storedVersion == null ||
            currentLocalVersion > storedVersion
        ) {
            return 0
        }
        return FETCH_INTERVAL
    }

    private fun storeCurrentVersion() {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit().putString(LOCAL_VERSION_KEY, currentLocalVersion.toString()).apply()
    }

    private fun getStoredVersion(): SemanticVersion? {
        val stringVersion = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(LOCAL_VERSION_KEY, null)
        return stringVersion?.let { SemanticVersion.fromVersionString(stringVersion) }
    }

    companion object {
        const val TAG = "FirebaseDataSource"

        const val PREFERENCES_NAME = "sk.azet.updatecenter.FirebaseVersionDataSource"
        const val LOCAL_VERSION_KEY = "sk.azet.updatecenter.LOCAL_VERSION_KEY"

        const val MUST_UPDATE_KEY = "update_center_must_update"
        const val SHOULD_UPDATE_KEY = "update_center_should_update"
        const val LATEST_VERSION_KEY = "update_center_latest_version"

        const val FETCH_INTERVAL: Long = 24 * 60 * 60
    }
}