package sk.azet.updatecenter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by droppa on 5/2/2018.
 */
class SemanticVersionTest {

    @Test
    fun compareTo() {
        val version1 = SemanticVersion(1, 1, 1)
        val version2 = SemanticVersion(1, 1, 1)
        val version3 = SemanticVersion(1, 1, 2)
        val version4 = SemanticVersion(1, 1, 1, "alfa")
        val version5 = SemanticVersion(1, 1, 1, "alfa")
        val version6 = SemanticVersion(1, 1, 1, "beta")
        val version7 = SemanticVersion(2, 1, 1, "beta")
        val version8 = SemanticVersion(2, 2, 1, "beta")

        assertTrue(version1 == version2)
        assertEquals(version1, version2)
        assertTrue(version1 != version3)
        assertNotEquals(version1, version3)
        assertTrue(version1 < version3)
        assertNotEquals(version1, version3)
        assertTrue(version3 > version1)
        assertNotEquals(version3, version1)
        assertTrue(version7 > version5)
        assertNotEquals(version7, version5)
        assertTrue(version5 < version7)
        assertNotEquals(version5, version7)
        assertTrue(version7 < version8)
        assertNotEquals(version7, version8)
        assertTrue(version8 > version7)
        assertNotEquals(version8, version7)

        assertTrue(version4 == version5)
        assertEquals(version4, version5)
        assertTrue(version4 != version6)
        assertNotEquals(version4, version6)
        assertTrue(version3 > version4)
        assertNotEquals(version3, version4)
        assertTrue(version4 < version3)
        assertNotEquals(version4, version3)
        assertTrue(version5 < version6)
        assertNotEquals(version5, version6)
        assertTrue(version6 > version5)
        assertNotEquals(version6, version5)

        assertTrue(version1 > version4)
        assertNotEquals(version1, version4)
        assertTrue(version4 < version1)
        assertNotEquals(version4, version1)
    }

    @Test
    fun dataClassTest() {
        val version = SemanticVersion(1, 1, 1)
        val version2 = version.copy()
        val version3 = version.copy(patch = 2)
        val version4 = version.copy(tag = "alfa")

        assertEquals(version.hashCode(), version2.hashCode())
        assertNotEquals(version.hashCode(), version3.hashCode())
        assertNotEquals(version.hashCode(), version4.hashCode())

        assertEquals(version.minor, version.component1())
        assertEquals(version.major, version.component2())
        assertEquals(version.patch, version.component3())
        assertEquals(version.tag, version.component4())
    }

    @Test
    fun toStringTest() {
        val version1 = SemanticVersion(1, 1, 1)
        assertEquals(version1.toString(), "1.1.1")
        val version2 = SemanticVersion(1, 1, 1, "alfa")
        assertEquals(version2.toString(), "1.1.1-alfa")
    }

    @Test
    fun parseTest() {
        assertEquals(SemanticVersion(1, 1, 1, "alpha"), SemanticVersion.fromVersionString("1.1.1-alpha"))

        assertEquals(SemanticVersion(1, 1, 1), SemanticVersion.fromVersionString("1.1.1"))

        assertEquals(SemanticVersion.fromVersionString("1.1"), SemanticVersion(1, 1, 0))

        assertEquals(SemanticVersion.fromVersionString("1.1.0.1"), SemanticVersion(1, 1, 0))

        assertEquals(SemanticVersion.fromVersionString("1.1.a"), SemanticVersion(1, 1, 0))

        assertEquals(SemanticVersion.fromVersionString("1.a.a"), SemanticVersion(1, 0, 0))

        assertEquals(SemanticVersion.fromVersionString("a.a.a"), SemanticVersion(0, 0, 0))

        assertEquals(SemanticVersion.fromVersionString(""), SemanticVersion(0, 0, 0))
    }
}