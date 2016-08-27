import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import jsprit.*;

import jsprit.analysis.toolbox.GraphStreamViewer;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.Jsprit;
import jsprit.core.algorithm.state.StateId;
import jsprit.core.algorithm.state.StateManager;
import jsprit.core.algorithm.state.StateUpdater;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.constraint.ConstraintManager;
import jsprit.core.problem.constraint.HardActivityConstraint;
import jsprit.core.problem.io.VrpXMLWriter;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.misc.JobInsertionContext;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.ActivityVisitor;
import jsprit.core.problem.solution.route.activity.TimeWindow;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.reporting.SolutionPrinter.Print;
import jsprit.core.util.Solutions;
import jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import vrpsolver.jsprit.CustomForwardVehicleRoutingTransportCosts;
import jsprit.core.problem.constraint.*;

public class Main {

	public static void main(String[] args) {

        double lat=0, lng=0;

        
		/*
		 * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
		 * you are free to add an arbitrary number of capacity dimensions with .addCacpacityDimension(dimensionIndex,dimensionValue)
		 */
		final int WEIGHT_INDEX = 0;
		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(WEIGHT_INDEX,1000);
		VehicleType vehicleType = vehicleTypeBuilder.build();

		/*
		 * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
		 */
		VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
		vehicleBuilder.setStartLocation(Location.newInstance(lng, lat));
		vehicleBuilder.setType(vehicleType);
		vehicleBuilder.setLatestArrival(9.5);
		VehicleImpl vehicle = vehicleBuilder.build();
		
		/*
		 * build services with id 1...4 at the required locations, each with a capacity-demand of 1.
		 * Note, that the builder allows chaining which makes building quite handy
		 */
		

        // Iterate through the data in the result set and display it.      
        Service service;
        ArrayList<Service> services = new ArrayList<Service>();
        
        for(int i = 0; i < 100; i++)  
        {  
           lat = 2*Math.random() - 1;
           lng = 2*Math.random() - 1;
           service = Service.Builder.newInstance(Integer.toString(i))
        		//   .addTimeWindow(TimeWindow.newInstance(0, 18.0))
        		   .addSizeDimension(WEIGHT_INDEX,1)
        		   .setLocation(Location.newInstance(lng, lat)).build();
           services.add(service);
           System.out.println("Created node " + i);
        }
        

	
		/*
		 * again define a builder to build the VehicleRoutingProblem
		 */
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.addVehicle(vehicle);
		vrpBuilder.addAllJobs(services); //.addJob(service1).addJob(service2).addJob(service3);//.addJob(service4);
		vrpBuilder.setRoutingCost(new CustomForwardVehicleRoutingTransportCosts());
		
		/*
		 * build the problem
		 * by default, the problem is specified such that FleetSize is INFINITE, i.e. an infinite number of 
		 * the defined vehicles can be used to solve the problem
		 * by default, transport costs are computed as Euclidean distances
		 */
		
		VehicleRoutingProblem problem = vrpBuilder.build();
		
		StateManager sm = new StateManager(problem);
		ConstraintManager cm = new ConstraintManager(problem, sm);
		
		//cm.addConstraint(new   (18.0, sm, problem.getTransportCosts()), ConstraintManager.Priority.CRITICAL);
		
		Jsprit.Builder algorithmBuilder =  Jsprit.Builder.newInstance(problem).setProperty(Jsprit.Parameter.THREADS, "4");
		/*
		* get the algorithm out-of-the-box. 
		*/
		
		algorithmBuilder.setStateAndConstraintManager(sm, cm);
		
		VehicleRoutingAlgorithm algorithm = algorithmBuilder.buildAlgorithm(); //Jsprit.createAlgorithm(problem);

		/*
		* and search a solution which returns a collection of solutions (here only one solution is constructed)
		*/
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
		 * use the static helper-method in the utility class Solutions to get the best solution (in terms of least costs)
		 */
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
		
		File dir = new File("output");
		// if the directory does not exist, create it
		if (!dir.exists()){
		    System.out.println("creating directory ./output");
		    boolean result = dir.mkdir();  
		    if(result) System.out.println("./output created");  
		}
		
		new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");
		
		SolutionPrinter.print(problem, bestSolution, Print.CONCISE);
		
		//SolutionPrinter.print(problem, bestSolution, Print.VERBOSE);
		
		new GraphStreamViewer(problem, bestSolution).setRenderDelay(100).display();
	}

}
