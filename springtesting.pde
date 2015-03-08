Group group;
void setup(){
	group = new Group();
	size(displayWidth, displayHeight-100);
	
}

void draw(){
	background(255);
	group.run();

	fill(0);
	text("Hold down SPACE to break buddysystem and delete fly-away particles", 10,height-20);
	text("Click SPACE to reset buddysystem", 10,height-40);
	text("Click mouse to add particle", 10,height-60);
}

void mousePressed(){
	group.addSpring(new Spring(mouseX, mouseY));
}

void keyPressed(){
	group.clearBuddies();
}