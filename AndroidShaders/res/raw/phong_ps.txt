// Frag shader Phong Shading - Per-pixel lighting

precision mediump float;

varying vec3 vNormal;
varying vec3 EyespaceNormal;

// light
uniform vec4 lightPos;
uniform vec4 lightColor;

// material
uniform vec4 matAmbient;
uniform vec4 matDiffuse;
uniform vec4 matSpecular;
uniform float matShininess;

// eye pos
uniform vec3 eyePos;

// from vertex s
varying vec3 lightDir, eyeVec;

void main() {
	vec3 N = normalize(EyespaceNormal);
    vec3 E = normalize(eyeVec); 
    
    vec3 L = normalize(lightDir);
    
    // Reflect the vector. Use this or reflect(incidentV, N);
    vec3 reflectV = reflect(-L, N);
    
    // Get lighting terms
    vec4 ambientTerm;
    ambientTerm = matAmbient * lightColor;
    	
    vec4 diffuseTerm = matDiffuse * max(dot(N, L), 0.0);
    vec4 specularTerm = matSpecular * pow(max(dot(reflectV, E), 0.0), matShininess);
    
    gl_FragColor =  ambientTerm + diffuseTerm + specularTerm;
	
}