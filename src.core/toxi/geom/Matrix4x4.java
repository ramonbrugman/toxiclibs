package toxi.geom;

/**
 * Implements a simple row-major 4x4 matrix class, all matrix operations are
 * applied to new instances. Use {@link #transpose()} to convert from
 * column-major formats...
 */

// FIXME considere OpenGL is using column-major ordering
// TODO add methods to apply to current instance only
// TODO still needs lots of refactoring
public class Matrix4x4 {

	public double[][] matrix;

	protected double[] temp = new double[4];

	// Default constructor
	// Sets matrix to identity
	public Matrix4x4() {
		init();
		matrix[0][0] = 1;
		matrix[1][1] = 1;
		matrix[2][2] = 1;
		matrix[3][3] = 1;
	}

	public Matrix4x4(double v11, double v12, double v13, double v14,
			double v21, double v22, double v23, double v24, double v31,
			double v32, double v33, double v34, double v41, double v42,
			double v43, double v44) {
		init();
		double[] m = matrix[0];
		m[0] = v11;
		m[1] = v12;
		m[2] = v13;
		m[3] = v14;
		m = matrix[1];
		m[0] = v21;
		m[1] = v22;
		m[2] = v23;
		m[3] = v24;
		m = matrix[2];
		m[0] = v31;
		m[1] = v32;
		m[2] = v33;
		m[3] = v34;
		m = matrix[3];
		m[0] = v41;
		m[1] = v42;
		m[2] = v43;
		m[3] = v44;
	}

	// Initializing constructor - single-dimensional array
	// Assumes row-major ordering (column idx increases faster)
	public Matrix4x4(double[] array) {
		if (array.length != 9 && array.length != 16) {
			throw new RuntimeException("Array.length must == 9 or 16");
		}
		init();
		if (array.length == 16) {
			matrix[0][0] = array[0];
			matrix[0][1] = array[1];
			matrix[0][2] = array[2];
			matrix[0][3] = array[3];

			matrix[1][0] = array[4];
			matrix[1][1] = array[5];
			matrix[1][2] = array[6];
			matrix[1][3] = array[7];

			matrix[2][0] = array[8];
			matrix[2][1] = array[9];
			matrix[2][2] = array[10];
			matrix[2][3] = array[11];

			matrix[3][0] = array[12];
			matrix[3][1] = array[13];
			matrix[3][2] = array[14];
			matrix[3][3] = array[15];
		} else if (array.length == 9) {
			matrix[0][0] = array[0];
			matrix[0][1] = array[1];
			matrix[0][2] = array[2];

			matrix[1][0] = array[3];
			matrix[1][1] = array[4];
			matrix[1][2] = array[5];

			matrix[2][0] = array[6];
			matrix[2][1] = array[7];
			matrix[2][2] = array[8];

			matrix[3][0] = array[9];
			matrix[3][1] = array[10];
			matrix[3][2] = array[11];
			matrix[3][3] = 1;
		}
	}

	public Matrix4x4(Matrix4x4 m) {
		init();
		for (int i = 0; i < 4; i++) {
			double[] mi = matrix[i];
			double[] mmi = m.matrix[i];
			mi[0] = mmi[0];
			mi[1] = mmi[1];
			mi[2] = mmi[2];
			mi[3] = mmi[3];
		}
	}

	public Matrix4x4 add(Matrix4x4 rhs) {
		Matrix4x4 result = new Matrix4x4();
		for (int i = 0; i < 4; i++) {
			double[] mi = matrix[i];
			double[] resm = result.matrix[i];
			double[] rhsm = rhs.matrix[i];
			resm[0] = mi[0] + rhsm[0];
			resm[1] = mi[1] + rhsm[1];
			resm[2] = mi[2] + rhsm[2];
			resm[3] = mi[3] + rhsm[3];
		}
		return result;
	}

	// Matrix-Vector Multiplication (Application)
	public Vec3D apply(Vec3D v) {
		for (int i = 0; i < 4; i++) {
			double[] m = matrix[i];
			temp[i] = v.x * m[0] + v.y * m[1] + v.z * m[2] + m[3];
		}
		return new Vec3D((float) temp[0], (float) temp[1], (float) temp[2])
				.scaleSelf((float) (1 / temp[3]));
	}

	public Matrix4x4 identity() {
		for (int i = 0; i < 4; i++) {
			double[] m = matrix[i];
			m[0] = m[1] = m[2] = m[3] = 0;
		}
		matrix[0][0] = 1;
		matrix[1][1] = 1;
		matrix[2][2] = 1;
		matrix[3][3] = 1;
		return this;
	}

	private final void init() {
		matrix = new double[4][];
		matrix[0] = new double[4];
		matrix[1] = new double[4];
		matrix[2] = new double[4];
		matrix[3] = new double[4];
	}

	// Matrix Inversion using Cramer's Method
	// Computes Adjoint matrix divided by determinant
	// Code modified from
	// http://www.intel.com/design/pentiumiii/sml/24504301.pdf
	public Matrix4x4 inverse() {
		final double[] tmp = new double[12];
		final double[] mat = new double[16];
		final double[] src = new double[16];
		final double[] dst = new double[16];

		// Copy all of the elements into the linear array
		for (int i = 0, k = 0; i < 4; i++) {
			double[] m = matrix[i];
			for (int j = 0; j < 4; j++) {
				mat[k++] = m[j];
			}
		}

		for (int i = 0; i < 4; i++) {
			int i4 = i << 2;
			src[i] = mat[i4];
			src[i + 4] = mat[i4 + 1];
			src[i + 8] = mat[i4 + 2];
			src[i + 12] = mat[i4 + 3];
		}

		// calculate pairs for first 8 elements (cofactors)
		tmp[0] = src[10] * src[15];
		tmp[1] = src[11] * src[14];
		tmp[2] = src[9] * src[15];
		tmp[3] = src[11] * src[13];
		tmp[4] = src[9] * src[14];
		tmp[5] = src[10] * src[13];
		tmp[6] = src[8] * src[15];
		tmp[7] = src[11] * src[12];
		tmp[8] = src[8] * src[14];
		tmp[9] = src[10] * src[12];
		tmp[10] = src[8] * src[13];
		tmp[11] = src[9] * src[12];

		// calculate first 8 elements (cofactors)
		double src0 = src[0];
		double src1 = src[1];
		double src2 = src[2];
		double src3 = src[3];
		double src4 = src[4];
		double src5 = src[5];
		double src6 = src[6];
		double src7 = src[7];
		dst[0] = tmp[0] * src5 + tmp[3] * src6 + tmp[4] * src7;
		dst[0] -= tmp[1] * src5 + tmp[2] * src6 + tmp[5] * src7;
		dst[1] = tmp[1] * src4 + tmp[6] * src6 + tmp[9] * src7;
		dst[1] -= tmp[0] * src4 + tmp[7] * src6 + tmp[8] * src7;
		dst[2] = tmp[2] * src4 + tmp[7] * src5 + tmp[10] * src7;
		dst[2] -= tmp[3] * src4 + tmp[6] * src5 + tmp[11] * src7;
		dst[3] = tmp[5] * src4 + tmp[8] * src5 + tmp[11] * src6;
		dst[3] -= tmp[4] * src4 + tmp[9] * src5 + tmp[10] * src6;
		dst[4] = tmp[1] * src1 + tmp[2] * src2 + tmp[5] * src3;
		dst[4] -= tmp[0] * src1 + tmp[3] * src2 + tmp[4] * src3;
		dst[5] = tmp[0] * src0 + tmp[7] * src2 + tmp[8] * src3;
		dst[5] -= tmp[1] * src0 + tmp[6] * src2 + tmp[9] * src3;
		dst[6] = tmp[3] * src0 + tmp[6] * src1 + tmp[11] * src3;
		dst[6] -= tmp[2] * src0 + tmp[7] * src1 + tmp[10] * src3;
		dst[7] = tmp[4] * src0 + tmp[9] * src1 + tmp[10] * src2;
		dst[7] -= tmp[5] * src0 + tmp[8] * src1 + tmp[11] * src2;

		// calculate pairs for second 8 elements (cofactors)
		tmp[0] = src2 * src7;
		tmp[1] = src3 * src6;
		tmp[2] = src1 * src7;
		tmp[3] = src3 * src5;
		tmp[4] = src1 * src6;
		tmp[5] = src2 * src5;
		tmp[6] = src0 * src7;
		tmp[7] = src3 * src4;
		tmp[8] = src0 * src6;
		tmp[9] = src2 * src4;
		tmp[10] = src0 * src5;
		tmp[11] = src1 * src4;

		// calculate second 8 elements (cofactors)
		src0 = src[8];
		src1 = src[9];
		src2 = src[10];
		src3 = src[11];
		src4 = src[12];
		src5 = src[13];
		src6 = src[14];
		src7 = src[15];
		dst[8] = tmp[0] * src5 + tmp[3] * src6 + tmp[4] * src7;
		dst[8] -= tmp[1] * src5 + tmp[2] * src6 + tmp[5] * src7;
		dst[9] = tmp[1] * src4 + tmp[6] * src6 + tmp[9] * src7;
		dst[9] -= tmp[0] * src4 + tmp[7] * src6 + tmp[8] * src7;
		dst[10] = tmp[2] * src4 + tmp[7] * src5 + tmp[10] * src7;
		dst[10] -= tmp[3] * src4 + tmp[6] * src5 + tmp[11] * src7;
		dst[11] = tmp[5] * src4 + tmp[8] * src5 + tmp[11] * src6;
		dst[11] -= tmp[4] * src4 + tmp[9] * src5 + tmp[10] * src6;
		dst[12] = tmp[2] * src2 + tmp[5] * src3 + tmp[1] * src1;
		dst[12] -= tmp[4] * src3 + tmp[0] * src1 + tmp[3] * src2;
		dst[13] = tmp[8] * src3 + tmp[0] * src0 + tmp[7] * src2;
		dst[13] -= tmp[6] * src2 + tmp[9] * src3 + tmp[1] * src0;
		dst[14] = tmp[6] * src1 + tmp[11] * src3 + tmp[3] * src0;
		dst[14] -= tmp[10] * src3 + tmp[2] * src0 + tmp[7] * src1;
		dst[15] = tmp[10] * src2 + tmp[4] * src0 + tmp[9] * src1;
		dst[15] -= tmp[8] * src1 + tmp[11] * src2 + tmp[5] * src0;

		double det = 1 / (src[0] * dst[0] + src[1] * dst[1] + src[2] * dst[2] + src[3]
				* dst[3]);

		Matrix4x4 result = new Matrix4x4();
		for (int i = 0, k = 0; i < 4; i++) {
			double[] rm = result.matrix[i];
			for (int j = 0; j < 4; j++) {
				rm[j] = dst[k++] * det;
			}
		}
		return result;
	}

	/**
	 * Matrix-Matrix Left-multiplication: matrix * this
	 * 
	 * @param mat
	 * @return product as new matrix
	 */
	public Matrix4x4 leftMultiply(Matrix4x4 mat) {
		return mat.multiply(this);
	}

	/**
	 * Matrix-Scalar Multiplication.
	 * 
	 * @param c
	 * @return product as new matrix
	 */
	public Matrix4x4 multiply(double c) {
		Matrix4x4 result = new Matrix4x4();
		for (int i = 0; i < 4; i++) {
			double[] m = matrix[i];
			double[] rm = result.matrix[i];
			rm[0] = c * m[0];
			rm[1] = c * m[1];
			rm[2] = c * m[2];
			rm[3] = c * m[3];
		}
		return result;
	}

	/**
	 * Matrix-Matrix Right-multiplication.
	 * 
	 * @param mat
	 * @return product as new matrix
	 */
	public Matrix4x4 multiply(Matrix4x4 mat) {
		Matrix4x4 result = new Matrix4x4();
		for (int i = 0; i < 4; i++) {
			double[] m = matrix[i];
			double[] rm = result.matrix[i];
			for (int j = 0; j < 4; j++) {
				rm[j] = m[0] * mat.matrix[0][j] + m[1] * mat.matrix[1][j]
						+ m[2] * mat.matrix[2][j] + m[3] * mat.matrix[3][j];
			}
		}
		return result;
	}

	// Apply Rotation about arbitrary axis to Matrix
	public Matrix4x4 rotate(Vec3D axis, double theta) {
		double x, y, z, s, c, t;
		x = axis.x;
		y = axis.y;
		z = axis.z;
		s = Math.sin(theta);
		c = Math.cos(theta);
		// c = Math.sqrt(1 - s * s);
		// this may be faster than the previous line
		t = 1 - c;

		double tx = t * x;
		double ty = t * y;

		return (new Matrix4x4(tx * x + c, tx * y + s * z, tx * z - s * y, 0, tx
				* y - s * z, ty * y + c, ty * z + s * x, 0, tx * z + s * y, ty
				* z - s * x, t * z * z + c, 0, 0, 0, 0, 1)).leftMultiply(this);
	}

	// Apply Rotation about X to Matrix
	public Matrix4x4 rotateX(double theta) {
		Matrix4x4 rot = new Matrix4x4();
		rot.matrix[1][1] = rot.matrix[2][2] = Math.cos(theta);
		rot.matrix[2][1] = Math.sin(theta);
		rot.matrix[1][2] = -rot.matrix[2][1];
		return rot.leftMultiply(this);
	}

	// Apply Rotation about Y to Matrix
	public Matrix4x4 rotateY(double theta) {
		Matrix4x4 rot = new Matrix4x4();
		rot.matrix[0][0] = rot.matrix[2][2] = Math.cos(theta);
		rot.matrix[0][2] = Math.sin(theta);
		rot.matrix[2][0] = -rot.matrix[0][2];
		return rot.leftMultiply(this);
	}

	// Apply Rotation about Z to Matrix
	public Matrix4x4 rotateZ(double theta) {
		Matrix4x4 rot = new Matrix4x4();
		rot.matrix[0][0] = rot.matrix[1][1] = Math.cos(theta);
		rot.matrix[1][0] = Math.sin(theta);
		rot.matrix[0][1] = -rot.matrix[1][0];
		return rot.leftMultiply(this);
	}

	// Apply Scale to Matrix
	public Matrix4x4 scale(double scaleX, double scaleY, double scaleZ) {
		Matrix4x4 scl = new Matrix4x4();
		scl.matrix[0][0] = scaleX;
		scl.matrix[1][1] = scaleY;
		scl.matrix[2][2] = scaleZ;
		// return scl.mul(this);
		return scl.leftMultiply(this);
	}

	// Matrix-Matrix Subtraction
	public Matrix4x4 sub(Matrix4x4 rhs) {
		Matrix4x4 result = new Matrix4x4();
		for (int i = 0; i < 4; i++) {
			double[] mi = matrix[i];
			double[] resm = result.matrix[i];
			double[] rhsm = rhs.matrix[i];
			resm[0] = mi[0] - rhsm[0];
			resm[1] = mi[1] - rhsm[1];
			resm[2] = mi[2] - rhsm[2];
			resm[3] = mi[3] - rhsm[3];
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "| " + matrix[0][0] + " " + matrix[0][1] + " " + matrix[0][2]
				+ " " + matrix[0][3] + " |\n" + "| " + matrix[1][0] + " "
				+ matrix[1][1] + " " + matrix[1][2] + " " + matrix[1][3]
				+ " |\n" + "| " + matrix[2][0] + " " + matrix[2][1] + " "
				+ matrix[2][2] + " " + matrix[2][3] + " |\n" + "| "
				+ matrix[3][0] + " " + matrix[3][1] + " " + matrix[3][2] + " "
				+ matrix[3][3] + " |";
	}

	// Apply Translation to Matrix
	public Matrix4x4 translate(double dx, double dy, double dz) {
		Matrix4x4 trn = new Matrix4x4();
		trn.matrix[0][3] = dx;
		trn.matrix[1][3] = dy;
		trn.matrix[2][3] = dz;
		// return trn.mul(this);
		return trn.leftMultiply(this);
	}

	// Matrix Transpose
	public Matrix4x4 transpose() {
		return new Matrix4x4(matrix[0][0], matrix[1][0], matrix[2][0],
				matrix[3][0], matrix[0][1], matrix[1][1], matrix[2][1],
				matrix[3][1], matrix[0][2], matrix[1][2], matrix[2][2],
				matrix[3][2], matrix[0][3], matrix[1][3], matrix[2][3],
				matrix[3][3]);
	}
}
