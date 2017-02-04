public class Planet {
	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;
	private static double gc = 6.67e-11;

	public Planet(double xP, double yP, double xV,
              double yV, double m, String img){
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}
	public Planet(Planet p){
		xxPos = p.xxPos;
		yyPos = p.yyPos;
		xxVel = p.xxVel;
		yyVel = p.yyVel;
		mass = p.mass;
		imgFileName = p.imgFileName;
	}

	public double calcDistance (Planet otherPlanet){
		return Math.sqrt(((xxPos-otherPlanet.xxPos)*(xxPos-otherPlanet.xxPos))+((yyPos-otherPlanet.yyPos)*(yyPos-otherPlanet.yyPos)));
	}
	public double calcForceExertedBy (Planet otherPlanet){
		return gc * mass * otherPlanet.mass / (calcDistance(otherPlanet)*calcDistance(otherPlanet));
	}
	public double calcForceExertedByX (Planet otherPlanet){
		return calcForceExertedBy(otherPlanet) * ((otherPlanet.xxPos-xxPos) / calcDistance(otherPlanet));
	}
	public double calcForceExertedByY (Planet otherPlanet){
		return calcForceExertedBy(otherPlanet) * ((otherPlanet.yyPos-yyPos) / calcDistance(otherPlanet));
	}
	public double calcNetForceExertedByX (Planet[] otherPlanets){
		double netForce = 0.0;
		for(int i=0; i<otherPlanets.length; i++){
			if(!(otherPlanets[i].equals(this))){
				netForce = netForce + calcForceExertedByX(otherPlanets[i]);
			}
		}
		return netForce;
	}
	public double calcNetForceExertedByY (Planet[] otherPlanets){
		double netForce = 0.0;
		for(int i=0; i<otherPlanets.length; i++){
			if(!(otherPlanets[i].equals(this))){
				netForce = netForce + calcForceExertedByY(otherPlanets[i]);
			}
		}
		return netForce;
	}
	public void update (double dt, double fx, double fy){
		double xAccel = fx/mass;
		double yAccel = fy/mass;
		xxVel = xxVel + dt * xAccel;
		yyVel = yyVel + dt * yAccel;
		xxPos = xxPos + dt * xxVel;
		yyPos = yyPos + dt * yyVel;
	}
	public void draw(){
		StdDraw.picture(xxPos, yyPos, "images/"+imgFileName);
		StdDraw.show();
	}
}