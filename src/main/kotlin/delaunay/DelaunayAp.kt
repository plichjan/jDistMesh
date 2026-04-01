package delaunay

import java.awt.*
import java.awt.event.*
import java.util.*
import javax.swing.*

/**
 * The Delaunay applet.
 *
 * Creates and displays a Delaunay Triangulation (DT) or a Voronoi Diagram
 * (VoD). Has a main program so it is an application as well as an applet.
 *
 * @author Paul Chew
 * Created July 2005. Derived from an earlier, messier version.
 * Modified December 2007. Updated some of the Triangulation methods. Added the
 * "Colorful" checkbox. Reorganized the interface between DelaunayAp and
 * DelaunayPanel. Added code to find a Voronoi cell.
 * Converted to Kotlin in 2026.
 */
class DelaunayAp : JApplet(), Runnable, ActionListener, MouseListener {

    private val debug = false // Used for debugging
    private var currentSwitch: Component? = null // Entry-switch that mouse is in

    private val voronoiButton = JRadioButton("Voronoi Diagram")
    private val delaunayButton = JRadioButton("Delaunay Triangulation")
    private val clearButton = JButton("Clear")
    private val colorfulBox = JCheckBox("More Colorful")
    private val delaunayPanel = DelaunayPanel(this)
    private val circleSwitch = JLabel("Show Empty Circles")
    private val delaunaySwitch = JLabel("Show Delaunay Edges")
    private val voronoiSwitch = JLabel("Show Voronoi Edges")

    companion object {
        private const val windowTitle = "Voronoi/Delaunay Window"

        /**
         * Main program (used when run as application instead of applet).
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val applet = DelaunayAp() // Create applet
            applet.init() // Applet initialization
            val dWindow = JFrame() // Create window
            dWindow.setSize(700, 500) // Set window size
            dWindow.title = windowTitle // Set window title
            dWindow.layout = BorderLayout() // Specify layout manager
            dWindow.defaultCloseOperation = JFrame.EXIT_ON_CLOSE // Specify closing behavior
            dWindow.add(applet, "Center") // Place applet into window
            dWindow.isVisible = true // Show the window
        }
    }

    /**
     * Initialize the applet.
     * As recommended, the actual use of Swing components takes place in the
     * event-dispatching thread.
     */
    override fun init() {
        try {
            SwingUtilities.invokeAndWait(this)
        } catch (e: Exception) {
            System.err.println("Initialization failure")
        }
    }

    /**
     * Set up the applet's GUI.
     * As recommended, the init method executes this in the event-dispatching
     * thread.
     */
    override fun run() {
        layout = BorderLayout()

        // Add the button controls
        val group = ButtonGroup()
        group.add(voronoiButton)
        group.add(delaunayButton)
        val buttonPanel = JPanel()
        buttonPanel.add(voronoiButton)
        buttonPanel.add(delaunayButton)
        buttonPanel.add(clearButton)
        buttonPanel.add(JLabel("          ")) // Spacing
        buttonPanel.add(colorfulBox)
        this.add(buttonPanel, "North")

        // Add the mouse-entry switches
        val switchPanel = JPanel()
        switchPanel.add(circleSwitch)
        switchPanel.add(Label("     ")) // Spacing
        switchPanel.add(delaunaySwitch)
        switchPanel.add(Label("     ")) // Spacing
        switchPanel.add(voronoiSwitch)
        this.add(switchPanel, "South")

        // Build the delaunay panel
        delaunayPanel.background = Color.gray
        this.add(delaunayPanel, "Center")

        // Register the listeners
        voronoiButton.addActionListener(this)
        delaunayButton.addActionListener(this)
        clearButton.addActionListener(this)
        colorfulBox.addActionListener(this)
        delaunayPanel.addMouseListener(this)
        circleSwitch.addMouseListener(this)
        delaunaySwitch.addMouseListener(this)
        voronoiSwitch.addMouseListener(this)

        // Initialize the radio buttons
        voronoiButton.doClick()
    }

    /**
     * A button has been pressed; redraw the picture.
     */
    override fun actionPerformed(e: ActionEvent) {
        if (debug)
            println((e.source as AbstractButton).text)
        if (e.source === clearButton) delaunayPanel.clear()
        delaunayPanel.repaint()
    }

    /**
     * If entering a mouse-entry switch then redraw the picture.
     */
    override fun mouseEntered(e: MouseEvent) {
        currentSwitch = e.component
        if (currentSwitch is JLabel) delaunayPanel.repaint()
        else currentSwitch = null
    }

    /**
     * If exiting a mouse-entry switch then redraw the picture.
     */
    override fun mouseExited(e: MouseEvent) {
        currentSwitch = null
        if (e.component is JLabel) delaunayPanel.repaint()
    }

    /**
     * If mouse has been pressed inside the delaunayPanel then add a new site.
     */
    override fun mousePressed(e: MouseEvent) {
        if (e.source !== delaunayPanel) return
        val point = Pnt(e.x.toDouble(), e.y.toDouble())
        if (debug) println("Click $point")
        delaunayPanel.addSite(point)
        delaunayPanel.repaint()
    }

    /**
     * Not used, but needed for MouseListener.
     */
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseClicked(e: MouseEvent) {}

    /**
     * @return true iff the "colorful" box is selected
     */
    fun isColorful(): Boolean {
        return colorfulBox.isSelected
    }

    /**
     * @return true iff doing Voronoi diagram.
     */
    fun isVoronoi(): Boolean {
        return voronoiButton.isSelected
    }

    /**
     * @return true iff within circle switch
     */
    fun showingCircles(): Boolean {
        return currentSwitch === circleSwitch
    }

    /**
     * @return true iff within delaunay switch
     */
    fun showingDelaunay(): Boolean {
        return currentSwitch === delaunaySwitch
    }

    /**
     * @return true iff within voronoi switch
     */
    fun showingVoronoi(): Boolean {
        return currentSwitch === voronoiSwitch
    }
}

/**
 * Graphics Panel for DelaunayAp.
 */
class DelaunayPanel(private val controller: DelaunayAp) : JPanel() {

    companion object {
        var voronoiColor: Color = Color.magenta
        var delaunayColor: Color = Color.green
        var pointRadius = 3
        private const val initialSize = 10000 // Size of initial triangle
    }

    private var dt: Triangulation // Delaunay triangulation
    private val colorTable = HashMap<Any, Color>() // Remembers colors for display
    private val initialTriangle: Triangle // Initial triangle
    private var graphics: Graphics? = null // Stored graphics context
    private val random = Random() // Source of random numbers

    /**
     * Create and initialize the DT.
     */
    init {
        initialTriangle = Triangle(
            Pnt(-initialSize.toDouble(), -initialSize.toDouble()),
            Pnt(initialSize.toDouble(), -initialSize.toDouble()),
            Pnt(0.0, initialSize.toDouble())
        )
        dt = Triangulation(initialTriangle)
    }

    /**
     * Add a new site to the DT.
     * @param point the site to add
     */
    fun addSite(point: Pnt) {
        dt.delaunayPlace(point)
    }

    /**
     * Re-initialize the DT.
     */
    fun clear() {
        dt = Triangulation(initialTriangle)
    }

    /**
     * Get the color for the specified item; generate a new color if necessary.
     * @param item we want the color for this item
     * @return item's color
     */
    private fun getColor(item: Any): Color {
        return colorTable.getOrPut(item) {
            Color(Color.HSBtoRGB(random.nextFloat(), 1.0f, 1.0f))
        }
    }

    /* Basic Drawing Methods */

    /**
     * Draw a point.
     * @param point the Pnt to draw
     */
    fun draw(point: Pnt) {
        val g = graphics ?: return
        val r = pointRadius
        val x = point.coord(0).toInt()
        val y = point.coord(1).toInt()
        g.fillOval(x - r, y - r, r + r, r + r)
    }

    /**
     * Draw a circle.
     * @param center the center of the circle
     * @param radius the circle's radius
     * @param fillColor null implies no fill
     */
    fun draw(center: Pnt, radius: Double, fillColor: Color?) {
        val g = graphics ?: return
        val x = center.coord(0).toInt()
        val y = center.coord(1).toInt()
        val r = radius.toInt()
        if (fillColor != null) {
            val temp = g.color
            g.color = fillColor
            g.fillOval(x - r, y - r, r + r, r + r)
            g.color = temp
        }
        g.drawOval(x - r, y - r, r + r, r + r)
    }

    /**
     * Draw a polygon.
     * @param polygon an array of polygon vertices
     * @param fillColor null implies no fill
     */
    fun draw(polygon: Array<Pnt>, fillColor: Color?) {
        val g = graphics ?: return
        val x = IntArray(polygon.size)
        val y = IntArray(polygon.size)
        for (i in polygon.indices) {
            x[i] = polygon[i].coord(0).toInt()
            y[i] = polygon[i].coord(1).toInt()
        }
        if (fillColor != null) {
            val temp = g.color
            g.color = fillColor
            g.fillPolygon(x, y, polygon.size)
            g.color = temp
        }
        g.drawPolygon(x, y, polygon.size)
    }

    /* Higher Level Drawing Methods */

    /**
     * Handles painting entire contents of DelaunayPanel.
     * Called automatically; requested via call to repaint().
     * @param g the Graphics context
     */
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        this.graphics = g

        // Flood the drawing area with a "background" color
        val temp = g.color
        if (!controller.isVoronoi()) g.color = delaunayColor
        else if (dt.contains(initialTriangle)) g.color = this.background
        else g.color = voronoiColor
        g.fillRect(0, 0, this.width, this.height)
        g.color = temp

        // If no colors then we can clear the color table
        if (!controller.isColorful()) colorTable.clear()

        // Draw the appropriate picture
        if (controller.isVoronoi())
            drawAllVoronoi(controller.isColorful(), true)
        else drawAllDelaunay(controller.isColorful())

        // Draw any extra info due to the mouse-entry switches
        val oldColor = g.color
        g.color = Color.white
        if (controller.showingCircles()) drawAllCircles()
        if (controller.showingDelaunay()) drawAllDelaunay(false)
        if (controller.showingVoronoi()) drawAllVoronoi(false, false)
        g.color = oldColor
    }

    /**
     * Draw all the Delaunay triangles.
     * @param withFill true iff drawing Delaunay triangles with fill colors
     */
    fun drawAllDelaunay(withFill: Boolean) {
        for (triangle in dt) {
            val vertices = triangle.toTypedArray()
            draw(vertices, if (withFill) getColor(triangle) else null)
        }
    }

    /**
     * Draw all the Voronoi cells.
     * @param withFill true iff drawing Voronoi cells with fill colors
     * @param withSites true iff drawing the site for each Voronoi cell
     */
    fun drawAllVoronoi(withFill: Boolean, withSites: Boolean) {
        // Keep track of sites done; no drawing for initial triangles sites
        val done = HashSet<Pnt>(initialTriangle)
        for (triangle in dt) {
            for (site in triangle) {
                if (done.contains(site)) continue
                done.add(site)
                val list = dt.surroundingTriangles(site, triangle)
                val vertices = Array(list.size) { i -> list[i].getCircumcenter() }
                draw(vertices, if (withFill) getColor(site) else null)
                if (withSites) draw(site)
            }
        }
    }

    /**
     * Draw all the empty circles (one for each triangle) of the DT.
     */
    fun drawAllCircles() {
        // Loop through all triangles of the DT
        for (triangle in dt) {
            // Skip circles involving the initial-triangle vertices
            if (triangle.containsAny(initialTriangle)) continue
            val c = triangle.getCircumcenter()
            val radius = c.subtract(triangle[0]).magnitude()
            draw(c, radius, null)
        }
    }
}
