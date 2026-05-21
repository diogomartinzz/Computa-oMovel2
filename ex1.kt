sealed class Event {
    data class Login(val username: String, val timestamp: Long) : Event()
    data class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()
    data class Logout(val username: String, val timestamp: Long) : Event()
}

fun List<Event>.filterByUser(name: String): List<Event> {
    return this.filter { event ->
        when (event) {
            is Event.Login -> event.username == name
            is Event.Purchase -> event.username == name
            is Event.Logout -> event.username == name
        }
    }
}

fun List<Event>.totalSpent(name: String): Double {
    return this
        .filterIsInstance<Event.Purchase>()
        .filter { it.username == name }
        .sumOf { it.amount }
}

fun processEvents(events: List<Event>, handler: (Event) -> Unit) {
    for (event in events) {
        handler(event)
    }
}

fun main() {
    val events = listOf(
        Event.Login("alice", 1000),
        Event.Purchase("alice", 49.9, 1100),
        Event.Purchase("bob", 19.99, 1200),
        Event.Login("bob", 1050),
        Event.Purchase("alice", 15.0, 1300),
        Event.Logout("alice", 1400),
        Event.Logout("bob", 1500)
    )

    processEvents(events) { event ->
        when (event) {
            is Event.Login ->
                println("[LOGIN] ${event.username} logged in at t=${event.timestamp}")

            is Event.Purchase ->
                println("[PURCHASE] ${event.username} spent $${event.amount} at t=${event.timestamp}")

            is Event.Logout ->
                println("[LOGOUT] ${event.username} logged out at t=${event.timestamp}")
        }
    }

    println()
    println("Total spent by alice: $${events.totalSpent("alice")}")
    println("Total spent by bob: $${events.totalSpent("bob")}")

    println()
    println("Events for alice:")
    for (event in events.filterByUser("alice")) {
        println(event)
    }
}