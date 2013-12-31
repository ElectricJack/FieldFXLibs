class AnimArc {
  float ang0;
  float ang1;
  float angVel;
  float r;
  
  public AnimArc() {
    r      = random(250,800);
    ang0    = random(0,2*PI);
    ang1    = random(0,2*PI);
    angVel = random(-0.02f,0.02f);
  }
  
  public void draw() {
    ang0 += angVel;
    ang1 += angVel;
    arc( 0, 0, r, r, ang0, ang1);
  } 
}
