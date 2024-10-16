package cz.libors.util

class PlanePrinter(
    val charRepresentation: Map<Long, Char> = mapOf(1L to 'x', 0L to '.'),
    private val zero: Long = 0L,
    private val unknown: (Long) -> Char = { '?' },
    private val inverse: Boolean = false,
    private val bounds: Pair<Point, Vector>? = null
) {

    fun print(bodies: List<Body>): String {
        val plane = mutableMapOf<Point, Long>()
        bodies.forEach { body -> body.points.forEach { p -> plane[p] = 1L } }
        return print(plane)
    }

    fun print(plane: Map<Point, Long>, curPos: Point = Point(Int.MAX_VALUE, Int.MAX_VALUE)): String {
        val chars = charRepresentation + (-100L to '=') + (-101L to '|')
        val sb = StringBuilder()
        val pln = plane.toMutableMap()
        if (bounds != null) {
            for (i in bounds.first.x..bounds.first.x + bounds.second.x) {
                pln[Point(i, bounds.first.y + bounds.second.y)] = -100L
                pln[Point(i, bounds.first.y)] = -100L
            }
            for (i in bounds.first.y..bounds.first.y + bounds.second.y) {
                pln[Point(bounds.first.x, i)] = -101L
                pln[Point(bounds.first.x + bounds.second.x, i)] = -101L
            }
        }
        val minX = pln.keys.minOf { it.x }
        val minY = pln.keys.minOf { it.y }
        val maxX = pln.keys.maxOf { it.x }
        val maxY = pln.keys.maxOf { it.y }
        val interval = if (inverse) maxY.downTo(minY) else minY..maxY
        for (row in interval) {
            for (char in minX..maxX) {
                val value = pln[Point(char, row)] ?: zero
                val ch = if (char == curPos.x && row == curPos.y)
                    '%' else chars[value] ?: unknown(value)
                sb.append(ch)
            }
            sb.append("\n")
        }
        return sb.toString()
    }

}