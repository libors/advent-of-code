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


class Graphics(
    val debugFromStart: Boolean = true,
    val delay: Long = 100,
    minBoundingBox: Pair<Point, Point>? = null,
    showEmpty: Boolean = false,
    inverse: Boolean = false
) {

    private val surface: Surface
    private var frame: Frame? = null
    private val semaphore = Semaphore(1)
    private var debugOn = debugFromStart

    private val snapshots = mutableListOf<Snapshot>()
    private var snapshotIdx = 0

    private var colors = listOf(Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.MAGENTA,
        Color.RED, Color.PINK, Color.GRAY, Color.BLACK)

    init {
        val keyListener = MyKeyAdapter({ debugSwitch() }, { previous() }, { next() })
        surface = Surface(keyListener, showEmpty, inverse, minBoundingBox)
    }

    private fun init() {
        frame = Frame(surface)
        SwingUtilities.invokeLater {
            frame!!.isVisible = true
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
        }

    }

    private fun previous() {
        if (debugOn) {
            if (snapshotIdx > 0) {
                snapshotIdx--
                showSnapshot(snapshots[snapshotIdx])
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

    fun showChars(points: Map<Point, Char>, order: String, showNotStated: Boolean = false) {
        val charPoints = points.entries.groupBy({ it.value }, { it.key })
        val bodies = order.toCharArray()
            .map { (charPoints[it] ?: emptyList()) }
            .map { Body(it.toSet()) }
        if (showNotStated) {
            val others = charPoints.filter { !order.contains(it.key) }.values
                .sortedByDescending { it.size }
                .map { Body(it.toSet()) }
            showBodies(bodies + others)
        } else {
            showBodies(bodies)
        }
    }

    fun showBodies(bodies: List<Iterable<Point>>) = showBodies(bodies.map { Body(it.toSet()) })
    fun showPoints(points: List<Point>, info: String = "") = showBodies(listOf(Body(points.toSet())), info)

    fun showBodies(bodies: List<Body>, info: String = "") {
        if (frame == null) {
            init()
        }
        val showPoints = bodies.flatMapIndexed { bodyIdx, body ->
            body.points.map { ShowPoint(it, colors[bodyIdx % colors.size]) }
        }
        val snapshot = Snapshot(info, showPoints)
        snapshots.add(snapshot)
        snapshotIdx = snapshots.size - 1
        showSnapshot(snapshot)
        if (debugOn) {
            semaphore.acquire()
        } else {
            Thread.sleep(delay)
        }
    }

    private fun showSnapshot(s: Snapshot) {
        frame!!.title = s.text
        surface.setPoints(s.points)
    }

    private data class Snapshot(val text: String, val points: List<ShowPoint>)

}

private data class ShowPoint(val coords: Point, val color: Color)

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
    val showEmpty: Boolean,
    val inverse: Boolean,
    val minBoundingBox: Pair<Point, Point>?
) : JPanel(),
    ActionListener {

    private val maxPointSize = 50

    private var points: List<ShowPoint> = listOf()
    private var pointSize = 10
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

        g2d.paint = Color.WHITE
        g2d.fillRect(tx(min.x), ty(min.y), tx(maxBound.x) - tx(min.x), ty(maxBound.y) - ty(min.y))
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
        g.fillOval(x, y, pointSize, pointSize)
        if (pointSize > 8) {
            g.paint = Color.LIGHT_GRAY
            g.drawOval(x, y, pointSize, pointSize)
        }
    }

    private fun paintEmptyPoint(p: Point, g: Graphics2D) {
        val x = tx(p.x)
        val y = ty(p.y)

        g.paint = Color.GRAY
        g.fillOval(x + pointSize / 2 - 1, y + pointSize / 2 - 1, 3, 3)
    }

    private inline fun tx(x: Int) = (x - min.x) * pointSize
    private inline fun ty(y: Int) = if (inverse) {
        (max.y - y) * pointSize
    } else (y - min.y) * pointSize

}
