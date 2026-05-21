class Pipeline {
    private val stages = mutableListOf<Pair<String, (List<String>) -> List<String>>>()

    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(Pair(name, transform))
    }

    fun execute(input: List<String>): List<String> {
        var result = input

        for (stage in stages) {
            result = stage.second(result)
        }

        return result
    }

    fun describe() {
        println("Pipeline stages:")

        for (i in stages.indices) {
            println("${i + 1}. ${stages[i].first}")
        }
    }

    fun compose(firstName: String, secondName: String, newName: String): Boolean {
        val first = stages.find { it.first == firstName }
        val second = stages.find { it.first == secondName }

        if (first == null || second == null) {
            return false
        }

        val composed: (List<String>) -> List<String> = { input ->
            second.second(first.second(input))
        }

        stages.add(Pair(newName, composed))
        return true
    }

    fun fork(
        input: List<String>,
        pipeline1: Pipeline,
        pipeline2: Pipeline
    ): Pair<List<String>, List<String>> {
        return Pair(
            pipeline1.execute(input),
            pipeline2.execute(input)
        )
    }
}

fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    val pipeline = Pipeline()
    pipeline.block()
    return pipeline
}

fun main() {
    val logs = listOf(
        "   INFO: server started   ",
        "   ERROR: disk full   ",
        "   DEBUG: checking config   ",
        "   ERROR: out of memory   ",
        "   INFO: request received   ",
        "   ERROR: connection timeout   "
    )

    val pipeline = buildPipeline {
        addStage("Trim") { lines ->
            lines.map { it.trim() }
        }

        addStage("Filter errors") { lines ->
            lines.filter { it.contains("ERROR") }
        }

        addStage("Uppercase") { lines ->
            lines.map { it.uppercase() }
        }

        addStage("Add index") { lines ->
            lines.mapIndexed { index, text ->
                "${index + 1}. $text"
            }
        }
    }

    pipeline.describe()

    println()
    println("Result:")

    val result = pipeline.execute(logs)

    for (line in result) {
        println(line)
    }
}