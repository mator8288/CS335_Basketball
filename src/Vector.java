public class Vector {
	private float x, y, z;
	
	public Vector() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vector(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
	} 
	
	public Vector(float X, float Y, float Z) {
		x = X;
		y = Y;
		z = Z;
	}
	
	public float getValue(int i) {
		if (i == 0) {
			return x;
		} else if (i == 1) {
			return y;
		} else if (i == 2) {
			return z;
		} else {
			return 0;
		}
	}
	
	public void setVector(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public void setParameter(int i, float val) {
		if (i == 0) {
			x = val;
		} else if (i == 1) {
			y = val;
		} else if (i == 2) {
			z = val;
		}
	}
	
	public void addParameter(int i, float val) {
		if (i == 0) {
			x += val;
		} else if (i == 1) {
			y += val;
		} else if (i == 2) {
			z += val;
		}
	}
	
	public void add(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public void sub(Vector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	
	public void changeDirection(int i) {
		if (i == 0) {
			x *= -1;
		} else if (i == 1) {
			y *= -1;
		} else if (i == 2) {
			z *= -1;
		}
	}
	
	public float distance(Vector v) {
		this.sub(v);
		float dist = length();
		this.add(v);
		return dist;
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public void normalize() {
		float length = length();
		x /= length;
		y /= length;
		z /= length;
	}
	
	public void dot(Vector v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
	}
}