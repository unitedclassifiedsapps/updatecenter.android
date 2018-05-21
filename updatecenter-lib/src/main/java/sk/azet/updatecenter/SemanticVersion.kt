package sk.azet.updatecenter

/**
 * Comparable semantic version representation.
 * [Specification](https://semver.org/)
 */
data class SemanticVersion @JvmOverloads constructor(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val tag: String? = null
) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion): Int {
        if (major > other.major) return 1
        if (major < other.major) return -1
        if (minor > other.minor) return 1
        if (minor < other.minor) return -1
        if (patch > other.patch) return 1
        if (patch < other.patch) return -1
        return if (tag == null) {
            if (other.tag == null) 0
            else 1
        } else {
            if (other.tag == null) -1
            else tag.compareTo(other.tag)
        }
    }

    override fun toString(): String {
        return "$major.$minor.$patch${if (tag != null) "-$tag" else ""}"
    }

    companion object {

        /**
         * Method used for generating SemanticVersion instance from string.
         * @param versionString string representation of semantic version (1.1.0, 1.1.1-alfa), missing or incorrect values are replaced by 0, missing tag is null.
         * @return new SemanticVersion instance.
         */
        @JvmStatic
        fun fromVersionString(versionString: String): SemanticVersion {

            var major: Int
            var minor: Int
            var patch: Int
            var tag: String? = null

            val versionIdentifiers = versionString.split("-", limit = 2)

            if (versionIdentifiers.size > 1) tag = versionIdentifiers[1]

            val numberIdentifiers = versionIdentifiers[0].split(".", limit = 3)

            major = numberIdentifiers[0].toIntOrNull() ?: 0
            minor = numberIdentifiers.getOrNull(1)?.toIntOrNull() ?: 0
            patch = numberIdentifiers.getOrNull(2)?.toIntOrNull() ?: 0

            return SemanticVersion(major, minor, patch, tag)
        }
    }
}