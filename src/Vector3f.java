public class Vector3f {
	private float x, y, z;
	
	public Vector3f() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vector3f(Vector3f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	} 
	
	public Vector3f(float X, float Y, float Z) {
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
	
	public void setVector(Vector3f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public void setVector(float X, float Y, float Z) {
		x = X;
		y = Y;
		z = Z;
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
	
	public void add(Vector3f v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public void sub(Vector3f v) {
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
	
	public float distance(Vector3f v) {
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
	
	public float dot(Vector3f v) {
		float dotProd = 0.0f;
		dotProd += x * v.x;
		dotProd += y * v.y;
		dotProd += z * v.z;
		return dotProd;
	}
}