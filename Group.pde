class Group {
	
//a list of particle objects
ArrayList<Particle> particles;
	
	//CONSTRUCTOR
	Group(){
		particles = new ArrayList<Particle>(); //initialize the arraylist
	}
	
 	void run(){	
 		//passing the entire list of particles to each particle individually
 		for (Particle b : particles){
			b.run(particles);
 		}
 	}

	void addParticle(Particle b){
		//adds a particle to the particles array
		particles.add(b);
	}

	void clearBuddies(){
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