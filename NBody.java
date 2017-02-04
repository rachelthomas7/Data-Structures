public class NBody{
	public static void main (String[] args){
		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String fileName = args[2];
		Planet[] planets = readPlanets(fileName);
		double radius = readRadius(fileName);
		StdDraw.setScale(-radius, radius);
		StdDraw.clear();
		StdDraw.picture(0, 0, "images/starfield.jpg");
		StdDraw.show();
		for(int i = 0; i<planets.length; i++){
			planets[i].draw();
		}
		double time = 0.0;
		while (time<T){
			double[] xForces = new double[planets.length];
			for(int q = 0; q<planets.length; q++){
				xForces[q]= planets[q].calcNetForceExertedByX(planets);
			}
			double[] yForces = new double[planets.length];
			for(int q = 0; q<planets.length; q++){
				yForces[q]= planets[q].calcNetForceExertedByY(planets);
			}
			for(int r = 0; r<planets.length; r++){
				planets[r].update(dt, xForces[r], yForces[r]);
			}
			StdDraw.picture(0, 0, "images/starfield.jpg");
			for(int i = 0; i<planets.length; i++){
				planets[i].draw();
			}
			StdDraw.show(10);
			time = time + dt;
		}
		//now, to print the final state of the universe:
		StdOut.printf("%d\n", planets.length);
		StdOut.printf("%.2e\n", radius);
		for (int i = 0; i < planets.length; i++) {
			StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   			planets[i].xxPos, planets[i].yyPos, planets[i].xxVel, planets[i].yyVel, planets[i].mass, planets[i].imgFileName);	
		}
		
	}
	public static double readRadius (String fileName){
		In in = new In(fileName);
		in.readInt();
		return in.readDouble();
	}
	public static Planet[] readPlanets (String fileName){
		In in = new In(fileName);
		int numPlanets = in.readInt();
		double radius = in.readDouble();
		Planet[] planets = new Planet[numPlanets];
		int count = 0;
		while(count < numPlanets){
			double xPos = in.readDouble();
			double yPos = in.readDouble();
			double xVel = in.readDouble();
			double yVel = in.readDouble();
			double mass = in.readDouble();
			String imgFileName = in.readString();
			Planet planety = new Planet(xPos, yPos, xVel, yVel, mass, imgFileName);
			planets[count] = planety;
			count = count + 1;
		}
	return planets;
	}
}