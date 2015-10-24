

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CenteredDiscretization2D {

	// var number of points
	private double r;
	private List<Double> offsets;
	private List<GridIdentifier> gridSquareIndex;
	

	public CenteredDiscretization2D(List<Point2D> points, double tolerance) {
		r = tolerance;
		discretizePoints(points);
	}
	
	public CenteredDiscretization2D(List<Point2D.Double> points, double tolerance, List<Double> offsets) {
		r = tolerance;
		this.offsets = offsets;
		gridSquareIndex = new ArrayList<GridIdentifier>(points.size());
		discretizeCheck(points);
	}
	
	public CenteredDiscretization2D(double tolerance, List<Double> offsets, List<GridIdentifier> identifiers) {
		r = tolerance;
		this.offsets = offsets;
		gridSquareIndex = identifiers;
	}

	private void discretizePoints(List<Point2D> points) {
		int numOfPoints = points.size();
		offsets = new ArrayList<Double>(numOfPoints*2);
		gridSquareIndex = new ArrayList<GridIdentifier>(numOfPoints);
		
		for (int i=0; i<numOfPoints; i++) {
			Point2D p = points.get(i);
			addPoint(p.getX(), p.getY());
		}
	}
	
	// method takes a point x,y and r val
	// adds ix, iy and dx, dy to respective lists
	private void addPoint(double x, double y) {

		int iX = (int) Math.floor((x - r) / (2*r));
		int iY = (int) Math.floor((y - r) / (2*r));
		
		double dX = ((((x - r) % (2*r)) + (2*r)) % (2*r));
		double dY = ((((y - r) % (2*r)) + (2*r)) % (2*r));
		
		gridSquareIndex.add(new GridIdentifier(iX,iY));
		offsets.add(dX);
		offsets.add(dY);
	}
	
	public void discretizeCheck(List<Point2D.Double> points) {
		int j=0;
		for (int i=0; i<points.size(); i++) {
			Point2D.Double p = points.get(i);
			double d1 = offsets.get(j);
			double d2 = offsets.get(j+1);
			
			GridIdentifier g = getIdentifier(p, d1, d2, r);
			gridSquareIndex.add(g);
			j+=2;
		}
	}
	
	public static GridIdentifier getIdentifier(Point2D.Double p, double d1, double d2, double r) {

		int x = (int) Math.floor((p.getX() - d1) / (2*r));
		int y = (int) Math.floor((p.getY() - d2) / (2*r));
		return new GridIdentifier(x,y);
	}
	
	public List<GridIdentifier> getIndexes() {
		return gridSquareIndex;
	}
	
	public String getPasswordString() {
		String s = "";
		for (int i=0; i<gridSquareIndex.size(); i++) {
			DecimalFormat df = new DecimalFormat("#.##");
			double d1 = offsets.get(2*i);
			double d2 = offsets.get(2*i+1);
			GridIdentifier g = gridSquareIndex.get(i);
			
			s += df.format(d1) + "," + df.format(d2) + ","  + g.getX() + "," + g.getY();
			if (i<gridSquareIndex.size()-1) {
				 s += ",";
			}
		}
		return s;
	}
	
	public String getOffsetString() {
		String s = "";
		for (int i=0; i<offsets.size(); i++) {
			double d = offsets.get(i);
			DecimalFormat df = new DecimalFormat("#.##");
			s += df.format(d);
			if (i<offsets.size()-1) {
			 s += ",";
			}
		}
		return s;
	}
}
