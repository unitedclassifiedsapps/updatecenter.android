package sk.azet.updatecenter

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by droppa on 5/3/2018.
 */
class UpdateCenterTest {

    @Test
    fun latestVersion() {
        val dataSource = setUpDataSource(
            SemanticVersion(1, 1, 1),
            SemanticVersion.fromVersionString("1.1.1"),
            false,
            false
        )
        val listener = TestVersionCheckListener()
        val uc = UpdateCenter(dataSource, listener)
        uc.check()

        assertTrue(listener.mustUpdateCallCount == 0)
        assertTrue(listener.shouldUpdateCallCount == 0)
        assertTrue(listener.notLatestCallCount == 0)
    }

    @Test
    fun notLatestVersion() {
        val dataSource = setUpDataSource(
            SemanticVersion(1, 1, 1),
            SemanticVersion.fromVersionString("1.1.3"),
            false,
            false
        )
        val listener = TestVersionCheckListener()
        val uc = UpdateCenter(dataSource, listener)
        uc.check()

        assertTrue(listener.mustUpdateCallCount == 0)
        assertTrue(listener.shouldUpdateCallCount == 0)
        assertTrue(listener.notLatestCallCount == 1)

        uc.check()
        assertTrue(listener.mustUpdateCallCount == 0)
        assertTrue(listener.shouldUpdateCallCount == 0)
        assertTrue(listener.notLatestCallCount == 2)
    }

    @Test
    fun mustUpdateVersion1() {
        val dataSource = setUpDataSource(
            SemanticVersion(1, 1, 1),
            SemanticVersion.fromVersionString("1.1.1"),
            false,
            true
        )
        val listener = TestVersionCheckListener()
        val uc = UpdateCenter(dataSource, listener)
        uc.check()

        assertTrue(listener.mustUpdateCallCount == 1)
        assertTrue(listener.shouldUpdateCallCount == 0)
        assertTrue(listener.notLatestCallCount == 0)
    }

    @Test
    fun mustUpdateVersion2() {
        val dataSource = setUpDataSource(
            SemanticVersion(1, 1, 1),
            SemanticVersion.fromVersionString("1.1.2"),
            true,
            true
        )
        val listener = TestVersionCheckListener()
        val uc = UpdateCenter(dataSource, listener)
        uc.check()

        assertTrue(listener.mustUpdateCallCount == 1)
        assertTrue(listener.shouldUpdateCallCount == 0)
        assertTrue(listener.notLatestCallCount == 0)
    }

    @Test
    fun shouldUpdate1() {
        val dataSource = setUpDataSource(
            SemanticVersion(1, 1, 1),
            SemanticVersion.fromVersionString("1.1.1"),
            true,
            false
        )
        val listener = TestVersionCheckListener()
        val uc = UpdateCenter(dataSource, listener)
        uc.check()

        assertTrue(listener.mustUpdateCallCount == 0)
        assertTrue(listener.shouldUpdateCallCount == 1)
        assertTrue(listener.notLatestCallCount == 0)
    }

    @Test
    fun shouldUpdate2() {
        val dataSource = setUpDataSource(
            SemanticVersion(1, 1, 1),
            SemanticVersion.fromVersionString("1.1.2"),
            true,
            false
        )
        val listener = TestVersionCheckListener()
        val uc = UpdateCenter(dataSource, listener)
        uc.check()

        assertTrue(listener.mustUpdateCallCount == 0)
        assertTrue(listener.shouldUpdateCallCount == 1)
        assertTrue(listener.notLatestCallCount == 0)
    }

    @Test
    fun errorTest() {
        val dataSource = setUpErrorDataSource()
        val listener = TestVersionCheckListener()
        val uc = UpdateCenter(dataSource, listener)
        uc.check()

        assertTrue(listener.mustUpdateCallCount == 0)
        assertTrue(listener.shouldUpdateCallCount == 0)
        assertTrue(listener.notLatestCallCount == 0)
        assertTrue(listener.errorCallCount == 1)
    }

    private fun setUpDataSource(
        currentVersion: SemanticVersion,
        latestVersion: SemanticVersion,
        shouldUpdateValue: Boolean,
        mustUpdateValue: Boolean
    ) = object : VersionDataSource {
        override val currentLocalVersion: SemanticVersion
            get() = currentVersion
        override val latestVersion: SemanticVersion
            get() = latestVersion
        override val shouldUpdateValue: Boolean
            get() = shouldUpdateValue
        override val mustUpdateValue: Boolean
            get() = mustUpdateValue

        override fun getUpdateValues(onSuccess: (Boolean, Boolean, SemanticVersion, SemanticVersion) -> Unit, onError: (SemanticVersion, Throwable?) -> Unit) {
            onSuccess(
                mustUpdateValue,
                shouldUpdateValue,
                currentVersion,
                latestVersion
            )
        }
    }

    private fun setUpErrorDataSource() = object : VersionDataSource {
        override val currentLocalVersion: SemanticVersion
            get() = SemanticVersion(1, 1, 1)
        override val latestVersion: SemanticVersion
            get() = SemanticVersion(1, 1, 1)
        override val shouldUpdateValue: Boolean
            get() = false
        override val mustUpdateValue: Boolean
            get() = false

        override fun getUpdateValues(onSuccess: (Boolean, Boolean, SemanticVersion, SemanticVersion) -> Unit, onError: (SemanticVersion, Throwable?) -> Unit) {
            onError(currentLocalVersion, null)
        }
    }

    private class TestVersionCheckListener : OnVersionCheckedListener {
        var notLatestCallCount = 0
        var shouldUpdateCallCount = 0
        var mustUpdateCallCount = 0
        var errorCallCount = 0
        override fun notLatestVersion(currentVersion: String, latestVersion: String) {
            notLatestCallCount++
        }

        override fun shouldUpdate(currentVersion: String, latestVersion: String) {
            shouldUpdateCallCount++
        }

        override fun mustUpdate(currentVersion: String, latestVersion: String) {
            mustUpdateCallCount++
        }

        override fun onError(currentVersion: String, exception: Throwable?) {
            errorCallCount++
        }
    }
}
