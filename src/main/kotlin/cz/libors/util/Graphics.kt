package cz.libors.util

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.concurrent.Semaphore
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

typealias ColorSchema = (Point, Int) -> Color

class Graphics(
    debugFromStart: Boolean = true,
    val delay: Long = 100,
    val rememberHistory: Boolean = false,
    circles: Boolean = false,
    minBoundingBox: Pair<Point, Point>? = null,
    showEmpty: Boolean = false,
    inverse: Boolean = false,
    val displayLabels: Boolean = true,
    val colorSchema: ColorSchema = ColorSchemas.staticColors(),
    labelColor: Color = Color.GRAY
) {

    private val surface: Surface
    private var frame: Frame? = null
    private val semaphore = Semaphore(1)
    private var debugOn = debugFromStart

    private val snapshots = mutableListOf<Snapshot>()
    private var snapshotIdx = 0
    private var snapshotsCreated = 0

    init {
        val keyListener = MyKeyAdapter({ debugSwitch() }, { previous() }, { next() })
        surface = Surface(keyListener, circles, showEmpty, inverse, minBoundingBox, labelColor)
    }

    private fun init() {
        frame = Frame(surface)
        SwingUtilities.invokeLater {
            frame!!.isVisible = true
        }
        if (debugOn) semaphore.acquire()
    }

    private fun next() {
        if (debugOn) {
            if (snapshotIdx < snapshots.size - 1) {
                snapshotIdx++
                showSnapshot(snapshots[snapshotIdx])
            } else {
                semaphore.release()
            }
        } else {
            debugOn = true
        }
    }

    private fun previous() {
        if (rememberHistory) {
            if (debugOn) {
                if (snapshotIdx > 0) {
                    snapshotIdx--
                    showSnapshot(snapshots[snapshotIdx])
                }
            } else {
                debugOn = true
            }
        }
    }

    private fun debugSwitch() {
        if (debugOn) {
            debugOn = false
            semaphore.release()
        } else {
            debugOn = true
        }
    }

    fun showInts(points: Map<Point, Int>, title: String = "") {
        val showPoints = points.map { ShowPoint(it.key, colorSchema(it.key, it.value), null) }
        showGraphicPoints(showPoints, title)
    }

    fun showChars(points: Map<Point, Char>, order: String = "", showNotStated: Boolean = false, title: String = "") {
        val result = mutableListOf<ShowPoint>()
        val orderIndices = order.toCharArray().mapIndexed { idx, ch -> ch to idx }.toMap()
        val otherIndices = if (!showNotStated && order.isNotEmpty()) mapOf() else
            points.filter { it.value !in orderIndices }
                .map { it.value }.groupingBy { it }.eachCount()
                .map { Pair(it.key, it.value) }
                .sortedByDescending { it.second }
                .mapIndexed { idx, p -> p.first to orderIndices.size + idx}
                .toMap()

        for ((p, ch) in points) {
            var colorIdx = orderIndices[ch]
            if (colorIdx == null) {
                colorIdx = otherIndices[ch]
            }
            if (colorIdx != null) {
                result.add(ShowPoint(p, colorSchema(p, colorIdx), if (displayLabels) ch else null))
            }
        }
        showGraphicPoints(result, title)
    }

    fun showPointLists(bodies: List<Iterable<Point>>, title: String = "") = showBodies(bodies.map { Body(it.toSet()) }, title)
    fun showPoints(points: Iterable<Point>, title: String = "") = showBodies(listOf(Body(points.toSet())), title)

    fun showBodies(bodies: List<Body>, title: String = "") {
        val showPoints = bodies.flatMapIndexed { bodyIdx, body ->
            body.points.map { ShowPoint(it, colorSchema(it, bodyIdx), null) }
        }
        showGraphicPoints(showPoints, title)
    }

    private fun showGraphicPoints(showPoints: List<ShowPoint>, title: String = "") {
        if (frame == null) {
            init()
        }
        val snapshot = Snapshot(title.ifEmpty { snapshotsCreated.toString() }, showPoints)
        snapshotsCreated++
        if (rememberHistory) {
            snapshots.add(snapshot)
            snapshotIdx = snapshots.size - 1
        }
        showSnapshot(snapshot)
        if (debugOn) {
            semaphore.acquire()
        } else {
            Thread.sleep(delay)
        }
    }

    private fun showSnapshot(s: Snapshot) {
        frame!!.title = s.title + if (debugOn) "    (debug)" else "    (running)"
        surface.setPoints(s.points)
    }

    private data class Snapshot(val title: String, val points: List<ShowPoint>)

}

object ColorSchemas {

    private val default_colors = listOf(
        Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.MAGENTA,
        Color.RED, Color.PINK, Color.GRAY, Color.BLACK
    )

    fun white() = StaticColors(listOf(Color.WHITE))::getColor
    fun specials() = StaticColors(listOf(Color.WHITE) + List(10) {Color.YELLOW})::getColor
    fun staticColors() = StaticColors(default_colors)::getColor
    fun staticColors(colors: List<Color>) = StaticColors(colors)::getColor
    fun heatMapColors(min: Int, max: Int, colors: List<Color>, outColor: Color) = HeatMapColors(min, max, colors, outColor)::getColor
    fun heatMapColors(min: Int, max: Int) = HeatMapColors(min, max, listOf(Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED), Color.BLACK)::getColor


    private class HeatMapColors(val min: Int, val max: Int, val colors: List<Color>, val outColor: Color) {
        private val oneRange = (max - min).toFloat() / (colors.size - 1)

        fun getColor(point: Point, value: Int): Color {
            if (value < min || value > max) return outColor
            if (max == value) return colors[colors.size - 1]
            if (min == value) return colors[0]

            val idx1 = ((value - min) / oneRange).toInt()
            val c1 = colors[idx1]
            val c2 = colors[idx1 + 1]
            val ratio = (((value - min) % oneRange) / oneRange)
            return Color(col(c1.red, c2.red, ratio), col(c1.green, c2.green, ratio), col(c1.blue, c2.blue, ratio), 255)
        }

        private fun col(first: Int, second: Int, ratio: Float): Int {
            return (first + (second - first) * ratio).toInt()
        }
    }

    private class StaticColors(val colors: List<Color>) {
        fun getColor(point: Point, value: Int): Color {
            return colors[value % colors.size]
        }
    }

}

private data class ShowPoint(val coords: Point, val color: Color, val label: Char?)

private class Frame(surface: Surface) : JFrame() {

    init {
        setSize(1000, 800)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        add(surface)
    }

}

private class MyKeyAdapter(
    val debugSwitch: () -> Unit,
    val previous: () -> Unit,
    val next: () -> Unit
) : KeyAdapter() {

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> previous()
            KeyEvent.VK_RIGHT -> next()
            KeyEvent.VK_ESCAPE -> {
                println("Stopping")
                System.exit(0)
            }

            KeyEvent.VK_SPACE -> debugSwitch()
        }
    }
}

private class Surface(
    keyListener: KeyListener,
    val circles: Boolean,
    val showEmpty: Boolean,
    val inverse: Boolean,
    val minBoundingBox: Pair<Point, Point>?,
    val labelColor: Color
) : JPanel(),
    ActionListener {

    private val maxPointSize = 50

    private var points: List<ShowPoint> = listOf()
    private var pointSize = 10
    private var myFont = font
    private var fontMetrics = getFontMetrics(myFont)
    private var min = Point(0, 0)
    private var max = Point(0, 0)

    init {
        isFocusable = true
        addKeyListener(keyListener)
    }

    fun setPoints(points: List<ShowPoint>) {
        this.points = points
        this.repaint()
    }

    override fun actionPerformed(e: ActionEvent) {
        println("a")
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        if (points.isEmpty()) return

        min = Point(points.minOf { it.coords.x }, points.minOf { it.coords.y })
        max = Point(points.maxOf { it.coords.x }, points.maxOf { it.coords.y })
        if (minBoundingBox != null) {
            min = Point(min(minBoundingBox.first.x, min.x), min(minBoundingBox.first.y, min.y))
            max = Point(max(minBoundingBox.second.x, max.x), max(minBoundingBox.second.y, max.y))
        }
        val maxBound = Point(max.x + 1, max.y + 1)

        val size = Point(maxBound.x - min.x, maxBound.y - min.y)
        val ratio = Point(width / size.x, height / size.y)

        pointSize = min(min(ratio.x, ratio.y), maxPointSize)
        myFont = font.deriveFont(Font.PLAIN, pointSize * 0.7F)
        fontMetrics = g.getFontMetrics(myFont);

        g2d.paint = Color.WHITE
        g2d.fillRect(tx(min.x), tyNoInverse(min.y), tx(maxBound.x) - tx(min.x), tyNoInverse(maxBound.y) - tyNoInverse(min.y))
        if (showEmpty) {
            val nonEmpty = points.map { it.coords }.toSet()
            for (x in min.x..max.x)
                for (y in min.y..max.y) {
                    val p = Point(x, y)
                    if (!nonEmpty.contains(p)) paintEmptyPoint(p, g2d)
                }
        }
        for (p in points) {
            paintPoint(p, g2d)
        }
        g2d.paint = Color.BLACK
        g2d.font = Font(null, Font.PLAIN, max(pointSize / 3, 18))
        g2d.drawString("${min.x},${min.y}", tx(min.x), ty(min.y) + pointSize * 2 / 3)
        g2d.drawString("${max.x},${max.y}", tx(maxBound.x - 1), ty(max.y) + pointSize * 2 / 3)
    }

    private fun paintPoint(p: ShowPoint, g: Graphics2D) {
        g.paint = p.color
        val x = tx(p.coords.x)
        val y = ty(p.coords.y)
        if (circles) {
            g.fillOval(x, y, pointSize, pointSize)
        } else {
            g.fillRect(x, y, pointSize, pointSize)
        }
        if (pointSize > 8) {
            g.paint = Color.LIGHT_GRAY
            if (circles) {
                g.drawOval(x, y, pointSize, pointSize)
            } else {
                g.drawRect(x, y, pointSize, pointSize)
            }
        }

        if (p.label != null && pointSize > 8) {
            val label = p.label.toString()
            g.paint = labelColor
            val labelX: Int = x + (pointSize - fontMetrics.stringWidth(label)) / 2;
            val labelY: Int = y + ((pointSize - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
            g.font = myFont;
            g.drawString(label, labelX, labelY);
        }
    }

    private fun paintEmptyPoint(p: Point, g: Graphics2D) {
        val x = tx(p.x)
        val y = ty(p.y)

        g.paint = Color.GRAY
        g.fillOval(x + pointSize / 2 - 1, y + pointSize / 2 - 1, 3, 3)
    }

    private inline fun tx(x: Int) = (x - min.x) * pointSize
    private inline fun tyNoInverse(y: Int) = (y - min.y) * pointSize
    private inline fun ty(y: Int) = if (inverse) (max.y - y) * pointSize else (y - min.y) * pointSize

}
