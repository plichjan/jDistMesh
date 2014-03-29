package cz.cvut.fel.plichjan;

import cz.cvut.fel.plichjan.distmesh.DistMesh2D;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * GUI class to test the delaunay_triangulation Triangulation package:
 */

public class ViewerFrame extends JFrame implements ActionListener, IViewer {

    private Thread dmThread;
    private List<Shape> shapes;
    private List<Shape> circles;
    private String fileName = "test.js";
    private Rectangle2D bbox;
    private float pixelSize;
    private AffineTransform transform;

    public static void main(String[] args) {
		ViewerFrame win = new ViewerFrame();
		win.start();
	}

	private static final long serialVersionUID = 1L;
	// *** private data ***
	public static final int DISMASH = 13;
	private int _stage;

    // *** text area ***
	public ViewerFrame() {
		this.setTitle("Delaunay GUI tester");
        addJPanel();
		this.setSize(500, 500);
		_stage = 0;

		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
	}

    private void addJPanel() {
        setLayout(new BorderLayout());
        final JPanel comp = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                if (bbox != null) {
                    applyLimits(g2, getWidth(), getHeight(), bbox, true);
                }
                ViewerFrame.this.paintComponent(g2);
            }
        };

        add(comp);
    }

    /**
     * Applies a coordinate transform to a Graphics2D graphics context.  The upper
     * left corner of the viewport where the graphics context draws is assumed to
     * be (0,0).  This method sets the global variables pixelSize and transform.
     *
     * @param g2 The drawing context whose transform will be set.
     * @param width The width of the viewport where g2 draws.
     * @param height The height of the viewport where g2 draws.
     * @param limitsRequested  Specifies a rectangle that will be visible in the
     *   viewport. Under the transform, the rectangle with corners (limitsRequested[0],
     *   limitsRequested[1]) and (limitsRequested[2],limitsRequested[3]) will just
     *   fit in the viewport.
     * @param preserveAspect if preserveAspect is false, then the limitsRequested
     *   rectangle will exactly fill the viewport; if it is true, then the limits
     *   will be expanded in one direction, horizontally or vertically, to make
     *   the aspect ratio of the displayed rectangle match the aspect ratio of the
     *   viewport.  Note that when preserveAspect is false, the units of measure in
     *   the horizontal and vertical directions will be different.
     */
    private void applyLimits(Graphics2D g2, int width, int height,
                             Rectangle2D limitsRequested, boolean preserveAspect) {
        Rectangle2D recLim = new Rectangle2D.Double();
        recLim.setRect(limitsRequested);
        if (preserveAspect) {
            double displayAspect = Math.abs((double) height / width);
            double requestedAspect = Math.abs(recLim.getHeight() / recLim.getWidth());
            if (displayAspect > requestedAspect) {
                double excess = recLim.getHeight() * (displayAspect / requestedAspect - 1);
                recLim.setRect(recLim.getX(), recLim.getY() - excess / 2, recLim.getWidth(), recLim.getHeight() + excess);
            } else if (displayAspect < requestedAspect) {
                double excess = recLim.getWidth() * (requestedAspect / displayAspect - 1);
                recLim.setRect(recLim.getX() - excess / 2, recLim.getY(), recLim.getWidth() + excess, recLim.getHeight());
            }
        }
        g2.translate(0, height);
        g2.scale(width / recLim.getWidth(), -height / recLim.getHeight());  // y grown up
        g2.translate(-recLim.getX(), -recLim.getY());
        double pixelWidth = Math.abs(recLim.getWidth() / width);
        double pixelHeight = Math.abs(recLim.getHeight() / height);
        pixelSize = (float) Math.min(pixelWidth, pixelHeight);
        transform = g2.getTransform();
    }

    public void paintComponent(Graphics2D g) {
        if (_stage == DISMASH) {
            drawDismash(g);
            if (circles != null) {
                drawMidpoints(g);
            }
        }
	}

    private void drawDismash(Graphics2D g) {
        g.setColor(Color.black);
        g.setStroke( new BasicStroke(pixelSize) );

        final ArrayList<Shape> list = new ArrayList<Shape>(shapes);
        for (Shape t : list) {
            g.draw(t);
        }
    }

    private void drawMidpoints(Graphics2D g) {
        g.setColor(Color.red);
        g.setStroke( new BasicStroke(pixelSize) );

        for (Shape t : circles) {
            g.draw(t);
        }
    }

	public void start() {
        createMenuBar();
        this.setVisible(true);
	}

	public void createMenuBar() {
		MenuBar mbar = new MenuBar();

		Menu m = new Menu("File");
		MenuItem m1;
		m1 = new MenuItem("Open");
		m1.addActionListener(this);
        m1.setShortcut(new MenuShortcut(KeyEvent.VK_O));
		m.add(m1);
		m1 = new MenuItem("Start DistMesh");
		m1.addActionListener(this);
		m1.setShortcut(new MenuShortcut(KeyEvent.VK_E));
		m.add(m1);
		MenuItem m6 = new MenuItem("Clear");
		m6.addActionListener(this);
        m6.setShortcut(new MenuShortcut(KeyEvent.VK_Q));
		m.add(m6);
		MenuItem m2 = new MenuItem("Exit");
		m2.addActionListener(this);
		m.add(m2);
		mbar.add(m);

		setMenuBar(mbar);
	}

	public void actionPerformed(ActionEvent evt) {
		String arg = evt.getActionCommand();
		if (arg.equals("Open")) {
            openTextFile();
        } else if (arg.equals("Start DistMesh")) {
            startDistMesh();
		} else if (arg.equals("Clear")) {
            _stage = 0;
            repaint();
		} else if (arg.equals("Exit")) {
			System.exit(209);
		}
	}

	// ********** Private methodes (open,save...) ********

	private void openTextFile() {
		_stage = 0;
		FileDialog d = new FileDialog(this, "Open text file", FileDialog.LOAD);
		d.setVisible(true);
		String dr = d.getDirectory();
		String fi = d.getFile();
        if (fi != null) { // the user actualy choose a file.
            fileName = dr + fi;
            startDistMesh();
		}
	}

    private void startDistMesh() {
        if (dmThread != null && dmThread.isAlive()) {
            return;
        }

        Runnable runnable = new Runnable() {
            public void run() {
                DistMesh2D distMesh2D = new DistMesh2D();
                distMesh2D.setViewer(ViewerFrame.this);

                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                engine.put("distMesh2D", distMesh2D);
                engine.put("viewFrame", ViewerFrame.this);

                try {
                    engine.eval(new java.io.FileReader(fileName));
                } catch (ScriptException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    System.err.format("%1$tT.%1$tL done.\n", Calendar.getInstance());
                }
            }
        };
        dmThread = new Thread(runnable, "DistMesh2D");
        System.err.format("%1$tT.%1$tL start.\n", Calendar.getInstance());
        dmThread.start();
    }

    @Override
    public void setNewPoints(List<Point2D> points, List<int[]> t, double[][] bbox) {
        _stage = DISMASH;
        this.bbox = new Rectangle2D.Double(bbox[0][0], bbox[0][1], 0, 0);
        this.bbox.add(bbox[1][0], bbox[1][1]);

        List<Shape> shapes = new ArrayList<Shape>(t.size());
        List<Shape> circles = new ArrayList<Shape>();
        for (int[] tr : t) {
            Point2D tmp;
            final Point2D a = points.get(tr[0]), b = points.get(tr[1]), c = points.get(tr[2]);

            if (tr.length == 3) {
                final Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 1+3);
                path.moveTo(a.getX(), a.getY());
                path.lineTo(b.getX(), b.getY());
                path.lineTo(c.getX(), c.getY());
                path.lineTo(a.getX(), a.getY());
                shapes.add(path);
            } else {
                final Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 1+3*2);
                path.moveTo(a.getX(), a.getY());
                tmp = getQuadPoint2D(a, b, points.get(tr[3]));
                path.quadTo(tmp.getX(), tmp.getY(), b.getX(), b.getY());
                tmp = getQuadPoint2D(b, c, points.get(tr[4]));
                path.quadTo(tmp.getX(), tmp.getY(), c.getX(), c.getY());
                tmp = getQuadPoint2D(c, a, points.get(tr[5]));
                path.quadTo(tmp.getX(), tmp.getY(), a.getX(), a.getY());
                shapes.add(path);

                addEllipse(circles, points.get(tr[3]));
                addEllipse(circles, points.get(tr[4]));
                addEllipse(circles, points.get(tr[5]));
            }
        }
        this.shapes = shapes;
        this.circles = circles;

        repaint();
    }

    private void addEllipse(List<Shape> shapes, Point2D a) {
        final double r = this.pixelSize * 5;
        shapes.add(new Ellipse2D.Double(a.getX() - r / 2, a.getY() - r / 2, r, r));
    }

    @Override
    public void drawMesh(Mesh mesh) {
        Rectangle2D.Double rec = null;
        List<Point2D> pdt = new ArrayList<Point2D>(mesh.getP().length);
        for (double[] point : mesh.getP()) {
            final Point2D.Double point2d = new Point2D.Double(point[0], point[1]);
            if (rec == null) {
                rec = new Rectangle2D.Double(point[0], point[1], 0, 0);
            }
            rec.add(point2d);
            pdt.add(point2d);
        }
        if (rec == null) {
            rec = new Rectangle2D.Double();
        }
        this.setNewPoints(pdt, Arrays.asList(mesh.getT()), new double[][]{{rec.getMinX(), rec.getMinY(),}, {rec.getMaxX(), rec.getMaxY(),}, });
    }

    private Point2D getQuadPoint2D(Point2D a, Point2D b, Point2D ab) {
        return new Point2D.Double(
                (4 * ab.getX() - a.getX() - b.getX()) / 2,
                (4 * ab.getY() - a.getY() - b.getY()) / 2
        );
    }

}
