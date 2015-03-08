class Group {
	
//a list of spring objects
ArrayList<Spring> springs;
	
	//CONSTRUCTOR
	Group(){
		springs = new ArrayList<Spring>(); //initialize the arraylist
	}
	
 	void run(){	
 		//passing the entire list of springs to each spring individually
 		for (Spring b : springs){
			b.run(springs);
 		}
 	}

	void addSpring(Spring b){
		//adds a spring to the springs array
		springs.add(b);
	}

	void clearBuddies(){
		// deleting all budies from all buddylists
		for (Spring b : springs){
			b.buddies.clear();
 		}

 		//removing springs outside the canvas
 		for (int i = 0; i < springs.size(); i++){

			if (springs.get(i).location.x > width) springs.remove(i);
			else if (springs.get(i).location.y > height) springs.remove(i);
			else if (springs.get(i).location.x < 0) springs.remove(i);
			else if (springs.get(i).location.y < 0) springs.remove(i);

 		}
	}

};