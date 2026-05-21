class Cache<K : Any, V : Any> {
    private val data = mutableMapOf<K, V>()

    fun put(key: K, value: V) {
        data[key] = value
    }

    fun get(key: K): V? {
        return data[key]
    }

    fun evict(key: K) {
        data.remove(key)
    }

    fun size(): Int {
        return data.size
    }

    fun getOrPut(key: K, default: () -> V): V {
        val value = data[key]

        if (value != null) {
            return value
        }

        val newValue = default()
        data[key] = newValue
        return newValue
    }

    fun transform(key: K, action: (V) -> V): Boolean {
        val value = data[key]

        if (value != null) {
            data[key] = action(value)
            return true
        }

        return false
    }

    fun snapshot(): Map<K, V> {
        return data.toMap()
    }

    fun filterValues(predicate: (V) -> Boolean): Map<K, V> {
        return data.filterValues(predicate)
    }
}

fun main() {
    val words = Cache<String, Int>()

    words.put("kotlin", 1)
    words.put("scala", 1)
    words.put("haskell", 1)

    println("--- Word frequency cache ---")
    println("Size: ${words.size()}")

    println("Frequency of kotlin: ${words.get("kotlin")}")

    println("getOrPut kotlin: ${words.getOrPut("kotlin") { 0 }}")
    println("getOrPut java: ${words.getOrPut("java") { 0 }}")

    println("Size after getOrPut: ${words.size()}")

    println("Transform kotlin (+1): ${words.transform("kotlin") { it + 1 }}")
    println("Transform cobol (+1): ${words.transform("cobol") { it + 1 }}")

    println("Snapshot: ${words.snapshot()}")

    println("Words with count > 0: ${words.filterValues { it > 0 }}")

    println()

    val ids = Cache<Int, String>()

    ids.put(1, "Alice")
    ids.put(2, "Bob")

    println("--- ID registry cache ---")
    println("Id 1 -> ${ids.get(1)}")
    println("Id 2 -> ${ids.get(2)}")

    ids.evict(1)

    println("After evict id 1, size: ${ids.size()}")
    println("Id 1 after evict -> ${ids.get(1)}")
}