// Pixel shader Gouraud Shading - Per-vertex lighting

precision mediump float;

// the color
varying vec4 color;

void main() {
	gl_FragColor = color;
}