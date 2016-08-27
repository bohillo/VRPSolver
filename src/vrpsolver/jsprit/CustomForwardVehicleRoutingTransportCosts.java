package vrpsolver.jsprit;

import java.sql.*;

import jsprit.core.problem.Location;
import jsprit.core.problem.cost.AbstractForwardVehicleRoutingTransportCosts;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.vehicle.Vehicle;

public class CustomForwardVehicleRoutingTransportCosts extends AbstractForwardVehicleRoutingTransportCosts {

	@Override
	public double getTransportCost(Location arg0, Location arg1, double arg2, Driver arg3, Vehicle arg4) {		
		double res = distance(arg0.getCoordinate().getY(), arg1.getCoordinate().getY(), arg0.getCoordinate().getX(), arg1.getCoordinate().getX(), 0, 0);
		return res;
	}

	@Override
	public double getTransportTime(Location arg0, Location arg1, double arg2, Driver arg3, Vehicle arg4) {
		// TODO Auto-generated method stub
		return getTransportCost(arg0, arg1, arg2, arg3, arg4) / 90.0 / 1000.0;
	}
	
	private double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

	    final int R = 6371; // Radius of the earth

	    Double latDistance = Math.toRadians(lat2 - lat1);
	    Double lonDistance = Math.toRadians(lon2 - lon1);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance)/1000.0;
	}

}
