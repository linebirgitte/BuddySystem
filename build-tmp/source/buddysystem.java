import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class buddysystem extends PApplet {

//declaring group that manages the particles
Group group;

public void setup(){
	group = new Group();
	size(displayWidth, displayHeight-100);
	
}

public void draw(){
	background(255);
	group.run();

	fill(0);
	text("Hold down SPACE to break buddysystem and delete fly-away particles", 10,height-20);
	text("Click SPACE to reset buddysystem", 10,height-40);
	text("Click mouse to add particle", 10,height-60);
}

public void mousePressed(){

	//add new particle to the particles list
	group.addParticle(new Particle(mouseX, mouseY));
}

public void keyPressed(){
	//reset buddies and delete fly-away particles
	group.clearBuddies();
}
class Group {
	
//a list of particle objects
ArrayList<Particle> particles;
	
	//CONSTRUCTOR
	Group(){
		particles = new ArrayList<Particle>(); //initialize the arraylist
	}
	
 	public void run(){	
 		//passing the entire list of particles to each particle individually
 		for (Particle b : particles){
			b.run(particles);
 		}
 	}

	public void addParticle(Particle b){
		//adds a particle to the particles array
		particles.add(b);
	}

	public void clearBuddies(){
		// deleting all budies from all buddylists
		for (Particle b : particles){
			b.buddies.clear();
 		}

 		//removing particles outside the canvas
 		for (int i = 0; i < particles.size(); i++){

			if (particles.get(i).location.x > width) particles.remove(i);
			else if (particles.get(i).location.y > height) particles.remove(i);
			else if (particles.get(i).location.x < 0) particles.remove(i);
			else if (particles.get(i).location.y < 0) particles.remove(i);

 		}
	}

};
class Particle {

	//internal settijgs
	boolean  debug = false;
	boolean borderOn = true;

	//movement
	PVector location;
	PVector velocity;
	PVector acceleration;

	float maxspeed = 10;
	float maxforce = 0.5f;

	//specs
	float radius = 10;
	float neighbordistance = 200;

	//Arraylist for the buddysystem
	ArrayList<Particle> buddies = new ArrayList<Particle>();
	//total number of buddies for each particle
	int buddyNum = 3;

	//CONSTRUCTOR
	Particle(float x, float y){
		acceleration = new PVector(0,0);
		velocity = PVector.random2D();
		velocity.limit(maxspeed);
		location = new PVector(x,y);
	}

	// run all relevant methods of Particle.
	public void run(ArrayList<Particle> particles){
		borders();
		move(particles);
		update();
		if (!keyPressed) addBuddies(particles);
		display();
		connect(particles);
	}

	public void update(){
		velocity.add(acceleration);
		location.add(velocity);
		acceleration.mult(0);
	}

	public void move(ArrayList<Particle> particles){
		PVector att = attract(particles);
		PVector sep = seperate(particles);

		//adding headings according to neighbors and buddies
		//particles wants to be close to their buddies
		//but they don't want to be too close to any other particle (including buddies)
		sep.mult(1.0f);
		att.mult(1.0f);

		acceleration.add(sep);
		acceleration.add(att);
	}

	//drawing connections between buddies
	public void connect(ArrayList<Particle> particles){
		for (Particle _other : buddies) {
			stroke(0);
			beginShape();
				vertex(location.x,location.y);
				vertex(_other.location.x,_other.location.y);
			endShape();
			if (debug) println("buddies = "+buddies.size());
		}		
	}

	// checking nearby particles to see if "thus" and any neighbor 
	// already have the allowed number of buddies
	// if there is room for another buddy: add to buddy list.
	public void addBuddies(ArrayList<Particle> particles){
		for (Particle other : particles){
			int check = 0;
			float d = PVector.dist(location, other.location);
			if (d > 0 && d < neighbordistance) {
				if (buddies.size() < buddyNum){
					check = buddies.indexOf(other);
					if (check == -1) {
						int otherIndex = particles.indexOf(other);
						if (particles.get(otherIndex).buddies.size() < buddyNum){
							particles.get(otherIndex).buddies.add(this);
							buddies.add(other);
						}
						if (debug) println("1buddies = "+buddies.size());
					}
				}
				
			}
		}
	}

	// find heading in opposite direction of where neighbors are
	public PVector seperate (ArrayList<Particle> particles){

		PVector steer = new PVector(0,0,0);
		int count = 0;

		// for every particle in the list check if it's too close
		for (Particle other : particles){
			float d = PVector.dist(location, other.location);

			//if the distance is greater than 0 (0 when checking yourself)
			//and less than specified in desiredseperation

			if (d > 0 && d < neighbordistance){
				// vector pointing away from neighbor
				PVector diff = PVector.sub(location, other.location);
				diff.normalize();
				diff.div(d); //weight by distance
				steer.add(diff);
				count++; // keep track of how many
			}
		}

		// Average -- divide by amount of particles in list that are too close
		if (count > 0) steer.div((float)count);

		if (steer.mag() > 0){
			// STEER = DESIRED - VELOCITY
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		return steer;
	}

	// PVector method for calculating heading to a desired location
	public PVector seek(PVector target) {

		//Vector pointing from the location to the target, the desired path
		PVector desired = PVector.sub(target, location);
		desired.normalize();
		desired.mult(maxspeed);

		//STEER = DESIRED - VELOCITY
		PVector steer = PVector.sub(desired, velocity);
		steer.limit(maxforce);
		return steer;
	}


	// avaraging the location of all vector nearby to find "desired" location
	// return vector towards that location
	public PVector attract(ArrayList<Particle> particles){

		PVector sum = new PVector(0,0); // empty vector for accumulating locations
		int count = 0;

		for (Particle other : buddies) {
				sum.add(other.location);
				count++;
		}
		if (count > 0) {
			sum.div((float)count);
			return seek(sum); // steer towards location
		}

		else {
			return new PVector(0,0);
		}
	}

	//making the particle
	public void display(){
		fill(0,180);
		noStroke();
		pushMatrix();
		translate(location.x,location.y);
		ellipse(0, 0, radius, radius);
		popMatrix();
	}

	//settings for borders
	public void borders(){
		if (borderOn){
			if (location.x < 0) velocity.x = -velocity.x;
			if (location.y < 0) velocity.y = -velocity.y;
			if (location.x > width) velocity.x = -velocity.x;
			if (location.y > height) velocity.y = -velocity.y;
		} else {
			if (location.x < radius) location.x = width-radius;
			if (location.y < radius) location.y = height-radius;
			if (location.x > width-radius) location.x = radius;
			if (location.y > height-radius) location.y = radius;
		}
	}
};
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "buddysystem" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
