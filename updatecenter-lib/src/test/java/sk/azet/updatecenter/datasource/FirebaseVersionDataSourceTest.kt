package sk.azet.updatecenter.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import sk.azet.updatecenter.SemanticVersion

@RunWith(MockitoJUnitRunner::class)
class FirebaseVersionDataSourceTest {

    @Mock
    lateinit var context: Context

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var mockRemoteConfig: FirebaseRemoteConfig

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var editor: SharedPreferences.Editor

    @Mock
    lateinit var mockTask: Task<Void>

    @Captor
    lateinit var onSuccessListener: ArgumentCaptor<OnCompleteListener<Void>>

    lateinit var dataSource: FirebaseVersionDataSource

    @Before
    fun init() {
        `when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPreferences)

        `when`(mockRemoteConfig.getBoolean(FirebaseVersionDataSource.MUST_UPDATE_KEY)).thenReturn(false)
        `when`(mockRemoteConfig.getBoolean(FirebaseVersionDataSource.SHOULD_UPDATE_KEY)).thenReturn(false)
        `when`(mockRemoteConfig.getString(FirebaseVersionDataSource.LATEST_VERSION_KEY)).thenReturn("0.0.0")
        `when`(mockRemoteConfig.info.configSettings.isDeveloperModeEnabled).thenReturn(false)
        `when`(mockRemoteConfig.fetch(ArgumentMatchers.anyLong())).thenReturn(mockTask)

        `when`(mockTask.addOnCompleteListener(onSuccessListener.capture())).thenReturn(mockTask)

        dataSource = FirebaseVersionDataSource(context, mockRemoteConfig, "0.0.0")
    }

    @Test
    fun successfulFetch() {
        `when`(sharedPreferences.getString(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(null)
        `when`(mockTask.isSuccessful).thenReturn(true)

        var succesCount = 0
        var failedCount = 0

        dataSource.getUpdateValues(
            onSuccess = { mustUpdate: Boolean, shouldUpdate: Boolean, currentVersion: SemanticVersion, latestVersion: SemanticVersion ->
                succesCount++
            },
            onError = { currentVersion: SemanticVersion, exception ->
                failedCount++
            })
        onSuccessListener.value.onComplete(mockTask)

        Assert.assertEquals(succesCount, 1)
        Assert.assertEquals(failedCount, 0)
    }

    @Test
    fun failedFetch() {
        `when`(sharedPreferences.getString(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn("0.0.0")
        `when`(mockTask.isSuccessful).thenReturn(false)

        var successCount = 0
        var failedCount = 0

        dataSource.getUpdateValues(
            onSuccess = { mustUpdate: Boolean, shouldUpdate: Boolean, currentVersion: SemanticVersion, latestVersion: SemanticVersion ->
                successCount++
            },
            onError = { currentVersion: SemanticVersion, exception ->
                failedCount++
            })
        onSuccessListener.value.onComplete(mockTask)

        Assert.assertEquals(successCount, 0)
        Assert.assertEquals(failedCount, 1)
    }
}