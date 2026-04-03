package cz.cvut.fel.plichjan

import cz.cvut.fel.plichjan.distmesh.DistMesh2D
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import java.awt.*
import java.awt.event.*
import java.awt.geom.*
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Serializable
import java.util.*
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import javax.swing.*
import kotlin.math.abs
import kotlin.math.min

/**
 * GUI class to test the delaunay_triangulation Triangulation package:
 */
class ViewerFrame : JFrame(), ActionListener, IViewer, Serializable {

    private var dmThread: Thread? = null
    private var shapes: List<Shape>? = null
    private var circles: List<Shape>? = null
    private var fileName = "test.main.kts"
    private var bbox: Rectangle2D? = null
    private var pixelSize: Float = 0f
    private var transform: AffineTransform? = null

    private var _stage: Int = 0

    companion object {
        const val DISMASH = 13
        private const val serialVersionUID = 1L

        @JvmStatic
        fun main(args: Array<String>) {
            val win = ViewerFrame()
            win.start()
        }
    }

    init {
        title = "Delaunay GUI tester"
        addJPanel()
        setSize(500, 500)
        _stage = 0

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                System.exit(0)
            }
        })
    }

    private fun addJPanel() {
        layout = BorderLayout()
        val comp = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g.create() as Graphics2D
                bbox?.let {
                    applyLimits(g2, width, height, it, true)
                }
                this@ViewerFrame.paintComponent(g2)
                g2.dispose()
            }
        }
        add(comp)
    }

    private fun applyLimits(
        g2: Graphics2D, width: Int, height: Int,
        limitsRequested: Rectangle2D, preserveAspect: Boolean
    ) {
        val recLim = Rectangle2D.Double()
        recLim.setRect(limitsRequested)
        if (preserveAspect) {
            val displayAspect = abs(height.toDouble() / width)
            val requestedAspect = abs(recLim.height / recLim.width)
            if (displayAspect > requestedAspect) {
                val excess = recLim.height * (displayAspect / requestedAspect - 1)
                recLim.setRect(recLim.x, recLim.y - excess / 2, recLim.width, recLim.height + excess)
            } else if (displayAspect < requestedAspect) {
                val excess = recLim.width * (requestedAspect / displayAspect - 1)
                recLim.setRect(recLim.x - excess / 2, recLim.y, recLim.width + excess, recLim.height)
            }
        }
        g2.translate(0, height)
        g2.scale(width / recLim.width, -height / recLim.height) // y grown up
        g2.translate(-recLim.x, -recLim.y)
        val pixelWidth = abs(recLim.width / width)
        val pixelHeight = abs(recLim.height / height)
        pixelSize = min(pixelWidth, pixelHeight).toFloat()
        transform = g2.transform
    }

    fun paintComponent(g: Graphics2D) {
        if (_stage == DISMASH) {
            drawDismash(g)
            if (circles != null) {
                drawMidpoints(g)
            }
        }
    }

    private fun drawDismash(g: Graphics2D) {
        g.color = Color.black
        g.stroke = BasicStroke(pixelSize)

        shapes?.let { currentShapes ->
            val list = ArrayList(currentShapes)
            for (t in list) {
                g.draw(t)
            }
        }
    }

    private fun drawMidpoints(g: Graphics2D) {
        g.color = Color.red
        g.stroke = BasicStroke(pixelSize)

        circles?.forEach { t ->
            g.draw(t)
        }
    }

    fun start() {
        createMenuBar()
        isVisible = true
    }

    private fun createMenuBar() {
        val mbar = MenuBar()

        val m = Menu("File")
        val m1 = MenuItem("Open").apply {
            addActionListener(this@ViewerFrame)
            shortcut = MenuShortcut(KeyEvent.VK_O)
        }
        m.add(m1)

        val m2 = MenuItem("Start DistMesh").apply {
            addActionListener(this@ViewerFrame)
            shortcut = MenuShortcut(KeyEvent.VK_E)
        }
        m.add(m2)

        val m6 = MenuItem("Clear").apply {
            addActionListener(this@ViewerFrame)
            shortcut = MenuShortcut(KeyEvent.VK_Q)
        }
        m.add(m6)

        val mExit = MenuItem("Exit").apply {
            addActionListener(this@ViewerFrame)
        }
        m.add(mExit)

        mbar.add(m)
        menuBar = mbar
    }

    override fun actionPerformed(evt: ActionEvent) {
        when (evt.actionCommand) {
            "Open" -> openTextFile()
            "Start DistMesh" -> startDistMesh()
            "Clear" -> {
                _stage = 0
                repaint()
            }
            "Exit" -> System.exit(209)
        }
    }

    private fun openTextFile() {
        _stage = 0
        val d = FileDialog(this, "Open text file", FileDialog.LOAD)
        d.isVisible = true
        val dr = d.directory
        val fi = d.file
        if (fi != null) {
            fileName = dr + fi
            startDistMesh()
        }
    }

    private fun startDistMesh() {
        if (dmThread?.isAlive == true) {
            return
        }

        val runnable = Runnable {
            val distMesh2D = DistMesh2D()
            distMesh2D.setViewer(this@ViewerFrame)

            // Populate ScriptContext for script support
            ScriptContext.distMesh2D = distMesh2D
            ScriptContext.viewFrame = this@ViewerFrame

            val manager = ScriptEngineManager()
            // Prefer kotlin script if possible, or fall back to javascript
            var engine = manager.getEngineByExtension("kts")
            if (engine == null) {
                engine = manager.getEngineByName("JavaScript")
            }
            
            if (engine == null) {
                System.err.println("No script engine found (tried kts, js)")
                return@Runnable
            }

            engine.put("distMesh2D_from_viewer", distMesh2D)
            engine.put("viewFrame", this@ViewerFrame)

            try {
                FileReader(fileName).use { reader ->
                    engine.eval(reader)
                }
            } catch (e: ScriptException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                System.err.format("%1\$tT.%1\$tL done.\n", Calendar.getInstance())
            }
        }
        dmThread = Thread(runnable, "DistMesh2D").apply {
            System.err.format("%1\$tT.%1\$tL start.\n", Calendar.getInstance())
            start()
        }
    }

    override fun setNewPoints(points: List<Point2D>, t: List<IntArray>, bbox: Array<DoubleArray>) {
        _stage = DISMASH
        val newBbox = Rectangle2D.Double(bbox[0][0], bbox[0][1], 0.0, 0.0)
        newBbox.add(bbox[1][0], bbox[1][1])
        this.bbox = newBbox

        val newShapes = ArrayList<Shape>(t.size)
        val newCircles = ArrayList<Shape>()
        for (tr in t) {
            val a = points[tr[0]]
            val b = points[tr[1]]
            val c = points[tr[2]]

            if (tr.size == 3) {
                val path = Path2D.Double(Path2D.WIND_NON_ZERO, 1 + 3)
                path.moveTo(a.x, a.y)
                path.lineTo(b.x, b.y)
                path.lineTo(c.x, c.y)
                path.lineTo(a.x, a.y)
                newShapes.add(path)
            } else {
                val path = Path2D.Double(Path2D.WIND_NON_ZERO, 1 + 3 * 2)
                path.moveTo(a.x, a.y)
                var tmp = getQuadPoint2D(a, b, points[tr[3]])
                path.quadTo(tmp.x, tmp.y, b.x, b.y)
                tmp = getQuadPoint2D(b, c, points[tr[4]])
                path.quadTo(tmp.x, tmp.y, c.x, c.y)
                tmp = getQuadPoint2D(c, a, points[tr[5]])
                path.quadTo(tmp.x, tmp.y, a.x, a.y)
                newShapes.add(path)

                addEllipse(newCircles, points[tr[3]])
                addEllipse(newCircles, points[tr[4]])
                addEllipse(newCircles, points[tr[5]])
            }
        }
        this.shapes = newShapes
        this.circles = newCircles

        repaint()
    }

    private fun addEllipse(shapes: MutableList<Shape>, a: Point2D) {
        val r = (this.pixelSize * 5).toDouble()
        shapes.add(Ellipse2D.Double(a.x - r / 2, a.y - r / 2, r, r))
    }

    override fun drawMesh(mesh: Mesh) {
        var rec: Rectangle2D.Double? = null
        val pdt = ArrayList<Point2D>(mesh.p!!.size)
        for (point in mesh.p!!) {
            val point2d = Point2D.Double(point[0], point[1])
            if (rec == null) {
                rec = Rectangle2D.Double(point[0], point[1], 0.0, 0.0)
            }
            rec.add(point2d)
            pdt.add(point2d)
        }
        if (rec == null) {
            rec = Rectangle2D.Double()
        }
        this.setNewPoints(
            pdt, mesh.t!!.toList(),
            arrayOf(doubleArrayOf(rec.minX, rec.minY), doubleArrayOf(rec.maxX, rec.maxY))
        )
    }

    private fun getQuadPoint2D(a: Point2D, b: Point2D, ab: Point2D): Point2D {
        return Point2D.Double(
            (4 * ab.x - a.x - b.x) / 2,
            (4 * ab.y - a.y - b.y) / 2
        )
    }
}
