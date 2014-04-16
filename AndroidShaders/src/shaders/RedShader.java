package shaders;

import graphics.shaders.R;

public class RedShader extends Shader{

	public RedShader()
	{
		super.vID = R.raw.red_vs;
		super.fID = R.raw.red_ps;
	}
	

}
