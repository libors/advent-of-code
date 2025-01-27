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
import kotlin.math.abs

typealias ColorSchema = (Point, Int) -> Color

class Graphics(
    debugFromStart: Boolean = true,
    val delay: Long = 100,
    val rememberHistory: Boolean = false,
    circles: Boolean = false,
    var window: Pair<Point, Point>? = null,
    showEmpty: Boolean? = null,
    inverse: Boolean = false,
    val displayLabels: Boolean = true,
    val colorSchema: ColorSchema = ColorSchemas.staticColors(),
    val charOrder: String = "",
    labelColor: Color = Color.GRAY,
    val adventTheme: Boolean = false,
) {

    private val surface: Surface
    private var frame: Frame? = null
    private val semaphore = Semaphore(1)
    private var debugOn = debugFromStart

    private val snapshots = mutableListOf<Snapshot>()
    private var snapshotIdx = 0
    private var snapshotsCreated = 0

    init {
        val keyListener = MyKeyAdapter(::debugSwitch, ::previous, ::next, ::moveWindow)
        surface = Surface(keyListener, circles, showEmpty?:adventTheme, inverse, window, labelColor, adventTheme)
    }

    private fun init() {
        frame = Frame(surface)
        SwingUtilities.invokeLater {
            frame!!.isVisible = true
        }
        if (debugOn) semaphore.acquire()
    }

    private fun moveWindow(dir: Vector) {
        if (window != null) {
            when (dir) {
                Vector.DOWN, Vector.UP, Vector.LEFT, Vector.RIGHT -> {
                    val amount = 1
                    window = Pair(window!!.first + dir * amount, window!!.second + dir * amount)
                    surface.updateWindow(window!!)
                }
            }
        }
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

    fun showChars(
        points: Map<Point, Char>,
        showNotStated: Boolean = false,
        title: String = "",
        percents: Map<Point, Int>? = null,
        visiblePoint: Point? = null
    ) {
        val result = mutableListOf<ShowPoint>()
        val orderIndices = charOrder.toCharArray().mapIndexed { idx, ch -> ch to idx }.toMap()
        val otherIndices = if (!showNotStated && charOrder.isNotEmpty()) mapOf() else
            points.filter { it.value !in orderIndices }
                .map { it.value }.groupingBy { it }.eachCount()
                .map { Pair(it.key, it.value) }
                .sortedByDescending { it.second }
                .mapIndexed { idx, p -> p.first to orderIndices.size + idx }
                .toMap()

        for ((p, ch) in points) {
            var colorIdx = orderIndices[ch]
            if (colorIdx == null) {
                colorIdx = otherIndices[ch]
            }
            if (colorIdx != null) {
                result.add(ShowPoint(p, colorSchema(p, colorIdx), if (displayLabels) ch else null, percents?.get(p)))
            }
        }
        showGraphicPoints(result, title, visiblePoint)
    }

    fun showPointLists(bodies: List<Iterable<Point>>, title: String = "") =
        showBodies(bodies.map { Body(it.toSet()) }, title)

    fun showPoints(points: Iterable<Point>, title: String = "") = showBodies(listOf(Body(points.toSet())), title)

    fun showBodies(bodies: List<Body>, title: String = "") {
        val showPoints = bodies.flatMapIndexed { bodyIdx, body ->
            body.points.map { ShowPoint(it, colorSchema(it, bodyIdx), null) }
        }
        showGraphicPoints(showPoints, title)
    }

    private fun showGraphicPoints(showPoints: List<ShowPoint>, title: String = "", visiblePoint: Point? = null) {
        if (frame == null) {
            init()
        }
        val snapshot = Snapshot(title.ifEmpty { snapshotsCreated.toString() }, showPoints, visiblePoint)
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
        if (s.visiblePoint != null) adjustWindow(s.visiblePoint)
        surface.setPoints(s.points)
    }

    private fun adjustWindow(visiblePoint: Point) {
        if (window != null) {
            var inner = innerWindow(window!!)
            while (!inner.contains(visiblePoint)) {
                if (visiblePoint.x < inner.first.x) moveWindow(Vector.LEFT)
                if (visiblePoint.x > inner.second.x) moveWindow(Vector.RIGHT)
                if (visiblePoint.y < inner.first.y) moveWindow(Vector.UP)
                if (visiblePoint.y > inner.second.y) moveWindow(Vector.DOWN)
                inner = innerWindow(window!!)
            }
        }
    }

    private fun innerWindow(w: Box): Box {
        val size = w.size()
        val bufferX = min(5, size.first / 5)
        val bufferY = min(5, size.second / 5)
        return Pair(Point(w.first.x + bufferX, w.first.y + bufferY), Point(w.second.x - bufferX, w.second.y - bufferY))
    }

    private data class Snapshot(val title: String, val points: List<ShowPoint>, val visiblePoint: Point?)

}

object ColorSchemas {

    private val default_colors = listOf(
        Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.MAGENTA,
        Color.RED, Color.PINK, Color.GRAY, Color.BLACK
    )

    fun white() = StaticColors(listOf(Color.WHITE))::getColor
    fun specials() = StaticColors(listOf(Color.WHITE) + List(10) { Color.YELLOW })::getColor
    fun staticColors() = StaticColors(default_colors)::getColor
    fun staticColors(colors: List<Color>, default: Color? = null) = StaticColors(colors, default)::getColor
    fun heatMapColors(min: Int, max: Int, colors: List<Color>, outColor: Color) =
        HeatMapColors(min, max, colors, outColor)::getColor

    fun heatMapColors(min: Int, max: Int) =
        HeatMapColors(min, max, listOf(Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED), Color.BLACK)::getColor


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

    private class StaticColors(val colors: List<Color>, val default: Color? = null) {
        fun getColor(point: Point, value: Int): Color {
            return if (value < colors.size) colors[value] else default ?: colors[value % colors.size]
        }
    }

}

private data class ShowPoint(val coords: Point, val color: Color, val label: Char?, val percent: Int? = null)

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
    val next: () -> Unit,
    val moveWindow: (Vector) -> Unit,
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
            KeyEvent.VK_S -> moveWindow(Vector.DOWN)
            KeyEvent.VK_W -> moveWindow(Vector.UP)
            KeyEvent.VK_A -> moveWindow(Vector.LEFT)
            KeyEvent.VK_D -> moveWindow(Vector.RIGHT)
        }
    }
}

private class Surface(
    keyListener: KeyListener,
    val circles: Boolean,
    val showEmpty: Boolean,
    val inverse: Boolean,
    var window: Pair<Point, Point>?,
    val labelColor: Color,
    val adventTheme: Boolean
) : JPanel(),
    ActionListener {

    private val maxPointSize = 100

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

    fun updateWindow(w: Pair<Point, Point>, repaint: Boolean = true) {
        window = w
        if (repaint) repaint()
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

        val pts = if (window == null) points else points.filter { window!!.contains(it.coords) }
        if (pts.isEmpty()) return

        val allMin = Point(points.minOf { it.coords.x }, points.minOf { it.coords.y })
        val allMax = Point(points.maxOf { it.coords.x }, points.maxOf { it.coords.y })
        val allBox = Box(allMin, allMax)

        if (window != null) {
            min = window!!.first
            max = window!!.second
        } else {
            min = allMin
            max = allMax
        }
        val maxBound = Point(max.x + 1, max.y + 1)

        val size = Point(maxBound.x - min.x, maxBound.y - min.y)
        val ratio = Point(width / size.x, height / size.y)

        pointSize = min(min(ratio.x, ratio.y), maxPointSize)
        myFont = font.deriveFont(Font.PLAIN, pointSize * 0.7F)
        fontMetrics = g.getFontMetrics(myFont);

        g2d.paint = if (adventTheme) Color.BLACK else Color.WHITE
        g2d.fillRect(
            tx(min.x),
            tyNoInverse(min.y),
            tx(maxBound.x) - tx(min.x),
            tyNoInverse(maxBound.y) - tyNoInverse(min.y)
        )
        if (showEmpty) {
            val nonEmpty = pts.map { it.coords }.toSet()
            for (x in min.x..max.x)
                for (y in min.y..max.y) {
                    val p = Point(x, y)
                    if (!nonEmpty.contains(p) && allBox.contains(p)) paintEmptyPoint(p, g2d)
                }
        }
        for (p in pts) {
            paintPoint(p, g2d)
        }
        paintCoordinates(g2d, maxBound)
        showWindowPosition(g2d, maxBound.x, allMin, allMax, min, max)
    }

    private fun showWindowPosition(g2d: Graphics2D, maxBoundX: Int, allMin: Point, allMax: Point, min: Point, max: Point) {
        val totalBox = listOf(allMin, allMax, min, max).boundingBox()
        val totalSize = totalBox.size()
        val fullSize = max(totalSize.first, totalSize.second)
        val ratio = 200.0 / fullSize

        fun len(orig: Int) = abs((ratio * orig).toInt())

        if (allMin != min || allMax != max) {
            g2d.paint = overlayBoxColor()
            g2d.fillRect(tx(maxBoundX) - len(totalSize.first), 0, len(totalSize.first), len(totalSize.second))
            g2d.paint = if (adventTheme) Color.GRAY else Color.LIGHT_GRAY
            g2d.drawRect(tx(maxBoundX) - len(totalBox.second.x - allMin.x), len(allMin.y), len(allMax.x - allMin.x), len(allMax.y - allMin.y))
            g2d.paint = if (adventTheme) Color.WHITE else Color.BLUE
            g2d.drawRect(tx(maxBoundX) - len(totalBox.second.x - min.x), len(min.y),  len(max.x - min.x), len(max.y - min.y))
        }

    }

    private fun paintCoordinates(g2d: Graphics2D, maxBound: Point) {
        g2d.font = Font(null, Font.PLAIN, max(pointSize / 3, 18))
        val minString = "${min.x},${min.y}"
        val maxString = "${max.x},${max.y}"

        fontMetrics = g2d.getFontMetrics(g2d.font)
        val minWidth = fontMetrics.stringWidth(minString)
        val maxWidth = fontMetrics.stringWidth(maxString)
        g2d.paint = overlayBoxColor()
        g2d.fillRect(0, 0, minWidth, pointSize)
        g2d.fillRect(tx(maxBound.x) - maxWidth, ty(max.y), maxWidth, pointSize)

        g2d.paint = if (adventTheme) Color.GRAY else Color.BLACK
        g2d.drawString(minString, tx(min.x), ty(min.y) + pointSize * 2 / 3)
        g2d.drawString(maxString, tx(maxBound.x) - maxWidth, ty(max.y) + pointSize * 2 / 3)
    }

    private fun overlayBoxColor(): Color = if (adventTheme) Color(0, 0, 0, 180) else Color(245, 245, 245, 200)

    private fun paintPoint(p: ShowPoint, g: Graphics2D) {
        g.paint = if (adventTheme) Color.BLACK else p.color
        val x = tx(p.coords.x)
        val y = ty(p.coords.y)
        if (circles) {
            g.fillOval(x, y, pointSize, pointSize)
        } else {
            if (p.percent != null && p.percent != 100) {
                g.paint = if (adventTheme) Color.BLACK else Color.WHITE
                g.fillRect(x, y, pointSize, pointSize)
                g.paint = Color(p.color.red, p.color.green, p.color.blue, 50)
                g.fillRect(x, y + applyPercents(p.percent), pointSize, pointSize)
            } else {
                g.fillRect(x, y, pointSize, pointSize)
            }
        }
        if (pointSize > 8) {
            g.paint = if (adventTheme) Color.DARK_GRAY else Color.LIGHT_GRAY
            if (circles) {
                g.drawOval(x, y, pointSize, pointSize)
            } else {
                g.drawRect(x, y, pointSize, pointSize)
            }
        }

        if (p.label != null && pointSize > 8) {
            val label = p.label.toString()
            g.paint = if (adventTheme) p.color else labelColor
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
    private inline fun applyPercents(percent: Int) = pointSize * (100 - percent) / 100

}
